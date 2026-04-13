import { useState } from "react";
import { MainSegType } from "../../../global";
import useUserStore from "../../../store/useUserStore";
import { Dropdown, Image, MenuProps, message } from "antd";

interface IProps {
  selectedType: MainSegType;
  updateSelectedType: (type: MainSegType) => void;
}

const HeaderBar: React.FC<IProps> = (props) => {
  const tabs: { title: string; tag: MainSegType }[] = [
    { title: "脚本", tag: "SCRIPT" },
    { title: "本地资源库", tag: "LOCAL" },
    { title: "云端资源库", tag: "CLOUD" },
  ];

  const uuid = useUserStore((state) => state.uuid);
  const avatar = useUserStore((state) => state.avatar);

  const items: MenuProps["items"] = [
    {
      key: "uid",
      label: "uid: " + uuid,
    },
    {
      key: "logout",
      label: "退出登录",
    },
  ];

  const onClick: MenuProps["onClick"] = async ({ key }) => {
    if (key == "uid") {
      await navigator.clipboard.writeText(uuid);
      message.success("UID 已复制");
    } else if (key == "logout") {
      useUserStore.getState().logout();
    }
  };

  return (
    <>
      <div className="h-[30px] text-[#B6B8B5] text-[12px] bg-[#333333] flex justify-between items-center">
        {/* 左侧菜单 */}
        <div className="flex h-full">
          {tabs.map((t) => (
            <div
              key={t.tag}
              className={`px-[25px] flex h-full items-center cursor-pointer ${props.selectedType == t.tag ? "bg-[#1F1F1F]" : ""}`}
              onClick={() => props.updateSelectedType(t.tag)}
            >
              {" "}
              {t.title}{" "}
            </div>
          ))}
        </div>

        {/* 右侧用户信息 */}
        <div className="pr-[16px]">
          <Dropdown menu={{ items, onClick }} placement="bottom">
            <Image
              className="rounded-full"
              src={avatar}
              preview={false}
              width={28}
            />
          </Dropdown>
        </div>
      </div>
      {/* <div class="right-user-info"> */}

      {/* <!-- 用户头像、用户ID --> */}
      {/* <el-dropdown v-if="userStore.isLogin == true" @command="userInfoItemClick">
        <span class="el-dropdown-link">
          <el-image :src="userStore.avatar" style="width: 30px; height: 30px; border-radius: 15px; cursor: pointer;"
            fit="contain" />
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="uuid">UID: {{ userStore.uuid }}</el-dropdown-item>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown> */}

      {/* // </div >
//   </div > */}
    </>
  );
};

export default HeaderBar;
