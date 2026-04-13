import tailwindcss from "@tailwindcss/vite";
import react from "@vitejs/plugin-react";
import path from "path";
import { defineConfig } from "vite";
import tsconfigPaths from "vite-tsconfig-paths";

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss(), tsconfigPaths()],
  server: {
    host: "0.0.0.0", // 明确指定为本地回环地址
    port: 5175, // 确保端口未被占用
    open: true, // 启动后自动在浏览器中打开（可选，通常在本机）
  },

  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  css: {
    postcss: {
      plugins: [
        // postcsspxtorem({
        //   rootValue: 1920 / 10, // 根据设计稿设定。设计稿是375px，通常设置为 375/10 = 37.5
        //   propList: ["*"], // 需要转换的属性，这里选择全部都进行转换
        //   exclude: /node_modules/i, // 忽略 node_modules 目录，防止第三方库样式被转换
        //   mediaQuery: false, // 允许媒体查询中转换
        //   selectorBlackList: [".norem", "html"], // 过滤掉.norem-开头的class，不进行rem转换
        // }),
      ],
    },
  },
});
