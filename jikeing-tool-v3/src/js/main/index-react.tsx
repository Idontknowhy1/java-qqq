import React from "react";
import ReactDOM from "react-dom/client";
import { initBolt } from "../lib/utils/bolt";
import "../assets/style/index.scss";
import '../assets/style/tailwind.css'
import { App } from "./main";
import { ConfigProvider, theme } from "antd";

initBolt();

ReactDOM.createRoot(document.getElementById("app") as HTMLElement).render(
  <ConfigProvider theme={{ algorithm: theme.darkAlgorithm }}>
    <App />
  </ConfigProvider>
);
