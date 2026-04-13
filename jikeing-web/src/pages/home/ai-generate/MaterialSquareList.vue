<template>
  <div class="this-components">
    <div class="top-button-list">
      <div class="type-text" :class="{ selected: selectedType == seg.tag }" @click="segmentChange(seg.tag)"
        v-for="seg in segments" :key="seg.tag">{{ seg.title }}</div>
    </div>

    <div class="list-wrapper" ref="target">
      <div class="item-wrapper" v-for="item in list" :key="item.id" @click="showTaskDetail(item)">
        <el-image :src="item.imageUrl" alt="" class="image" fit="cover" />
      </div>
      <!-- 加载状态提示 -->
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="!hasMore" class="no-more">没有更多数据了</div>
    </div>

    <TaskDetailPop v-if="showTaskPop" @use-template="useTemplate" @make-same="makeSame" :data="currentTask"
      @dismiss="showTaskPop = false" />

  </div>

</template>

<script setup lang="ts">
import { apiMaterailSquareList } from '@/api/home/materialSquare.js'
import { ElMessage } from 'element-plus';
import { onMounted, ref } from 'vue';
import TaskDetailPop from './components/TaskDetailPop.vue';
import { useInfiniteScroll } from '@vueuse/core';

const selectedType = ref('material-square')
const loading = ref(false)

const emit = defineEmits(['use-template', 'make-same'])

const currentTask = ref(null)
const showTaskPop = ref(false)

const list = ref([])
const target = ref(null);
const hasMore = ref(true);
const page = ref(1)
const pageSize = 50

const segments = [{
  title: '探索发现',
  tag: 'material-square'
}]

const useTemplate = (url, prompt) => {
  emit('use-template', url, prompt)
}

const makeSame = (refreImages, prompt) => {
  emit('make-same', refreImages, prompt)
}

const showTaskDetail = (task) => {
  currentTask.value = task
  showTaskPop.value = true
}

const queryList = async () => {
  try {
    loading.value = true
    let res = await apiMaterailSquareList({ params: { pageNum: page.value, pageSize: pageSize } })
    loading.value = false
    if (res.code === 10000) {
      list.value.push(...res.data.list);
      if (res.data.list < pageSize) {
        hasMore.value = false
      }
      if (res.data.list.length > 0) {
        page.value += 1
      }
    } else {
      ElMessage.error('数据加载失败,' + res.msg)
      console.error('列表数据加载失败', res.msg)
    }
  } catch (err) {
    loading.value = false
    ElMessage.error('数据加载失败')
    console.error('列表数据加载失败', err)
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

onMounted(() => {
  queryList()
})

</script>

<style lang="scss" scoped>
.this-components {
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
    display: flex;
    flex-wrap: wrap;
    align-items: flex-start;
    align-content: flex-start;
    width: 100%;
    height: calc(100% - 55px - 20px * 2);
    overflow: hidden;
    overflow-y: auto;
    display: flex;
    margin-top: 20px;

    .item-wrapper {
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
    }

  }
}

.detail-pop-drawer {
  background-color: #111827;
}

.loading,
.no-more {
  display: block;
  text-align: center;
  padding: 20px;
  color: #666;
  width: 100%;
}
</style>