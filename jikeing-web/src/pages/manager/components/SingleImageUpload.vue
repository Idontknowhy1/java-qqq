<template>
    <div>
        <!-- 上传按钮 -->
        <el-upload class="upload-demo" ref="uploadRef" :show-file-list="false" action="#" :before-upload="beforeUpload"
            :http-request="upload">
            <el-button class="el-icon-upload" size="small" type="primary">
                上传文件/图片
            </el-button>
        </el-upload>

        <!-- 裁剪组件 -->
        <VueCropperCom v-model:show="dialog.cropperStatus" :file="cropperFile" :com-name="''" @callFn="cropperCallFn" />
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import VueCropperCom from './VueCropperCom.vue'
import { uploadOss } from '@/utils/common.ts'

// 定义组件属性
const props = defineProps({
    from: {
        type: String,
        default: ''
    }
})

// 定义组件事件
const emit = defineEmits(['callfn'])

// 响应式状态
const dialog = reactive({
    cropperStatus: false,
    cropperFrom: ''
})

const cropperFile = ref(null)
const uploadRef = ref(null)

/**
 * 上传处理
 * @param {Object} item - 上传文件对象
 */
const upload = (item) => {
    uploadCallfn({
        from: props.from,
        file: item.file,
        type: 'cropper'
    })
}

/**
 * 裁剪组件回调
 * @param {Object} value - 裁剪后的文件
 */
const cropperCallFn = (value) => {
    handleUpload({
        file: value.file,
        from: dialog.cropperFrom
    })

    // 关闭裁剪对话框
    dialog.cropperStatus = false
}

/**
 * 上传选项处理 - 提示用户是否裁剪
 * @param {Object} value - 文件对象
 */
const uploadCallfn = (value) => {
    // ElMessageBox.confirm(
    //     '是否需要截图?',
    //     '图片处理选项',
    //     {
    //         confirmButtonText: '需要截图',
    //         cancelButtonText: '不需要截图，直接上传',
    //         type: 'warning',
    //         distinguishCancelAndClose: true
    //     }
    // )
    //     .then(() => {
    //         // 用户选择需要截图
    //         cropperFile.value = value.file
    //         dialog.cropperFrom = value.from
    //         dialog.cropperStatus = true
    //     })
    //     .catch((action) => {
    //         // 用户选择不需要截图 (action === 'cancel')
    //         if (action === 'cancel') {
    //             handleUpload({
    //                 file: value.file,
    //                 from: value.from
    //             })
    //         }
    //     })

    handleUpload({
        file: value.file,
        from: value.from
    })
}

/**
 * 处理上传到OSS
 * @param {Object} item - 要上传的文件
 */
const handleUpload = (item) => {
    uploadOss(item, (value) => {
        // 上传成功后向父组件发送事件
        emit('callfn', {
            from: item.from,
            value: value
        })
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
        if (fileSizeMB > 5) {
            ElMessage.error('图片尺寸不得大于5M！')
            return false
        }
    }
    return true
}


</script>

<style scoped>
.upload-demo {
    display: inline-block;
    margin-right: 10px;
}

.el-icon-upload {
    padding: 8px 15px;
    font-size: 14px;
}
</style>