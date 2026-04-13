// store/counterStore.js
import { create } from 'zustand';
import { sendGet } from '../api/api';
import { devtools } from 'zustand/middleware'; // 导入devtools中间件
import zukeeper from 'zukeeper'
import { evalTS } from '../lib/utils/bolt';

let defaultAvatar = 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg'

interface IState {
    id: number
    uuid: string
    username: string
    nickname: string
    avatar: string
    token: string
    is_plugin_member: boolean
    isLogin: boolean

    localRootPath: string

    loading: boolean
}

interface IAction {
    loginSuccess: (data: {
        id: number
        token: string
    }) => void
    requestUserInfo: (token: string) => void
    logout: () => void

    getLocalRootPath: () => void
}

// 使用 create 函数创建 store，其返回值是一个 Hook
const useUserStore = create<IState & IAction>((set, get) => ({

    // 状态
    id: 0,
    uuid: '',
    username: '',
    nickname: '',
    avatar: '',
    token: '',

    is_plugin_member: false,

    isLogin: false,

    localRootPath: 'xxx',

    loading: false,

    // "id": "1975396581005688832",
    // "uuid": "f0ajn9cehtz4",
    // "username": "wx_f0ajn9cehtz4",
    // "nickname": "永平",
    // "avatar": "https://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoeOax1CZKbF6Lvp4icxZhmVib9yXLh2msprcAUAaicWFFxQlic1IHgPib4NZ6Y3ibBOe8ibVV2Mj7ibxfibUQ/132",
    // "mobile": "",
    // "roles": [
    //     "admin"
    // ],
    // "role": "admin",
    // "baidu_bind": false,
    // "create_time": "",
    // "is_plugin_member": true,
    // "plugin_member_expire_at": "2026-09-15 23:59:59",
    // "is_material_member": true,
    // "material_member_expire_at": "2026-09-15 23:59:59",
    // "vip_level": 0

    // 登录成功
    loginSuccess: (data) => {
        set((state) => ({
            id: data.id,
            isLogin: true,
            token: data.token
        }))
        localStorage.setItem('token', data.token)

        // 获取用户数据
        get().requestUserInfo(data.token)
    },

    // 获取用户信息
    requestUserInfo: (token: string) => {
        set((state) => ({
            isLogin: true,
            loading: true
        }))


        sendGet('/v1/user/info').then(response => {
            // 未登录
            if (response.code === 200) {
                set((state) => ({
                    uuid: response.data.uuid,
                    is_plugin_member: response.data.is_plugin_member,
                    avatar: response.data.avatar || defaultAvatar
                }))
            } else {
                localStorage.clear()
                alert("获取用户信息异常");
                set((state) => ({
                    isLogin: false,
                    loading: false
                }))
            }

        }).catch(error => {
            localStorage.clear()
            alert("获取用户信息异常");
            set((state) => ({
                isLogin: false,
                loading: false
            }))
        })
    },

    // 退出登录
    logout() {
        localStorage.clear()

        set((state) => ({
            id: 0,
            uuid: '',
            is_plugin_member: false,
            token: '',
            avatar: defaultAvatar,
            isLogin: false
        }))
    },

    getLocalRootPath() {
        evalTS("getResourceRootPath").then((newPath: string) => {
            set(() => ({ localRootPath: newPath }))
        });
    }

}));

export default useUserStore;