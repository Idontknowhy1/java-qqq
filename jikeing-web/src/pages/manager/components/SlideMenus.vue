<template>
    <div class="menu">
        <div class="sec_menu-list" v-if="userStore.sec_menu != null && userStore.sec_menu.length > 0">
            <el-menu router class="el-menu-vertical-demo" @select="handleSelect" :default-active="sec_menu_index">
                <el-menu-item :index="item.path" v-for="(item, key) in userStore.sec_menu" :key="key">
                    <span slot="title">{{ item.name }}</span>
                </el-menu-item>
            </el-menu>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '../../../store/useUserStore';
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue';

const userStore = useUserStore()

const route = useRoute();
const router = useRouter();

// 响应式数据
const isCollapse = ref(false);
const menu_index = ref('');
const sec_menu_index = ref('');
const action = ref('');
const app_name = ref('二级菜单');
const open_icon = ref(new URL('../../assets/images/open_icon.png', import.meta.url).href);

// 计算属性
const menu = computed(() => userStore.menu);

// 方法
const handleBackButton = () => {
    toggleMenu(route.path);
};

const toggleMenu = (pathListArr) => {
    menu_index.value = pathListArr;
    let pathList = pathListArr.split('/');

    if (pathList.length > 3) {
        pathList = pathList.slice(1, 3);
        menu_index.value = '/' + pathList.join('/');
    }

    sec_menu_index.value = pathListArr;
    let sec_menu_list = [];

    for (let i in menu.value) {
        if (menu.value[i].path === menu_index.value) {
            if (menu.value[i].children && menu.value[i].children.length > 0) {
                sec_menu_list = menu.value[i].children;
                sec_menu_index.value = sec_menu_list.value[0]?.path || '';

                for (let j in sec_menu_list) {
                    if (sec_menu_list[j].path === pathListArr) {
                        sec_menu_index.value = pathListArr;
                    }
                }
                break;
            } else {
                sec_menu_list = [];
            }
        }
    }
};

const handleSelect = (key) => {
    toggleMenu(key);
    router.push({ path: key });
};

// 监听路由变化
watch(
    () => route.path,
    (newPath) => {
        if (newPath !== '') {
            toggleMenu(newPath);
        }
    },
    { immediate: true } // 立即执行一次
);

// 生命周期钩子
// onMounted(() => {
//     window.addEventListener('popstate', handleBackButton);
// });

// onBeforeUnmount(() => {
//     window.removeEventListener('popstate', handleBackButton);
// });

</script>
<style scoped lang="scss">
::v-deep .el-menu {
    border: 0 !important;
    background: none;
}

::v-deep .el-menu-item,
.el-submenu__title {
    height: 40px;
    line-height: 40px;
}

.sec_menu-list {
    ::v-deep .el-menu-item {
        text-align: center;
        padding-left: 15px !important;
        border-radius: 5px;
        //color:#333;
    }
}

::v-deep .is-active {
    background: #ECF5FF;
}

.menu {
    height: 100%;
    position: relative;
    border: 0px solid #000;
    margin: 0px 0px 0 0px;
    border-radius: 0px;

    .menu_list_width {
        width: 130px;
    }

    .sec_menu-list {
        width: 120px;
        border: 0px solid #000;
        background: #fff;
        border-radius: 5px;
        margin: 0px 0 0 15px;
        color: #333;

        .menu_item_title {
            width: 100%;
            //line-height: 38px;
            text-align: center;
            font-size: 14px;
            line-height: 32px;
            background: #00907F;
            color: #fff;
            border-top-left-radius: 5px;
            border-top-right-radius: 5px;
            font-weight: bold;
        }

        .menu_item {
            width: 100%;
            line-height: 38px;
            text-align: center;
            font-size: 14px;
            cursor: pointer;
        }

        .menu_item:hover {
            background: #f0f3fc;
            color: #409EFF;
            border-radius: 5px;
        }

        .active {
            background: #f0f3fc;
            color: #409EFF;
        }
    }
}
</style>