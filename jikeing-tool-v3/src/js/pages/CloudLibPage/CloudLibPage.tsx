import { useState } from "react"
import PageNavBar from "../HomePage/components/PageNavBar"
import CloudCengDieTeXiao from "./components/CloudCengDieTeXiao/CloudCengDieTeXiao"
import { evalTS } from "@/js/lib/utils/bolt"
import { Button } from "antd"
import AttackFlame from "./components/AttackFlame/AttackFlame"
import useUserStore from "@/js/store/useUserStore"
import LocalSetting from "../LocalLibPage/components/LocalSetting"

interface IProps {

}

const CloudLibPage: React.FC<IProps> = (props) => {
    const navItems: string[] = ['特效叠层', '转场预设', '音效', '预设工程', '滤镜调色']
    const [selectedTab, setSelectedTab] = useState('特效叠层')

    const localRootPath = useUserStore(state => state.localRootPath)

    return <>
        <div className="" style={{ height: 'calc(100% - 80px)' }}>
            {/* 已经设置过本地资源路径 */}
            {localRootPath.length > 0 && <div className="h-full">
                <PageNavBar tabs={navItems} updateTab={t => setSelectedTab(t)} selectedTab={selectedTab} />
                {selectedTab == '特效叠层' && <CloudCengDieTeXiao />}
                {selectedTab != '特效叠层' && <div className="text-[20px] text-[#999999] mt-[150px]">该功能正在开发中，敬请期待</div>}
            </div>}

            {/* 没有设置过本地资源路径 */}
            {localRootPath.length == 0 && <div>
                <PageNavBar tabs={['设置']} selectedTab='设置' updateTab={t => setSelectedTab(t)} />
                <LocalSetting />
            </div>}

        </div>
    </>
}

export default CloudLibPage