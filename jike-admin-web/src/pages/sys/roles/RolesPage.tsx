interface IProps {}

import {
  PageContainer,
  ProForm,
  ProFormSwitch,
  ProFormText,
} from "@ant-design/pro-components";
import {
  Button,
  Form,
  message,
  Modal,
  Space,
  Table,
  Tag,
  Tree,
  type TableColumnsType,
  type TreeDataNode,
  type TreeProps,
} from "antd";
import { useEffect, useState } from "react";
import { sendGet, sendPost } from "../../../api/api";

interface IProps {}

interface DataType {
  key: React.ReactNode;
  id: number;
  name: string;
  disabled: boolean;
}

type FieldType = {
  id?: number;
  name?: string;
  disabled?: boolean;
};

const RolesPage: React.FC<IProps> = (props) => {
  const [data, setData] = useState<DataType[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [formLoading, setFormLoading] = useState<boolean>(false);
  const [plainPermissions, setPlainPermissions] = useState<IMenu[]>([]);
  const [allPermissions, setAllPermissions] = useState<TreeDataNode[]>([]);
  // 所有菜单Key
  const [allPermissionKeys, setAllPermissionKeys] = useState<React.Key[]>([]);
  const [checkedPermissionKeys, setCheckedPermissionKeys] = useState<{
    checked: React.Key[];
    halfChecked: React.Key[];
  }>({ checked: [], halfChecked: [] });
  const onPermissionCheck: TreeProps["onCheck"] = (checkedKeysValue) => {
    setCheckedPermissionKeys(
      checkedKeysValue as { checked: React.Key[]; halfChecked: React.Key[] },
    );
  };

  const [form] = Form.useForm();

  const [currentRoleId, setCurrentRoleId] = useState<number>(0);

  const [isModalOpen, setShowModal] = useState<boolean>(false);
  const [isAdd, setIsAdd] = useState<boolean>(true);

  const [isPermissionModalOpen, setPermissionModalOpen] =
    useState<boolean>(false);

  const fetchData = async () => {
    try {
      setLoading(true);
      const res = await sendGet("/sysrole/v1/list");
      if (res.code === 10000) {
        res.data.forEach((item) => {
          item.key = item.id;
        });
        setData(res.data);
      } else {
        message.error("获取数据列表失败:" + res.msg);
        console.error("获取数据列表失败:", res.msg);
      }
      setLoading(false);
    } catch (error) {
      setLoading(false);
      console.log(error);
    }
  };
  const saveData = async (values: FieldType) => {
    try {
      setFormLoading(true);
      const res = await sendPost("/sysrole/v1/save", values);
      if (res.code === 10000) {
        setShowModal(false);
        fetchData();
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
  const fetchRolePermissions = async (roleId: number) => {
    try {
      setLoading(true);
      const res = await sendGet("/syspermission/v1/rolePermissions", {
        roleId,
      });
      if (res.code === 10000) {
        setCheckedPermissionKeys({
          checked: res.data || [],
          halfChecked: [],
        });
      } else {
        message.error("获取角色权限失败:" + res.msg);
        console.error("获取角色权限失败:", res.msg);
      }
      setLoading(false);
    } catch (error) {
      setLoading(false);
      console.log(error);
    }
  };
  const fetchAllPermissions = async () => {
    try {
      setLoading(true);
      const res = await sendGet("/syspermission/v1/list");
      if (res.code === 10000) {
        let _menus = res.data || [];

        const _allKeys = _menus.map((item) => item.id);
        _menus.forEach((element) => {
          element.key = element.id;
          element.title = element.name;
        });

        let folders = _menus.filter((item) => item.pid === 0);
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

        setPlainPermissions(_menus);
        setAllPermissions(folders);
        setAllPermissionKeys(_allKeys);
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
  const saveRolePermissions = async (values: React.Key[]) => {
    try {
      setLoading(true);
      const res = await sendPost("/syspermission/v1/updateRolePermission", {
        roleId: currentRoleId,
        permissionIds: values,
      });
      if (res.code === 10000) {
        setPermissionModalOpen(false);
      } else {
        message.error("保存失败:" + res.msg);
        console.error("保存失败:", res.msg);
      }
      setLoading(false);
    } catch (error) {
      setLoading(false);
      console.log(error);
    }
  };

  const showAddForm = () => {
    setIsAdd(true);
    form.setFieldsValue({
      id: 0,
      name: "",
      disabled: false,
    });
    setShowModal(true);
  };
  const showEditForm = (item: DataType) => {
    setIsAdd(false);
    form.setFieldsValue({
      id: item.id,
      name: item.name,
      disabled: item.disabled,
    });
    setShowModal(true);
  };
  const showPermissionForm = (item: DataType) => {
    setCurrentRoleId(item.id);
    fetchRolePermissions(item.id);
    setPermissionModalOpen(true);
  };

  const getNodeIds = (pid: React.Key) => {
    let _pers = [];
    // 递归调用，获取子项的 id
    plainPermissions
      .filter((item) => item.pid === pid)
      .forEach((item) => {
        _pers.push(...getNodeIds(item.id));
      });
    return [pid, ..._pers];
  };

  // 对某个节点全选
  const selectNodeAllChildren = (nodeId: React.Key) => {
    let rolePermissions = checkedPermissionKeys.checked;
    let willAddChildren = getNodeIds(nodeId);
    // 去重，防止加2次
    rolePermissions = rolePermissions.filter(
      (item) => !willAddChildren.includes(item),
    );
    setCheckedPermissionKeys({
      checked: [...rolePermissions, ...willAddChildren],
      halfChecked: [],
    });
  };
  // 对某个节点取消全选
  const deSelectNodeAllChildren = (nodeId: React.Key) => {
    let rolePermissions = checkedPermissionKeys.checked;
    let willAddChildren = getNodeIds(nodeId);
    rolePermissions = rolePermissions.filter(
      (item) => !willAddChildren.includes(item),
    );
    setCheckedPermissionKeys({
      checked: [...rolePermissions],
      halfChecked: [],
    });
  };

  const columns: TableColumnsType<DataType> = [
    {
      title: "id",
      dataIndex: "id",
      key: "id",
    },
    {
      title: "名称",
      dataIndex: "name",
      key: "name",
    },
    {
      title: "禁用",
      dataIndex: "disabled",
      key: "disabled",
      render: (value) => {
        if (value === true) {
          return <Tag color="#FF0000">已禁用</Tag>;
        }
        return <></>;
      },
    },
    {
      title: "操作",
      render: (_, record) => {
        return (
          <Space>
            <Button size="small" onClick={() => showEditForm(record)}>
              Edit
            </Button>
            <Button
              size="small"
              type="primary"
              danger
              onClick={() => showPermissionForm(record)}
            >
              Permissions
            </Button>
          </Space>
        );
      },
    },
  ];

  useEffect(() => {
    fetchData();
    fetchAllPermissions();
  }, []);

  const waitTime = (time: number = 100) => {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve(true);
      }, time);
    });
  };

  return (
    <>
      <div className="flex h-full text-[18px]">
        <PageContainer
          style={{ width: "100%" }}
          loading={loading}
          header={{
            extra: [
              <Button key="add" type="primary" onClick={showAddForm}>
                创建新角色
              </Button>,
              <Button key="refresh" onClick={fetchData}>
                刷新
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
      </div>

      {/* 创建角色、修改角色 */}
      <Modal
        title={isAdd ? "创建新角色" : "编辑角色"}
        closable={false}
        open={isModalOpen}
        confirmLoading={formLoading}
        onCancel={() => setShowModal(false)}
        onOk={() => {
          form.validateFields().then((values) => {
            saveData(values);
          });
        }}
      >
        <ProForm
          form={form}
          labelCol={{ span: 4 }}
          layout="horizontal"
          submitter={false}
          loading={loading}
        >
          <ProFormText label="id" name="id" hidden={true} />
          <ProFormText
            label="角色名称"
            name="name"
            rules={[{ required: true, message: "请输入角色名称" }]}
          />
          <ProFormSwitch label="禁用" name="disabled" />
        </ProForm>
      </Modal>

      {/* 角色权限 */}
      <Modal
        title="角色权限"
        width={"calc(100vh)"}
        closable={false}
        open={isPermissionModalOpen}
        footer={[
          <div key="footer" className="flex justify-between">
            <Space>
              <Button
                onClick={() => {
                  console.log(allPermissionKeys);
                  setCheckedPermissionKeys({
                    checked: allPermissionKeys,
                    halfChecked: [],
                  });
                }}
              >
                全选
              </Button>
              <Button
                onClick={() =>
                  setCheckedPermissionKeys({ checked: [], halfChecked: [] })
                }
              >
                取消全选
              </Button>
            </Space>
            <Space>
              <div>{`已选择 ${checkedPermissionKeys.checked.length + "/" + allPermissionKeys.length} 个权限`}</div>
              <Button onClick={() => setPermissionModalOpen(false)}>
                取消
              </Button>
              <Button
                key="submit"
                type="primary"
                loading={loading}
                onClick={() => {
                  saveRolePermissions(checkedPermissionKeys.checked);
                }}
              >
                提交
              </Button>
            </Space>
          </div>,
        ]}
      >
        <Tree
          style={{
            height: "calc(100vh * 0.6)",
            overflowY: "auto",
          }}
          checkable
          showLine
          defaultExpandAll={true}
          checkStrictly={true}
          onCheck={onPermissionCheck}
          checkedKeys={checkedPermissionKeys}
          treeData={allPermissions}
          titleRender={(node) => {
            return (
              <Space>
                <span>
                  {node.title as string} - {node.key}
                </span>
                {node.type === "MENU" && (
                  <>
                    <Tag onClick={() => selectNodeAllChildren(node.key)}>
                      全选
                    </Tag>
                    <Tag onClick={() => deSelectNodeAllChildren(node.key)}>
                      取消全选
                    </Tag>
                  </>
                )}
              </Space>
            );
          }}
        />
      </Modal>
    </>
  );
};

export default RolesPage;
