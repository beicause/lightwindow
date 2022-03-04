import { defineConfig } from 'vite'
import { createVuePlugin as Vue2 } from 'vite-plugin-vue2'
import AutoComponent from 'unplugin-vue-components/vite'
import { VuetifyResolver } from 'unplugin-vue-components/resolvers'
import ScriptSetup from 'unplugin-vue2-script-setup/vite'
import WindiCss from 'vite-plugin-windicss'
import Icons from 'unplugin-icons/vite'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    Vue2(),
    WindiCss(),
    Icons(),
    AutoComponent({
      resolvers: [VuetifyResolver()],
      dts: false
    }),
    ScriptSetup()
  ],
  resolve: {
    alias: [{
      find: '@', replacement: path.resolve(__dirname, 'src')
    }]
  },
  build: {
    // for webview
    cssTarget: 'chrome61',
    rollupOptions: {
      input: {
        main: path.resolve('index.html'),
        genshin: path.resolve('genshin/index.html')
      }
    }
  }
})
