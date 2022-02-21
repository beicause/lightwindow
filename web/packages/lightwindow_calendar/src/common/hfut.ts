import axios, {AxiosResponse} from "axios";
import {Event} from "@/common/data";
import {dayFormat, timeFormat} from "@/common/util";

async function getHfutEvents(username: string, password: string) {
    const res = await hfutLogin(username, password)
    const token = res.data.data.token
    return await getHfutEventsByToken(token)
}

async function hfutLogin(username: string, password: string) {
    const data = new FormData()
    data.append('username', username)
    data.append('password', password)
    return await axios.request({
        url: 'https://smile.huii.top/user/login',
        method: 'POST',
        headers: {
            'content-type': 'application/x-www-form-urlencoded'
        },
        data
    });
}

async function getHfutEventsByToken(hfutToken: string) {
    const res = await getHfutSchedule(hfutToken)
    const {
        courses,
        diy,
        exams
    } = res.data.data
    const startDay = (await getHfutBase()).data.data.semesterstart
    //const events0 = parseHfut(startDay, diy)
    const events1 = parseHfut(startDay, exams)
    const events2 = parseHfut(startDay, courses)
    return [res, [...events1, ...events2], hfutToken] as [AxiosResponse, Event[], string]
}

async function getHfutSchedule(hfutToken: string) {
    return await axios.request({
        url: 'https://smile.huii.top/apps/schedule',
        headers: {
            token: hfutToken
        },
    });
}

async function getHfutBase() {
    return await axios.request({
        url: 'https://smile.huii.top/common/basicdatas'
    });
}

function parseHfut(startDay: string, type: Array<AnyObject>) {
    const events: Array<Event> = []
    for (let week = 0; week < type.length; week++) {
        type[week].forEach((e: { day: number; time: any[]; realTime: string | undefined; name: any; place: any; }) => {
            const date = new Date(startDay)
            date.setDate(date.getDate() + week * 7 + e.day - 1)
            switch (e.time[0]) {
                case 1:
                    date.setHours(8);
                    break
                case 2:
                    date.setHours(9);
                    break
                case 3:
                    date.setHours(10);
                    date.setMinutes(10);
                    break
                case 4:
                    date.setHours(11);
                    date.setMinutes(10);
                    break
                case 5:
                    date.setHours(14);
                    break
                case 6:
                    date.setHours(15);
                    break
                case 7:
                    date.setHours(16);
                    break
                case 8:
                    date.setHours(17);
                    break
                case 9:
                    date.setHours(19);
                    break
                case 10:
                    date.setHours(20);
                    break
                case 11:
                    date.setHours(21);
                    break
            }
            if (e.realTime != undefined) {
                const rt = (e.realTime.split('~'))[0]
                const [h, m] = rt.split(':')
                date.setHours(<number><unknown>h)
                date.setMinutes(<number><unknown>m)
            }
            const day = dayFormat(date)
            const time = timeFormat(date)
            const title = e.name
            const detail = e.place
            events.push({
                day,
                detail,
                time,
                title,
                alarm: '0',
                color: '#000000'
            } as Event)
        })
    }
    return events
}
export {
    getHfutEvents,
    getHfutEventsByToken
}
