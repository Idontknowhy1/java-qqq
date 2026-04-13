import { Button, Divider, Input, message } from "antd";
import clsx from "clsx";
import { useState } from "react";
import { sendGet } from "../../../api/api";
interface IProps {}

const SingleSetVip: React.FC<IProps> = (props) => {
  const [data, setData] = useState<any>(null);
  const [uuid, setUUid] = useState("");
  const [loading, setLoading] = useState(false);

  const fetchData = async () => {
    try {
      setLoading(true);
      const res = await sendGet("/user/v1/get-member-info-by-uuid", {
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

  const propertyClass = clsx("flex gap-[16px]");
  const titleClass = clsx("w-[100px] font-bold");
  const valueClass = clsx("");

  return (
    <>
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
            fetchData();
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
            <div className={titleClass}>用户名</div>
            <div className={valueClass}>{data.username}</div>
          </div>
          <div className={propertyClass}>
            <div className={titleClass}>昵称</div>
            <div className={valueClass}>{data.nickname}</div>
          </div>
          <div className={propertyClass}>
            <div className={titleClass}>会员等级</div>
            <div className={valueClass}>{data.vipLevel}</div>
          </div>
          <div className={propertyClass}>
            <div className={titleClass}>会员到期时间</div>
            <div className={valueClass}>{data.pluginMemberExpireAt}</div>
          </div>
        </div>
      )}
    </>
  );
};

export default SingleSetVip;
