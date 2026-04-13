import useUserStore from "@/js/store/useUserStore"
import { useEffect, useState } from "react"
import PageNavBar from "../HomePage/components/PageNavBar"
import LocalSetting from "./components/LocalSetting"
import CengDieTeXiao from "./components/CengDieTeXiao/CengDieTeXiao"
import LocalEffectList from "./components/LocalEffectList/LocalEffectList"
import LocalAudioList from "./components/LocalAudioList/LocalAudioList"
import LocalFilterList from "./components/LocalFilterList/LocalFilterList"

interface IProps {

}

const LocalLibPage: React.FC<IProps> = (props) => {

    const localRootPath = useUserStore(state => state.localRootPath)

    const navItems: string[] = ['特效叠层', '转场预设', '音效', '预设工程', '滤镜调色', '设置']
    const [selectedTab, setSelectedTab] = useState('特效叠层')

    useEffect(() => {

    }, [])

    return <>
        <div className=""  style={{ height: 'calc(100% - 80px)' }}>

            {/* 已经设置过本地资源路径 */}
            {localRootPath.length > 0 && <div className="h-full">
                <PageNavBar tabs={navItems} updateTab={t => setSelectedTab(t)} selectedTab={selectedTab} />
                {selectedTab === '设置' && <LocalSetting />}
                {selectedTab === '特效叠层' && <CengDieTeXiao />}
                {selectedTab === '转场预设' && <LocalEffectList />}
                {selectedTab === '音效' && <LocalAudioList />}
                {selectedTab === '滤镜调色' && <LocalFilterList />}
            </div>}

            {/* 没有设置过本地资源路径 */}
            {localRootPath.length == 0 && <div>
                <PageNavBar tabs={['设置']} selectedTab='设置' updateTab={ t => setSelectedTab(t) } />
                <LocalSetting />
            </div>}

        </div>
    </>
}

export default LocalLibPage