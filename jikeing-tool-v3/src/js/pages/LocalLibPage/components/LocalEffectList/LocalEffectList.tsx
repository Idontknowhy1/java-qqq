import { useEffect, useRef, useState } from 'react';
import styles from './LocalEffectList.module.scss'
import { evalTS } from "@/js/lib/utils/bolt";
import { FileRef } from "@/jsx/aeft/aeft";
import { Dropdown, Menu, MenuProps, message, Tree, TreeDataNode } from "antd";
import { DataNode } from "antd/es/tree";

interface IProps {

}

const LocalEffectList: React.FC<IProps> = (props) => {

    const [folderList, setForderList] = useState<TreeDataNode[]>([])
    const [fileList, setFileList] = useState<FileRef[]>([])

    const videoRefs = useRef([]);

    const setVideoRef = (el, index) => {
        videoRefs.current[index] = el;
    };

    const reloadFolderList = () => {
        evalTS("getFolderContent", "effect", "ffx").then(content => {
            setForderList(content.filter(c => c.isFileOrFolder == false).map(p => ({
                title: p.name,
                key: p.path,
                children: p.children.filter(c => c.isFileOrFolder == false).map(c => ({
                    title: c.name,
                    key: c.path,
                    children: []
                }))
            })))
        })
    }

    function fileRef2VideoSrc(fileRef): any {
        const coverVideo = suffix2Type(fileRef.path, "mp4");
        return `file:///${coverVideo}`
    }

    function suffix2Type(path: string, type: string): string {
        const lastDotIndex = path.lastIndexOf(".");
        const prefix = path.substring(0, lastDotIndex);
        return `${prefix}.${type}`;
    }

    const playVideo = (index) => {
        if (videoRefs.current[index]) {
            videoRefs.current[index].play();
        }
    };

    const pauseVideo = (index) => {
        if (videoRefs.current[index]) {
            videoRefs.current[index].pause();
        }
    };

    function addResource2Project(audioPath: string) {
        evalTS("addEffect", audioPath)
    }

    function showFileList(path: string) {
        //   lastFileListFolderPath = path;
        //   if (funSelected.value == '工程') {
        //     evalTS("getFileBySuffix", path, "aep").then(response => {
        //       projectList.value = response;
        //     });
        //   }
        //   else if (funSelected.value == '音频') {
        //     evalTS("getAudioList", path).then(response => {
        //       audioList.value = response;
        //     });
        //   }
        //   else if (funSelected.value == '文案') {
        //     evalTS("getScriptList", path).then(response => {
        //       scriptList.value = response;
        //     });
        //   }
        //   else if (funSelected.value == '效果') {
            evalTS("getEffectFile", path).then(response => {
              setFileList(response)
            })
        //   }
        //   }
        //   else if (funSelected.value == '模板') {
        //     evalTS("getFileBySuffix", path, "aep").then(response => {
        //       templateList.value = response;
        //     })
        //   }
        //   else if (funSelected.value == '滤镜') {
        //     evalTS("getFileBySuffix", path, "ffx").then(response => {
        //       filterList.value = response;
        //     })
        //   }
    }

    useEffect(() => {
        reloadFolderList()
    }, [])

    const onSelect = (selectedKeys: React.Key[], info: any) => {
        console.log('selected', selectedKeys, info);
        // alert(JSON.stringify(info))
        showFileList(info.node.key)
        // alert(info.node.key)
    };

    const items: MenuProps['items'] = [
        {
            label: '1st menu item',
            key: '1',
        },
        {
            label: '2nd menu item',
            key: '2',
        },
        {
            label: '3rd menu item',
            key: '3',
        },
    ];

    const onClick: MenuProps['onClick'] = ({ key }) => {
        message.info(`Click on item ${key}`);
    };

    const titleRender = (nodeData: DataNode) => {
        return (
            <Dropdown menu={{ items, onClick }} trigger={['contextMenu']} >
                <div>{nodeData.title}</div>
            </Dropdown>
        );
    };

    return <>
        <div className="flex text-[14px] p-[16px]" style={{ height: 'calc(100% - 15px)' }}>
            <Tree
                className="w-[200px] bg-[#252726]"
                // style={{ backgroundColor: '#252726',width: 300 }}
                treeData={folderList}
                onSelect={onSelect}
                titleRender={titleRender}
                showLine
            />
            <div className="flex w-full h-full" >
                <div className={styles.gridContainer}>
                    {
                        fileList.map((f, index) => <div key={f.name} className={styles.gridItem} onClick={() => addResource2Project(f.path)}>

                            <video
                                className={styles.gridItemCoverVideo}
                                ref={el => setVideoRef(el, index)}
                                src={fileRef2VideoSrc(f)}
                                onMouseEnter={() => playVideo(index)}
                                onMouseLeave={() => pauseVideo(index)}
                                muted
                                loop
                            />

                            <div className={styles.gridItemName}>{f.name}</div>
                        </div>)
                    }
                </div>
            </div >
        </div >
    </>
}

export default LocalEffectList