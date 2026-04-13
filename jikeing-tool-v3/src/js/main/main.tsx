import { useEffect, useState } from "react";
import { os, path } from "../lib/cep/node";
import LoginPage from "../pages/LoginPage/LoginPage";
import UnActivity from "../pages/LoginPage/UnActivity";

import useUserStore from "../store/useUserStore";
import HomePage from "../pages/HomePage/HomePage";

export const App = () => {

  const isLogin = useUserStore(state => state.isLogin)
  const is_plugin_member = useUserStore(state => state.is_plugin_member)

  return (
    <div className="app h-full">
      {isLogin == false && <LoginPage />}
      {isLogin && is_plugin_member == true && <HomePage />}
      {isLogin && is_plugin_member == false && <UnActivity />}
    </div>
  );
};
