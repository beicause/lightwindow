<script setup lang="ts">
import { features, Features, isNoticeRunning } from '../feature'
import FeatureItem from '../components/FeatureItem.vue'
import { Android } from '@/common/js/const'
import { ref } from '@vue/composition-api'

const refFeatures = ref(features)

if (!localStorage.getItem('notice_server')) {
  refFeatures.value[0].runClick?.call(undefined)
}

isNoticeRunning.value = !!Android?.isNoticeRunning()
</script>

<template>
  <v-container>
    <FeatureItem
      :key="item.name"
      v-for="item in refFeatures"
      :name="item.name"
      :showRun="item.showRun"
      @nav-click="onNavClick(item)"
      @run-click="item.runClick ? item.runClick() : undefined"
      :isRunning="item.isRunning ? item.isRunning.value : undefined"
      :prependIcon="item.prependIcon"
    ></FeatureItem>
    <!-- 上面我手动对isRunning 解包ref,否则出错 -->
  </v-container>
</template>

<script lang="ts">

export default {

  methods: {
    onNavClick(item: Partial<Features>) {
      const r = item.navClick ? item.navClick() : undefined
      if (r) { (this as any).$router.push(r) }
    }
  }
}

</script>
