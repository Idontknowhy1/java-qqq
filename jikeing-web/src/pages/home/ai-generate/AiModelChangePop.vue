<template>
    <div class="ai-model-change-pop">
        <div class="title">模型选择</div>
        <div class="model-list-wrapper">

            <div class="model-item" v-for="model in models" :key="model.id" @click="emit('updateModel', model)">
                <div class="left-wrapper">
                    <img src="@/assets/images/ai-model-item-image.png" alt="" class="model-image">
                </div>
                <div class="center-wrapper">
                    <div class="model-name">{{ model.title }}</div>
                    <div class="model-desc">{{ model.desc }}</div>
                </div>
                <div class="right-wrapper">
                    <img src="@/assets/images/ai-model-item-checked-btn.png" alt="" class="model-checked-arrow"
                        :class="{ selected: selectedModel.id == model.id }">
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

defineProps(['selectedModel'])
const emit = defineEmits(['updateModel'])

const models = [{
    id: "b",
    title: '模型一',
    desc: '更优惠的积分选择'
}, {
    id: "b2",
    title: '模型二',
    desc: '更遵循指令，更高的图片质量'
}]

</script>

<style lang="scss" scoped>
.ai-model-change-pop {
    width: 470px;
    height: 260px;
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
        flex-direction: column;

        .model-item {
            display: flex;
            align-items: center;
            background-color: #333C4F;
            border-radius: 12px;
            width: 430px;
            height: 88px;
            cursor: pointer;
            margin-bottom: 8px;

            .left-wrapper {
                display: flex;
                justify-content: center;
                align-items: center;
                width: 74px;

                .model-image {
                    width: 48px;
                    height: 48px;
                    background-color: blue;
                }
            }

            .center-wrapper {
                flex-grow: 1;

                .model-name {
                    font-size: 18px;
                    color: white;
                }

                .model-desc {
                    font-size: 16px;
                    color: #8498B8;
                }
            }

            .right-wrapper {
                display: flex;
                justify-content: center;
                align-items: center;
                width: 70px;

                .model-checked-arrow {
                    visibility: hidden;
                    width: 20px;
                    height: 20px;
                }

                .model-checked-arrow.selected {
                    visibility: visible;
                }

                .status {
                    color: #999;
                }
            }
        }
    }
}
</style>