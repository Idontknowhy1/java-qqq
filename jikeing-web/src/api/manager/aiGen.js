import request from '../request'

const baseUrl = import.meta.env.VITE_JAVA_BASE_API

export function aiAdminTaskList(params) {
    return request({ url: baseUrl + '/v1/nanotask/admin/page-list',method: 'get',data: params})
}