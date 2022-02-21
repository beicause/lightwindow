// const RSAKey = require('cryptico')
// import { Event } from './data'
// import { dateFormat, dayFormat, getEventDate } from './util'
// import axios from 'axios'

// type Pair<K, V> = {
//     first: K,
//     second: V
// }
// export async function getGnnuSchedule(username: string, password: string): Promise<Event[]> {
//     const cookie = await login(username, password)
//     const calendar = new Date()
//     const xqm = calendar.getMonth() >= 8 ? 3 : 12
//     const it = await axios.request({
//         url: "http://jwgl.gnnu.cn/jwglxt/kbcx/xskbcx_cxXsKb.html?gnmkdm=N2151&su=" + username,
//         headers: { "Cookie": cookie },
//         data: {
//             "xqm": "" + xqm,
//             "xnm": "" + calendar.getFullYear(),
//             "kzlx": "ck"
//         }
//     })
//     return parseSchedule(it.data, await getTermDate(username, cookie))
// }


// async function getTermDate(username: string, cookie: string): Promise<Date> {
//     const html = (await axios.request({
//         url: "http://jwgl.gnnu.cn/jwglxt/xtgl/index_cxAreaFive.html?localeKey=zh_CN&gnmkdm=index&su=" + username,
//         method: 'POST',
//         data: {},
//         headers: {
//             Cookie: cookie
//         }
//     })).data as string
//     const year = new Date().getFullYear()
//     const dayText = html.match(new RegExp(`>.*${year}.*$</th>`)) as unknown as string
//     const date = dayText.split("(")
//     return getEventDate(date[1].split("至")[0])
// }

// async function login(username: string, password: string): Promise<string> {
//     const cookieToken = await getCookieToken()
//     const csrftoken = cookieToken.second
//     const keys = await getPublicKey(cookieToken.first)
//     const enPassword = encodePassword(keys, password)
//     await axios.request({
//         url: `http://jwgl.gnnu.cn/jwglxt/xtgl/login_slogin.html?time=${new Date().getTime()}`,
//         headers: { Cookie: cookieToken.first },
//         data: {
//             "csrftoken": csrftoken,
//             "yhm": username,
//             "mm": enPassword,
//             "language": "zh_CN"
//         }
//     })
//     return document.cookie
// }

// async function getPublicKey(cookie: string): Promise<Pair<string, string>> {
//     const it = await axios.request({
//         headers: { "Cookie": cookie },
//         url: `http://jwgl.gnnu.cn/jwglxt/xtgl/login_getPublicKey.html?time=${new Date().getTime()}&_=${new Date().getTime()}`,
//     })
//     const arr = it.data.split("\"")
//     if (!arr) throw new Error("public key is null")
//     const m = arr[3]
//     const e = arr[7]
//     return {
//         first: m, second: e
//     }

// }

// async function getCookieToken(): Promise<Pair<string, string>> {
//     const it = await axios.get("http://jwgl.gnnu.cn/jwglxt/xtgl/login_slogin.html")
//     const html = it.data
//     const token = parseToken(html)
//     if (!token) throw new Error('token is null')
//     const cookie = document.cookie
//     return {
//         first: cookie, second: token
//     }
// }

// function parseSchedule(s: any, start: Date): Array<Event> {
//     const kbList = s["kbList"] as any[]
//     const list = [] as Event[]
//     for (const i in kbList) {
//         const k = kbList[i]
//         const title = k["kcmc"] as string
//         const detail = k["cdmc"] as string
//         const xqj = k["xqj"] as number
//         const t = k["jc"].split("-")[0] as string
//         let time =
//             t === "1" ? "08:20"
//                 : t === "2" ? "09:10"
//                     : t === "3" ? "10:15"
//                         : t === "4" ? "11:05"
//                             : t === "5" ? "14:00"
//                                 : t === "6" ? "14:50"
//                                     : t === "7" ? "15:55"
//                                         : t === "8" ? "16:45"
//                                             : t === "9" ? "19:10"
//                                                 : t === "10" ? "20:00"
//                                                     : t === "11" ? "20:50"
//                                                         : new Error("course time error")
//         if (typeof time !== 'string') throw time
//         else time += ':00'
//         let startWeek: number
//         let endWeek: number
//         let isStep: boolean
//         const it = k["zcd"].split("-")
//         startWeek = it[0].toInt()
//         endWeek = it[1].substring(0, it[1].indexOf('周')).toInt()
//         isStep = it[1][it[1].length - 2] == '单'

