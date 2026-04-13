<template>
    <div class="user-main-container">
        <router-link :to="{ path: '/manage/resource/editBanner' }">
            <el-button type="primary" size="medium" :icon="Plus">添加</el-button>
        </router-link>
        <div class="table_data">
            <el-table :header-cell-style="{ background: '#eef1f6', color: '#606266' }" v-loading="loading"
                highlight-current-row ref="multipleTable" border class="eltable" :data="list" style="width: 100%">

                <!--      <el-table-column property="id" label="ID" width="80">
      </el-table-column>-->
                <el-table-column property="title" label="标题">
                </el-table-column>
                <el-table-column property="image" label="图片">
                    <template #default="scope">
                        <el-image :src="scope.row.image" style="width: 60px;"></el-image>
                    </template>
                </el-table-column>
                <el-table-column property="link" label="链接">
                </el-table-column>
                <el-table-column property="sort" label="排序"/>
                <el-table-column property="status" label="状态">
                    <template #default="scope">
                        <el-switch v-model="scope.row.status" :active-value="activeValue"
                            :inactive-value="inactiveValue">
                        </el-switch>
                    </template>
                </el-table-column>
                <el-table-column property="create_time" label="创建时间">
                </el-table-column>
                <el-table-column label="操作" fixed="right">
                    <template #default="scope">
                        <div>
                            <router-link :to="{ path: '/manage/resource/editBanner', query: { id: scope.row.id } }">
                                <el-link type="primary">编辑</el-link></router-link>&nbsp;
                            <el-link type="danger" slot="reference" @click="() => confirmDeleteBanners(scope.row)">删除</el-link>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
            <div class="page">
                <PageControl :total="page_data.total" v-model:page="page_data.page" :limit="page_data.page_size"
                    @pagination="queryBannersList" />
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { bannersList, deleteBanners } from '@/api/manager/banner'
import PageControl from '../components/PageControl.vue'
import { onMounted, ref } from 'vue'
import { ElMessageBox } from 'element-plus'

import {
    Plus,
    Search,
    Refresh
} from '@element-plus/icons-vue'

const loading = ref(false)
const list = ref([])
const activeValue = ref(0)
const inactiveValue = ref(1)
const page_data = ref({
    page: 1,
    page_size: 10,
    total: 0,
    category_id: '',
})

const queryBannersList = () => {
    bannersList({ params: page_data.value }).then(res => {
        if (res.code == 200) {
            list.value = res.data.list
            page_data.value.total = res.data.total
        }
    })
}
const confirmDeleteBanners = (item) => {
    ElMessageBox.confirm(
        '确定删除当前数据, 是否继续?',
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
            distinguishCancelAndClose: true
        }
    )
        .then(() => {
            deleteBanners({ id: item.id }).then(res => {
                if (res.code == 200) {
                    queryBannersList()
                }
            })
        })
        .catch((action) => {
        })
}

onMounted(() => {
    queryBannersList()
})

</script>

<style scoped></style>