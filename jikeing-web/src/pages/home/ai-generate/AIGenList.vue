<template>
    <div class="ai-gen-list-component">
        <div class="ai-gen-task-list" ref="target">
            <div class="ai-gen-task-cell" v-for="item in list" :key="item.idText">
                <!-- 普通图片 -->
                <div v-if="item.model != 'ms'" class="normal-cell-wrapper">
                    <div class="task-image-wrapper">
                        <div class="task-image"
                            v-if="item.status == 2 || (item.resultImages && item.resultImages.length > 0)">
                            <div class="background-wrapper">
                                <el-image class="task-image-background" fit="cover"
                                    :src="displayImage(item.resultImages[0])" />
                            </div>
                            <el-image class="task-image-fore" fit="contain" :src="displayImage(item.resultImages[0])"
                                :preview-src-list="item.resultImages" />
                        </div>
                        <div v-if="item.status < 2" class="status-tip">
                            <img src="@/assets/images/ai-gen-loading2.gif" style="width: 100%;">
                        </div>
                        <div v-if="item.status == 3" class="status-tip">
                            <span v-if="item.latestFailReason.includes('violate')"
                                style="color: white;">您的提示词或垫图违规</span>
                            <img v-if="item.latestFailReason.includes('violate') == false" class="gen-failure-image"
                                src="@/assets/images/ai_gen_failure.png" />
                        </div>
                    </div>
                    <div class="right-wrapper">
                        <div class="text-[14px] font-bold text-[white] mb-[8px]">
                            ID: {{ item.idText }}
                        </div>
                        <div v-if="item.prompt && item.prompt != ''" class="prompt-wrapper">
                            <div class="prompt-text multi-line-ellipsis">
                                {{ item.prompt }}
                            </div>
                            <el-icon :size="20" color="#6B7280" style="cursor: pointer;"
                                @click="() => copyText(item.prompt)">
                                <CopyDocument />
                            </el-icon>
                        </div>
                        <div class="time-wrapper">
                            <div class="create-time">创建时间: {{ item.createTimeText }}</div>
                        </div>
                        <div class="flex gap-[16px]">
                            <CellTag v-if="item.hp == true" text="高清" />
                            <div v-if="item.hp == false" class="flex gap-[16px]">
                                <!-- 模型标签 -->
                                <CellTag v-if="displayModel(item) != ''" :text="displayModel(item)" />
                                <!-- 比例标签 -->
                                <CellTag v-if="displayAspectRatio(item) != ''" :text="displayAspectRatio(item)" />

                            </div>

                        </div>
                        <div class="option-bar" v-if="item.model != 'ms'">
                            <!-- 下载按钮 -->
                            <!-- <img v-if="item.status == 2" class="option-btn" src="@/assets/images/ai-image-download-btn.png" -->
                            <!-- alt="" @click="downloadFile(displayImage(item))" /> -->

                            <!-- 下载按钮 -->
                            <div v-if="item.status == 2" class="option-btn-cus"
                                @click="downloadFile(item.resultImages[0])">
                                <img class="option-btn-icon" src="@/assets/images/ai-image-download-icon.png"
                                    alt=""><span>下载</span>
                            </div>


                            <!-- 高清 -->
                            <!-- <img v-if="item.hp == false && item.status == 2" class="option-btn ml-[16px]" -->
                            <!-- src="@/assets/images/ai-image-hp-btn.png" alt="" @click="sendImageHp(item)" /> -->
                            <div v-if="item.hp == false && item.status == 2" class="option-btn-cus"
                                @click="sendImageHp(item.idText, null)">
                                <img class="option-btn-icon" src="@/assets/images/ai-image-hp-icon.png"
                                    alt=""><span>高清</span>
                            </div>

                            <!-- 用作参考图 -->
                            <!-- <img class="option-btn not-visited:ml-[16px]"
                            src="@/assets/images/ai-image-use-template-btn.png" alt=""
                            v-if="item.hp == false && item.status == 2"
                            @click="useTemplate([displayImage(item)], item.prompt)" /> -->
                            <div v-if="item.hp == false && item.status == 2" class="option-btn-cus"
                                @click="useTemplate([item.resultImages[0]], item.prompt)">
                                <img class="option-btn-icon" src="@/assets/images/ai-image-template-icon.png"
                                    alt=""><span>用作参考图</span>
                            </div>

                            <!-- 多视角 -->
                            <div v-if="item.hp == false && item.status == 2" class="option-btn-cus"
                                @click="sendAiMultiScene(item)">
                                <img class="option-btn-icon" src="@/assets/images/ai-image-mutli-scene-icon.png" alt="">
                                <div>多视角</div>
                            </div>

                            <!-- 再次生成 -->
                            <!-- <img class="option-btn ml-[16px]" src="@/assets/images/ai-image-re-gen-btn.png" alt=""
                            @click="() => reGen(item)" v-if="item.hp == false" /> -->
                            <div v-if="item.hp == false" class="option-btn-cus" @click="reGen(item)">
                                <img class="option-btn-icon" src="@/assets/images/ai-image-re-gen-icon.png" alt="">
                                <div>再次生成</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 多视角 -->
                <div v-if="item.model == 'ms'" class="mutli-scene-cell-wrapper">
                    <div class="flex items-center h-[50px] gap-[16px]">
                        <div class="flex p-[8px] text-[#999999] text-[12px] bg-[#333333] rounded">{{ item.model }}</div>
                        <div class="text-[white]">ID: {{ item.id }}</div>
                    </div>
                    <div class="image-list-wrapper" v-if="item.status == 2">
                        <div class="item-wrapper"
                            v-for="image in [item.resultImages[0], item.resultImages[1], item.resultImages[2], item.resultImages[3]]"
                            :key="image">
                            <el-image class="task-image-item-wrapper" fit="contain" :src="displayImage(image)"
                                @click="currentMutliScene = item" />
                            <div class="mini-buttons-bar">
                                <img class="mini-btn" src="@/assets/images/ai-image-download-icon.png" alt=""
                                    @click="downloadFile(image)" />
                                <img class="mini-btn" src="@/assets/images/ai-image-hp-icon.png" alt=""
                                    @click="sendImageHp(item.idText, image)" />
                                <img class="mini-btn" src="@/assets/images/ai-image-template-icon.png" alt=""
                                    @click="useTemplate([image], '')" />
                            </div>
                        </div>
                        <!-- <div
                            class="flex items-center justify-center p-[8px] h-[44px] my-[auto] text-[#999999] text-[12px] bg-[#333333] rounded cursor-pointer">
                            显示更多</div> -->
                    </div>
                    <div class="image-list-wrapper" v-if="item.status < 2">
                        <img src="@/assets/images/ai-gen-loading2.gif" class="loading-image">
                    </div>
                    <div v-if="item.status == 3" class="status-tip">
                        <img class="gen-failure-image" src="@/assets/images/ai_gen_failure.png" />
                    </div>
                </div>
            </div>
        </div>
        <div class="form-wrapper">
            <AIGenPopForm @save-successs="commitSuccess" @show-privacy-pop="showPrivacyPop = true"
                ref="promptFormRef" />
        </div>

        <AIMutliSceneDetailPop v-if="currentMutliScene" :task-id="currentMutliScene.idText"
            :images="currentMutliScene.resultImages" @dismiss="currentMutliScene = null" @use-template="useTemplate"
            @send-image-hp="sendImageHp" />
    </div>
