import { fetch } from '@/common/js/util'

export const baseUrl = 'http://dblp.uni-trier.de/db/conf/'

export interface Conf { name: string, homeUrl: string }
export interface Paper { title: string, url: string }
export interface Year { name: string, year: number, urls:string[] }

const conf = (name: string, fragment?: string) => {
  return { name, homeUrl: baseUrl + (fragment ?? name.toLowerCase()) }
}
export const conferences = [
  { type: '计算机体系结构/并行与分布计算/存储系统', conf: [conf('PPoPP'), conf('FAST'), conf('DAC'), conf('HPCA'), conf('MICRO'), conf('SC'), conf('ASPLOS'), conf('ISCA'), conf('USENIX ATC', 'usenix')] },
  { type: '计算机网络', conf: [conf('SIGCOMM'), conf('MobiCom'), conf('INFOCOM'), conf('NSDI')] },
  { type: '网络与信息安全', conf: [conf('CCS'), conf('EUROCRYPT'), conf('S&P', 'sp'), conf('CRYPTO'), conf('USENIX Security', 'uss')] },
  { type: '软件工程/系统软件/程序设计语言', conf: [conf('PLDI'), conf('POPL'), conf('FSE/ESEC', 'sigsoft'), conf('OOPSLA'), conf('ASE', 'kbse'), conf('ICSE'), conf('ISSTA'), conf('OSDI')] },
  { type: '数据库/数据挖掘/内容检索', conf: [conf('SIGMOD'), conf('SIGKDD', 'kdd'), conf('ICDE'), conf('SIGIR'), conf('VLDB')] },
  { type: '计算机科学理论', conf: [conf('STOC'), conf('SODA'), conf('CAV'), conf('FOCS'), conf('LICS')] },
  { type: '计算机图形学与多媒体', conf: [conf('ACM MM', 'mm'), conf('SIGGRAPH'), conf('VR'), conf('IEEE VIS', 'visualization')] },
  { type: '人工智能', conf: [conf('AAAI'), conf('NeurIPS', 'nips'), conf('ACL'), conf('CVPR'), conf('ICCV'), conf('ICML'), conf('IJCAI')] },
  { type: '人机交互与普适计算', conf: [conf('CSCW'), conf('CHI'), conf('UbiComp', 'huc')] },
  { type: '交叉/综合/新兴', conf: [conf('WWW'), conf('RTSS')] }
] as { type: string, conf: Conf[] }[]

export async function getPaperByYear (urls:string[]): Promise<Paper[]> {
  console.log(JSON.stringify(urls))

  const data = [] as Paper[]
  for (const _url of urls) {
    const html = await fetch(_url)
    const parser = new DOMParser()
    const doc = parser.parseFromString(html, 'text/html')
    const els = doc?.getElementsByClassName('inproceedings')

    if (els) {
      for (let i = 0; i < els.length; i++) {
        const el = els[i]
        const url = el.getElementsByClassName('publ')[0]?.children[0]?.children[0]?.children[0]?.children[0]?.getAttribute('href') ?? ''
        const title = el.getElementsByClassName('title')[0].textContent ?? ''
        data.push({ url, title })
      }
    }
  }
  return data
}

export async function getConfYear (homeUrl: string): Promise<Year[]> {
  const html = await fetch(homeUrl + '/index.html')
  const parser = new DOMParser()
  const headers = parser.parseFromString(html, 'text/html').getElementById('main')?.getElementsByTagName('header')
  const data = [] as Year[]
  if (headers) {
    for (let i = 0; i < headers.length; i++) {
      const el = headers[i]
      const h2 = el.children[0]
      if (el.children.length !== 1 || !h2.id.match(/^\d{4}$/)) continue
      const name = h2.textContent ?? 'null'
      const year = parseInt(h2.id)
      const urls = [] as string[]
      let ul = el.nextElementSibling
      while (ul && ul.tagName !== 'UL') ul = ul.nextElementSibling
      const events = ul?.children
      if (events) {
        for (let j = 0; j < events.length; j++) {
          const e = events[j]
          const url = e.getElementsByClassName('publ')[0].children[0].children[0].children[0].children[0].getAttribute('href')
          url && urls.push(url)
        }
      }
      data.push({ name, year, urls })
    }
  }
  return data
}
