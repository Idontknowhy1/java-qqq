<template>
    <div class="main-container">
        <HeaderNav />
        <div class="account_bind">
            <div class="name">
                <div class="title">填写会员信息</div>
                <div class="remark">请仔细填写你的社交媒体账号，绑定你的素材使用权（手机号必填，账号信息至少填写一个）</div>
            </div>

            <div class="content_box">
                <div class="form_box">
                    <div class="form_item">
                        <div class="label">绑定手机号码</div>
                        <div class="input">
                            <el-input v-model="form.mobile" placeholder="请输入您的手机号码"></el-input>
                        </div>
                    </div>
                    <div class="form_item">
                        <div class="label">抖音账号信息</div>
                        <div class="input">
                            <el-input v-model="form.douyin_link" placeholder="请输入抖音链接"
                                style="margin: 0 22px 0 0"></el-input>
                            <el-input v-model="form.douyin_account" placeholder="抖音账号"></el-input>
                        </div>
                    </div>
                    <div class="form_item">
                        <div class="label">快手账号信息</div>
                        <div class="input">
                            <el-input v-model="form.kuaishou_link" placeholder="请输入快手账号链接"
                                style="margin: 0 22px 0 0"></el-input>
                            <el-input v-model="form.kuaishou_account" placeholder="快手账号"></el-input>
                        </div>
                    </div>
                    <div class="form_item">
                        <div class="label">B站账号信息</div>
                        <div class="input">
                            <el-input v-model="form.bilibili_link" placeholder="请输入B站账号链接"
                                style="margin: 0 22px 0 0"></el-input>
                            <el-input v-model="form.bilibili_account" placeholder="B站 UID"></el-input>
                        </div>
                    </div>
                </div>

                <div class="btn">
                    <div class="submit_btn" @click="submitFn">提交信息</div>
                </div>

                <div class="work_time">
                    <div class="contact_desc">如有疑问请联系管理员</div>
                    <div class="time">工作时间：周一至周五 9:00-18:00</div>
                </div>

                <div class="desc_remark">
                    <div class="text_1">请务必仔细填写自己运营的媒体账号信息，付费后网站素材只授权于已绑定的媒体账号，单个平台只可授权一个账号
                        （如需更改账号信息，请联系客服更改，否则后期账号涉及版权法律责任后果自负。</div>
                    <div class="text_2">注意：购买素材网会员只拥有素材使用权，并不代表拥有版权，所有素材版权归即课素材网拥有,禁止二次转卖行为，严
                        禁以任何形式转卖素材及素材网账号，一经发现一律停用并追究个人法律责任</div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import HeaderNav from './components/HeaderNav.vue';
import { studentInfo } from "@/api/home/common";
import { useUserStore } from '@/store/useUserStore.ts'
import { ElMessage } from 'element-plus';
import { ref } from "vue";

const userStore = useUserStore()

const form = ref({
    mobile: '',
    douyin_link: '',
    douyin_account: '',
    kuaishou_link: '',
    kuaishou_account: '',
    bilibili_link: '',
    bilibili_account: '',
})

const submitFn = () => {
    if (userStore.loginStatus == 1) {
        if (form.value.mobile == '') {
            ElMessage({
                message: '手机号为必填',
                type: 'warning',
            })
            return false
        }
        if (form.value.douyin_link == '' && form.value.kuaishou_link == ''
            && form.value.bilibili_link == '') {
            ElMessage({
                message: '抖音，快手，Bilibili 请至少填写一项',
                type: 'warning',
            })
            return false
        }
        studentInfo(form.value).then(res => {
            if (res.code == 200) {
                ElMessage({
                message: '提交成功',
                type: 'success',
            })
            }
        })
    } else {
        userStore.setDialogLoginStatus(true)
    }

}
</script>

<style scoped lang="scss">
// :root {
//     --input-border-default: #E1E1E1;
//     --input-border-hover: #C0C4CC;
//     --input-border-focus: #409EFF;
//     --input-border-disabled: #E4E7ED;
// }

// /* 应用变量 */
// :deep(.el-input__wrapper) {
//     box-shadow: 0 0 0 1px var(--input-border-default) inset;
// }

// :deep(.el-input__wrapper:hover) {
//     box-shadow: 0 0 0 1px var(--input-border-hover) inset;
// }

// :deep(.el-input__wrapper.is-focus) {
//     box-shadow: 0 0 0 1px var(--input-border-focus) inset;
// }

// :deep(.el-input.is-disabled .el-input__wrapper) {
//     box-shadow: 0 0 0 1px var(--input-border-disabled) inset;
// }

:deep(.el-input) {
    width: 372px;
    height: 46px;
}

:deep(.el-input__wrapper) {
    background: none;
    border: 1px solid #2A2B3E;
}

:deep(.el-input__inner) {
    background: none;
    border: none;
}

::v-deep .el-input__wrapper::placeholder {
    color: #9CA3AF !important;
}

input::-webkit-input-placeholder {
    color: #000;
    /* WebKit浏览器 */
}

input::-moz-placeholder {
    color: #9CA3AF;
    /* Mozilla Firefox */
}

input:-ms-input-placeholder {
    color: #9CA3AF;
    /* Internet Explorer */
}



.account_bind {
    border: 0px solid #ff0000;
    width: 100%;
    height: 100%;
    overflow-y: hidden;


    .name {
        width: 94%;
        background: #111828;
        color: #fff;
        border: 1px solid #111828;
        margin: 0 auto;

        .title {
            font-size: 22px;
            font-weight: bold;
            margin: 40px 0 0 0px;
        }

        .remark {
            font-size: 14px;
            color: #9CA3AF;
            margin: 20px 0 0px 0px;
            padding: 0 0 40px 0;
        }
    }

    .content_box {
        width: 94%;
        border: 0px solid #fff;
        background: #111226;
        border-radius: 20px;
        margin: 0 auto 50px;
        padding: 0 0 10px;

        .form_box {
            width: 768px;
            margin: 0px auto 0;
            padding: 20px 0 0 0;
            border: 0px solid #fff;

            .form_item {
                margin: 30px 0 0 0;

                .label {
                    color: #D1D5DB;
                    margin: 10px 0 10px 0;
                    font-size: 14px;
                }

                .input {
                    display: flex;
                }
            }

        }

        .btn {
            width: 768px;
            border: 0px solid #ff0000;
            margin: 50px auto 0;

            .submit_btn {
                cursor: pointer;
                width: 128.02px;
                line-height: 48px;
                border-radius: 4px;
                text-align: center;
                color: #fff;
                background: linear-gradient(0deg, rgba(0, 0, 0, 0), rgba(0, 0, 0, 0)), linear-gradient(90deg, #2563EB 0%, #DB2777 100%);
                z-index: 0;
                margin: 0;
            }
        }

        .work_time {
            width: 96%;
            border-top: 1px solid #2A2B3E;
            line-height: 34px;
            margin: 50px auto 0;
            color: #9CA3AF;
            font-size: 16px;
            padding: 30px 0 0 0;

            .contact_desc {
                text-align: center;
            }

            .time {
                text-align: center;
            }
        }

        .desc_remark {
            width: 768px;
            margin: 30px auto;
            font-size: 14px;

            .text_1 {
                width: 94%;
                text-align: left;
                color: #9CA3AF;
                margin: 20px auto 0;
                line-height: 24px;
            }

            .text_2 {
                width: 94%;
                text-align: left;
                color: #EF4444;
                margin: 20px auto 0;
                line-height: 24px;
                opacity: 0.8;
            }
        }
    }

}
</style>