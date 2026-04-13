module.exports = {
  // 确保content路径覆盖了你的组件和样式文件
  content: [
    "./src/**/*.{js,ts,jsx,tsx}", // 扫描所有组件文件
    "./index.html",
    "./src/**/*.scss", // 额外扫描 SCSS 文件（如果需要在其中写类名）
  ],
  theme: {
    extend: {},
  },
  plugins: [],
};