</template>

<script lang="ts" setup>
import { nextTick, onMounted, onUnmounted, ref } from 'vue';
import AIGenPopForm from './AIGenPopForm.vue';
import { aiTaskList, aiBatTaskDetail, commitAiHp, commitAiMultiScene } from '@/api/home/common.js'
import { ElMessage } from 'element-plus';
import { downloadFile } from '../../../utils/common';
import { useInfiniteScroll } from '@vueuse/core';
import AIMutliSceneDetailPop from './AIMutliSceneDetailPop.vue';
import { copyText } from '@/utils/common'
import CellTag from './components/CellTag.vue'

const props = defineProps(['defaultImages', 'defaultPrompt'])
const emit = defineEmits(['use-template'])

const promptFormRef = ref(null)

const currentMutliScene = ref(null)

const target = ref(null);
const hasMore = ref(true);
let page = 1
const pageSize = 20
const loading = ref(false)

let looping = false

const list = ref([])

// 用作参考图
const useTemplate = (urls, prompt) => {
    promptFormRef.value?.setFormData({
        prompt: prompt,
        refer_images: urls
    })
}

const displayImage = (url) => {
    return url + '?x-oss-process=image/resize,l_200'
}
const displayModel = (item) => {
    if (item.model == 'b') {
        return "模型一"
    } else if (item.model == 'b2') {
        return "模型二"
    }
    return ""
}
const displayAspectRatio = (item) => {
    let aspectRatio = item.aspectRatio ?? ""
    if (item.args && item.args.res && item.args.res.length > 0) {
        if (aspectRatio.length > 0) {
            aspectRatio += ' | '
        }
        aspectRatio += item.args.res
    }
    return aspectRatio
}

