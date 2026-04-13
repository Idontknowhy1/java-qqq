import MinusCircleOutlined from "@ant-design/icons/MinusCircleOutlined";
import PlusCircleOutlined from "@ant-design/icons/PlusCircleOutlined";
import { PageContainer } from "@ant-design/pro-components";
import { Button, Form, Input, message, Space, Switch } from "antd";
import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { sendPost } from "../../../api/api";
import MaterialCategories from "../../../components/MaterialCategories";
import OssUpload from "../../../components/OssUpload";
import OssUploadList from "../../../components/OssUploadList";

interface IProps {}

const MaterialEdit: React.FC<IProps> = (props) => {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const record = useLocation().state;
  const [loading, setLoading] = useState(false);

  const [title, setTitle] = useState("创建素材");

  const goBack = () => navigate(-1);

  const saveData = async (values: any) => {
    try {
      setLoading(true);
      const res = await sendPost("/materials/v1/save", {
        ...values,
        status: values.status ? "0" : "1",
        details: JSON.stringify(
          values.details.map((p) => ({
            url: p,
          })),
        ),
        properties: JSON.stringify(values.properties),
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
      setTitle("编辑素材");
      form.setFieldsValue({
        id: record.id,
        categoryId: record.categoryId,
        intro: record.intro,
        title: record.title,
        coverImage: record.coverImage,
        baiduUrl: record.baiduUrl,
        sort: record.sort,
        status: record.status == "1" ? false : true,
        details: JSON.parse(record.details ?? "[]").map((d) => d.url),
        properties: JSON.parse(record.properties ?? "[]"),
      });
    } else {
      form.setFieldsValue({
        id: "",
        categoryId: "",
        intro: "",
        title: "",
        coverImage: "",
        baiduUrl: "",
        sort: 0,
        status: true,
        details: [],
        properties: [],
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
            label="分类"
            name="categoryId"
            rules={[{ required: true, message: "请选择分类" }]}
          >
            <MaterialCategories />
          </Form.Item>
          <Form.Item
            label="标题"
            name="title"
            rules={[{ required: true, message: "请输入标题" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item label="简介" name="intro">
            <Input />
          </Form.Item>
          <Form.Item label="封面" name="coverImage">
            <OssUpload />
          </Form.Item>
          <Form.Item label="百度链接" name="baiduUrl">
            <Input />
          </Form.Item>
          <Form.Item label="排序" name="sort">
            <Input />
          </Form.Item>
          <Form.Item label="启用" name="status">
            <Switch />
          </Form.Item>
          <Form.Item label="详情内容" name="details">
            <OssUploadList />
          </Form.Item>
          <Form.Item label="自定义属性">
            <Form.List name="properties">
              {(fields, { add, remove }) => (
                <>
                  {fields.map(({ key, name, ...restField }) => (
                    <Space
                      key={key}
                      style={{ display: "flex", marginBottom: 8 }}
                      align="baseline"
                    >
                      <Form.Item name={[name, "name"]}>
                        <Input />
                      </Form.Item>
                      <span>:</span>
                      <Form.Item name={[name, "value"]}>
                        <Input placeholder="属性值" style={{ width: 200 }} />
                      </Form.Item>
                      <MinusCircleOutlined
                        onClick={() => remove(name)}
                        style={{ color: "#ff4d4f" }}
                      />
                    </Space>
                  ))}
                  <Form.Item>
                    <Button
                      type="dashed"
                      onClick={() => add()}
                      className="w-[400px]"
                      icon={<PlusCircleOutlined />}
                    >
                      添加属性对
                    </Button>
                  </Form.Item>
                </>
              )}
            </Form.List>
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

export default MaterialEdit;
