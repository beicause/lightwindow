<template>
  <view class="app-container">
    <the-nav-bar @nav="nav"></the-nav-bar>
    <!-- <uni-transition v-show="index===0" :show="index===0" :mode-class="mode"> -->
      <view v-if="index===0" class="calendar-com-height">
        <calendar-day :editable="editable" ref="day"></calendar-day>
      </view>
    <!-- </uni-transition> -->
    <!-- <uni-transition v-show="index===1" :show="index===1" :mode-class="mode"> -->
      <view v-if="index===1" class="calendar-com-height">
        <calendar-week :editable="editable" ref="week"></calendar-week>
      </view>
    <!-- </uni-transition> -->
    <!-- <uni-transition v-show="index===2" :show="index===2" :mode-class="mode"> -->
      <view v-if="index===2" class="calendar-com-height">
        <calendar-month :editable="editable" ref="mon"></calendar-month>
      </view>
    <!-- </uni-transition> -->
    <the-build-button class="build-margin" @cancel-click="cancelClick"
                      @build-click="buildClick"></the-build-button>

    <!--<editor-fold desc="全局提示框">-->
    <uni-popup ref="msg" type="message" :mask-click="false">
      <uni-popup-message :type="popType" :message="popMsg" :duration="0"></uni-popup-message>
    </uni-popup>
    <!--</editor-fold>-->

    <!--<editor-fold desc="全局对话框">-->
    <uni-popup ref="dialog" type="dialog" @maskClick="$refs.dialog.close()">
      <uni-popup-dialog @close="$refs.dialog.close()" :content="dialogText" :type="dialogType" @confirm="confirm()">
      </uni-popup-dialog>
    </uni-popup>
    <!--</editor-fold>-->
  </view>
</template>

<script lang="ts">
import Vue from 'vue'
import TheBuildButton from "../../components/the-build-button/TheBuildButton.vue";
import CalendarDay from "../../components/calendar/CalendarDay.vue";
import CalendarWeek from "../../components/calendar/CalendarWeek.vue";
import CalendarMonth from "../../components/calendar/CalendarMonth.vue";
import TheNavBar from "@/components/the-nav-bar/TheNavBar.vue";
import {Event, marksArrayToMap, PopMsg} from "@/common/data";
import { compareEvents, getEventDate, showPopMsg} from "@/common/util";
import store from "@/store";
import {getStorage} from "@/common/cache";
import TheUserDataGraph from "@/components/the-build-button/TheUserDataGraph.vue";

/**
 * 入口页面
 * @description 入口页面，提供全局提示框和对话框
 * @link showPopMsg 显示提示框
 * @link closePopMsg 关闭提示框
 * @link showDialog 显示对话框
 * @link closeDialog 关闭对话框
 * @see TheNavBar
 * @see TheBuildButton
 * @see CalendarDay
 * @see CalendarWeek
 * @see CalendarMonth
 */

export default Vue.extend({
  name: "index",
  components: {
    TheUserDataGraph,
    TheNavBar, CalendarMonth, CalendarWeek, CalendarDay, TheBuildButton
  },
  data() {
    return {
      index: 0,//日程表索引 日0，周1，月2
      // mode: 'fade',//过渡动画模式
      editable: false,//是否开启编辑模式
      popType: '',//下面是提示框和对话框参数
      popMsg: '',//
      dialogType: '',//
      dialogText: '',//
      timerId: 0,
      confirm: () => {
      },//
    }
  },
  //<!--<editor-fold desc="监听全局提示框和对话框事件">-->
  mounted() {
    uni.$on('showPopMsg', (e: PopMsg) => {
      clearTimeout(this.timerId)
      this.timerId=0
      this.showPop(e.msg, e.type)
      if (e.duration) this.timerId= setTimeout(() =>this.closePop(), e.duration) as unknown as number
    })
    uni.$on('closePopMsg', () => {
      clearTimeout(this.timerId)
      this.timerId=0
      this.closePop()
    })
    uni.$on('showDialog', (e: PopMsg) => {
      this.showDialog(e.msg, e.type, e.confirm)
    })
    uni.$on('closeDialog', () => {
      this.closeDialog()
    })
  },
  //<!--</editor-fold>-->

  beforeDestroy() {
    uni.$off('showPopMsg')
    uni.$off('closePopMsg')
    uni.$off('showDialog')
    uni.$off('closeDialog')
  },
  methods: {
    // <!--<editor-fold desc="这些方法提供给uni.$on()监听回调">-->
    showPop(msg: string, type: string): void {
      this.popMsg = msg
      this.popType = type;
      (this.$refs.msg as any).open()
    },
    closePop() {
      (this.$refs.msg as any).close()
    },
    showDialog(msg: string, type: string, confirm: () => void = () => {
      (this.$refs.dialog as any).close()
    }) {
      this.dialogText = msg;
      this.dialogType = type;
      this.confirm = confirm;
      (this.$refs.dialog as any).open()
    },
    closeDialog() {
      (this.$refs.dialog as any).close()
    },
    // <!--</editor-fold>-->

    /**
     * 导航
     * */
    nav(e: { from: number, to: number }): void {
      // uni.$emit('navigate', e)
      // if (e.to > e.from) this.mode = 'slide-right'
      // else this.mode = 'slide-left'
      this.index = e.to
    },

    /**
     * "取消按钮点击事件，从缓存复原数据"
     * */
    cancelClick() {
      this.editable = false;
      store.commit('updateEvents', JSON.parse(getStorage('events')))
      store.commit('updateMarks', marksArrayToMap(JSON.parse(getStorage('marks'))))
    },

    /**
     * [编辑|保存]按钮点击事件，[开启编辑模式|缓存数据]
     * */
    buildClick(editable: boolean) {
      this.editable = editable
      if (!editable) {
        const alarms = store.state.events.filter(e =>
            e.alarm.match(/^\*/) && getEventDate(e).getTime() > new Date().getTime())
        const cAlarms = (JSON.parse(getStorage('events')) as Event[]).filter(e =>
            e.alarm.match(/^\*/) && getEventDate(e).getTime() > new Date().getTime())
        const [a, b] = compareEvents(alarms, cAlarms)

        if (a.length !== 0 || b.length !== 0) showPopMsg({
          msg: '闹钟提醒发生变化，若要生效，请导出闹钟提醒',
          type: 'info',
          duration: 2000
        })
        store.commit('cacheEvents')
      }
    }
  }
})
</script>

<style scoped>
</style>
