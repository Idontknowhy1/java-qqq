<template>
    <div class="flex flex-col items-center p-[16px] gap-[20px] border-r-[1px] border-r-[#262D38]">
        <div class="flex w-full"
            style="font-size: 18px; font-weight: bold; margin-bottom: 10px; display: flex; align-items: center; gap: 8px; color: #2d60ff;">
            <span style="">✨</span> 视频生成
        </div>

        <!-- 图生视频、文生视频 -->
        <SegmentControl customClass="h-[44px] w-full text-[14px]" textClass="text-[#FFFFFF] text-[12px]"
            :segments="segments" :selectSegment="selectSegment" @update:selectSegment="selectSegment = $event" />

        <!-- 上传图片 -->
        <div v-if="selectSegment === '图生视频'"
            class="flex flex-col justify-center items-center h-[120px] w-full bg-[#1c1f2e] rounded-[6px] cursor-pointer hover:border-[#2d60ff] hover:border-[1px] hover:border-dashed">
            <el-upload v-if="imageUrl == null || imageUrl === ''" action="#" :show-file-list="false"
                :before-upload="beforeUpload" :http-request="upload">
                <div
                    class="flex flex-col items-center justify-center gap-[8px] h-full w-full  text-[14px] font-bold text-[#6B7280]">
                    <i class="fa-regular fa-image" style="font-size: 24px; margin-bottom: 10px;"></i>
                    <div class="flex items-center justify-center">
                        点击上传图片
                    </div>
                </div>
            </el-upload>
            <div v-if="imageUrl && imageUrl !== ''" class="relative w-full h-full rounded-[6px]">
                <el-image class="w-full h-full" :src="imageUrl" fit="cover"></el-image>
                <el-icon class="upload-image-remove-btn" :size="20" color="#FF0000" @click="() => imageUrl = ''">
                    <Delete />
                </el-icon>
            </div>
        </div>

        <!-- 角色卡 -->
        <!-- 新增：角色卡区域 -->
        <div v-if="false">
            <div class="section-title">
                <span>角色卡 (Character Cards)</span>
                <span style="font-size: 12px; cursor: pointer;" @click="emit('showCharacterList')">管理 ></span>
            </div>
            <div class="character-cards">
                <!-- 角色 1 -->
                <div class="char-card selected" onclick="selectChar(this)">
                    <img src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?ixlib=rb-1.2.1&auto=format&fit=crop&w=200&q=80"
                        alt="Mike">
                    <span>男主角</span>
                </div>
                <!-- 角色 2 -->
                <div class="char-card" onclick="selectChar(this)">
                    <img src="https://images.unsplash.com/photo-1494790108377-be9c29b29330?ixlib=rb-1.2.1&auto=format&fit=crop&w=200&q=80"
                        alt="Sarah">
                    <span>女主角</span>
                </div>
                <!-- 角色 3 -->
                <div class="char-card" onclick="selectChar(this)">
                    <img src="https://images.unsplash.com/photo-1500648767791-00dcc994a43e?ixlib=rb-1.2.1&auto=format&fit=crop&w=200&q=80"
                        alt="David">
                    <span>配角 A</span>
                </div>
                <!-- 添加角色 -->
                <div class="char-card"
                    style="display: flex; align-items: center; justify-content: center; background: rgba(255,255,255,0.05);">
                    <i class="fa-solid fa-plus" style="color: #8b92a7;"></i>
                </div>
            </div>
        </div>

        <!-- 描述词输入 -->
        <div
            class="flex flex-col justify-center h-[160px] w-full bg-[#1c1f2e] p-[16px] rounded-[6px] text-[12px] gap-[8px]">
            <div class="text-[#2d60ff]">创意描述</div>
            <textarea type="text" placeholder="请输入描述词" resize="none"
                class="w-full h-full text-[#ccc] bg-transparent resize-none outline-none leading-[1.6]"
                v-model="prompt" />
        </div>

        <!-- 参数调整 -->
        <div class="flex flex-col items-center justify-center w-full gap-[12px]">

            <!-- 模型 -->
            <VideoGenerateFormProperty title="模型">
                <DropdownSetting :options="models.map(item => item.label)" v-model="selectModel" />
            </VideoGenerateFormProperty>

            <!-- 比例 -->
            <VideoGenerateFormProperty title="比例">
                <DropdownSetting :options="scales" v-model="selectScale" />
            </VideoGenerateFormProperty>

            <!-- 时长 -->
            <VideoGenerateFormProperty title="时长">
                <DropdownSetting :options="durations" v-model="selectDuration" />
            </VideoGenerateFormProperty>

            <!-- 数量 -->
            <VideoGenerateFormProperty title="数量">
                <div :class="['flex justify-center items-center w-[100px] h-[30px] rounded-[6px] p-[4px]']">
                    <div v-for="item in ['1', '2', '3', '4']" :key="item"
                        class="flex flex-1 justify-center items-center text-[#6B7280] h-full rounded-[6px] cursor-pointer"
                        :class="['text-[#FFF] bg-[#1c1f2e]', { 'text-[#FFF] bg-[#2d60ff]': item === selectNum }]"
                        @click="selectNum = item">
                        {{ item }}
                    </div>
                </div>
            </VideoGenerateFormProperty>
        </div>

        <!-- 提交按钮 -->
        <div class=" relative cursor-pointer group w-full h-12 rounded-xl bg-gradient-to-r from-[#3b82f6] to-[#2563eb] hover:from-[#2563eb] hover:to-[#1d4ed8] text-white font-bold text-base shadow-[0_0_20px_rgba(59,130,246,0.5)] hover:shadow-[0_0_25px_rgba(59,130,246,0.7)] active:scale-[0.98] transition-all duration-200 flex items-center justify-center disabled:opacity-70 disabled:cursor-not-allowed disabled:active:scale-100"
            @click="commit">
            <span v-if="commitLoading == false" class="flex items-center gap-2 group-disabled:hidden">
                <svg class="w-5 h-5 fill-current" viewBox="0 0 24 24">
                    <path d="M7.5 5.6L10 0l2.5 5.6L18 8l-5.5 2.4L10 16 7.5 10.4 2 8l5.5-2.4z" />
                    <path d="M15.5 14.6L17 11l1.5 3.6L22 16l-3.5 1.4L17 21l-1.5-3.6L12 16l3.5-1.4z" />
                </svg>
                立即生成
            </span>
            <span v-if="commitLoading" class="flex items-center gap-2 group-disabled:flex">
                <svg class="w-5 h-5 text-white animate-spin" xmlns="http://www.w3.org/2000/svg" fill="none"
                    viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor"
                        d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z">
                    </path>
                </svg>
                正在提交...
            </span>
            <div class="btn-tag z-[100]">限时免费</div>
        </div>

    </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import SegmentControl from './SegmentControl.vue'
