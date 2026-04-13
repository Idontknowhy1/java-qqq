import { useState } from "react"
import PageNavBar from "../HomePage/components/PageNavBar"
import DuiKouXing from "./components/DuiKouXing"
import YiJianPiPei from "./components/YiJianPiPei"
import YiJianPiaoDong from "./components/YiJianPiaoDong"
import GugeBinding from "./components/GugeBinding"
import IKMuOu from "./components/IKMuOu"
import AnimateTool from "./components/AnimateTool"

interface IProps {

}

const ScriptPage: React.FC<IProps> = (props) => {

    const navItems: string[] = ['对口型', '一键匹配', '一键飘动', '动画工具', '骨骼绑定', '木偶IK']
    const [selectedTab, setSelectedTab] = useState('对口型')

    return <>
        <div className="h-full">
            <PageNavBar tabs={navItems} updateTab={t => setSelectedTab(t)} selectedTab={selectedTab} />

            { selectedTab === '对口型' && <DuiKouXing /> }
            { selectedTab === '一键匹配' && <YiJianPiPei /> }
            { selectedTab === '一键飘动' && <YiJianPiaoDong /> }
            { selectedTab === '动画工具' && <AnimateTool /> }
            { selectedTab === '骨骼绑定' && <GugeBinding /> }
            { selectedTab === '木偶IK' && <IKMuOu /> }
        </div>
    </>
}

export default ScriptPage