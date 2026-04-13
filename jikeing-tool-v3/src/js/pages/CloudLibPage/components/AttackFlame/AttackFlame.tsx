import styles from './AttackFlame.module.scss'

import useUserStore from "@/js/store/useUserStore"

import mTypes from '../types.json'
import mList from '../materials.json'
import { ensureFolder } from "@/js/utils/fileUtils"
import downloadFile from "@/js/utils/download"
import { evalTS } from "@/js/lib/utils/bolt"
import CloudVideoCard from "../CloudVideoCard/CloudVideoCard"
import { useEffect, useState } from 'react'


interface IProps {

}

const AttackFlame: React.FC<IProps> = (props) => {

    const [fileList, setFileList] = useState([])

    const [loadUrl, setLoadUrl] = useState('')

    const getMaterialList = (typeId: number) => {
        let materials = mList.filter(m => m.typeId == 33)[0].children ?? []
        setFileList(materials)
    }

    useEffect(() => {
        getMaterialList()
    }, [])

    return <>
        <div className="flex text-[18px] text-[white] p-[16px] pb-[0] gap-[16px] h-full box-border">
            <div className="flex w-full h-full bg-[#252726]" >
                <div className={styles.gridContainer}>
                    {
                        fileList.map(f => <CloudVideoCard key={f.name} video={f} loadUrl={loadUrl} updateLoadUrl={setLoadUrl} />)
                    }
                </div>
            </div>
        </div>
    </>
}

export default AttackFlame