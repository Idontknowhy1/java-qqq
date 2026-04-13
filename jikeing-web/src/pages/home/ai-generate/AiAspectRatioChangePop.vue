<template>
    <div class="ai-model-change-pop">
        <div class="title">选择比例</div>
        <div class="model-list-wrapper">
            <div class="model-item" v-for="model in models" :key="model.title"
                @click="emit('updateAspect', model.title)" :class="{ selected: selectedAspect == model.title }">
                <div class="flex flex-col items-center gap-[5px]">
                    <img :src="model.image" class="w-[24px] h-[24px]" />
                    <div>{{ model.title }}</div>
                </div>
            </div>
        </div>
        <div v-if="showRes" class="title">选择分辨率</div>
        <div v-if="showRes" class="model-list-wrapper" style="height: 44px;">
            <div class="model-item" style="width: 50%; height: 100%;" v-for="model in resList" :key="model"
                @click="emit('updateRes', model)" :class="{ selected: selectedRes == model }">
                <div class="flex flex-col items-center gap-[5px]">
                    <div>{{ model }}</div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">

import scale_auto_nor_image from '../../../assets/images/ai_scale_nor/auto@2x.png'
import scale_21_9_nor_image from '../../../assets/images/ai_scale_nor/21-9@2x.png'
import scale_16_9_nor_image from '../../../assets/images/ai_scale_nor/16-9@2x.png'
import scale_4_3_nor_image from '../../../assets/images/ai_scale_nor/4-3@2x.png'
import scale_3_2_nor_image from '../../../assets/images/ai_scale_nor/3-2@2x.png'
import scale_1_1_nor_image from '../../../assets/images/ai_scale_nor/1-1@2x.png'
import scale_2_3_nor_image from '../../../assets/images/ai_scale_nor/2-3@2x.png'
import scale_3_4_nor_image from '../../../assets/images/ai_scale_nor/3-4@2x.png'
import scale_9_16_nor_image from '../../../assets/images/ai_scale_nor/9-16@2x.png'

defineProps(['selectedAspect', 'selectedRes', 'showRes'])
const emit = defineEmits(['updateAspect', 'updateRes'])

const resList = ["2k", "4k"]

const models = [
    { title: '智能', image: scale_auto_nor_image },
    { title: '21:9', image: scale_21_9_nor_image },
    { title: '16:9', image: scale_16_9_nor_image },
    { title: '4:3', image: scale_4_3_nor_image },
    { title: '3:2', image: scale_3_2_nor_image },
    { title: '1:1', image: scale_1_1_nor_image },
    { title: '2:3', image: scale_2_3_nor_image },
    { title: '3:4', image: scale_3_4_nor_image },
    { title: '9:16', image: scale_9_16_nor_image },
]

</script>

<style lang="scss" scoped>
.ai-model-change-pop {
    width: 470px;
    height: 1%;
    display: flex;
    gap: 16px;
    flex-direction: column;
    background-color: #273043;
    padding: 20px;
    border-radius: 18px;
    box-sizing: border-box;

    .title {
        color: #8498B8;
        font-size: 16px;
    }

    .model-list-wrapper {
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: #333C4F;
        border-radius: 8px;
        height: 66px;
        width: 430px;

        .model-item {
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 12px;
            height: 58px;
            width: 62px;
            cursor: pointer;
            font-size: 14px;
            color: white;
        }

        .model-item.selected {
            background-color: #414C61;
        }
    }
}
</style>