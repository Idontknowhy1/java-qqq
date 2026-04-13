<template>
    <!-- 查询条件 -->
    <el-form label-position="right" inline label-width="60px">
        <el-form-item label="用户ID">
            <el-input v-model="uuid" />
        </el-form-item>
        <el-form-item>
            <el-button type="primary" @click="queryUserInfo">查询</el-button>
        </el-form-item>
    </el-form>

    <!-- 用户信息 -->
    <div v-if="userInfo && userInfo.id != ''">
        <el-form class="user-info-form" label-position="right" inline :model="userInfo" label-width="150px"
            style="max-width:700px">
            <el-form-item label="用户ID:" style="color: red; font-weight: 600; width: 300px;">
                <el-text>{{ userInfo.uuid }}</el-text>
            </el-form-item>
            <el-form-item label="用户名:" style="width: 300px;">
                <el-text>{{ userInfo.username }}</el-text>
            </el-form-item>
            <el-form-item label="昵称:" style="width: 300px;">
                <el-text>{{ userInfo.nickname }}</el-text>
            </el-form-item>
            <el-form-item label="会员等级:" style="width: 300px;">
                <el-text v-if="userInfo.vipLevel > 0">{{ userInfo.vipLevel }}</el-text>
            </el-form-item>
            <el-form-item label="会员到期时间:" style="width: 300px;">
                <el-text v-if="userInfo.materialMemberExpireAt">{{ userInfo.materialMemberExpireAt }}</el-text>
            </el-form-item>

        </el-form>
    </div>

    <!-- 添加会员弹窗 -->
    <el-dialog v-model="addVipMemberFormDialog" :title="formTitle" width="500px" destroy-on-close
        :close-on-click-modal="false" :close-on-press-escape="false" center :show-close="false">
        <el-form :model="newVipInfo" style="width: 280px">
            <el-form-item label="永久会员">
                <el-switch v-model="newVipInfo.lifeLong" />
            </el-form-item>
            <el-form-item label="到期时间" v-if="newVipInfo.lifeLong == false">
                <el-date-picker v-model="newVipInfo.expireAt" type="date" placeholder="" />
            </el-form-item>
            <el-form-item label="会员等级" v-if="addMemberType == 'Material'">
                <el-input v-model="newVipInfo.vipLevel" type="number" />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="addVipMemberFormDialog = false">取消</el-button>
                <el-button type="primary" @click="saveVipInfo">
                    保存
                </el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { getUserInfoByUUid, updateMemberInfo } from '@/api/manager/user.js'
import { ElMessage } from 'element-plus';
import { computed, ref } from 'vue';
import dayjs from 'dayjs';

type MemberType = 'Material' | 'Plugin'
interface INewVipInfo {
    expireAt: Date | null
    vipLevel: string
    lifeLong: boolean
}

const defaultUserInfo = {
    id: '',
    uuid: '',
    username: '',
    nickname: '',
    materialMemberExpireAt: '',
    pluginMemberExpireAt: '',
    imageSplitMemberExpireAt: '',
    aiGenMemberExpireAt: '',
    vipLevel: 0
}

const getDefaultNewVipInfo = (): INewVipInfo => {
    return {
        expireAt: null,
        vipLevel: '1',
        lifeLong: true
    }
}

const newVipInfo = ref(getDefaultNewVipInfo())

const uuid = ref('')
const userInfo = ref(defaultUserInfo)
const showDeleteMaterialPopoverDialog = ref(false)
const showDeletePluginPopoverDialog = ref(false)
const addVipMemberFormDialog = ref(false)
const addMemberType = ref<MemberType>('Material')

const formTitle = computed(() => {
    return addMemberType.value == 'Material' ? '添加素材网会员' : '添加插件会员'
})

const formatTimestamp = (timestamp) => {
    return dayjs(timestamp * 1000).format('YYYY-MM-DD');
}

const showPopover = (type: MemberType) => {
    addMemberType.value = type
    addVipMemberFormDialog.value = true
}

// 查询用户信息
const queryUserInfo = async () => {
    if (uuid.value === '') {
        ElMessage.warning({
            message: "请输入用户ID"
        })
        return
    }

    const res = await getUserInfoByUUid(uuid.value)

    if (res.code == 500 && res.msg == 'record not found') {
        userInfo.value = defaultUserInfo

        return
    }

    if (res.code == 10000) {
        if (res.data) {
            userInfo.value = res.data
        } else {
            ElMessage.error({
                message: "未查询到用户信息"
            })
        }
    } else {
        ElMessage.error({
            message: "查询用户信息失败"
        })
        userInfo.value = defaultUserInfo
    }
}

// 保存会员信息
const saveVipInfo = async () => {

    // 默认2099年
    let expireAt = 4070880000
    if (newVipInfo.value.lifeLong == false) {
        if (newVipInfo.value.expireAt == null) {
            ElMessage.error({
                message: "请输入到期时间"
            })
            return
        }
        expireAt = newVipInfo.value.expireAt!.getTime() / 1000
    }

    let res = await updateMemberInfo({
        "user_id": userInfo.value.id,
        "member_type": addMemberType.value == 'Material' ? 1 : 2,
        "expired_create": expireAt,
        "vip_level": parseInt(newVipInfo.value.vipLevel)
    })

    if (res.code == 200) {
        queryUserInfo(uuid.value)
        addVipMemberFormDialog.value = false
        newVipInfo.value = getDefaultNewVipInfo()
    } else {
        ElMessage.error({
            message: "更新失败"
        })
    }
}

</script>

<style lang="scss" scoped>
.user-info-form {
    :deep(.el-form-item__label) {
        font-weight: 600;
    }
}

.dialog-footer button:first-child {
    margin-right: 10px;
}
</style>