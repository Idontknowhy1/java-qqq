<template>
    <div class="main-container">
        <HeaderNav />
        <div class="demand_box">
            <div class="name">
                <div class="title">需求提交</div>
                <div class="remark">请填写您的需求信息和参考资料</div>
            </div>
            <div class="content">
                <div class="tab">
                    <div class="tab_item" :class="{ checked: form.type == 'characters' }"
                        @click="setChecked('characters')">
                        <div class="item_icon"><img src="@/assets/images/themes/demand_character.png" /></div>
                        <div class="item_name">人物</div>
                    </div>
                    <div class="tab_item" :class="{ checked: form.type == 'scenes' }" @click="setChecked('scenes')">
                        <div class="item_icon"><img src="@/assets/images/themes/demand_scene.png" /></div>
                        <div class="item_name">场景</div>
                    </div>
                    <div class="tab_item" :class="{ checked: form.type == 'things' }" @click="setChecked('things')">
                        <div class="item_icon"><img src="@/assets/images/themes/demand_prop.png" /></div>
                        <div class="item_name">道具</div>
                    </div>
                </div>
                <div class="form_box">
                    <div class="form_item">
                        <div class="label">{{ name_type_list[form.type] }}</div>
                        <div class="input">
                            <el-input v-model="form.name" :placeholder="placeholder_list[form.type]"></el-input>
                        </div>
                    </div>
                    <div class="form_item" v-if="form.type == 'characters'">
                        <div class="label">人物描述</div>
                        <div class="input">
                            <el-input v-model="form.desc" type="textarea" placeholder="请详细描述人物的特征、性格等信息"
                                rows="3"></el-input>
                        </div>
                    </div>
                    <div class="form_item">
                        <div class="label">其他补充信息</div>
                        <div class="input">
                            <el-input v-model="form.extra_info" type="textarea" placeholder="请填写其他需要补充的信息"
                                rows="3"></el-input>
                        </div>
                    </div>
                    <div class="form_item">
                        <div class="label">{{ extra_info_list[form.type] }}</div>
                        <div class="input">
                            <!-- <ImagesUpload fileType="drag" :images="form.images"></ImagesUpload> -->
                        </div>
                        <div class="image_list">
                            <div class="item_image" v-for="(item, key) in form.images" :key="key">
                                <i class="el-icon-circle-close" @click="deleteInfo(key)"></i>
                                <el-image style="width: 100px; height: 100px" :src="item.url">
                                </el-image>
                            </div>

                        </div>
                    </div>
                </div>

                <div class="btn">
                    <div class="submit_btn btn_checked" @click="submitFn">提交信息</div>
                    <div class="submit_btn" @click="resetFn">重置</div>
                </div>

                <div class="work_time">
                    <div class="work_time_name">
                        <div class="title">需求处理说明</div>
                        <div class="time">工作时间：周一至周五 9:00-18:00</div>
                    </div>
                    <div class="work_time_content">
                        <p>1. 每周固定更新资产：动态漫人物3 组，Q 版人物5 组，场景5 组，道具5 组</p>
                        <p>2. 如需求描述不清晰，我们可能会联系您进行进一步沟通</p>
                        <p>3. 如当周需求单，将优先完成需求内容，若无新需求，则按“通用范围”清单进行常规更新，不做额外顺延。</p>
                    </div>
                    <!--          <div class="contact_desc">如有疑问请联系管理员</div>
          <div class="time">工作时间：周一至周五 9:00-18:00</div>-->
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
// import ImagesUpload from "@/components/common/ImagesUpload";
import { characterRequirements } from "@/api/home/common";
import { useUserStore } from "../../store/useUserStore";
import HeaderNav from './components/HeaderNav.vue';
import { ElMessage } from 'element-plus';

const userStore = useUserStore()

// 数据属性转为 ref/reactive
const form = reactive({
    type: 'characters',
    name: '',
    desc: '',
    extra_info: '',
    images: [],
})

const name_text = ref('')
const desc_text = ref('')

// 静态数据可保留为常量
const name_type_list = {
    characters: '人物名字（来源）',
    scenes: '场景（来源）',
    things: '道具（来源）',
}

const extra_info_list = {
    characters: '参考图片（至少提供一张人物正面视角图，参考图越多定制越详细）',
    scenes: '参考图片（最多8张，参考图越多定制越详细）',
    things: '参考图片（最多8张，参考图越多定制越详细）',
}

const placeholder_list = {
    characters: '请输入人物名字',
    scenes: '例：东方明珠',
    things: '例：胜利女神像',
}

// 方法直接定义为函数
const deleteInfo = (key) => {
    form.images.splice(key, 1)
}

const setChecked = (code) => {
    form.type = code
}

const resetFn = () => {
    Object.keys(form).forEach(key => {
        if (key === 'type') return
        form[key] = Array.isArray(form[key]) ? [] : ''
    })
}

const submitFn = async () => {
    if (userStore.loginStatus) {
        try {
            const res = await characterRequirements(form)
            if (res.code === 200) {
                ElMessage.success('提交成功')
                resetFn()
            }
        } catch (error) {
            console.error('提交失败:', error)
        }
    } else {
        userStore.setDialogLoginStatus(true)
    }
}

