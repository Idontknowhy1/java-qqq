import {
  PageContainer,
  ProTable,
  type ActionType,
  type ProColumns,
} from "@ant-design/pro-components";
import { Button, Image, message, Space, Switch } from "antd";
import { useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { sendGet } from "../../../api/api";
import Accessor from "../../../components/Accessor";
import MaterialCategories from "../../../components/MaterialCategories";
import RemoveButton from "../../../components/RemoveButton";

interface IProps {}

interface DataType {
  key: React.ReactNode;
  id: string;
  title: string;
  coverImage: string;
  categoryId: number;
  categoryName: string;
  status: string;
  sort: number;
  createTime: string;
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
      const res = await sendGet("/materials/v1/page-list", {
        pageNum: params.current,
        pageSize: params.pageSize,
        categoryId: params.categoryId,
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
      const res = await sendGet("/materials/v1/delete?id=" + record.id);
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
      title: "标题",
      dataIndex: "title",
      key: "title",
      hideInSearch: true,
    },
    {
      title: "图片",
      dataIndex: "coverImage",
      key: "coverImage",
      hideInSearch: true,
      render: (value) => {
        return <Image src={value} alt={value} style={{ width: 100 }} />;
      },
    },
    {
      title: "分类",
      dataIndex: "categoryId",
      hideInTable: true,
      hideInSetting: true,
      renderFormItem(_, { type }, form) {
        if (type === "form") {
          return null; // 确保只在搜索栏中渲染
        }
        // 获取当前表单中 projectId 的值
        const fieldValue = form.getFieldValue("categoryId");
        return (
          <MaterialCategories
            value={fieldValue}
            onChange={(newValue) => {
              form.setFieldsValue({ categoryId: newValue });
              form.submit();
            }}
          />
        );
      },
    },
    {
      title: "分类",
      dataIndex: "categoryName",
      hideInSearch: true,
    },
    {
      title: "排序",
      dataIndex: "sort",
      key: "nsortame",
      hideInSearch: true,
    },
    {
      title: "创建时间",
      dataIndex: "createTime",
      key: "createTime",
      hideInSearch: true,
    },
    {
      title: "状态",
      dataIndex: "status",
      key: "status",
      hideInSearch: true,
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
      hideInSearch: true,
      render: (_, record) => {
        return (
          <Space>
            <Accessor permission="materials:save">
              <Button
                type="link"
                size="small"
                onClick={() => navigate(`/materials/edit`, { state: record })}
              >
                编辑
              </Button>
            </Accessor>
            <Accessor permission="materials:delete">
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
          search={{
            labelWidth: "auto",
          }}
          //   form={{
          //     // Since transform is configured, the submitted parameters are different from the defined ones, so they need to be transformed here
          //     syncToUrl: (values, type) => {
          //       if (type === "get") {
          //         return {
          //           ...values,
          //           created_at: [values.startTime, values.endTime],
          //         };
          //       }
          //       return values;
          //     },
          //   }}
          pagination={{
            pageSize: pageInfo.pageSize,
            onShowSizeChange: (current, size) => {
              setPageInfo({
                current,
                pageSize: size,
              });
            },
          }}
          toolBarRender={() => [
            <Accessor key="add" permission="materials:save">
              <Button
                type="primary"
                onClick={() => navigate(`/materials/edit`, { state: null })}
              >
                创建
              </Button>
            </Accessor>,
          ]}
        />
      </PageContainer>
    </>
  );
};

export default MaterialListPage;
