<template>
    <div class="download-btn-inner" @click="downloadFile(url)">
        <i class="fa-solid fa-download"></i>
    </div>
</template>
<script setup lang="ts">
import { downloadFile } from '@/utils/common'

defineProps({
    url: {
        type: String,
        default: ''
    }
})

</script>

<style scoped lang="scss">
/* 工具类 */
.download-btn-inner {
    color: #fff;
    width: 30px;
    height: 30px;
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
}
</style>