<template>
  <div class="doc-page">
    <div class="doc-header">仓库Git设置</div>
    <div class="doc-body">
      <div class="pdtb-10">进入仓库目录，管理员可以点击左侧项目目录头部Git导航，设置仓库Git地址</div>

      <div class="pdtb-10">
        <div class="pd-30 coos-row">
          <a :href="_SERVER_URL + 'resources/doc/git-1.png'" target="blank_">
            <img :src="_SERVER_URL + 'resources/doc/git-1.png'" class="col-12 bd-1 bd-grey" />
          </a>
        </div>
        <div class="pd-30 coos-row mgt-10">
          <a :href="_SERVER_URL + 'resources/doc/git-2.png'" target="blank_">
            <img :src="_SERVER_URL + 'resources/doc/git-2.png'" class="col-12 bd-1 bd-grey" />
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
