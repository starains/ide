<template>
  <div class="doc-page">
    <div class="doc-header">仓库项目</div>
    <div class="doc-body">
      <div class="pdtb-10">仓库目录中，默认以根路径作为一个项目，如果将子目录作为项目，可以右击目录，点击设为项目导航，即可将该目录配置为项目</div>
      <div class="pdtb-10">
        <span class="color-red">Team IDE会读取项目目录下pom.xml文件，识别Java Maven</span>，如果没有则作为普通项目
      </div>
      <div class="pdtb-10">
        <div class="pd-30 coos-row">
          <a :href="_SERVER_URL + 'resources/doc/p-1.png'" target="blank_">
            <img :src="_SERVER_URL + 'resources/doc/p-1.png'" class="col-12 bd-1 bd-grey" />
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
