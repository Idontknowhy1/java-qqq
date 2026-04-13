<template>
    <div class="flex mt-[8px] p-[8px] text-[#999999] text-[12px] bg-[#333333] rounded">{{ text }}</div>
</template>

<script setup lang="ts">
defineProps({
    text: {
        type: String
    }
})
</script>
