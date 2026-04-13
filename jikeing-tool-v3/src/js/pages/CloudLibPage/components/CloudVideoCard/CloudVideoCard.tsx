
import { message, Spin } from 'antd'
import styles from './CloudVideoCard.module.scss'
import { evalTS } from '@/js/lib/utils/bolt'
import downloadFile from '@/js/utils/download'
import useUserStore from '@/js/store/useUserStore'
import { ensureFolder } from '@/js/utils/fileUtils'
interface IProps {
    video: any
    loadUrl: string
    updateLoadUrl: (url: string) => void
}

const CloudVideoCard: React.FC<IProps> = (props) => {

    const materialCard = (f) => {
        return <div className={styles.gridItemCoverWrapper} onClick={() => addToAE(f.name, f.url)}>
            <img className={styles.gridItemCoverVideo} src={f.coverUrl} />
            <div className={styles.girdItemAuthorRight}>@王野特效</div>
        </div>
    }

    const addToAE = async (name: string, urlString: string) => {

        // 创建URL对象
        const url = new URL(urlString.trim()); // 使用trim()去除末尾的换行符\n
        // 从路径中提取文件名
        // const fileName = name + '-' +url.pathname.split('/').pop();
        const fileName = name + '.mp4';

        let rootPath = await evalTS('joinPath', [useUserStore.getState().localRootPath, "downloads"])
        // 判断文件夹是否存在
        let checkFolderRes = ensureFolder(rootPath)

        if (checkFolderRes.success == false) {
            message.error('文件下载失败')
            return
        }

        let filePath = await evalTS('joinPath', [rootPath, fileName])

        if (window.cep.fs.stat(filePath).data.mtime) {
            evalTS("addResource", filePath)
        } else {
            props.updateLoadUrl(urlString)
            downloadFile(urlString, filePath, (loaded, total, finish) => {
                props.updateLoadUrl('')
                if (finish) {
                    console.log('文件下载完成')
                    evalTS("addResource", filePath)
                }
            })
        }
    }

    return <>
        <div key={props.video.name} className={styles.gridItem}>
            {props.loadUrl === props.video.url && <Spin>
                {materialCard(props.video)}
            </Spin>}
            {props.video.loadUrl != props.video.url && materialCard(props.video)}

            {/* <Spin>
                {materialCard(props.video)}
            </Spin> */}

            <div className={styles.gridItemTitle}>{props.video.name}</div>
        </div>
    </>
}

export default CloudVideoCard