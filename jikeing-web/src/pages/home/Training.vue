<template>
  <div class="main-container">
    <HeaderNav />
    <header_nav></header_nav>
    <div class="training_box">
      <!-- <img src="@/assets/images/themes/banner-1.png" /> -->
      <img src="@/assets/images/themes/banner-3.png" />
    </div>
  </div>
</template>

<script setup lang="ts">
import HeaderNav from './components/HeaderNav.vue';
</script>

<style scoped lang="scss">
@media screen and (max-width: 5000px) and (min-width: 1600px) {
  .training_box {
    width: 1391px;
    border: 0px solid #fff;
    margin: 0 auto;
    img {
      width: 1391px;
      margin: 10px auto 0;
      border-radius: 10px;
    }
  }
}

@media screen and (max-width: 1600px) and (min-width: 760px) {
  .training_box {
    width: 1034px;
    border: 0px solid #fff;
    margin: 0 auto;
    img {
      width: 1034px;
      margin: 10px auto 0;
      border-radius: 10px;
    }
  }
}
</style>