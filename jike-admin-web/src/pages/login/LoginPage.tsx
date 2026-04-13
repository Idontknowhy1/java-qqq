import LogoImage from "@/assets/images/logo.png";
import { LockOutlined, UserOutlined } from "@ant-design/icons";
import {
  LoginFormPage,
  ProConfigProvider,
  ProFormText,
} from "@ant-design/pro-components";
import { Image, message } from "antd";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { sendPost } from "../../api/api";
import { md5Pwd } from "../../utils/utils";

const LoginPage = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const handleSubmit = async (values: {
    account: string;
    password: string;
  }) => {
    try {
      // 登录
      setLoading(true);
      const res = await sendPost("/sysaccount/v1/login", {
        account: values.account,
        password: md5Pwd(values.password),
      });
      setLoading(false);
      if (res.code === 10000) {
        localStorage.setItem("x-token", res.data.token);
        message.success("登录成功！");
        navigate("/");
      } else {
        // message.error(res.msg);
      }
    } catch (error) {
      setLoading(false);
      console.log(error);
      message.error("登录失败");
    }
  };

  return (
    <div
      style={{
        backgroundColor: "white",
        height: "100vh",
      }}
    >
      <LoginFormPage
        // backgroundImageUrl="https://mdn.alipayobjects.com/huamei_gcee1x/afts/img/A*y0ZTS6WLwvgAAAAAAAAAAAAADml6AQ/fmt.webp"
        // logo="https://github.githubassets.com/favicons/favicon.png"
        backgroundVideoUrl="https://gw.alipayobjects.com/v/huamei_gcee1x/afts/video/jXRBRK_VAwoAAAAAAAAAAAAAK4eUAQBr"
        // title="Github"
        containerStyle={{
          backdropFilter: "blur(4px)",
        }}
        onFinish={async (values) => {
          handleSubmit(values);
        }}
        loading={loading}
      >
        <div className="flex justify-center mb-[20px]">
          <Image src={LogoImage} width={200} />
        </div>
        <>
          <ProFormText
            name="account"
            fieldProps={{
              size: "large",
              prefix: <UserOutlined className={"prefixIcon"} />,
            }}
            placeholder={"用户名"}
            rules={[
              {
                required: true,
                message: "请输入用户名!",
              },
            ]}
          />
          <ProFormText.Password
            name="password"
            fieldProps={{
              size: "large",
              prefix: <LockOutlined className={"prefixIcon"} />,
            }}
            placeholder={"密码"}
            rules={[
              {
                required: true,
                message: "请输入密码！",
              },
            ]}
          />
        </>
      </LoginFormPage>
    </div>
  );
};

export default () => {
  return (
    <ProConfigProvider>
      {/* <div className="input-wrapper" style={{ width: 200 }}>
        <input placeholder="用户名" style={{ width: 100 }} />
      </div>
      <div className="input-wrapper" style={{ width: 200 }}>
        <input placeholder="用户名1" style={{ width: 100 }} />
      </div>
      <div className="input-wrapper" style={{ width: 200 }}>
        <input placeholder="用户名2" style={{ width: 100 }} />
      </div> */}

      <LoginPage />
    </ProConfigProvider>
  );
};
