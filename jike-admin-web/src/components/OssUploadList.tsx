// src/components/OssUpload/index.tsx

import { PlusOutlined } from "@ant-design/icons";
import type { GetProp, UploadFile, UploadProps } from "antd";
import { Image, message, Upload } from "antd";
import React, { useEffect, useRef, useState } from "react";
import { sendGet } from "../api/api";

type FileType = Parameters<GetProp<UploadProps, "beforeUpload">>[0];

interface IProps {
  value?: string[];
  onChange?: (value: string[]) => void;
  maxSize?: number;
}

const getBase64 = (file: FileType): Promise<string> =>
  new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result as string);
    reader.onerror = (error) => reject(error);
  });

const OssUploadList: React.FC<IProps> = ({ value, onChange, maxSize = 10 }) => {
  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewImage, setPreviewImage] = useState("");
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const uploadDataRef = useRef<any>(null);

  const getOssPolicy = async () => {
    return sendGet("/oss/policy");
  };

  // 获取上传参数
  const getUploadParams = async (file: File) => {
    try {
      const res = await getOssPolicy();
      if (res.code === 10000) {
        uploadDataRef.current = res.data;
        return true;
      } else {
        message.error(res.msg || "获取上传凭证失败");
      }
      return false;
    } catch (error) {
      message.error("获取上传凭证失败");
      return false;
    }
  };

  // 上传前处理
  const beforeUpload: UploadProps["beforeUpload"] = async (file) => {
    // 检查文件大小
    const isLtMaxSize = file.size / 1024 / 1024 < maxSize;
    if (!isLtMaxSize) {
      message.error(`图片必须小于 ${maxSize}MB!`);
      return false;
    }

    const success = await getUploadParams(file);
    if (!success) {
      return false;
    }

    return true;
  };

  // 自定义上传操作
  const customRequest: UploadProps["customRequest"] = (options) => {
    const { file, onSuccess, onError } = options;
    const formData = new FormData();
    const uploadData = uploadDataRef.current;

    // 构建表单数据[6](@ref)
    formData.append("key", uploadData.dir + "${filename}");
    formData.append("policy", uploadData.policy);
    formData.append("OSSAccessKeyId", uploadData.accessKeyId);
    formData.append("signature", uploadData.signature);
    formData.append("success_action_status", "200");
    formData.append("file", file);

    fetch(uploadData.host, {
      method: "POST",
      body: formData,
    })
      .then((response) => {
        if (response.ok) {
          const fileUrl = `${uploadData.host}/${uploadData.dir}${(file as File).name}`;
          onSuccess?.(fileUrl);
          console.log("imageUrl", fileUrl);
          message.success("上传成功");
        } else {
          throw new Error("上传失败");
        }
      })
      .catch((error) => {
        onError?.(error);
        message.error("上传失败");
      })
      .finally(() => {});
  };

  // 打开预览界面
  const handlePreview = async (file: UploadFile) => {
    if (!file.url && !file.preview) {
      file.preview = await getBase64(file.originFileObj as FileType);
    }
    setPreviewImage(file.url || (file.preview as string));
    setPreviewOpen(true);
  };

  // 上传按钮
  const uploadButton = (
    <button style={{ border: 0, background: "none" }} type="button">
      <PlusOutlined />
      <div style={{ marginTop: 8 }}>上传图片</div>
    </button>
  );

  useEffect(() => {
    if (value) {
      let list = value.map((url) => ({
        uid: url,
        name: "image.png",
        status: "done",
        url: url,
      }));
      setFileList(list as UploadFile[]);
    }
  }, [value]);

  const handleChange: UploadProps["onChange"] = ({ fileList: newFileList }) => {
    setFileList(newFileList);
    if (newFileList.filter((f) => f.status == "uploading").length == 0) {
      let list = newFileList
        .filter((f) => f.status == "done")
        .map((f) => f.url || f.response);
      console.log("---上传", list);
      onChange?.(list);
    }
  };

  return (
    <>
      <Upload
        listType="picture-card"
        fileList={fileList}
        customRequest={customRequest}
        beforeUpload={beforeUpload}
        onPreview={handlePreview}
        onChange={handleChange}
      >
        {fileList.length >= 10 ? null : uploadButton}
      </Upload>
      {previewImage && (
        <Image
          styles={{ root: { display: "none" } }}
          preview={{
            open: previewOpen,
            onOpenChange: (visible) => setPreviewOpen(visible),
            afterOpenChange: (visible) => !visible && setPreviewImage(""),
          }}
          src={previewImage}
        />
      )}
    </>
  );
};

export default OssUploadList;
