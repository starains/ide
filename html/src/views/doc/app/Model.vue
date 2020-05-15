<template>
  <div class="doc-page">
    <div class="doc-header">App Designer</div>
    <div class="doc-body">
      <div class="pdtb-10">
        首先
        <router-link :to="{path:'/doc/app/app'}">
          <a class="coos-link color-green pdlr-5">设置模型</a>
        </router-link>
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
