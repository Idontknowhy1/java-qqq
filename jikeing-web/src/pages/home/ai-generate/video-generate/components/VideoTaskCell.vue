vue
<template>
    <div>
        <div class="flex justify-between w-full p-[16px] text-[#6B7280] gap-[16px]">
            <!-- 创作时间、ID -->
            <div class="flex items-center gap-[8px] text-[12px] text-[#8b92a7]">
                <span v-if="task.model == 'video-hp'">高清放大</span>
                <span v-else-if="task.hasReferImage">图生视频</span>
                <span v-else>文生视频</span>
                | 创作时间：{{ task.createTimeText }}
                | ID：{{ task.idText }}
            </div>
            <div>
                <button class="btn-remix" @click="emit('makeSame', task)">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M12 3v18" />
                        <path d="M3 12h18" />
                    </svg>
                    做同款
                </button>
            </div>
        </div>

        <!-- 垫图、提示词 -->
        <div class="flex items-start gap-[16px] justify-between mb-[16px]">
            <div class="flex gap-[8px]">
                <el-image v-if="task.hasReferImage" :src="task.referImage" class=" shrink-0 w-[100px] h-[100px]"
                    :preview-src-list="[task.referImage]" fit="cover" />
                <div class="leading-[1.6] text-[14px] text-[#ccc]">{{ task.prompt }}</div>
                <el-icon v-if="task.prompt && task.prompt.length > 0" :size="20" color="#6B7280"
                    style="cursor: pointer;" @click="() => copyText(task.prompt)">
                    <CopyDocument />
                </el-icon>
            </div>

        </div>

        <!-- 图片预览 -->
        <div class="flex flex-wrap gap-[16px]">
            <div class="relative w-[calc((100%-16px)*0.5)] min-w-[200px] aspect-[16/9] flex items-center justify-center bg-[#55364358] rounded-[12px]"
                v-for="card in task.outputsJSON" :key="card.index">
                <!-- 正在生成 -->
                <div v-if="card.status < 2" class="relative w-full h-full">
                    <LoadingSpin />
                </div>
                <!-- 正在失败 -->
                <div v-if="card.status >= 3">
                    <div class="flex items-center justify-center w-[500px] h-full text-[14px] text-[#6B7280] overflow-hidden"
                        style="display: -webkit-box; 
                            -webkit-line-clamp: 5;   /* 限制行数 */
                            -webkit-box-orient: vertical;">
                        {{ card.msg || '生成失败' }}
                    </div>
                </div>
                <!-- 生成完成 -->
                <div v-if="card.status == 2"
                    class="relative flex items-center justify-center w-full h-full rounded-[12px] overflow-hidden">
                    <video :src="card.value" styles="width: 100%; height: 100%;object-fit:cover;"
                        class="rounded-[12px]" />
                    <div class="absolute top-[50%] left-[50%] translate-x-[-50%] translate-y-[-50%]"
                        @click="emit('playVideo', card.value)">
                        <img :src="videoPlayBtnImage" alt="播放视频" class="w-[44px]">
                    </div>
                    <div class="download-btn">
                        <!-- 已经是高清放大任务，不能在高清放大 -->
                        <button v-if="task.model != 'video-hp'" @click="emit('handleUpscale', task, card)"
                            class="flex items-center gap-1.5 px-3 py-1.5 bg-black/40 hover:bg-black/60 backdrop-blur-md text-white text-sm font-medium rounded-lg border border-white/20 transition-all active:scale-95 group/btn">
                            <!-- 高清放大图标 (使用 SVG) -->
                            <svg xmlns="http://www.w3.org/2000/svg"
                                class="w-4 h-4 text-blue-300 group-hover/btn:animate-pulse" viewBox="0 0 24 24"
                                fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                stroke-linejoin="round">
                                <path d="m21 11-8-8" />
                                <path d="m21 3-9 9" />
                                <path d="M12 18H3" />
                                <path d="M10 22l-2-2 2-2" />
                                <path d="M20 12v10" />
                                <path d="M24 18l-2 2-2-2" />
                                <path d="M9 4v4" />
                            </svg>
                            <span>高清放大</span>
                        </button>
                        <div class="bg-black/60 rounded-[6px]">
                            <DownloadBtn :url="card.value" />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

</template>
<script setup lang="ts">
import { copyText, downloadFile } from '@/utils/common'
import LoadingSpin from '@/components/LoadingSpin.vue'
import videoPlayBtnImage from '@/assets/images/video-play-btn.png'
import DownloadBtn from '../../../../../components/DownloadBtn.vue'

const props = defineProps({
    task: {
        type: {
            outputsJSON: Array
        },
        default: () => ({
            outputsJSON: []
        })
    }
})
const emit = defineEmits(['playVideo', 'makeSame', 'handleUpscale'])

</script>
<style scoped>
/* 工具类 */
.download-btn {
    position: absolute;
    top: 10px;
    right: 10px;
    color: #fff;
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    gap: 20px;
}

/* 右侧：做同款按钮 */
.btn-remix {
    flex-shrink: 0;
    /* 防止按钮被挤压 */
    background: var(--primary-gradient);
    border: none;
    color: white;
    font-size: 13px;
    font-weight: 600;
    padding: 6px 16px;
    border-radius: 20px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 4px;
    transition: opacity 0.2s;
    box-shadow: 0 0 10px rgba(59, 130, 246, 0.3);
    /* 轻微发光 */
}

.btn-remix:hover {
    opacity: 0.9;
}
</style>