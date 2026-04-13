<template>
    <div class="ai-gen-pop-form">
        <div class="image-upload-wrapper">
            <div class="image-preview-list-wrapper" ref="sortableList">
                <div class="image-preview-wrapper" v-for="image in images" :key="image">
                    <el-image class="upload-image-preview" fit="cover" :src="image" />
                    <el-icon class="upload-image-remove-btn" :size="20" color="#FF0000"
                        @click="() => removeImage(image)">
                        <Delete />
                    </el-icon>
                </div>
            </div>

            <!-- 添加图片按钮 -->
            <el-popover v-if="hasAgreedPrivacy && images.length < 5" placement="right-end" :show-arrow="false"
                effect="dark" trigger="click" popper-style="background-color: transparent; padding: 0; border: none;">
                <template #reference>
                    <img src="@/assets/images/add_new_image.png" style="width: 110px; height: 90px;" fit="cover" />
                </template>
                <template #default>
                    <AiSelectImgeTypePop @select-from-local-file-result="toggleImageWithUrl"
                        @select-from-role="selectFromRole" />
                </template>
            </el-popover>

            <!-- 点击打开隐私弹窗 -->
            <img v-if="hasAgreedPrivacy == false" src="@/assets/images/add_new_image.png"
                style="width: 110px; height: 90px;" @click="uploadImageClick" />
        </div>

        <div class="bottom-wrapper">

            <div class="prompt-wrapper">
                <textarea class="prompt-input" v-model="prompt"
                    placeholder="请输入图片生成提示词，例如：做一张“韩漫风美女人物形象”片生成提示词片生成提示词片生成提示词片生成提示词片生成提示词" />
            </div>

            <div class="options-bar">
                <!-- 切换模型按钮 -->
                <el-popover placement="top-start" trigger="click" :show-arrow="false" effect="dark"
                    popper-style="background-color: transparent; padding: 0; border: none;">
                    <template #reference>
                        <div class="model-btn">{{ selectedModel.title }} <img style="width: 24px;"
                                src="@/assets/images/ai-model-change-btn-icon.png" /></div>
                    </template>
                    <template #default>
                        <AiModelChangePop :selectedModel="selectedModel" @updateModel="changeModel" />
                    </template>
                </el-popover>

                <!-- 切换比例按钮 -->
                <el-popover placement="top-start" trigger="click" :show-arrow="false" effect="dark"
                    popper-style="background-color: transparent; padding: 0; border: none;">
                    <template #reference>
                        <div class="model-btn">{{ selectedModel.id == 'b2' ? selectAspectRatio + ' | ' + selectAspectRes
                            :
                            selectAspectRatio }}
                            <img style="width: 24px;" src="@/assets/images/ai-model-change-btn-icon.png" />
                        </div>
                    </template>
                    <template #default>
                        <AiAspectRatioChangePop :selectedAspect="selectAspectRatio" :selectedRes="selectAspectRes"
                            :showRes="selectedModel.id == 'b2'" @updateAspect="(r) => selectAspectRatio = r"
                            @updateRes="(r) => selectAspectRes = r" />
                    </template>
                </el-popover>

                <div class="relative flex items-center justify-center h-[32px] bg-[#364458] text-[#FFFFFF] text-[14px] rounded-[16px] px-[16px] gap-[10px] ml-auto cursor-pointer"
                    @click="commit">
                    <!-- 积分图标 -->
                    <img :src="scoreImage" alt="" class="w-[16px] h-[16px]">
                    <!-- 积分余额 -->
                    <div v-if="aiGenPrice > 0">{{ aiGenPrice }}</div>
                </div>
            </div>

        </div>
        <!-- 角色库弹窗 -->
        <AiRoleLibPop v-if="showRoleLibPop" @dismiss="showRoleLibPop = false" @toggle-image="toggleImageWithUrl"
            :selected-images="images" />

        <!-- 隐私协议弹窗 -->
        <AiPrivacyPop v-if="showPrivacyPop" @refuse="showPrivacyPop = false" @agree="agreePrivacy" />
    </div>
