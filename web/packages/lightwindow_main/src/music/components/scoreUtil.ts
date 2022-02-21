
const FIRST_SCORE = 'FIRST_SCORE'
const SECOND_SCORE = 'SECOND_SCORE'
const BEAT_DURATION = 'BEAT_DURATION'
const SECTION_BEATS = 4

function sectionBeat (r: string): number {
  let num = 0
  let isReduce = 0
  for (let i = 0; i < r.length; i++) {
    if (r[i] === '_') {
      isReduce++
    } else if (isReduce > 0) {
      num -= 1 - 1 / Math.pow(2, isReduce)
      isReduce = 0
    }
    if (r[i] === '~') num++
    if (/\d/.test(r[i])) num++
  }
  return num
}

function formatScore (s: string): string {
  if (!s) return ''
  if (s[0] !== '/') s = '/' + s
  if (s[s.length - 1] !== '/') s += '/'
  const sections = s.substring(1, s.length - 1)
    .replaceAll(/[^0-7/_~ud#]/g, '')
    .split('/')
  for (let i = 0; i < sections.length; i++) {
    const beats = sectionBeat(sections[i])
    if (beats === SECTION_BEATS) continue
    if (beats < SECTION_BEATS) {
      sections[i] += '0'
      i--
    }
    if (beats > SECTION_BEATS) {
      for (let j = 1; j <= sections[i].length; j++) {
        const mutableStr = sections[i].substring(0, j)
        if (sectionBeat(mutableStr) === SECTION_BEATS) {
          sections[i] = mutableStr + '/' + sections[i].substring(j)
          break
        }
      }
      sections[i] = formatScore(sections[i])
    }
  }
  return sections.join('/')
}

interface ScoreInfo {
    inputValue: string
    level: 0 | 1 | -1,
    gain: number
  }
export {
  SECTION_BEATS, FIRST_SCORE, SECOND_SCORE, BEAT_DURATION, sectionBeat, formatScore, ScoreInfo
}
