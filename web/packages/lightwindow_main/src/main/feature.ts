import { Android, INDEX_URL } from '@/common/js/const'
import { ref, Ref } from '@vue/composition-api'

export type MayBeRef<T extends Record<any, any>> = {
  [K in keyof T]: Ref<T[K]> | T[K]
}

export interface FeatureProps {
  showRun: boolean, isRunning: boolean, name: string, prependIcon: string
}

export const defaultProps = {
  isRunning: false, name: '', showRun: false, prependIcon: ''
}
export interface Features extends MayBeRef<FeatureProps> {
  navClick(): void | string, runClick(): void
}

export const isNoticeRunning = ref(false)

export const features: Partial<Features>[] = [
  {
    name: '日程表',
    prependIcon: 'fal fa-calendar-week',
    showRun: !!Android,
    isRunning: isNoticeRunning,
    runClick() {
      isNoticeRunning.value = !isNoticeRunning.value
      if (Android) {
        if (isNoticeRunning.value) {
          Android.startNoticeService()
          localStorage.setItem('notice_server', '1')
        } else {
          Android.stopNoticeService()
          localStorage.setItem('notice_server', '0')
        }
      }
    },
    navClick() {
      if (Android) {
        Android.redirectToCalendar()
      } else {
        window.location.href = INDEX_URL + '/calendar//'
      }
    }
  },
  { name: '音乐谱', prependIcon: 'fal fa-music', navClick: () => '/music' },
  { name: '计算机顶会', prependIcon: 'mdi-text-box-search-outline', navClick: () => '/paper' }
]
