import { message } from "antd";
import axios from "axios";
import useUserStore from "../store/useUserStore";

let baseUrl = import.meta.env.VITE_BASE_API_URL;

//请求地址
axios.defaults.baseURL = baseUrl;

//设置超时时间
axios.defaults.timeout = 30000;
// post请求头
axios.defaults.responseType = "json";
axios.defaults.headers.post["Content-Type"] = "application/json";
axios.defaults.withcredentials = true;

//请求拦截(请求发出前处理请求)
axios.interceptors.request.use(
  (config) => {
    config.headers["x-token"] = localStorage.getItem("x-token");
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

// 响应拦截器（处理响应数据）
axios.interceptors.response.use(
  (res) => {
    const ret = res.data;
    if (ret.code == 10003) {
      message.error(ret.msg || "系统错误");
      // token无效
      useUserStore
        .getState()
        .logout()
        .then(() => {})
        .finally(() => {
          if (window.location.pathname !== "/login") {
            location.href = "/login";
          }
        });
    } else {
      // ElMessage({
      //   showClose: true,
      //   message: ret.msg || "系统错误",
      //   type: "error",
      //   duration: 4000,
      // });
    }
    return res; //Promise.reject(ret.msg)
  },
  (error) => {
    // ElMessage({
    //   showClose: true,
    //   message: error.message,
    //   type: "error",
    //   duration: 3000,
    // });
    return Promise.reject(error);
  },
);

// 封装get方法
export function sendGet(url, data = {}) {
  return new Promise((resolve, reject) => {
    axios
      .get(url, { params: data })
      .then((res) => {
        resolve(res.data);
      })
      .catch((err) => {
        reject(err.data);
      });
  });
}

// 封装post方法
export function sendPost(url, params = {}) {
  return new Promise((resolve, reject) => {
    axios
      .post(url, params)
      .then((res) => {
        resolve(res.data);
      })
      .catch((err) => {
        reject(err.data);
      });
  });
}

// 封装post方法
// function put(url, params) {
//     return new Promise((resolve, reject) => {
//         axios.put(url, params).then(res => {
//             resolve(res.data);
//         }).catch(err => {
//             reject(err.data);
//         })
//     });
// }

//对外接口
// export function request({ url, method, data }) {
//     if (method == 'get') {
//         return get(url, data);
//     } else if (method == 'post') {
//         return post(url, data);
//     } else if (method == 'put') {
//         // return put(url, data);
//     }
// }
