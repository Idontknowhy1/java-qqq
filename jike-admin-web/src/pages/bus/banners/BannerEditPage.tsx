import { PageContainer } from "@ant-design/pro-components";
import { Button, Form, Input, message, Space, Switch } from "antd";
import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { sendPost } from "../../../api/api";
import OssUpload from "../../../components/OssUpload";

interface IProps {}

const BannerEditPage: React.FC<IProps> = (props) => {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const record = useLocation().state;
  const [loading, setLoading] = useState(false);

  const [title, setTitle] = useState("创建Banner");

  const goBack = () => navigate(-1);

  const saveData = async (values: any) => {
    try {
      setLoading(true);
      const res = await sendPost("/banners/v1/save", {
        ...values,
        status: values.status ? "0" : "1",
      });
      if (res.code === 10000) {
        message.success("保存成功");
        goBack();
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
    if (record) {
      setTitle("编辑Banner");
      form.setFieldsValue({
        id: record.id,
        title: record.title,
        image: record.image,
        link: record.link,
        sort: record.sort,
        status: record.status == "1" ? false : true,
      });
    } else {
      form.setFieldsValue({
        id: "",
        title: "",
        image: "",
        link: "",
        sort: 0,
        status: true,
      });
    }
  }, [record]);

  return (
    <>
      <PageContainer
        onBack={goBack}
        header={{
          title: title,
        }}
      >
        <Form form={form} labelCol={{ span: 4 }}>
          <Form.Item label="id" name="id" hidden={true}>
            <Input />
          </Form.Item>
          <Form.Item
            label="标题"
            name="title"
            rules={[{ required: true, message: "请输入标题" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item label="头像" name="image">
            <OssUpload />
          </Form.Item>
          <Form.Item label="链接" name="link">
            <Input />
          </Form.Item>
          <Form.Item label="排序" name="sort">
            <Input />
          </Form.Item>
          <Form.Item label="启用" name="status">
            <Switch />
          </Form.Item>
        </Form>
        <div className="flex justify-end w-full">
          <Space>
            <Button
              type="primary"
              onClick={() =>
                form.validateFields().then((values) => {
                  saveData(values);
                })
              }
            >
              提交
            </Button>
            <Button onClick={goBack}>取消</Button>
          </Space>
        </div>
      </PageContainer>
    </>
  );
};

export default BannerEditPage;