// 再次生成
const reGen = (item) => {
    promptFormRef.value?.setFormData({
        prompt: item.prompt,
        refer_images: item.referImages ?? [],
        modelType: item.model,
        aspectRatio: item.aspectRatio,
        res: item.args?.res,
    })
}

const sendImageHp = async (taskId, referImage) => {
    try {
        let refParams: String | null = null;
        if (referImage && referImage.length > 0) {
            refParams = JSON.stringify([referImage])
        }
        let res = await commitAiHp({ taskId: taskId, referImages: refParams })
        if (res.code === 10000) {
            ElMessage.success('任务已提交')
            // 刷新列表
            commitSuccess()
        } else {
            console.error('高清放大失败，', res.msg)
            ElMessage.error(res.msg)
        }
    } catch (err) {
        console.error('高清放大失败')
        ElMessage.error("高清放大失败")
    }
}

// 提交多视角
const sendAiMultiScene = async (item) => {
    try {
        let res = await commitAiMultiScene(item.idText)
        if (res.code === 10000) {
            ElMessage.success('任务已提交')
            // 刷新列表
            commitSuccess()
        } else {
            console.error('提交失败，', res.msg)
            ElMessage.error(res.msg)
        }
    } catch (err) {
        console.error('提交失败，')
        ElMessage.error("提交失败，")
    }
}

const commitSuccess = () => {
    page = 1
    queryList()
}

const queryList = async () => {
    try {
        let res = await aiTaskList({
            params: {
                pageNum: page,
                pageSize: pageSize,
            }
        })
        console.log('----res = ', res)
        if (res.code === 10000) {
            looping = false
            res.data.list.forEach(element => {
                element.referImages = JSON.parse(element.referImages)
                element.resultImages = JSON.parse(element.resultImages)
                element.args = JSON.parse(element.args)
            });
            if (res.data.list < pageSize) {
                hasMore.value = false
            }

            if (page == 1) {
                // 先清空
                list.value = []
                await nextTick()
                list.value = res.data.list
            } else {
                list.value.push(...res.data.list);
            }
            if (res.data.list.length > 0) {
                page = page + 1
            }
            // 没完成的，就单独获取进度
            if (looping == false) {
                refreshDetailIfNeed()
            }
            // console.log(list.value.map(t => t.prompt))

        } else {
            ElMessage.error('数据加载失败,' + res.msg)
            console.error('ai列表数据加载失败', res.msg)
        }
    } catch (err) {
        ElMessage.error('数据加载失败')
        console.error('ai列表数据加载失败', err)
    }
}

useInfiniteScroll(
    target,
    async () => {
        if (loading.value || !hasMore.value) return;
        await queryList();
    },
    { distance: 10 }
);

const refreshDetailIfNeed = async () => {
    looping = true
    let unFinishIdList = list.value.filter(t => t.status < 2).map(t => t.idText)
    if (unFinishIdList.length > 0) {
        requestDetail(unFinishIdList)
    } else {
        looping = false
    }
}
const requestDetail = async (ids) => {
    try {
        let res = await aiBatTaskDetail({ ids })
        if (res.code === 10000) {
            for (let r of res.data) {
                if (r.status == 2) {
                    for (let t of list.value) {
                        if (t.idText == r.idText) {
                            t.status = r.status
                            t.resultImages = JSON.parse(r.resultImages)
                        }
                    }
                }
            }

            // 3s后下一波调用
            setTimeout(() => {
                refreshDetailIfNeed()
            }, 3000);

        } else {
            // ElMessage.error('数据加载失败,' + res.msg)
            console.error('获取详情失败', res.msg)
            looping = false
        }
    } catch (err) {
        // ElMessage.error('数据加载失败')
        console.error('获取详情失败', err)
        looping = false
    }
    return false
}

