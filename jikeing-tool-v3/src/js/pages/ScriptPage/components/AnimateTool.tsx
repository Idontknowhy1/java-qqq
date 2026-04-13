import { useState } from "react";
import { evalTS } from "../../../lib/utils/bolt";
import { Input, InputNumber } from "antd";

import leftTop from '../../../assets/images/left-top.png';
import top from '../../../assets/images/top.png';
import rightTop from '../../../assets/images/right-top.png';
import left from '../../../assets/images/left.png';
import center from '../../../assets/images/center.png';
import right from '../../../assets/images/right.png';
import leftBottom from '../../../assets/images/left-bottom.png';
import bottom from '../../../assets/images/bottom.png';
import rightBottom from '../../../assets/images/right-bottom.png';

import alignTimeImage from '../../../assets/images/align-time.png';
import alignLayerImage from '../../../assets/images/align-layer.png';

interface IProps {

}

const AnimateTool: React.FC<IProps> = (props) => {

    const [alignAmount, setAlignAmount] = useState(1)
    const [alignStep, setSlignStep] = useState(1)

    const animToolList = [
        {
            title: '弹性',
            onclick: (event) => {
                if (event.nativeEvent.shiftKey === true) {
                } else {
                    evalTS("placeElasticExpress")
                }
            }
        },
        {
            title: '弹跳',
            onclick: (event) => {
                if (event.nativeEvent.shiftKey === true) {
                } else {
                    evalTS("placeBounceExpress")
                }
            }
        },
        {
            title: '克隆',
            onclick: (event) => {
                if (event.nativeEvent.shiftKey === true) {
                    evalTS("cloneKeyframe", true)
                } else {
                    evalTS("cloneKeyframe", false)
                }
            }
        }
    ]

    const changeAnchorPoint = (type: number) => {
        evalTS("changeAnchorPosition", type)
    }

    const addNullObj = () => {
        evalTS('addNullObj')
    }

    const alignCurrentTime = () => {
        evalTS("alignCurrentTime");
    }

    const alignLayerByLayer = () => {
        evalTS("alignLayerByLayer", alignAmount, alignStep);
    }

    return <>
        <div className="p-[16px]">
            <div className="flex">
                <div style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(3, 35px)',
                    gridTemplateRows: 'repeat(3, 1fr)',
                    gap: '8px'
                }}>
                    <img src={leftTop} onClick={() => changeAnchorPoint(1)} />
                    <img src={top} onClick={() => changeAnchorPoint(2)} />
                    <img src={rightTop} onClick={() => changeAnchorPoint(3)} />
                    <img src={left} onClick={() => changeAnchorPoint(4)} />
                    <img src={center} onClick={() => changeAnchorPoint(5)} />
                    <img src={right} onClick={() => changeAnchorPoint(6)} />
                    <img src={leftBottom} onClick={() => changeAnchorPoint(7)} />
                    <img src={bottom} onClick={() => changeAnchorPoint(8)} />
                    <img src={rightBottom} onClick={() => changeAnchorPoint(9)} />
                </div>

                <div className="vertical-btn-group flex-1 ml-[16px]">
                    {animToolList.map(t => <button key={t.title} className="vertical-btn-group-item" onClick={t.onclick}>
                        {t.title}
                    </button>)}
                </div>
            </div>

            <div className="flex gap-[8px] items-center mt-[16px]">
                <button className="vertical-btn-group-item w-[100px]">顺序</button>
                <img src={alignTimeImage} onClick={alignCurrentTime} />
                <img src={alignLayerImage} onClick={alignLayerByLayer} />
                <label className="flex items-center text-[12px] text-[white] space-x-[8px]">amount
                    <InputNumber className="w-[60px] h-[25px]" min={1} max={1000000} controls={false} precision={0} value={alignAmount} onChange={(e) => setAlignAmount(e.target.value)} />
                </label>
                <label className="flex items-center text-[12px] text-[white] space-x-[8px]">step
                    <InputNumber className="w-[60px] h-[25px]" min={1} max={1000000} controls={false} precision={0} value={alignStep} onChange={(e) => setSlignStep(e.target.value)} />
                </label>
            </div>

            <button className="vertical-btn-group-item mt-[16px]" onClick={addNullObj}>添加空对象</button>
        </div>
    </>
}

export default AnimateTool