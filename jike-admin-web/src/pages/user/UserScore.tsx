import { PageContainer } from "@ant-design/pro-components";
import { Button, Divider, Input, InputNumber, message } from "antd";
import clsx from "clsx";
import { useState } from "react";
import { sendGet } from "../../api/api";

interface IProps {}

const UserScore: React.FC<IProps> = (props) => {
  const [data, setData] = useState(null);
  const [uuid, setUUid] = useState("");
  const [score, setScore] = useState(0);
  const [loading, setLoading] = useState(false);

  const fetchData = async () => {
    try {
      setLoading(true);
      const res = await sendGet("/userscore/v1/get-user-score", {
        uuid: uuid,
      });
      setLoading(false);
      if (res.code === 10000) {
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

  const addUserScore = async () => {
    try {
      const res = await sendGet("/userscore/v1/add-score", {
        toUserId: data.userIdText,
        score: score,
      });
      if (res.code === 10000) {
        fetchData();
      } else {
        message.error("获取数据失败:" + res.msg);
        console.error("获取数据失败:", res.msg);
      }
    } catch (error) {
      console.log(error);
    }
  };

  const propertyClass = clsx("flex gap-[16px]");
  const titleClass = clsx("w-[70px] font-bold");
  const valueClass = clsx("");

  return (
    <>
      <PageContainer>
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
        <Divider />
        {data && (
          <div>
            <div className={propertyClass}>
              <div className={titleClass}>用户ID</div>
              <div className={valueClass}>{uuid}</div>
            </div>
            <div className={propertyClass}>
              <div className={titleClass}>会员积分</div>
              <div className={valueClass}>{data.vipScore}</div>
            </div>
            <div className={propertyClass}>
              <div className={titleClass}>永久积分</div>
              <div className={valueClass}>{data.forScore}</div>
            </div>
            <div className="flex w-[300px]">
              <InputNumber
                placeholder="请输入即课网uid"
                value={score}
                onChange={(e) => setScore(e || 0)}
              />
              <Button type="primary" onClick={addUserScore}>
                添加永久积分
              </Button>
            </div>
          </div>
        )}
      </PageContainer>
    </>
  );
};

export default UserScore;
