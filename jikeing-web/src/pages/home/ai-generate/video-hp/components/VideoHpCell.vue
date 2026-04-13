<template>
    <!-- 主卡片容器 -->
    <div
        class="w-72 bg-[#0d1117] border border-zinc-800/50 rounded-2xl overflow-hidden shadow-2xl transition-all hover:border-zinc-700">

        <!-- 上半部分：预览/状态区 -->
        <div class="relative flex items-center justify-center p-4 h-44">

            <!-- 右上角生成状态标签 -->
            <div class="absolute top-3 right-3 flex items-center gap-[8px] px-2.5 py-1 bg-indigo-500/10 border
                border-indigo-500/30 rounded-lg z-10">
                <!-- 旋转的加载图标 -->
                <svg v-if="item.status < 2" class="w-3.5 h-3.5 text-indigo-400 animate-spin" viewBox="0 0 24 24"
                    fill="none" stroke="currentColor">
                    <path d="M21 12a9 9 0 1 1-6.219-8.56" stroke-width="2.5" stroke-linecap="round" />
                </svg>
                <span v-if="item.status < 2" class="text-[11px] font-medium text-indigo-400">生成中</span>
                <span v-if="item.status == 2" class="text-[11px] font-medium text-indigo-400">生成完成</span>
                <span v-if="item.status == 3" class="text-[11px] font-medium text-indigo-400">生成失败</span>
                <DownloadBtn v-if="item.status == 2" :url="item.outputJSON.value" />
            </div>


            <!-- 中间视频文件占位图标 -->
            <!-- <div
                class="flex items-center justify-center w-20 h-20 border rounded-full bg-zinc-800/30 border-zinc-800/50">
                <svg class="w-10 h-10 text-zinc-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.2"
                        d="M7 21h10a2 2 0 002-2V9.414a1 1 0 00-.293-.707l-5.414-5.414A1 1 0 0012.586 3H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.2"
                        d="M11 15.5l2-1.5-2-1.5v3z" />
                </svg>
            </div> -->

            <div v-if="item.status == 2"
                class="relative flex items-center justify-center w-full h-full rounded-[12px] overflow-hidden">
                <video :src="item.value" styles="width: 100%; height: 100%;object-fit:cover;" class="rounded-[12px]" />
                <div class="absolute top-[50%] left-[50%] translate-x-[-50%] translate-y-[-50%] cursor-pointer"
                    @click="emit('playVideo', item.outputJSON.value)">
                    <img :src="videoPlayBtnImage" alt="播放视频" class="w-[44px]">
                </div>
            </div>
        </div>

        <!-- 下半部分：信息区 -->
        <div class="bg-[#161b22] px-4 py-4 flex justify-between items-end border-t border-zinc-800/30">
            <div class="flex flex-col gap-1">
                <h3 class="w-48 text-sm font-medium truncate text-zinc-100">
                    {{ item.argsJSON.name || item.idText }}
                </h3>
                <span class="font-mono text-xs text-zinc-500">ID: {{ item.idText }}</span>
                <span class="font-mono text-xs text-zinc-500">创建时间: {{ item.createTimeText }}</span>
            </div>

            <!-- 删除按钮 -->
            <!-- <button class="p-1 transition-colors text-zinc-500 hover:text-red-400">
                <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                        d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
            </button> -->
        </div>
    </div>
</template>

<script setup>

import { ref, onMounted } from 'vue'
import { copyText, downloadFile } from '@/utils/common'
import videoPlayBtnImage from '@/assets/images/video-play-btn.png'
import DownloadBtn from '../../../../../components/DownloadBtn.vue'

defineProps(['item'])
const emit = defineEmits(['playVideo'])

</script>

<style scoped></style>