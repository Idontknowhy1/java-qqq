import { lazy } from "react";
import { Navigate } from "react-router-dom";
import MainLayout from "../layout/MainLayout";
import { NotFoundPage } from "../pages/404";

const routerConfig: IRouterItem[] = [
  {
    path: "/",
    element: <MainLayout />,
    children: [
      {
        index: true,
        // path: "home",
        element: lazy(() => import("../pages/home/HomePage")),
      },
      {
        path: "sys-permissions",
        element: lazy(() => import("../pages/sys/permissions/PermissionsPage")),
        permission: "permissions",
      },
      {
        path: "sys-roles",
        element: lazy(() => import("../pages/sys/roles/RolesPage")),
        permission: "roles",
      },
      {
        path: "banners",
        element: lazy(() => import("../pages/bus/banners/BannersPage")),
        permission: "banners",
      },
      {
        path: "banners/edit",
        element: lazy(() => import("../pages/bus/banners/BannerEditPage")),
        permission: "banners:save",
      },
      {
        path: "materials",
        element: lazy(() => import("../pages/bus/materials/MaterialListPage")),
        permission: "materials",
      },
      {
        path: "materials/edit",
        element: lazy(() => import("../pages/bus/materials/MaterialEdit")),
        permission: "materials:save",
      },
      {
        path: "squares",
        element: lazy(() => import("../pages/bus/square/SquareListPage")),
        permission: "squares",
      },
      {
        path: "image-split",
        element: lazy(() => import("../pages/bus/split/ImageSplitListPage")),
        permission: "image-split",
      },
      {
        path: "ai-images",
        element: lazy(() => import("../pages/bus/ai-images/AiImageListPage")),
        permission: "ai-images",
      },
      {
        path: "user-order-check",
        element: lazy(() => import("../pages/order/UserOrderList")),
      },
      {
        path: "orders",
        element: lazy(() => import("../pages/order/OrderList")),
        permission: "orders",
      },
      {
        path: "user-score",
        element: lazy(() => import("../pages/user/UserScore")),
        permission: "user-score:update",
      },
      {
        path: "user-vip",
        element: lazy(() => import("../pages/user/UserVip")),
        permission: "user-vip:update",
      },
      {
        path: "base-users",
        element: lazy(() => import("../pages/base/users/UsersPage")),
        permission: "users",
      },
      {
        path: "users-profile",
        element: lazy(() => import("../pages/sys/profile/ProfilePage")),
      },
      {
        path: "sys-config",
        element: lazy(() => import("../pages/sys/config/SysConfig")),
        permission: "sys-config",
      },
      {
        path: "*",
        element: <NotFoundPage />,
      },
    ],
  },
  {
    path: "/login",
    element: lazy(() => import("../pages/login/LoginPage")),
  },
  {
    path: "*",
    element: <Navigate to="/" replace={true} />,
  },
];

export default routerConfig;
