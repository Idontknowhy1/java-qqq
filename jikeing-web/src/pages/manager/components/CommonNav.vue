<template>
  <div class="common_nav">
    <div class="title">
      <div class="main">{{ title }}</div> &nbsp;
      <div class="child" v-if="childTitle"> - {{ childTitle }}</div>
    </div>
    <div class="back" v-if="backTitle && appPathObj">
      <el-button type="danger" size="medium" :icon="Back" round @click="appToPath()" plain>{{ backTitle }}</el-button>
    </div>
    <div class="back" v-if="typeof routerPath.path != 'undefined'">
      <router-link :to="routerPath">
        <el-button type="danger" size="medium" round plain :icon="Back">返回</el-button>
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">

import { defineProps, defineEmits } from 'vue'
import { useRouter } from 'vue-router'

import {
    Back
} from '@element-plus/icons-vue'

const router = useRouter()
const emit = defineEmits(['event'])

// 定义组件的属性
defineProps({
  title: {
    type: String,
    default: '',
  },
  childTitle: {
    type: String,
    default: '',
  },
  backTitle: {
    type: String,
    default: '',
  },
  appPathObj: {
    type: Object,
    default: () => ({}) // 使用工厂函数返回新对象
  },
  routerPath: {
    type: Object,
    default: () => ({})
  },
  icon: {
    type: String,
    default: '',
  }
})

// 处理返回按钮点击
const handleBack = () => {
  if (routerPath.path && routerPath.query) {
    appToLink(routerPath.path, routerPath.query)
  } else {
    // 如果未指定路由，默认返回上一页
    router.go(-1)
  }
}

// 路径跳转方法
const appToLink = (path, query) => {
  if (!path) return
  
  router.push({
    path: path,
    query: query || {}
  })
}

// 处理操作按钮点击
const appToPath = () => {
  const { appPathObj } = props
  if (appPathObj && appPathObj.type === 'emit') {
    // 触发自定义事件
    emit('event', appPathObj.param)
  } else if (appPathObj && appPathObj.common_path) {
    // 路由跳转
    appToLink(appPathObj.common_path, appPathObj.query)
  }
}

</script>

<style scoped>

</style>