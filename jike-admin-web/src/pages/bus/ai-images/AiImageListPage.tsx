import {
  PageContainer,
  ProTable,
  type ActionType,
  type ProColumns,
} from "@ant-design/pro-components";
import { Button, Drawer, Image, message } from "antd";
import clsx from "clsx";
import { useRef, useState } from "react";
import { sendGet, sendPost } from "../../../api/api";

interface IProps {}

interface DataType {
  id: string;
  model: string;
  uuid: string;
  nickname: string;
  referImages: string;
  resultImages: string;
  coverImage: string;
  prompt: string;
  createTimeText: string;
  aspectRatio: string;
}

const AiImageListPage: React.FC<IProps> = (props) => {
  const actionRef = useRef<ActionType>(null);
  const [currentRecord, setCurrentRecord] = useState<DataType | null>(null);
  const [showDetailDraw, setShowDetailDraw] = useState(false);

  const [pageInfo, setPageInfo] = useState({
    current: 1,
    pageSize: 10,
  });

  const fetchData = async (params) => {
    try {
      const res = await sendGet("/nanotasks/v1/page-list", {
        pageNum: params.current,
        pageSize: params.pageSize,
        uuid: params.uuid,
      });
      if (res.code === 10000) {
        res.data.list.forEach((element) => {
          element.resultImages = JSON.parse(element.resultImages) ?? [];
          element.coverImage = element.resultImages[0];
          element.referImages = JSON.parse(element.referImages) ?? [];
        });
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

  const addSquare = async () => {
    if (currentRecord == null) {
      return;
    }
    try {
      const res = await sendPost("/materialsquare/v1/save", {
        assId: currentRecord.id,
        prompt: currentRecord.prompt,
        imageUrl: currentRecord.resultImages[0],
        referImages: JSON.stringify(currentRecord.referImages),
      });
      if (res.code === 10000) {
        message.success("添加成功");
      } else {
        message.error("获取数据失败:" + res.msg);
        console.error("获取数据失败:", res.msg);
      }
    } catch (error) {
      console.log(error);
    }
  };

  const showDetail = (record: DataType) => {
    setShowDetailDraw(true);
    setCurrentRecord(record);
  };

  const columns: ProColumns<DataType> = [
    {
      title: "图片",
      dataIndex: "resultImages",
      key: "resultImages",
      hideInSearch: true,
      render: (value) => {
        return (
          <Image.PreviewGroup items={value}>
            <Image width={200} src={value[0]} />
          </Image.PreviewGroup>
        );
      },
    },
    {
      title: "模型",
      dataIndex: "model",
      key: "model",
      render: (value) => {
        if (value === "multiple_scene") return "多视角";
        if (value === "nano-banana") return "banana";
        if (value === "banana2") return "banana pro";
        return value;
      },
      hideInSearch: true,
    },
    {
      title: "id(点击查看详情)",
      dataIndex: "id",
      key: "id",
      render: (value, record) => {
        return (
          <Button type="link" onClick={() => showDetail(record)}>
            {value}
          </Button>
        );
      },
      hideInSearch: true,
    },
    {
      title: "uuid",
      dataIndex: "uuid",
      key: "uuid",
    },
    {
      title: "用户名",
      dataIndex: "nickname",
      key: "nickname",
      hideInSearch: true,
    },
    {
      title: "提示词",
      dataIndex: "prompt",
      key: "prompt",
      hideInSearch: true,
    },
    {
      title: "创建时间",
      dataIndex: "createTimeText",
      key: "createTimeText",
      hideInSearch: true,
    },
  ];

  let detailIdColTitleStyle = clsx("text-[16px] font-bold w-[80px] shrink-0");
  let detailIdColValueStyle = clsx("flex flex-wrap text-[16px] grow gap-[8px]");

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

      <Drawer
        title="AI图片"
        placement="right"
        onClose={() => setShowDetailDraw(false)}
        open={showDetailDraw}
        width={700}
      >
        <div className="flex flex-col gap-[16px]">
          <div className="flex">
            <div className={detailIdColTitleStyle}>ID:</div>
            <div className={detailIdColValueStyle}>{currentRecord?.id}</div>
          </div>
          <div className="flex">
            <div className={detailIdColTitleStyle}>垫图：</div>
            <div className={detailIdColValueStyle}>
              <div className={detailIdColValueStyle}>
                {currentRecord?.referImages.map((image) => (
                  <Image
                    src={image}
                    style={{
                      width: 100,
                      height: 100,
                      borderRadius: 1,
                      border: "2px solid #A594F9",
                      padding: 4,
                      objectFit: "cover",
                    }}
                  />
                ))}
              </div>
            </div>
          </div>
          <div className="flex">
            <div className={detailIdColTitleStyle}>提示词：</div>
            <div className={detailIdColValueStyle}>{currentRecord?.prompt}</div>
          </div>
          <div className="flex">
            <div className={detailIdColTitleStyle}>结果图：</div>
            <div className={detailIdColValueStyle}>
              {currentRecord?.resultImages.map((image) => (
                <Image
                  src={image}
                  style={{
                    width: 100,
                    height: 100,
                    borderRadius: 4,
                    filter: "grayscale(50%)",
                    border: "2px solid #A594F9",
                    padding: 4,
                    transition: "all 0.3s ease",
                    objectFit: "cover",
                  }}
                />
              ))}
            </div>
          </div>
          <Button type="primary" style={{ width: 200 }} onClick={addSquare}>
            添加到素材广场
          </Button>
        </div>
      </Drawer>
    </>
  );
};

export default AiImageListPage;
