<template>
  <div class="content">
    <el-dialog :title="title" width="1050px" class="icon-dialog" top="5vh" v-model="show" @open="open"
      :before-close="closeForm">
      <div class="breadcrumb">
        <span class="item" @click="nextList({ path: '/' })">
          全部文件<span class="line" v-if="breadcrumb.length > 0">/</span>
        </span>
        <span class="item" v-for="(item, key) in breadcrumb" :key="key" @click="nextList({ path: item.path })">
          {{ item.name }}<span class="line" v-if="breadcrumb.length - 1 > key">/</span>
        </span>
      </div>
      <div class="data-box">

        <div class="data-list" v-for="(item, key) in list" :key="key">
          <div class="checkbox" v-if="(item.category == 1 || item.category == 2 || item.category == 3
            || item.category == 4 || item.category == 6) && item.isdir == 0">
            <el-checkbox v-model="item.checked" @change="setCheck(item, key)"></el-checkbox>
          </div>
          <div class="item" :class="{ dir: item.isdir == 1 }">
            <div v-if="item.isdir == 1">
              <div @click="nextList(item)"><el-image :src="icon_list[60]" class="icon"> </el-image></div>
            </div>
            <div v-else>
              <el-image :src="item.thumbs.url1" v-if="item.category == 3" class="thumbs"> </el-image>
              <el-image :src="icon_list[item.category]" v-else class="icon"> </el-image>
            </div>
            <div class="name">{{ item.server_filename }}</div>
          </div>
        </div>
      </div>
      <div class="page">
        <Page :total="page_data.total" :page.sync="page_data.page" :limit.sync="page_data.page_size"
          @pagination="baidupanList" />
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" size="medium" @click="submit">确定</el-button>
        <el-button size="medium" @click="closeForm">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>

import { baidupanList } from "@/api/manager/baidupan";

import { ref, reactive, computed, watch } from 'vue'
// import { Folder } from '@element-plus/icons-vue'
// import { formatFileSize, formatDate } from '@/utils/format'

// 定义组件属性
const props = defineProps({
  show: {
    type: Boolean,
    default: false
  }
})

const show = computed({
  get: () => props.show,
  set: (val) => emit('update:show', val)
})

// 定义组件事件
const emit = defineEmits(['update:show', 'callfn'])

// 响应式状态
const visible = computed({
  get: () => props.show,
  set: (val) => emit('update:show', val)
})

const title = ref('选择网盘文件')
const list = ref([])
const loading = ref(false)
const breadcrumb = ref([])
const checkedId = ref('')
const checkedCategoryId = ref('')
const checkedName = ref('')

const page_data = reactive({
  page: 1,
  page_size: 20,
  dir: '',
  total: 0
})

// 图标映射
const icon_list = {
  1: new URL('@/assets/images/video-icon.png', import.meta.url).href,
  2: new URL('@/assets/images/audio-icon.png', import.meta.url).href,
  3: new URL('@/assets/images/image-icon.png', import.meta.url).href,
  4: new URL('@/assets/images/doc-icon.png', import.meta.url).href,
  5: new URL('@/assets/images/app-icon.png', import.meta.url).href,
  6: new URL('@/assets/images/yasu-icon.png', import.meta.url).href,
  7: new URL('@/assets/images/bt-icon.png', import.meta.url).href,
  60: new URL('@/assets/images/folder-icon.png', import.meta.url).href
}

// 获取文件类型图标
const getIcon = (category) => {
  return icon_list[category] || icon_list[60] // 默认使用文件夹图标
}

// 提交选择
const submit = () => {
  if (!checkedId.value) {
    ElMessage.error('请选择文件')
    return false
  }

  emit('callfn', {
    fs_id: checkedId.value,
    category: checkedCategoryId.value,
    checkedName: checkedName.value
  })

  closeForm()
}

// 进入下一级目录
const nextList = (item) => {
  if (item.path) {
    const paths = item.path.split('/')
    const name = paths[paths.length - 1]

    // 查找是否已存在该路径
    const existingIndex = breadcrumb.value.findIndex(b => b.name === name)

    if (existingIndex !== -1) {
      // 如果已存在，截断到该位置
      breadcrumb.value = breadcrumb.value.slice(0, existingIndex + 1)
    } else {
      // 添加新路径
      breadcrumb.value.push({ path: item.path, name })
    }

    page_data.dir = item.path
  } else {
    // 返回根目录
    breadcrumb.value = []
    page_data.dir = ''
  }

  fetchBaidupanList()
}

// 返回根目录
const goToRoot = () => {
  breadcrumb.value = []
  page_data.dir = ''
  fetchBaidupanList()
}

// 返回指定路径
const goToPath = (path, index) => {
  breadcrumb.value = breadcrumb.value.slice(0, index + 1)
  page_data.dir = path
  fetchBaidupanList()
}

// 选择文件
const setCheck = (item) => {
  checkedId.value = item.fs_id
  checkedCategoryId.value = item.category
  checkedName.value = item.name
}

// 行点击事件
const handleRowClick = (row) => {
  setCheck(row)
}

// 分页变化
const handlePageChange = (page) => {
  page_data.page = page
  fetchBaidupanList()
}

// 获取百度网盘文件列表
const fetchBaidupanList = async () => {
  try {
    loading.value = true
    const res = await baidupanList({ params: page_data })

    if (res.code === 200) {
      list.value = res.data.list.map(item => ({
        ...item,
        checked: false
      }))
      page_data.total = res.data.total
    }
  } catch (error) {
    console.error('获取网盘列表失败:', error)
    ElMessage.error('获取网盘列表失败')
  } finally {
    loading.value = false
  }
}

// 打开对话框
const open = () => {
  // 重置选择
  checkedId.value = ''
  checkedCategoryId.value = ''
  checkedName.value = ''

  // 初始化路径
  breadcrumb.value = []
  page_data.dir = ''
  page_data.page = 1

  fetchBaidupanList()
}

// 关闭对话框
const closeForm = () => {
  visible.value = false
}

</script>

<style scoped lang="scss">
::v-deep .el-image__inner {
  border-radius: 4px;
}

.breadcrumb {
  margin: 0px 0;

  .item {
    line-height: 40px;
    margin: 0 10px 0 0;
    cursor: pointer;

    .line {
      color: #c0c4cc;
      margin: 0 0 0 10px;
    }
  }

}

.data-box {
  display: flex;
  justify-content: flex-start;
  flex-wrap: wrap;

  .data-list {
    position: relative;
    width: 10%;
    //height: 100px;
    border: 0px solid #ff0000;
    margin: 10px 15px 10px 15px;
    padding: 10px 0px 10px 0px;
    text-align: center;
    cursor: pointer;

    .checkbox {
      position: absolute;
      top: 2px;
      right: 2px;
    }

    .item {
      .icon {
        width: 40px;
        height: 40px;
      }

      .thumbs {
        width: 50px !important;
        height: 50px !important;
      }

      .name {
        font-size: 14px;
        margin: 10px auto 0;
        color: #333;
        white-space: normal;
        /* 允许换行 */
        overflow-wrap: break-word;
      }
    }
  }

  .data-list:hover {
    background: #fff;
    border-radius: 6px;
  }

}
</style>