<template>
  <div class="main-container">
    <div class="detail">
      <div class="back">
        <div class="img" @click="goBack" style="width: 50px; cursor: pointer;">
          <i class="el-icon-back"></i> 返回
        </div>
      </div>

      <div class="content">
        <div class="content_box">
          <div class="item image_list">
            <img :src="item.url" v-for="(item, key) in detail.details" :key="key" @click="checked_image_fn(item)" />
          </div>

          <div class="item image_info">
            <!--fit="contain"  style="height:100%;"-->
            <el-image :src="checked_image" :preview-src-list="previewSrcList"></el-image>
          </div>

          <div class="item other_info">
            <div class="name">{{ detail.title }}</div>
            <div class="view_time">
              <div class="time">
                <div><img src="@/assets/images/time.png" class="icon" /></div>&nbsp;
                <div>{{ detail.create_time }}</div>
              </div>
              <div class="view_count">
                <div><img src="@/assets/images/view.png" class="icon" /></div>&nbsp;
                <div>{{ detail.view_num }}次浏览</div>
              </div>
            </div>
            <div class="material_info">
              <div class="title">素材信息</div>
              <div class="item_box">
                <div class="item_list" v-for="(item, key) in detail.properties" :key="key">
                  <div class="label">{{ item.name }}</div>
                  <div class="value">{{ item.value }}</div>
                </div>
              </div>
            </div>
            <div v-if="userStore.loginStatus && userStore.is_material_member == 1">
              <!-- 简笔画 -->
              <a class="download_btn" v-if="userStore.vip_level == 1 && detail.category.id == '11'" :href="detail.baidu_url" target="_blank">立即下载</a>
              <a class="download_btn" v-else-if="userStore.vip_level > 1" :href="detail.baidu_url" target="_blank">立即下载</a>
              <div v-else @click="checkDownload">
                <div class="download_btn">立即下载</div>
              </div>
            </div>
            <div class="download_btn" v-else @click="checkDownload">
              <div class="download-btn">立即下载</div>
            </div>

          </div>

        </div>

      </div>
    </div>

    <Footer />
  </div>
</template>

<script lang="ts" setup>
import Footer from '../../layout/default/Footer.vue';
// import detail_info from "./component/detail_info.vue"
import { materialsDetail } from "@/api/home/materials";
import { useUserStore } from '../../store/useUserStore';
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import '@/assets/scss/themes/black/detail.scss'

const props = defineProps({
  id: {
    required: true,
    type: Number
  }
})

const emit = defineEmits(['dismissPage'])

const userStore = useUserStore()
const route = useRoute()

const detail = ref({ category: { id: '0' } })
const checked_image = ref('')
const previewSrcList = ref([])

onMounted(() => {
  queryDetail()
  userStore.getUserInfo()
})

const goBack = () => {
  emit('dismissPage', null)
}

const checkDownload = () => {
  if (userStore.loginStatus) {
    if (userStore.is_material_member == 0) {
      ElMessage({
        message: '当前账号未激活，请联系对应的工作人员',
        type: 'warning',
      })
    } else {
      ElMessage({
        message: '当前账号无权限，请联系对应的工作人员',
        type: 'warning',
      })
    }
  } else {
    userStore.setDialogLoginStatus(true)
  }
}
const checked_image_fn = (item) => {
  checked_image.value = item.url
}

const queryDetail = () => {
  materialsDetail({ params: { id: props.id } }).then(res => {
    if (res.code == 200) {
      if (res.data.properties == null) {
        res.data.properties = []
      }
      detail.value = res.data

      if (detail.value.details.length > 0) {
        checked_image.value = detail.value.details[0].url
      }
      for (let i in detail.value.details) {
        previewSrcList.value.push(detail.value.details[i].url)
      }
    }
  })
}

</script>

<style scoped lang="scss">
.download-btn {
  font-size: 20px;
}

.download-vip-level-tip {
  font-size: 12px;
  margin-top: 8px;
}
</style>