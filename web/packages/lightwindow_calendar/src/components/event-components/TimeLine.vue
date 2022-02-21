<template>
	<view>
		<canvas class="canvas" canvas-id="canvas"></canvas>
	</view>
</template>

<script lang="ts">
import Vue from 'vue'

/**
 * 左侧时间线组件，由canvas绘制
 */

	export default Vue.extend({
  name: "TimeLine",
  data() {
    return {
      date: new Date(),
      timer: 0
    };
  },
  mounted() {
    this.draw()
    this.timer = setInterval(() => {
      this.draw()
			}, 3000) as unknown as number
		},
		beforeDestroy() {
			clearInterval(this.timer)
		},
		methods:{
			draw(){
				this.date = new Date()
				const nowSecond = this.date.getHours() * 3600 + this.date.getMinutes() * 60 + this.date
					.getSeconds()
				const percent = nowSecond / (24 * 3600)
				const ctx = uni.createCanvasContext('canvas')
				ctx.moveTo(12, 0)
				ctx.lineTo(12, 400 * percent)
				ctx.setStrokeStyle('black')
				ctx.fillText(this.date.getHours() + ':'
				 + (this.date.getMinutes() / 10 < 1 ? '0' + this.date.getMinutes() :
						this.date.getMinutes()), 0,400 * percent + 10)
				ctx.stroke()
				ctx.beginPath()
				ctx.setStrokeStyle("#007AFF")
				ctx.moveTo(12, 400 * percent + 12)
				ctx.lineTo(12, 400)
				ctx.stroke()
				if (percent < 0.97) ctx.fillText((percent * 100).toFixed(2) + "%", 0, 410)
				ctx.draw()
			}
		}
	})
</script>

<style scoped>
	.canvas {
    height: 412px;
    width: 34px;
  }
</style>
