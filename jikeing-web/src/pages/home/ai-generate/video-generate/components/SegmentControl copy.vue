<template>
    <div :class="['flex justify-center items-center bg-[#0b0c15] rounded-[6px] p-[4px]', customClass]">
        <div v-for="item in segments" :key="item"
            class="flex flex-1 justify-center items-center text-[#6B7280] h-full rounded-[6px] cursor-pointer"
            :class="['text-[#FFF] bg-[#1c1f2e]', textClass, { 'text-[#FFF] bg-[#2d60ff]': item === selectSegment }]"
            @click="emit('update:selectSegment', item)">
            {{ item }}
        </div>
    </div>
</template>
<script setup lang="ts">

defineProps(['segments', 'selectSegment', 'customClass', 'textClass'])
const emit = defineEmits(['update:selectSegment'])

</script>
<style scoped></style>