import type { ProSettings } from "@ant-design/pro-components";
import { ProLayout } from "@ant-design/pro-components";
import { Dropdown, message, Spin, type MenuProps } from "antd";
import { useState } from "react";
import { Outlet, useNavigate } from "react-router-dom";
import { sendGet } from "../api/api";
import usePermissionStore from "../store/usePermissionStore";
import useUserStore from "../store/useUserStore";
import { aes_decrypt } from "../utils/crypto-utils";

export default () => {
  const permissionsStore = usePermissionStore();
  const navigate = useNavigate();
  const userStore = useUserStore();

  const [loading, setLoading] = useState(false);

  const [settings] = useState<Partial<ProSettings> | undefined>({
    fixSiderbar: true,
    layout: "mix",
    // splitMenus: true,
  });

  const queryPermissions = async () => {
    try {
      setLoading(true);
      const res = (await sendGet("/syspermission/v1/userPermissions")) as any;
      setLoading(false);
      console.log("权限数据：", res);
      if (res.code === 10000 && res.data) {
        const data = JSON.parse(aes_decrypt(res.data));

        permissionsStore.updatePermissions(data.permissions || []);

        let _menus = data.menus || [];

        // 转换为MenuItem
        let _allMenus = _menus
          .filter((r: any) => r.type === "MENU")
          .map((menu: any) => ({
            id: menu.id,
            name: menu.name,
            path: menu.route,
            pid: menu.pid,
            icon: menu.icon,
            hideInMenu: false,
          }));

        // 还原菜单层次
        _allMenus
          .filter((m: any) => m.pid > 0)
          .forEach((sm: any) => {
            _allMenus.forEach((p: any) => {
              if (p.id === sm.pid) {
                p.children = p.children || [];
                p.children.push(sm);
              }
            });
          });
        return _allMenus.filter((m: any) => m.pid === 0);
      } else {
        message.error("获取权限数据失败," + res.msg);
      }
    } catch (error) {
      setLoading(false);
      message.error("获取权限数据失败," + error);
      console.error("获取权限数据失败：", error);
    }
  };

  const onAvatarClick: MenuProps["onClick"] = async ({ key }) => {
    if (key === "profile") {
      navigate("/users-profile");
    } else if (key === "logout") {
      userStore.logout();
      navigate("/login");
    }
  };

  return (
    <>
      <ProLayout
        prefixCls="my-prefix"
        siderMenuType="sub"
        menu={{
          collapsedShowGroupTitle: true,
          request: async () => {
            userStore.getUserInfo();
            let res = await queryPermissions();
            return res;
          },
        }}
        avatarProps={{
          src: "https://gw.alipayobjects.com/zos/antfincdn/efFD%24IOql2/weixintupian_20170331104822.jpg",
          size: "small",
          title: <div>{userStore.name}</div>,

          render: (props, dom) => {
            return (
              <Dropdown
                menu={{
                  items: [
                    {
                      key: "profile",
                      label: "个人中心",
                    },
                    {
                      key: "logout",
                      label: "退出登录",
                    },
                  ],
                  onClick: onAvatarClick,
                }}
              >
                {dom}
              </Dropdown>
            );
          },
        }}
        headerTitleRender={(logo, title, _) => {
          return (
            <a>
              {logo}
              {title}
            </a>
          );
        }}
        menuFooterRender={(props) => {
          if (props?.collapsed) return undefined;
          return (
            <div
              style={{
                textAlign: "center",
                paddingBlockStart: 12,
              }}
            >
              <div>© 2021 Made with love</div>
              <div>by Ant Design</div>
            </div>
          );
        }}
        onMenuHeaderClick={(e) => console.log(e)}
        menuItemRender={(item, dom) => (
          <div
            onClick={() => {
              console.log("----单机菜单", item);
              // setPathname(item.path || "/welcome");
              navigate(item.path || "/welcome");
            }}
          >
            {dom}
          </div>
        )}
        {...settings}
      >
        <div className="h-full p-[32px]">
          <div className="bg-[white]">
            {loading ? <Spin size="large" /> : <Outlet />}
          </div>
        </div>
      </ProLayout>
    </>
  );
};