import { ElMessage } from 'element-plus'
import { uploadOss } from '../../../../../utils/common'
import { commitSoraTask } from '@/api/home/common'
import VideoGenerateFormProperty from './VideoGenerateFormProperty.vue'
import DropdownSetting from '@/components/DropdownSetting.vue'

const emit = defineEmits(['commitSuccess', 'showCharacterList'])

const commitLoading = ref(false)

const segments = ["图生视频", "文生视频"]
const models = [
    {
        label: 'Sora',
        value: 'so2'
    },
    // {
    //     label: 'Sora Pro',
    //     value: 'so2p'
    // }
    // ,
    // {
    //     label: 'veo3-pro',
    //     value: 'veo3p'
    // },
    // {
    //     label: 'veo3-pro-frames',
    //     value: 'veo3pfs'
    // },
    // {
    //     label: 'veo3-fast-frames',
    //     value: 'veo3ffs'
    // },
    {
        label: 'veo3-frames',
        value: 'veo3fs'
    }
]
const durations = ['10s', '15s']
const scales = ['横屏', '竖屏']

const selectSegment = ref('图生视频')
const selectModel = ref('Sora')
const selectScale = ref('横屏')
const selectDuration = ref('10s')
const selectNum = ref('1')
const imageUrl = ref('')
const prompt = ref('')

