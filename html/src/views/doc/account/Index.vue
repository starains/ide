<template>
  <div class="doc-page">
    <div class="doc-header">账号说明</div>
    <div class="doc-body">
      <div class="pdtb-10">
        新用户注册完账号，系统将自动分配空间地址给新用户，用户空间地址为
        <span class="pdlr-10 color-orange">系统地址/登录账号</span>
      </div>
      <div class="pdtb-10">
        示例：
        <a
          class="pdlr-10 color-green coos-link"
          :href="_ROOT_URL + 'demo'"
          target="_blank"
        >demo（{{_ROOT_URL}}demo）</a>
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
