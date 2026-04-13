import request from '../request'

const baseUrl = import.meta.env.VITE_JAVA_BASE_API
const adminBaseUrl = import.meta.env.VITE_JAVA_ADMIN_BASE_API

export function addScore(params) {
    return request({ url: adminBaseUrl + '/userscore/v1/add-score',method: 'get', data: params})
}

export function getUserScore(params) {
    return request({ url: adminBaseUrl + '/userscore/v1/get-user-score',method: 'get', data: params})
}
