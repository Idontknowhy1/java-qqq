<template>
  <el-dialog
    v-model="visible"
    title="裁切图片"
    width="98%"
    top="6vh"
    @open="onOpen"
    :before-close="handleClose"
    append-to-body
  >
    <div class="cropper-content">
      <div class="cropper-box">
        <div class="cropper">
          <vue-cropper
            ref="cropperRef"
            :img="option.img"
            :outputSize="option.outputSize"
            :outputType="option.outputType"
            :info="option.info"
            :canScale="option.canScale"
            :autoCrop="option.autoCrop"
            :autoCropWidth="option.autoCropWidth"
            :autoCropHeight="option.autoCropHeight"
            :fixed="option.fixed"
            :fixedNumber="option.fixedNumber"
            :full="option.full"
            :fixedBox="option.fixedBox"
            :canMove="option.canMove"
            :canMoveBox="option.canMoveBox"
            :original="option.original"
            :centerBox="option.centerBox"
            :height="option.height"
            :infoTrue="option.infoTrue"
            :maxImgSize="option.maxImgSize"
            :enlarge="option.enlarge"
            :mode="option.mode"
            @realTime="realTime"
            @imgLoad="imgLoad"
          />
        </div>
        
        <!-- 尺寸和比例设置 -->
        <div class="footer_size">
          <label class="btn" for="uploads">尺寸(px)：</label>
          <span>
            宽
            <el-input-number
              size="small"
              v-model="option.autoCropWidth"
              :min="100"
              :max="2000"
              :step="10"
              controls-position="right"
              style="width: 120px;"
            />
          </span>
          <span style="margin-left: 10px;">
            高
            <el-input-number
              size="small"
              v-model="option.autoCropHeight"
              :min="100"
              :max="2000"
              :step="10"
              controls-position="right"
              style="width: 120px;"
            />
          </span>
          
          <label class="btn" for="uploads" style="margin-left: 15px;">比例：</label>
          <span>
            <el-input-number
              size="small"
              v-model="fixedNumber[0]"
              :min="1"
              :max="20"
              controls-position="right"
              style="width: 80px;"
            />
          </span>
          <span style="margin: 0 5px;">:</span>
          <span>
            <el-input-number
              size="small"
              v-model="fixedNumber[1]"
              :min="1"
              :max="20"
              controls-position="right"
              style="width: 80px;"
            />
          </span>
          <el-button size="small" type="primary" @click="applySettings" style="margin-left: 10px;">
            应用比例
          </el-button>
        </div>
        
        <!-- 操作按钮 -->
        <div class="footer-btn">
          <div class="scope-btn">
            <label class="btn" for="uploads">选择图片</label>
            <input
              type="file"
              id="uploads"
              style="position:absolute; clip:rect(0 0 0 0);"
              accept="image/png, image/jpeg, image/gif, image/jpg"
              @change="selectImg"
            />
            <el-button size="small" type="danger" plain icon="el-icon-zoom-in" @click="changeScale(0.1)">
              放大
            </el-button>
            <el-button size="small" type="danger" plain icon="el-icon-zoom-out" @click="changeScale(-0.1)">
              缩小
            </el-button>
            <el-button size="small" type="danger" plain @click="rotateLeft">
              ↺ 左旋转
            </el-button>
            <el-button size="small" type="danger" plain @click="rotateRight">
              ↻ 右旋转
            </el-button>
          </div>
          <div class="upload-btn">
            <el-button size="small" type="primary" @click="uploadImg">
              保存裁剪 <i class="el-icon-upload"></i>
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 预览效果图 -->
      <div class="show-preview">
        <div :style="previews.div" class="preview">
          <img :src="previews.url" :style="previews.img" />
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed, nextTick } from 'vue'
import { VueCropper } from 'vue-cropper'
import { ElMessage } from 'element-plus'

// 定义组件属性
const props = defineProps({
  show: Boolean,
  comName: String,
  file: File
})

// 定义组件事件
const emit = defineEmits(['update:show', 'callFn'])

// DOM 引用
const cropperRef = ref(null)

// 响应式状态
const fileName = ref('')
const fixedNumber = ref([16, 9])
const previews = ref({})
const visible = computed({
  get: () => props.show,
  set: (val) => emit('update:show', val)
})

// 裁剪选项
const option = reactive({
  img: '',
  outputSize: 1,
  outputType: 'jpeg',
  info: true,
  canScale: true,
  autoCrop: true,
  autoCropWidth: 700,
  autoCropHeight: 393.75,
  fixed: true,
  fixedNumber: [16, 9],
  full: true,
  fixedBox: true,
  canMove: true,
  canMoveBox: true,
  original: true,
  centerBox: false,
  height: true,
  infoTrue: false,
  maxImgSize: 1000,
  enlarge: 1,
  mode: '500px 500px',
  high: true
})

