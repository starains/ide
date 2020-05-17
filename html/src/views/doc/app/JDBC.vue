<template>
  <div class="doc-page">
    <div class="doc-header">JDBC</div>
    <div class="doc-body">
      <div class="pdtb-10">
        首先
        <router-link :to="{path:'/doc/app/app'}">
          <a class="coos-link color-green pdlr-5">设置模型</a>
        </router-link>
      </div>
      <div class="pdtb-10">打开已配置的模型目录，右击创建文件，文件名定义为jdbc，打开jdbc设置驱动</div>
      <div class="pdtb-10">如果您有自己的驱动地址可以直接填写，如果没有则可以由Team IDE分配数据库资源</div>

      <div class="pdtb-10">
        <div class="pd-30 coos-row">
          <a :href="_SERVER_URL + 'resources/doc/jdbc-1.png'" target="_blank">
            <img :src="_SERVER_URL + 'resources/doc/jdbc-1.png'" class="col-12 bd-1 bd-grey" />
          </a>
        </div>
        <div class="pd-30 coos-row mgt-10">
          <a :href="_SERVER_URL + 'resources/doc/jdbc-2.png'" target="_blank">
            <img :src="_SERVER_URL + 'resources/doc/jdbc-2.png'" class="col-12 bd-1 bd-grey" />
          </a>
        </div>
        <div class="pd-30 coos-row mgt-10">
          <a :href="_SERVER_URL + 'resources/doc/jdbc-3.png'" target="_blank">
            <img :src="_SERVER_URL + 'resources/doc/jdbc-3.png'" class="col-12 bd-1 bd-grey" />
          </a>
        </div>
      </div>
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