// 定义一个方法，用于从父组件接收新数据并更新内部状态
const setFormData = (newData) => {
    selectModel.value = newData.model
    selectScale.value = newData.argsJSON.orientation == 'landscape' ? '横屏' : '竖屏'
    selectDuration.value = newData.argsJSON.duration + 's' || '10s'
    selectNum.value = '' + newData.argsJSON.number || '1'
    prompt.value = newData.prompt || ''
    if (newData.referImages?.length > 0) {
        imageUrl.value = JSON.parse(newData.referImages)?.[0] || ''
        selectSegment.value = '图生视频'
    } else {
        imageUrl.value = ''
        selectSegment.value = '文生视频'
    }

    // prompt.value = newData.prompt
    // images.value = newData.refer_images
    // // 模型
    // if (newData.modelType) {
    //     selectedModel.value = {
    //         id: newData.modelType,
    //         title: newData.modelType == 'b' ? '模型一' : '模型二',
    //         desc: newData.modelType == 'b' ? '更优惠的积分选择' : '更专业的模型选择'
    //     }
    // }
    // // 分辨率
    // if (newData.aspectRatio) {
    //     selectAspectRatio.value = newData.aspectRatio
    // }
    // // 分辨率
    // if (newData.res) {
    //     selectAspectRes.value = newData.res
    // }
}

defineExpose({
    setFormData
})

/**
 * 上传前的验证
 * @param {File} file - 上传的文件
 * @returns {boolean} 是否允许上传
 */
const beforeUpload = (file) => {
    // 如果是图片类型，检查文件大小
    if (file.type.startsWith('image/')) {
        const fileSizeMB = file.size / (1024 * 1024)
        if (fileSizeMB > 10) {
            ElMessage.error('图片尺寸不得大于10M！')
            return false
        }
    }
    return true
}
const upload = (item) => {
    uploadOss(item, (value) => {
        imageUrl.value = value.url
        // emit('select-from-local-file-result', value.url)
    })
}

const commit = async () => {
    try {
        // 防止重复提交
        if (commitLoading.value) {
            return
        }
        commitLoading.value = true
        setTimeout(() => {
            commitLoading.value = false
        }, 3000);

        if (!prompt.value) {
            ElMessage.error('请输入描述词')
            return
        }



        let model = models.find(item => item.label == selectModel.value)?.value || 'so2'
        let referImages = imageUrl.value ? JSON.stringify([imageUrl.value]) : null
        let args = {
            prompt: prompt.value,
            orientation: selectScale.value == '横屏' ? 'landscape' : 'portrait',
            size: 'small', // 'large'
            duration: Number(selectDuration.value.replace('s', '')),
            number: Number(selectNum.value),
        }
        let params = {
            model: model,
            prompt: prompt.value,
            referImages: referImages
        }
        if (referImages) {
            args.referImages = referImages
            params.referImages = referImages
        }
        params.args = JSON.stringify(args)

        const res = await commitSoraTask(params)
        if (res.code === 10000) {
            ElMessage.success('提交成功')
            emit('commitSuccess')
        } else {
            ElMessage.error(res.msg || '提交失败')
        }
    } catch (error) {
        console.error(error)
        ElMessage.error('提交失败')
    }
}

/**
    * 切换下拉菜单的显示/隐藏
    * @param {Event} event 点击事件
    * @param {string} menuId 菜单的ID
    */
function toggleDropdown(event, menuId) {
    alert(event.currentTarget)
    event.stopPropagation(); // 阻止冒泡，防止触发 document 的关闭事件

    const menu = document.getElementById(menuId);
    const trigger = event.currentTarget;
    const isCurrentlyOpen = menu.classList.contains('show');

    // 1. 关闭所有已打开的菜单 (互斥效果)
    closeAllDropdowns();

    // 2. 如果刚才点的那个没开，就把它打开
    if (!isCurrentlyOpen) {
        menu.classList.add('show');
        trigger.classList.add('active'); // 让箭头旋转
    }
}

/**
 * 选中选项后的逻辑
 * @param {string} type 类型标识 (model/ratio/duration)
 * @param {string} value 选中的值
 * @param {HTMLElement} element 点击的选项元素
 */
function selectOption(type, value, element) {
    event.stopPropagation(); // 阻止冒泡

    // 更新显示的数值
    document.getElementById(`val-${type}`).textContent = value;

    // 更新选中样式 (Visual Feedback)
    const parentMenu = element.parentElement;
    const options = parentMenu.querySelectorAll('.dropdown-option');
    options.forEach(opt => opt.classList.remove('selected'));
    element.classList.add('selected');

    // 关闭菜单
    closeAllDropdowns();

    console.log(`Changed ${type} to: ${value}`);
}

/**
 * 关闭所有下拉菜单
 */
function closeAllDropdowns() {
    // 隐藏所有菜单
    document.querySelectorAll('.dropdown-menu').forEach(menu => {
        menu.classList.remove('show');
    });
    // 复位所有箭头
    document.querySelectorAll('.dropdown-trigger').forEach(trigger => {
        trigger.classList.remove('active');
    });
}

