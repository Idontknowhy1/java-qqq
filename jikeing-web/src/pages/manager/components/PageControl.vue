<template>
  <div :class="{'hidden':hidden}" class="pagination-container">
    <el-pagination
      background
      layout="prev, pager, next"
      v-model:current-page="currentPage"
      :page-size="limit"
      :small="small"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div> <!--:page-size.sync="pageSize" :page-sizes="pageSizes"-->
</template>

<script lang="ts" setup>
import { computed } from 'vue'

const props = defineProps({
  total: {
    required: true,
    type: Number
  },
  page: {
    type: Number,
    default: 1
  },
  limit: {
    type: Number,
    default: 20
  },
  pageSizes: {
    type: Array,
    default: () => [18, 30, 100]
  },
  hidden: {
    type: Boolean,
    default: false
  },
  small: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:page', 'update:limit', 'pagination'])

const currentPage = computed({
  get: () => props.page,
  set: (val) => emit('update:page', val)
})

const pageSize = computed({
  get: () => props.limit,
  set: (val) => emit('update:limit', val)
})

const handleSizeChange = (val) => {
  emit('pagination', { page: currentPage.value, limit: val })
}

const handleCurrentChange = (val) => {
  emit('pagination', { page: val, limit: pageSize.value })
}
</script>

<style scoped>
.pagination-container {
  /*background: #fff;*/
  padding: 0px 0;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}
.el-pager li{min-width: 20px}
.pagination-container.hidden {
  display: none;
}
</style>
