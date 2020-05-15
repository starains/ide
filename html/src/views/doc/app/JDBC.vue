<template>
  <div class="doc-page">
    <div class="doc-header">JDBC</div>
    <div class="doc-body">
      <div class="pdtb-10">打开已配置的文件目录，右击创建文件，文件名定义为jdbc，打开jdbc设置驱动</div>

      <div class="pdtb-10">
        <div class="pd-30 coos-row">
          <a :href="_SERVER_URL + 'resources/doc/app-1.png'" target="blank_">
            <img :src="_SERVER_URL + 'resources/doc/app-1.png'" class="col-12 bd-1 bd-grey" />
          </a>
        </div>
        <div class="pd-30 coos-row mgt-10">
          <a :href="_SERVER_URL + 'resources/doc/app-2.png'" target="blank_">
            <img :src="_SERVER_URL + 'resources/doc/app-2.png'" class="col-12 bd-1 bd-grey" />
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
