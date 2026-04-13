<template>
    <!-- 查询条件 -->
    <el-form label-position="right" inline label-width="60px">
        <el-form-item label="用户ID">
            <el-input v-model="uuid" />
        </el-form-item>
        <el-form-item>
            <el-button type="primary" @click="queryUserScore">查询</el-button>
        </el-form-item>
    </el-form>

    <!-- 用户信息 -->
    <div v-if="scoreInfo.userId > 0">
        <el-form class="user-info-form" label-position="right" inline :model="scoreInfo" label-width="150px"
            style="max-width:700px">
            <el-form-item label="用户ID:" style="color: red; font-weight: 600; width: 300px;">
                <el-text>{{ scoreInfo.uuid }}</el-text>
            </el-form-item>
            <el-form-item label="会员积分:" style="width: 300px;">
                <el-text>{{ scoreInfo.vipScore }}</el-text>
            </el-form-item>
            <el-form-item label="永久积分:" style="width: 300px;">
                <el-text>{{ scoreInfo.forScore }}</el-text>
            </el-form-item>
        </el-form>
        <div>
            <el-input style="width: 100px;" type="number" v-model="newScore" />
            <el-button type="primary" @click="queryAddScore">添加永久积分</el-button>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { addScore, getUserScore } from '@/api/manager/score.js'
import { ElMessage } from 'element-plus';
import { ref } from 'vue';

const defaultScoreInfo = {
    forScore: 0,
    vipScore: 0,
    userId: 0,
    userIdText: '',
}

const uuid = ref('')
const scoreInfo = ref(defaultScoreInfo)

const newScore = ref(0)

const queryUserScore = async () => {
    if (uuid.value === '') {
        ElMessage.warning({
            message: "请输入用户ID"
        })
        return
    }

    const res = await getUserScore({ uuid: uuid.value })

    if (res.code == 10000) {
        res.data.uuid = uuid.value
        scoreInfo.value = res.data
    } else {
        ElMessage.error({
            message: "查询用户积分失败"
        })
        scoreInfo.value = defaultScoreInfo
    }
}

// 添加信息
const queryAddScore = async () => {

    if (newScore.value == 0) {
        ElMessage.warning({
            message: "请输入需要添加的积分"
        })
        return
    }

    let res = await addScore({
        "toUserId": scoreInfo.value.userIdText,
        "score": newScore.value
    })

    if (res.code == 10000) {
        queryUserScore()
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