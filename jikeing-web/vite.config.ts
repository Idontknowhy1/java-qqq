import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path';
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
// export default defineConfig({
//   plugins: [
//     vue()
//   ],
//   resolve: {
//     alias: {
//       '@': path.resolve(__dirname, './src')
//     }
//   },

// })

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  // loadEnv 会读取 .env 文件。process.cwd() 返回项目根目录
  const env = loadEnv(mode, process.cwd()); //[2,5](@ref)
  // 这里可以读取所有环境变量，包括不以 VITE_ 开头的
  // const someNonClientVar = env.SOME_NON_CLIENT_VAR;

  // console.log('-----mode',mode)
  // console.log('-----process.cwd()',process.cwd())
  // console.log('-----VITE_APP_TITLE,', env)

  return {
    plugins: [
      vue(),
      tailwindcss(),
    ],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, './src')
      },
    },
    server: {
      host: '0.0.0.0'
    }
  };
});