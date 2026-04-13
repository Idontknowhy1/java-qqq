<template>
    <el-dialog title=" " model-value="{{ true }}" class="dialog_login" width="350px" :before-close="handleClose"
        :lock-scroll="false">
        <div class="dialog_login_content">
            <div class="logo">
                <img src="@/assets/images/logo.png" />
            </div>
            <div class="login_content">
                <div class="login_tab">
                    <div class="tab_item active" :class="{ active: login_active == 'weixin' }"
                        @click="loginFn('weixin')">
                        微信登录</div>
                    <el-tooltip class="item" effect="dark" content="暂未开放登录" placement="top-start">
                        <div class="tab_item" :class="{ active: login_active == 'mobile' }" @click="loginFn('mobile')">
                            手机号登录
                        </div>
                    </el-tooltip>
                </div>

                <div class="weixin">
                    <div class="login_qr">
                        <img :src="qrcode_image" />
                    </div>
                    <div class="text">请使用微信扫一扫登录</div>
                </div>
            </div>
        </div>
    </el-dialog>
</template>

<script setup lang="ts">
import '@/assets/scss/themes/black/dialog.scss'

import { getSceneQrcode, querySceneStatus } from '@/api/home/common'
import { useUserStore } from '../../../store/useUserStore';
import { ref, reactive, onBeforeUnmount, onMounted } from 'vue'
import { useRouter } from 'vue-router'

import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()

// 响应式数据
const login_active = ref('weixin')
const form = reactive({
    login_status: false,
    prefix: '1',
    username: '',
    password: '',
    captcha_id: '',
    captcha_answer: '',
})
const formRules = ref({
    username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
    captcha_answer: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
})
const digital_code = ref('')
const success_url = ref('/user/info')
const qrcode_image = ref('')
const scene_id = ref('')
const interval = ref(null)

onMounted(() => {
    getSceneQrcode().then(res => {
        if (res.code == 200) {
            qrcode_image.value = res.data.qrcode_image
            scene_id.value = res.data.scene_id
            loopGetLoginStatus()
        }
    })
})

// 清除定时器
onBeforeUnmount(() => {
    clearInterval(interval.value)
})

// 方法定义
const loginFn = code => {
    if (code === 'mobile') {
        // ElMessage.error('暂未开放登录')
    }
}

const loopGetLoginStatus = () => {
    interval.value = setInterval(() => {
        querySceneStatus({ params: { scene_id: scene_id.value } }).then(res => {
            console.log('获取扫码结果 ',res)
            if (res.code == 200 && res.data.token) {
                localStorage.setItem('userToken', res.data.token)
                clearInterval(interval.value)
                
                userStore.setDialogLoginStatus(false)
                userStore.getUserInfo()
            }
        })
    }, 2000)
}

const handleLogin = () => {
    // 表单验证和登录逻辑需要补充
    // 此处保留原方法框架，需根据Element Plus API调整
    console.log('登录逻辑需要实现')
}

const digital = () => {
    digital().then(res => {
        if (res.code == 200) {
            digital_code.value = res.data.pic_path
            form.captcha_id = res.data.captcha_id
        }
    })
}

const handleClose = () => {
    userStore.setDialogLoginStatus(false)
}

</script>

<style scoped>
::v-deep .el-dialog__body {
    padding: 0;
}
</style>