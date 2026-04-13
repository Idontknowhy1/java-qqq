import request from "@/api/request";

const javaBaseUrl = import.meta.env.VITE_JAVA_BASE_API

export function getSceneQrcode(params) {
    return request({ url: '/v1/user/get-scene-qrcode',method: 'get',data: params})
}

export function querySceneStatus(params) {
    return request({ url: '/v1/user/query-scene-status',method: 'get',data: params})
}

export function studentInfo(params) {
    return request({ url: '/v1/user/student-info',method: 'put',data: params})
}

export function characterRequirements(params) {
    return request({ url: '/v1/materials/requirements',method: 'put',data: params})
}

export function commitAiTask(params) {
    return request({ url: javaBaseUrl + '/v1/nanotask/submit',method: 'post',data: params})
}

export function aiTaskList(params) {
    return request({ url: javaBaseUrl + '/v1/nanotask/page-list',method: 'get',data: params})
}
export function aiBatTaskDetail(url, params) {
    let _url = url || '/v1/nanotask/bat-tasks-detail'
    return request({ url: javaBaseUrl + _url,method: 'post',data: params})
}

export function uploadSplitList(params) {
    return request({ url: javaBaseUrl + '/imagesplit/v1/page-list',method: 'get',data: params})
}

export function getScoreConfig() {
    return request({ url: javaBaseUrl + '/userscore/v1/get-score-config',method: 'get',data: {}})
}

export function commitAiHp(params) {
    return request({ url: javaBaseUrl + '/v1/nanotask/hp',method: 'post',data: params})
}

export function commitAiMultiScene(taskId) {
    return request({ url: javaBaseUrl + '/v1/nanotask/multi-scene',method: 'post',data: { taskId }})
}

export function commitSoraTask(params) {
    return request({ url: javaBaseUrl + '/sorotask/v1/submit',method: 'post',data: params})
}

export function soraTaskList(params) {
    return request({ url: javaBaseUrl + '/sorotask/v1/page-list',method: 'get',data: params})
}

export function videoHpTaskList(params) {
    return request({ url: javaBaseUrl + '/sorotask/v1/hp/page-list',method: 'get',data: params})
}
export function commitVideoHpTask(params) {
    return request({ url: javaBaseUrl + '/sorotask/v1/hp/submit',method: 'post',data: params})
}
// 对已有视频任务进行高清放大
export function commitVideoTaskHp(params) {
    return request({ url: javaBaseUrl + '/sorotask/v1/task/hp',method: 'post',data: params})
}

export function getSysConfig() {
    return request({ url: javaBaseUrl + '/sysconfig/v1/getconfig',method: 'get',data: {}})
}