<template>
  <div class="search-user-order-page">

    <div class="input-wrapper">
      <el-input placeholder="请输入即课网uid" v-model="userId"/>
      <el-button type="primary" @click="searchUserOrderList">查询</el-button>
    </div>
    <div class="input-wrapper">
      <el-input placeholder="请输入商户单号" v-model="orderNo" />
      <el-button type="primary" @click="searchUserOrderList">查询</el-button>
    </div>

    <div class="content-wrapper">
      <div class="error-info">{{ errorInfo }}</div>
      <div class="order-list-wrapper">
        <div class="order-item" v-for="order in orderList" :key="order.id">{{ order.info }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { ref } from 'vue';
import { selectUserOrderList } from '@/api/manager/order.ts'

const errorInfo = ref('')
const userId = ref('')
const orderNo = ref('')
const orderList = ref([])

const searchUserOrderList = async () => {
  if (userId.value.length == 0 && orderNo.value.length == 0) {
    ElMessage.info('请输入即课网uid 或者 商户单号')
    return
  }
  try {
    let res = await selectUserOrderList({ uuid: userId.value, orderNo: orderNo.value })
    if (res.code === 10000) {
      console.log(res.data)
      errorInfo.value = ''
      res.data.map(o => {
        o.info = o.createTimeText + ' 用户 ' + userId.value + ' 购买了 ' + o.name + ', 实际支付' + o.payPrice + ', 交易单号：' + o.orderNo
        return o
      })
      orderList.value = res.data
    } else {
      errorInfo.value = res.msg
    }
  } catch (err) {
    errorInfo.value = err
  }
}

</script>

<style lang="scss" scoped>
.search-user-order-page {
  width: 100%;
  height: 100%;
  background-color: white;
  padding: 20px;

  .input-wrapper {
    width: 400px;
    display: flex;
  }

  .order-list-wrapper {
    margin-top: 20px;
    font-size: 14px;
    .order-item {
      line-height: 30px;
    }
  }
}
</style>