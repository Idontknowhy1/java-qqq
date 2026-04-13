import { evalTS } from "../../../lib/utils/bolt"

interface IProps {

}

const IKMuOu: React.FC<IProps> = (props) => {

    const buttonList = [
        {
            title: '创建控制器',
            onclick: () => evalTS("createControllers")
        }, {
            title: '创建 IK',
            onclick: () => evalTS("createIK")
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

export default IKMuOu