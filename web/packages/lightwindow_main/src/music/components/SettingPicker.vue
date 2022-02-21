<template>
  <div>
    <div class="mask" v-show="mask" @click="close"></div>
    <transition name="drawer-slide" @after-leave="mask=false">
      <div class="drawer" v-if="drawer">
        <slot></slot>
      </div>
    </transition>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'

export default Vue.extend({
  name: 'SettingPicker',
  data () {
    return {
      mask: false,
      drawer: false
    }
  },
  methods: {
    open () {
      this.mask = true
      this.drawer = true
    },
    close () {
      this.drawer = false
    }
  }
})
</script>

<style scoped>
.mask {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  z-index: 7;
  opacity: 0.46;
  background-color: rgb(33, 33, 33);
  border-color: rgb(33, 33, 33);
}

.drawer {
  z-index: 7;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: white;
}

.drawer-slide-enter, .drawer-slide-leave-to {
  transform: translateY(100%);

}

.drawer-slide-enter-active {
  transition: all .2s;
}

.drawer-slide-leave-active {
  transition: all .1s
}
</style>
