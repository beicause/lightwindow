<template>
  <view>
    <uni-calendar :showMonth="false" :date="activeDay" :selected="selected" @change="select"></uni-calendar>
    <uni-transition :show="editable" :mode-class="['fade','slide-left']">
      <view class="mark-input-view">
        <text>标记</text>
        <input v-model="inputText"/>
      </view>
    </uni-transition>
    <event-schedule-vertical :day="activeDay"></event-schedule-vertical>
  </view>
</template>
<script lang="ts">
import Vue from 'vue'
import store from "@/store";
import EventScheduleVertical from "@/components/event-components/EventScheduleVertical.vue";
import {Mark, marksMapToArray} from "@/common/data";

/**
 * 页面直接组件-日程-月
 *
 * @description 这个组件仅可编辑每天的标记Mark
 * @property {Boolean} editable = [true | false] 是否开启编辑模式
 * @see EventScheduleVertical
 */

export default Vue.extend({
  components: {EventScheduleVertical},
  name: "CalendarMonth",
  props: {
    editable: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      isBuildMode: false,
    };
  },
  computed: {
    selected: {
      get(): Mark[] {
        return marksMapToArray(store.state.marks)
      }
    },
    inputText: {
      get(): string {
        const text=store.state.marks.get(this.activeDay)
        return text?text:''
      },
      set(v:string){
        if (v!=='') store.commit('addMarks',[{date:this.activeDay,info:v}])
        else store.commit('removeMarks',[{date:this.activeDay,info:''}])
      }
    },
    activeDay(): string {
      return store.state.activeDay
    }
  },
  methods: {
    select(e: any) {
      store.commit('setActiveDay', e.fulldate as string)
    }
  }
})
</script>

<style scoped>
.mark-input-view {
  position: absolute;
  height: 50px;
  margin-top: -50px;
  color: #007AFF;
  font-size: 14px;
  display: flex;
  align-items: center;
  background-color: white;
}

input {
  margin-left: 10px;
  border: #007AFF dotted 1px;
  width: 42px;
}
</style>
