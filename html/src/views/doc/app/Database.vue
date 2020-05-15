<template>
  <div class="doc-page">
    <div class="doc-header">Database</div>
    <div class="doc-body">
      <div class="pdtb-10">
        首先
        <router-link :to="{path:'/doc/app/app'}">
          <a class="coos-link color-green pdlr-5">设置模型</a>
        </router-link>
      </div>
      <div class="pdtb-10">打开已配置的模型目录，右击创建文件夹，文件夹定义为database，在database目录下新建文件，文件名称作为库名称，打开文件即可进入设计页面</div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Index",
  components: {},
  data() {
    return { source: source };
  }
};
</script>
