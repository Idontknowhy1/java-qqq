import request from '../request'

const baseUrl = import.meta.env.VITE_JAVA_BASE_API
const adminBaseUrl = import.meta.env.VITE_JAVA_ADMIN_BASE_API

export function getUserInfoByUUid(uuid) {
    return request({ url: adminBaseUrl + '/user/v1/get-member-info-by-uuid', method: 'get', data: { params: { uuid } } })
}

export function updateMemberInfo(params) {
    return request({ url: adminBaseUrl + '/user/v1/add-vip', method: 'get', data: params })
}
