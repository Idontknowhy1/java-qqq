import { useUserStore } from "../store/useUserStore";
import { putUrl } from "@/api/manager/base";
import { ElMessage } from "element-plus";

export function deviceType() {
	const deviceType = window.navigator.userAgent.includes('Mobile') ? 'mobile' : 'pc'
	return deviceType;
}
// 日期格式化
export function parseTime(time, pattern) {
	if (arguments.length === 0 || !time) {
		return null
	}
	const format = pattern || '{y}-{m}-{d} {h}:{i}:{s}'
	let date
	if (typeof time === 'object') {
		date = time
	} else {
		if ((typeof time === 'string') && (/^[0-9]+$/.test(time))) {
			time = parseInt(time)
		} else if (typeof time === 'string') {
			time = time.replace(new RegExp(/-/gm), '/');
		}
		if ((typeof time === 'number') && (time.toString().length === 10)) {
			time = time * 1000
		}
		date = new Date(time)
	}
	const formatObj = {
		y: date.getFullYear(),
		m: date.getMonth() + 1,
		d: date.getDate(),
		h: date.getHours(),
		i: date.getMinutes(),
		s: date.getSeconds(),
		a: date.getDay()
	}
	const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
		let value = formatObj[key]
		// Note: getDay() returns 0 on Sunday
		if (key === 'a') { return ['日', '一', '二', '三', '四', '五', '六'][value] }
		if (result.length > 0 && value < 10) {
			value = '0' + value
		}
		return value || 0
	})
	return time_str
}

//element confirm重新封装
// export function confirm(param) {
//     // 默认参数
//     let config = {
//         tip: '提示',
//         content: '你确定要执行此操作么吗？',
//         btn: { confirm: '确定', cancel: '取消', },
//         type: 'warning'
//     }
//     // 如果有参数传入并且长度大于0 则替换原来的指定默认配置 
//     if (param && Object.keys(param).length) {
//         for (let item in param) {
//             config[item] = param[item]
//         }
//     }
//     return new Promise((resolve) => {
//         vm.$confirm(config.content, config.tip, {
//             confirmButtonText: config.btn.confirm,
//             cancelButtonText: config.btn.cancel,
//             type: config.type,
//             dangerouslyUseHTMLString: true
//         }).then(() => {
//             resolve()
//         }).catch(() => {
//             //vm.$message({ type: 'info', message: '已取消' })
//         })
//     })
// }

//获取复选框 下拉多选的键名 根据值
export function formatStr(val, data) {
	if (val) {
		const fieldConfig = typeof (data) == 'string' ? JSON.parse(data) : data
		let str = ''
		val.split(",").forEach(item => {
			fieldConfig.forEach(vo => {
				if (item == vo.val) {
					str += ',' + vo.key
				}
			})
		})
		return str.substr(1)
	}
}

//json对象转为url参数
export function param(json) {
	if (!json) return ''
	return cleanArray(
		Object.keys(json).map(key => {
			if (json[key] === undefined) return ''
			return encodeURIComponent(key) + '=' + encodeURIComponent(json[key])
		})
	).join('&')
}

export function cleanArray(actual) {
	const newArray = []
	for (let i = 0; i < actual.length; i++) {
		if (actual[i]) {
			newArray.push(actual[i])
		}
	}
	return newArray
}

export function param2Obj(url) {
	const search = decodeURIComponent(url.split('?')[1]).replace(/\+/g, ' ')
	if (!search) {
		return {}
	}
	const obj = {}
	const searchArr = search.split('&')
	searchArr.forEach(v => {
		const index = v.indexOf('=')
		if (index !== -1) {
			const name = v.substring(0, index)
			const val = v.substring(index + 1, v.length)
			obj[name] = val
		}
	})
	return obj
}

export function checkPermission(url) {
	const useStore = useUserStore()
	if (useStore.role_id == 1 || useStore.actions.includes(url)) {
		return true
	}
}

export function formatRichText(html) {
	let newContent = html.replace(/\<img/gi, '<img style="max-width:100%;height:auto;display:block;"');
	return newContent;
}

// export function appToLink(common_path, params) {
// 	let url = {
// 		path: common_path,
// 		query:{}
// 	}
// 	url.query = Object.assign({}, url.query, params)
// 	this.$router.push(url)
// }

export function copyData(copy_domain) {
	// 创建一个input框
	const input = document.createElement('input')
	// 将指定的DOM节点添加到body的末尾
	document.body.appendChild(input)
	// 设置input框的value值为复制的值
	const address = copy_domain
	input.setAttribute('value', address)
	// 选取文本域中的内容
	input.select()
	// copy的意思是拷贝当前选中内容到剪贴板
	// document.execCommand（）方法操纵可编辑内容区域的元素
	// 返回值为一个Boolean，如果是 false 则表示操作不被支持或未被启用
	if (document.execCommand('copy')) {
		document.execCommand('copy')
	}
	// 删除这个节点
	document.body.removeChild(input)
}

//数据尺寸计算器
// export function dataSizeCheckPlusData(form, plus_data_ob, key) { //验证增加的数据是否合格
// 	if (plus_data_ob.name == '') {
// 		this.$message({message: '请输入名称',type: 'error'})
// 		return false
// 	}
// 	if (plus_data_ob.name.length > 15) {
// 		this.$message({message: '名称长度不能超过15个字符', type: 'error'})
// 		return false
// 	}
// 	for (let i in form.data[key].list) {
// 		if (form.data[key].list[i].name == plus_data_ob.name) {
// 			this.$message({message: '名称不能重复',type: 'error'})
// 			return false
// 		}
// 	}
// 	return true
// }

export function cutChineseString(str, start, end) {
	// 将字符串转换为数组
	var chineseArray = str.split('');
	// 截取数组指定位置的字符
	var cutArray = chineseArray.slice(start, end);
	// 将数组重新拼接为字符串
	return cutArray.join('');
}

export function uploadOss(item, callback) {
	let file = item.file
	let exts = file.type.split('/')
	let param = JSON.stringify({ blob_type: 'image', ext: exts[1] })
	putUrl(param).then(res => {
		if (res.code === 200) {
			fetch(res.data.put_url, {
				method: 'PUT',
				headers: res.data.headers,
				body: file
			}).then(response => {
				if (response.status == 200) {
					// ElMessage.success('上传成功')
					if (typeof callback == 'function') {
						callback({ url: res.data.access_url })
					}
				} else {
					ElMessage.error('上传失败')
				}
			}).then(data => {
				//console.log(data)
			}).catch(error => {
				ElMessage.error('上传失败11')
				console.error('Error:', error)
			});
		}
	});
}

export const downloadFile = (url) => {
	const link = document.createElement('a');
	link.href = url; // 文件的URL地址
	link.download = 'downloaded-file'; // 建议指定文件名
	document.body.appendChild(link);
	link.click();
	document.body.removeChild(link);
}


export const copyText = (prompt) => {
    navigator.clipboard.writeText(prompt)
        .then(() => {
            ElMessage.success('文本已成功复制到剪贴板')
            console.log('文本已成功复制到剪贴板');
        })
        .catch(err => {
            console.error('复制失败: ', err);
        });
}