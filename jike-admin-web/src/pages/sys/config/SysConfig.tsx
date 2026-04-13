import { PageContainer } from "@ant-design/pro-components";
import { useState } from "react";
import BaseConfig from "./components/BaseConfig";

interface IProps {}

const SysConfigPage: React.FC<IProps> = (props) => {
  const [selectedKey, setSelectedKey] = useState("base-config");
  return (
    <>
      <PageContainer
        title={null}
        tabList={[
          {
            tab: "基础配置",
            key: "base-config",
          },
        ]}
        tabActiveKey={selectedKey}
        onTabChange={(key) => setSelectedKey(key)}
      >
        {selectedKey == "base-config" && <BaseConfig />}
      </PageContainer>
    </>
  );
};

export default SysConfigPage;
