declare module 'vue' {
  type UI = typeof import('vuetify/lib/components')
  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface GlobalComponents extends UI {}
}

export {}
