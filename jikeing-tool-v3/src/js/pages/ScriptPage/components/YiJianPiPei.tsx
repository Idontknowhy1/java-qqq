import { evalTS } from "../../../lib/utils/bolt"

interface IProps {

}

const YiJianPiPei: React.FC<IProps> = (props) => {

    const buttonList = [
        {
            title: '复制（复制前需要框选关键帧）',
            onclick: () => evalTS("onCopyKeyframe")
        }
        ,
        {
            title: '粘贴速率',
            onclick: () => evalTS("onPasteKeyframeEase")
        }
        ,
        {
            title: '粘贴值',
            onclick: () => evalTS("onPasteKeyframeValue")
        }
        ,
        {
            title: '删除',
            onclick: () => evalTS("onDeleteKeyframe")
        }
    ]

    return <>
        <div className="p-[16px]">
            <div className="vertical-btn-group">
                {buttonList.map(btn => <button key={btn.title} className="vertical-btn-group-item" onClick={btn.onclick}>
                    {btn.title}
                </button>)}
            </div>
        </div >
    </>
}

export default YiJianPiPei