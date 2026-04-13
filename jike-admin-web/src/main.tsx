import "amfe-flexible";
import ReactDOM from "react-dom/client";
import { RouterProvider } from "react-router-dom";
import "./assets/style/base.scss";

import "./index.css";
import router from "./routers/router";

import dayjs from "dayjs";
import "dayjs/locale/zh-cn";
// import { ParallaxProvider } from "react-scroll-parallax";

dayjs.locale("zh-cn");

ReactDOM.createRoot(document.getElementById("root")!).render(
  <RouterProvider router={router} />,
);
