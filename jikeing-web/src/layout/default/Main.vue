<template>
  <div id="container">
    <keep-alive>
      <router-view v-if="$route.meta.keepAlive"></router-view>
    </keep-alive>
    <router-view v-if="!$route.meta.keepAlive"></router-view>
  </div>
</template>

<script lang="ts" setup>
import { onMounted } from 'vue';
import { useUserStore } from '../../store/useUserStore';

const userStore = useUserStore()

onMounted(() => {
  userStore.getUserInfo()
})
</script>

<style lang="scss" scoped></style>