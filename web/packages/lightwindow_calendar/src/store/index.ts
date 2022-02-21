import Vue from 'vue'
import Vuex from 'vuex'
import {compareEvents, dateFormat, dayFormat, getEventsDiff, timeToSecond} from '@/common/util'
import {getStorage, setStorage} from '@/common/cache'
import {EduUserInfo, Event, Mark, marksArrayToMap, marksMapToArray} from '@/common/data'
import {androidSyncData} from "@/common/android";

Vue.use(Vuex);
//缓存
//updateTime修改事件
//events事件
//eduUserInfo教务账号信息
//marks月历标记
//eduEvents课程事件
const store = new Vuex.Store({
    state: {
        events: [] as Event[],
        marks: new Map<string, string>(),
        updateTime: '',
        activeDay: dayFormat(new Date()),
        eduUserInfo: {school: '', username: '', password: ''} as EduUserInfo,
        eduEvents: [] as Event[]
    },
    getters: {
        getDayEvents: (state) =>
            (day: string) => state.events.filter(i => i.day === day)
                .sort((a, b) => timeToSecond(a.time) - timeToSecond(b.time)) as Event[]
    },
    mutations: {
        setActiveDay(state, day: string) {
            if (!day.match(/\d{4}-\d{2}-\d{2}/)) throw '日期格式错误'
            state.activeDay = day
        },
        /**
         * 将缓存载入store的数据中
         * */
        initAll(state) {
            const e = getStorage('events')
            const m = getStorage('marks')
            const u = getStorage('eduUserInfo')
            const ee = getStorage('eduEvents')
            state.events = e ? JSON.parse(e) : []
            state.marks = m ? marksArrayToMap(JSON.parse(m)) : new Map<string, string>()
            state.eduUserInfo = u ? JSON.parse(u) : {school: '', username: '', password: ''} as EduUserInfo
            state.eduEvents = ee ? JSON.parse(ee) : []
            androidSyncData(state.events)
            state.updateTime = getStorage('updateTime')
            state.activeDay = dayFormat(new Date())
        },
        /**
         * 用这个方法改变的变量
         * */
        setAndCache(state, payload: { updateTime?: string, eduUserInfo?: EduUserInfo, eduEvents?: Event[] }) {
            for (let payloadKey in payload) {
                const setValue:any=(payload as any)[payloadKey];
                (state as any)[payloadKey] =setValue
                setStorage(payloadKey as any,typeof setValue==='string'?setValue:JSON.stringify(setValue))
            }
        },
        updateMarks(state, marks: Map<string, string>) {
            state.marks = marks
        },
        addMarks(state, marks: Mark[]) {
            marks.forEach(mark => state.marks.set(mark.date, mark.info))
            state.marks = new Map<string, string>(state.marks)
        },
        removeMarks(state, marks: Mark[]) {
            marks.forEach(mark => state.marks.delete(mark.date))
            state.marks = new Map<string, string>(state.marks)
        },
        cacheMarks(state) {
            // console.log('缓存marks')
            setStorage("marks", JSON.stringify(marksMapToArray(state.marks)))
        },
        cacheEvents(state) {
            const e = getStorage('events')
            const [a, b] = compareEvents(state.events, JSON.parse(e ? e : "[]"))
            // console.log(a, b)
            if (a.length === 0 && b.length === 0) {
                // console.log('无变化不缓存')
                return
            }
            setStorage('events', JSON.stringify(state.events));
            androidSyncData(state.events);
            (this as any).commit('setAndCache', {updateTime: dateFormat(new Date())})
            // console.log('缓存更新', state.updateTime)
        },
        addEvents(state, events: Event[]) {
            const b = getEventsDiff(events, state.events)
            state.events = [...state.events, ...b]
        },
        removeEvents(state, events: Event[]) {
            state.events = getEventsDiff(state.events, events)
        },
        updateEvents(state, newValue: Event[]) {
            state.events = newValue
        },
        /**
         * 更新某天的事件
         * */
        updateDayEvents(state, payload: { dayEvents: Event[], day: string }) {
            const old = state.events.filter(i => i.day === payload.day) as Event[];
            (this as any).commit("removeEvents", old);
            (this as any).commit("addEvents", payload.dayEvents);
        },
    }
})

export default store
