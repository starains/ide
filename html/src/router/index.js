import Vue from 'vue'
import Router from 'vue-router'

import Index from '@/views/Index'

import EventIndex from '@/views/event/Index'
import TeamIndex from '@/views/team/Index'
import TeamForm from '@/views/team/Form'
import OptionIndex from '@/views/option/Index'
import SpaceCreate from '@/views/space/Create'
import JoinIndex from '@/views/join/Index'
import StarIndex from '@/views/star/Index'

import ConfigureIndex from '@/views/configure/Index'
import EnvironmentIndex from '@/views/environment/Index'
import RemoteIndex from '@/views/remote/Index'



const originalPush = Router.prototype.push
Router.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Index',
      component: Index
    },
    {
      path: '/configure',
      name: 'ConfigureIndex',
      component: ConfigureIndex
    },
    {
      path: '/environment',
      name: 'EnvironmentIndex',
      component: EnvironmentIndex
    },
    {
      path: '/remote',
      name: 'RemoteIndex',
      component: RemoteIndex
    },
    {
      path: '/space/create',
      name: 'SpaceCreate',
      component: SpaceCreate
    },
    {
      path: '/event',
      name: 'EventIndex',
      component: EventIndex
    },
    {
      path: '/team',
      name: 'TeamIndex',
      component: TeamIndex
    },
    {
      path: '/team/form',
      name: 'TeamForm',
      component: TeamForm
    },
    {
      path: '/option',
      name: 'OptionIndex',
      component: OptionIndex
    },
    {
      path: '/join',
      name: 'JoinIndex',
      component: JoinIndex
    },
    {
      path: '/star',
      name: 'StarIndex',
      component: StarIndex
    }
  ]
})
