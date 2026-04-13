<template>
    <div class="banner">
        <el-carousel :interval="5000" arrow="always">
            <el-carousel-item v-for="(item, key) in list" :key="key">
                <router-link :to="{ path: item.link }">
                    <el-image :src="item.image"></el-image>
                </router-link>
            </el-carousel-item>
        </el-carousel>
    </div>
</template>

<script setup lang="ts">
import { bannersList } from '@/api/home/banner'
import { onMounted, ref } from 'vue'
const list = ref([])

const queryBanners = () => {
    bannersList().then(res => {
        if (res.code == 200) {
            list.value = res.data.list
        }
    })
}

onMounted(() => {
    queryBanners()
})

</script>

<style scoped lang="scss">
::v-deep .el-carousel__container {
    height: calc(100vw * 300 / 1920);
}
::v-deep .el-carousel__item img {
    object-fit: cover;
    /* 裁剪并覆盖 */
    width: 100%;
    height: 100%;
}
</style>