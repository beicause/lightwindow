<script setup lang="ts">
import { ref } from '@vue/composition-api'
import { VBtn } from 'vuetify/lib'

// eslint-disable-next-line no-undef
withDefaults(defineProps<{
  showRun?: boolean, isRunning?: boolean, name?: string, prependIcon?: any, showNav?: boolean
}>(), {
  isRunning: false, name: '', showRun: false, prependIcon: '', showNav: true
})

const navBtn = ref<null | typeof VBtn>(null)

</script>

<template>
  <div>
    <v-row no-gutters align="center" @click="e => navBtn.click(e)">
      <div class="pr-1 w-5 h-5 blue--text">
        <v-icon dense color="blue" v-if="typeof prependIcon === 'string'">{{ prependIcon }}</v-icon>
        <component v-else :is="prependIcon"></component>
      </div>
      <div class="blue--text">{{ name }}</div>
      <v-spacer></v-spacer>
      <v-btn
        class="mr-1"
        v-if="showRun"
        icon
        @click.stop="$emit('run-click')"
        :color="isRunning ? 'blue' : 'red'"
      >
        <v-icon dense :color="isRunning ? 'blue' : 'red'">
          {{
            isRunning ? 'fas fa-spinner fa-spin' : 'mdi-play-speed'
          }}
        </v-icon>
      </v-btn>
      <v-btn v-if="showNav" color="blue" icon ref="navBtn" @click.stop="$emit('nav-click')">
        <v-icon>fal fa-location-arrow</v-icon>
      </v-btn>
    </v-row>

    <div class="pb-2">
      <v-divider></v-divider>
    </div>
  </div>
</template>
