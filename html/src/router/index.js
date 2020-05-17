import Vue from 'vue'
import Router from 'vue-router'

import Path from '@/views/Path'

import Doc from '@/views/doc/Index'
import DocInstall from '@/views/doc/install/Index'
import DocCode from '@/views/doc/code/Index'
import DocPlugin from '@/views/doc/plugin/Index'
import DocAccount from '@/views/doc/account/Index'
import DocSpace from '@/views/doc/space/Index'
import DocSpaceCreate from '@/views/doc/space/Create'
import DocSpaceTeam from '@/views/doc/space/Team'
import DocRepository from '@/views/doc/repository/Index'
import DocRepositoryCreate from '@/views/doc/repository/Create'
import DocRepositoryGit from '@/views/doc/repository/Git'
import DocRepositoryProject from '@/views/doc/repository/Project'
import DocRepositoryProjectStarter from '@/views/doc/repository/ProjectStarter'


import DocApp from '@/views/doc/app/Index'
import DocAppApp from '@/views/doc/app/App'
import DocAppJDBC from '@/views/doc/app/JDBC'
import DocAppDatabase from '@/views/doc/app/Database'
import DocAppTable from '@/views/doc/app/Table'
import DocAppDao from '@/views/doc/app/Dao'
import DocAppService from '@/views/doc/app/Service'
import DocAppControl from '@/views/doc/app/Control'
import DocAppBean from '@/views/doc/app/Bean'
import DocAppDictionary from '@/views/doc/app/Dictionary'
import DocAppJava from '@/views/doc/app/Java'

import DocAppHTML from '@/views/doc/app/html/Index'
import DocAppHTMLFrame from '@/views/doc/app/html/Frame'
import DocAppHTMLPage from '@/views/doc/app/html/Page'
import DocAppHTMLVue from '@/views/doc/app/html/Vue'


const originalPush = Router.prototype.push
Router.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

Vue.use(Router)

export default new Router({
  routes: [
    { path: '/', component: Path },

    { path: '/doc', component: Doc },
    { path: '/doc/account', component: DocAccount },
    { path: '/doc/install', component: DocInstall },
    { path: '/doc/code', component: DocCode },
    { path: '/doc/plugin', component: DocPlugin },
    { path: '/doc/space', component: DocSpace },
    { path: '/doc/space/create', component: DocSpaceCreate },
    { path: '/doc/space/team', component: DocSpaceTeam },
    { path: '/doc/repository', component: DocRepository },
    { path: '/doc/repository/create', component: DocRepositoryCreate },
    { path: '/doc/repository/git', component: DocRepositoryGit },
    { path: '/doc/repository/project', component: DocRepositoryProject },
    { path: '/doc/repository/project/starter', component: DocRepositoryProjectStarter },

    { path: '/doc/app', component: DocApp },
    { path: '/doc/app/app', component: DocAppApp },
    { path: '/doc/app/jdbc', component: DocAppJDBC },
    { path: '/doc/app/database', component: DocAppDatabase },
    { path: '/doc/app/table', component: DocAppTable },
    { path: '/doc/app/dao', component: DocAppDao },
    { path: '/doc/app/service', component: DocAppService },
    { path: '/doc/app/control', component: DocAppControl },
    { path: '/doc/app/bean', component: DocAppBean },
    { path: '/doc/app/dictionary', component: DocAppDictionary },
    { path: '/doc/app/java', component: DocAppJava },

    { path: '/doc/app/html', component: DocAppHTML },
    { path: '/doc/app/html/frame', component: DocAppHTMLFrame },
    { path: '/doc/app/html/page', component: DocAppHTMLPage },
    { path: '/doc/app/html/vue', component: DocAppHTMLVue },

    { path: '/:model', component: Path }, { path: '/:model/:page', component: Path }
  ]
})
