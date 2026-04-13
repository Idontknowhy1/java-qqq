import { PageContainer } from "@ant-design/pro-components";
import {
  Button,
  Form,
  Input,
  message,
  Modal,
  Radio,
  Space,
  Switch,
  Table,
  Tag,
  type TableColumnsType,
} from "antd";
import { useEffect, useState } from "react";
import { sendGet, sendPost } from "../../../api/api";
import RemoveButton from "../../../components/RemoveButton";

interface IProps {}

interface DataType {
  key: React.ReactNode;
  id: number;
  type: string;
  name: string;
  pid: number;
  route: string;
  code: string;
  orderIndex: number;
  children?: DataType[];
}

const PermissionsPage: React.FC<IProps> = (props) => {
  const [data, setData] = useState<DataType[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [formLoading, setFormLoading] = useState<boolean>(false);
  const [isModalOpen, setShowModal] = useState<boolean>(false);
  const [isAdd, setIsAdd] = useState<boolean>(true);

  const [form] = Form.useForm();

  const fetchData = async () => {
    try {
      setLoading(true);
      const res = await sendGet("/syspermission/v1/list");
      if (res.code === 10000) {
        let _menus = res.data || [];

        _menus.forEach((element) => {
          element.key = element.id;
        });

        let folders = _menus.filter(
          (item) => item.type === "MENU" && item.pid === 0,
        );
        // 遍历子项，将其添加到父项的 children 数组中
        _menus
          .filter((item) => item.pid > 0)
          .forEach((item) => {
            _menus.forEach((element) => {
              if (element.id === item.pid) {
                element.children = element.children || [];
                element.children.push(item);
              }
            });
          });

        setData(folders);
      } else {
        message.error("获取功能点列表失败:" + res.msg);
        console.error("获取功能点列表失败:", res.msg);
      }
      setLoading(false);
    } catch (error) {
      setLoading(false);
      console.log(error);
    }
  };
  const saveData = async (values: any) => {
    try {
      setFormLoading(true);
      const res = await sendPost("/syspermission/v1/save", {
        ...values,
        status: values.status ? "0" : "1",
      });
      if (res.code === 10000) {
        message.success("保存成功");
        fetchData();
        setShowModal(false);
      } else {
        message.error("保存失败:" + res.msg);
        console.error("保存失败:", res.msg);
      }
      setFormLoading(false);
    } catch (error) {
      setFormLoading(false);
      console.log(error);
    }
  };
  const confirmDelete: PopconfirmProps["onConfirm"] = async (
    record: DataType,
  ) => {
    try {
      const res = await sendGet("/syspermission/v1/delete?id=" + record.id);
      if (res.code === 10000) {
        fetchData();
      } else {
        message.error("删除失败:" + res.msg);
        console.error("删除失败:", res.msg);
      }
    } catch (error) {
      console.error(error);
      message.error("删除失败");
    }
  };

  const showAddForm = (type: "MENU" | "FUNC", pid: number) => {
    setIsAdd(true);
    form.setFieldsValue({
      id: "0",
      name: "",
      type: type,
      code: "",
      route: "",
      pid: pid,
      orderIndex: 0,
      enable: true,
    });
    setShowModal(true);
  };
  const showEditForm = (data: DataType) => {
    setIsAdd(false);
    form.setFieldsValue(data);
    setShowModal(true);
  };

  const columns: TableColumnsType<DataType> = [
    {
      title: "名称",
      dataIndex: "name",
      key: "name",
    },
    {
      title: "类型",
      dataIndex: "type",
      key: "type",
      render: (value) => {
        if (value === "MENU") {
          return <Tag color="#2db7f5">{value}</Tag>;
        }
        return <Tag color="orange">{value}</Tag>;
      },
    },
    {
      title: "code",
      dataIndex: "code",
      key: "code",
    },
    {
      title: "route",
      dataIndex: "route",
      key: "route",
    },
    {
      title: "排序",
      dataIndex: "orderIndex",
      key: "orderIndex",
      width: 60,
    },
    {
      title: "状态",
      dataIndex: "enable",
      key: "enable",
      render: (value) => {
        return <Switch checked={value} />;
      },
    },
    {
      title: "操作",
      render: (_, record) => {
        return (
          <Space>
            {record.pid === 0 && (
              <Button
                size="small"
                onClick={() => showAddForm("MENU", record.id)}
              >
                Add Sub Menu
              </Button>
            )}
            {record.pid > 0 && record.type == "MENU" && (
              <Button
                size="small"
                onClick={() => showAddForm("FUNC", record.id)}
              >
                Add Func
              </Button>
            )}
            <Button size="small" onClick={() => showEditForm(record)}>
              Edit
            </Button>
            {(record.children == null || record.children?.length === 0) && (
              <RemoveButton onDelete={() => confirmDelete(record)} />
            )}
          </Space>
        );
      },
    },
  ];

  useEffect(() => {
    fetchData();
  }, []);

  return (
    <>
      <PageContainer
        // style={{ width: "100%" }}
        loading={loading}
        header={{
          ghost: true,
          extra: [
            <Button
              key="add"
              type="primary"
              onClick={() => showAddForm("MENU", 0)}
            >
              创建
            </Button>,
          ],
        }}
      >
        <Table<DataType>
          style={{ height: "calc(100%-72px)", overflowY: "auto" }}
          columns={columns}
          dataSource={data}
          pagination={false}
          size="small"
          sticky={true}
        />
      </PageContainer>

      {/* 创建、修改 */}
      <Modal
        title={isAdd ? "创建" : "编辑"}
        closable={false}
        open={isModalOpen}
        confirmLoading={formLoading}
        onOk={() => {
          form.validateFields().then((values) => {
            saveData(values);
          });
        }}
        onCancel={() => setShowModal(false)}
      >
        <Form form={form} labelCol={{ span: 4 }}>
          <Form.Item label="id" name="id" hidden={true}>
            <Input />
          </Form.Item>
          <Form.Item label="pid" name="pid" hidden={true}>
            <Input />
          </Form.Item>
          <Form.Item
            label="名称"
            name="name"
            rules={[{ required: true, message: "请输入名称" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item label="类型" name="type" hidden={true}>
            <Radio.Group
              options={[
                { value: "MENU", label: "MENU" },
                { value: "FUNC", label: "FUNC" },
              ]}
            />
          </Form.Item>
          <Form.Item
            label="code"
            name="code"
            rules={[{ required: true, message: "请输入code" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item label="route" name="route">
            <Input />
          </Form.Item>
          <Form.Item label="排序" name="orderIndex">
            <Input />
          </Form.Item>
          <Form.Item label="启用" name="enable">
            <Switch />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
};

export default PermissionsPage;
