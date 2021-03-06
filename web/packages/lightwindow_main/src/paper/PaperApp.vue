<script setup lang="ts">
import { conferences, getConfYear, Paper, Conf, Year, getPaperByYear } from './paperSrc'
import AppBar from '@/common/AppBar.vue'
import { onUnmounted, ref } from '@vue/composition-api'
import { copy, showPop } from '@/common/js/util'

const showYearList = ref(false)
const years = ref([] as Year[])
const papers = ref([] as Paper[])
const disableYear = ref(false)
const disableConf = ref(false)
let reqYear = ''

function confNameClick(conf: Conf) {
  const cache = localStorage.getItem(conf.homeUrl)
  history.pushState(null, '')
  years.value = []
  if (!cache)showPop('正在加载', 'info', 30000)
  disableConf.value = true
  new Promise<Year[]>((resolve, reject) => {
    if (cache) resolve(JSON.parse(cache))
    else getConfYear(conf.homeUrl).then(y => resolve(y))
  }).then(_years => {
    years.value = _years
    showYearList.value = true
    if (!cache) showPop('加载成功', 'success')
    localStorage.setItem(conf.homeUrl, JSON.stringify(_years))
    disableConf.value = false
  })
}
function yearClick(year: Year) {
  if (reqYear === year.name) return
  reqYear = year.name
  papers.value = []
  const cache = localStorage.getItem(JSON.stringify(year.urls))
  if (!cache)showPop('正在加载', 'info', 30000)
  disableYear.value = true
  new Promise<Paper[]>((resolve, reject) => {
    if (cache) resolve(JSON.parse(cache))
    else getPaperByYear(year.urls).then(p => resolve(p))
  }).then(_papers => {
    papers.value = _papers
    if (!cache)showPop('加载成功', 'success')
    localStorage.setItem(JSON.stringify(year.urls), JSON.stringify(_papers))
    disableYear.value = false
  })
}

function copyUrl(p: Paper) {
  copy(p.url)
}

const onPopState = (e: any) => {
  showYearList.value = !showYearList.value
}
window.addEventListener('popstate', onPopState)
onUnmounted(() => {
  window.removeEventListener('popstate', onPopState)
})
</script>
<template>
  <div>
    <AppBar>
      <VIcon dense color="blue">mdi-text-box-search-outline</VIcon>
      <span class="blue--text">计算机顶会</span>
    </AppBar>
    <VMain>
      <VExpansionPanels accordion v-show="!showYearList">
        <VExpansionPanel v-for="category in conferences" :key="category.type">
          <VExpansionPanelHeader class="blue--text">{{ category.type }}</VExpansionPanelHeader>
          <VExpansionPanelContent>
            <VListItemGroup>
              <template v-for="conf in category.conf">
                <VListItem
                  :disabled="disableConf"
                  color="primary"
                  :key="conf.name"
                  @click="confNameClick(conf)"
                >{{ conf.name }}</VListItem>
                <VDivider :key="conf.name + '_'"></VDivider>
              </template>
            </VListItemGroup>
          </VExpansionPanelContent>
        </VExpansionPanel>
      </VExpansionPanels>
      <VExpansionPanels :disabled="disableYear" accordion v-show="showYearList">
        <VExpansionPanel v-for="item in years" :key="item.name">
          <VExpansionPanelHeader @click="yearClick(item)" class="blue--text">{{ item.name }}</VExpansionPanelHeader>
          <VExpansionPanelContent v-if="papers.length > 0">
            <VListItemGroup color="primary">
              <template v-for="p in papers">
                <VListItem :key="p.title" class="px-0" @click="copyUrl(p)">{{ '- ' + p.title }}</VListItem>
                <VDivider :key="p.title + '_'"></VDivider>
              </template>
            </VListItemGroup>
          </VExpansionPanelContent>
        </VExpansionPanel>
      </VExpansionPanels>
    </VMain>
  </div>
</template>
