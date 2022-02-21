<script lang="ts">
import Vue from 'vue'
import store from './store';

export default Vue.extend({
  mpType: 'app',
  onLaunch: function () {
    store.commit('initAll');
    uni.preloadPage({url: "/pages/day/day"})
    uni.preloadPage({url: "/pages/week/week"})
    uni.preloadPage({url: "/pages/mon/mon"})
  }
})
</script>

<style>
/*每个页面公共css */

.app-container {
  padding: 0 5px;
}

.build-margin {
  margin-top: 10px;
}

.cld-com-height {
  height: 507px;
}
</style>
