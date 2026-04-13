import type { Config } from "tailwindcss";

export default {
  content: [
    "./src/js/main/**/*.{html,js,ts,vue,tsx,scss,css}",
    "./src/js/main/*.{html,js,ts,vue,tsx,scss,css}",
    "./src/js/pages/**/*.{html,js,ts,vue,tsx,scss,css}",
    "./src/js/pages/*.{html,js,ts,vue,tsx,scss,css}",
     "!./node_modules/**/*"
  ],
  theme: {
    extend: {},
  },
  plugins: [],
} satisfies Config;
