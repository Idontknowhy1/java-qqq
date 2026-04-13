<script setup lang="ts">
import WxLoginDialog from '@/pages/home/components/WxLoginDialog.vue';
import { useUserStore

 } from './store/useUserStore';
const userStore = useUserStore()

</script>

<template>
  
   <!-- 微信登陆对话框 -->
  <WxLoginDialog v-if="userStore.dialogLoginStatus"/>

   <router-view/>
</template>

<style scoped type="scss">
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
  font-size: 20px;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
