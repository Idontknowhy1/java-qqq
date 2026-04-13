import { Form, Input, List, message, Modal } from "antd";
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { sendPost } from "../../../../api/api";
import useUserStore from "../../../../store/useUserStore";
import { md5Pwd } from "../../../../utils/utils";

type Unpacked<T> = T extends (infer U)[] ? U : T;

const SecurityView: React.FC = () => {
  const [isUpdatePwdOpen, setIsUpdatePwdOpen] = useState(false);
  const [form] = Form.useForm();
  const navigate = useNavigate();

  const getData = () => [
    {
      title: "账户密码",
      description: (
        <>
          {/* 当前密码强度：
          {passwordStrength.strong} */}
        </>
      ),
      actions: [
        <a key="Modify" onClick={() => setIsUpdatePwdOpen(true)}>
          修改
        </a>,
      ],
    },
    // {
    //   title: '密保手机',
    //   description: `已绑定手机：138****8293`,
    //   actions: [<a key="Modify">修改</a>],
    // },
    // {
    //   title: '密保问题',
    //   description: '未设置密保问题，密保问题可有效保护账户安全',
    //   actions: [<a key="Set">设置</a>],
    // },
    // {
    //   title: '备用邮箱',
    //   description: `已绑定邮箱：ant***sign.com`,
    //   actions: [<a key="Modify">修改</a>],
    // },
    // {
    //   title: 'MFA 设备',
    //   description: '未绑定 MFA 设备，绑定后，可以进行二次确认',
    //   actions: [<a key="bind">绑定</a>],
    // },
  ];

  const data = getData();

  const savePwd = async (values: {
    oldPassword: string;
    newPassword: string;
    confirmPassword: string;
  }) => {
    try {
      if (values.newPassword !== values.confirmPassword) {
        message.error("两次输入的密码不一致");
        return;
      }

      const res = await sendPost("/sysaccount/v1/changePwd", {
        oldPwd: md5Pwd(values.oldPassword),
        newPwd: md5Pwd(values.newPassword),
      });
      if (res.code === 10000) {
        message.success("保存成功！");
        useUserStore.getState().logout();
        navigate("/login");
      } else {
        message.error("保存失败:" + res.msg);
        console.error("保存失败:", res.msg);
      }
    } catch (error) {
      console.log(error);
      console.error("保存失败:");
    }
  };

  return (
    <>
      <List<Unpacked<typeof data>>
        itemLayout="horizontal"
        dataSource={data}
        renderItem={(item) => (
          <List.Item actions={item.actions}>
            <List.Item.Meta title={item.title} description={item.description} />
          </List.Item>
        )}
      />
      <Modal
        title="修改密码"
        closable={false}
        open={isUpdatePwdOpen}
        okText="确认"
        cancelText="取消"
        onOk={() => {
          form.validateFields().then((values) => {
            savePwd(values);
          });
        }}
        onCancel={() => setIsUpdatePwdOpen(false)}
      >
        <Form form={form} labelCol={{ span: 4 }}>
          <Form.Item label="id" name="id" hidden={true}>
            <Input />
          </Form.Item>
          <Form.Item
            label="旧密码"
            name="oldPassword"
            rules={[{ required: true, message: "请输入旧密码" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="新密码"
            name="newPassword"
            rules={[{ required: true, message: "请输入新密码" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="确认密码"
            name="confirmPassword"
            rules={[{ required: true, message: "请输入确认密码" }]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
};

export default SecurityView;
