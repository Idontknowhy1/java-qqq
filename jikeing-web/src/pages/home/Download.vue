<template>
    <div class="main-container">
        <HeaderNav></HeaderNav>
        <div class="download_box">
            <div class="name">
                <div class="title">Jike Tool</div>
                <div class="remark">提升效率的必备工具</div>
            </div>

            <div class="tool_content">
                <div class="img">
                    <img src="@/assets/images/themes/download_cover.png" />
                </div>
                <div class="info">
                    <div class="title">Jike Tool</div>
                    <div class="desc">一款强大的生产力工具，帮助您更高效地完成工作。集成了多种实用功能，界面简洁直观，使用便捷。 </div>
                    <div class="item">
                        <div class="icon"><img src="@/assets/images/themes/checked.png" /></div>
                        <div>动画插件</div>
                    </div>
                    <div class="item">
                        <div class="icon"><img src="@/assets/images/themes/checked.png" /></div>
                        <div>工程管理</div>
                    </div>
                    <div class="item">
                        <div class="icon"><img src="@/assets/images/themes/checked.png" /></div>
                        <div>资源预设</div>
                    </div>

                    <div class="operate">

                        <div class="download_btn" v-if="userStore.loginStatus && userStore.is_plugin_member == 1">
                            <a :href="downloadUrl" target="_blank">
                                <div class="icon"><img src="@/assets/images/themes/download_icon.png" /></div>
                                <div>立即下载</div>
                            </a>
                        </div>
                        <div class="download_btn" v-else @click="checkDownload">
                            <div class="icon"><img src="@/assets/images/themes/download_icon.png" /></div>
                            <div>立即下载</div>
                        </div>

                        <!--            <div class="remark">
              <div class="icon"><img src="@/assets/images/themes/remark_icon.png" /></div>
              <div>功能介绍</div>
            </div>-->
                    </div>
                </div>

            </div>



            <div class="remark_info">
                <div class="title">核心特性</div>
                <div class="item_box">
                    <div class="item_list">
                        <div class="icon"><img src="@/assets/images/themes/icon_1.png" /></div>
                        <div class="item_name">快速处理</div>
                        <div class="remark">高效的数据处理能力，显著提升工作效率</div>
                    </div>
                    <div class="item_list">
                        <div class="icon"><img src="@/assets/images/themes/icon_2.png" /></div>
                        <div class="item_name">工程管理</div>
                        <div class="remark">高效存储，一键管理</div>
                    </div>
                    <div class="item_list">
                        <div class="icon"><img src="@/assets/images/themes/icon_3.png" /></div>
                        <div class="item_name">自动更新</div>
                        <div class="remark">定期推送更新，持续优化用户体验</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import HeaderNav from './components/HeaderNav.vue';
import { useUserStore } from '../../store/useUserStore';
import { ElMessage } from 'element-plus';
import { getSysConfig } from '@/api/home/common';
import { onMounted, ref } from 'vue';

const userStore = useUserStore()

// const downloadUrl = 'https://pan.baidu.com/s/159sPAJzymhZkOofP2eEQeQ?pwd=wi87'
const downloadUrl = ref('')

const checkDownload = () => {
    if (userStore.loginStatus == 0) {
        userStore.setDialogLoginStatus(true)
    } else {
        ElMessage({
            message: '很抱歉，只有会员身份才能下载， 或重新登录验证身份',
            type: 'warning',
        })
    }
}


const fetchUrl = async () => {
    try {
        const res = await getSysConfig();
        if (res.code === 10000) {
            downloadUrl.value = res.data.aeUrl
        } else {
            ElMessage.error("获取失败:" + res.msg);
            console.error("获取失败:", res.msg);
        }
    } catch (error) {
        console.log(error);
    }
};

onMounted(() => {
    fetchUrl()
})

</script>

<style scoped lang="scss">
.download_box {
    border: 0px solid #ff0000;
    width: 100%;
    height: 100%;
    overflow-y: hidden;

    .name {
        // background: #111226;
        color: #fff;
        text-align: center;

        .title {
            font-size: 24px;
            font-weight: bold;
            margin: 40px 0 0 30px;
        }

        .remark {
            font-size: 14px;
            color: #9CA3AF;
            margin: 20px 0 0px 30px;
            padding: 0 0 40px 0;
        }
    }

    .tool_content {
        width: 90%;
        min-height: 400px;
        border: 0px solid #ff0000;
        margin: 0 auto;
        padding: 20px;
        border-radius: 10px;
        display: flex;
        color: #FFFFFF;
        background: #111226;

        .img {
            width: 45%;

            img {
                width: 100%;
                border-radius: 4px;
            }
        }

        .info {
            width: 55%;

            .title {
                font-size: 24px;
                font-weight: bold;
                margin: 20px 0 20px 50px;
            }

            .desc {
                font-size: 16px;
                color: #9CA3AF;
                margin: 30px 10px 30px 50px;
                line-height: 24px;
                border: 0px solid #ff0000;
            }

            .item {
                font-size: 16px;
                display: flex;
                align-items: center;
                color: #9CA3AF;
                margin: 20px 0 0 50px;

                .icon {
                    width: 15px;
                    height: 15px;
                    margin: 0 10px 0 0;

                    img {
                        width: 100%;
                    }
                }
            }

            .operate {
                //width: 100%;
                border: 0px solid #ff0000;
                margin: 40px 0 0 50px;
                display: flex;
                font-size: 14px;

                .download_btn {
                    border-radius: 4px;
                    background: linear-gradient(0deg, rgba(0, 0, 0, 0), rgba(0, 0, 0, 0)), linear-gradient(90deg, #2563EB 0%, #DB2777 100%);
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    color: #fff;
                    padding: 12px 14px;
                    margin: 0 40px 0 0;

                    a {
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        color: #fff;

                        .icon {
                            width: 18px;
                            height: 18px;
                            margin: 0 10px 0 0;

                            img {
                                width: 100%;
                            }
                        }
                    }

                }

                .remark {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    color: #fff;
                    padding: 6px 14px;
                    margin: 0 40px 0 0;
                    border-radius: 4px;
                    background: rgba(0, 0, 0, 0);
                    box-sizing: border-box;
                    border: 1px solid #4B5563;

                    .icon {
                        width: 16px;
                        height: 16px;
                        margin: 0 10px 0 0;

                        img {
                            width: 100%;
                        }
                    }
                }
            }
        }
    }

    .remark_info {
        border: 0px solid #ff0000;
        margin: 50px 0 0 0;

        .title {
            //width: 96%;
            font-size: 20px;
            margin: 0 0 20px 3%;
            color: #fff;
            border: 0px solid #ff0000;
        }

        .item_box {
            width: 96%;
            margin: 0px auto 50px;
            display: flex;
            color: #fff;

            .item_list {
                width: 31%;
                border-radius: 16px;
                background: linear-gradient(0deg, rgba(0, 0, 0, 0.001), rgba(0, 0, 0, 0.001)), rgba(26, 27, 46, 0.5);
                backdrop-filter: blur(10px);
                margin: 0 1% 0 1%;

                .icon {
                    width: 20px;
                    height: 20px;
                    margin: 20px 0 20px 30px;

                    img {
                        width: 100%;
                    }
                }

                .item_name {
                    font-size: 16px;
                    margin: 20px 0 20px 30px;
                }

                .remark {
                    color: #9CA3AF;
                    font-size: 16px;
                    margin: 0 0 10px 30px;
                    padding: 0 0 10px 0;
                }
            }
        }
    }
}
</style>