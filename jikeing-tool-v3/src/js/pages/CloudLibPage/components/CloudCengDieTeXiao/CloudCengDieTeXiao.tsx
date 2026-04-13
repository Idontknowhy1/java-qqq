import { message, Spin, Tree, TreeDataNode } from "antd"
import { useEffect, useState } from "react"
import styles from './CloudCengDieTeXiao.module.scss'
import useUserStore from "@/js/store/useUserStore"

import mTypes from '../types.json'
import mList from '../materials.json'
import { ensureFolder } from "@/js/utils/fileUtils"
import downloadFile from "@/js/utils/download"
import { evalTS } from "@/js/lib/utils/bolt"
import CloudVideoCard from "../CloudVideoCard/CloudVideoCard"
import { sendGet } from "@/js/api/api"
import { getVideoMaterial, getVideoMaterialFolderList } from "@/js/api/common"

interface IProps {

}

const CloudCengDieTeXiao: React.FC<IProps> = (props) => {

    const [folderList, setForderList] = useState<TreeDataNode[]>([])
    const [fileList, setFileList] = useState([])

    const [loadUrl, setLoadUrl] = useState('')
    const [pageInfo, setPageInfo] = useState({ pageNum: 1, pageSize: 120 })
    const [loading, setLoading] = useState(false)

    const onSelect = (selectedKeys: React.Key[], info: any) => {
        console.log('selected', selectedKeys, info);
        getMaterialList(info.node.id)
    };

    const getFolderList = async () => {
        try {
            let res = await getVideoMaterialFolderList()
            if (res.code === 10000) {
                generateTree(res.data)
            } else {
                message.error('获取分类失败,' + res.msg)
                console.error('获取分类失败', res.msg)
            }
        } catch (err) {
            message.error('获取分类失败')
            console.error('获取分类失败', err)
        }
    }

    // 构造菜单数据
    const generateTree = (list) => {
        let map = {}
        list = list.map(t => {
            t.children = []
            t.title = t.name
            map[t.id] = t
            return t
        })
        list.forEach(t => {
            if (t.pid > 0) {
                map[t.pid].children.push(t)
            }
        })
        let folders = list.filter(f => f.pid == 0)
        setForderList(folders)
    }

    const getMaterialList = async (typeId: number) => {
        try {
            setLoading(true)
            let res = await getVideoMaterial(typeId, pageInfo.pageNum, pageInfo.pageSize)
            setLoading(false)
            if (res.code === 10000) {
                console.log('----', res)
                setFileList(res.data.list)
            } else {
                message.error('获取数据失败,' + res.msg)
                console.error('获取数据失败', res.msg)
            }
        } catch (err) {
            setLoading(false)
            message.error('获取数据失败')
            console.error('获取数据失败', err)
        }

    }

    useEffect(() => {
        getFolderList()
    }, [])

    return <>
        <div className="flex text-[18px] text-[white] p-[16px] pb-[0] gap-[16px] h-full box-border">
            <Tree
                className="w-[200px] bg-[#252726]"
                treeData={folderList}
                onSelect={onSelect}
                showLine
            />
            <div className="relative flex w-full h-full bg-[#252726]" >
                <div className={styles.gridContainer}>
                    {
                        fileList.map(f => <CloudVideoCard key={f.id} video={f} loadUrl={loadUrl} updateLoadUrl={setLoadUrl} />)
                    }
                </div>
                {
                    loading && <Spin style={{
                        position: "absolute",
                        left: 0,
                        top: 0,
                        width: '100%',
                        height: '100%',
                        display: "flex",
                        justifyContent: 'center',
                        alignItems: "center",
                        backgroundColor: 'rgba(0,0,0,0.3)'
                    }}>
                    </Spin>
                }
            </div>
        </div>
    </>
}

export default CloudCengDieTeXiao
