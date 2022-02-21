import {colorArray, Event, Mark, PopMsg} from "@/common/data";

/**
 * 返回日期格式 yyyy-MM-dd
 * */
function dayFormat(date: Date): string {
    const y = date.getFullYear()
    const m = date.getMonth() + 1
    const d = date.getDate()
    return y + '-' + (m / 10 < 1 ? '0' + m : m) + '-' + (d / 10 < 1 ? '0' + d : d)
}

/**
 * 返回日期格式 HH:mm:ss
 * */
function timeFormat(date: Date): string {
    const h = date.getHours()
    const m = date.getMinutes()
    const s = date.getSeconds()
    return (h / 10 < 1 ? '0' + h : h) + ':' + (m / 10 < 1 ? '0' + m : m) + ':' + (s / 10 < 1 ? '0' + s : s)
}

/**
 * 返回日期格式 yyyy-MM-dd HH:mm:ss
 * */
function dateFormat(date: Date): string {
    return dayFormat(date) + ' ' + timeFormat(date)
}

/**
 * HH:mm:ss转秒数
 * */
function timeToSecond(time: string): number {
    if (!time.match(/^\d{2}:\d{2}:\d{2}/)) throw '日期格式错误'
    const [hh, mm, ss] = time.split(':') as unknown as [number, number, number]
    return (hh * 3600 + mm * 60 + (ss << 0))
}

/**
 * 秒数转HH:mm:ss
 * */
function secondToTime(second: number): string {
    const s = second % 3600 % 60
    const m = (second % 3600 - s) / 60
    const h = (second - 60 * m - s) / 3600
    const plusZero = (n: number) => (n < 10 ? '0' : '') + n
    return plusZero(h) + ':' + plusZero(m) + ':' + plusZero(s)
}

/**
 * yyyy-MM-dd字符串转Date对象
 * */
function getEventDate(event: Event | string): Date {
    const mDate = new Date()
    let y: number, m: number, d: number, h: number, s: number, ms: number
    if (typeof event === 'string') {
        if (event.match(/\d{4}-\d{2}-\d{2}/)) {
            if (!event.match(/ \d{2}:\d{2}:\d{2}/)) event += ' 00:00:00'
        } else throw '日期格式错误'
        const [day, time] = event.split(' ') as [string, string]
        [y, m, d] = day.split('-').map(value => value as unknown as number << 0);
        [h, s, ms] = time.split(':').map(value => value as unknown as number << 0)
    } else {
        [y, m, d] = event.day.split('-').map(value => value as unknown as number << 0);
        [h, s, ms] = event.time.split(':').map(value => value as unknown as number << 0)

    }
    mDate.setFullYear(y)
    mDate.setMonth(m - 1)
    mDate.setDate(d)
    mDate.setHours(h)
    mDate.setSeconds(s)
    mDate.setMilliseconds(ms)
    return mDate
}

/**
 * 过滤出列表a，b各自的特有的事件（去除a，b相同事件），simple因为忽略了color和alarm
 * */
function compareSimpleEvents(a: Event[], b: Event[]): [Event[], Event[]] {
    const diffA = getSimpleDiff(a, b)
    const diffB = getSimpleDiff(b, a)
    return [diffA, diffB]
}

/**
 * 过滤出列表a,b各自的特有的事件（去除a，b相同事件）
 * */
function compareEvents(a: Event[], b: Event[]): [Event[], Event[]] {
    const diffA = getEventsDiff(a, b)
    const diffB = getEventsDiff(b, a)
    return [diffA, diffB]
}

/**
 * 过滤出列表a的特有的事件（去除a，b相同事件），simple因为忽略了color和alarm
 * */
function getSimpleDiff(a: Event[], b: Event[]): Event[] {
    return a.filter(r => b.every(l => r.title != l.title || r.time != l.time || r.day != l.day || r.detail != l.detail))
}

/**
 * 过滤出列表a的特有的事件（去除a，b相同事件）
 * */
function getEventsDiff(a: Event[], b: Event[]): Event[] {
    return a.filter(i => b.every(l => i.title != l.title || i.time != l.time || i.day != l.day || i.detail != l.detail || i.color != l.color || i.alarm != l.alarm))
}

/**
 * 过滤出列表a特有的标记（去除a，b相同标记）
 * */
function compareMarks(a: Mark[], b: Mark[]): [Mark[], Mark[]] {
    const diffA = a.filter(r => b.every(l => r.date != l.date || r.info != l.info))
    const diffB = b.filter(r => a.every(l => r.date != l.date || r.info != l.info))
    return [diffA, diffB]
}

interface PartEvent {
    time?: string,
    title?: string,
    day?: string,
    detail?: string,
    alarm?: string,
    color?: string
}

/**
 * 返回只包含a特有的属性的对象（去除a，b相同属性）
 * */
function getEventDiff(a: Event, b: Event): PartEvent {
    const ma = JSON.parse(JSON.stringify(a))
    if (ma.day === b.day) delete ma.day
    if (ma.detail === b.detail) delete ma.detail
    if (ma.time === b.time) delete ma.time
    if (ma.title === b.title) delete ma.title
    if (ma.color === b.color) delete ma.color
    if (ma.alarm === b.alarm) delete ma.alarm
    return ma
}

/**
 * 返回只包含a，b特有的属性的对象（去除a，b相同属性）
 * */
function compareEvent(a: Event, b: Event): [PartEvent, PartEvent] {
    return [getEventDiff(a, b), getEventDiff(b, a)]
}


/**
 * 修改事件颜色，相同title具有相同颜色
 * */
function giveColor(events: Event[]) {
    let titleColor = new Map<string, string>()
    let colorIndex = 0
    events.forEach(e => {
        const color = titleColor.get(e.title)
        if (color !== undefined) e.color = color
        else {
            e.color = colorArray[colorIndex].value
            titleColor.set(e.title, e.color)
            colorIndex += 2
            if (colorIndex % 2 === 0) {
                if (colorIndex === colorArray.length) colorIndex = 1
                else if (colorIndex === colorArray.length + 1) colorIndex = 0
            }
            colorIndex = colorIndex % colorArray.length
        }
    })
}

/**
 * 打开全局提示框
 * */
function showPopMsg(config: PopMsg) {
    uni.$emit('showPopMsg', config)
}

/**
 * 关闭全局提示框
 * */
function closePopMsg() {
    uni.$emit('closePopMsg')
}

/**
 * 打开全局对话框
 * */
function showDialog(config: PopMsg) {
    uni.$emit('showDialog', config)
}

/**
 * 关闭全局对话框
 * */
function closeDialog() {
    uni.$emit('closeDialog')
}

export {
    compareMarks,
    dayFormat,
    timeFormat,
    getEventDate,
    dateFormat,
    giveColor,
    compareEvents,
    getEventsDiff,
    getEventDiff,
    compareEvent,
    getSimpleDiff,
    compareSimpleEvents,
    showPopMsg,
    closePopMsg,
    showDialog,
    closeDialog,
    timeToSecond,
    secondToTime
}
