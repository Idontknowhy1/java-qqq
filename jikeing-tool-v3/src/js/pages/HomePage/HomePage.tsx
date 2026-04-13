import { useEffect, useState } from "react"
import CloudLibPage from "../CloudLibPage/CloudLibPage"
import LocalLibPage from "../LocalLibPage/LocalLibPage"
import ScriptPage from "../ScriptPage/ScriptPage"
import HeaderBar from "./components/HeaderBar"
import { MainSegType } from "../../global"
import useUserStore from "@/js/store/useUserStore"

const HomePage = () => {

    const [selectedType, setSelectedType] = useState<MainSegType>('SCRIPT')

    useEffect(() => {
        // 加载本地资源路径
        useUserStore.getState().getLocalRootPath()
    }, [])

    return <>
        <div className="home-page text-[#FF0000] text-[50px] h-full">
            <HeaderBar 
                selectedType={selectedType}
                updateSelectedType={t => setSelectedType(t)}/>
            
            { selectedType == 'SCRIPT' && <ScriptPage /> }
            { selectedType == 'LOCAL' && <LocalLibPage /> }
            { selectedType == 'CLOUD' && <CloudLibPage /> }
        </div>
    </>
}

export default HomePage