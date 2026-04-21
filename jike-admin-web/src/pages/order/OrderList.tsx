import {
  PageContainer,
  ProTable,
  type ActionType,
  type ProColumns,
} from "@ant-design/pro-components";
import { Button, message, Select, Space, Tag } from "antd/lib";
import { useRef, useState } from "react";
import { sendGet } from "../../api/api";

interface IProps {}

interface DataType {
  id: number;
  name: string;
  orderNo: string;
  outTradeNo: string;
  payPrice: number;
  payTime: string;
  createTime: string;
  refundNo: string;
  refundTime: string;
  status: number;
}

const OrderList: React.FC<IProps> = (props) => {
  const actionRef = useRef<ActionType>(null);
  const [selectStatus, setSelectStatus] = useState(2);

  const [pageInfo, setPageInfo] = useState({
    current: 1,
    pageSize: 50,
  });

  const fetchData = async (params) => {
    try {
      const res = await sendGet("/order/v1/page-list", {
        ...params,
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

  // 支付状态检测
  const checkStatus = async (orderNo: string) => {
    try {
      const res = await sendGet("/order/v1/check-status", {
        orderNo,
      });
      if (res.code === 10000) {
        if (res.data.payed) {
          actionRef.current?.reload();
        }
        message.success(res.data.msg);
      } else {
        message.error("支付状态检测失败:" + res.msg);
        console.error("支付状态检测失败:", res.msg);
      }
    } catch (error) {
      console.log(error);
    }
  };

  // 退款
  // const refund = async (orderNo: string) => {
  //   try {
  //     const res = await sendGet("/order/v1/refund", {
  //       orderNo,
  //     });
  //     if (res.code === 10000) {
  //       if (res.data.payed) {
  //         actionRef.current?.reload();
  //       }
  //       message.success(res.data.msg);
  //     } else {
  //       message.error("支付状态检测失败:" + res.msg);
  //       console.error("支付状态检测失败:", res.msg);
  //     }
  //   } catch (error) {
  //     console.log(error);
  //   }
  // };

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
      title: "状态",
      dataIndex: "status",
      hideInSetting: true,
      hideInTable: true,
      renderFormItem(_, { type }, form) {
        if (type === "form") {
          return null; // 确保只在搜索栏中渲染
        }
        // 获取当前表单中 projectId 的值
        const fieldValue =
          form.getFieldValue("status") === undefined
            ? 2
            : form.getFieldValue("status");
        return (
          <Select
            value={fieldValue}
            options={[
              { value: 0, label: "待支付" },
              { value: 1, label: "已支付" },
              { value: 3, label: "已退款" },
            ]}
            onChange={(newValue) => {
              form.setFieldsValue({ status: newValue });
              form.submit();
              setSelectStatus(newValue);
            }}
          />
        );
      },
    },
    {
      title: "用户ID",
      dataIndex: "uuid",
    },
    {
      title: "商品名",
      dataIndex: "name",
      key: "name",
      hideInSearch: true,
    },
    {
      title: "商户单号",
      dataIndex: "orderNo",
      key: "orderNo",
    },
    {
      title: "微信单号",
      dataIndex: "outTradeNo",
      key: "outTradeNo",
      hideInTable: selectStatus < 1,
    },
    {
      title: "状态",
      dataIndex: "status",
      key: "status",
      hideInSearch: true,
      render: (value) => {
        if (value === 0) {
          return <Tag color="orange">待支付</Tag>;
        } else if (value === 1) {
          return <Tag color="green">已支付</Tag>;
        } else if (value === 3) {
          return <Tag color="red">已退款</Tag>;
        }
        return <></>;
      },
    },
    {
      title: "创建时间",
      dataIndex: "createTime",
      key: "createTime",
      hideInSearch: true,
    },
    {
      title: "支付时间",
      dataIndex: "payTime",
      key: "payTime",
      hideInSearch: true,
      hideInTable: selectStatus < 1,
    },
    {
      title: "支付价格",
      dataIndex: "payPrice",
      key: "payPrice",
      hideInSearch: true,
      hideInTable: selectStatus < 2,
    },
    {
      title: "退款时间",
      dataIndex: "refundTime",
      key: "refundTime",
      hideInSearch: true,
      hideInTable: selectStatus != 3,
    },
    {
      title: "操作",
      hideInSearch: true,
      hideInTable: selectStatus > 0,
      render: (_, record) => {
        if (record.status === 0) {
          return (
            <Space>
              <Button
                type="link"
                size="small"
                onClick={() => checkStatus(record.orderNo)}
              >
                支付状态检测
              </Button>
            </Space>
          );
        }
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
          scroll={{ x: 1600 }}
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
          pagination={{
            pageSize: pageInfo.pageSize,
            showSizeChanger: true,
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

export default OrderList;
