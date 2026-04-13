<template>
    <el-dialog v-model="dialogVisible" width="900" :show-close="false" style="padding: 0;" append-to-body
        :close-on-press-escape="false" :close-on-click-modal="false">
        <div class="pop-wrapper">
            <div class="header-bar">
                <div class="title">多视角</div>
                <img class="close-btn" src="@/assets/images/close-btn.png" @click="emit('dismiss')" />
            </div>
            <div class="list-wrapper" ref="target">
                <div class="list-item" v-for="item in images" :key="item">
                    <el-image class="item-image" :src="item" fit="cover" :preview-src-list="[item]"/>
                    <div class="mini-buttons-bar">
                        <img class="mini-btn" src="@/assets/images/ai-image-download-icon.png" alt="" @click="downloadFile(item)" />
                        <img class="mini-btn" src="@/assets/images/ai-image-hp-icon.png" alt=""  @click="emit('sendImageHp', taskId, item)"/>
                        <img class="mini-btn" src="@/assets/images/ai-image-template-icon.png" alt="" @click="emit('useTemplate', [item], '')"/>
                    </div>
                </div>
            </div>
        </div>
    </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { downloadFile } from '@/utils/common';
 
const props = defineProps(['images', 'taskId'])
const emit = defineEmits(['dismiss', 'useTemplate', 'sendImageHp'])

const dialogVisible = ref(true)

</script>

<style lang="scss" scoped>
.pop-wrapper {
    padding: 30px;
    padding-top: 10px;
    box-sizing: border-box;
    background-color: #22173D;
    border-radius: 20px;

    .header-bar {
        width: 100%;
        height: 65px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        border-bottom: 1px solid #444;

        .title {
            font-size: 22px;
            color: white;
            font-weight: 600;
        }

        .close-btn {
            width: 32px;
            height: 32px;
        }
    }

    .list-wrapper {
        width: 100%;
        height: 500px;
        overflow: hidden;
        overflow-y: auto;
        display: flex;
        flex-wrap: wrap;
        align-content: flex-start;
        gap: 10px;

        .list-item {
            position: relative;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            width: 160px;
            height: 160px;
            background-color: #666;
            border-radius: 12px;

            .item-image {
                border-radius: 12px;
            }

            .mini-buttons-bar {
                display: flex;
                justify-content: center;
                align-items: center;
                position: absolute;
                bottom: 8px;
                right: 8px;
                border-radius: 10px;

                display: flex;
                height: 30px;
                background-color: rgba($color: #191B1C, $alpha: 0.8);
                padding: 0 18px;
                gap: 10px;

                .mini-btn {
                    width: 22px;
                    height: 22px;
                    cursor: pointer;
                }
            }
        }
    }
}
</style>