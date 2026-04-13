<template>
  <div class="rank-video-card rounded-[20px] mb-[40px] relative cursor-pointer overflow-hidden" :class="{
    'w-[450px]': isLarge,
    'w-[350px]': !isLarge
  }" @click="handleCardTap">
    <img class="video-image rounded-[20px]" :src="data.cover" />
    <div class="absolute bottom-0 w-full h-[56px] flex items-center pl-[20px]">
      <img class="w-[40px] rounded-[20px]" :src="data.avatar" />
      <div class="text-[white] text-[24px] ml-[20px]">{{ data.name }}</div>
    </div>

    <!-- 播放按钮（注释部分） -->
    <div class="play-btn absolute w-full h-full left-0 top-0 flex justify-center items-center">
      <img class="w-[60px]" :src="playBtnImage" />
    </div>
  </div>
</template>

<script setup lang="ts">
import playBtnImage from '@/assets/images/video-play-btn.png'

// 定义类型
interface RankCard {
  avatar: string
  cover: string
  video: string
  name: string
}

// 定义Props
interface Props {
  data: RankCard
  showPlayBtn?: boolean
  isLarge?: boolean
}

// 定义Emits
interface Emits {
  (e: 'cardTap', data: RankCard): void
}

// 接收Props
const props = withDefaults(defineProps<Props>(), {
  showPlayBtn: false
})

// 定义Emits
const emit = defineEmits<Emits>()

// 处理卡片点击事件
const handleCardTap = () => {
  emit('cardTap', props.data)
}

</script>

<style scoped lang="scss">
.rank-video-card {
  border: 5px solid transparent;
  /* 预先占用空间 */
  transition: border-color 0.3s;

  .video-image {
    transition: transform 0.5s ease;
  }

  .play-btn {
    opacity: 0;
    transition: opacity 0.5s ease;
  }
}

.rank-video-card:hover {
  border-color: #3498db;

  .video-image {
    transform: scale(1.1);
  }

  .play-btn {
    opacity: 1;
  }
}
</style>