<template>
    <div class="user-main-container">
        <div>
            <div>
                <el-button type="primary" size="medium" @click="queryList">刷新</el-button>

                <!-- <el-button type="primary" size="medium" @click="showCategorySelector = true">管理分类</el-button> -->
            </div>
            <el-table :header-cell-style="{ background: '#eef1f6', color: '#606266' }" v-loading="loading" :data="list"
                style="width: 100%">
                <el-table-column label="图片">
                    <template #default="scope">
                        <el-image :src="scope.row.imageUrl" :preview-src-list="[scope.row.imageUrl]"
                            :preview-teleported="true" style="width: 60px;"></el-image>
                    </template>
                </el-table-column>
                <!-- <el-table-column property="id" label="id(点击查看详情)" style="width: 200px;">
                    <template #default="scope">
                        <el-link @click="showDetail(scope.row)">{{ scope.row.id }}</el-link>
                    </template>
                </el-table-column> -->
                <el-table-column property="prompt" label="提示词" />
                <el-table-column property="createTimeText" label="创建时间"></el-table-column>
                <el-table-column label="操作" fixed="right">
                    <template #default="scope">
                        <div>
                            <el-link type="danger" slot="reference"
                                @click="() => confirmDelete(scope.row)">删除</el-link>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
            <div class="page">
                <PageControl :total="pageInfo.total" v-model:page="pageInfo.pageNum" :limit="pageInfo.pageSize"
                    @pagination="handleCurrentChange" />
            </div>
        </div>

    </div>
</template>

<script lang="ts" setup>
import { apiDeleteMaterailFromSquare, apiMaterailSquarePageList } from '@/api/manager/materialSquare.js'
import { onMounted, ref } from 'vue'
import PageControl from '@/pages/manager/components/PageControl.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { param } from '../../../../utils/common'

const loading = ref(false)

const showDrawer = ref(false)
const currentTask = ref()

const list = ref([])

// 分页数据
const pageInfo = ref({
    pageNum: 1,
    pageSize: 20,
    total: 0
})
const handleCurrentChange = (val) => {
    pageInfo.value.pageNum = val.page
    pageInfo.value.pageSize = val.limit
    queryList()
}

const queryList = async () => {
    try {
        console.log('----pageInfo = ', pageInfo.value)
        loading.value = true
        let res = await apiMaterailSquarePageList({ params: pageInfo.value })
        console.log('----res = ', res)
        loading.value = false
        if (res.code === 10000) {
            list.value = res.data.list
            pageInfo.value.total = res.data.totalCount
        } else {
            ElMessage.error('数据加载失败,' + res.msg)
            console.error('列表数据加载失败', res.msg)
        }
    } catch (err) {
        loading.value = false
        ElMessage.error('数据加载失败')
        console.error('列表数据加载失败', err)
    }
}

const confirmDelete = (item) => {
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
            apiDeleteMaterailFromSquare({ params: { id: item.id } }).then(res => {
                if (res.code == 10000) {
                    queryList()
                }
            })
        })
        .catch((action) => {
        })
}

onMounted(() => {
    queryList();
})

</script>

<style scoped lang="scss">
.query {
    display: flex;

    .category-picker {
        width: 300px;
    }
}

.section-title {
    margin-bottom: 16px;
    margin-top: 16px;
    font-weight: 600;
}

.prompt-wrapper {
    font-size: 16px;
}
</style>