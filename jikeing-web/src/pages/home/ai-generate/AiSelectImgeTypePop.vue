<template>
    <div class="ai-select-image-type-pop">
        <div class="title">添加图像</div>
        <div class="type-list-wrapper">

            <el-upload class="avatar-uploader" action="#" :show-file-list="false" :before-upload="beforeUpload"
                :http-request="upload">
                <div class="type-item">
                    <img src="@/assets/images/ai-selected-image-type-local.png" alt="" class="model-image">
                    <div class="model-name">本地上传</div>
                </div>
            </el-upload>
            <!-- 
            <div class="type-item" @click="test">
                <img src="@/assets/images/ai-selected-image-type-local.png" alt="" class="model-image">
                <div class="model-name">本地上传</div>
            </div> -->

            <div class="type-item" @click="roleClick">
                <img src="@/assets/images/ai-selected-image-type-role.png" alt="" class="model-image">
                <div class="model-name">角色库</div>
            </div>

        </div>
    </div>
</template>

<script setup lang="ts">
import { uploadOss } from '@/utils/common.ts'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['select-from-local-file-result', 'select-from-role'])

const test = () => {
    emit('select-from-local-file-result', 'https://media.res.yunzhijiu.cn/images/87c87c4b7b3cc525a9e7667bbc2e5a25.jpeg')
}
const roleClick = () => {
    emit('select-from-role')
}

const upload = (item) => {
    handleUpload({
        file: item.file
    })
}

/**
 * 处理上传到OSS
 */
const handleUpload = (item) => {
    uploadOss(item, (value) => {
        emit('select-from-local-file-result', value.url)
    })
}

/**
 * 上传前的验证
 * @param {File} file - 上传的文件
 * @returns {boolean} 是否允许上传
 */
const beforeUpload = (file) => {
    // 如果是图片类型，检查文件大小
     if (file.type.startsWith('image/')) {
         const fileSizeMB = file.size / (1024 * 1024)
         if (fileSizeMB > 10) {
             ElMessage.error('图片尺寸不得大于10M！')
             return false
         }
     }
    return true
}


</script>

<style lang="scss" scoped>
.ai-select-image-type-pop {
    width: 200px;
    height: 150px;
    display: flex;
    gap: 16px;
    flex-direction: column;
    background-color: #273043;
    padding: 20px 10px;
    border-radius: 18px;
    box-sizing: border-box;

    .title {
        color: #8498B8;
        font-size: 16px;
    }

    .type-list-wrapper {
        display: flex;
        flex-direction: column;

        .type-item {
            display: flex;
            align-items: center;
            border-radius: 12px;
            width: 180px;
            height: 35px;
            cursor: pointer;
            margin-bottom: 8px;
            padding-left: 10px;

            .model-image {
                width: 18px;
                height: 18px;
            }

            .model-name {
                margin-left: 10px;
                font-size: 18px;
                color: white;
            }
        }
    }
}
</style>