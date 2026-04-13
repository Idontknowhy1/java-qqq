<template>
    <div class="bat-user-vip-page">
        <div class="user-id-list-wrapper">
            <el-form :inline="true" label-position="top">
                <el-form-item label="用户ID列表">
                    <el-input v-model="userIdList" :rows="20" type="textarea" placehold="请输入用户ID，不同ID之间使用换行分隔"
                        clearable />
                </el-form-item>
            </el-form>
            <div class="bottom-buttons">
                <el-button class="buttons" :loading="loading" type="primary" @click="executeAddVip">开始添加</el-button>
            </div>
        </div>

        <el-form label-position="top" style="width: 250px; margin-right: 16px;">
            <!-- <el-form-item label="添加素材网会员">
                <el-switch v-model="materialVipInfo.setted" />
            </el-form-item>
            <el-form-item label="素材网永久会员" v-if="materialVipInfo.setted">
                <el-switch v-model="materialVipInfo.lifeLong" />
            </el-form-item>
            <el-form-item label="素材网到期时间" v-if="materialVipInfo.setted && materialVipInfo.lifeLong == false">
                <el-date-picker v-model="materialVipInfo.expireAt" type="date" placeholder="" />
            </el-form-item> -->
            <el-form-item label="会员等级">
                <el-select v-model="newVipTag" placeholder="请选择会员级别" style="width: 240px">
                    <el-option v-for="item in vipLevels" :key="item.tag" :label="item.label" :value="item.tag" />
                </el-select>
                <div>
                    <div v-for="item in vipLevelDescriptions[newVipTag]" :key="item">{{ item }}</div>
                </div>
            </el-form-item>
        </el-form>

        <!-- <el-form label-position="top" style="width: 200px">
            <el-form-item label="添加插件会员">
                <el-switch v-model="pluginVipInfo.setted" />
            </el-form-item>
            <el-form-item label="插件永久会员" v-if="pluginVipInfo.setted">
                <el-switch v-model="pluginVipInfo.lifeLong" />
            </el-form-item>
            <el-form-item label="插件到期时间" v-if="pluginVipInfo.setted && pluginVipInfo.lifeLong == false">
                <el-date-picker v-model="pluginVipInfo.expireAt" type="date" placeholder="" />
            </el-form-item>
        </el-form> -->

        <div class="log-wrapper">
            <span class="log-item" v-for="item in logs" :key="item">{{ item }}</span>
        </div>
    </div>
</template>

<script setup lang="ts">

import { getUserInfoByUUid, updateMemberInfo } from '@/api/manager/user.js'
import { el } from 'date-fns/locale';
import { ElMessage } from 'element-plus';
import { ref } from 'vue';

const logs = ref<string[]>([])

const loading = ref(false)

const vipLevels = [
    { label: ' LV00', tag: 'VIP_LV00' },
    { label: ' LV01', tag: 'VIP_LV01' },
    { label: ' LV02', tag: 'VIP_LV02' },
    { label: ' LV03', tag: 'VIP_LV03' }
]

const vipLevelDescriptions = {
    'VIP_LV00': [
        "删除会员"
    ],
    'VIP_LV01': [
        "简笔画素材（一年）",
        "简笔画录播",
        "小说授权",
    ],
    'VIP_LV02': [
        "全部素材网使用权益（一年）",
        "AE插件（一年）",
        "每月赠送300积分",
        "小说授权: 无",
        "简笔画录播"
    ],
    'VIP_LV03': [
       "全部素材网使用权益（一年）",
        "AE插件（一年）",
        "每月赠送900积分",
        "小说授权: 无",
        "简笔画录播",
        "365天直播课",
        "商单渠道保证",
        "讲师晋升通道",
        "一年陪跑（远程答疑）"
    ]
}

// 用户UUID和ID映射表
const userIdMap = ref([])
// vip添加成功的数量
const vipSuccess = ref([])

// 用户ID列表
const userIdList = ref('')
// 是否设置素材网会员
const newVipTag = ref('VIP_LV03')

const executeAddVip = async () => {
    logs.value = []
    userIdMap.value = []

    if (userIdList.value.length == 0) {
        ElMessage.warning({
            message: '请输入用户ID'
        })
        return
    }

    loading.value = true

    // 检查用户ID是否有效
    logs.value.push('正在校验用户ID')
    await checkUserIds()

    logs.value.push(' ')
    logs.value.push('正在添加会员')
    await addVip()

    logs.value.push(' ')
    logs.value.push('批量添加会员完成')
    ElMessage.success('批量添加会员完成')

    loading.value = false
}


// 添加会员
const addVip = async () => {
    vipSuccess.value = []
    for (let user of userIdMap.value) {
        await _addVip(user)
    }
    logs.value.push('  ' + vipSuccess.value.length + ' 个用户添加会员成功')
}

// 添加会员
const _addVip = async (user) => {
    let res = await updateMemberInfo({
        "toUserId": user.id,
        "vipTag": newVipTag.value,
        "vipName": vipLevels.filter(v => v.tag == newVipTag.value)[0].label ?? ''
    })
 
    if (res.code == 10000) {
        vipSuccess.value.push(user.uuid)
    } else {
        logs.value.push(' ' + user.uuid + ' 添加会员失败')
    }
}

// 检查用户ID是否有效
const checkUserIds = async () => {
    let uuids = userIdList.value.split('\n')
    logs.value.push('共输入 ' + uuids.length + ' 个用户ID')
    for (let uuid of uuids) {
        await _checkUserId(uuid)
    }
    console.log(userIdMap.value)
    logs.value.push('  其中 ' + userIdMap.value.length + ' 个有效用户ID')
}

// 查询用户信息
const _checkUserId = async (uuid: string) => {
    const res = await getUserInfoByUUid(uuid)

    if (res.code == 10000) {
        if (res.data) {
            userIdMap.value.push({
                id: res.data.id,
                uuid: uuid
            })
        } else {
            logs.value.push('  ' + uuid + ' 无效')
        }
    } else {
        ElMessage.error({
            message: "查询用户信息失败"
        })
        return
    }
}

// ((async function () {
//     for (let item in [1, 2, 3, 4, 5]) {
//         await new Promise((resolve) => {
//             setTimeout(() => {
//                 console.log('--- item =' + item)
//                 resolve(true)
//             }, 1000);
//         })
//     }
// })())

</script>


<style lang="scss" scoped>
.bat-user-vip-page {
    display: flex;

    .user-id-list-wrapper {
        display: flex;
        flex-direction: column;

        .bottom-buttons {
            display: flex;
            flex-direction: column;
            align-items: flex-start;
            width: 100%;
        }
    }

    .log-wrapper {
        width: 300px;
        display: flex;
        flex-direction: column;
        font-size: 12px;
        font-weight: 400;

        .log-item {
            line-height: 20px;
        }
    }
}
</style>