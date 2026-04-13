<template>
    <div class="user-main-container">
        <div>
            <router-link :to="{ path: '/manage/resource/editMaterial' }">
                <el-button type="primary" size="medium" :icon="Plus">添加</el-button>
            </router-link>&nbsp;

            <!-- <el-button type="primary" size="medium" @click="showCategorySelector = true">管理分类</el-button> -->
        </div>
        <div class="query">
            <el-select class="category-picker" v-model="page_data.category_id" placeholder="请选择分类" size="small"
                clearable>
                <el-option v-for="item in category_option" :key="item.id" :label="item.name" :value="item.id">
                </el-option>
            </el-select>
            <el-button type="primary" size="small" @click="queryMaterialsList" :icon="Search">查询</el-button>
            <el-button type="danger" size="small" @click="refresh" :icon="Refresh">刷新</el-button>
        </div>
        <div>
            <el-table :header-cell-style="{ background: '#eef1f6', color: '#606266' }" v-loading="loading"
                highlight-current-row ref="multipleTable" border class="eltable" :data="list" style="width: 100%">
                <el-table-column property="title" label="标题" />
                <el-table-column property="cover_image" label="封面图片">
                    <template #default="scope">
                        <el-image :src="scope.row.cover_image" style="width: 60px;"></el-image>
                    </template>
                </el-table-column>
                <el-table-column property="member_price" label="会员价" />
                <el-table-column property="original_price" label="原价" />
                <el-table-column property="category" label="分类">
                    <template #default="scope">
                        {{ scope.row.category.name }}
                    </template>
                </el-table-column>
                <!-- <el-table-column property="author" label="作者"/>
                <el-table-column property="file_name" label="网盘文件"/>
                <el-table-column property="view_num" label="浏览量"/> -->

                <el-table-column property="sort" label="排序">
                    <!-- <template #default="scope">
                        {{ scope.row.sort }}
                    </template> -->
                </el-table-column>

                <el-table-column property="vip_level" label="会员要求">
                    <template #default="scope">
                        {{ scope.row.vip_level }}
                    </template>
                </el-table-column>
                <el-table-column property="status" label="状态">
                    <template #default="scope">
                        <el-switch v-model="scope.row.status" :active-value="activeValue"
                            :inactive-value="inactiveValue">
                        </el-switch>
                    </template>
                </el-table-column>
                <el-table-column property="createTime" label="创建时间" />
                <el-table-column label="操作" fixed="right">
                    <template #default="scope">
                        <div>
                            <router-link :to="{ path: '/manage/resource/editMaterial', query: { id: scope.row.id } }">
                                <el-link type="primary">编辑</el-link></router-link>&nbsp;
                            <el-link type="danger" slot="reference"
                                @click="() => confirmDeleteMaterials(scope.row)">删除</el-link>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
            <div class="page">
                <PageControl :total="page_data.total" v-model:page="page_data.page" :limit="page_data.page_size"
                    @pagination="queryMaterialsList" />
            </div>
        </div>
        <!-- <dialogCategory :show.sync="dialog.categoryStatus"></dialogCategory> -->
    </div>
</template>

<script lang="ts" setup>
// import dialogCategory from './component/dialogCategory.vue'
import { categoryList, materialsList, deleteMaterials } from '@/api/manager/materials'
import { onMounted, ref } from 'vue'
// import Page from '@/components/common/Page.vue'
import PageControl from '../components/PageControl.vue'

import {
    Plus,
    Search,
    Refresh
} from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

const loading = ref(false)
const showCategorySelector = ref(false)
const list = ref([])
const activeValue = ref(0)
const inactiveValue = ref(1)
const page_data = ref({
    page: 1,
    page_size: 10,
    total: 0,
    category_id: '',
})
const category_option = ref([])

const refresh = () => {
    page_data.value.category_id = ''
    queryMaterialsList()
}

const queryMaterialsList = () => {
    materialsList({ params: page_data.value }).then(res => {
        if (res.code == 200) {
            list.value = res.data.list
            page_data.value.total = res.data.total
        }
    })
}
const confirmDeleteMaterials = (item) => {
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
            deleteMaterials({ ids: [item.id] }).then(res => {
                if (res.code == 200) {
                    queryMaterialsList()
                }
            })
        })
        .catch((action) => {
        })
}
const queryCategoryList = () => {
    categoryList().then(res => {
        if (res.code == 200) {
            category_option.value = res.data.list
        }
    })
}

onMounted(() => {
    queryMaterialsList()
    queryCategoryList()
})

</script>

<style scoped lang="scss">
.query {
    display: flex;

    .category-picker {
        width: 300px;
    }
}
</style>