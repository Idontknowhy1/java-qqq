<template>
    <div class="user-main-container">
        <div>
            <div>
                <el-button type="primary" size="medium" @click="queryList">刷新</el-button>

                <!-- <el-button type="primary" size="medium" @click="showCategorySelector = true">管理分类</el-button> -->
            </div>
            <el-table :header-cell-style="{ background: '#eef1f6', color: '#606266' }" v-loading="loading" :data="list"
                style="width: 100%">
                <el-table-column property="id" label="id" />
                <el-table-column property="title" label="名称" />
                <el-table-column label="图片">
                    <template #default="scope">
                        <el-image :src="scope.row.imageUrl" :preview-src-list="[scope.row.imageUrl]"
                            :preview-teleported="true" style="width: 100px;"></el-image>
                    </template>
                </el-table-column>
                <!-- <el-table-column property="id" label="id(点击查看详情)" style="width: 200px;">
                    <template #default="scope">
                        <el-link @click="showDetail(scope.row)">{{ scope.row.id }}</el-link>
                    </template>
                </el-table-column> -->
                <el-table-column property="detail" label="要求" width="500" />
                <el-table-column label="状态">
                    <template #default="scope">
                        <div v-if="scope.row.state == 0">待审核</div>
                        <div v-if="scope.row.state == 1">进行中</div>
                        <div v-if="scope.row.state == 3">已拒绝</div>
                        <div v-if="scope.row.state == 2">已完成</div>
                    </template>
                </el-table-column>
                 <el-table-column label="拒绝原因">
                    <template #default="scope">
                        <span v-html="scope.row.reason"></span>
                    </template>
                </el-table-column>
                <el-table-column property="createTimeText" label="创建时间"></el-table-column>
                <el-table-column label="操作" fixed="right" width="200">
                    <template #default="scope">
                        <el-button v-if="scope.row.state == 0" slot="reference"
                            @click="() => confirmChangeState(scope.row, 1)">接受</el-button>
                        <el-button v-if="scope.row.state == 0" slot="reference"
                            @click="() => refuse(scope.row)">拒绝</el-button>
                        <el-button v-if="scope.row.state == 1" slot="reference"
                            @click="() => showUploadDrawer(scope.row)">上传PSD</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="page">
                <PageControl :total="pageInfo.total" v-model:page="pageInfo.pageNum" :limit="pageInfo.pageSize"
                    @pagination="handleCurrentChange" />
            </div>
        </div>

        <el-drawer v-model="showDrawer" title="结果回填" direction="rtl">
            <div v-if="currentTask">
                <div class="section-title">百度网盘连接地址</div>
                <div class="result-image-wrapper">
                    <el-input v-model="resultUrl" />
                </div>
                <el-button type="primary" @click="updateResultUrl(currentTask.id)">保存</el-button>
            </div>
        </el-drawer>


    </div>
</template>

<script lang="ts" setup>
import { apiAdminUploadSplitAuditList, apiAdminUploadSplitStateChange, apiAdminUploadSplitRefuse } from '@/api/manager/materialSquare.js'
import { onMounted, ref } from 'vue'
import PageControl from '@/pages/manager/components/PageControl.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)

const showDrawer = ref(false)
const currentTask = ref()

const resultUrl = ref('')

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
        let res = await apiAdminUploadSplitAuditList({ params: pageInfo.value })
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

const showDetail = (task) => {
    currentTask.value = task
    showDrawer.value = true
}

const confirmChangeState = (item, state) => {
    ElMessageBox.confirm(
        '确定接受请求, 是否继续?',
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
            distinguishCancelAndClose: true
        }
    )
        .then(() => {
            apiAdminUploadSplitStateChange({ id: item.id, state }).then(res => {
                if (res.code == 10000) {
                    queryList()
                }
            })
        })
        .catch((action) => {
        })
}

const refuse = (item) => {
    ElMessageBox.prompt('请输入拒绝原因', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showInput: true,
        inputType: 'textarea'
    })
        .then(({ value }) => {
            if (value.length == 0) {
                ElMessage.info('请输入原因')
                return
            }
             apiAdminUploadSplitStateChange({ id: item.id, reason: value, state: 3 }).then(res => {
                if (res.code == 10000) {
                    queryList()
                }
            })
        })
        .catch(() => {
        })
}

const updateResultUrl = (id) => {
    if (resultUrl.value.length == 0) {
        ElMessage.info('请填写百度网盘地址')
    }
    apiAdminUploadSplitStateChange({ id: id, state: 2, resultUrl: resultUrl.value }).then(res => {
        if (res.code == 10000) {
            currentTask.value = null
            showDrawer.value = false
            queryList()
        }
    })
}

const showUploadDrawer = (item) => {
    currentTask.value = item
    showDrawer.value = true
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