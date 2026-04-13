interface IProps {}
import { Button, Input, message } from "antd";
import { useEffect, useState } from "react";
import { sendGet, sendPost } from "../../../../api/api";

const BaseConfig: React.FC<IProps> = (props) => {
  const [config, setConfig] = useState<{
    aeUrl: string;
  }>({
    aeUrl: "",
  });
  const [loading, setLoading] = useState(false);

  const saveUrl = async () => {
    try {
      setLoading(true);
      const res = await sendPost("/sysconfig/v1/saveconfig", config);
      if (res.code === 10000) {
        message.success("保存成功");
      } else {
        message.error("保存失败:" + res.msg);
        console.error("保存失败:", res.msg);
      }
      setLoading(false);
    } catch (error) {
      setLoading(false);
      console.log(error);
    }
  };

  const fetchUrl = async () => {
    try {
      setLoading(true);
      const res = await sendGet("/sysconfig/v1/getconfig");
      if (res.code === 10000) {
        setConfig(res.data);
      } else {
        message.error("保存失败:" + res.msg);
        console.error("保存失败:", res.msg);
      }
      setLoading(false);
    } catch (error) {
      setLoading(false);
      console.log(error);
    }
  };

  useEffect(() => {
    fetchUrl();
  }, []);

  return (
    <>
      <div className="">
        <div className="flex flex-col gap-[16px]">
          <div className="">AE插件下载地址</div>
          <div className="flex gap-[16px]">
            <Input
              value={config.aeUrl}
              onChange={(e) => setConfig({ ...config, aeUrl: e.target.value })}
            />
            <Button type="primary" onClick={saveUrl} loading={loading}>
              保存
            </Button>
          </div>
        </div>
      </div>
    </>
  );
};

export default BaseConfig;
