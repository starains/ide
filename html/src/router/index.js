import Vue from 'vue'
import Router from 'vue-router'

import Path from '@/views/Path'

import DocIndex from '@/views/doc/Index'
import DocInstallIndex from '@/views/doc/install/Index'
import DocCodeIndex from '@/views/doc/code/Index'
import DocPluginIndex from '@/views/doc/plugin/Index'
import DocAccountIndex from '@/views/doc/account/Index'
import DocSpaceIndex from '@/views/doc/space/Index'
import DocSpaceCreate from '@/views/doc/space/Create'
import DocSpaceTeam from '@/views/doc/space/Team'
import DocRepositoryIndex from '@/views/doc/repository/Index'
import DocRepositoryCreate from '@/views/doc/repository/Create'
import DocRepositoryGit from '@/views/doc/repository/Git'


const originalPush = Router.prototype.push
Router.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

Vue.use(Router)

export default new Router({
  routes: [
    { path: '/', component: Path },

    { path: '/doc', component: DocIndex },
    { path: '/doc/account', component: DocAccountIndex },
    { path: '/doc/install', component: DocInstallIndex },
    { path: '/doc/code', component: DocCodeIndex },
    { path: '/doc/plugin', component: DocPluginIndex },
    { path: '/doc/space', component: DocSpaceIndex },
    { path: '/doc/space/create', component: DocSpaceCreate },
    { path: '/doc/space/team', component: DocSpaceTeam },
    { path: '/doc/repository', component: DocRepositoryIndex },
    { path: '/doc/repository/create', component: DocRepositoryCreate },
    { path: '/doc/repository/git', component: DocRepositoryGit },

    { path: '/:model', component: Path }, { path: '/:model/:page', component: Path }
  ]
})
