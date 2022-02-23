
export interface Android {
  exception: (value: string) => void,
  isNoticeRunning: () => boolean,
  startNoticeService: () => void,
  stopNoticeService: () => void,
  close: () => void,
  showZoom: () => void,
  getClipboardText: () => string,
  getAppVersion: () => string,
  showVersionUpdate: () => void,
  redirectToMain: () => void,
  redirectToCalendar: () => void,
  getPolicy: () => string,
  setPolicy: (value: string) => void
}
// export interface AppVersionInfo {
//   /* eslint-disable camelcase */
//   is_app_update: boolean,
//   is_web_update: boolean,
//   local_web_version: string,
//   local_app_version: string,
//   web_version: string,
//   app_version: string,
//   force_update: boolean,
//   version_info: string
//   /* eslint-enable camelcase */
// }
// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface AndroidBase{
}

export type IsEqual<X, Y> = [X] extends [Y]?[Y] extends [X]? true:false:false
