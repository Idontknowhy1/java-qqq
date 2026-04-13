<template>
    <div class="user-main-container">
        <CommonNav title="添加资源" backTitle="" :appPathObj="{}" :routerPath="{ path: '/manage/resource' }"
            icon="el-icon-back">
        </CommonNav>
        <div class="form" style="display: flex; justify-content: flex-start;">
            <el-form :model="form" :rules="formRules" ref="formRef" label-width="130px">
                <el-form-item label="分类">
                    <el-select v-model="form.category_id" placeholder="请选择分类" clearable>
                        <el-option v-for="item in category_option" :key="item.id" :label="item.name" :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="标题">
                    <el-input type="text" v-model="form.title" size="medium" placeholder="请输入" clearable></el-input>
                </el-form-item>
                <el-form-item label="简介">
                    <el-input type="text" v-model="form.intro" size="medium" placeholder="请输入" clearable></el-input>
                </el-form-item>
                <el-form-item label="封面">
                    <div class="image">
                        <el-image :src="form.cover_image" v-if="form.cover_image" />&nbsp;
                        <SingleImageUpload @callfn="uploadedCall" from="cover_image" />
                        <p class="remark-text">大小为：1M以内，支持JPG，GIF，PNG</p>
                    </div>
                </el-form-item>
                <el-form-item label="会员价">
                    <el-input type="text" v-model="form.member_price" size="medium" placeholder="请输入" clearable>
                        <template slot="prepend">￥</template>
                    </el-input>
                </el-form-item>
                <el-form-item label="原价">
                    <el-input type="text" v-model="form.original_price" size="medium" placeholder="请输入" clearable>
                        <template slot="prepend">￥</template>
                    </el-input>
                </el-form-item>
                <el-form-item label="下载要求会员级别">
                    <el-input type="number" v-model="form.vip_level" size="medium" placeholder="请输入" clearable>
                    </el-input>
                </el-form-item>
                <el-form-item label="网盘链接">
                    <el-input type="text" v-model="form.baidu_url" size="medium" placeholder="请输入百度网盘下载链接" clearable>
                    </el-input>
                </el-form-item>
                <el-form-item label="状态">
                    <el-switch v-model="form.status" :active-value="activeValue"
                        :inactive-value="inactiveValue"></el-switch>
                </el-form-item>
                <el-form-item label="排序">
                    <el-input type="text" v-model="form.sort" size="medium" placeholder="请输入，排序值，越大越靠前" clearable
                        style="width: 300px;"></el-input>
                </el-form-item>
                <!-- <el-form-item label="选择网盘文件">
                    <el-input type="text" v-model="form.file_name" size="medium" placeholder="请选择" disabled
                        style="width: 500px;"></el-input>&nbsp;
                    <el-button type="primary" size="medium" @click="() => dialog.baidupanStatus = true">选择</el-button>
                </el-form-item> -->
                <el-form-item label="详情内容">
                    <!--        <TinymceEditor :tinymceContent.sync="form.details" tinymceId="tinymace_id-1" ></TinymceEditor>-->
                    <div class="image">
                        <div v-if="form.details != null && form.details.length > 0" class="image_list">
                            <div class="list" v-for="(item, key) in form.details" :key="key">
                                <el-icon class="el-icon-circle-close" @click="removeDetailImage(item)">
                                    <CircleCloseFilled />
                                </el-icon>
                                <el-image :src="item.url"></el-image>
                            </div>
                        </div>
                        <SingleImageUpload @callfn="uploadedCall" from="details"></SingleImageUpload>
                    </div>
                    <p class="remark-text">大小为：1M以内，支持JPG，GIF，PNG</p>
                </el-form-item>
                <el-form-item label="自定义属性">
                    <div class="item_box">
                        <div class="item" v-for="(item, key) in form.properties" :key="key">
                            <el-input placeholder="请输入属性名" v-model="item.name" style="width: 150px;"></el-input>：
                            <el-input placeholder="请输入属性值" v-model="item.value" style="width: 150px;"></el-input>&nbsp;
                            <i class="el-icon-remove-outline" style="font-size: 24px;" @click="removerAttrs(key)"></i>
                        </div>
                        <el-button type="primary" :icon="Plus" size="small" @click="addAttrs">添加</el-button>
                    </div>

                </el-form-item>

                <el-form-item label="">
                    <span>
                        <el-button type="primary" size="medium" @click="onSubmit()" v-if="!loading">确 定</el-button>
                        <el-button type="primary" size="medium" v-else>保存中..</el-button>
                    </span>&nbsp;
                    <router-link :to="{ path: '/manage/resource/material', query: {} }">
                        <el-button size="medium">返 回</el-button>
                    </router-link>
                </el-form-item>
            </el-form>
        </div>
        <DialogBaiduPanList v-model:show="dialog.baidupanStatus" @callfn="baidupanCallfn" />
    </div>
