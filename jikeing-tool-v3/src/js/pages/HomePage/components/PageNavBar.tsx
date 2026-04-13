
interface IProps {
    tabs: string[]
    selectedTab: string
    updateTab: (tab: string) => void
}

const PageNavBar: React.FC<IProps> = (props) => {
    return <>
        <div className="h-[30px] flex bg-[#333333] text-[12px] text-[white]">
            {
                props.tabs.map(t => <div key={t} className={`px-[18px] h-full flex items-center rounded-t-[5px] cursor-pointer ${props.selectedTab == t ? 'bg-[#2989E5]' : ''}`}
                    onClick={() => props.updateTab(t)}>
                    {t}
                </div>)
            }
        </div>
    </>
}

export default PageNavBar