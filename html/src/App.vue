
<template>
  <div class="app">
    <Header></Header>
    <template v-if="source.installed">
      <template v-if="source.LOGIN_USER != null">
        <template v-if="source.space == null">
          <template v-if="source.SPACE_TYPE == null">
            <router-view></router-view>
          </template>
          <template v-else>
            <template v-if="source.space != null">
              <Toolbar></Toolbar>
            </template>
            <router-view></router-view>
          </template>
        </template>
        <template v-else>
          <template v-if="source.space != null">
            <Toolbar></Toolbar>
          </template>
          <router-view></router-view>
          <template v-if="source.SPACE_TYPE =='REPOSITORYS'">
            <div class="app-repository" :class="{'show' : $route.path == '/'}">
              <Rpository ref="repository" :repository="source.repository"></Rpository>
            </div>
          </template>
        </template>
      </template>
      <template v-else>
        <template v-if="source.toRegister">
          <Register></Register>
        </template>
        <template v-else>
          <Login></Login>
        </template>
      </template>
    </template>
    <template v-else>
      <Install></Install>
    </template>
    <PreferenceForm ref="preference-form"></PreferenceForm>
  </div>
</template>
<script>
import Header from "@/views/components/Header";
import Login from "@/views/components/Login";
import Register from "@/views/components/Register";
import Install from "@/views/components/Install";
import Toolbar from "@/views/components/Toolbar";
import PreferenceForm from "@/views/components/PreferenceForm";
import Rpository from "@/views/repository/Index";

import tool from "@/common/js";

export default {
  name: "App",
  components: {
    Header,
    Toolbar,
    Login,
    Register,
    Install,
    Rpository,
    PreferenceForm
  },
  data() {
    return { source: source };
  },
  methods: {
    handleOpen() {},
    handleClose() {},
    hasAction(path) {
      if (source.isManager) {
        return true;
      }
      let actions = source.actions || [];
      actions.forEach(action => {
        if (action.url && action.url.startsWith(path)) {
          return true;
        }
      });
      return false;
    }
  },
  mounted() {
    source.preferenceForm = this.$refs["preference-form"];
    $(".app").css("min-height", $(window).height());
    $(window).resize(function() {
      $(".app").css("min-height", $(window).height());
    });
  }
};
</script>

<style src="@/App.css">
</style>
