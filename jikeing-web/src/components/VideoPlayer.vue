template
<template>
    <div class="absolute left-0 top-0 w-full h-full bg-[black]/30 z-50 flex flex-col items-center justify-center"
        @click="emit('setShowVideo', false)">
        <video class="w-[50vw]" :src="playingVideoUrl" autoplay controls></video>
    </div>
</template>

<script setup lang="ts">

const props = defineProps({
    playingVideoUrl: {
        type: String,
        default: ''
    }
})

const emit = defineEmits(['setShowVideo'])

</script>
