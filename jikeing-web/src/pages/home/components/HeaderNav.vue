<template>
  <div class="front-header">
    <div class="header-nav border-b-[1px] border-[#262D38]">
      <div class="left">
        <div class="center">
          <router-link :to="{ path: '/' }">
            <img src="@/assets/images/logo.png" class="logo" />
          </router-link>
          <ul>
            <li :class="{ 'active': currentNav == item.code }" v-for="(item, key) in menu" :key="key"
              @mouseover="handleMouseMove(key, 'over')" @mouseout="handleMouseMove(key, 'move')"
              @click="goto(item.code)">
              <span><img :src="item.icon"></img></span>
              <span>{{ item.name }}</span>
            </li>
          </ul>
        </div>
      </div>
      <div class="right">
        <div v-if="userStore.loginStatus === 1 && userStore.vip_level > 1"
          class="flex justify-center items-center space-x-[16px">
          <div
            class="relative flex items-center justify-center h-[32px] text-[#FFFFFF] text-[14px] mr-[20px] border border-[#80ABF6] rounded-[16px] px-[16px] gap-[10px]">

            <!-- 积分图标 -->
            <img :src="scoreImage" alt="" class="w-[16px] h-[16px]">
            <!-- 积分余额 -->
            {{ userStore.vipScore + userStore.forScore }}
            <!-- 签到浮窗 -->
            <div class="sign-popover w-[174px] h-[82px] absolute right-0 top-[50px] z-10"
              v-if="userStore.vip_level > 2 && userStore.todayResigned == false">
              <img :src="scoreResignBgImage" alt="">
              <div class="absolute h-[30px] w-[80px] top-[45px] left-[10px] cursor-pointer" @click="doResign"></div>
            </div>

          </div>

          <!-- <div v-if="userStore.vip_level > 2">
            <div class="resign-button" v-if="userStore.todayResigned">已签到</div>
            <div class="resign-button" v-if="userStore.todayResigned == false" @click="doResign">签到</div>
          </div> -->


        </div>
        <div class="user-info">
          <div v-if="userStore.loginStatus">
            <el-dropdown @command="handleCommand">
              <div>
                <img :src="userStore.avatar" slot="reference" class="login-avatar"
                  v-if="userStore.avatar != null && userStore.avatar != '' && userStore.avatar != 'undefined'" />
                <img :src="default_avatar" slot="reference" class="login-avatar" v-else />
                <i class="el-icon-arrow-down el-icon--right"></i>
              </div>
              <template #dropdown>
                <el-dropdown-menu trigger="click">
                  <!--                <el-dropdown-item command="account">个人中心</el-dropdown-item>-->
                  <!--                <el-dropdown-item command="a">我的课程</el-dropdown-item>-->
                  <el-dropdown-item command="manage" v-if="userStore.role == 'admin'">管理账户</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <div v-else class="login_icon">
            <el-button type="primary" size="small" @click="loginFn" class="login">登录</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch, onMounted, computed } from 'vue'
import { useUserStore } from '@/store/useUserStore.ts'
import { useRoute, useRouter } from 'vue-router'
import { resign } from '@/api/home/score.ts'
import '@/assets/scss/themes/black/header.scss'
import { ElMessage } from 'element-plus'
import scoreImage from '@/assets/images/nav-score-image.png'
import scoreResignBgImage from '@/assets/images/nav-score-resign-image.png'

const route = useRoute()
const router = useRouter()

const userStore = useUserStore()

// 图片资源 - 使用动态导入
const assets = import.meta.glob('@/assets/images/**/*', { eager: true })
const getAsset = (path) => {
  const key = Object.keys(assets).find(k => k.includes(path))
  return key ? assets[key].default : ''
}

// 响应式数据
const currentNav = ref('home')
const default_avatar = ref(getAsset('default-avatar.jpg') || '')

// 菜单数据结构
const menu = ref([
  {
    id: 1,
    name: '账号绑定',
    code: '/accountBind',
    icon: getAsset('accountBind.png'),
    defaultIcon: getAsset('accountBind.png'),
    checkedIcon: getAsset('accountBind_checked.png')
  },
  {
    id: 2,
    name: '课程培训',
    code: '/training',
    icon: getAsset('training.png'),
    defaultIcon: getAsset('training.png'),
    checkedIcon: getAsset('training_checked.png')
  },
  {
    id: 3,
    name: 'AI辅助',
    code: '/ai-gen',
    icon: getAsset('robot.png'),
    defaultIcon: getAsset('robot.png'),
    checkedIcon: getAsset('robot_checked.png')
  },
  {
    id: 4,
    name: '需求提交',
    code: '/demand',
    icon: getAsset('demand.png'),
    defaultIcon: getAsset('demand.png'),
    checkedIcon: getAsset('demand_checked.png')
  },
  {
    id: 5,
    name: '优秀作品',
    code: '/rank-video-list',
    icon: getAsset('excellent_works.png'),
    defaultIcon: getAsset('excellent_works.png'),
    checkedIcon: getAsset('excellent_works_checked.png')
  },
  {
    id: 6,
    name: '软件下载',
    code: '/download',
    icon: getAsset('download.png'),
    defaultIcon: getAsset('download.png'),
    checkedIcon: getAsset('download_checked.png')
  }
])

// 初始设置活动导航
onMounted(() => {
  console.log('width:', window.screen.width)
  setActive(route.path)
})

