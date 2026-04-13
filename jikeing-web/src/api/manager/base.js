import request from '../request'

export function putUrl(params) {
	return request({ url: '/v1/oss/put-url',method: 'post',data: params})
}

export function baidupanAuthUrl(params) {
	return request({ url: '/v1/baidupan/oauth-url',method: 'get',data: params})
}



export function fileList(params) {
	return request({ url: '/api/base/file/getList',method: 'post',data: params})
}

//获取上传路径以及上传类型
export function upload(params) {
	return request({ url: '/api/base/file/upload',method: 'post',data: params})
}

//删除文件
export function removeFile(params) {
	return request({ url: '/api/base/file/remove',method: 'post',data: params})
}

//删除文件
export function getUserTypeList(params) {
	return request({ url: '/api/base/login/getUserTypeList',method: 'post',data: params})
}

export function addGroup(params) {
	return request({ url: '/api/base/file/addGroup',method: 'post',data: params})
}

export function groupList(params) {
	return request({ url: '/api/base/file/getGroupList',method: 'post',data: params})
}

export function assocGroup(params) {
	return request({ url: '/api/base/file/assocGroup',method: 'post',data: params})
}

export function deleteFilesGroup(params) {
	return request({ url: '/api/base/file/deleteFilesGroup',method: 'post',data: params})
}

export function deleteGroup(params) {
	return request({ url: '/api/base/file/deleteGroup',method: 'post',data: params})
}

//oss直传返回的文件写入库
export function createFile(params) {
	return request({ url: '/api/base/file/createFile',method: 'post',data: params})
}

//获取验证码
export function captch(params) {
  return request({ url: '/Login/verify',method: 'post',data: params})
}



