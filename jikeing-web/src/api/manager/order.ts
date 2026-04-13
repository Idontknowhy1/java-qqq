import request from '../request'

const baseUrl = import.meta.env.VITE_JAVA_BASE_API
const adminBaseUrl = import.meta.env.VITE_JAVA_ADMIN_BASE_API

export function selectUserOrderList(params) {
    return request({ url: adminBaseUrl + '/order/v1/user-payed-order-list',method: 'get', data: params})
}