</script>

<style scoped lang="scss">


// :root {
//     --input-border-default: #E1E1E1;
//     --input-border-hover: #C0C4CC;
//     --input-border-focus: #409EFF;
//     --input-border-disabled: #E4E7ED;
// }

// /* 应用变量 */
// :deep(.el-input__wrapper) {
//     box-shadow: 0 0 0 1px var(--input-border-default) inset;
// }

// :deep(.el-input__wrapper:hover) {
//     box-shadow: 0 0 0 1px var(--input-border-hover) inset;
// }

// :deep(.el-input__wrapper.is-focus) {
//     box-shadow: 0 0 0 1px var(--input-border-focus) inset;
// }

// :deep(.el-input.is-disabled .el-input__wrapper) {
//     box-shadow: 0 0 0 1px var(--input-border-disabled) inset;
// }

:deep(.el-input__wrapper) {
    background: none;
    border: 1px solid #2A2B3E;
}

// :deep(.el-input__inner) {
//     background: none;
//     border: none;
// }

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

::v-deep .el-upload-dragger {
    width: 768px;
    height: 200px;
    background: none;
    border-color: #2A2B3E;
}

::v-deep .el-icon-circle-close {
    position: absolute;
    top: -10px;
    right: -10px;
    color: #ff0000;
    z-index: 1000;
    font-size: 20px;
    cursor: pointer;
}

.upload_btn {
    width: 100px;
    line-height: 36px;
    background: #1A1B2E;
    margin: 10px auto;
    color: #D1D5DB;
    border-radius: 6px;
    text-align: center;
    font-size: 14px;
}

.demand_box {
    border: 0px solid #ff0000;
    width: 100%;
    height: 100%;
    overflow-y: hidden;

    .name {
        width: 94%;
        background: #111828;
        color: #fff;
        border: 1px solid #111828;
        margin: 0 auto;

        .title {
            font-size: 22px;
            font-weight: bold;
            margin: 30px 0 0 30px;
        }

        .remark {
            font-size: 14px;
            color: #9CA3AF;
            margin: 20px 0 0px 30px;
            padding: 0 0 40px 0;
        }
    }

    .content {
        width: 94%;
        background: #111226;
        border-radius: 20px;
        margin: 0 auto 50px;
        padding: 0 0 10px;
        border: 0px solid #fff;

        .tab {
            width: 768px;
            height: 50px;
            line-height: 50px;
            border: 0px solid #ff0000;
            margin: 0px auto;
            padding: 40px 0 0 0;
            display: flex;
            justify-content: space-between;

            .tab_item {
                width: 32%;
                display: flex;
                justify-content: center;
                align-items: center;
                color: #fff;
                border: 1px solid #2A2B3E;
                font-size: 16px;
                border-radius: 4px;
                cursor: pointer;

                .item_icon {
                    width: 16px;
                    height: 16px;
                    position: relative;
                    border: 0px solid #ff0000;
                    margin: 0 4px 0 0;

                    img {
                        vertical-align: top;
                        position: relative;
                        width: 16px;
                        height: 16px;
                        margin: 0;
                        padding: 0;
                    }
                }
            }

            .checked {
                background: #0066FF;
                border: 1px solid #0066FF;
            }
        }

        .form_box {
            width: 768px;
            margin: 0px auto 0;
            padding: 20px 0 0 0;
            border: 0px solid #fff;

            .form_item {
                margin: 30px 0 0 0;

                .label {
                    color: #D1D5DB;
                    margin: 10px 0 10px 0;
                    font-size: 14px;
                }

                .input {
                    display: flex;
                }

                .image_list {
                    display: flex;
                    position: relative;

                    .item_image {
                        margin: 10px 10px 0 0;
                        position: relative;

                    }
                }
            }

        }

        .btn {
            width: 768px;
            border: 0px solid #ff0000;
            margin: 50px auto 0;
            display: flex;

            .submit_btn {
                cursor: pointer;
                width: 128.02px;
                line-height: 48px;
                border-radius: 4px;
                text-align: center;
                z-index: 0;
                margin: 0 20px 0 0;
                border: 1px solid #4B5563;
                color: #9CA3AF;
            }

            .btn_checked {
                border: none;
                color: #fff;
                background: linear-gradient(0deg, rgba(0, 0, 0, 0), rgba(0, 0, 0, 0)), linear-gradient(90deg, #2563EB 0%, #DB2777 100%);
            }
        }

        .work_time {
            width: 60%;
            border-top: 1px solid #2A2B3E;
            line-height: 34px;
            margin: 50px auto 50px;
            color: #9CA3AF;
            padding: 20px 0 0 0;
            font-size: 12px;

            .work_time_name {
                width: 100%;
                display: flex;
                justify-content: space-between;
                margin: 20px 0 20px 0;

                .title {
                    font-size: 18px;
                    color: #fff;
                }

                .time {
                    font-size: 12px;
                }
            }

            .work_time_content {}

            /*.contact_desc {
        text-align: center;
      }
      .time {
        text-align: center;
      }*/
        }
    }


}
</style>