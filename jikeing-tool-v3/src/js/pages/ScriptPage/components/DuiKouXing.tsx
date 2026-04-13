import { evalTS } from "../../../lib/utils/bolt"

interface IProps {

}

const DuiKouXing: React.FC<IProps> = (props) => {

    const buttonList = [
        {
            title: '添加音频',
            onclick: () => evalTS("copyAudioLayer2EmbedCompItem")
        },
        {
            title: '删除音频',
            onclick: () => evalTS("deleteMergeAudio")
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

export default DuiKouXing