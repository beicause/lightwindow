import { getVersion } from '../src/common/js/util'
import { formatScore, sectionBeat } from '../src/music/components/scoreUtil'
import { conferences, getConfYear, getPaperByYear } from '../src/paper/paperSrc'
test('get version', async () => {
  const res = await getVersion()
  console.log(res.data)
  expect(res.data).toBeTruthy()
})

test('music score section beat count', () => {
  expect(sectionBeat('_1_21~~')).toBe(4)
})

test('format music score', () => {
  expect(formatScore('//111~~_1_2')).toBe('0000/111~/~_1_200')
})

test('aaa', async () => {
  await getConfYear(conferences[0].conf[0].url)
})
