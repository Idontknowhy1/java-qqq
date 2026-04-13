import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css' // 引入暗黑模式样式 [1,2,6](@ref)
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import route from './route/index'
import HeaderNav from '@/pages/home/components/HeaderNav.vue';


import './assets/scss/index.scss'
import './assets/scss/themes/black/index.scss'
import './index.css'
import './assets/scss/reset.css'

const pinia = createPinia()

const app = createApp(App)
app.use(ElementPlus, {
    locale: zhCn,
})
app.use(pinia)
app.use(route)

app.component('HeaderNav', HeaderNav)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 优化后的 v-click-outside 指令
app.directive('click-outside', {
  beforeMount(el, binding) {
    el.clickOutsideEvent = function(event) {
      // 使用 composedPath() 获取点击的完整冒泡路径
      // 如果路径中包含 el (组件本身)，说明是点击内部，不触发关闭
      if (event.composedPath().includes(el)) {
        return;
      }
      // 否则，执行关闭回调
      binding.value(event);
    };
    document.addEventListener('click', el.clickOutsideEvent);
  },
  unmounted(el) {
    document.removeEventListener('click', el.clickOutsideEvent);
  }
});

app.mount('#app')
