import { Button, Popconfirm } from "antd";
interface IProps {
  onDelete: () => void;
}

const RemoveButton: React.FC<IProps> = (props) => {
  return (
    <>
      <Popconfirm
        title="确认删除吗？"
        onConfirm={props.onDelete}
        okText="是"
        cancelText="否"
      >
        <Button size="small" type="link" danger>
          删除
        </Button>
      </Popconfirm>
    </>
  );
};

export default RemoveButton;
