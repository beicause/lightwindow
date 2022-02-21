<script setup lang="ts">
import { defineProps, withDefaults } from '@vue/runtime-dom'
import { defaultProps } from '../feature'

interface P{
   showRun?:boolean, isRunning?:boolean, name:string, prependIcon:string
}

withDefaults(defineProps<P>(), defaultProps)
</script>

<template>
  <div>
    <v-row no-gutters align="center">
      <v-icon dense class="pr-1" color="blue">{{ prependIcon }}</v-icon>
      <div class="blue--text">{{ name }}</div>
      <v-spacer></v-spacer>
      <v-btn class="mr-1" v-if="showRun" icon @click="$emit('run-click')"
             :color="isRunning?'blue':'red'">
        <v-icon dense :color="isRunning?'blue':'red'">{{
            isRunning ? 'fas fa-spinner fa-spin' : 'mdi-play-speed'
          }}
        </v-icon>
      </v-btn>
      <v-btn color="blue" icon @click="$emit('nav-click')">
        <v-icon>fal fa-location-arrow</v-icon>
      </v-btn>
    </v-row>

    <div class="pb-2">
      <v-divider></v-divider>
    </div>
  </div>
</template>
