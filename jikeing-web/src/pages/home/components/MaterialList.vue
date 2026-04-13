<template>
    <div class="index_list_1">
        <div class="content">
            <div class="item" v-for="(item, key) in list" :key="key" @click="toLink(item)">
                <div class="img">
                    <div class="rights">
                        <!--            <div class="text">版权</div>-->
                        <img src="@/assets/images/themes/rights_icon.png">
                    </div>
                    <el-image :src="item.cover_image" class="cover_image"></el-image>
                </div>
                <div class="category_price">
                    <div class="price" v-if="item.original_price > 0">￥{{ item.original_price }}</div>
                </div>
                <div class="title">
                    <div class="title_text">{{ item.title }}</div>
                    <div class="category_list">
                        <div class="category_list_item color1">
                            {{ item.category.name }}
                        </div>
                    </div>
                </div>
                <div class="sub_title">{{ item.intro }}</div>
                <div class="other">
                    <div class="time">
                        <div><img src="@/assets/images/time.png" class="icon" /></div>&nbsp;
                        <div>{{ item.create_time }}</div>
                    </div>
                    <div class="view_count">
                        <div><img src="@/assets/images/view.png" class="icon" /></div>&nbsp;
                        <div>{{ item.view_num }}次浏览</div>
                    </div>
                </div>
                <!--      </router-link>-->
            </div>
        </div>
        <div class="pagination">
            <el-pagination :page-size="pageInfo.page_size" background layout="prev, pager, next"
                @current-change="handleCurrentChange" :total="pageInfo.total">
            </el-pagination>
        </div>

    </div>
</template>

<script lang="ts" setup>
import { materialsList } from "@/api/home/materials";
import { useUserStore } from '@/store/useUserStore.ts'
import { ref } from "vue";
import { useRouter } from "vue-router";
const useStore = useUserStore()
const router = useRouter()

const emit = defineEmits(['showDetail'])

// 数据列表
const list = ref([])
// 分页数据
const pageInfo = ref({
    category_id: '',
    page: 1,
    page_size: 12,
    total: 0
})

// 点击后跳转到素材详情页
const toLink = (item) => {
    if (useStore.loginStatus == 0) {
        useStore.setDialogLoginStatus(true)
    } else {
        // router.push({ path: '/detail', query: { id: item.id } })
        emit('showDetail', item)
    }
}

const handleCurrentChange = (val) => {
    pageInfo.value.page = val
    requestList(pageInfo.value.category_id, 0)
}

const requestList = (category_id, type) => {
    if (type === 1) {
        pageInfo.page = 1
    }
    pageInfo.value.category_id = category_id
    materialsList({ params: pageInfo.value }).then(res => {
        console.log('首页素材', res)
        if (res.code == 200) {
            list.value = res.data.list
            pageInfo.value.total = res.data.total
        }
    })
}

defineExpose({
    requestList
})

</script>
<style scoped lang="scss"></style>