import { PageContainer } from "@ant-design/pro-components";
import {
  Button,
  Image,
  message,
  Space,
  Switch,
  Table,
  type TableColumnsType,
} from "antd";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { sendGet } from "../../../api/api";
import Accessor from "../../../components/Accessor";
import RemoveButton from "../../../components/RemoveButton";

interface IProps {}

interface DataType {
  key: React.ReactNode;
  id: number;
  name: string;
  disabled: boolean;
}

const BannersPage: React.FC<IProps> = (props) => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<any[]>([]);
  const navigate = useNavigate();

  const fetchData = async () => {
    try {
      setLoading(true);
      const res = await sendGet("/banners/v1/list");
      if (res.code === 10000) {
        res.data.forEach((item) => {
          item.key = item.id;
        });
        setData(res.data);
      } else {
        message.error("获取数据失败:" + res.msg);
        console.error("获取数据失败:", res.msg);
      }
      setLoading(false);
    } catch (error) {
      setLoading(false);
      console.log(error);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const confirmDelete = async (record: DataType) => {
    try {
      const res = await sendGet("/banners/v1/delete?id=" + record.id);
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

  const columns: TableColumnsType<DataType> = [
    {
      title: "idText",
      dataIndex: "idText",
      key: "idText",
      hidden: true,
    },
    {
      title: "标题",
      dataIndex: "title",
      key: "title",
    },
    {
      title: "图片",
      dataIndex: "image",
      key: "image",
      render: (value) => {
        return <Image src={value} alt={value} style={{ width: 100 }} />;
      },
    },
    {
      title: "链接",
      dataIndex: "link",
      key: "link",
    },
    {
      title: "排序",
      dataIndex: "sort",
      key: "nsortame",
    },

    {
      title: "状态",
      dataIndex: "status",
      key: "status",
      render: (value) => {
        if (value === "1") {
          return <Switch checked={false} />;
        } else if (value === "0") {
          return <Switch checked={true} />;
        }
        return <></>;
      },
    },
    {
      title: "操作",
      render: (_, record) => {
        return (
          <Space>
            <Accessor permission="banners:save">
              <Button
                type="link"
                size="small"
                onClick={() => navigate(`/banners/edit`, { state: record })}
              >
                编辑
              </Button>
            </Accessor>
            <Accessor permission="banners:delete">
              <RemoveButton onDelete={() => confirmDelete(record)} />
            </Accessor>
          </Space>
        );
      },
    },
  ];

  return (
    <>
      <PageContainer
        style={{ width: "100%" }}
        loading={loading}
        header={{
          extra: [
            <Accessor key="add" permission="banners:save">
              <Button
                type="primary"
                onClick={() => navigate(`/banners/edit`, { state: null })}
              >
                创建
              </Button>
            </Accessor>,
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
    </>
  );
};

export default BannersPage;
