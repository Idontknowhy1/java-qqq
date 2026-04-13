<template>
    <el-dialog v-model="dialogVisible" width="800" :show-close="false" style="padding: 0;" :close-on-click-modal="false"
        :close-on-press-escape="false" append-to-body>
        <div class="privacy-content-wrapper">
            <div class="header-bar">
                <div class="title-wrapper">上传</div>
                <!-- <img class="close-btn" src="@/assets/images/close-btn.png" @click="emit('cancel')"></img> -->
            </div>
            <div class="content-wrapper">
                <div class="left-wrapper">
                    <img class="preview-image" :src="imageUrl" />
                </div>
                <div class="right-wrapper">
                    <div class="title-wrapper">任务名称</div>
                    <div class="input-wrapper">
                        <el-input v-model="formData.name" placeholder="请输入这个任务的名称，例如：不小晚"></el-input>
                    </div>
                    <div class="title-wrapper">要求</div>
                    <div class="input-wrapper">
                        <el-input v-model="formData.prompt" type="textarea" placeholder="我想把这张图的动作再夸张一些但是我不知道该怎末办"
                            rows="5"></el-input>
                    </div>
                    <div class="button-wrapper">
                        <div class="btn cancel-btn" @click="emit('dismiss')">取消</div>
                        <div class="btn relative flex items-center justify-center h-[32px] bg-[#364458] text-[#FFFFFF] text-[14px] rounded-[16px] px-[16px] gap-[10px] ml-auto cursor-pointer"
                            @click="confirm">
                            <!-- 积分图标 -->
                            <img :src="scoreImage" alt="" class="w-[16px] h-[16px]">
                            <!-- 积分余额 -->
                            {{ uploadSplitPrice }}
                        </div>
                    </div>

                </div>
            </div>
            <div class="bottom-wrapper">注：1.拆分人物请上传白色背景，2.请详细描述需要拆分的部位，3.上传拆分需要消耗{{ uploadSplitPrice }}积分</div>
        </div>
    </el-dialog>
</template>

<script setup lang="ts">
import { getScoreConfig } from '@/api/home/common.js'
import { apiImageUploadSubmit } from '@/api/home/materialSquare.js'
import { ElMessage } from 'element-plus';
import { onMounted, ref } from 'vue';
import { useUserStore } from '../../../../store/useUserStore';
import scoreImage from '@/assets/images/nav-score-image.png'

const emit = defineEmits(['dismiss'])
const uploadSplitPrice = ref(100)

const props = defineProps(['imageUrl'])
const userStore = useUserStore()

const dialogVisible = ref(true)
const formData = ref({
    name: '',
    prompt: ''
})

const confirm = async () => {
    if (formData.value.name == '') {
        ElMessage.info('请输入名称')
        return
    }
    if (formData.value.prompt == '') {
        ElMessage.info('请输入要求')
        return
    }

    try {
        let res = await apiImageUploadSubmit({
            detail: formData.value.prompt,
            title: formData.value.name,
            imageUrl: props.imageUrl
        })
        if (res.code == 10000) {
            ElMessage.success('上传成功')
            // 刷新余额
            userStore.getScoreBalance()

            emit('dismiss')


        } else {
            ElMessage.error('上传失败，' + res.msg)
            console.error('上传失败', res.msg)
        }
    } catch (err) {
        ElMessage.error('上传失败')
        console.error('上传失败', err)
    }
}

const queryScoreConfig = async () => {
    try {
        let res = await getScoreConfig()
        if (res.code === 10000) {
            uploadSplitPrice.value = res.data.imageSplitPrice
        }
    } catch (err) {

    }
}

onMounted(() => {
    queryScoreConfig()
})

</script>

<style lang="scss" scoped>
.privacy-content-wrapper {
    display: flex;
    flex-direction: column;
    background-color: #22173D;
    width: 100%;
    height: 470px;
    border-radius: 20px;
    z-index: 1000;
    padding: 0 40px;
    box-sizing: border-box;

    .header-bar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        height: 68px;
        border-bottom: 1px solid #495F81;

        .title-wrapper {
            font-size: 22px;
            font-weight: 600;
            color: white;
        }

        .close-btn {
            width: 32px;
            height: 32px;
        }
    }

    .content-wrapper {
        flex-grow: 1;
        display: flex;
        margin-top: 20px;
        justify-content: space-between;

        .left-wrapper {
            display: flex;
            justify-content: center;
            align-items: center;
            width: 48%;
            box-sizing: border-box;

            .preview-image {
                max-width: 100%;
                max-height: 300px;
                border-radius: 14px;
            }
        }

        .right-wrapper {
            width: 48%;

            .title-wrapper {
                display: flex;
                align-items: center;
                font-size: 16px;
                color: white;
                height: 40px;
            }

            .button-wrapper {
                margin-top: 20px;
                display: flex;
                justify-content: space-between;

                .btn {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    width: 158px;
                    height: 40px;
                    border-radius: 8px;
                    font-size: 18px;
                    cursor: pointer;
                }

                .cancel-btn {
                    background-color: #2E394F;
                    color: rgba($color: #FFFFFF, $alpha: 0.6);
                }
            }
        }
    }

    .bottom-wrapper {
        display: flex;
        justify-content: center;
        align-items: center;
        font-size: 14px;
        color: #00BEFF;
        height: 60px;
    }
}

:deep(.el-input__wrapper) {
    background: none;
    border: 1px solid #2A2B3E;
}

::v-deep .el-input__inner {
    height: 46px;
    background: none;
}

::v-deep .el-textarea__inner {
    background: none;
    border: 1px solid #2A2B3E;
}

::v-deep .el-input__inner::placeholder {
    color: #9CA3AF !important;
}

::v-deep .el-textarea__inner::placeholder {
    color: #9CA3AF !important;
}
</style>