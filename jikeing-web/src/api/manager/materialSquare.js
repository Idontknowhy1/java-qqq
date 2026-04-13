import request from '../request'

const baseUrl = import.meta.env.VITE_JAVA_BASE_API

export function apiMaterailSquarePageList(params) {
    return request({ url: baseUrl + '/materialsquare/v1/page-list',method: 'get',data: params})
}

export function apiAddMaterailToSquare(params) {
    return request({ url: baseUrl + '/materialsquare/v1/save',method: 'post',data: params})
}

export function apiDeleteMaterailFromSquare(params) {
    return request({ url: baseUrl + '/materialsquare/v1/delete',method: 'get',data: params})
}

export function apiAdminUploadSplitAuditList(params) {
    return request({ url: baseUrl + '/imagesplit/v1/admin/page-list',method: 'get',data: params})
}

export function apiAdminUploadSplitStateChange(params) {
    return request({ url: baseUrl + '/imagesplit/v1/admin/update-state',method: 'post',data: params})
}
export function apiAdminUploadSplitRefuse(params) {
    return request({ url: baseUrl + '/imagesplit/v1/admin/refuse',method: 'post',data: params})
}