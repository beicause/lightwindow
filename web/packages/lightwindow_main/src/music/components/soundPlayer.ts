import SoundFont from 'soundfont-player'
import { showPop } from '@/common/js/util'
import { Ref, ref } from '@vue/runtime-dom'

export const player = ref({}) as Ref<SoundFont.Player>
export let audio = null as AudioContext|null

export function loadPlayer () {
  audio = new AudioContext()
  SoundFont.instrument(audio, './acoustic_grand_piano.js' as any)
    .then(p => {
      player.value = p
    }).catch((err) => {
      console.log(err)
    }).finally(() => {
      if (!player.value.play) showPop('load fail, please try refreshing', 'error', 2000)
    })
}
