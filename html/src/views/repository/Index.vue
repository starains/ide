<template>
  <div
    class="repository-box"
    v-loading="repository.loading"
    :element-loading-text="repository.loading_msg"
  >
    <div class="repository-top"></div>
    <div class="repository-left">
      <Project :repository="repository"></Project>
    </div>
    <div class="repository-center">
      <Tabs :repository="repository"></Tabs>
    </div>
    <div class="repository-contextmenu">
      <Contextmenu :contextmenu="repository.contextmenu"></Contextmenu>
    </div>

    <Starter :repository="repository"></Starter>
    <Runner :repository="repository"></Runner>

    <GitCertificate ref="git-certificate"></GitCertificate>
    <GitRemote ref="git-remote"></GitRemote>
    <GitPlus ref="git-plus"></GitPlus>
    <GitPush ref="git-push"></GitPush>
    <GitRevert ref="git-revert"></GitRevert>
    <StarterForm ref="starter-form"></StarterForm>
    <RunnerForm ref="runner-form"></RunnerForm>
    <BranchCreate ref="branch-create"></BranchCreate>
    <AppOptionForm ref="app-option-form"></AppOptionForm>
  </div>
</template>

<script>
import Project from "@/views/repository/Project";
import Tabs from "@/views/repository/Tabs";
import Starter from "@/views/repository/Starter";
import Runner from "@/views/repository/Runner";
import Contextmenu from "@/views/components/Contextmenu";

import GitCertificate from "@/views/repository/components/git/GitCertificate";
import GitRemote from "@/views/repository/components/git/GitRemote";
import GitPlus from "@/views/repository/components/git/GitPlus";
import GitPush from "@/views/repository/components/git/GitPush";
import GitRevert from "@/views/repository/components/git/GitRevert";
import StarterForm from "@/views/repository/components/starter/StarterForm";
import RunnerForm from "@/views/repository/components/runner/RunnerForm";
import BranchCreate from "@/views/repository/components/branch/BranchCreate";
import AppOptionForm from "@/views/repository/components/app/AppOptionForm";

export default {
  props: ["repository"],
  components: {
    Project,
    Tabs,
    Starter,
    Runner,
    Contextmenu,
    GitCertificate,
    GitRemote,
    GitPlus,
    GitPush,
    GitRevert,
    StarterForm,
    RunnerForm,
    BranchCreate,
    AppOptionForm
  },
  data() {
    return { source: source };
  },
  beforeMount() {},
  watch: {},
  methods: {},
  mounted() {
    source.gitRemoteForm = this.$refs["git-remote"];
    source.gitCertificateForm = this.$refs["git-certificate"];
    source.gitPlusForm = this.$refs["git-plus"];
    source.gitPushForm = this.$refs["git-push"];
    source.gitRevertForm = this.$refs["git-revert"];
    source.starterForm = this.$refs["starter-form"];
    source.runnerForm = this.$refs["runner-form"];
    source.branchCreateForm = this.$refs["branch-create"];
    source.appOptionForm = this.$refs["app-option-form"];
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style  >
.repository-box {
  position: relative;
  background-color: #ddd;
  box-shadow: 0px 0px 10px #ddd;
  width: 100%;
  height: 100%;
}
.repository-box .repository-top {
  position: absolute;
  top: 5px;
  left: 0px;
  right: 0px;
  height: 35px;
  background-color: #fff;
  display: none;
}
.repository-box .repository-left {
  position: absolute;
  top: 0px;
  left: 0px;
  bottom: 0px;
  width: 310px;
  background-color: #fff;
  box-shadow: 0px 0px 10px #ddd;
}
.repository-box .repository-left .repository-left-content {
  position: absolute;
  top: 30px;
  left: 0px;
  bottom: 0px;
  right: 0px;
}
.repository-box .repository-center {
  position: absolute;
  top: 0px;
  left: 310px;
  bottom: 0px;
  right: 0px;
  background-color: #fff;
  box-shadow: 0px 0px 10px #ddd;
}
</style>
