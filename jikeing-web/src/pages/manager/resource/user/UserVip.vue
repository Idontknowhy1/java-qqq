<template>
    <div class="user-main-container user-vip-page">
        <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
            <el-tab-pane label="批量会员设置" name="Bat">
                <BatUserVip />
            </el-tab-pane>
            <el-tab-pane label="单会员设置" name="Single">
                <SingleUserVip />
            </el-tab-pane>
        </el-tabs>
    </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import SingleUserVip from './SingleUserVip.vue';
import BatUserVip from './BatUserVip.vue';

type UserType = 'Single' | 'Bat'
const activeName = ref<UserType>('Bat')

</script>

<style lang="scss" scoped>
.user-vip-page {
    padding: 20px 0 0 20px;
}
</style>