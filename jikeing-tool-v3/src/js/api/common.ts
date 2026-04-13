import { sendGet } from "./api";

let baseUrl = 'https://api-v2-test.jikeing.com'

export function getVideoMaterialFolderList() {
    return sendGet(baseUrl + '/aematerialtype/v1/list?module=videoEffect')
}

export function getVideoMaterial(typeId: number, pageNum: number, pageSize: number) {
    return sendGet(baseUrl + '/aematerial/v1/page-list?typeId='+typeId+'&pageNum='+pageNum+'&pageSize='+pageSize)
}

// // 封装post方法
// export function sendPost(url, params = {}) {
//     return new Promise((resolve, reject) => {
//         axios.post(url, params).then(res => {
//             resolve(res.data);
//         }).catch(err => {
//             reject(err.data);
//         })
//     });
// }