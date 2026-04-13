<template>
    <div class="main-container ai-generate-page">
        <HeaderNav />
        <div class="ai-generate-content-wrapper">
            <div class="left-wrapper">
                <div class="segment-wrapper">
                    <div class="segment-item" :class="{ selected: selectedSegement == menu.tag }"
                        @click="() => changeSeg(menu)" v-for="menu in sideMenus">
                        <div class="segment-item-image" :class="menu.image"></div>
                        <span class="segment-item-title">{{ menu.title }}</span>
                    </div>
                </div>
            </div>
            <div class="right-wrapper">

                <MaterialSquareList v-if="selectedSegement == 'material-square'" :selected-seg="selectedSegement"
                    @use-template="useImageAndPrompt" @make-same="makeSame" />
                <!-- 图片生成 -->
                <AIGenList v-if="selectedSegement == 'generate'" :defaultImages="generateDefaultImages"
                    :defaultPrompt="generateDefaultPrompt" />

                <!-- 视频生成 -->
                <AIVideoGenerate v-if="selectedSegement == 'generate-v'" />
                <!-- 视频高清生成 -->
                <VideoHpPage v-if="selectedSegement == 'generate-v-hp'" />

                <AiAssetsList v-if="selectedSegement == 'assets'" selected-seg="generate"
                    @use-template="useImageAndPrompt" />
                <AiAssetsList v-if="selectedSegement == 'role'" :selected-seg="selectedSegement"
                    @use-template="useImageAndPrompt" />
                <AiAssetsList v-if="selectedSegement == 'actions'" :selected-seg="selectedSegement"
                    @use-template="useImageAndPrompt" />
                <AiAssetsList v-if="selectedSegement == 'emotion'" :selected-seg="selectedSegement"
                    @use-template="useImageAndPrompt" />
                <AiAssetsList v-if="selectedSegement == 'scene'" :selected-seg="selectedSegement"
                    @use-template="useImageAndPrompt" />

                <AiAssetsList v-if="selectedSegement == 'upload-split'" :selected-seg="selectedSegement" />
                <!-- <AiAssetsList v-if="selectedSegement == 'agreement'" /> -->
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import AIGenList from './AIGenList.vue';
import AiAssetsList from './AiAssetsList.vue';
import { useUserStore } from '../../../store/useUserStore';
import MaterialSquareList from './MaterialSquareList.vue';
import AIVideoGenerate from './video-generate/AIVideoGenerate.vue';
import VideoHpPage from './video-hp/VideoHpPage.vue';

const useStore = useUserStore()
const generateDefaultImages = ref([])
const generateDefaultPrompt = ref('')

const selectedSegement = ref('material-square')

const changeSeg = (seg) => {
    if (seg.isFolder) {
        return
    }
    selectedSegement.value = seg.tag
}

const useImageAndPrompt = (url, prompt) => {
    generateDefaultImages.value = [url]
    generateDefaultPrompt.value = prompt
    selectedSegement.value = 'generate'
}

const makeSame = (referImages, prompt) => {
    generateDefaultImages.value = referImages
    generateDefaultPrompt.value = prompt
    selectedSegement.value = 'generate'
}

const sideMenus = [{
    title: '探索发现',
    image: 'material-square',
    tag: 'material-square'
}, {
    title: '图片生成',
    image: 'generate',
    tag: 'generate'
},
{
    title: '视频生成',
    image: 'generate-video',
    tag: 'generate-v'
},
// {
//     title: '视频高清',
//     image: 'generate-video',
//     tag: 'generate-v-hp'
// },
{
    title: '资产',
    image: 'assets',
    tag: 'assets'
},
//  {
//     title: '提示词',
//     image: 'prompt',
//     tag: 'prompt'
// },
{
    title: '绘图宝典',
    image: 'dectionary',
    tag: 'dectionary',
    isFolder: true
}, {
    title: '角色',
    image: 'role',
    tag: 'role'
}, {
    title: '动作',
    image: 'actions',
    tag: 'actions'
},
// {
//     title: '表情',
//     image: 'emotion',
//     tag: 'emotion'
// }, {
//     title: '场景',
//     image: 'scene',
//     tag: 'scene'
// }, 
{
    title: '上传拆分',
    image: 'upload-split',
    tag: 'upload-split'
}

    // , {
    //     title: '平台协议',
    //     image: 'agreement',
    //     tag: 'agreement'
    // }
]

