import { message, Select } from "antd";
import { useEffect, useState } from "react";
import { sendGet } from "../api/api";
interface IProps {
  value?: string | number;
  onChange?: (newValue: string | number) => void;
}

const MaterialCategories: React.FC<IProps> = (props) => {
  const [data, setData] = useState<{ value: string | number; label: string }[]>(
    [],
  );
  const [selectedValue, setSelectedValue] = useState<string | number>("");

  const queryData = async () => {
    try {
      const res = await sendGet("/dicts/v1/detail-list?type=materials_types");
      if (res.code === 10000) {
        let data = res.data.map((item) => ({
          value: item.code,
          label: item.name,
        }));
        setData(data);
      } else {
        message.error("获取数据失败:" + res.msg);
        console.error("获取数据失败:", res.msg);
      }
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    queryData();
  }, []);

  useEffect(() => {
    if (props.value) {
      let selectedValue =
        data.find((item) => item.value == props.value)?.value || "";
      setSelectedValue(selectedValue);
    }
  }, [props.value, data]);

  return (
    <>
      <Select
        value={selectedValue}
        onChange={(newValue) => {
          props.onChange(newValue);
        }}
        options={data}
      />
    </>
  );
};

export default MaterialCategories;
