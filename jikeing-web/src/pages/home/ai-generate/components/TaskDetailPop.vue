<template>
    <el-dialog v-model="dialogVisible" width="80%" style="padding: 0;" append-to-body align-center
        @closed="dialogClosed">
        <div class="task-detail-pop-wrapper">
            <div class="image-preview-wrapper">
                <el-image :src="data.imageUrl" :preview-src-list="[data.imageUrl]" class="image" fit="contain" />
            </div>
            <div class="right-wrapper">
                <div class="content-wrapper">
                    <div class="section-title">图片提示词</div>
                    <div class="prompt-wrapper">{{ data.prompt }}</div>
                    <div class="button-wrapper">
                        <div class="button" @click="makeSame(data.referImages, data.prompt)">
                            <img class="icon-image" src="@/assets/images/make-same-image.png" />
                            <span>做同款</span>
                        </div>
                        <div class="button" @click="useTemplate(data.imageUrl, '')">
                            <img class="icon-image" src="@/assets/images/use-as-refer-image.png" />
                            <span>用作参考图</span>
                        </div>
                        <div class="button" @click="downloadFile(data.imageUrl)">
                            <img class="icon-image" src="@/assets/images/download-btn-icon.png" />
                            <span>下载</span>
                        </div>
                        <div class="button" @click="useUploadSplit(data.imageUrl)" v-if="showUploadSplit">
                            <img class="icon-image" src="@/assets/images/upload-split-btn-icon.png" />
                            <span>上传拆分</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </el-dialog>

    <!-- 隐私协议弹窗 -->
    <UploadSplitPopForm v-if="showUploadFormPop" @dismiss="showUploadFormPop = false" :imageUrl="currentUploadTaskUrl" />

</template>

<script setup lang="ts">
import { ref } from 'vue';
import { downloadFile } from '@/utils/common.ts'
import UploadSplitPopForm from './uploadSplitPopForm.vue';

defineProps(['data', 'showUploadSplit'])
const emit = defineEmits(['dismiss', 'use-template', 'make-same'])

const dialogVisible = ref(true)
const showUploadFormPop = ref(false)

const currentUploadTaskUrl = ref('')

const dialogClosed = () => {
    emit('dismiss')
}

// 用作模版、用作参考图
const useTemplate = (url, prompt) => {
    emit('use-template', url, prompt)
}

// 做同款
const makeSame = (referImages, prompt) => {
    if (referImages && referImages.length > 0) {
        emit('make-same', JSON.parse(referImages), prompt)
    } else {
        emit('make-same', [], prompt)
    }

}

// 上传拆分
const useUploadSplit = (url) => {
    showUploadFormPop.value = true
    currentUploadTaskUrl.value = url
}

</script>

<style lang="scss" scoped>
.task-detail-pop-wrapper {
    display: block;
    height: calc(100vh * 0.8);
    background-color: #111827;
    display: flex;

    .image-preview-wrapper {
        display: flex;
        justify-content: center;
        align-items: center;
        flex-grow: 1;

        .image {
            width: 90%;
            height: 90%;
        }
    }

    .right-wrapper {
        width: 400px;
        padding: 40px 0;

        .content-wrapper {
            height: 100%;
            box-sizing: border-box;
            border-left: 1px solid #495F81;
            padding: 0 30px;

            .section-title {
                font-size: 12px;
                color: rgba($color: #FFFFFF, $alpha: 0.3);
                margin-bottom: 16px;
            }

            .prompt-wrapper {
                font-size: 12px;
                color: rgba($color: #FFFFFF, $alpha: 0.8);
                margin-bottom: 20px;
                line-height: 16px;
            }

            .button-wrapper {
                display: flex;
                flex-wrap: wrap;
                font-size: 12px;
                color: white;

                .button {
                    .icon-image {
                        width: 20px;
                        height: 20px;
                        margin-right: 5px;
                    }

                    display: flex;
                    justify-content: center;
                    align-items: center;
                    background-color: #364358;
                    width: 134px;
                    height: 43px;
                    border-radius: 10px;
                    margin-bottom: 16px;
                    margin-right: 10px;
                    cursor: pointer;
                }
            }
        }


    }
}
</style>