<template>
  <div class="right-nav">
    <el-popover placement="left" title="" :width="110" trigger="hover" content="">
      <template #default>
        <div class="kf_qrcode">
          <img src="@/assets/images/kefu-qrcode.jpg">
          <div class="text">添加客服微信</div>
        </div>
      </template>

      <template #reference>
        <div class="item contact">
          <img src="@/assets/images/kefu.png">
        </div>
      </template>
      
    </el-popover>
    <div class="item up" @click="scrollToTop()">
      <img src="@/assets/images/up.png">
    </div>
  </div>
</template>

<script lang="ts" setup>
const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    left: 0,
    behavior: 'smooth'
  });
}
</script>

<style scoped lang="scss">
.kf_qrcode {
  width: 120px;
  margin: 0 auto;
  padding: 0 0 10px 0;
  border: 0px solid #ccc;

  img {
    width: 120px;
    height: 120px;
    margin: 10px auto 0;
  }

  .text {
    width: 100%;
    text-align: center;
  }
}

.right-nav {
  position: fixed;
  width: 60px;
  height: 180px;
  right: 10px;
  bottom: 100px;
  z-index: 1000;
  cursor: pointer;

  .item {
    width: 48px;
    height: 48px;
    line-height: 50px;
    text-align: center;
    border-radius: 8px;
    opacity: 1;
    background: #1B9AF5;
    box-shadow: 0px 4px 6px -4px rgba(0, 0, 0, 0.1), 0px 10px 15px -3px rgba(0, 0, 0, 0.1);
    margin: 16px 0 0 0;

    border: 0px solid #ff0000;
    z-index: 1000;

    img {
      width: 20px;
      height: 20px;
      margin: 14px 0 0 0;
    }
  }
}

/*
.right-nav {
  position: fixed;
  width: 34px;
  height: 80px;
  right: 26px;
  bottom: 150px;
  background: #fff;
  border-radius: 50px;
  box-shadow:0 0 1px #ccc;
  cursor: pointer;
  .contact {
    margin: 10px 0 4px 6px;
    img {
      width: 21px;
      height: auto;
    }

  }
  .line {
    width: 80%;
    height: 1px;
    margin: 0 auto;
    background: #EFEFEF;
  }
  .up {
    margin: 6px 0 0 5px;
    img {
      width: 26px;
      height: auto;
    }
  }

}*/
</style>