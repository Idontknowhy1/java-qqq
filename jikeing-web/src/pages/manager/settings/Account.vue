<template>
    <div class="user-main-container">
        <div class="content">
            <div class="account_info">
                <div class="info-box top-margin">
                    <p class="label">用户名</p>
                    <p class="value">{{ userStore.username }}
                    </p>
                </div>
                <div class="info-box">
                    <p class="label">头像</p>
                    <p class="value avatar">
                        <el-avatar :size="40" :src="default_avatar" v-if="userStore.avatar == ''"></el-avatar>
                        <el-avatar :size="40" :src="userStore.avatar" v-else></el-avatar>
                    </p>
                </div>

                <!-- <div class="info-box">
                    <p class="label">昵称</p>
                    <p class="value" v-if="!edit_status.nickname">
                        <span class="value-text">{{ form.nickname }}</span>
                        <span class="setting" @click="edit('nickname')">修改</span>
                    </p>
                    <p class="value" v-else>
                        <el-input size="small" v-model="form.nickname" style="width: 200px;"></el-input>&nbsp;
                        <span class="init_set" @click="handleSave">保存</span>&nbsp;&nbsp;
                        <span class="init_set" @click="edit_status.nickname = false">取消</span>
                    </p>
                </div>

                <div class="info-box">
                    <p class="label">百度网盘</p>
                    <p class="value">
                        <span v-if="form.baidu_bind == true">已授权</span>
                        <span v-else><a :href="baidupanAuthUrl" target="_self">去授权</a></span>
                    </p>
                </div> -->
            </div>
        </div>
    </div>

</template>

<script lang="ts" setup>

import { baidupanAuthUrl } from '@/api/manager/base'
import { onMounted, reactive, ref } from 'vue'
import default_avatar from '@/assets/images/default-avatar.jpg'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../../store/useUserStore'
// import ImagesPick from "@/components/common/ImagesPick.vue"

const userStore = useUserStore()

const form = ref({
    username: '',
    avatar: '',
    nickname: '',
    name: '',
    text_message_status: false,
    text_message_amount: '',
    remark: '',
    level: '',
    baidu_bind: false,
    role: ''
})

const edit_status = ref({
    nickname: false,
    name: false,
    remark: false,
    avatar: false
})

const baidupanAuthUrl = ref('')

// 方法定义
const authMsg = () => {
    // 模拟路由查询参数
    const query = { baidu_state: 1 }
    if (typeof query.baidu_state != 'undefined') {
        if (query.baidu_state == 1) {
            ElMessage.success('授权成功')
        } else {
            ElMessage.error('授权失败')
        }
    }
}

// const handleAvatarChange = (file) => {
//     // 模拟文件处理
//     const reader = new FileReader()
//     reader.onload = (e) => {
//         previewAvatar.value = e.target.result
//     }
//     reader.readAsDataURL(file.raw)
// }

// const saveAvatar = () => {
//     if (previewAvatar.value) {
//         form.avatar = previewAvatar.value
//         edit_status.avatar = true
//         handleSave()
//         dialog.uploadImageStatus = false
//         ElMessage.success('头像更新成功')
//     } else {
//         ElMessage.warning('请选择新头像')
//     }
// }

const getBaidupanAuthUrl = async () => {
    try {
        const res = await baidupanAuthUrl()
        if (res.code == 200) {
            baidupanAuthUrl.value = res.data.url
        }
    } catch (error) {
        ElMessage.error('获取授权链接失败')
    }
}

const edit = (key) => {
    if (key === 'nickname') {
        edit_status.value.nickname = true
    } else if (key === 'avatar') {
        // edit_status.avatar = true
        // dialog.uploadImageStatus = true
        // previewAvatar.value = ''
    } else if (key === 'name') {
        edit_status.value.name = true
    } else if (key === 'username') {
        edit_status.value.username = true
    }
}

