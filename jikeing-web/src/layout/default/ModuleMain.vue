<template>
    <div style="background-color: white;">
        <el-watermark :width="130" :height="30" :content="userStore.nickname" :font="{ fontSize: 26 }">
            <ManagerHeader />
            <div class="main-content">
                <div class="slide-menu-wrapper">
                    <SlideMenus />
                </div>
                <div class="router-view">
                    <router-view></router-view>
                </div>
            </div>
        </el-watermark>

    </div>
</template>

<script lang="ts" setup>
import ManagerHeader from '../../pages/manager/components/ManagerHeader.vue';
import SlideMenus from '../../pages/manager/components/SlideMenus.vue';
import { useUserStore } from '../../store/useUserStore';
const userStore = useUserStore()
</script>

<style lang="scss" scoped>
.main-content {
    display: flex;
    height: 100%;

    .slide-menu-wrapper {
        width: 150px;
        height: 100%;
    }

    .router-view {
        flex: 1;
    }
}
</style>