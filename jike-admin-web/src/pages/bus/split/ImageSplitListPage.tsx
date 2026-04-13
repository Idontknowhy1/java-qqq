import {
  PageContainer,
  ProTable,
  type ActionType,
  type ProColumns,
} from "@ant-design/pro-components";
import { Button, Image, Input, message, Modal, Popconfirm, Space } from "antd";
import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { sendGet, sendPost } from "../../../api/api";

interface IProps {}

interface DataType {
  id: string;
  title: string;
  detail: string;
  imageUrl: string;
  resultUrl: string;
  state: number;
  reason: string;
  createTimeText: string;
}

const MaterialListPage: React.FC<IProps> = (props) => {
  const navigate = useNavigate();
  const actionRef = useRef<ActionType>(null);

  const [modalInfo, setModalInfo] = useState<{
    type: "UNKNOW" | "REFUSE" | "UPLOAD";
    show: boolean;
  }>({
    type: "UNKNOW",
    show: false,
  });
  const [textInfo, setTextInfo] = useState("");
  const [currentRecord, setCurrentRecord] = useState<DataType | null>(null);

  const [pageInfo, setPageInfo] = useState({
    current: 1,
    pageSize: 10,
  });

  const fetchData = async (params) => {
    try {
      const res = await sendGet("/imagesplit/v1/page-list", {
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

  const updateStatus = async (params) => {
    try {
      const res = await sendPost("/imagesplit/v1/update-state", {
        id: params.id,
        state: params.state,
        resultUrl: params.resultUrl,
        reason: params.reason,
      });
      if (res.code === 10000) {
        actionRef.current?.reload();
        setModalInfo({
          show: false,
          type: "UNKNOW",
        });
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
    },
    {
      title: "标题",
      dataIndex: "title",
      key: "title",
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
      title: "要求",
      dataIndex: "detail",
      key: "detail",
      hideInSearch: true,
    },
    {
      title: "状态",
      dataIndex: "state",
      key: "state",
      hideInSearch: true,
      width: 100,
      render: (value) => {
        // 0-待审核,1-制作中，2-已完成，3-驳回
        if (value == 0) return <div>待审核</div>;
        else if (value == 1) return <div>制作中</div>;
        else if (value == 2) return <div>已完成</div>;
        else if (value == 3) return <div>驳回</div>;
        return "";
      },
    },
    {
      title: "提交时间",
      dataIndex: "createTimeText",
      key: "createTimeText",
      width: 200,
    },
    {
      title: "原因",
      dataIndex: "reason",
      key: "reason",
      hideInSearch: true,
      width: 200,
    },
    {
      title: "操作",
      hideInSearch: true,
      width: 200,
      render: (_, record) => {
        if (record.state == 0) {
          // 待审核
          return (
            <Space>
              <Popconfirm
                title="确认接受吗？"
                onConfirm={() =>
                  updateStatus({
                    id: record.id,
                    state: 1,
                  })
                }
                okText="是"
                cancelText="否"
              >
                <Button>接受</Button>
              </Popconfirm>
              <Button
                onClick={() => {
                  setCurrentRecord(record);
                  setModalInfo({
                    show: true,
                    type: "REFUSE",
                  });
                }}
              >
                拒绝
              </Button>
            </Space>
          );
        } else if (record.state == 1) {
          // 待审核
          return (
            <Space>
              <Button
                onClick={() => {
                  setCurrentRecord(record);
                  setModalInfo({
                    show: true,
                    type: "UPLOAD",
                  });
                }}
              >
                上传回填
              </Button>
            </Space>
          );
        } else if (record.state == 2) {
          // 已完成
          return (
            <Space>
              <Popconfirm
                title="确认撤销吗？"
                onConfirm={() =>
                  updateStatus({
                    id: record.id,
                    state: 1,
                  })
                }
                okText="是"
                cancelText="否"
              >
                <Button>撤销上传</Button>
              </Popconfirm>
            </Space>
          );
        }
      },
    },
  ];

  useEffect(() => {
    if (modalInfo.show == false) {
      setCurrentRecord(null);
      setTextInfo("");
    }
  }, [modalInfo.show]);

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

        <Modal
          title={modalInfo.type == "REFUSE" ? "拒绝理由" : "上传回填"}
          open={modalInfo.show}
          onOk={() => {
            if (textInfo.length == 0) {
              message.info("请输入内容");
              return;
            }
            if (modalInfo.type == "UPLOAD") {
              updateStatus({
                id: currentRecord?.id,
                state: 2,
                resultUrl: textInfo,
              });
            } else {
              updateStatus({
                id: currentRecord?.id,
                state: 3,
                reason: textInfo,
              });
            }
          }}
          onCancel={() =>
            setModalInfo({
              show: false,
              type: "UNKNOW",
            })
          }
        >
          <Input.TextArea
            rows={10}
            value={textInfo}
            onChange={(e) => setTextInfo(e.target.value)}
          />
        </Modal>
      </PageContainer>
    </>
  );
};

export default MaterialListPage;
