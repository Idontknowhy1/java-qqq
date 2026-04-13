import usePermissionStore from "../store/usePermissionStore";

// src/hooks/usePermission.ts
export const usePermission = (permission: string) => {
  return usePermissionStore.getState().permissions.includes(permission);
};
