<template>
  <div class="coos-jumbotron">
    <h1 class="color-lime pdb-20">TeamIDE 团队在线开发平台</h1>
    <p>开发者可以在线建库、建表、可视化编辑页面、编辑服务、在线预览、导出工程的</p>
    <p>TeamIDE开发平台，致力于项目在线开发，可视化开发，最大程度减少开发者编码，提高应用开发效率</p>
    <p v-if="!source.isLogin">
      现在
      <a @click="toLogin()" class="coos-link color-green pdlr-10">登录</a>
      开发者平台，在线开发您的应用吧！
    </p>
    <p v-if="source.isLogin">
      <template v-if="source.LOGIN_USER.space == null">暂无仓库数据，请联系管理员。</template>
      <template v-else>
        您可以点击
        <a
          v-bind:href="app.getSpaceUrl(source.LOGIN_USER.space)"
          class="coos-link color-green pdlr-10"
        >我的仓库</a>管理您的项目。
      </template>
    </p>
  </div>
</template>

<script>
export default {
  name: "IndexIndex",
  data() {
    return { source: source };
  },
  methods: {
    toLogin() {
      source.toLogin();
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.coos-jumbotron {
  color: white;
  position: relative;
  text-align: center;
  padding: 90px 30px 130px;
  background: #008992;
  background: -prefix-linear-zgradient(top, #008992, white);
  background: linear-gradient(to bottom, #008992, white);
  background: -prefix-linear-zgradient(top, #008992, white);
  line-height: 30px;
  font-size: 19px;
}
</style>
