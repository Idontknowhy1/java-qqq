<template>
    <div class="relative flex items-center w-[calc(100%-50px)] h-full gap-[20px] box-sizing: border-box px-[20px]">
        <div class="w-[350px] shrink-0 h-[calc(100vh-86px)] overflow-auto ">
            <VideoGenerateForm ref="formRef" @commitSuccess="requestList(1)"
                @showCharacterList="showCharacterList = true" />
        </div>
        <div class="flex flex-col h-full w-max-[800px] overflow-auto gap-[30px]">
            <VideoTaskCell v-for="item in list" :key="item.idText" :task="item" @playVideo="playVideo"
                @makeSame="makeSame" @handleUpscale="handleUpscale" />
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

        <!-- 角色卡遮照层 -->
        <div v-if="showCharacterList" class="absolute top-0 left-0 w-full h-[calc(100vh-55px)]"
            @click="showCharacterList = false">
        </div>
        <transition name="slide">
            <div v-if="showCharacterList" class="drawer">
                <CharacterList @dismiss="showCharacterList = false" />
            </div>
        </transition>
    </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import VideoGenerateForm from './components/VideoGenerateForm.vue'
import VideoTaskCell from './components/VideoTaskCell.vue'
import { soraTaskList, aiBatTaskDetail, commitVideoTaskHp } from '@/api/home/common'
import { ElMessage } from 'element-plus'
import CharacterList from './components/characters/CharatcorList.vue'
import VideoPlayer from '../../../../components/VideoPlayer.vue'
import { fr } from 'date-fns/locale'

const showVideo = ref(false)
const playingVideoUrl = ref('')

const formRef = ref(null)

// 显示角色列表
const showCharacterList = ref(false)

let looping = false

const list = ref([])

const pageInfo = ref({
    pageNum: 1,
    pageSize: 10,
    total: 0
})

const makeSame = async (task) => {
    formRef.value?.setFormData(task)
}
// 高清放大
const handleUpscale = async (task, card) => {
    try {
        const res = await commitVideoTaskHp({ taskId: task.idText, args: JSON.stringify({ video: card.value }) })
        if (res.code === 10000) {
            task = res.data
            requestList(0)
            ElMessage.success('高清放大成功')
        } else {
            ElMessage.error(res.msg || '高清放大失败')
        }
    } catch (error) {
        console.error(error)
        ElMessage.error('高清放大失败')
    }
}

const handleCurrentChange = (val) => {
    pageInfo.value.pageNum = val
    requestList(val)
}

const handerTaskStatus = (item) => {
    item.argsJSON = JSON.parse(item.args ?? "{}")
    item.outputsJSON = JSON.parse(item.outputs || "[]") ?? []

    let number = item.argsJSON.number || 1

    if (item.referImages && item.referImages.length > 0) {
        item.referImagesJSON = JSON.parse(item.referImages)
        item.referImage = item.referImagesJSON[0]
    }
    if (item.status === 2) {
        item.outputsJSON = JSON.parse(item.outputs)
        item.outputsJSON.forEach((value, index) => {
            value.index = index
        })
    }
    // else if (item.status === 3) {
    //     for (let i = 0; i < number; i++) {
    //         item.outputsJSON.push({ type: "", status: 3, value: null })
    //     }
    // } 
    else if (item.status <= 1) {
        for (let i = 0; i < number; i++) {
            item.outputsJSON.push({ type: "", status: 1, value: null })
        }
    }

    item.hasReferImage = item.referImages && item.referImages.length > 0
}

const requestList = async (pageNum: number) => {
    try {
        const res = await soraTaskList({ pageNum: pageNum, pageSize: pageInfo.value.pageSize, })
        if (res.code === 10000) {
            res.data.list.forEach(item => {
                handerTaskStatus(item)
            })
            console.log('-----res.data', res.data)
            list.value = res.data.list || []
            pageInfo.value.total = res.data.totalCount || 0
            if (looping == false) {
                refreshDetailIfNeed()
            }
        } else {
            ElMessage.error(res.msg || '获取视频列表失败')
        }
    } catch (error) {
        console.error(error)
        ElMessage.error('获取视频列表失败')
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
        let res = await aiBatTaskDetail('/sorotask/v1/bat-tasks-detail', { ids })
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
