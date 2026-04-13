import { evalTS } from "../../../lib/utils/bolt"

interface IProps {

}

const GugeBinding: React.FC<IProps> = (props) => {

    const buttonList = [
        {
            title: '添加骨骼',
            onclick: () => evalTS("addBoneEffect")
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

export default GugeBinding