</template>

<script setup lang="ts">
import DialogBaiduPanList from '../components/DialogBaiduPanList.vue'
import CommonNav from '../components/CommonNav.vue'
import SingleImageUpload from '../components/SingleImageUpload.vue'

import { categoryList, createMaterials, updateMaterials, materialsDetail } from '@/api/manager/materials'

import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
// import DialogBaiduPanList from './components/DialogBaiduPanList.vue'

import {
    Plus,
    Close,
    CircleCloseFilled
} from '@element-plus/icons-vue'
import { param } from '../../../utils/common'


const activeValue = ref(0)
const inactiveValue = ref(1)
const route = useRoute()
const router = useRouter()

// 响应式状态
const loading = ref(false)
const formRef = ref(null)

const form = reactive({
    id: route.query.id || '',
    category_id: '',
    title: '',
    cover_image: null,
    intro: '',
    member_price: '0',
    original_price: '0',
    details: [],
    file_name: '',
    baidu_url: '',
    baidu_fsid: '0',
    baidu_category: '',
    sort: '0',
    properties: [],
    vip_level: '',
    status: true,
})

const formRules = {
    username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
    captcha_answer: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const dialog = reactive({
    uploadImageStatus: false,
    baidupanStatus: false,
})

const category_option = ref([])

// 生命周期钩子
onMounted(() => {
    fetchCategoryList()
    if (route.query.id) {
        fetchMaterialsDetails(route.query.id)
    }
})

// 删除详情的图片
const removeDetailImage = (item) => {
    form.details = form.details.filter(d => d != item)
}

// 方法
const removeAttrs = (index) => {
    form.properties.splice(index, 1)
}

const addAttrs = () => {
    form.properties.push({ name: '', value: '' })
}

const baidupanCallfn = (value) => {
    form.file_name = value.checkedName
    form.baidu_fsid = value.fs_id
    form.baidu_category = value.category
}

const uploadedCall = (res) => {
    switch (res.from) {
        case 'cover_image':
            form.cover_image = res.value.url
            break
        case 'details':
            if (form.details == null) {
                form.details = [{ width: '', height: '', url: res.value.url }]
            } else {
                form.details.push({ width: '', height: '', url: res.value.url })
            }
            break
    }
}

const fetchMaterialsDetails = async (id) => {
    try {
        const res = await materialsDetail({ params: { id } })
        if (res.code === 200) {
            // 合并数据到 form 对象
            Object.assign(form, {
                ...res.data,
                properties: res.data.properties || []
            })
        }
    } catch (error) {
        console.error('获取详情失败:', error)
        ElMessage.error('获取详情失败')
    }
}

const onSubmit = async () => {
    // 表单验证
    const valid = await formRef.value.validate()
    if (!valid) return

    try {
        loading.value = true

        if (form.id && parseInt(form.id) > 0) {
            const res = await updateMaterials(form)
            if (res.code === 200) {
                ElMessage.success('更新成功')
                router.push({ path: '/manage/resource/material' })
            }
        } else {
            const res = await createMaterials(form)
            if (res.code === 200) {
                ElMessage.success('创建成功')
                router.push({ path: '/manage/resource/material' })
            }
        }
    } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('提交失败')
    } finally {
        loading.value = false
    }
}

// 分类列表
const fetchCategoryList = async () => {
    try {
        const res = await categoryList()
        if (res.code === 200) {
            category_option.value = res.data.list
        }
    } catch (error) {
        console.error('获取分类失败:', error)
    }
}

</script>

<style scoped lang="scss">
.image {
    display: flex;
    align-items: center;

    .image_list {
        display: flex;
        align-items: center;

        .list {
            border: 0px solid #f3f3f3;
            position: relative;
            margin: 0 10px 0 0;

            .el-icon-circle-close {
                position: absolute;
                top: -6px;
                right: -6px;
                color: #000;
                font-size: 20px;
                z-index: 100;
                cursor: pointer;
                background-color: white;
                border-radius: 10px;
            }
        }
    }

    ::v-deep .el-image__inner {
        width: 60px;
        height: 60px;
        border-radius: 4px;
        margin: 0 0px 0 0;
    }
}

.remark-text {
    font-size: 13px;
    color: #9A9A9A;
    border: 0px solid #ccc;
    line-height: 25px;
    margin: 0 0px 0 10px;
}

.item {
    margin: 0 0 10px 0;
}
</style>