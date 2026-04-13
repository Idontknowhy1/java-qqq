<template>
    <div class="loading-inner">
        <div class="loading-spinner"></div>
    </div>
</template>

<style scoped>
/* 加载动画 */
.loading-spinner {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    border: 3px solid transparent;
    border-top-color: #2d60ff;
    border-right-color: #00c6ff;
    box-shadow: 0 0 20px rgba(45, 96, 255, 0.5);
    animation: spin 1s linear infinite;
}

.loading-inner {
    position: absolute;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    /* background: radial-gradient(circle, rgba(45, 96, 255, 0.1) 0%, rgba(0, 0, 0, 0) 70%); */
}

@keyframes spin {
    0% {
        transform: rotate(0deg);
    }

    100% {
        transform: rotate(360deg);
    }
}
</style>