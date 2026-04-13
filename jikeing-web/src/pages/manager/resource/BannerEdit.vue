<template>
    <div class="user-main-container">
        <CommonNav title="添加广告" backTitle="" :appPathObj="{}" :routerPath="{ path: '/manage/resource/banner' }"
            icon="el-icon-back"></CommonNav>
        <div class="form">
            <el-form :model="form" :rules="formRules" ref="formRef" label-width="120px" class="ruleForm">
                <el-form-item label="标题">
                    <el-input type="text" v-model="form.title" size="medium" placeholder="请输入" clearable
                        style="width:500px;"></el-input>&nbsp;
                </el-form-item>
                <el-form-item label="链接">
                    <el-input type="text" v-model="form.link" size="medium" placeholder="请输入" clearable
                        style="width:500px;">
                        <!--          <template slot="prepend">http://</template>-->
                    </el-input>&nbsp;
                </el-form-item>
                <el-form-item label="状态">
                    <el-switch v-model="form.status" :active-value="activeValue"
                        :inactive-value="inactiveValue"></el-switch>
                </el-form-item>
                <el-form-item label="排序">
                    <el-input type="number" v-model="form.sort" size="medium" placeholder="请输入，排序值，越大越靠前" clearable
                        style="width:500px;"></el-input>&nbsp;
                </el-form-item>
                <el-form-item label="图片资源">
                    <div class="image">
                        <el-image :src="form.image" v-if="form.image"></el-image>
                        <singleImageUpload @callfn="uploadCallfn" from="image"></singleImageUpload>
                    </div>
                    <p class="remark-text">支持JPG，GIF，PNG，1M以内</p>
                </el-form-item>
                <el-form-item label="">
                    <div class="submit_btn">
                        <span>
                            <el-button type="primary" size="medium" @click="onSubmit()" v-if="!loading">确 定</el-button>
                            <el-button type="primary" size="medium" v-else>保存中..</el-button>
                        </span>&nbsp;
                        <router-link :to="{ path: '/manage/resource/banner', query: {} }">
                            <el-button size="medium" type="danger">返回</el-button>
                        </router-link>
                    </div>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { createBanners, bannersDetail, updateBanners } from '@/api/manager/banner'
import CommonNav from '../components/CommonNav.vue'
import SingleImageUpload from '../components/SingleImageUpload.vue'
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

import {
    Plus
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
    id: route.query.id,
    image: '',
    title: '',
    link: '',
    sort: 0,
    status: true
})

const formRules = {
    username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
    captcha_answer: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

// 生命周期钩子
onMounted(() => {
    if (route.query.id) {
        fetchBannersDetail(route.query.id)
    }
})

const fetchBannersDetail = (id) => {
    bannersDetail({ params: { id: id } }).then(res => {
        if (res.code == 200) {
            Object.assign(form, {
                ...res.data
            })
        }
    })
}
const onSubmit = async () => {
    // 表单验证
    const valid = await formRef.value.validate()
    if (!valid) return

    try {
        loading.value = true

        if (typeof form.id != 'undefined' && parseInt(form.id) > 0) {
            updateBanners(form).then(res => {
                loading.value = false
                if (res.code == 200) {
                    router.push({ path: '/manage/resource/banner' })
                }
            })
        } else {
            createBanners(form).then(res => {
                loading.value = false
                if (res.code == 200) {
                    router.push({ path: '/manage/resource/banner' })
                }
            })
        }
    } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('提交失败')
    } finally {
        loading.value = false
    }
}
const uploadCallfn = (value) => {
    switch (value.from) {
        case 'image':
            form.image = value.value.url
            break
    }
}

</script>

<style scoped lang="scss">
.image {
    display: flex;

    ::v-deep .el-image__inner {
        width: 60px;
        height: auto;
        border-radius: 4px;
        margin: 0 4px 0 0;
    }
}

.remark-text {
    font-size: 13px;
    color: #9A9A9A;
    border: 0px solid #ccc;
    line-height: 25px;
}
</style>