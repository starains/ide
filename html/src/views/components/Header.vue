<template>
  <div class="app-header">
    <div class="app-header-left">
      <ul class="app-nav bg-teal app-header-nav">
        <li>
          <a :href="_ROOT_URL">
            <img class :src="_SERVER_URL+'/resources/images/coos-30.jpg'" />
          </a>
        </li>
        <li>
          <a :href="_ROOT_URL">
            <h2 class>TeamIDE</h2>
          </a>
        </li>
        <li>
          <a>
            <input class="coos-search-input coos-bd-no" placeholder="搜索" />
          </a>
        </li>
      </ul>
    </div>
    <div class="app-header-center">
      <ul class="app-nav app-nav-ul app-header-nav">
        <li>
          <a target="_blank" :href="_SERVER_URL + '/resources/font/demo_index.html'">IconFont</a>
        </li>
        <li class="app-nav-left" v-if="source.isManager">
          <a>系统管理</a>
          <ul class="app-nav-ul">
            <li class>
              <router-link to="/manage/configure">系统设置</router-link>
            </li>
            <li class>
              <router-link to="/manage/environment">TeamIDE环境配置</router-link>
            </li>
            <li class>
              <router-link to="/manage/remote">远程服务器配置</router-link>
            </li>
            <li class>
              <router-link to="/manage/plugin">插件管理</router-link>
            </li>
          </ul>
        </li>
      </ul>
    </div>
    <div class="app-header-right">
      <ul class="app-nav app-nav-ul app-header-nav">
        <li class="app-nav-left" v-if="source.LOGIN_USER != null">
          <a>
            <img class :src="_SERVER_URL+'/resources/images/coos-30.jpg'" />
            {{source.LOGIN_USER.name}}
          </a>
          <ul class="app-nav-ul">
            <li class>
              <a :href="app.getSpaceUrl(source.LOGIN_USER.space)" style>我的仓库</a>
            </li>
            <li class>
              <router-link to="/user/center">个人中心</router-link>
            </li>
            <li class>
              <a @click="doLogout()">退出</a>
            </li>
          </ul>
        </li>
        <li class="app-nav-left" v-if="source.LOGIN_USER != null">
          <a @click="source.toPreference()">偏好设置</a>
        </li>
        <li class="app-nav-left" v-if="source.LOGIN_USER == null">
          <a @click="source.toLogin()">登录</a>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  name: "Header",
  data() {
    return {
      source: source
    };
  },
  beforeMount() {},
  watch: {},
  methods: {
    doLogout() {
      source.doLogout();
    }
  },
  computed: {},
  mounted() {}
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.app-header {
  color: #ffffff;
  background-color: #008992 !important;
  position: relative;
  width: 100%;
  margin: 0px auto;
}
.app-header ul.app-header-nav > li > a:hover {
  color: #ffffff;
  background-color: #01747b !important;
}
.app-header:after {
  content: "";
  display: table;
  clear: both;
}
.app-header-left {
  position: relative;
  float: left;
  padding: 0px 0px 0px 100px;
}
.app-header-center {
  position: relative;
  float: left;
  padding: 0px 0px;
}
.app-header-right {
  position: relative;
  float: right;
  padding: 0px 100px 0px 0px;
}
</style>
