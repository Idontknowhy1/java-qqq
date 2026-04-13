<template>
  <div class="relative">
    <div class="w-[100vw] h-[100vh] overflow-hidden overflow-y-auto relative">
      <!-- 返回按钮 -->
      <img class="absolute w-[30px] left-[20px] top-[20px] z-10 cursor-pointer" :src="backBtnImage" @click="goBack" />

      <div class="flex flex-col items-center bg-[black] w-[100vw] relative">
        <!-- 顶部Banner图片 -->
        <img class="w-full" :src="topBannerImage" />

        <!-- 大赛介绍 -->
        <img class="w-[512px] mt-[148px]" :src="introduceTitleImage" alt="" />

        <img class="w-[1100px] mt-[80px]" :src="introduceTableImage" />

        <!-- 大赛奖项 -->
        <img class="w-[512px] mt-[148px]" :src="rewardTitleImage" />
        <img class="w-[1100px] mt-[80px]" :src="rewardTableImage" />

        <!-- 第一赛季十强王者 -->
        <img class="w-[804px] mt-[148px]" :src="rankSectionOneTitleImage" />
        <div class="h-[200px] mt-[108px] flex justify-center items-center">
          <img class="w-[155px]" :src="rankLeftLeafImage" />
          <RankVideoCard :data="cardInfo" :show-play-btn="true" @card-tap="playVideo" :is-large="true" />
          <img class="w-[155px]" :src="rankRightLeafImage" />
        </div>
        <div class="w-[1100px] mt-[80px] flex flex-wrap justify-between">
          <RankVideoCard v-for="item in sectionList" :key="item.name" :data="item" @card-tap="playVideo" />
        </div>

        <!-- 第一赛季潜力新秀 -->
        <img class="w-[804px] h-[108px] mt-[148px]" :src="rankSectionTwoTitleImage" />
        <div class="w-[1100px] mt-[80px] flex flex-wrap justify-between">
          <RankVideoCard v-for="item in sectionTwoList" :key="item.name" :data="item" @card-tap="playVideo" />
        </div>

        <!-- 联系我们 -->
        <img class="w-[512px] h-[108px] mt-[148px]" :src="rankContactTitleImage" />
        <img class="w-[400px] mt-[106px] mb-[200px]" :src="rankContactQrCodeImage" />
      </div>
    </div>

    <!-- 视频播放模态框 -->
    <VideoPlayer v-if="showVideo" :playing-video-url="playingVideoUrl" @setShowVideo="setShowVideo" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import topBannerImage from '@/assets/images/rank-top-banner.png'
import introduceTitleImage from '@/assets/images/rank-introduce-title.png'
import introduceTableImage from '@/assets/images/rank-introduce-table.png'
import rewardTitleImage from '@/assets/images/rank-reward-title.png'
import rewardTableImage from '@/assets/images/rank-reward-table.png'

import rankSectionOneTitleImage from '@/assets/images/rank-section-1-title.png'
import rankSectionTwoTitleImage from '@/assets/images/rank-section-2-title.png'
import rankContactTitleImage from '@/assets/images/rank-contact-us-title.png'
import rankContactQrCodeImage from '@/assets/images/rank-contact-us-qr-code.png'

import backBtnImage from '@/assets/images/rank-page-back-btn.png'

import rankLeftLeafImage from '@/assets/images/rank-left-leaf.png'
import rankRightLeafImage from '@/assets/images/rank-right-leaf.png'
import RankVideoCard from './RankVideoCard.vue'
import { useRouter } from 'vue-router'
import VideoPlayer from '@/components/VideoPlayer.vue'

const router = useRouter()

// 定义类型
interface RankCard {
  avatar: string
  cover: string
  video: string
  name: string
}

// 响应式数据
const showVideo = ref(false)
const playingVideoUrl = ref('')

// 生成卡片信息的函数
const generateCardInfo = (name: string): RankCard => ({
  avatar: 'https://jikeing.oss-cn-hangzhou.aliyuncs.com/rank/202511/' + name + '/avatar.png',
  cover: 'https://jikeing.oss-cn-hangzhou.aliyuncs.com/rank/202511/' + name + '/cover.png',
  video: 'https://jikeing.oss-cn-hangzhou.aliyuncs.com/rank/202511/' + name + '/video.mp4',
  name: name
})

// 计算属性
const cardInfo = computed(() => generateCardInfo('花间一壶酒'))

const sectionList = ref<RankCard[]>([
  generateCardInfo('无畏'),
  generateCardInfo('兔兔红了眼'),
  generateCardInfo('小勤'),
  generateCardInfo('明晚'),
  generateCardInfo('叶'),
  generateCardInfo('小峰'),
  generateCardInfo('富贵'),
  generateCardInfo('忽然之间'),
  generateCardInfo('夏天'),
])

const sectionTwoList = ref<RankCard[]>([
  generateCardInfo('王禺期'),
  generateCardInfo('顾楠'),
  generateCardInfo('零')
])

// 方法
const playVideo = (card: RankCard) => {
  playingVideoUrl.value = card.video
  showVideo.value = true
}

const setShowVideo = (value: boolean) => {
  showVideo.value = value
}

const goBack = () => {
  router.back()
}

</script>