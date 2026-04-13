import { createBrowserRouter, type RouteObject } from "react-router-dom";
import PermissionGuard from "../components/PermissionGuard";
import routerConfig from "./config";

const generateRouters = (config: IRouterItem[]): RouteObject[] => {
  return config.map((route) => {
    return {
      ...route,
      element: (
        <PermissionGuard
          path={route.path ?? "--"}
          permission={route.permission}
        >
          {route.element}
        </PermissionGuard>
      ),
      children: route.children ? generateRouters(route.children) : null,
    };
  }) as RouteObject[];
};

const router = createBrowserRouter(generateRouters(routerConfig));
export default router;
