import request from '../request'

//
export function baidupanList(params) {
    return request({ url: '/v1/baidupan/filelist',method: 'get',data: params})
}