onMounted(() => {
    queryList();
    promptFormRef.value?.setFormData({
        prompt: props.defaultPrompt ?? '',
        refer_images: props.defaultImages ?? []
    })
})

</script>

<style lang="scss" scoped>
$imageHeight: 220px;

.ai-gen-list-component {
    position: relative;
    width: 100%;
    height: 100%;
}

.ai-gen-task-list {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin: 20px;
    margin-left: 0;
    height: calc(100% - 55px - 20px * 2);
    width: 100%;
    overflow: hidden;
    overflow-y: auto;
    padding-bottom: 200px;
    box-sizing: border-box;

    .ai-gen-task-cell {
        display: flex;
        margin-bottom: 50px;
        width: 1000px;

        .normal-cell-wrapper {
            display: flex;

            .task-image-wrapper {
                display: flex;
                justify-content: center;
                align-items: center;
                width: calc($imageHeight * 4 / 3);
                height: $imageHeight;
                background-color: #252F41;
                flex-shrink: 0;

                .task-image {
                    position: relative;
                    width: 100%;
                    height: 100%;

                    .background-wrapper {
                        position: absolute;
                        width: 100%;
                        height: 100%;

                        .task-image-background {

                            left: 0;
                            top: 0;
                            width: 100%;
                            height: 100%;
                            filter: blur(10px);
                            /* 模糊半径设置为5像素 */
                            overflow: hidden;
                            /* 隐藏伪元素的模糊溢出 */
                        }
                    }

                    .task-image-fore {
                        width: 100%;
                        height: 100%;
                    }
                }

                .status-tip {
                    .gen-failure-image {
                        width: 150px;
                        object-fit: contain;
                    }
                }
            }

            .right-wrapper {
                flex-grow: 1;
                display: flex;
                flex-direction: column;
                margin-left: 30px;

                .prompt-wrapper {
                    display: flex;

                    .prompt-text {
                        font-size: 18px;
                        color: white;
                    }
                }

                .time-wrapper {
                    display: flex;
                    align-items: center;
                    margin-top: 13px;
                    font-size: 16px;
                    color: #6B7280;
                    width: 100%;
                    height: 20px;
                }

                .option-bar {
                    display: flex;
                    // width: calc(156px * 0.7);
                    height: calc(44px * 0.7);
                    margin-top: auto;
                    gap: 10px;

                    .option-btn-cus {
                        box-sizing: border-box;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        height: calc(44px * 0.7);
                        min-width: 80px;
                        padding: 0 14px;
                        cursor: pointer;
                        color: white;
                        font-size: 14px;
                        background-color: #364358;
                        border-radius: 4px;
                        gap: 8px;
                        width: auto;

                        .option-btn-icon {
                            width: 15px;
                            height: 15px;
                        }
                    }
                }
            }
        }

        .mutli-scene-cell-wrapper {
            display: flex;
            flex-direction: column;
            // margin-bottom: 10px;
            width: 1000px;
            // background-color: red;

            .image-list-wrapper {
                display: flex;
                gap: 16px;

                .item-wrapper {
                    position: relative;
                    width: $imageHeight;
                    height: $imageHeight;

                    .task-image-item-wrapper {
                        display: flex;
                        width: 100%;
                        height: 100%;
                        background-color: #252F41;
                        flex-shrink: 0;
                        margin-right: 10px;
                        cursor: pointer;
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

                .loading-image {
                    width: calc($imageHeight * 4 / 3);
                    height: $imageHeight;
                }

                .gen-failure-image {
                    width: 150px;
                    object-fit: contain;
                }
            }
        }
    }
}

.multi-line-ellipsis {
    display: -webkit-box;
    -webkit-line-clamp: 4;
    /* 限制文本显示的行数 */
    -webkit-box-orient: vertical;
    line-height: 20px;
    overflow: hidden;
    text-overflow: ellipsis;
}

.form-wrapper {
    position: absolute;
    bottom: 20px;
    width: 100%;
    display: flex;
    justify-content: center;
}
</style>