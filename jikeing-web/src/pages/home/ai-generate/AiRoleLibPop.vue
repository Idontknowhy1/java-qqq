<template>
    <el-dialog v-model="dialogVisible" width="700" :show-close="false" style="padding: 0;" append-to-body
        :close-on-press-escape="false" :close-on-click-modal="false">
        <div class="role-lib-wrapper">
            <div class="header-bar">
                <div class="title">角色库</div>
                <img class="close-btn" src="@/assets/images/close-btn.png" @click="emit('dismiss')" />
            </div>
            <div class="image-type-list">
                <div class="type-wapper first-seg-wrapper">
                    <div class="type-text" v-for="item in segments" :key="item.tag"
                        :class="{ selected: firstSelectedType == item.tag }"
                        @click="() => firstSegmentChanged(item.tag)">
                        {{ item.title }}
                    </div>
                </div>
                <div class="type-wapper second-seg-wrapper">
                    <div class="type-text" v-for="item in secondTypes" :key="item.title"
                        :class="{ selected: secondSelectedType == item.title }"
                        @click="() => secondSegmentChanged(item)">
                        {{ item.title }}
                    </div>
                </div>
            </div>
            <div class="list-wrapper" ref="target">
                <div class="role-item" v-for="item in list" :key="item.url">
                    <div class="image-wrapper" @click="emit('toggleImage', item.url)">
                        <el-image class="role-image" :src="item.url" fit="cover"
                            :class="{ selected: selectedImages.includes(item.url) }" /></img>
                        <!-- <div class="choose-btn" @click="selectedImage(item.url)">选择</div> -->
                    </div>
                    <span class="name">{{ item.name }}</span>
                </div>
            </div>


        </div>
    </el-dialog>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { onMounted, ref } from 'vue';
import { aiTaskList } from '@/api/home/common.js'
import { roleList } from '@/api/roleList.js'
import { actionList } from '@/api/actionList.js'
import { useInfiniteScroll } from '@vueuse/core';

const props = defineProps(['selectedImages'])
const emit = defineEmits(['toggleImage', 'dismiss'])
const list = ref([])

const dialogVisible = ref(true)

const firstSelectedType = ref('')
const secondSelectedType = ref('')

const secondTypes = ref([])

const loading = ref(false)
const target = ref(null);
const hasMore = ref(true);
const page = ref(1)
const pageSize = 20

const segments = [{
    title: 'AI生图',
    tag: 'generate'
}, {
    title: '角色',
    tag: 'role'
}, {
    title: '动作',
    tag: 'actions'
}, {
    title: '表情',
    tag: 'emotion'
}, {
    title: '场景',
    tag: 'scene'
}]

const firstSegmentChanged = (seg) => {
    firstSelectedType.value = seg
    secondTypes.value = []
    list.value = []
    if (seg == 'generate') {
        queryAiGenList()

    } else if (seg == 'role') {
        queryRoleList()

    } else if (seg == 'actions') {
        queryActionList()

    } else if (seg == 'emotion') {
    } else if (seg == 'scene') {
    } else if (seg == 'upload-split') {
    }
}

const secondSegmentChanged = (item) => {
    secondSelectedType.value = item.title
    list.value = item.items
}

/**
 * 获取AI生成的图片
 */
const queryAiGenList = async () => {
    try {
        let res = await aiTaskList({ params: { pageNum: page.value, pageSize: pageSize } })
        if (res.code === 10000) {
            if (firstSelectedType.value != 'generate') {
                return
            }

            let tempList = [{
                title: '全部',
                items: res.data.list.filter(t => t.status == 2).map(t => ({ name: '', url: JSON.parse(t.resultImages)[0] }))
            }]
            secondTypes.value = tempList
            secondSelectedType.value = '全部'
            list.value.push(...tempList[0].items);
            if (res.data.list < pageSize) {
                hasMore.value = false
            }
            if (res.data.list.length > 0) {
                page.value += 1
            }

        } else {
            ElMessage.error('数据加载失败,' + res.msg)
            console.error('ai列表数据加载失败', res.msg)
        }
    } catch (err) {
        ElMessage.error('数据加载失败')
        console.error('ai列表数据加载失败', err)
    }
}

/**
 * 查询角色列表
 */
const queryRoleList = async () => {
    secondTypes.value = roleList
    secondSelectedType.value = roleList[0].title
    list.value = roleList[0].items
}

/**
 * 查询角色列表
 */
const queryActionList = async () => {
    const tempList = actionList.map(item => {
        item.title = item.title.replace('动作', '')
        return item
    })
    secondTypes.value = tempList
    secondSelectedType.value = tempList[0].title
    list.value = tempList[0].items
}

onMounted(() => {
    firstSegmentChanged('generate')
})

useInfiniteScroll(
    target,
    async () => {
        if (firstSelectedType.value != 'generate' || loading.value || !hasMore.value) return;
        await queryAiGenList();
    },
    { distance: 10 }
);

</script>

<style lang="scss" scoped>
.role-lib-wrapper {
    padding: 30px;
    padding-top: 10px;
    box-sizing: border-box;
    background-color: #22173D;
    border-radius: 20px;

    .header-bar {
        width: 100%;
        height: 65px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        border-bottom: 1px solid #444;

        .title {
            font-size: 22px;
            color: white;
            font-weight: 600;
        }

        .close-btn {
            width: 32px;
            height: 32px;
        }
    }

    .image-type-list {
        display: flex;
        flex-direction: column;

        .type-wapper {
            display: flex;
            margin-bottom: 16px;
        }

        .type-text {
            font-size: 18px;
            color: #6B7280;
            margin-right: 10px;
            cursor: pointer;
            margin-right: 20px;
        }

        .type-text.selected {
            color: white;
        }

        .second-seg-wrapper .type-text {
            font-size: 16px;
        }
    }

    .list-wrapper {
        width: 100%;
        height: 400px;
        overflow: hidden;
        overflow-y: auto;
        display: flex;
        flex-wrap: wrap;
        align-content: flex-start;

        .role-item {
            display: flex;
            flex-direction: column;

            .image-wrapper {
                position: relative;
                width: 142px;
                height: calc(142px * 4 / 3);
                margin-right: 16px;
                background-color: #666;
                display: flex;
                justify-content: center;
                align-items: center;
                border-radius: 12px;
                margin-bottom: 8px;

                .role-image {
                    width: 100%;
                    height: 100%;
                    border-radius: 12px;
                    box-sizing: border-box;
                }

                .role-image.selected {
                    border: 5px solid #35C9FC;
                }

                .choose-btn {

                    display: flex;
                    justify-content: center;
                    align-items: center;
                    position: absolute;
                    background-color: #666;
                    width: 50px;
                    height: 20px;
                    right: 0;
                    bottom: 0;
                    cursor: pointer;
                    color: white;
                }
            }

            .name {
                font-size: 12px;
                color: white;
                margin-bottom: 16px;
            }
        }
    }
}
</style>