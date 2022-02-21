<template>
  <view class="container">
    <uni-tag @click="btnMore" text="更多" inverted type="primary"></uni-tag>
    <view class="container-btn-build">
      <uni-transition :mode-class="['fade' ,'slide-right']" :show="isBuildMode">
        <uni-tag @click="cancel" type="primary" text="取消" :inverted="true" style="margin-right: 15px;">
        </uni-tag>
      </uni-transition>
      <uni-tag @click="build" :text="tagText" type="primary" :inverted="!isBuildMode"></uni-tag>
    </view>
    <!--<editor-fold desc="这是更多菜单">-->
    <uni-popup ref="popMore">
      <uni-transition :show="true" :mode-class="['fade','slide-left']">
        <view class="container-settings">
          <view class="settings-item" @click="openSetSensor">
            <text class="settings-item-text">设置重力打开</text>
            <uni-icons type="arrowright" color="#007aff"></uni-icons>
          </view>
          <view class="settings-item" @click="eduLogin">
            <text class="settings-item-text">导入教务课表</text>
            <uni-icons type="arrowright" color="#007aff"></uni-icons>
          </view>
          <view class="settings-item" @click="setAlarm">
            <text class="settings-item-text">导出闹钟提醒</text>
            <uni-icons type="arrowright" color="#007aff"></uni-icons>
          </view>
          <view class="settings-item" @click="userData">
            <text class="settings-item-text">本地数据</text>
            <uni-icons type="arrowright" color="#007aff"></uni-icons>
          </view>

        </view>
      </uni-transition>
    </uni-popup>
    <!--</editor-fold>-->
    <uni-popup ref="popSetSensor">
      <view class="content">
        <text style="color:#007aff;margin-bottom: 12px">重力打开</text>
        <text style="text-indent: 2em;font-size: 14px">开启后倒置手机即可随时打开日程表，详情请查看说明</text>
        <view @click="setSensor" style="display: flex;align-items: center; margin: 4px auto">
          <text v-if="enableSensor" style="color: #2196F3;font-size: 24px" class="fas fa-spinner fa-spin"></text>
          <svg v-else style="width: 24px;height: 24px">
            <path :d="mdiPlaySpeed" fill="#F44336"/>
          </svg>
        </view>
      </view>
    </uni-popup>
    <uni-popup ref="popEduLogin">
      <the-edu-login></the-edu-login>
    </uni-popup>
    <uni-popup ref="popUserData">
      <the-user-data></the-user-data>
    </uni-popup>
  </view>
</template>

<script lang="ts">
import TheUserData from "@/components/the-build-button/TheUserData.vue";
import TheEduLogin from "@/components/the-build-button/TheEduLogin.vue";
import Vue from 'vue'
import {showDialog} from "@/common/util";
import {Android, androidSetAlarm} from "@/common/android";
import {mdiPlaySpeed} from '@mdi/js'

/**
 * *页面直接组件-底部按钮组
 * @event build-click [编辑|保存]按钮点击事件，e=isBuildMode:boolean(点击后，是否处于编辑模式)
 * @event cancel-click 取消按钮点击事件
 */

export default Vue.extend({
  name: "TheBuildButton",
  components: {
    TheUserData,
    TheEduLogin
  },
  data() {
    return {
      mdiPlaySpeed,
      isBuildMode: false,
      tagText: '编辑',
      enableSensor:false,
    };
  },
  methods: {
    build(): void {
      this.isBuildMode = !this.isBuildMode
      this.tagText = this.isBuildMode ? '保存' : '编辑'
      this.$emit('build-click', this.isBuildMode)
    },
    cancel(): void {
      this.isBuildMode = !this.isBuildMode
      this.tagText = '编辑'
      this.$emit('cancel-click')
    },
    /**
     * 打开更多菜单
     * */
    btnMore(): void {
      (this.$refs.popMore as any).open()
    },
    openSetSensor(){
      if (Android)this.enableSensor=Android.getEnableSensor()==='true';
      (this.$refs.popMore as any).close();
      (this.$refs.popSetSensor as any).open()
    },
    setSensor(){
      this.enableSensor=!this.enableSensor
      Android?.setEnableSensor(''+this.enableSensor)
    },
    /**
     * 打开导出系统闹钟界面
     * */
    setAlarm() {
      showDialog({
        msg: '确认将已设置的闹钟提醒加入系统闹钟吗？\n已加入的闹钟不会撤销，如需取消闹钟，请手动前往设置。',
        type: 'info',
        confirm: () => androidSetAlarm()
      });
      (this.$refs.popMore as any).close();
    },
    /**
     * 打开导入课表登陆界面
     * */
    eduLogin(): void {
      (this.$refs.popEduLogin as any).open() ;
      (this.$refs.popMore as any).close();
    },
    /**
     * 打开数据界面
     * */
    userData() {
      (this.$refs.popUserData as any).open();
      (this.$refs.popMore as any).close()
    }
  }
})
</script>

<style scoped>
.container {
  height: 32px;
}

.container-btn-build {
  float: right;
  display: flex;
}
.content{
  background-color: white;
  width: 300px;
  display: flex;
  justify-content: center;
  flex-direction: column;
  border-radius: 5px;
  padding: 10px;
}
.container-settings {
  display: flex;
  flex-direction: column;
}

.settings-item {
  display: flex;
  align-items: center;
  padding: 5px;
  margin: 2px;
  background-color: #FFFFFF;
  border: #007AFF solid 1px;
  border-radius: 5px;
  color: #007AFF;
  width: 300px;
  height: 30px;
}

.settings-item-text {
  flex: 1;
}
</style>
