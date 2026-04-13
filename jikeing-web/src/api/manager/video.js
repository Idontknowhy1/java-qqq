import request from '../request'

export function getOSSToken (params) {
    return request({ url: '/admin/v1/vod/upload-auth',method: 'post',data: params})
}

export function createVideo (params) {
    return request({ url: '/admin/v1/videos/create',method: 'post',data: params})
}

export function updateVideo (params) {
    return request({ url: '/admin/v1/videos/update',method: 'post',data: params})
}

export function videoList (params) {
    return request({ url: '/admin/v1/videos/list',method: 'get',data: params})
}

export function videoDetail (params) {
    return request({ url: '/admin/v1/videos/detail',method: 'get',data: params})
}

export function createCollection (params) {
    return request({ url: '/admin/v1/collection/create',method: 'post',data: params})
}

export function deleteCollection (params) {
    return request({ url: '/admin/v1/collection/delete',method: 'post',data: params})
}

export function collectionList (params) {
    return request({ url: '/admin/v1/collection/list',method: 'get',data: params})
}

export function collectionDetail (params) {
    return request({ url: '/admin/v1/collection/detail',method: 'get',data: params})
}

export function updateCollection (params) {
    return request({ url: '/admin/v1/collection/update',method: 'post',data: params})
}

