
<template>
  <div class="app">
    <Header></Header>
    <template v-if="source.installed">
      <template v-if="$route.path == '/doc' || $route.path.startsWith('/doc/')">
        <Doc></Doc>
      </template>
      <template v-else-if="source.space == null">
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
          <template v-if="source.NOT_FOUND_REPOSITORY_BRANCH == null">
            <Rpository ref="repository" :repository="source.repository"></Rpository>
          </template>
        </template>
      </template>
    </template>
    <template v-else>
      <Install></Install>
    </template>
    <PreferenceForm ref="preference-form"></PreferenceForm>
    <Login></Login>
    <Register></Register>
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
import IndexIndex from "@/views/index/IndexIndex";
import Doc from "@/views/Doc";

export default {
  name: "App",
  components: {
    Header,
    Toolbar,
    Login,
    Register,
    Install,
    Rpository,
    PreferenceForm,
    IndexIndex,
    Doc
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
