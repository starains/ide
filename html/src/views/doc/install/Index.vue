<template>
  <div class="doc-page">
    <div class="doc-header">安装说明</div>
    <div class="doc-body">
      <div class="pdtb-10">
        进入
        <a
          class="coos-link color-green pdlr-5"
          target="_blank"
          href="https://gitee.com/teamide/ide"
        >Team IDE</a>下载最新源码，源码为Java语言，使用
        <a
          class="coos-link color-green pdlr-5"
          target="_blank"
          href="https://maven.apache.org/"
        >Maven</a>管理依赖包。
      </div>
      <div class>
        <h4 class="pdtb-10 color-grey">安装部署</h4>
        <div class="md-box">
          <Install></Install>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Install from "./install.md";
import test from "./test.md";
export default {
  name: "Index",
  components: { Install },
  data() {
    return { source: source, Install };
  },
  mounted() {}
};
</script>
