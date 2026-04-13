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