onMounted(() => {
    // 全局点击监听：点击空白处关闭菜单
    document.addEventListener('click', function (event) {
        closeAllDropdowns();
    });
})

</script>
<style scoped>
:global(:root) {
    --bg-dark: #0b0c15;
    --panel-bg: #131620;
    --input-bg: #1c1f2e;
    --border-color: #2a2e45;
    --primary-blue: #2d60ff;
    --primary-gradient: linear-gradient(135deg, #2d60ff 0%, #00c6ff 100%);
    --text-main: #ffffff;
    --text-sub: #8b92a7;
    --accent-glow: 0 0 15px rgba(45, 96, 255, 0.4);
}

.upload-image-remove-btn {
    position: absolute;
    top: 10px;
    right: 10px;
    width: 30px;
    height: 30px;
    background-color: #495F81;
    border-radius: 10px;
}

/* 新增：角色卡样式 */
.section-title {
    font-size: 14px;
    color: #8b92a7;
    margin-bottom: 10px;
    display: flex;
    justify-content: space-between;
}

.character-cards {
    display: flex;
    gap: 10px;
    overflow-x: auto;
    padding-bottom: 5px;
}

.char-card {
    min-width: 70px;
    height: 90px;
    background-color: #1c1f2e;
    border-radius: 8px;
    position: relative;
    cursor: pointer;
    border: 1px solid transparent;
    overflow: hidden;
    transition: all 0.2s;
    color: white;
}

.char-card img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    opacity: 0.6;
}

.char-card span {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    padding: 4px;
    font-size: 10px;
    background: rgba(0, 0, 0, 0.7);
    text-align: center;
}

.char-card.selected {
    border: 2px solid #2d60ff;
    box-shadow: 0 0 0 2px rgba(45, 96, 255, 0.5);
}

.char-card.selected img {
    opacity: 1;
}

.submit-btn {
    width: 100%;
    padding: 0 16px;
    background: var(--primary-gradient);
    border: none;
    padding: 14px;
    border-radius: 8px;
    color: #fff;
    font-weight: bold;
    font-size: 16px;
    cursor: pointer;
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
    margin-top: auto;
    box-shadow: var(--accent-glow);
}

.btn-tag {
    position: absolute;
    top: -10px;
    right: -5px;
    background: #00c6ff;
    color: #000;
    font-size: 10px;
    padding: 2px 6px;
    border-radius: 4px;
    font-weight: bold;
}

/* --- 右侧下拉触发区域 --- */
.dropdown-trigger {
    display: flex;
    align-items: center;
    cursor: pointer;
    height: 100%;
    position: relative;
    /* 关键：用于下拉菜单的绝对定位基准 */
    color: var(--text-value);
    font-size: 15px;
    user-select: none;
    /* 防止双击选中文字 */
}

.current-value {
    margin-right: 8px;
}

/* 箭头图标 */
.arrow-icon {
    width: 12px;
    height: 12px;
    fill: none;
    stroke: currentColor;
    stroke-width: 2;
    stroke-linecap: round;
    stroke-linejoin: round;
    transition: transform 0.3s ease;
}

/* 激活状态下箭头旋转 */
.dropdown-trigger.active .arrow-icon {
    transform: rotate(180deg);
}

/* --- 下拉菜单样式 --- */
.dropdown-menu {
    position: absolute;
    top: calc(100% + 4px);
    /* 在触发器下方一点点 */
    right: 0;
    width: 180px;
    /* 下拉框宽度 */
    background-color: #1e1e26;
    border: 1px solid #2a2a35;
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
    padding: 6px;
    z-index: 100;

    /* 隐藏状态动画设置 */
    opacity: 0;
    visibility: hidden;
    transform: translateY(-10px);
    transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
}

/* 显示状态 */
.dropdown-menu.show {
    opacity: 1;
    visibility: visible;
    transform: translateY(0);
}

/* 下拉选项 */
.dropdown-option {
    padding: 10px 12px;
    color: #d1d1d6;
    font-size: 14px;
    border-radius: 6px;
    cursor: pointer;
    transition: background 0.2s;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.dropdown-option:hover {
    background-color: var(--bg-hover);
    color: white;
}

.dropdown-option.selected {
    color: #6e6eff;
    /* 选中项高亮色 */
    background-color: rgba(110, 110, 255, 0.1);
}
</style>