</template>

<script lang="ts" setup>
import { commitAiTask, getScoreConfig } from '@/api/home/common.js'
import { ElMessage } from 'element-plus';
import { onMounted, onUnmounted, ref } from 'vue';
import AiModelChangePop from './AiModelChangePop.vue';
import AiSelectImgeTypePop from './AiSelectImgeTypePop.vue';
import AiRoleLibPop from './AiRoleLibPop.vue';
import AiPrivacyPop from './AiPrivacyPop.vue';
import { useUserStore } from '../../../store/useUserStore';
import Sortable from 'sortablejs'
import AiAspectRatioChangePop from './AiAspectRatioChangePop.vue';
import scoreImage from '@/assets/images/nav-score-image.png'

const sortableList = ref(null)
const aiGenPrice = ref(0)

const userStore = useUserStore()

const selectedModel = ref({
    id: 'b',
    title: '模型一',
    desc: '更优惠的积分选择'
})
const selectAspectRatio = ref('智能')
const selectAspectRes = ref('2k')
const changeModel = (m) => {
    selectedModel.value = m
}

const images = ref([])
const prompt = ref('')

const showRoleLibPop = ref(false)

const hasAgreedPrivacy = ref(false)

// 1. 声明组件可以触发的事件
const emit = defineEmits(['save-successs']);

const vipChecked = () => {
    if (userStore.loginStatus == 0) {
        userStore.setDialogLoginStatus(true)
        return false
    }
    return true
}

// 点击了上传图片按钮
const uploadImageClick = () => {
    if (!vipChecked()) {
        return
    }
    showPrivacyPop.value = true
}

const showPrivacyPop = ref(false)

// 同意协议
const agreePrivacy = () => {
    localStorage.setItem('agreed-privacy', '1')
    hasAgreedPrivacy.value = true
    showPrivacyPop.value = false
}

const commit = async () => {
    if (!vipChecked()) {
        return
    }

    if (prompt.value.length == 0) {
        return
    }
    // if (images.value.length == 0) {
    //     ElMessage.error('请上传垫图')
    //     return
    // }

    try {
        let res = await commitAiTask({
            prompt: prompt.value,
            referImages: images.value,
            modelType: selectedModel.value.id,
            aspectRatio: selectAspectRatio.value == '智能' ? null : selectAspectRatio.value,
            resolution: selectAspectRes.value,
        })
        console.log(' 提交任务结果 ', res)
        if (res.code == 10000) {
            images.value = []
            prompt.value = ''
            ElMessage.success('提交成功')

            // 刷新余额
            userStore.getScoreBalance()

            emit('save-successs')
        }
    } catch (err) {
        console.error(' 任务提交失败 ', err)
    }
}

const removeImage = (image) => {
    images.value = images.value.filter(m => m != image)
}

const toggleImageWithUrl = (url) => {
    if (images.value.includes(url)) {
        images.value = images.value.filter(i => i != url)
    } else {
        if (images.value.length >= 5) {
            return
        }
        images.value.push(url)
    }
}
const selectFromRole = () => {
    showRoleLibPop.value = true
}

// 定义一个方法，用于从父组件接收新数据并更新内部状态
const setFormData = (newData) => {
    prompt.value = newData.prompt
    images.value = newData.refer_images
    // 模型
    if (newData.modelType) {
        selectedModel.value = {
            id: newData.modelType,
            title: newData.modelType == 'b' ? '模型一' : '模型二',
            desc: newData.modelType == 'b' ? '更优惠的积分选择' : '更专业的模型选择'
        }
    }
    // 分辨率
    if (newData.aspectRatio) {
        selectAspectRatio.value = newData.aspectRatio
    }
    // 分辨率
    if (newData.res) {
        selectAspectRes.value = newData.res
    }
}

