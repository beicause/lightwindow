<template>
	<view>
		<header-date></header-date>
		<view style="display: flex;">
        <time-line class="-time-line"></time-line>
			<event-day-schedule :fontSize="12" :day="day" @plus-click="plusClick" ref="daySchedule"
                      :editable="editable" :areaStyle="{marginLeft:'-20px',flex:1}"></event-day-schedule>
		</view>

	</view>
</template>

<script lang="ts">
import store from '../../store'
import TimeLine from '../event-components/TimeLine.vue'
import HeaderDate from "@/components/calendar/HeaderDate.vue";
import TheBuildButton from "@/components/the-build-button/TheBuildButton.vue";
import Vue from "vue";
import EventDaySchedule from "@/components/event-components/EventDaySchedule.vue";

/**
 * *页面直接组件-日程-天
 *
 * @property {Boolean} editable = [true | false] 是否开启编辑模式
 * @see EventDaySchedule
 */

	export default Vue.extend({
  components: {
    EventDaySchedule,
    TimeLine,
    HeaderDate,
    TheBuildButton,
  },
  name: "CalendarDay",
  props: {
    editable: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    /**
     * 注入EventDaySchedule组件的日期
     * */
			day():string{
				return store.state.activeDay
			}
		},
		methods: {
			plusClick():void{
				(this.$refs.daySchedule as any).add()
			}
		},
	})
</script>

<style scoped>
	.-time-line {
    margin: 22px 0 22px 8px;
  }
</style>
