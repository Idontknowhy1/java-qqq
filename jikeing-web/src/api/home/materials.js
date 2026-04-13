import request from "@/api/request";

export function materialsList(params) {
    return request({ url: '/v1/materials',method: 'get',data: params})
}

export function materialsDetail(params) {
    return request({ url: '/v1/materials/detail',method: 'get',data: params})
}