import request from '../request'

//广告
export function bannersList(params) {
    return request({ url: '/admin/v1/banner/list',method: 'get',data: params})
}

export function createBanners(params) {
    return request({ url: '/admin/v1/banner/create',method: 'post',data: params})
}

export function deleteBanners(params) {
    return request({ url: '/admin/v1/banner/delete',method: 'post',data: params})
}

export function bannersDetail(params) {
    return request({ url: '/admin/v1/banner/detail',method: 'get',data: params})
}

export function updateBanners(params) {
    return request({ url: '/admin/v1/banner/update',method: 'post',data: params})
}


