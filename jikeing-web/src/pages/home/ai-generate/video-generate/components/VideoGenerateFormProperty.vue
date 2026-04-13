<template>
    <div
        class="flex items-center justify-between w-full h-[40px] px-[16px] py-[10px] bg-[#1c1f2e] text-[14px] rounded-[6px]">
        <div class="text-[#8b92a7]">{{ title }}</div>
        <div class="flex items-center justify-center h-full">
            <slot />
        </div>
    </div>
</template>
<script setup lang="ts">
defineProps(['title'])
</script>
<style scoped></style>