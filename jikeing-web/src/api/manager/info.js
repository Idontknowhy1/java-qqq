import request from '../request'

const baseUrl = import.meta.env.VITE_JAVA_BASE_API

//用户登录
/*
export function getUserInfo(params) {
    return request({ url: '/api/manager/info/getUserInfo',method: 'post',data: params})
}

//设置用户信息 菜单 路由组建
export function setUserInfo(params) {
    return request({ url: '/api/manager/info/setUserInfo',method: 'post',data: params})
}
*/

export function digital(params) {
    return request({ url: '/v1/captcha/digital',method: 'post',data: params})
}

export function register(params) {
    return request({ url: '/v1/user/register',method: 'post',data: params})
}

export function login(params) {
    return request({ url: '/v1/user/login/byUsername',method: 'post',data: params})
}

export function getUserInfo(params) {
    return request({ url: baseUrl + '/v1/user/info',method: 'get',data: params})
}