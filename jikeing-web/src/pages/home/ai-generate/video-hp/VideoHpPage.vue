<template>
    <div class="relative flex flex-col w-full h-full box-sizing: border-box p-[20px] gap-[20px]">
        <div class="flex items-center justify-start h-[50px] px-[20px]">
            <button class="flex items-center gap-2 px-6 py-2.5 rounded-full bg-gradient-to-r from-blue-600 to-indigo-600
                hover:from-blue-500 hover:to-indigo-500 text-white shadow-lg shadow-blue-900/20 hover:shadow-blue-900/40
                transition-all text-sm font-medium group" @click="showUploadDialog = true">
                <span>新建高清任务</span>
            </button>
        </div>
        <div class="flex items-start content-start flex-wrap overflow-auto gap-[16px]">
            <VideoHpCell v-for="item in list" :key="item.idText" :item="item" @playVideo="playVideo" />
            <div class="pagination">
                <el-pagination :page-size="pageInfo.pageSize" background layout="prev, pager, next"
                    @current-change="handleCurrentChange" :total="pageInfo.total" hide-on-single-page>
                </el-pagination>
            </div>
        </div>

        <!-- 视频播放模态框 -->
        <Teleport to="body">
            <VideoPlayer v-if="showVideo" :playing-video-url="playingVideoUrl" @setShowVideo="showVideo = false" />
        </Teleport>

        <UploadDialog v-if="showUploadDialog" ref="uploadDialogRef" @commitSuccess="commitSuccess"
            @close="showUploadDialog = false" />
    </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import VideoHpCell from './components/VideoHpCell.vue'
import { videoHpTaskList, aiBatTaskDetail } from '@/api/home/common'
import { ElMessage } from 'element-plus'
import VideoPlayer from '../../../../components/VideoPlayer.vue'
import UploadDialog from './components/UploadDialog.vue'

const showVideo = ref(false)
const playingVideoUrl = ref('')
const showUploadDialog = ref(false)

let looping = false

const list = ref([])

const pageInfo = ref({
    pageNum: 1,
    pageSize: 10,
    total: 0
})

const handleCurrentChange = (val) => {
    pageInfo.value.pageNum = val
    requestList(val)
}

const handerTaskStatus = (item) => {
    item.argsJSON = JSON.parse(item.args ?? "{}")
    item.outputsJSON = JSON.parse(item.outputs || "[]") ?? []

    if (item.status === 2) {
        item.outputsJSON = JSON.parse(item.outputs)
        item.outputJSON = item.outputsJSON[0]
    }
    else if (item.status <= 1) {
        item.outputsJSON.push({ type: "", status: 1, value: null })
    }

    item.hasReferImage = item.referImages && item.referImages.length > 0
}

const requestList = async (pageNum: number) => {
    try {
        const res = await videoHpTaskList({ pageNum: pageNum, pageSize: pageInfo.value.pageSize, })
        if (res.code === 10000) {
            res.data.list.forEach(item => {
                handerTaskStatus(item)
            })
            list.value = res.data.list || []
            pageInfo.value.total = res.data.totalCount || 0
            if (looping == false) {
                refreshDetailIfNeed()
            }
        } else {
            ElMessage.error(res.msg || '获取列表失败')
        }
    } catch (error) {
        console.error(error)
        ElMessage.error('获取列表失败')
    }
}

const refreshDetailIfNeed = async () => {
    looping = true
    let unFinishIdList = list.value.filter(t => t.outputsJSON.filter(o => o.status == 1).length > 0).map(t => t.idText)
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
                list.value.forEach((t, index) => {
                    if (t.idText == r.idText) {
                        handerTaskStatus(r)
                        list.value[index] = r
                    }
                })
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

const playVideo = (url: string) => {
    playingVideoUrl.value = url
    showVideo.value = true
}

const commitSuccess = () => {
    requestList(0)
    showUploadDialog.value = false
}

onMounted(() => {
    requestList(0)
})

</script>
<style scoped>
.drawer {
    position: absolute;
    top: 0;
    right: 0;
    width: calc(100vw*0.6);
    height: calc(100vh-55px);
    background-color: color-mix(in srgb, #fff 50%, #000 50%);
    box-shadow: -2px 0 8px rgba(0, 0, 0, 0.15);
    box-sizing: border-box;
}

/* 定义进入和离开动画的激活状态 */
.slide-enter-active,
.slide-leave-active {
    transition: transform 0.3s ease-out;
}

/* 定义进入开始和离开结束时的状态（从右侧进入，向右移出） */
.slide-enter-from,
.slide-leave-to {
    transform: translateX(100%);
}
</style>
