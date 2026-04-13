<template>
    <div class="main-container_login">
        <div class="header_login">
            <div class="logo"><el-image :src="logo"></el-image></div>
            <div class="back" style="color: white; cursor: pointer;" @click="jumpToHome">返回首页</div>
        </div>
        <div class="box">
            <div class="login-image">
                <el-image :src="logo_image"></el-image>
            </div>
            <div class="login-content">
                <div class="name">
                    <div class="title">资源平台<!--<img src="@/assets/images/blank.png" class="blank-dot" />--></div>
                    <div class="desc"></div>
                </div>
                <div class="login-form">
                    <el-form size="large" :model="form" :rules="formRules" ref="ruleFormRef" label-width="0px"
                        class="demo-ruleForm">
                        <el-form-item label="" prop="username" required>
                            <el-input placeholder="请输入用户名" v-model="form.username" class="input-with">
                                <!-- <template #prepend>
                                    <el-select v-model="form.prefix" placeholder="请选择"
                                        style="width:110px;">
                                        <el-option label="中国+86" :key="1" :value="1"></el-option>
                                    </el-select>
                                </template> -->
                            </el-input>
                        </el-form-item>
                        <el-form-item label="" prop="password" required>
                            <el-input placeholder="请输入密码" v-model="form.password" class="input-with"></el-input>
                            <!-- <p class="remark" v-if="action_type == 'reg'">必须包含数字、大小写字母、特殊字符等8位字符</p> -->
                        </el-form-item>
                        <el-form-item label="" prop="captcha_answer" required>
                            <el-input placeholder="请输入验证码" v-model="form.captcha_answer" class="input-with">
                                <template #append>
                                    <img :src="digital_code" class="digital_code" @click="digital" />
                                </template>
                            </el-input>
                        </el-form-item>

                        <el-form-item label="">
                            <div class="submit-btn" @click="handleLogin">{{ login_text }}
                            </div>
                            <!-- <div class="submit-btn" @click="handleReg" v-if="action_type == 'reg'">注 册</div> -->
                        </el-form-item>
                    </el-form>
                </div>
                <div class="contact-info">
                    <div class="left">
                        <div class="first">客服微信</div>
                        <div class="sec">yunzhijiu001 / 18868770401</div>
                        <div class="three">请扫码二维码添加客服微信咨询</div>
                    </div>
                    <div class="right">
                        <el-image :src="kefu_qrcode"></el-image>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { digital, login } from '@/api/manager/info'
import { onMounted, ref } from 'vue';
import { useUserStore } from '../../store/useUserStore';
import { useRouter } from 'vue-router';
import logo from '@/assets/images/logo.png'
import logo_image from '@/assets/images/login.png'
import kefu_qrcode from '@/assets/images/kefu-qrcode.png'
import type { FormInstance, FormRules } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()

const ruleFormRef = ref<FormInstance>()
const digital_code = ref('')
const loading = ref(false)
const login_text = ref('登 录')
const success_url = '/manage/setting/account'

const form = ref({
    prefix: 1,
    username: '',
    password: '',
    captcha_id: '',
    captcha_answer: '',
    //type: 1,
    //fid:  localStorage.getItem('fid')
})
const formRules = {
    username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
    captcha_answer: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const queryDigital = () => {
    digital().then(res => {
        if (res.code == 200) {
            digital_code.value = res.data.pic_path
            form.value.captcha_id = res.data.captcha_id
        }
    })
}

const jumpToHome = () => {
    router.push({ path: '/', query: {} })
}

const callFn = (data) => {
    userStore.setLoginStatus(true)
    router.push({ path: data.path, query: {} })
}

const submitForm = async (formEl: FormInstance | undefined) => {
    if (!formEl) return
    await formEl.validate((valid, fields) => {
        if (valid) {
            console.log('submit!')
        } else {
            console.log('error submit!', fields)
        }
    })
}

const handleLogin = async () => {
    loading.value = true
    await ruleFormRef.value.validate((valid, fields) => {
        loading.value = false
        if (valid) {
            login_text.value = '登录中..'
            userStore.frontLogin(form.value).then(res => {
                login_text.value = '登 录'
                queryDigital()
                if (res.code == 200) {
                    callFn({ path: success_url })
                }
            }).catch(() => {
            })

            setTimeout(function () {
                login_text.value = '登 录'
            }, 3000)
        }
    })
}

onMounted(() => {
    queryDigital()
})

</script>
<style scoped lang="scss">
::v-deep .el-icon-arrow-left {
    font-weight: bold;
    color: white;
}

::v-deep .input-with {
    width: 340px;
}

.remark {
    width: 100%;
    line-height: 24px;
    color: #999;
    font-size: 13px;
    text-align: left;
}

.main-container_login {
    width: 100% !important;
    height: 100% !important;
    margin: 0 !important;
    text-align: center;
    background-image: url('../../assets/images/login-backgroud.png');
    background-repeat: no-repeat;
    background-size: 100% 100%;
    position: fixed !important;
    font-size: 14px;

    .header_login {
        width: 74%;
        height: 50px;
        border: 0px solid #ccc;
        margin: 60px auto 0;
        display: flex;
        justify-content: space-between;
        font-size: 14px;
        color: #000000;

        .logo {
            width: 40px;
            height: 40px;
        }

        .back {
            margin: 4px 5px 0 0;

            .router-link-active {
                color: #fff;
            }
        }
    }

    .box {
        width: 74%;
        height: 550px;
        border: 0px solid #ccc;
        border-radius: 10px;
        position: relative;
        margin: 0px auto;
        background: #fff;
        display: flex;

        .login-image {
            width: 60%;
            height: 100%;
            margin: 0;

            .el-image {
                width: 100%;
                height: 100%;
                border-radius: 5px;
            }
        }

        .login-content {
            width: 38%;
            height: 100%;

            .action_type {
                line-height: 60px;
                display: flex;
                border: 0px solid #ccc;
                text-align: center;
                align-items: center;
                justify-content: center;
                margin-top: 30px;
            }

            .name {
                width: 340px;
                border: 0px solid #ccc;
                margin: 0 auto;

                .title {
                    margin: 40px 0 0 0px;
                    font-size: 22px;
                    color: #000000;
                    font-weight: bold;
                    text-align: center;

                    .blank-dot {
                        width: 6px;
                        height: 6px;
                        margin: 0 6px 0 6px;
                    }
                }

                .desc {
                    text-align: left;
                    margin: 10px 0 0 0px;
                    font-size: 14px;
                    color: #999999;
                }
            }

            .login-form {
                width: 340px;
                border: 0px solid #ccc;
                margin: 20px auto 0;

                .submit-btn {
                    width: 340px;
                    height: 40px;
                    border: 0px solid #ccc;
                    background: #00907F;
                    border-radius: 5px;
                    color: #fff;
                    cursor: pointer;
                }

                .sendSms {
                    cursor: pointer;
                }

                .digital_code {
                    width: 80px;
                    border: 0px solid #ff0000;
                }
            }

            .contact-info {
                width: 340px;
                border: 0px solid #ccc;
                margin: 0 auto;
                display: flex;

                .left {
                    width: 60%;
                    margin-top: 10px;

                    div {
                        line-height: 30px;
                        text-align: left;
                    }

                    .first {
                        font-size: 16px;
                    }

                    .sec {
                        font-size: 14px;
                        color: #00907F;
                    }

                    .three {
                        font-size: 14px;
                        color: #999999;
                    }
                }

                .right {
                    width: 40%;

                    .el-image {
                        width: 100px;
                        height: 100px;
                    }
                }
            }
        }
    }
}
</style>