const handleSave = (type) => {
    const param = {}

    // 收集所有需要保存的字段
    for (const key in edit_status) {
        if (edit_status[key]) {
            switch (key) {
                case 'remark':
                    param.remark = form.remark
                    break
                case 'nickname':
                    if (form.nickname.length > 12) {
                        ElMessage.error('昵称不能超过12个字符')
                        return
                    }
                    param.nickname = form.nickname
                    break
                case 'avatar':
                    param.avatar = form.avatar
                    break
                case 'name':
                    if (form.name.length > 20) {
                        ElMessage.error('商户名称不能超过20个字符')
                        return
                    }
                    param.name = form.name
                    break
                case 'username':
                    param.username = form.username
                    break
            }
            edit_status[key] = false
        }
    }

    // 如果有特定字段需要保存
    if (type && form[type]) {
        param[type] = form[type]
    }

    // 如果有需要保存的数据
    if (Object.keys(param).length > 0) {
        // 模拟保存操作
        console.log('保存数据:', param)
        ElMessage.success('保存成功')
    }
}

const bindBaidupan = () => {
    if (baidupanAuthUrl.value) {
        window.open(baidupanAuthUrl.value, '_blank')
    } else {
        ElMessage.error('获取授权链接失败')
    }
}

// 生命周期钩子
onMounted(() => {
    userStore.getUserInfo()
    // authMsg()
})

</script>
<style scoped lang="scss">
::v-deep .el-textarea__inner {
    background-color: #f6f7f8 !important;
    border: 0px solid #fff;
    height: 100px;
}

/*::v-deep .el-button {
  height: 40px;
  margin-top: 10px;
}*/
::v-deep .el-avatar {
    margin-top: 7px;
}

::v-deep .el-radio-group {
    line-height: 55px;
    margin-top: 5px;
}

::v-deep .el-tag {
    margin: 12px 3px 2px 0;
}

.content {
    //width: 98%;
    height: 100%;
    margin: 0 auto;
    border: 0px solid #ccc;

    .account_info {
        width: 40%;
        min-height: 320px;
        border: 0px solid #ff0000;
        margin: 0px 0 0 0px;
        padding-bottom: 10px;
        background: #fff;
        border-radius: 10px;

        .info-box {
            position: relative;
            display: flex;
            flex-wrap: wrap;
            width: 100%;
            //min-height: 55px;
            line-height: 50px;
            border: 0px solid #ccc;
            margin: 0px auto;

            .label {
                width: 20%;
                margin-left: 40px;
                font-size: 16px;
                font-weight: 500;
                color: #c8c8d0;
            }

            .value {
                max-width: 70%;
                font-size: 14px;
                font-weight: 600;
                color: #40404c;
                line-height: 50px;
                // padding-top: 18px;
                border: 0px solid #ff0000;
                display: flex;
                flex-wrap: wrap;

                .value-text {
                    margin-right: 10px;
                }

                .setting {
                    border: 0px solid #ff0000;
                    //margin-left: 50px;
                    color: #00907F;
                    display: inline-block;
                    //vertical-align: middle;
                    cursor: pointer;
                }

                ::v-deep .el-button--mini {
                    height: 28px !important;
                    margin-top: 12px !important;
                }

                .init_set {
                    border: 0px solid #ff0000;
                    color: #00907F;
                    display: inline-block;
                    //vertical-align: middle;
                    cursor: pointer;
                }
            }

            .avatar {

                //padding-top: 3px !important;
                .setting {
                    //margin-top: 16px !important;
                }
            }

            .desc {
                padding-top: 0px !important;

                .desc_text {
                    margin: 10px 0 0 0 !important;
                    width: 350px !important;
                    height: 100px !important;
                }
            }

            .edit_status {
                border: 0px solid #ccc;
                margin-left: 155px;
                height: 20px;
                line-height: 20px;
                font-size: 14px;
                color: #409EFF;
            }
        }

        .top-margin {
            padding-top: 20px !important;
        }

        /*    div:first-child {
          margin-top: 40px;
        }
        div:last-child {
          margin-bottom: 40px;
        }*/
    }
}
</style>