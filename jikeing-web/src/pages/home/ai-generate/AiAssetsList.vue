<template>
  <div class="ai-assets-list-components">
    <div class="top-button-list">
      <div class="type-text" :class="{ selected: selectedType == seg.tag }" @click="segmentChange(seg.tag)"
        v-for="seg in segments" :key="seg.tag">{{ seg.title }}</div>
    </div>

    <div class="list-wrapper">
      <div class="image-section" v-for="sect in list" :key="sect.title">
        <div class="section-title">{{ sect.title }}</div>
        <div class="image-list-wrapper">
          <div class="item-wrapper" v-for="item in sect.items" :key="item.name">
            <div class="image-wrapper">

              <!-- AI生图 -->
              <el-image v-if="selectedType == 'generate'" :src="displayImage(item.imageUrl)" alt="" class="image"
                fit="contain" @click="showAiTaskDetail(item)" />

              <!-- 上传拆分 -->
              <div v-else-if="selectedType == 'upload-split'" class="upload-split-item-wrapper">
                <el-image :src="item.imageUrl" alt="" class="image" fit="cover" />
                <div v-if="item.state == 2" class="choose-btn always" @click="downloadFile(item.resultUrl)">下载PSD
                </div>
                <div v-if="item.state == 0" class="status-btn always">待审核</div>
                <div v-if="item.state == 1" class="status-btn always">制作中</div>

                <div v-if="item.state == 3" class="status-btn" style="color: red;">拒绝
                  <el-tooltip effect="dark" :content="item.reason" :raw-content="true">
                    <div style="cursor: pointer;">(查看原因)</div>
                  </el-tooltip>
                </div>
              </div>

              <!-- 角色 -->
              <div v-else style="width: 100%; height: 100%;" class="role-image-wrapper">
                <el-image v-if="item.miniUrl" :src="item.miniUrl" alt="" class="image" :preview-src-list="[item.url]"
                  fit="cover" />
                <el-image v-else :src="item.url" alt="" class="image" :preview-src-list="[item.url]" fit="cover" />
                <div class="choose-btn" @click="useTemplate(item.url, '')">立即使用</div>
              </div>
            </div>
            <span class="image-name multi-line-ellipsis">{{ item.name }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination" v-if="selectedType == 'generate' || selectedType == 'upload-split'">
      <el-pagination :page-size="pageInfo.page_size" background layout="prev, pager, next"
        @current-change="handleCurrentChange" :total="pageInfo.total">
      </el-pagination>
    </div>

    <TaskDetailPop v-if="showAiTaskPop" :showUploadSplit="true" @use-template="useTemplate" @make-same="makeSame"
      :data="currentAiTask" @dismiss="showAiTaskPop = false" />

  </div>
</template>

<script setup lang="ts">
import { uploadSplitList, aiTaskList } from '@/api/home/common.js'
import { ElMessage } from 'element-plus';
import { onMounted, ref } from 'vue';
import { roleList } from '@/api/roleList.js'
import { actionList } from '@/api/actionList.js'
import TaskDetailPop from './components/TaskDetailPop.vue';
import { downloadFile } from '@/utils/common.ts'

const selectedType = ref('generate')

const props = defineProps(['selectedSeg'])
const emit = defineEmits(['use-template', 'make-same'])

const showAiTaskPop = ref(false)
const currentAiTask = ref(null)

const useTemplate = (url, prompt) => {
  emit('use-template', url, prompt)
}

const makeSame = (refreImages, prompt) => {
  emit('make-same', refreImages, prompt)
}

const showAiTaskDetail = (task) => {
  currentAiTask.value = task
  showAiTaskPop.value = true
}

// 分页数据
const pageInfo = ref({
  pageNum: 1,
  pageSize: 20,
  total: 0
})

const displayImage = (url) => {
  return url + '?x-oss-process=image/resize,l_200'
}

const handleCurrentChange = (val) => {
  pageInfo.value.pageNum = val
  queryAiGenList()
}

const segments = [{
  title: '生成图片',
  tag: 'generate'
}, {
  title: '角色',
  tag: 'role'
}, {
  title: '动作',
  tag: 'actions'
}, {
  title: '表情',
  tag: 'emotion'
}, {
  title: '场景',
  tag: 'scene'
}, {
  title: '上传拆分',
  tag: 'upload-split'
}]

const list = ref([])

/**
 * 获取AI生成的图片
 */
const queryAiGenList = async () => {
  try {
    let res = await aiTaskList({ params: pageInfo.value })
    console.log('----res = ', res)
    if (res.code === 10000) {
      if (selectedType.value != 'generate') {
        return
      }
      pageInfo.value.total = res.data.totalCount
      // 对数据进行按日期分类
      let sections = []
      let dateMap = {}
      res.data.list.forEach(element => {
        element.referImages = JSON.parse(element.referImages)
        element.resultImages = JSON.parse(element.resultImages)
      });
      for (let item of res.data.list) {
        if (item.status != 2) {
          continue
        }
        let date = item.createTimeText.substr(0, 10)
        let items = dateMap[date]
        if (!items) {
          items = []
          dateMap[date] = items
          sections.push({
            title: date,
            items: items
          })
        }
        items.push({
          imageUrl: item.resultImages[0],
          prompt: item.prompt
        })
      }
      list.value = sections
    } else {
      ElMessage.error('数据加载失败,' + res.msg)
      console.error('ai列表数据加载失败', res.msg)
    }
  } catch (err) {
    ElMessage.error('数据加载失败')
    console.error('ai列表数据加载失败', err)
  }
}

/**
 * 获取上传拆分列表
 */
const queryUploadSplitList = async () => {
  try {
    let res = await uploadSplitList({ params: pageInfo.value })
    console.log('----res = ', res)
    if (res.code === 10000) {
      if (selectedType.value != 'upload-split') {
        return
      }
      pageInfo.value.total = res.data.totalCount
      // 对数据进行按日期分类
      let sections = []
      let dateMap = {}
      for (let item of res.data.list) {
        let date = item.createTimeText.substr(0, 10)
        let items = dateMap[date]
        if (!items) {
          items = []
          dateMap[date] = items
          sections.push({
            title: date,
            items: items
          })
        }
        items.push({
          imageUrl: item.imageUrl,
          name: item.title,
          state: item.state,
          resultUrl: item.resultUrl,
          reason: item.reason
        })
      }
      list.value = sections
    } else {
      ElMessage.error('数据加载失败,' + res.msg)
      console.error('列表数据加载失败', res.msg)
    }
  } catch (err) {
    ElMessage.error('数据加载失败')
    console.error('列表数据加载失败', err)
  }
}

/**
 * 查询角色列表
 */
const queryRoleList = async () => {
  list.value = roleList
}

/**
 * 查询角色列表
 */
const queryActionList = async () => {
  list.value = actionList
}

/**
 * segment切换
 */
const segmentChange = (seg) => {
  list.value = []
  selectedType.value = seg
  if (seg == 'generate') {
    queryAiGenList()

  } else if (seg == 'role') {
    queryRoleList()

  } else if (seg == 'actions') {
    queryActionList()

  } else if (seg == 'emotion') {
  } else if (seg == 'scene') {
  } else if (seg == 'upload-split') {
    pageInfo.value.page = 1
    queryUploadSplitList()
  }
}

onMounted(() => {
  if (props.selectedSeg) {
    selectedType.value = props.selectedSeg
    segmentChange(props.selectedSeg)
  } else {
    segmentChange('generate')
  }
})

</script>

<style lang="scss" scoped>
.ai-assets-list-components {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  padding: 20px;
  box-sizing: border-box;

  .top-button-list {
    height: 40px;
    display: flex;

    .type-text {
      font-size: 18px;
      color: #6B7280;
      margin-right: 20px;
      cursor: pointer;
    }

    .type-text.selected {
      color: white;
    }
  }

  .list-wrapper {
    width: 100%;
    height: 100%;
    overflow: hidden;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    margin-top: 20px;

    .image-section {
      margin-bottom: 20px;

      .section-title {
        font-size: 26px;
        color: white;
        font-weight: 600;
      }


      .image-list-wrapper {
        display: flex;
        flex-wrap: wrap;
        margin-top: 16px;

        .item-wrapper {
          color: white;
          margin-bottom: 20px;
        }

        .image-wrapper {
          position: relative;
          width: 180px;
          height: calc(180px * 4 / 3);
          margin-bottom: 16px;
          margin-right: 16px;
          background-color: #666;
          display: flex;
          justify-content: center;
          align-items: center;
          border-radius: 12px;

          .image {
            width: 100%;
            height: 100%;
            border-radius: 12px;
          }

          .btn-wrapper {
            position: absolute;
            bottom: 10px;
            right: 10px;
            display: flex;
            background-color: #666;
            padding: 10px;
            border-radius: 10px;

            .button {
              width: 22px;
              height: 22px;
              object-fit: contain;
              cursor: pointer;
            }
          }
        }
      }

      .upload-split-item-wrapper {
        width: 100%;
        height: 100%;

        .image {
          width: 100%;
          height: 100%;
          border-radius: 12px;
        }
      }
    }

    .image-name {
      width: 180px;
      overflow: hidden;
    }


    .choose-btn {
      visibility: hidden;
      display: flex;
      justify-content: center;
      align-items: center;
      position: absolute;
      bottom: 0;
      background: linear-gradient(to right, #35C9FC, #0708ED);
      width: 100%;
      height: 30px;
      font-size: 14px;
      cursor: pointer;
      border-top-left-radius: 10px;
      border-top-right-radius: 10px;
    }

    .choose-btn.always {
      visibility: visible;
    }

    .status-btn {
      display: flex;
      justify-content: center;
      align-items: center;
      position: absolute;
      bottom: 0;
      background-color: #333333;
      width: 100%;
      height: 30px;
      font-size: 14px;
    }

    .multi-line-ellipsis {
      display: -webkit-box;
      -webkit-line-clamp: 1;
      /* 限制文本显示的行数 */
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }

  .role-image-wrapper:hover :deep(.choose-btn) {
    visibility: visible !important;
    ;
  }

}
</style>