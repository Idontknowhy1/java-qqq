import request from '../request'

//广告
export function bannersList(params) {
    return request({ url: '/v1/home/banners',method: 'get',data: params})
}