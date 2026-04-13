<template>
    <div class="header">
        <div class="header-nav">
            <div class="left">
                <div class="name">
                    <router-link :to="{ path: '/' }">
                        <h1>即课资源平台</h1>
                    </router-link>
                </div>
                <div class="nav">
                    <ul>
                        <li @click="goto(item)" :class="{ 'active': currentNav == item.path }"
                            v-for="(item, key) in menu" :key="key">{{ item.name }}
                        </li>
                    </ul>
                </div>
            </div>
            <div class="right">
                <div class="nav">
                    <!-- <ul>
                  <li @click="goto('/user/workspace', 1)" :class="{'active': currentNav == 'user'}">会员中心</li>
               </ul>-->
                </div>
                <div class="user-info">
                    <div class="login_text" v-if="loginStatus == 0">
                        <el-button type="primary" size="mini" @click="loginFn()">去登录</el-button>
                    </div>
                    <div class="login_text nickname" v-if="loginStatus == 1">
                        {{ username }}
                    </div>
                    &nbsp; &nbsp;
                    <div v-if="loginStatus == 1">
                        <el-popover placement="bottom" width="150" trigger="hover">
                            <div class="popover_user_info"
                                style="text-align: center;line-height: 30px;color:#00907F;cursor: pointer;">
                                <div @click="() => goto('/manage/info', 1)">个人信息</div>
                                <div @click="logout">退出登录</div>
                            </div>
                            <img :src="avatar" slot="reference" class="login-avatar"
                                v-if="avatar != null && avatar != '' && avatar != 'undefined'" />
                            <img :src="default_avatar" slot="reference" class="login-avatar" v-else />
                        </el-popover>
                    </div>
                    <!--          <div @click="loginFn()" v-else>
            <img src="@/assets/images/login-icon.png" />
          </div>-->
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../../store/useUserStore'
import '../../assets/scss/themes/black/header.scss'

const userStore = useUserStore()
const route = useRoute()
const router = useRouter()

// 计算属性
const loginStatus = computed(() => userStore.loginStatus)
const avatar = computed(() => userStore.avatar || defaultAvatar.value)
const username = computed(() => userStore.username)

// 响应式数据
const currentNav = ref('/manage/resource')
const defaultAvatar = ref(new URL('../../assets/images/default-avatar.jpg', import.meta.url).href)
const menu = ref([
    {
        path: '/manage/resource',
        name: '资源',
        children: [
            { id: '1', name: '广告管理', path: '/manage/resource/banner' },
            { id: '2', name: '素材管理', path: '/manage/resource/material' }
        ]
    },
    {
        path: '/manage/setting',
        name: '设置',
        children: []
    }
])

const dialog = ref({
    loginStatus: false,
    addressStatus: false
})

// 方法和逻辑
const setActive = (path) => {
    const pathList = path.split('/')
    if (pathList.length > 3) {
        currentNav.value = '/' + pathList.slice(1, 3).join('/')
    } else {
        currentNav.value = path
    }
}

const setSecMenu = () => {
    const secMenu = []
    for (const item of menu.value) {
        if (item.path === currentNav.value && item.children?.length) {
            secMenu.push(...item.children)
            break
        }
    }
    userStore.setSecMenu(secMenu)
}

const goto = (item) => {
    if (!loginStatus.value) {
        loginFn()
        return
    }

    setActive(item.path)
    router.push({ path: item.path })
}

const loginFn = () => {
    dialog.value.loginStatus = true
}

const logout = async () => {
    try {
        await userStore.logout()
        router.push('/')
    } catch (error) {
        console.error('Logout failed:', error)
    }
}

// 生命周期钩子和监听
onMounted(() => {
    setActive(route.path)
    setSecMenu()
})

watch(
    () => route.path,
    (newPath) => {
        setActive(newPath)
        setSecMenu()
    },
    { immediate: true }
)


