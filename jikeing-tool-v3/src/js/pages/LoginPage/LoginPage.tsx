import { Button, Image } from "antd";
import { sendGet } from "../../api/api";
import { useEffect, useState } from "react";
import packageConfig from '../../../../package.json'
import jikeLogoImage from '@/js/assets/images/jike-logo-image.png'
import useUserStore from "../../store/useUserStore";

const LoginPage = () => {

    const MAX_REQUEST_LOGIN_STATUS_COUNT = 30;
    const [qrCodeUrl, setQrCodeUrl] = useState('')
    const [resquestCodeCount, setResquestCodeCount] = useState(0)

    const userStore = useUserStore.getState()

    /**
     * 获取登录二维码
     */
    const requsetLoginQRCode = () => {
        sendGet('/v1/user/get-scene-qrcode').then(response => {
            console.log(response)
            if (response.code === 200) {
                let data = response.data
                setQrCodeUrl(data.qrcode_image)
                setResquestCodeCount(0)
                requestLoginStatus(data.scene_id);
            } else {
                alert("获取登录码失败,请检查您的网络")
            }
        }).catch(error => {
            alert("获取登录码失败,请检查您的网络")
        })
    }

    /**
     * 获取登录状态
     */
    const requestLoginStatus = (sceneId: string) => {
        sendGet(`/v1/user/query-scene-status?scene_id=${sceneId}`).then(response => {
            if (response.code === 200) {
                const data = response.data;
                //-1为过期，0为未登录 1登录成功
                const status = data.status;
                console.log(`登录状态:${status}`)
                if (status == 1) {
                    userStore.loginSuccess(data)
                } else {
                    if (useUserStore.getState().isLogin == false) {
                        setTimeout(() => {
                            setResquestCodeCount(state => state + 1)
                            if (resquestCodeCount < MAX_REQUEST_LOGIN_STATUS_COUNT) {
                                requestLoginStatus(sceneId);
                            }
                        }, 2000);
                    }
                }
            } else {
                console.log(`获取登录状态错误:${error}`)
            }

        }).catch(error => {
            console.log(`获取登录状态错误:${error}`)
        })
    }

    useEffect(() => {
        let token = localStorage.getItem('token') || ''
        if (token.length > 0) {
            userStore.requestUserInfo(token)
        } else {
            // 获取微信二维码
            requsetLoginQRCode()
        }
    }, [])

    return <>
        <div className="flex flex-col items-center">
            <Image src={jikeLogoImage} preview={false} className="w-[148px] h-[60px] mt-[40px] mb-[60px]" />
            <div className="relative w-[240px] h-[240px] text-[white]">
                {/* 二维码 */}
                {qrCodeUrl.length > 0 && <Image src={qrCodeUrl} preview={false} />}

                {/* 刷新登录二维码 */}
                {
                    resquestCodeCount > MAX_REQUEST_LOGIN_STATUS_COUNT && <div className="absolute top-[0] left-[0] w-full h-full flex items-center justify-center"
                        style={{ backgroundColor: 'rgba(0, 0, 0, 0.8)', }}
                    >
                        <Button type="primary" onClick={requsetLoginQRCode}>刷新登录二维码</Button>
                    </div>
                }

            </div>
            <div className="mt-[16px] text-[white]">使用微信扫一扫登录</div>
            <div className="mt-[16px] text-[#FFFFFF]">
                v{packageConfig.version}
            </div>

        </div >
    </>
}

export default LoginPage