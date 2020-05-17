<template>
  <div class="doc-page">
    <div class="doc-header">App Designer设置</div>
    <div class="doc-body">
      <div class="pdtb-10">右击项目点击App Designer设置，设置模型目录</div>

      <div class="pdtb-10">
        <div class="pd-30 coos-row">
          <a :href="_SERVER_URL + 'resources/doc/app-1.png'" target="_blank">
            <img :src="_SERVER_URL + 'resources/doc/app-1.png'" class="col-12 bd-1 bd-grey" />
          </a>
        </div>
        <div class="pd-30 coos-row mgt-10">
          <a :href="_SERVER_URL + 'resources/doc/app-2.png'" target="_blank">
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
