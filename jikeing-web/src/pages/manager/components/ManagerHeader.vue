<template>
    <div class="header">
        <div class="header-nav">
            <div class="left">
                <!--    <img src="@/assets/images/logo.png" />-->
                <div class="name">
                    <router-link :to="{ path: '/' }">
                        <h1>即课资源平台</h1>
                    </router-link>
                </div>
                <div class="nav">
                    <ul>
                        <li @click="goto(item)" :class="{ 'active': currentRoutePath.includes(item.path) }"
                            v-for="(item, key) in menu" :key="key">{{ item.name }}
                        </li>
                    </ul>
                </div>
            </div>
            <div class="right">
                <div class="user-info">
                    <div class="login_text nickname" v-if="userStore.loginStatus">
                        {{ userStore.username }}
                    </div>
                    &nbsp; &nbsp;
                    <div v-if="userStore.loginStatus">
                        <el-popover placement="bottom" width="150" trigger="hover">
                            <template #default>
                                <div class="popover_user_info"
                                    style="text-align: center;line-height: 30px;color:#00907F;cursor: pointer;">
                                    <!-- <div @click="goto('/manage/info', 1)">个人信息</div> -->
                                    <div @click="() => userStore.logout()">退出登录</div>
                                </div>
                            </template>
                            <template #reference>
                                <img :src="userStore.avatar" class="login-avatar"
                                    v-if="userStore.avatar != null && userStore.avatar != '' && userStore.avatar != 'undefined'" />
                                <img :src="default_avatar" class="login-avatar" v-else />
                            </template>
                        </el-popover>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../../../store/useUserStore'
import default_avatar from '@/assets/images/default-avatar.jpg'
import { onMounted, ref, watch } from 'vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const currentRoutePath = ref('/manage/resource')

watch(route, (newValue) => {
    currentRoutePath.value = newValue.path
})

// 菜单数据
const menu = [
    {
        path: '/manage/resource',
        name: '资源',
        children: [
            { id: '1', name: '素材管理', path: '/manage/resource/material' },
            { id: '2', name: '广告管理', path: '/manage/resource/banner' },
            { id: '3', name: '用户会员', path: '/manage/resource/user-vip' },
            { id: '3-2', name: '用户积分', path: '/manage/resource/user-score' },
            { id: '4', name: 'Ai任务列表', path: '/manage/resource/admin-ai-gen-list' },
            { id: '5', name: '素材广场', path: '/manage/resource/admin-material-square-list' },
            { id: '6', name: '上传拆分审核', path: '/manage/resource/admin-upload-split-list' },
            { id: '6', name: '用户订单查询', path: '/manage/resource/search-user-orders' },
            
        ]
    },
    {
        path: '/manage/setting',
        name: '设置',
        children: [
            { id: '1', name: '用户信息', path: '/manage/setting/account' },
        ]
    }
]

const goto = (item) => {
    setSecMenu(item)
    router.push(item.path)
}

const setSecMenu = (item) => {
    userStore.setSecMenu(item.children)
}

onMounted(() => {
    currentRoutePath.value = window.location.href
})

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
        align-items: center;

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
            height: 100%;
            border: 0px solid #000;
            display: flex;
            cursor: pointer;
            justify-content: space-between;
            align-items: center;

            .nav {
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
                align-items: center;

                //overflow:hidden;
                .nickname {
                    display: flex;
                    align-items: center;
                    width: 150px;
                    font-size: 14px;
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
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