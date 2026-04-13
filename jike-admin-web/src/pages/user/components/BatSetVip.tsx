interface IProps {}
import { Button, Input, message, Select } from "antd";
import { useState } from "react";
import { sendGet } from "../../../api/api";
const BatSetVip: React.FC<IProps> = (props) => {
  const [vipLevel, setVipLevel] = useState("VIP_LV03");
  const [userIdList, setUserIdList] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [logs, setLogs] = useState<string[]>([]);

  // 用户UUID和ID映射表
  let userIdMap: any[] = [];
  // vip添加成功的数量
  let vipSuccess: any[] = [];

  const vipLevels = [
    { label: " LV00", value: "VIP_LV00" },
    { label: " LV01", value: "VIP_LV01" },
    { label: " LV02", value: "VIP_LV02" },
    { label: " LV03", value: "VIP_LV03" },
  ];

  const vipLevelDescriptions = {
    VIP_LV00: ["删除会员"],
    VIP_LV01: ["简笔画素材（一年）", "简笔画录播", "小说授权"],
    VIP_LV02: [
      "全部素材网使用权益（一年）",
      "AE插件（一年）",
      "每月赠送300积分",
      "小说授权: 无",
      "简笔画录播",
    ],
    VIP_LV03: [
      "全部素材网使用权益（一年）",
      "AE插件（一年）",
      "每月赠送900积分",
      "小说授权: 无",
      "简笔画录播",
      "365天直播课",
      "商单渠道保证",
      "讲师晋升通道",
      "一年陪跑（远程答疑）",
    ],
  };

  const executeAddVip = async () => {
    setLogs(() => []);
    userIdMap = [];

    if (userIdList.length == 0) {
      message.info("请输入用户ID");
      return;
    }

    setLoading(true);

    // 检查用户ID是否有效
    setLogs((logs) => [...logs, "正在校验用户ID"]);
    await checkUserIds();

    setLogs((logs) => [...logs, " ", "正在添加会员"]);
    await addVip();

    message.success("批量添加会员完成");
    setLogs((logs) => [...logs, " ", "批量添加会员完成"]);

    setLoading(false);
  };

  // 添加会员
  const addVip = async () => {
    vipSuccess = [];
    for (let user of userIdMap) {
      await _addVip(user);
    }
    setLogs((logs) => [
      ...logs,
      "  " + vipSuccess.length + " 个用户添加会员成功",
    ]);
  };

  // 添加会员
  const _addVip = async (user: any) => {
    try {
      setLoading(true);
      const res = await sendGet("/user/v1/add-vip", {
        toUserId: user.id,
        vipTag: vipLevel,
        vipName: vipLevels.filter((v) => v.value == vipLevel)[0].label ?? "",
      });
      setLoading(false);
      if (res.code === 10000) {
        vipSuccess.push(user.uuid);
        setLogs((logs) => [...logs, " " + user.uuid + " 添加会员成功"]);
      } else {
        setLogs((logs) => [
          ...logs,
          " " + user.uuid + " 添加会员失败," + res.msg,
        ]);
      }
    } catch (error) {
      setLoading(false);
      console.log(error);
    }
  };

  // 检查用户ID是否有效
  const checkUserIds = async () => {
    let uuids = userIdList.split("\n");
    setLogs((logs) => [...logs, "共输入 " + uuids.length + " 个用户ID"]);
    for (let uuid of uuids) {
      await _checkUserId(uuid);
    }
    console.log(userIdMap);
    setLogs((logs) => [
      ...logs,
      "  其中 " + userIdMap.length + " 个有效用户ID",
    ]);
  };

  // 查询用户信息
  const _checkUserId = async (uuid: string) => {
    try {
      setLoading(true);
      const res = await sendGet("/user/v1/get-member-info-by-uuid", {
        uuid: uuid,
      });
      setLoading(false);
      if (res.code === 10000) {
        if (res.code == 10000) {
          if (res.data) {
            userIdMap.push({
              id: res.data.id,
              uuid: uuid,
            });
          } else {
            setLogs((logs) => [...logs, "  " + uuid + " 无效"]);
          }
        } else {
          message.error("查询用户信息失败");
          return;
        }
      } else {
        setLogs((logs) => [...logs, " " + user.uuid + " 添加会员失败"]);
      }
    } catch (error) {
      setLoading(false);
      console.log(error);
    }
  };

  return (
    <>
      <div className="flex gap-[16px]">
        <div className="flex flex-col w-[200px] gap-[16px]">
          <div className="">用户ID列表</div>
          <Input.TextArea
            rows={10}
            onChange={(e) => setUserIdList(e.target.value)}
          />
          <Button type="primary" loading={loading} onClick={executeAddVip}>
            添加会员
          </Button>
        </div>
        <div className="flex flex-col w-[200px] gap-[16px]">
          <div className="">会员等级</div>
          <Select
            style={{ width: "100%" }}
            value={vipLevel}
            onChange={(value) => setVipLevel(value)}
            options={vipLevels}
          />
          <div>
            {vipLevelDescriptions[vipLevel] &&
              vipLevelDescriptions[vipLevel].map((d) => <div key={d}>{d}</div>)}
          </div>
        </div>
        <div className="w-[300px] flex flex-col text-[12px]">
          {logs.map((l, index) => (
            <span className="line-[30px]" key={index}>
              {l}
            </span>
          ))}
        </div>
      </div>
    </>
  );
};

export default BatSetVip;
