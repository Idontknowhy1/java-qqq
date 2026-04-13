import request from '../request'

export function categoriesList(params) {
    return request({ url: '/v1/materials/categories',method: 'get',data: params})
}


export function coursesList(params) {
    return request({ url: '/v1/courses/list',method: 'get',data: params})
}

export function coursesDetail(params) {
    return request({ url: '/v1/courses/detail',method: 'get',data: params})
}

export function coursesComments(params) {
    return request({ url: '/v1/courses/comments',method: 'get',data: params})
}

export function coursesLessons(params) {
    return request({ url: '/v1/courses/lessons',method: 'get',data: params})
}

export function categoryList(params) {
    return request({ url: '/admin/v1/materials/category/list',method: 'get',data: params})
}

export function deleteCategory(params) {
    return request({ url: '/admin/v1/materials/category/delete',method: 'post',data: params})
}

export function createCategory(params) {
    return request({ url: '/admin/v1/materials/category/create',method: 'post',data: params})
}

export function updateCategory(params) {
    return request({ url: '/admin/v1/materials/category/update',method: 'post',data: params})
}

export function materialsList(params) {
    return request({ url: '/admin/v1/materials/list',method: 'get',data: params})
}

export function materialsDetail(params) {
    return request({ url: '/admin/v1/materials/detail',method: 'get',data: params})
}

export function createMaterials(params) {
    return request({ url: '/admin/v1/materials/create',method: 'post',data: params})
}

export function updateMaterials(params) {
    return request({ url: '/admin/v1/materials/update',method: 'post',data: params})
}

export function deleteMaterials(params) {
    return request({ url: '/admin/v1/materials/delete-batch',method: 'post',data: params})
}

