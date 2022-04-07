import { Android, INDEX_URL } from '@/common/js/const'
import { ref, Ref } from '@vue/composition-api'
import { VNode } from 'vue'
// @ts-ignore
import Genshin from '~icons/arcticons/genshin-impact'

export type MayBeRef<T extends Record<any, any>> = {
  [K in keyof T]: Ref<T[K]> | T[K]
}

export interface FeatureProps {
  showRun: boolean, isRunning: boolean, name: string, prependIcon: string | VNode, showNav: boolean
}

export interface Features extends MayBeRef<FeatureProps> {
  navClick(): void | string, runClick(): void
}

export const isNoticeRunning = ref(false)
export const isGenshinRunning = ref(false)
export const isSnowRunning = ref(false)
export const NOTICE_SERVICE_FIRST = 'notice_service_first'
// export const GENSHIN_SERVICE_FIRST = 'genshin_service_first'

export const features: Partial<Features>[] = [
  {
    name: '日程表',
    prependIcon: 'fal fa-calendar-week',
    showRun: true,
    isRunning: isNoticeRunning,
    runClick() {
      isNoticeRunning.value = !isNoticeRunning.value
      if (!Android) return
      if (isNoticeRunning.value) Android.startNoticeService()
      else Android.stopNoticeService()
      localStorage.setItem(NOTICE_SERVICE_FIRST, '')
    },
    navClick() {
      if (Android) Android.redirectToCalendar()
      else window.location.href = INDEX_URL + '/calendar//'
    }
  },
  { name: '音乐谱', prependIcon: 'fal fa-music', navClick: () => '/music' },
  { name: '计算机会议', prependIcon: 'mdi-text-box-search-outline', navClick: () => '/paper' },
  {
    name: '派蒙',
    showRun: true,
    showNav: false,
    prependIcon: Genshin,
    isRunning: isGenshinRunning,
    runClick() {
      isGenshinRunning.value = !isGenshinRunning.value
      if (!Android) return
      if (isGenshinRunning.value) Android.showGenshin()
      else Android.closeGenshin()
      // localStorage.setItem(GENSHIN_SERVICE_FIRST, '')
    }
  },
  {
    name: '雪花',
    showRun: true,
    showNav: false,
    prependIcon: 'fal fa-snowflake',
    isRunning: isSnowRunning,
    runClick() {
      isSnowRunning.value = !isSnowRunning.value
      if (!Android) return
      if (isSnowRunning.value) Android.showSnow()
      else Android.closeSnow()
    }
  }
]
