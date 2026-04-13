<template>
    <div class="user-main-container">
        <div>
            <div class="search-form-wrapper">
                <el-input v-model="uuid" class="user-id-input" placeholder="请输入用户ID" />
                <el-button type="primary" size="medium" @click="queryList">刷新</el-button>

                <!-- <el-button type="primary" size="medium" @click="showCategorySelector = true">管理分类</el-button> -->
            </div>
            <el-table :header-cell-style="{ background: '#eef1f6', color: '#606266' }" v-loading="loading" :data="list"
                style="width: 100%">
                <el-table-column label="图片">
                    <template #default="scope">
                        <el-image :src="scope.row.resultImage[0]" :preview-src-list="scope.row.resultImage"
                            :preview-teleported="true" style="width: 60px;"></el-image>
                    </template>
                </el-table-column>
                <el-table-column property="model" label="任务类型" style="width: 20px;"></el-table-column>
                <el-table-column property="id" label="id(点击查看详情)" style="width: 200px;">
                    <template #default="scope">
                        <el-link @click="showDetail(scope.row)">{{ scope.row.idText }}</el-link>
                    </template>
                </el-table-column>
                <el-table-column property="uuid" label="用户ID" style="width: 80px;"></el-table-column>
                <el-table-column property="nickname" label="用户名" style="width: 50px;"></el-table-column>
                <el-table-column property="prompt" label="提示词" />
                <el-table-column property="createTimeText" label="创建时间"></el-table-column>
                <!-- <el-table-column label="操作" fixed="right">
                    <template #default="scope">
                        <div>
                            <router-link :to="{ path: '/manage/resource/editMaterial', query: { id: scope.row.id } }">
                                <el-link type="primary">编辑</el-link></router-link>&nbsp;
                            <el-link type="danger" slot="reference"
                                @click="() => confirmDeleteMaterials(scope.row)">删除</el-link>
                        </div>
                    </template>
                </el-table-column> -->
            </el-table>
            <div class="page">
                <PageControl :total="pageInfo.total" v-model:page="pageInfo.page" :limit="pageInfo.page_size"
                    @pagination="handleCurrentChange" />
            </div>
        </div>

        <el-drawer v-model="showDrawer" title="AI图片" direction="rtl">
            <div v-if="currentTask">
                <div class="section-title">ID：{{ currentTask.idText }}</div>
                <div class="section-title">结果图</div>
                <div class="result-image-wrapper">
                    <el-image :src="currentTask.resultImage" :preview-src-list="[currentTask.resultImage]"
                        :preview-teleported="true" style="width: 200px; height: 200px;" fit="contain"></el-image>
                </div>
                <div class="section-title">垫图</div>
                <div class="refer-image-wrapper">
                    <el-image v-for="image in currentTask.referImages" :src="image" :preview-src-list="[image]"
                        :preview-teleported="true" style="width: 100px; height: 100px;" fit="contain"></el-image>
                </div>
                <div class="section-title">提示词</div>
                <div class="prompt-wrapper">
                    {{ currentTask.prompt }}
                </div>
                <el-button type="primary" @click="execAddMaterailToSquare(currentTask)">添加到素材广场</el-button>
            </div>
        </el-drawer>


    </div>
</template>

<script lang="ts" setup>
import { aiAdminTaskList } from '@/api/manager/aiGen.js'
import { apiAddMaterailToSquare } from '@/api/manager/materialSquare.js'
import { onMounted, ref } from 'vue'
import PageControl from '@/pages/manager/components/PageControl.vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)

const uuid = ref('')

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
        let res = await aiAdminTaskList({ params: { ...pageInfo.value, uuid: uuid.value } })
        console.log('----res = ', res)
        loading.value = false
        if (res.code === 10000) {
            res.data.list.forEach(element => {
                element.resultImage = JSON.parse(element.resultImages)
                element.referImages = JSON.parse(element.referImages || "[]") || []
            });
            list.value = res.data.list
            pageInfo.value.total = res.data.totalCount
        } else {
            ElMessage.error('数据加载失败,' + res.msg)
            console.error('ai列表数据加载失败', res.msg)
        }
    } catch (err) {
        loading.value = false
        ElMessage.error('数据加载失败')
        console.error('ai列表数据加载失败', err)
    }
}

const showDetail = (task) => {
    currentTask.value = task
    showDrawer.value = true
}

const execAddMaterailToSquare = async (task) => {
    try {
        let res = await apiAddMaterailToSquare({
            assId: task.id,
            name: "",
            prompt: task.prompt,
            size: "",
            imageUrl: task.resultImage,
            referImages: JSON.stringify(task.referImages)
        })
        if (res.code === 10000) {
            ElMessage.success('添加完成')
        } else {
            ElMessage.error('添加失败' + res.msg)
            console.error('添加失败', res.msg)
        }

    } catch (err) {
        ElMessage.error('添加失败')
        console.error('添加失败', err)
    }
}

// const confirmDeleteMaterials = (item) => {
//     ElMessageBox.confirm(
//         '确定删除当前数据, 是否继续?',
//         '提示',
//         {
//             confirmButtonText: '确定',
//             cancelButtonText: '取消',
//             type: 'warning',
//             distinguishCancelAndClose: true
//         }
//     )
//         .then(() => {
//             deleteMaterials({ ids: [item.id] }).then(res => {
//                 if (res.code == 200) {
//                     queryMaterialsList()
//                 }
//             })
//         })
//         .catch((action) => {
//         })
// }

onMounted(() => {
    queryList();
})

</script>

<style scoped lang="scss">
.search-form-wrapper {
    display: flex;

    .user-id-input {
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