// store/counterStore.js
import { create } from "zustand";
import { sendGet } from "../api/api";
import usePermissionStore from "./usePermissionStore";

interface IState {
  logined: boolean;
  token: string;

  avatar: string;
  id: string;
  account: string;
  name: string;
}

interface IAction {
  updateToken: (token: string) => void;
  getUserInfo: () => void;
  logout: () => Promise<boolean>;
}

// 使用 create 函数创建 store，其返回值是一个 Hook
const useUserStore = create<IState & IAction>((set, get) => ({
  logined: false,
  token: "",
  avatar: "",
  id: "",
  account: "",
  name: "",

  updateToken: async (token: string) => {
    set({ token });
    localStorage.setItem("x-token", token);
    get().getUserInfo();
  },

  getUserInfo: async () => {
    try {
      const res = await sendGet("/user/v1/info");
      console.log("用户信息：", res);
      if (res.code === 10000 && res.data) {
        set({
          logined: true,
          avatar: res.data.avatar || "",
          id: res.data.id || "",
          account: res.data.account || "",
          name: res.data.name || "",
        });
      }
    } catch (error) {
      console.error("获取用户信息失败：", error);
    }
  },

  logout: async () => {
    set({
      logined: false,
    });
    await sendGet("/sysaccount/v1/logout");
    usePermissionStore.getState().clear();
    localStorage.removeItem("x-token");
    localStorage.removeItem("x-token-2");
    return true;
  },
}));

export default useUserStore;
