<template>
    <div class="main-container">
        <HeaderNav />
        <RightNav />
        <div style="position: relative;">
            <Banner />
            <CategoryNav @callfn="callCategroyItem" from="material" name="动画素材" type="list" />
            <MaterialList ref="index_list" @show-detail="(item) => showDetail(item)" />
            <Footer />
            <el-dialog v-model="showDetial" fullscreen :show-close="false" class="fullscreen-dialog" destroy-on-close>
                <MaterialDetail :id="detialId" @dismiss-page="() => showDetial = false" />
            </el-dialog>

        </div>
    </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue';

import RightNav from './components/RightNav.vue'
import Footer from '../../layout/default/Footer.vue';
import Banner from './components/Banner.vue';
import CategoryNav from './components/CategoryNav.vue';
import { useUserStore } from '@/store/useUserStore.ts'
import MaterialList from './components/MaterialList.vue';
import HeaderNav from './components/HeaderNav.vue';
import MaterialDetail from './MaterialDetail.vue';

const userStore = useUserStore()

const detialId = ref(0)
const showDetial = ref(false)

const index_list = ref<InstanceType<typeof MaterialList> | null>(null)

const callCategroyItem = (value) => {
    if (index_list.value) {
        index_list.value.requestList(value.id, 1)
    }
}

const showDetail = (item) => {
    detialId.value = item.id
    showDetial.value = true
}

// callCategroyItem(value) {
//     this.$refs['index_list'].materialsList(value.id, 1)
// },
// checkLogin() {
//     if (this.$store.getters.loginStatus == 1) {
//         this.$router.push({ path: this.success_url, query: {} })
//     }
// },
// closeForm() {
//     this.$router.push({ path: '/user/info', query: {} })
//     this.showStatus = false
// },

</script>

<style lang="scss" scoped>
/* 全屏对话框样式 - 关键部分 */
.fullscreen-dialog {
    margin: 0 !important;
    width: 100vw !important;
    max-width: 100vw !important;
    height: 100vh !important;
    overflow: hidden;
    border-radius: 0 !important;
    background-color: red;
}

:deep(.el-dialog__header) {
    display: none;
}

:deep(.el-dialog) {
    padding: 0;
}

.fullscreen-dialog .el-dialog__body {
    padding: 0 !important;
    height: 100vh;
    // background: #1e1e2f;
    color: #fff;
    overflow: hidden;
    background-color: red;
}
</style>