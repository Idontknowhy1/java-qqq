<template>
    <!-- 1. 编辑项目弹窗 -->
    <div
        class="fixed inset-0 z-50 flex items-center justify-center transition-all duration-300 bg-black/60 backdrop-blur-sm">
        <div
            class="bg-[#0f0f12] border border-zinc-800 w-full max-w-lg rounded-[32px] p-8 shadow-2xl scale-95 transition-all">
            <div class="flex items-center justify-between mb-8">
                <h2 class="text-2xl font-semibold text-white">上传视频</h2>
                <div @click="emit('close')" class="transition-colors text-zinc-500 hover:text-white">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M6 18L18 6M6 6l12 12" />
                    </svg>
                </div>
            </div>
            <div class="space-y-6 text-left">
                <!-- <div>
                    <label class="block mb-2 ml-1 text-sm text-zinc-400">项目名称</label>
                    <input type="text" value=""
                        class="w-full bg-[#1a1a1f] border border-zinc-800 rounded-2xl px-5 py-4 text-white focus:outline-none focus:border-indigo-500 transition-colors">
                </div> -->
                <div>
                    <!-- <label class="block mb-2 ml-1 text-sm text-zinc-400">视频</label> -->
                    <div
                        class="w-full h-60 bg-[#1a1a1f] border-2 border-dashed border-zinc-800 rounded-2xl flex flex-col items-center justify-center hover:border-indigo-500/50 cursor-pointer transition-all">

                        <el-upload action="#" :show-file-list="false" :before-upload="beforeUpload"
                            :http-request="upload">
                            <div
                                class="flex flex-col items-center justify-center gap-[8px] h-full w-full  text-[14px] font-bold text-[#6B7280]">
                                <svg v-if="commitLoading == false" class="w-8 h-8 mb-2 text-zinc-600" fill="none"
                                    stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                                        d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                </svg>
                                <span v-if="commitLoading == false" class="text-sm text-zinc-500">上传视频文件</span>
                            </div>
                        </el-upload>


                        <svg v-if="commitLoading" class="w-8 text-indigo-400 animate-spin" viewBox="0 0 24 24"
                            fill="none" stroke="currentColor">
                            <path d="M21 12a9 9 0 1 1-6.219-8.56" stroke-width="2.5" stroke-linecap="round" />
                        </svg>
                    </div>
                </div>
            </div>
            <!-- <div class="flex gap-4 mt-10">
                <button onclick="toggleModal('editModal', false)"
                    class="flex-1 px-6 py-4 font-medium transition-colors rounded-2xl bg-zinc-800 hover:bg-zinc-700">取消</button>
                <button onclick="toggleModal('editModal', false)"
                    class="flex-1 px-6 py-4 font-semibold text-black transition-colors bg-white rounded-2xl hover:bg-zinc-200">保存修改</button>
            </div> -->
        </div>
    </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { ref } from 'vue'
import { uploadOss } from '../../../../../utils/common'
import { commitVideoHpTask } from '../../../../../api/home/common'

const commitLoading = ref(false)

const emit = defineEmits(['commitSuccess', 'close'])

/**
 * 上传前的验证
 * @param {File} file - 上传的文件
 * @returns {boolean} 是否允许上传
 */
const beforeUpload = (file) => {
    // 如果是图片类型，检查文件大小
    // if (file.type.startsWith('image/')) {
    //     const fileSizeMB = file.size / (1024 * 1024)
    //     if (fileSizeMB > 10) {
    //         ElMessage.error('视频不得大于10M！')
    //         return false
    //     }
    // }
    return true
}
const upload = (item) => {
    commitLoading.value = true
    uploadOss(item, (value) => {
        // imageUrl.value = value.url
        // emit('select-from-local-file-result', value.url)
        commit(item.file.name, value.url)
    })
}

const commit = async (name: string, videoUrl: string) => {
    try {
        commitLoading.value = true
        const res = await commitVideoHpTask({
            args: JSON.stringify({
                video: videoUrl,
                name: name
            })
        })
        commitLoading.value = false
        if (res.code === 10000) {
            ElMessage.success('提交成功')
            emit('commitSuccess')
        } else {
            ElMessage.error(res.msg || '提交失败')
        }
    } catch (error) {
        commitLoading.value = false
        console.error(error)
        ElMessage.error('提交失败')
    }
}


</script>