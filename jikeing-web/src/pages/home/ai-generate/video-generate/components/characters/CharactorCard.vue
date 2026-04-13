<template>
    <div class="flex flex-col bg-[#131620] rounded-[8px]">

        <!-- 图片 -->
        <img alt="" class="w-full h-[150px] rounded-t-[8px]" />

        <!-- 角色名称 -->
        <div class="flex items-center px-[10px] w-full h-[50px] border-b-[1px] border-b-[#666666]">
            <span class="text-[16px] font-bold text-[#fff]">角色名称</span>
        </div>

        <!-- 操作按钮 -->
        <div class="card-footer">
            <div class="card-actions">
                <div class="action-btn" title="编辑"><i class="fa-solid fa-pen"></i></div>
                <div class="action-btn delete" title="删除" onclick="deleteCard(this)"><i class="fa-solid fa-trash"></i>
                </div>
            </div>
        </div>
    </div>
</template>
<style scoped>
.card-footer {
    padding: 12px 10px;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    border-top: 1px solid #2a2e45;
}

.card-actions {
    display: flex;
    gap: 8px;
}

.action-btn {
    width: 32px;
    height: 32px;
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border: 1px solid #2a2e45;
    background: #1c1f2e;
    color: #8b92a7;
    transition: all 0.2s;
}

.action-btn:hover {
    color: #fff;
    border-color: #fff;
}

.action-btn.delete:hover {
    color: #ff4757;
    border-color: #ff4757;
    background: rgba(255, 71, 87, 0.1);
}
</style>