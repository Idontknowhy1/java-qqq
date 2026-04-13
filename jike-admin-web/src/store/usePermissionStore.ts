// store/counterStore.js
import { message, type MenuProps } from "antd";
import { create } from "zustand";
import { sendGet } from "../api/api";

type MenuItem = Required<MenuProps>["items"][number];

interface IState {
  loading: boolean;
  loaded: boolean;
  menus: MenuItem[];
  plainMenus: IMenu[];
  permissions: string[];
}

interface IAction {
  queryPermissions: () => void;
  // queryMenus: () => void;
  clear: () => void;
  getMenuRoute: (key: string) => string;
  updatePermissions: (permissions: string[]) => void;
}

// 使用 create 函数创建 store，其返回值是一个 Hook
const usePermissionStore = create<IState & IAction>((set, get) => ({
  loading: true,
  loaded: false,
  menus: [],
  plainMenus: [],
  permissions: [],

  queryPermissions: async () => {
    try {
      set({ loading: true });
      const res = (await sendGet("/syspermission/v1/userPermissions")) as any;
      set({ loading: false });
      console.log("权限数据：", res);
      if (res.code === 10000 && res.data) {
        let _menus = res.data.menus || [];

        // 转换为MenuItem
        let _allMenus: MenuItem[] = _menus
          .filter((r: any) => r.type === "MENU")
          .map((menu: any) => ({
            key: menu.id,
            label: menu.name,
            route: menu.route,
            pid: menu.pid,
          }));

        // 还原菜单层次
        _allMenus
          .filter((m: any) => m.pid > 0)
          .forEach((sm: any) => {
            _allMenus.forEach((p: any) => {
              if (p.key === sm.pid) {
                p.children = p.children || [];
                p.children.push(sm);
              }
            });
          });

        set({
          loaded: true,
          plainMenus: _menus,
          menus: _allMenus.filter((m: any) => m.pid === 0),
          permissions: res.data.permissions || [],
        });
      } else {
        message.error("获取权限数据失败," + res.msg);
      }
    } catch (error) {
      message.error("获取权限数据失败," + error);
      console.error("获取权限数据失败：", error);
      set({ loading: false });
    }
  },

  // queryMenus: async () => {
  //   try {
  //     if (get().loaded) {
  //       return;
  //     }
  //     set({ loading: true });
  //     const res = await sendGet("/syspermission/v1/userMenus");
  //     console.log("菜单数据：", res);
  //     set({ loading: false });
  //     if (res.code === 10000 && res.data) {
  //       let _menus = res.data.menus || [];

  //       // 转换为MenuItem
  //       let _allMenus: MenuItem[] = _menus
  //         .filter((r: any) => r.type === "MENU")
  //         .map((menu: any) => ({
  //           key: menu.id,
  //           label: menu.name,
  //           route: menu.route,
  //           pid: menu.pid,
  //         }));

  //       // 还原菜单层次
  //       _allMenus
  //         .filter((m: any) => m.pid > 0)
  //         .forEach((sm) => {
  //           _allMenus.forEach((p) => {
  //             if (p.key === sm.pid) {
  //               p.children = p.children || [];
  //               p.children.push(sm);
  //             }
  //           });
  //         });

  //       set({
  //         loaded: true,
  //         plainMenus: _menus,
  //         menus: _allMenus.filter((m) => m.pid === 0),
  //       });
  //     } else {
  //       message.error("获取菜单数据失败," + res.msg);
  //     }
  //   } catch (error) {
  //     message.error("获取菜单数据失败," + error);
  //     console.error("获取菜单数据失败：", error);
  //     set({
  //       loading: false,
  //     });
  //   }
  // },

  updatePermissions: (permissions: string[]) => {
    set({
      permissions,
    });
  },

  clear: async () => {
    set({
      loaded: false,
      menus: [],
      plainMenus: [],
      permissions: [],
    });
  },

  getMenuRoute: (key: string) => {
    const menu = get().plainMenus.find((m) => m.id == key);
    return menu?.route || "";
  },
}));

export default usePermissionStore;
