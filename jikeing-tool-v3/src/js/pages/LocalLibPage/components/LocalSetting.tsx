import useUserStore from "@/js/store/useUserStore"
import folderImage from '@/js/assets/images/folder.png'
import { evalTS } from "@/js/lib/utils/bolt"

interface IProps {

}

const LocalSetting: React.FC<IProps> = (props) => {

    const localRootPath = useUserStore(state => state.localRootPath)

    const changeResourceRoot = () => {
        evalTS("selectFolder").then((newPath: string) => {
            useUserStore.getState().getLocalRootPath()
        });
    }

    return <>
        <div className="flex text-[18px] text-[white] p-[16px] gap-[16px]">
            {localRootPath.length == 0 && <div>未设置</div>}
            {localRootPath.length > 0 && <div>资源目录</div>}
            <div>{localRootPath}</div>
            <img src={folderImage} onClick={changeResourceRoot} />
        </div>
    </>
}

export default LocalSetting