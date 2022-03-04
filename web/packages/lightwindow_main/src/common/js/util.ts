import CompositionApi, { reactive } from '@vue/composition-api'
import axios, { AxiosResponse } from 'axios'
import Vue from 'vue'
import { Android } from './const'
Vue.use(CompositionApi)

export const popState = reactive({
  showPop: false,
  text: '',
  type: 'info' as 'info' | 'error' | 'success' | 'warning',
  duration: 800,
  timerId: NaN
})
export function showPop(text: string, type: 'info' | 'error' | 'success' | 'warning' = 'info', duration = 800): void {
  closePop()
  popState.duration = duration
  popState.text = text
  popState.type = type
  popState.showPop = true
  popState.timerId = setTimeout(() => closePop(), duration) as unknown as number
}

function closePop(): void {
  clearTimeout(popState.timerId)
  popState.showPop = false
  popState.text = ''
  popState.duration = 800
  popState.type = 'info'
}

export async function getVersion(): Promise<AxiosResponse<{
  /* eslint-disable  camelcase */
  app_version: string,
  force_update: boolean,
  version_info: string
  /* eslint-enable  camelcase */
}>> {
  return axios.get('https://qingcheng.asia/version.json')
}

export function isMobile(): boolean {
  const agents = navigator.userAgent
  let f = false;
  ['Android', 'iPhone', 'iPad', 'iPod', 'Windows Phone', 'SymbianOS'].forEach(e => {
    if (agents.indexOf(e) > 0) f = true
  })
  return f
}

export function disableScroll(): void {
  const body = document.body
  const html = document.documentElement
  html.style.maxHeight = '90vh'
  body.style.maxHeight = '90vh'
  html.style.overflow = 'hidden'
  body.style.overflow = 'hidden'
}
export function resumeScroll(): void {
  const body = document.body
  const html = document.documentElement
  html.style.maxHeight = ''
  html.style.overflow = ''
  body.style.maxHeight = ''
  body.style.overflow = ''
}

export async function fetch(url: string, options?: RequestInit): Promise<string> {
  if (!Android) return (await window.fetch(url, options)).text()

  return new Promise((resolve, reject) => {
    if (!Android) return
    Android.onNetFailure = (msg: string) => reject(new Error(msg))
    Android.onNetResponse = (res: string) => resolve(res)

    Android.fetch(url, JSON.stringify(options))
  })
}

export function copy(value:string, success?:() => void) {
  new Promise((resolve, reject) => {
    if (Android) {
      Android.writeClipboard(value)
      resolve(true)
    } else {
      navigator.clipboard.writeText(value).catch(() => {
        showPop('复制失败', 'error')
        reject(new Error())
      }).then(() => resolve(true))
    }
  }).then(() => {
    success && success()
    showPop('复制成功', 'success')
  })
}
