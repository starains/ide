<template>
  <div class="coos-jumbotron">
    <h1 class="color-lime pdb-20">Team IDE 团队在线开发平台</h1>
    <p>Team IDE 是团队在线项目开发平台，项目成员无需在自己电脑安装开发环境，直接在Team IDE上进行源码编辑，使用Git管理源码版本，在线运行测试，部署项目等</p>
    <p>Team IDE 提供App Designer插件，在线可视化编辑Database，Table，Dao，Server，Bean，Controller等</p>
    <p>在线设计的App模型可以一键生成Java源码，服务端生成Spring Boot工程源码，如果有页面模型，则生成静态html源码</p>
    <p>页面可视化页面设计器，在线编辑页面，生成可以生成Vue源码</p>
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
.coos-jumbotron > * {
  margin: 0px;
  padding: 0px;
}
</style>
