import { Form } from "antd";

interface IProps {}

const HomePage: React.FC<IProps> = (props) => {
  const [form] = Form.useForm();

  const onFinish = (values: any) => {
    console.log("表单数据:", values);
    // 提交表单逻辑
  };

  const test = () => {
    // 使用示例
    // const key: string = "46EBA22EF5204DD5B110A1F730513965"; // 建议密钥长度为16/24/32字节
    // const content: string = "Hello, AES Encryption!";
    // // 加密
    // const encryptedString: string = aes_encrypt(content, key);
    // console.log("加密结果 (Base64):", encryptedString);
    // // 解密
    // const decryptedText: string = aes_decrypt(encryptedString, key);
    // console.log("解密结果:", decryptedText); // 输出: "Hello, AES Encryption!"
  };

  return (
    <>
      {/* <Button onClick={test}>测试</Button>
      <Form form={form} onFinish={onFinish} layout="vertical">
        <Form.Item label="头像" name="avatar">
          <OssUpload />
        </Form.Item>

        <Form.Item label="封面图片" name="coverImage">
          <OssUpload accept="image/png,image/jpeg" maxSize={5} />
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit">
            提交
          </Button>
        </Form.Item>
      </Form> */}
    </>
  );
};

export default HomePage;
