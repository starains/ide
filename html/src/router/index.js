import Vue from 'vue'
import Router from 'vue-router'

import Path from '@/views/Path'

const originalPush = Router.prototype.push
Router.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

Vue.use(Router)

export default new Router({
  routes: [{ path: '/', component: Path }, { path: '/:model', component: Path }, { path: '/:model/:page', component: Path }]
})
