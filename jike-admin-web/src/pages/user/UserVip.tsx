import { PageContainer } from "@ant-design/pro-components";
import { useState } from "react";
import BatSetVip from "./components/BatSetVip";
import SingleSetVip from "./components/SingleSetVip";

interface IProps {}

const UserVip: React.FC<IProps> = (props) => {
  const [activeKey, setActiveKey] = useState<"BAT" | "SINGLE">("BAT"); // 设置默认激活的标签页

  return (
    <>
      <PageContainer
        tabList={[
          {
            tab: "批量会员设置",
            key: "BAT",
          },
          {
            tab: "会员查询",
            key: "SINGLE",
          },
        ]}
        tabActiveKey={activeKey}
        onTabChange={(key) => setActiveKey(key)}
      >
        {activeKey == "BAT" && <BatSetVip />}
        {activeKey == "SINGLE" && <SingleSetVip />}
      </PageContainer>
    </>
  );
};

export default UserVip;