// 监听路由变化
watch(() => route.path, (newPath) => {
  setActive(newPath)
})

// 方法定义
const handleCommand = (command) => {
  switch (command) {
    case 'logout':
      logout()
      break
    case 'account':
      router.push({ path: '/user/info' })
      break
    case 'manage':
      // router.push({ path: '/manage/resource' })
      window.open('https://ht.jikeing.com/', '_blank')
      break
  }
}

const handleMouseMove = (index, type) => {
  const item = menu.value[index]

  switch (type) {
    case 'over': // 移入
      item.icon = item.checkedIcon
      break
    case 'move': // 移出
      if (item.code !== currentNav.value) {
        item.icon = item.defaultIcon
      }
      break
  }
}

const setActive = (path) => {
  const pathList = path.split('/')
  if (pathList.length > 3) {
    currentNav.value = '/' + pathList.slice(1, 3).join('/')
  } else {
    currentNav.value = '/' + (pathList[1] || '')
  }

  menu.value.forEach(item => {
    if (item.code === currentNav.value) {
      item.icon = item.checkedIcon
    } else if (item.icon !== item.defaultIcon) {
      item.icon = item.defaultIcon
    }
  })
}

const goto = (code) => {
  if (userStore.loginStatus == false) {
    loginFn()
    return
  }
  if (code) {
    currentNav.value = code
  }
  router.push({ path: code, query: {} })
}

const loginFn = () => {
  userStore.setDialogLoginStatus(true)
}

const logout = async () => {
  try {
    await userStore.logout()
    router.push('/')
  } catch (error) {
    console.error('Logout failed:', error)
  }
}

const doResign = async () => {
  try {
    let res = await resign()
    if (res.code === 10000) {
      userStore.getScoreBalance()
    } else {
      console.error('签到失败,' + res.msg)
      ElMessage.error('签到失败, ' + res.msg)
    }
  } catch (err) {
    console.error('签到失败', err)
    ElMessage.error('签到失败')
  }
}

</script>
<style scoped lang="scss">
::v-deep .router-link-active {
  color: #00907F;
  font-weight: 800;
}

::v-deep .el-popover__reference {
  margin: 5px 13px 0 0px !important;
  cursor: pointer;
}

.right {
  display: flex;
  align-items: center;

  .resign-button {
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 16px;
    width: 80px;
    height: 30px;
    border: 1px solid white;
    border-radius: 15px;
    margin-right: 16px;
  }
}

.front-header {
  width: 100%;
  border: 0px solid #ff0000;
  display: block;
  // z-index: 100000;
  // background: #1F2937;

  .header-nav {
    width: 100%;
    height: 55px;
    border-bottom: 1px solid #262D38;
    display: flex;
    //margin: 0 auto;

    .left {
      width: 70%;
      border: 0px solid #ff0000;
      display: flex;
      //justify-content: center;
      position: relative;
      margin: 0 0 0 0px;

      .center {
        display: flex;
        justify-content: center;
        border: 0px solid #fff;

        .logo {
          width: 84px;
          height: auto;
          margin: 3px 10px 10px 32px;
        }

        ul {
          border: 0px solid #ff0000;
          display: flex;
          justify-content: flex-start;
          margin: 0 0 0 0px;

          li {
            cursor: pointer;
            font-size: 14px;
            font-weight: 500;
            line-height: 55px;
            letter-spacing: 0px;
            color: #6B7280;
            margin: 0 30px 0 6px;
            display: flex;
            align-items: center;
            border: 0px solid #ff0000; //1F2937

            img {
              width: 16px;
              height: 16px;
              margin: 0px 0 0 0;
            }

            span {
              height: 16px;
              line-height: 16px;
              border: 0px solid #ff0000;
              margin: 0 4px 0 0;
            }
          }

          li:hover {
            color: #1B9AF5;
            //border-bottom: 2px solid #1B9AF5;
          }

          .active {
            color: #1B9AF5;
            //border-bottom: 2px solid #1B9AF5;
          }
        }
      }
    }

    .right {
      width: 50%;
      border: 0px solid #fff;
      height: 100%;
      display: flex;
      justify-content: flex-end;

      .nav {
        width: 12%;
        border: 0px solid #ff0000;
        display: flex;
      }

      .user-info {
        position: relative;
        max-width: 80%;
        border: 0px solid #000;
        display: flex;
        margin: 0 32px 0 0;

        .nickname {
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 1;
          -webkit-box-orient: vertical;
          color: #fff;
        }

        div {
          display: flex;
          align-items: center;
          cursor: pointer;

          .login-avatar {
            width: 35px;
            height: 35px;
            border-radius: 20px;
            margin: 0px 12px 0 0px !important;
            border: 0px solid #6B7280;
          }

          .el-icon-arrow-down {
            margin: 0px 0 0 0;
            font-size: 20px;
            color: #fff;
          }
        }

        .login_text {
          font-size: 14px;
          display: inline-block;
          margin-top: 2px;
        }

        .login_icon {
          .el-button {
            margin: 0px 0px 0 10px;
          }

        }
      }
    }
  }
}

.sign-popover {
  animation: gentle-float 3s ease-in-out infinite;
}

@keyframes gentle-float {

  0%,
  100% {
    transform: translateY(0);
    /* 起始和结束位置 */
  }

  50% {
    transform: translateY(-8px);
    /* 向上飘动的最高点 */
  }
}
</style>