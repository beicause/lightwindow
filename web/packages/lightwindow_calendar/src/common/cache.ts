function setStorage(k: 'updateTime' | 'events' | 'marks' | 'eduUserInfo' | 'eduEvents', v: string) {
    localStorage.setItem(k, v)
}

function getStorage(k: 'updateTime' | 'events' | 'marks' | 'eduUserInfo' | 'eduEvents'): string {
    const s = localStorage.getItem(k)
    if (!s) {
        if (k === 'events' || k === 'marks' || k === 'eduEvents') return "[]"
        if (k === 'eduUserInfo') return "{}"
    }
    if (typeof s === 'string') return s
    else return JSON.stringify(s)
}

function removeStorage(ks: ('updateTime' | 'events' | 'marks' | 'eduUserInfo' | 'eduEvents')[]) {
    ks.forEach(k => {
        localStorage.removeItem(k)
    })
}

export {
    setStorage, getStorage, removeStorage
}