</script>
<style scoped lang="scss">
::v-deep .router-link-active {
    color: #00907F;
    font-weight: 800;
}

::v-deep .el-popover__reference {
    margin: 10px 13px 0 0px !important;
    cursor: pointer;
}

.header {
    position: relative;
    width: 100%;
    height: 65px;
    border: 0px solid #ff0000;
    //position: absolute;
    display: block;
    background: #f3f3f3;

    .header-nav {
        position: relative;
        width: 98%;
        height: 55px;
        border: 0px solid #ccc;
        margin: 0px auto 0px;
        background: #fff;
        border-radius: 40px;
        box-shadow: 0 0 1px #ccc;
        display: flex;
        justify-content: space-between;

        .left {
            width: 80%;
            height: 100%;
            border: 0px solid #ff0000;
            display: flex;
            line-height: 55px;
            cursor: pointer;
            justify-content: flex-start;

            img {
                // width: 30px;
                margin: 10px 10px 10px 20px;
            }

            div {
                display: flex;
                margin-left: 20px;
                border: 0px solid #ccc;

                h1 {
                    font-size: 20px;

                    span {
                        font-size: 20px;
                    }

                    img {
                        width: 6px;
                        margin: 0 5px 0 5px;
                    }
                }

                h3 {
                    font-size: 14px;
                    margin: 2px 0 0 10px;
                    color: #333333;
                }
            }

            .nav {
                width: 70%;
                height: 100%;
                border: 0px solid #ff0000;
                display: flex;
                margin-left: 30px;

                ul {
                    width: 100%;
                    height: 100%;
                    display: flex;
                    line-height: 55px;
                    justify-content: flex-start;

                    a {
                        color: #000;
                    }

                    li {
                        margin-right: 20px;
                        padding: 0 8px 0 8px;
                        border: 0px solid #ccc;
                        cursor: pointer;
                        font-size: 14px;
                    }

                    .active {
                        height: 53px;
                        color: #00907F;
                        font-weight: bold;
                        border-bottom: 2px solid #00907F;
                    }
                }
            }

        }

        .right {
            width: 20%;
            height: 100%;
            border: 0px solid #000;
            display: flex;
            cursor: pointer;
            justify-content: space-between;

            .nav {
                width: 12%;
                height: 100%;
                border: 0px solid #ff0000;
                display: flex;

                ul {
                    width: 100%;
                    height: 100%;
                    display: flex;
                    line-height: 55px;
                    justify-content: flex-end;

                    a {
                        color: #000;
                    }

                    li {
                        margin-right: 20px;
                        padding: 0 8px 0 8px;
                        border: 0px solid #ccc;
                        cursor: pointer;
                        font-size: 14px;
                    }

                    .active {
                        height: 53px;
                        color: #00907F;
                        font-weight: bold;
                        border-bottom: 2px solid #00907F;
                    }
                }
            }

            .user-info {
                max-width: 80%;
                height: 100%;
                border: 0px solid #000;
                display: flex;
                z-index: 100000;

                //overflow:hidden;
                .nickname {
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    display: -webkit-box;
                    -webkit-line-clamp: 1;
                    -webkit-box-orient: vertical;
                    color: #333;
                }

                div {
                    //width: 90%;
                    height: 55px;
                    display: flex;
                    border: 0px solid #ff0000;
                    line-height: 50px;
                    justify-content: flex-end;
                    cursor: pointer;

                    img {
                        width: 35px;
                        height: 35px;
                        margin: auto;
                        cursor: pointer;
                    }

                    .login-avatar {
                        width: 35px;
                        height: 35px;
                        border-radius: 20px;
                        z-index: 10 !important;
                    }
                }

                .login_text {
                    font-size: 14px;
                    display: inline-block;
                    margin-top: 2px;
                }

                .search-icon {
                    img {
                        width: 22px;
                        height: 22px;
                        margin: auto;
                        cursor: pointer;
                    }
                }
            }
        }
    }
}
</style>