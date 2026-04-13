import usePermissionStore from "../store/usePermissionStore";

interface IProps {
  permission?: string; // 必须拥有的权限
  children: any; // 被保护的组件
}

const Accessor: React.FC<IProps> = (props) => {
  const userPermissions = usePermissionStore((state) => state.permissions);
  const hasPermission =
    props.permission && props.permission.length > 0
      ? userPermissions.includes(props.permission)
      : true;

  return hasPermission ? props.children : null;
};

export default Accessor;
