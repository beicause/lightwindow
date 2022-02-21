
export interface Android {
    setSystemAlarm: () => void,
    addEvents: (events: string) => void,
    removeEvents: (events: string) => void,
    getEvents: () => string,
    close: () => void,
    showZoom: () => void,
    requestCsustEvents: (username: string, password: string) => string,
    requestGnnuEvents: (username: string, password: string) => string,
    redirectToMain: () => void,
    redirectToCalendar: () => void,
    setEnableSensor: (value: string) => void,
    getEnableSensor: () => string
}
interface Event {
    title: string,
    detail: string,
    day: string,
    time: string
    color: string,
    alarm: string
}
interface Mark {
    date: string,
    info: string
}

interface EduUserInfo {
    username: string
    password: string
    school: string
}
function marksMapToArray(m:Map<string,string>):Mark[]{
    const ms:Mark[]=[]
    for (let [k,v] of m){
        ms.push({date:k,info:v})
    }
    return ms
}
function marksArrayToMap(ms:Mark[]):Map<string,string>{
    const map=new Map<string,string>()
    for (let m of ms){
        map.set(m.date,m.info)
    }
    return map
}
const colorArray = [
    {name: '红色', value: '#F44336'},
    {name: '粉色', value: '#eca7a7'},
    {name: '橙色', value: '#FF9800'},
    {name: '黄色', value: '#FBC02D'},
    {name: '绿色', value: '#27ae60'},
    {name: '浅绿', value: '#8BC34A'},
    {name: '青色', value: '#00BCD4'},
    {name: '蓝色', value: '#007aff'},
    {name: '深蓝', value: '#536DFE'},
    {name: '紫色', value: '#9C27B0'},
]

interface PopMsg {
    msg: string,
    type: 'success' | 'error' | 'info' | 'warn',
    //duration undefined则不自动关闭
    duration?: number,
    confirm?:()=>void
}

export {EduUserInfo, colorArray, Event, PopMsg, Mark, marksArrayToMap, marksMapToArray}
