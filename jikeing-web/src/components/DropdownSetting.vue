<!-- src/components/DropdownSetting.vue -->
<template>
    <div class="setting-item" ref="settingItemRef" v-click-outside="closeDropdown">
        <div class="dropdown-trigger" :class="{ 'active': isOpen }" @click="toggleDropdown" ref="triggerRef">
            <span class="current-value">{{ selectedValue }}</span>
            <svg class="arrow-icon" viewBox="0 0 24 24">
                <polyline points="6 9 12 15 18 9"></polyline>
            </svg>

            <!-- 下拉内容 -->
            <div v-if="isOpen" class="dropdown-menu bg-[red]"
                :class="{ 'show': isOpen, 'align-top': isFlipped, 'align-bottom': !isFlipped }" ref="menuRef">
                <div v-for="option in options" :key="option" class="dropdown-option"
                    :class="{ 'selected': selectedValue === option }" @click.stop="selectOption(option)">
                    {{ option }}
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue';

const props = defineProps({
    options: {
        type: Array,
        required: true,
    },
    modelValue: { // v-model 绑定值
        type: String,
        required: true,
    },
});

const emit = defineEmits(['update:modelValue']);

const isOpen = ref(true);
const isFlipped = ref(false); // 控制是否向上展开
const selectedValue = ref(props.modelValue);

// DOM 元素引用
const settingItemRef = ref(null);
const triggerRef = ref(null);
const menuRef = ref(null);

/**
 * 切换下拉菜单的显示/隐藏
 */
const toggleDropdown = async () => {
    console.log('toggleDropdown');
    isOpen.value = !isOpen.value;
    // if (isOpen.value) {
    //     await nextTick(); // 确保菜单已渲染在 DOM 中
    //     checkFlipPosition();
    // }
};

/**
 * 选中选项并更新 v-model
 * @param {string} option 选中的值
 */
const selectOption = (option) => {
    selectedValue.value = option;
    emit('update:modelValue', option);
    isOpen.value = false;
};

/**
 * 关闭下拉菜单 (由 v-click-outside 指令调用)
 */
const closeDropdown = () => {
    isOpen.value = false;
};

/**
 * 检查下拉菜单是否需要向上展开
 */
const checkFlipPosition = () => {
    if (!triggerRef.value || !menuRef.value) return;

    const triggerRect = triggerRef.value.getBoundingClientRect();
    const menuRect = menuRef.value.getBoundingClientRect();

    const spaceBelow = window.innerHeight - triggerRect.bottom;
    const spaceAbove = triggerRect.top;

    // 如果下方空间不足，且上方空间足够，则向上展开
    // 留一点裕量，比如 20px
    if (spaceBelow < menuRect.height + 20 && spaceAbove > menuRect.height + 20) {
        isFlipped.value = true;
    } else {
        isFlipped.value = false;
    }
};

// 监听 modelValue 变化，同步组件内部状态
watch(() => props.modelValue, (newVal) => {
    selectedValue.value = newVal;
});

// 在组件挂载时监听窗口大小变化，以便在调整窗口时重新检查菜单位置
// onMounted(() => {
//     window.addEventListener('resize', checkFlipPosition);
// });
// // 在组件卸载时移除监听器
// onMounted(() => {
//     window.removeEventListener('resize', checkFlipPosition);
// });
</script>

<style scoped>
/* 这里不再需要大部分样式，因为已经放在 global.css 中。
   scoped 样式只用于组件特有的、且不希望影响全局的样式。
   但在这个组件中，我们大部分样式都是通用的。
   为了演示，我保留了对 .setting-item 的 relative 定位，但实际上也可能在全局。
*/
/* .setting-item { */
/* 确保下拉菜单能相对于此定位 */
/* } */

/* 下拉菜单在组件内部，所以这里的 scoped 规则会生效 */
/* .dropdown-menu { */
/* 动画和定位通过 class 来控制 */
/* top / bottom 都在 style.css 中，这里不再重复 */
/* } */

/* --- setting-item 通用样式 --- */
.setting-item {
    /* 卡片背景 */
    border-radius: 12px;
    padding: 0;
    height: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    transition: background-color 0.2s;
    position: relative;
    /* 为下拉菜单提供定位上下文 */
}

/* --- 右侧下拉触发区域 --- */
.dropdown-trigger {
    display: flex;
    align-items: center;
    cursor: pointer;
    height: 100%;
    color: #ffffff;
    /* 右侧数值颜色 */
    font-size: 15px;
    user-select: none;
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
    /* [新增] 关键修复：让图标不阻挡点击，点击直接穿透到父级 div */
    pointer-events: none;
}

/* 激活状态下箭头旋转 */
.dropdown-trigger.active .arrow-icon {
    transform: rotate(180deg);
}

/* --- 下拉菜单样式 --- */
.dropdown-menu {
    position: absolute;
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
    /* opacity: 0;
    visibility: hidden;
    transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1); */
}

/* 默认向下展开 */
.dropdown-menu.align-bottom {
    top: calc(100% + 4px);
    /* 在触发器下方一点点 */
    transform: translateY(-10px);
}

/* 向上展开 */
.dropdown-menu.align-top {
    bottom: calc(100% + 4px);
    /* 在触发器上方一点点 */
    transform: translateY(10px);
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
    background-color: #22222c;
    /* 悬停背景 */
    color: white;
}

.dropdown-option.selected {
    color: #6e6eff;
    /* 选中项高亮色 */
    background-color: rgba(110, 110, 255, 0.1);
}
</style>