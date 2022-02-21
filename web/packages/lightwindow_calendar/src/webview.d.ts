import { Android } from '@/common/data'

declare global {
    interface Window {
        Android?: Android
        getVersion?: () => string
        AndroidBase?:{
            clearPageCache:()=>void
        }
    }
}
