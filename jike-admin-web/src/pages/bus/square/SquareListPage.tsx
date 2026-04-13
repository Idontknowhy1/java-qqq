import {
  PageContainer,
  ProTable,
  type ActionType,
  type ProColumns,
} from "@ant-design/pro-components";
import { Image, message, Space } from "antd";
import { useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { sendGet } from "../../../api/api";
import Accessor from "../../../components/Accessor";
import RemoveButton from "../../../components/RemoveButton";

interface IProps {}

interface DataType {
  id: string;
  title: string;
  imageUrl: string;
  model: string;
  name: string;
  prompt: string;
  referImages: string;
  size: string;
  sortIndex: number;
}

const MaterialListPage: React.FC<IProps> = (props) => {
  const navigate = useNavigate();
  const actionRef = useRef<ActionType>(null);

  const [pageInfo, setPageInfo] = useState({
    current: 1,
    pageSize: 10,
  });

  const fetchData = async (params) => {
    try {
      const res = await sendGet("/materialsquare/v1/page-list", {
        pageNum: params.current,
        pageSize: params.pageSize,
      });
      if (res.code === 10000) {
        return {
          data: res.data.list,
          success: true,
          total: res.data.totalCount,
        };
      } else {
        message.error("获取数据失败:" + res.msg);
        console.error("获取数据失败:", res.msg);
      }
    } catch (error) {
      console.log(error);
    }
  };

  const confirmDelete = async (record: DataType) => {
    try {
      const res = await sendGet("/materialsquare/v1/delete?id=" + record.id);
      if (res.code === 10000) {
        actionRef.current?.reload();
      } else {
        message.error("删除失败:" + res.msg);
        console.error("删除失败:", res.msg);
      }
    } catch (error) {
      console.error(error);
      message.error("删除失败");
    }
  };

  const columns: ProColumns<DataType> = [
    {
      title: "id",
      dataIndex: "id",
      key: "id",
      hidden: true,
      hideInSetting: true,
      hideInSearch: true,
    },
    {
      title: "图片",
      dataIndex: "imageUrl",
      key: "imageUrl",
      hideInSearch: true,
      render: (value) => {
        return <Image src={value} alt={value} style={{ width: 100 }} />;
      },
    },
    {
      title: "提示词",
      dataIndex: "prompt",
      key: "prompt",
      hideInSearch: true,
    },
    {
      title: "操作",
      hideInSearch: true,
      render: (_, record) => {
        return (
          <Space>
            <Accessor key="add" permission="squares:delete">
              <RemoveButton onDelete={() => confirmDelete(record)} />
            </Accessor>
          </Space>
        );
      },
    },
  ];

  return (
    <>
      <PageContainer style={{ width: "100%" }}>
        <ProTable<DataType>
          columns={columns}
          actionRef={actionRef}
          cardBordered
          request={async (params, sort, filter) => {
            console.log(params, sort, filter);
            return fetchData({
              ...params,
              ...filter,
            });
          }}
          rowKey="id"
          search={false}
          pagination={{
            pageSize: pageInfo.pageSize,
            onShowSizeChange: (current, size) => {
              setPageInfo({
                current,
                pageSize: size,
              });
            },
          }}
        />
      </PageContainer>
    </>
  );
};

export default MaterialListPage;
