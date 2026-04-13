// src/components/OssUpload/index.tsx

import { UploadOutlined } from "@ant-design/icons";
import type { UploadProps } from "antd";
import { Button, Image, message, Upload } from "antd";
import React, { useEffect, useRef, useState } from "react";
import { sendGet } from "../api/api";

interface OssUploadProps {
  value?: string;
  onChange?: (value: string) => void;
  maxSize?: number;
}

const OssUpload: React.FC<OssUploadProps> = ({
  value,
  onChange,
  maxSize = 10,
}) => {
  const [loading, setLoading] = useState(false);
  const [imageUrl, setImageUrl] = useState<string>(value || "");
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

    setLoading(true);
    const success = await getUploadParams(file);
    if (!success) {
      setLoading(false);
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
          setImageUrl(fileUrl);
          onChange?.(fileUrl);
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
      .finally(() => {
        setLoading(false);
      });
  };

  useEffect(() => {
    if (value) {
      setImageUrl(value);
    }
  }, [value]);

  return (
    <div className="flex flex-col w-[100px]">
      {imageUrl && imageUrl !== "" && (
        <Image
          src={imageUrl}
          alt="avatar"
          style={{ width: 100, height: 100, objectFit: "cover" }}
          preview={{ mask: "点击预览" }}
        />
      )}

      {/* 或者使用默认的Upload样式 */}
      <Upload
        customRequest={customRequest}
        beforeUpload={beforeUpload}
        showUploadList={false}
      >
        <Button icon={<UploadOutlined />} loading={loading}>
          选择图片
        </Button>
      </Upload>
    </div>
  );
};

export default OssUpload;
