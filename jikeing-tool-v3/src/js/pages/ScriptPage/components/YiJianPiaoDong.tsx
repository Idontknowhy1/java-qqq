import { evalTS } from "../../../lib/utils/bolt"

interface IProps {

}

const YiJianPiaoDong: React.FC<IProps> = (props) => {

    const buttonList = [
        {
            title: '飘动',
            onclick: () => evalTS("sway")
        },
        {
            title: '飘动（3D）',
            onclick: () => alert("开发中")
        },
        {
            title: '清除图钉',
            onclick: () => evalTS("clearSwayElement")
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

export default YiJianPiaoDong