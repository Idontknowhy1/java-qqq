<template>
    <div class="w-full h-full bg-[#0b0c15] border-l-[1px] border-l-[#262D38]" @click="">

        <!-- Header信息 -->
        <div class="flex items-center w-full h-[55px] border-b-[1px] border-b-[#262D38] px-[20px] gap-[20px]">
            <h2 class="text-[white]">角色卡</h2>

            <!-- "我的角色", "社区角色" -->
            <SegmentControl customClass="h-[44px] w-[200px] text-[14px] bg-[transparent]"
                textClass="text-[#FFFFFF] text-[12px]" :segments="segments" :selectSegment="selectSegment"
                @update:selectSegment="selectSegment = $event" />

            <button class="create-btn" @click="showModal = true">
                <i class="fa-solid fa-plus"></i> 新建角色
            </button>
        </div>

        <!-- 列表信息 -->
        <div class="w-full h-[calc(100vh-110px)] p-[20px] overflow-y-auto">
            <div v-if="selectSegment === '我的角色'"
                class="grid grid-cols-[repeat(auto-fill,minmax(200px,1fr))] gap-[20px]">
                <CharactorCard v-for="item in list" :key="item" />
            </div>
            <div v-else>
                <!-- 社区角色列表 -->
            </div>
        </div>

        <!-- 新建角色模态框 -->
        <div :class="['modal-overlay', { 'show': showModal }]">
            <div class="modal">
                <div class="modal-header">
                    <span>新建角色资产</span>
                    <i class="fa-solid fa-xmark" style="cursor:pointer" @click="showModal = false"></i>
                </div>
                <div class="form-group">
                    <label class="form-label">参考头像</label>
                    <div class="upload-placeholder">
                        <i class="fa-solid fa-cloud-arrow-up" style="font-size: 24px; margin-bottom: 8px;"></i>
                        <span>点击上传或拖拽图片到此处</span>
                    </div>
                </div>
                <div class="form-group">
                    <label class="form-label">角色名称</label>
                    <input type="text" class="form-input" placeholder="例如：Cyber Ninja">
                </div>
                <div class="form-group">
                    <label class="form-label">触发词 (Trigger Word)</label>
                    <input type="text" class="form-input" placeholder="@role_name (用于Prompt调用)">
                </div>
                <div class="form-group">
                    <label class="form-label">外观描述</label>
                    <textarea class="form-input" style="height: 80px; resize: none;"
                        placeholder="详细描述角色的发型、衣着、面部特征..."></textarea>
                </div>
                <div class="modal-footer">
                    <button class="btn-cancel" @click="showModal = false">取消</button>
                    <button class="btn-confirm" onclick="createChar()">确认创建</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import SegmentControl from '../SegmentControl.vue'
import CharactorCard from './CharactorCard.vue'

const segments = ["我的角色", "社区角色"]
const selectSegment = ref('我的角色')

const emit = defineEmits(['dismiss'])
const showModal = ref(false)


const list = ref([1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20])

</script>

<style scoped>
.create-btn {
    margin-left: auto;
    background: linear-gradient(135deg, #2d60ff 0%, #00c6ff 100%);
    border: none;
    padding: 10px 24px;
    border-radius: 8px;
    color: #fff;
    font-weight: 500;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 8px;
    transition: transform 0.2s;
}

/* 模态弹窗 */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.7);
    backdrop-filter: blur(5px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
    opacity: 0;
    visibility: hidden;
    transition: all 0.3s;
}

.modal-overlay.show {
    opacity: 1;
    visibility: visible;
}

.modal {
    background: #131620;
    width: 500px;
    border-radius: 12px;
    border: 1px solid #2a2e45;
    padding: 24px;
    box-shadow: 0 20px 50px rgba(0, 0, 0, 0.5);
    transform: translateY(20px);
    transition: all 0.3s;
}

.modal-overlay.show .modal {
    transform: translateY(0);
}

.modal-header {
    color: #ffffff;
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
    font-size: 18px;
    font-weight: bold;
}

.form-group {
    margin-bottom: 16px;
}

.form-label {
    display: block;
    color: #8b92a7;
    margin-bottom: 8px;
    font-size: 14px;
}

.form-input {
    width: 100%;
    background: #1c1f2e;
    border: 1px solid #2a2e45;
    padding: 10px;
    border-radius: 6px;
    color: #fff;
    font-size: 14px;
}

.form-input:focus {
    border-color: #2d60ff;
}

.upload-placeholder {
    width: 100%;
    height: 120px;
    background: #1c1f2e;
    border: 1px dashed #2a2e45;
    border-radius: 6px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #8b92a7;
    cursor: pointer;
}

.modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 24px;
}

.btn-cancel {
    background: transparent;
    border: 1px solid #2a2e45;
    color: #8b92a7;
    padding: 8px 16px;
    border-radius: 6px;
    cursor: pointer;
}

.btn-confirm {
    background: #2d60ff;
    border: none;
    color: #fff;
    padding: 8px 20px;
    border-radius: 6px;
    cursor: pointer;
}
</style>