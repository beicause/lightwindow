/* eslint-disable @typescript-eslint/no-empty-function */

export const POLICY_VERSION = '1'
export const GITHUB_URL = 'https://github.com/beicause/lightwindow'
export const EMAIL = '1494181792@qq.com'
export const INDEX_URL = window.location.protocol + '//' + window.location.host
// mockAndroid()
export const Android = window.Android

// export function mockAndroid ():void {
//   let isRunning = false
//   window.Android = {
//     exception: (value: string) => {},
//     isNoticeRunning: () => isRunning,
//     startNoticeService: () => { isRunning = true },
//     stopNoticeService: () => { isRunning = false },
//     close: () => {},
//     showZoom: () => {},
//     getClipboardText: () => 'string',
//     getAppVersion: () => 'string',
//     showVersionUpdate: () => {},
//     redirectToMain: () => {},
//     redirectToCalendar: () => {},
//     getPolicy: () => POLICY_VERSION,
//     setPolicy: (value: string) => {}
//   }
//   console.log(window.Android)
// }
