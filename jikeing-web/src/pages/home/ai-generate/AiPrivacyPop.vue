<template>
    <el-dialog v-model="dialogVisible" width="590" :show-close="false" style="padding: 0;" :close-on-click-modal="false"
        :close-on-press-escape="false" append-to-body>
        <div class="privacy-content-wrapper">
            <img src="@/assets/images/ai-privacy-bg-image.png">
            <div class="content-wrapper">
                <div class="title">开始在即课中进行创作</div>
                <div class="desc">使用此功能，即表示您确认您对上传的任何内容拥有必要的权利。不要生成侵犯他人知识产权或隐私权的内容。您生成的内容将存储在我们的服务器上，以及推送到该角色资产中。</div>
                <div class="buttons-wrapper">
                    <img class="bottom-button" src="@/assets/images/ai-privacy-refuse-btn.png"
                        @click="emit('refuse')" />
                    <img class="bottom-button" src="@/assets/images/ai-privacy-agree-btn.png" @click="emit('agree')" />
                </div>
            </div>
        </div>
    </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const emit = defineEmits(['refuse', 'agree'])

const dialogVisible = ref(true)

</script>

<style lang="scss" scoped>
.privacy-content-wrapper {
    background-color: #22173D;
    width: 100%;
    height: 578px;
    border-radius: 20px;
    z-index: 1000;

    .content-wrapper {
        display: flex;
        flex-direction: column;
        padding: 20px;

        .title {
            font-size: 24px;
            color: white;
            font-weight: 600;
        }

        .desc {
            font-size: 14px;
            color: #666;
            margin-top: 16px;
            line-height: 26px;
        }

        .buttons-wrapper {
            height: 52px;
            width: 100%;
            margin-top: 20px;
            display: flex;
            justify-content: space-between;

            .bottom-button {
                width: 258px;
                height: 52px;
            }
        }
    }
}
</style>