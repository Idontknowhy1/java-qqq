import request from '../request'

//广告
export function articleList(params) {
    return request({ url: '/admin/v1/articles/list',method: 'get',data: params})
}

export function createArticle(params) {
    return request({ url: '/admin/v1/articles/create',method: 'post',data: params})
}

export function deleteArticle(params) {
    return request({ url: '/admin/v1/articles/delete',method: 'post',data: params})
}

export function articleStatus(params) {
    return request({ url: '/admin/v1/articles/status',method: 'post',data: params})
}

export function articleDetail(params) {
    return request({ url: '/admin/v1/articles/detail',method: 'get',data: params})
}

export function updateArticle(params) {
    return request({ url: '/admin/v1/articles/update',method: 'put',data: params})
}

export function createCategory(params) {
    return request({ url: '/admin/v1/articles/category/create',method: 'post',data: params})
}

export function deleteCategory(params) {
    return request({ url: '/admin/v1/articles/category/delete',method: 'post',data: params})
}

export function categoryList(params) {
    return request({ url: '/admin/v1/articles/category/list',method: 'get',data: params})
}

export function categoryDetail(params) {
    return request({ url: '/admin/v1/articles/category/detail',method: 'get',data: params})
}

export function updateCategory(params) {
    return request({ url: '/admin/v1/articles/category/update',method: 'post',data: params})
}