</script>

<style lang="scss" scoped>
$leftWrapperWidth: 180px;

.ai-generate-page {
    display: flex;
    flex-direction: column;

    .ai-generate-content-wrapper {
        display: flex;
        flex-grow: 1;

        .left-wrapper {
            display: flex;
            justify-content: center;
            padding-top: 0px;
            width: $leftWrapperWidth;
            height: calc(100vh - 100px);
            box-sizing: border-box;
            border-right: 1px solid #262D38;

            .segment-wrapper {
                display: flex;
                flex-direction: column;
                align-items: center;
                padding-top: 100px;

                .segment-item {
                    display: flex;
                    align-items: center;
                    padding-left: 20px;
                    height: 55px;
                    width: 150px;
                    background-color: transparent;
                    border-radius: 12px;
                    margin-bottom: 10px;
                    cursor: pointer;

                    .segment-item-image {
                        width: 20px;
                        height: 20px;
                        margin-right: 10px;
                        background-size: contain;
                        background-repeat: no-repeat;
                    }

                    .segment-item-title {
                        font-size: 14px;
                        color: #636A78;
                    }

                    .segment-item-image.material-square {
                        background-image: url(@/assets/images/ai_gen_segment_btn_material_square_nor.png);
                    }

                    .segment-item-image.generate {
                        background-image: url(@/assets/images/ai_gen_segment_btn_gen_nor.png);
                    }

                    .segment-item-image.generate-video {
                        background-image: url(@/assets/images/ai_gen_segment_btn_gen_video_nor.png);
                    }

                    .segment-item-image.assets {
                        background-image: url(@/assets/images/ai_gen_segment_btn_assets_nor.png);
                    }

                    .segment-item-image.prompt {
                        background-image: url(@/assets/images/ai_gen_segment_btn_prompt_nor.png);
                    }

                    .segment-item-image.dectionary {
                        background-image: url(@/assets/images/ai_gen_segment_btn_dectionary_nor.png);
                    }

                    .segment-item-image.upload-split {
                        background-image: url(@/assets/images/ai_gen_segment_btn_upload_split_nor.png);
                    }

                    .segment-item-image.agreement {
                        background-image: url(@/assets/images/ai_gen_segment_btn_agreement_nor.png);
                    }
                }

                .segment-item.selected {
                    background-color: #1F2937;

                    .segment-item-image.material-square {
                        background-image: url(@/assets/images/ai_gen_segment_btn_material_square_sel.png);
                    }

                    .segment-item-image.generate {
                        background-image: url(@/assets/images/ai_gen_segment_btn_gen_sel.png);
                    }

                    .segment-item-image.generate-video {
                        background-image: url(@/assets/images/ai_gen_segment_btn_gen_video_sel.png);
                    }

                    .segment-item-image.assets {
                        background-image: url(@/assets/images/ai_gen_segment_btn_assets_sel.png);
                    }

                    .segment-item-image.prompt {
                        background-image: url(@/assets/images/ai_gen_segment_btn_prompt_sel.png);
                    }

                    .segment-item-image.dectionary {
                        background-image: url(@/assets/images/ai_gen_segment_btn_dectionary_sel.png);
                    }

                    .segment-item-image.upload-split {
                        background-image: url(@/assets/images/ai_gen_segment_btn_upload_split_sel.png);
                    }

                    .segment-item-image.agreement {
                        background-image: url(@/assets/images/ai_gen_segment_btn_agreement_sel.png);
                    }

                    .segment-item-title {
                        color: #35C8FC;
                    }
                }
            }
        }

        .right-wrapper {
            flex-grow: 1;
            height: calc(100vh - 55px);
            width: calc(100vw - $leftWrapperWidth);
            box-sizing: border-box;
            overflow: hidden;
            overflow-y: auto;
        }
    }
}
</style>