// 监听显示状态变化
watch(visible, (newVal) => {
  if (newVal && props.file) {
    showImg()
  }
})

// 监听比例变化
watch(fixedNumber, (newVal) => {
  option.fixedNumber = [newVal[0], newVal[1]]
}, { deep: true })

// 组件打开时
const onOpen = () => {
  if (props.file) {
    showImg()
  }
}

// 应用比例设置
const applySettings = () => {
  const ratio = fixedNumber.value[1] / fixedNumber.value[0]
  option.autoCropHeight = option.autoCropWidth * ratio
  resetCropper()
}

// 重置裁剪器
const resetCropper = () => {
  if (cropperRef.value) {
    cropperRef.value.refresh()
  }
}

// 图片加载回调
const imgLoad = (msg) => {
  console.log("工具初始化函数:", msg)
}

// 图片缩放
const changeScale = (num) => {
  if (cropperRef.value) {
    cropperRef.value.changeScale(num)
  }
}

// 向左旋转
const rotateLeft = () => {
  if (cropperRef.value) {
    cropperRef.value.rotateLeft()
  }
}

// 向右旋转
const rotateRight = () => {
  if (cropperRef.value) {
    cropperRef.value.rotateRight()
  }
}

// 实时预览
const realTime = (data) => {
  previews.value = data
}

// 显示图片
const showImg = () => {
  if (!props.file) return
  
  fileName.value = props.file.name
  const reader = new FileReader()
  
  reader.onload = (e) => {
    option.img = e.target.result
    previews.value.img = e.target.result
    
    // 确保裁剪器已初始化
    nextTick(() => {
      if (cropperRef.value) {
        cropperRef.value.refresh()
      }
    })
  }
  
  reader.readAsDataURL(props.file)
}

// 选择新图片
const selectImg = (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  
  // 验证文件类型
  if (!/\.(jpg|jpeg|png|JPG|PNG)$/.test(file.name)) {
    ElMessage.error('图片类型要求：jpeg、jpg、png')
    return
  }
  
  fileName.value = file.name
  const reader = new FileReader()
  
  reader.onload = (e) => {
    option.img = e.target.result
    previews.value.img = e.target.result
  }
  
  reader.readAsDataURL(file)
}

// 上传图片
const uploadImg = () => {
  if (!cropperRef.value) return
  
  // 获取截图的blob数据
  cropperRef.value.getCropBlob((blob) => {
    if (!blob) return
    
    // 创建文件对象
    const croppedFile = new File([blob], fileName.value, {
      type: `image/${option.outputType}`,
      lastModified: Date.now()
    })
    
    // 触发回调
    emit('callFn', {
      file: croppedFile,
      comName: props.comName
    })
    
    // 关闭对话框
    visible.value = false
  })
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
}
</script>

<style scoped lang="scss">
.cropper-content{
  display: flex;
  display: -webkit-flex;
  justify-content: flex-end;
  .cropper-box{
    flex: 1;
    width: 100%;
    border:0px solid #ff0000;
    //width: 60%;
    .cropper{
      width: auto;
      height: 500px;
    }
  }

  .show-preview{
    flex: 1;
    -webkit-flex: 1;
    display: flex;
    display: -webkit-flex;
    justify-content: center;
    border:0px solid #ff0000;
    width: 40%;
    .preview{
      overflow: hidden;
      border:1px solid #67c23a;
      background: #cccccc;
    }
  }
}
.footer_size {
  margin: 20px 0 0 0;
}
.footer-btn{
  margin: 10px 0 30px 0;
  display: flex;
  display: -webkit-flex;
  justify-content: flex-end;
  .scope-btn{
    display: flex;
    display: -webkit-flex;
    justify-content: space-between;
    padding-right: 10px;
  }
  .upload-btn{
    flex: 1;
    -webkit-flex: 1;
    display: flex;
    display: -webkit-flex;
    justify-content: center;
  }
  .btn {
    outline: none;
    display: inline-block;
    line-height: 1;
    white-space: nowrap;
    cursor: pointer;
    -webkit-appearance: none;
    text-align: center;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    outline: 0;
    -webkit-transition: .1s;
    transition: .1s;
    font-weight: 500;
    padding: 8px 15px;
    font-size: 12px;
    border-radius: 3px;
    color: #fff;
    background-color: #409EFF;
    border-color: #409EFF;
    margin-right: 10px;
  }
}
</style>