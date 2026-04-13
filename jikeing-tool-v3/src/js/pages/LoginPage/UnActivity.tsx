import { Button, message, Spin } from "antd"
import useUserStore from "../../store/useUserStore"

const UnActivity = () => {

    const uuid = useUserStore(state => state.uuid)
    const logout = useUserStore.getState().logout
    const loading = useUserStore.getState().loading

    const copyUserId2Clipboard = () => {
        const textarea = document.createElement('textarea');
        textarea.value = useUserStore.getState().uuid;
        // 防止页面滚动
        textarea.style.position = 'fixed';
        document.body.appendChild(textarea);
        textarea.select();

        try {
            const successful = document.execCommand('copy');
            console.log(successful ? '复制成功' : '复制失败');
        } catch (err) {
            console.error('备用复制方法失败:', err);
        }

        document.body.removeChild(textarea);
        message.success('uid已复制')
    }

    const relogin = () => {
        logout()
    }

    return <>
        {
            loading && <Spin size='large'>
                <div className="w-[100vw] h-[100vh]">asdfasd</div>
            </Spin>
        }
        {
            loading == false && <div className="m-[20px]">
                <p className="text-[white]">当前账号未激活，请将您的UID提供给对应的工作人员，我们将立即为您激活</p>
                <div className="flex justify-center items-center mb-[10px] space-x-[10px]">
                    <p className="text-[white]">UID:{uuid}</p>
                    <Button type="primary" onClick={copyUserId2Clipboard}>复制</Button>
                </div>
                <Button type="primary" onClick={relogin}>重新登录</Button>
            </div >
        }

    </>
}

export default UnActivity