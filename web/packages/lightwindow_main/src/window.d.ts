import { Android, AndroidBase } from '@/common/js/types'

declare global {
  interface Window {
    // eslint-disable-next-line camelcase
    aplus_queue?: { action: string|'aplus.sendPV', arguments: unknown[] }[],
    Android?: Android
    showZoom?: () => void
    getVersion?: () => string
  }
}
