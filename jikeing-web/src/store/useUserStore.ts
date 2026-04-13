import { defineStore } from 'pinia'
import { login, register, getUserInfo } from '@/api/manager/info'
import { getUserScoreBalance } from '@/api/home/score'

export const useUserStore = defineStore('user', {
    state: () => ({
        addRoutes: [],
        menu: localStorage.getItem("menu") != '' ? JSON.parse(localStorage.getItem("menu")) : '',
        sec_menu: localStorage.getItem("sec_menu") != '' ? JSON.parse(localStorage.getItem("sec_menu")) : [],
        actions: [],
        user_id: localStorage.getItem("user_id"),
        nickname: localStorage.getItem("nickname"),
        username: localStorage.getItem("username"),
        avatar: localStorage.getItem("avatar"),
        role: localStorage.getItem('role') ? localStorage.getItem('role') : '',
        site_title: '',
        show_notice: false,
        loginStatus: 0,
        is_plugin_member: localStorage.getItem("is_plugin_member") == "1" ? 1 : 0,
        is_material_member: localStorage.getItem("is_material_member") == "1" ? 1 : 0,
        is_ai_gen_member: localStorage.getItem("is_ai_gen_member") == "1" ? 1 : 0,
        is_image_split_member: localStorage.getItem("is_image_split_member") == "1" ? 1 : 0,
        dialogLoginStatus: false,
        vip_level: 1,
        vipScore: 0,
        forScore: 0,
        todayResigned: false
    }),
    actions: {
        setLoginStatus(status) {
            this.loginStatus = status
        },
        setUserInfo(val: any) {
            if (val) {
                this.user_id = val.id
                this.nickname = val.nickname
                this.username = val.username
                this.avatar = val.avatar
                this.role = (JSON.parse(val.roles) ?? ['user'])[0]
                this.is_plugin_member = val.pluginMember ? 1 : 0
                this.is_material_member = val.materialMember ? 1 : 0
                this.is_ai_gen_member = val.aiGenMember ? 1 : 0
                this.is_image_split_member = val.imageSplitMember ? 1 : 0
                this.vip_level = val.vipLevel
            } else {
                this.user_id = ''
                this.nickname = null
                this.username = null
                this.avatar = null
                this.role = ''
                this.is_plugin_member = 0
                this.is_material_member = 0
                this.is_ai_gen_member = 0
                this.is_image_split_member = 0
                this.vip_level = 0
            }


            localStorage.setItem("avatar", this.avatar)
            localStorage.setItem("username", this.username)
            localStorage.setItem("is_plugin_member", this.is_plugin_member)
            localStorage.setItem("is_material_member", this.is_material_member)
            localStorage.setItem("is_ai_gen_member", this.is_ai_gen_member)
            localStorage.setItem("is_image_split_member", this.is_image_split_member)

        },
        setSecMenu(secMenu: any) {
            localStorage.setItem('sec_menu', JSON.stringify(secMenu))
            this.sec_menu = secMenu
        },
        frontReg(userInfo: any) {
            return new Promise((resolve, reject) => {
                register(userInfo).then(res => {
                    resolve(res)
                }).catch(error => {
                    reject(error)
                })
            })
        },
        frontLogin(userInfo: any) {
            return new Promise((resolve, reject) => {
                login(userInfo).then(res => {
                    if (res.code == 200) {
                        this.loginStatus = 1
                        localStorage.setItem("userToken", res.data.token)
                        localStorage.setItem("expireAt", res.data.expireAt)
                    }
                    resolve(res)
                }).catch(error => {
                    reject(error)
                })
            })
        },
        getUserInfo() {
            // 同时查询用户余额
            this.getScoreBalance()
            return new Promise((resolve, reject) => {
                getUserInfo().then(res => {
                    if (res.data) {
                        localStorage.setItem("username", res.data.username)
                        localStorage.setItem('role', res.data.role)
                        this.setUserInfo(res.data)
                        this.loginStatus = 1
                    } else {
                        localStorage.clear()
                    }
                    resolve(res)
                }).catch(error => {
                    reject(error)
                })
            })
        },
        getScoreBalance() {
            return new Promise((resolve, reject) => {
                getUserScoreBalance().then(res => {
                    if (res.code === 10000) {
                        this.vipScore = res.data.vipScore
                        this.forScore = res.data.forScore
                        this.todayResigned = res.data.todayResigned
                    } else {
                    }
                    resolve(res)
                }).catch(error => {
                    reject(error)
                })
            })
        },
        logout() {
            return new Promise((resolve, _) => {
                localStorage.clear()
                this.setUserInfo(null)
                this.setLoginStatus(0)
                this.dialogLoginStatus = false
                resolve({})
            })
        },
        setDialogLoginStatus(show: boolean) {
            return new Promise((resolve, _) => {
                this.dialogLoginStatus = show
                resolve({})
            })
        }
    }
})