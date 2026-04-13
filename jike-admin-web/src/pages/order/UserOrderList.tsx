import { PageContainer } from "@ant-design/pro-components";
import { Button, Divider, Input, message } from "antd";
import { useState } from "react";
import { sendGet } from "../../api/api";

interface IProps {}

const UserOrderList: React.FC<IProps> = (props) => {
  const [uuid, setUUid] = useState("");
  const [orderNo, setOrderNo] = useState("");
  const [data, setData] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchData = async (params) => {
    try {
      setLoading(true);
      const res = await sendGet("/order/v1/user-payed-order-list", {
        uuid: params.uuid,
        orderNo: params.orderNo,
      });
      setLoading(false);
      if (res.code === 10000) {
        if (res.data.length == 0) {
          message.info("未查询到信息");
        }
        setData(res.data);
      } else {
        message.error("获取数据失败:" + res.msg);
        console.error("获取数据失败:", res.msg);
      }
    } catch (error) {
      setLoading(false);
      console.log(error);
    }
  };

  return (
    <>
      <PageContainer>
        <div className="flex flex-col gap-[16px]">
          <div className="flex w-[300px] gap-[16px]">
            <Input
              placeholder="请输入即课网uid"
              value={uuid}
              onChange={(e) => setUUid(e.target.value)}
            />
            <Button
              loading={loading}
              type="primary"
              onClick={() => {
                if (uuid.length == 0) {
                  message.info("请输入uid");
                  return;
                }
                fetchData({ uuid: uuid });
              }}
            >
              查询
            </Button>
          </div>
          <div className="flex w-[300px] gap-[16px]">
            <Input
              placeholder="请输入商户单号"
              value={orderNo}
              onChange={(e) => setOrderNo(e.target.value)}
            />
            <Button
              loading={loading}
              type="primary"
              onClick={() => {
                if (orderNo.length == 0) {
                  message.info("请输入商户单号");
                  return;
                }
                fetchData({ orderNo: orderNo });
              }}
            >
              查询
            </Button>
          </div>
        </div>
        <Divider />
        <div>
          {data.map((d) => (
            <div>
              {/* 2025-10-13 07:43:14 用户 购买了 LV02, 实际支付300, 交易单号：1427199986768019456 */}
              {d.createTimeText} 用户购买了 {d.name}, 实际支付{d.payPrice},
              商户单号为: {d.orderNo}
            </div>
          ))}
        </div>
      </PageContainer>
    </>
  );
};

export default UserOrderList;
