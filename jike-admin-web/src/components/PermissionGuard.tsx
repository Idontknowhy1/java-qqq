// src/components/PermissionGuard.tsx
import { Result } from "antd";
import React, { isValidElement, Suspense } from "react";
import usePermissionStore from "../store/usePermissionStore";

interface ProtectedRouteProps {
  path: string;
  permission?: string; // 必须拥有的权限
  children: any; // 被保护的组件
}

const PermissionGuard = (props: ProtectedRouteProps) => {
  const permissions = usePermissionStore((state) => state.permissions);
  const returnValue = () => {
    return (
      <>
        <Suspense fallback={<div>Loading...</div>}>
          {isValidElement(props.children) && <div>{props.children}</div>}
          {isValidElement(props.children) == false && (
            <div>{React.createElement(props.children)}</div>
          )}
        </Suspense>
      </>
    );
  };

  if (props.permission == undefined || props.permission.length == 0) {
    return returnValue();
  }

  // console.log("权限：", permissions);
  const hasPermission = permissions.includes(props.permission);

  if (!hasPermission) {
    return (
      <Result
        status="403"
        title="权限不足"
        subTitle={`您无权限访问该页面, 缺少权限：["${props.permission}"]`}
      />
    );
  }

  return returnValue();
};

export default PermissionGuard;