const queryScoreConfig = async () => {
    try {
        let res = await getScoreConfig()
        if (res.code === 10000) {
            aiGenPrice.value = res.data.aiGenPrice
        }
    } catch (err) {

    }
}

// 将该方法暴露给父组件
defineExpose({
    setFormData
})

onMounted(() => {
    if (localStorage.getItem('agreed-privacy') == '1') {
        hasAgreedPrivacy.value = true
    }

    if (sortableList.value) {
        Sortable.create(sortableList.value, {
            animation: 150, // 动画延迟
            onEnd: (evt) => {
                // 手动同步数据：根据旧索引和新索引重新排序数组
                const { oldIndex, newIndex } = evt
                const draggedItem = images.value[oldIndex]
                images.value.splice(oldIndex, 1)
                images.value.splice(newIndex, 0, draggedItem)
            }
        })
    }

    queryScoreConfig()

    let lastPrompt = localStorage.getItem('lastPrompt')
    if (lastPrompt) {
        let lastPromptObj = JSON.parse(lastPrompt)
        selectedModel.value = lastPromptObj.model
        selectAspectRatio.value = lastPromptObj.aspectRatio
        selectAspectRes.value = lastPromptObj.res
    }
})

onUnmounted(() => {
    localStorage.setItem('lastPrompt', JSON.stringify({
        model: selectedModel.value,
        aspectRatio: selectAspectRatio.value,
        res: selectAspectRes.value,
    }))
})

</script>

<style lang="scss" scoped>
.ai-gen-pop-form {
    display: flex;
    flex-direction: column;
    width: 766px;
    height: 270px;
    min-height: 270px;
    box-sizing: border-box;
    padding: 24px 18px;
    border: 1px solid #495F81;
    border-radius: 16px;
    background: linear-gradient(#111827, #111827, #425068);

    .image-upload-wrapper {
        position: relative;
        display: flex;
        justify-content: flex-start;
        align-items: center;
        width: 100%;
        height: 100px;
        font-size: 16px;
        color: #8498B8;
        margin-bottom: 10px;

        .image-preview-list-wrapper {
            display: flex;
        }

        .image-preview-wrapper {
            position: relative;
            width: 110px;
            height: 90px;
            object-fit: cover;
            border-radius: 12px;
            margin-right: 16px;
        }

        .upload-image-preview {
            width: 100%;
            height: 100%;
            border-radius: 18px;
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
    }

    .bottom-wrapper {
        display: flex;
        flex-direction: column;
        flex-grow: 1;
        padding-top: 20px;
        gap: 16px;
        border-top: 1px solid #495F81;

        .options-bar {
            display: flex;
            justify-content: center;
            // background-color: red;
            align-items: flex-end;
            width: 100%;
            gap: 16px;

            .model-btn {
                width: 112px;
                height: 40px;
                background-color: #364458;
                display: flex;
                justify-content: center;
                align-items: center;
                border-radius: 10px;
                cursor: pointer;
                font-size: 14px;
                color: white;
            }
        }

        .prompt-wrapper {
            width: 100%;
            height: 50px;
            flex-grow: 1;
            // background-color: red;

            .prompt-input {
                background-color: transparent;
                border: none;
                resize: none;
                width: 100%;
                height: 100%;
                font-size: 16px;
                color: #8498B8;
                outline: none;
                line-height: 16px;
            }

            .prompt-input::highlight {
                border: none;
            }

            .prompt-input::placeholder {
                color: #999;
                /* 改变文字颜色 */
                font-size: 14px;
                line-height: 16px;
                /* 改变字体大小 */
                font-style: italic;
                /* 设置字体为斜体 */
                font-family: Arial, sans-serif;
                /* 更改字体 */
                /* 其他支持的样式：font-weight, letter-spacing, text-transform 等 */
            }

        }

    }
}
</style>