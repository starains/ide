<template>
  <div class>
    <template v-if="$route.params.model == null || $route.params.model == ''">
      <Index></Index>
    </template>
    <template v-else-if="$route.params.model == 'space'">
      <template v-if="$route.params.page == 'create'">
        <SpaceCreate></SpaceCreate>
      </template>
      <template v-else-if="$route.params.page == 'event'">
        <SpaceEventIndex></SpaceEventIndex>
      </template>
      <template v-else-if="$route.params.page == 'team'">
        <SpaceTeamIndex></SpaceTeamIndex>
      </template>
      <template v-else-if="$route.params.page == 'teamForm'">
        <SpaceTeamForm></SpaceTeamForm>
      </template>
      <template v-else-if="$route.params.page == 'option'">
        <SpaceOptionIndex></SpaceOptionIndex>
      </template>
      <template v-else-if="$route.params.page == 'join'">
        <SpaceJoinIndex></SpaceJoinIndex>
      </template>
      <template v-else-if="$route.params.page == 'star'">
        <SpaceStarIndex></SpaceStarIndex>
      </template>
      <template v-else>
        <Error404></Error404>
      </template>
    </template>
    <template v-else-if="$route.params.model == 'user'">
      <template v-if="!source.isLogin">
        <Error403></Error403>
      </template>
      <template v-else-if="$route.params.page == 'center'">
        <UserCenter></UserCenter>
      </template>
      <template v-else>
        <Error404></Error404>
      </template>
    </template>
    <template v-else-if="$route.params.model == 'manage'">
      <template v-if="!source.isManager">
        <Error403></Error403>
      </template>
      <template v-else-if="$route.params.page == 'configure'">
        <ManageConfigureIndex></ManageConfigureIndex>
      </template>
      <template v-else-if="$route.params.page == 'environment'">
        <ManageEnvironmentIndex></ManageEnvironmentIndex>
      </template>
      <template v-else-if="$route.params.page == 'remote'">
        <ManageRemoteIndex></ManageRemoteIndex>
      </template>
      <template v-else-if="$route.params.page == 'plugin'">
        <ManagePluginIndex></ManagePluginIndex>
      </template>
      <template v-else-if="$route.params.page == 'user'">
        <ManageUserIndex></ManageUserIndex>
      </template>
      <template v-else-if="$route.params.page == 'nginx'">
        <ManageNginxConfigIndex></ManageNginxConfigIndex>
      </template>
      <template v-else>
        <Error404></Error404>
      </template>
    </template>
    <template v-else>
      <Error404></Error404>
    </template>
  </div>
</template>

<script>
import Index from "@/views/Index";
import Error404 from "@/views/error/404";
import Error403 from "@/views/error/403";

import SpaceCreate from "@/views/space/Create";
import SpaceEventIndex from "@/views/space/event/Index";
import SpaceTeamIndex from "@/views/space/team/Index";
import SpaceTeamForm from "@/views/space/team/Form";
import SpaceOptionIndex from "@/views/space/option/Index";
import SpaceJoinIndex from "@/views/space/join/Index";
import SpaceStarIndex from "@/views/space/star/Index";

import ManageConfigureIndex from "@/views/manage/configure/Index";
import ManageEnvironmentIndex from "@/views/manage/environment/Index";
import ManageRemoteIndex from "@/views/manage/remote/Index";
import ManagePluginIndex from "@/views/manage/plugin/Index";
import ManageUserIndex from "@/views/manage/user/Index";
import ManageNginxConfigIndex from "@/views/manage/nginx/Index";

import UserCenter from "@/views/user/Center";
export default {
  name: "PathPage",
  components: {
    Index,
    Error404,
    Error403,
    SpaceCreate,
    SpaceEventIndex,
    SpaceTeamIndex,
    SpaceTeamForm,
    SpaceOptionIndex,
    SpaceJoinIndex,
    SpaceStarIndex,
    ManageConfigureIndex,
    ManageEnvironmentIndex,
    ManageRemoteIndex,
    ManagePluginIndex,
    ManageUserIndex,
    ManageNginxConfigIndex,
    UserCenter
  },
  data() {
    return { source: source };
  },
  mounted() {}
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
