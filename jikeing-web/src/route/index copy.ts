import { createWebHashHistory, createWebHistory, createRouter } from 'vue-router'
import Layout from '../layout/default/Main.vue'
import HomePage from '../pages/home/Index.vue'
import AccountBinding from '../pages/home/AccountBinding.vue'
import Training from '../pages/home/Training.vue'
import Download from '../pages/home/Download.vue'
import Demand from '../pages/home/Demand.vue'
import MaterialDetail from '../pages/home/MaterialDetail.vue'
import LoginPage from '../pages/login/LoginPage.vue'
import ModuleMain from '../layout/default/ModuleMain.vue'
import Account from '../pages/manager/settings/Account.vue'
import NotFoundPage from '../pages/NotFoundPage.vue'
import MaterialList from '../pages/manager/resource/MaterialList.vue'
import BannerList from '../pages/manager/resource/BannerList.vue'
import MaterialEdit from '../pages/manager/resource/MaterialEdit.vue'
import BannerEdit from '../pages/manager/resource/BannerEdit.vue'
import UserVip from '../pages/manager/resource/user/UserVip.vue'
import AIGen from '../pages/home/ai-generate/AIGenList.vue'
import AiGeneratePage from '../pages/home/ai-generate/AiGeneratePage.vue'
import AIGenList from '../pages/manager/resource/ai-gen/AIGenList.vue'
import AdminMaterialSquareList from '../pages/manager/resource/images/AdminMaterialSquareList.vue'
import AdminUploadSplitAuditList from '../pages/manager/resource/images/AdminUploadSplitAuditList.vue'
import SearchUserOrder from '../pages/manager/resource/order/SearchUserOrder.vue'
import SingleUserScore from '../pages/manager/resource/user/SingleUserScore.vue'
import RankPage from '../pages/home/rank/RankPage.vue'
import OutterSearchUserOrder from '../pages/manager/resource/order/OutterSearchUserOrder.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/home',
    children: [{
      path: 'home',
      name: 'home',
      meta: {
        title: '首页', header: false, leftMenu: false, footer: true
      },
      component: HomePage,
    },
    // {
    //   path: 'detail',
    //   name: 'detail',
    //   meta: {
    //     title: '详情页', header: false, leftMenu: false, footer: true
    //   },
    //   component: MaterialDetail,
    // }, 
    {
      path: '/accountBind',
      name: 'accountBind',
      meta: {
        title: '账户绑定',
        header: false,
        leftMenu: false,
        footer: false
      },
      component: AccountBinding,
    },
    {
      path: '/training',
      name: 'training',
      meta: {
        title: '课程培训',
        header: false,
        leftMenu: false,
        footer: false
      },
      component: Training,
    }, {
      path: 'download',
      name: 'download',
      meta: {
        title: '软件下载',
        header: false,
        leftMenu: false,
        footer: false
      },
      component: Download,
    }, {
      path: 'demand',
      name: 'demand',
      meta: {
        title: '需求提交',
        header: false,
        leftMenu: false,
        footer: false
      },
      component: Demand,
    }, {
      path: 'ai-gen',
      name: 'ai-gen',
      meta: {
        title: 'AI辅助',
        header: false,
        leftMenu: false,
        footer: false
      },
      component: AiGeneratePage,
    }]
  },
  {
    path: '/login',
    name: 'login',
    meta: {
      title: '即课登录',
      header: false,
      leftMenu: false,
      footer: false
    },
    component: LoginPage,
  },
  {
    path: '/manage',
    name: 'manage',
    component: Layout,
    redirect: '/manage/resource',
    children: [
      {
        path: 'resource',
        name: 'resource',
        meta: {
          title: '资源管理', header: true, leftMenu: false, footer: true
        },
        redirect: '/manage/resource/material',
        component: ModuleMain,
        children: [
          {
            path: 'material',
            name: 'material',
            meta: {
              title: '素材列表',
              header: true,
              leftMenu: true,
              footer: true
            },
            component: MaterialList
          },
          {
            path: 'editMaterial',
            name: 'editMaterial',
            meta: {
              title: '编辑素材',
              header: true,
              leftMenu: true,
              footer: true
            },
            component: MaterialEdit
          },
          {
            path: 'admin-ai-gen-list',
            name: 'adminAiGenList',
            meta: {
              title: 'Ai任务列表',
              header: true,
              leftMenu: true,
              footer: true
            },
            component: AIGenList
          },
          {
            path: 'admin-material-square-list',
            name: 'adminMaterailSquareList',
            meta: {
              title: '素材广场',
              header: true,
              leftMenu: true,
              footer: true
            },
            component: AdminMaterialSquareList
          },
          {
            path: 'admin-upload-split-list',
            name: 'adminUploadSplitList',
            meta: {
              title: '上传拆分',
              header: true,
              leftMenu: true,
              footer: true
            },
            component: AdminUploadSplitAuditList
          },
          {
            path: 'banner',
            name: 'banner',
            meta: {
              title: '广告列表',
              header: true,
              leftMenu: true,
              footer: true
            },
            component: BannerList
          },
          {
            path: 'editBanner',
            name: 'editBanner',
            meta: {
              title: '编辑广告',
              header: true,
              leftMenu: true,
              footer: true
            },
            component: BannerEdit
          },
          {
            path: 'user-vip',
            name: 'user-vip',
            meta: {
              title: '用户会员',
              header: true,
              leftMenu: true,
              footer: true
            },
            component: UserVip
          },
          {
            path: 'user-score',
            name: 'user-score',
            component: SingleUserScore
          },
          {
            path: 'search-user-orders',
            name: 'searchUserOrder',
            component: SearchUserOrder
          },
          {
            path: ':path*', // 匹配所有子路径
            redirect: '/manage/resource/material',
            component: NotFoundPage
          }
        ]
      },
      {
        path: 'setting',
        name: 'setting',
        redirect: '/manage/setting/account',
        meta: {
          title: 'setting',
          header: true,
          leftMenu: false,
          footer: true
        },
        component: ModuleMain,
        children: [
          {
            path: 'account',
            name: 'account',
            meta: {
              title: '帐户信息',
              header: true,
              leftMenu: true,
              footer: true
            },
            component: Account
          }
        ]
      }
    ]
  },
  {
    path: '/search-user-orders',
    name: 'searchUserOrders',
    component: OutterSearchUserOrder
  },
  {
    path: '/rank-video-list',
    name: 'rankVideoList',
    component: RankPage
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router