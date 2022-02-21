<template>
  <view>
    <view class="container-band">
      <text @click="toMain" class="fal fa-arrow-square-left"
            style="color: #2196F3;font-size: 20px;position: absolute;left: 16px"></text>

      <view style="color:#2196F3;height: 20px;">
        <text class="fal fa-calendar-week"></text>
        <text>日程表</text>
      </view>

      <text @click="closeClick" class="fal fa-times-circle"
            style="color: #2196F3;font-size: 20px;position: absolute;right: 16px"></text>
    </view>
    <uni-segmented-control class="segmented" :current="current" :values="['日','周','月']" styleType="button"
                           @clickItem="onClickItem">
    </uni-segmented-control>
  </view>
</template>

<script lang="ts">
import Vue from 'vue'
import { Android } from '@/common/android'

/**
 * *页面直接组件-顶部导航栏
 * @event nav 分段器点击事件，e:{from:number,to:number}
 */

export default Vue.extend({
  name: "TheNavBar",
  components: {
  },
  data() {
    return {
      current: 0,
      zoom: true,
      Android
    }
  },
  methods: {
    closeClick(){
      if (Android)Android.close()
      else window.parent.postMessage('close','*')
    },
    toMain() {
      if (Android) {
        Android.redirectToMain()
      } else window.location.href = "https://qingcheng.asia/main"
    },
    onClickItem(e: any) {
      switch (e.currentIndex) {
        case 0:
          this.$emit('nav', {
            from: this.current,
            to: 0
          })
          break
        case 1:
          this.$emit('nav', {
            from: this.current,
            to: 1
          })
          break
        case 2:
          this.$emit('nav', {
            from: this.current,
            to: 2
          })
          break
      }
      this.current = e.currentIndex
    }
  }
})
</script>

<style scoped>
.segmented {
  height: 25px;
}

.container-band {
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.border {
  border: solid 1px;
}

i {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
