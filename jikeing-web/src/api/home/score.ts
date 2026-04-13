import request from "@/api/request";

const javaBaseUrl = import.meta.env.VITE_JAVA_BASE_API
const adminBaseUrl = import.meta.env.VITE_JAVA_ADMIN_BASE_API

export function getUserScoreBalance(params) {
    return request({ url: javaBaseUrl + '/userscore/v1/balance-info',method: 'get',data: params})
}

export function resign() {
    return request({ url: javaBaseUrl + '/userscore/v1/daily-resign',method: 'get',data: {}})
}