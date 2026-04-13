import axios from 'axios'

let baseUrl = 'https://api.jikeing.com'

//请求地址
axios.defaults.baseURL = baseUrl

//设置超时时间
axios.defaults.timeout = 30000
// post请求头
axios.defaults.responseType = 'json'
axios.defaults.headers.post['Content-Type'] = 'application/json'
axios.defaults.withcredentials = true

//请求拦截(请求发出前处理请求)
axios.interceptors.request.use((config) => {
    const token = localStorage.getItem("token")
    if (token) {
        config.headers['x-token'] = token
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

// 响应拦截器（处理响应数据）
// axios.interceptors.response.use(
//     (res) => {
//         const ret = res.data
//         if (ret.code != 200 && !ret.key) {
//             if (ret.code == 401) {
//                 useUserStore().logout().then(() => {
//                     //location.href = '/login';
//                 })
//             } else if (ret.msg == 'record not found'){
//             } else {
//                 ElMessage({
//                     showClose: true,
//                     message: ret.msg || '系统错误',
//                     type: 'error',
//                     duration: 4000
//                 })
//             }
//             return res //Promise.reject(ret.msg)
//         }
//         return res
//     },
//     (error) => {
//         ElMessage({
//             showClose: true,
//             message: error.message,
//             type: 'error',
//             duration: 3000
//         })
//         return Promise.reject(error);
//     }
// );

// 封装get方法
export function sendGet(url, data = {}) {
    return new Promise((resolve, reject) => {
        axios.get(url, {params: data}).then(res => {
            resolve(res.data);
        }).catch(err => {
            reject(err.data);
        })
    });
}

// 封装post方法
export function sendPost(url, params = {}) {
    return new Promise((resolve, reject) => {
        axios.post(url, params).then(res => {
            resolve(res.data);
        }).catch(err => {
            reject(err.data);
        })
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