//         const calendar = new Date()
//         for (let w = startWeek; w <= endWeek; isStep ? w += 2 : w++) {
//             calendar.setTime(start.getTime())
//             calendar.setDate(calendar.getDate() + xqj % 7 + 1 - calendar.getDay())
//             calendar.setDate(calendar.getDate() + (w - 1) * 7)
//             const day = dayFormat(calendar)
//             list.push({ title, time, detail, day, color: '#000000', alarm: '0' })
//         }
//     }
//     return list
// }

// function parseToken(html: string): string | null {
//     const doc = document.createElement('div')
//     doc.innerHTML = html
//     return doc.querySelector("#csrftoken")?.getAttribute("constue") ?? null
// }

// function encodePassword(data: Pair<string, string>, password: string): string {
//     const rsaKey = new RSAKey();
//     rsaKey.setPublic(b64tohex(data.first), b64tohex(data.second))
//     const enPassword = hex2b64(rsaKey.encrypt(password))
//     return enPassword
// }

// //     function ByteArray.toHexstring():string{
// //        const sb=stringBuilder()
// //        for (i in this){
// //            const hex:string=Integer.toHexstring(0xff and i.toInt())
// //            if(hex.length==1) sb.append("0")
// //            sb.append(hex)
// //        }
// //        return sb.tostring()
// //    }
// const b64map = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
// const b64pad = '='
// function b64tohex(s: string): string {
//     var ret = ""
//     var k = 0 // b64 state, 0-3
//     var slop = 0
//     for (let i = 0; i < s.length; i++) {
//         if (s[i] == b64pad) break
//         const v = b64map.indexOf(s[i])
//         if (v < 0) continue
//         switch (k) {
//             case 0: {
//                 ret += int2char(v >> 2)
//                 slop = v & 3
//                 k = 1
//                 break
//             }
//             case 1: {
//                 ret += int2char((slop << 2) | (v >> 4))
//                 slop = v & 0xf
//                 k = 2
//                 break
//             }
//             case 2: {
//                 ret += int2char(slop)
//                 ret += int2char(v >> 2)
//                 slop = v & 3
//                 k = 3
//                 break
//             }
//             default: {
//                 ret += int2char((slop << 2) | (v >> 4))
//                 ret += int2char(v & 0xf)
//                 k = 0
//                 break
//             }
//         }
//     }
//     if (k == 1)
//         ret += int2char(slop << 2)
//     return ret
// }

// function hex2b64(h: string) {
//     var i;
//     var c;
//     var ret = "";
//     for (i = 0; i + 3 <= h.length; i += 3) {
//         c = parseInt(h.substring(i, i + 3), 16);
//         ret += b64map.charAt(c >> 6) + b64map.charAt(c & 63);
//     }
//     if (i + 1 == h.length) {
//         c = parseInt(h.substring(i, i + 1), 16);
//         ret += b64map.charAt(c << 2);
//     }
//     else if (i + 2 == h.length) {
//         c = parseInt(h.substring(i, i + 2), 16);
//         ret += b64map.charAt(c >> 2) + b64map.charAt((c & 3) << 4);
//     }
//     while ((ret.length & 3) > 0) ret += b64pad;
//     return ret;
// }


// function int2char(i: number): string {
//     const biRm = "0123456789abcdefghijklmnopqrstuvwxyz"
//     return biRm[i]
// }