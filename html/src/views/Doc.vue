<template>
  <div class="doc-frame">
    <div class="doc-frame-body">
      <div class="doc-frame-left coos-scrollbar">
        <el-menu class="doc-frame-menu" :default-openeds="['0','1','2','3','4','5']" :router="true">
          <el-submenu index="0">
            <template slot="title">
              <span slot="title">
                <div class="ft-20">TeamIDE</div>
              </span>
            </template>
            <el-menu-item index="/doc">说明</el-menu-item>
            <el-menu-item index="/doc/install">安装</el-menu-item>
            <el-menu-item index="/doc/code">源码</el-menu-item>
            <el-menu-item index="/doc/account">账号</el-menu-item>
            <el-menu-item index="/doc/space">空间</el-menu-item>
            <el-menu-item index="/doc/space/team">空间成员</el-menu-item>
            <el-menu-item index="/doc/repository">仓库</el-menu-item>
            <el-menu-item index="/doc/repository/git">Git</el-menu-item>
            <el-menu-item index="/doc/repository/project">仓库项目</el-menu-item>
            <el-menu-item index="/doc/repository/project/starter">启动项目</el-menu-item>
          </el-submenu>
          <el-submenu index="1">
            <template slot="title">
              <span slot="title">
                <div class="ft-20">App Designer</div>
              </span>
            </template>
            <el-menu-item index="/doc/app">说明</el-menu-item>
            <el-menu-item index="/doc/app/app">应用设置</el-menu-item>
            <el-menu-item index="/doc/app/jdbc">JDBC</el-menu-item>
            <el-menu-item index="/doc/app/database">Database</el-menu-item>
            <el-menu-item index="/doc/app/table">Table</el-menu-item>
            <el-menu-item index="/doc/app/dao">Dao</el-menu-item>
            <el-menu-item index="/doc/app/service">Service</el-menu-item>
            <el-menu-item index="/doc/app/control">Control</el-menu-item>
            <el-menu-item index="/doc/app/bean">Bean</el-menu-item>
            <el-menu-item index="/doc/app/dictionary">Dictionary</el-menu-item>

            <el-menu-item index="/doc/app/java">生成Java源码</el-menu-item>

            <el-menu-item index="/doc/app/html">HTML</el-menu-item>
            <el-menu-item index="/doc/app/html/frame">HTML Frame</el-menu-item>
            <el-menu-item index="/doc/app/html/page">HTML Page</el-menu-item>

            <el-menu-item index="/doc/app/html/vue">生成Vue源码</el-menu-item>
          </el-submenu>
        </el-menu>
      </div>
      <div class="doc-frame-right coos-scrollbar">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  components: {},
  data() {
    return { source: source, window: window };
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style >
.doc-frame {
  height: 100%;
}
.doc-frame-body {
  display: flex;
  height: 100%;
  background: #fff;
}
.doc-frame-left {
  width: 250px;
  display: flex;
  flex-direction: row;
  flex-basis: auto;
}
.doc-frame-left {
  overflow: auto;
  position: fixed;
  top: 40px;
  left: 0px;
  right: 0px;
  bottom: 0px;
}
.doc-frame-right {
  flex: 1;
  position: fixed;
  overflow: auto;
  top: 40px;
  left: 250px;
  right: 0px;
  bottom: 0px;
}
.doc-frame-menu {
  width: 250px;
}
.doc-frame .el-submenu__title {
  height: 45px;
  line-height: 45px;
  pointer-events: none;
}
.doc-frame .el-submenu__title > i:last-child {
  display: none;
}
.doc-frame .el-submenu .el-menu-item {
  height: 35px;
  line-height: 35px;
}
.doc-page {
  padding: 0px;
}
.doc-header {
  font-size: 22px;
  font-weight: 600;
  color: #1f2f3d;
  border-bottom: 3px solid #ff9800;
  padding: 10px 20px;
}
.doc-body {
  padding: 10px 20px;
}
.md-box {
  box-sizing: border-box;
  margin: 0 auto;
}
.md-box > pre > code code {
  background-color: #f9fafc;
  padding: 0 4px;
  border: 1px solid #eaeefb;
  border-radius: 4px;
}

/*! normalize.css v3.0.1 | MIT License | git.io/normalize */
.markdown pre {
  overflow: auto;
}

.markdown code,
.markdown pre {
  font-family: monospace, monospace;
  font-size: 1em;
  font-family: Source Code Pro, Consolas, Menlo, Monaco, Courier New, monospace;
}

.markdown code {
  padding: 2px 4px;
  font-size: 90%;
  color: #c7254e;
  background-color: #f9f2f4;
  border-radius: 4px;
}

.markdown pre {
  display: block;
  padding: 9.5px;
  margin: 0 0 10px;
  font-size: 13px;
  line-height: 1.42858;
  word-break: break-all;
  word-wrap: break-word;
  color: #333;
  background-color: #f5f5f5;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.markdown pre code {
  padding: 0;
  font-size: inherit;
  color: inherit;
  white-space: pre-wrap;
  background-color: transparent;
  border-radius: 0;
}

.markdown pre {
  position: static;
  max-height: none;
}

.markdown {
  line-height: 1.6;
  word-wrap: break-word;
  color: #333;
}

.markdown h1 {
  font-size: 2em;
  line-height: 1.75em;
}

.markdown h2 {
  font-size: 1.75em;
  line-height: 1.5em;
}

.markdown div {
  font-size: 1.5em;
  line-height: 1.25em;
}

.markdown h4 {
  font-size: 1.25em;
  line-height: 1em;
}

.markdown h5 {
  font-size: 1em;
  line-height: 0.86em;
}

.markdown h6 {
  font-size: 0.86em;
  line-height: 0.75em;
}

.markdown blockquote,
.markdown h4,
.markdown h5,
.markdown ol,
.markdown p,
.markdown pre,
.markdown table:not([class]) {
  margin: 1.5em 0;
}

.markdown h1,
.markdown h2,
.markdown div {
  margin: 1.5em 0 0;
}

.markdown h1 + .widget-codetool + pre,
.markdown h2 + .widget-codetool + pre,
.markdown div + .widget-codetool + pre {
  margin-top: 1.5em !important;
}

.markdown h1,
.markdown h2 {
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.markdown > h1:first-child,
.markdown blockquote:first-child,
.markdown h2:first-child,
.markdown div:first-child,
.markdown h4:first-child,
.markdown ol:first-child,
.markdown p:first-child,
.markdown pre:first-child {
  margin-top: 0;
}

.markdown ol {
  margin-left: 3em;
  padding-left: 0;
}

.markdown ol li {
  margin: 0.3em 0;
}

.markdown ol ol,
.markdown ol ul {
  margin-top: 0;
  margin-bottom: 0;
}

.markdown ol p {
  margin: 0;
}

.markdown p:last-child {
  margin-bottom: 0;
}

.markdown div > br:only-child,
.markdown div > p:empty,
.markdown p + br:not([class]),
.markdown p > div:empty,
.markdown p > p:empty {
  display: none;
}

.markdown audio,
.markdown img:not([class]),
.markdown video {
  position: static !important;
  max-width: 100%;
}

.markdown img:not([class]) {
  padding: 3px;
  border: 1px solid #ddd;
}

.markdown img:not([class]).emoji {
  padding: 0;
  border: none;
}

.markdown blockquote {
  border-left: 2px solid #409eff;
  background: #f6f6f6;
  color: #555;
  font-size: 1em;
}

.markdown code,
.markdown pre {
  font-size: 0.93em;
}

.markdown pre {
  padding: 1em;
  border: none;
  overflow: auto;
  line-height: 1.45;
  position: relative;
  background-color: #f6f6f6;
  background-size: 30px, 30px;
}

.markdown pre code {
  background: none;
  font-size: 1em;
  overflow-wrap: normal;
  white-space: inherit;
}

.markdown hr {
  margin: 1.5em auto;
  border-top: 2px dotted #eee;
}

.markdown kbd {
  margin: 0 4px;
  padding: 3px 4px;
  background: #eee;
  color: #555;
}

.markdown .x-scroll {
  overflow-x: auto;
}

.markdown table:not([class]) {
  width: 100%;
}

.markdown table:not([class]) td,
.markdown table:not([class]) th {
  border: 1px solid #e6e6e6;
  padding: 5px 8px;
  word-break: normal;
}

.markdown table:not([class]) th {
  background: #f3f3f3;
}

.markdown a:not([class]) {
  color: #409eff;
  text-decoration: none;
}

.markdown a:not([class]):hover {
  border-bottom: 1px solid #409eff;
  text-decoration: none;
}
</style>
