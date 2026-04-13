import { Result } from "antd";
import { useNavigate } from "react-router-dom";

export const NotFoundPage = () => {
  const navigate = useNavigate();

  return (
    <div className="flex items-center justify-center w-full error-container">
      <Result
        status="404"
        title="404"
        subTitle="您访问的页面不存在"
        // extra={
        //   <Button type="primary" onClick={() => navigate("/")}>
        //     返回首页
        //   </Button>
        // }
      />
    </div>
  );
};
