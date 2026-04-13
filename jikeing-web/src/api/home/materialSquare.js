import request from '../request'

const baseUrl = import.meta.env.VITE_JAVA_BASE_API

export function apiMaterailSquareList(params) {
    return request({ url: baseUrl + '/materialsquare/v1/page-list',method: 'get',data: params})
}

export function apiImageUploadSubmit(params) {
    return request({ url: baseUrl + '/imagesplit/v1/submit',method: 'post',data: params})
}