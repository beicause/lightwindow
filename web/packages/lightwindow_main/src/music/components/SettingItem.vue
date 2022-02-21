<template>
  <v-row align="center" justify="center" class="py-2">
    <v-btn @click="$emit('down-click')" small color="blue" outlined fab width="16" height="16">-
    </v-btn>
    <div class="blue--text setting-name">
      <slot></slot>
    </div>
    <v-btn @click="$emit('up-click')" small color="blue" outlined fab width="16" height="16">+
    </v-btn>
  </v-row>
</template>

<script lang="ts">
import Vue from 'vue'

export default Vue.extend({
  name: 'SettingItem',
  props: {
    name: {
      type: String,
      default: ''
    }
  }
})
</script>

<style scoped>
.setting-name {
  width: 100px;
  padding-left: 10px;
}
</style>
