<template>
  <div class="doc-page">
    <div class="doc-header">空间说明</div>
    <div class="doc-body">
      <div class="pdtb-10">用户登录成功后进入个人空间地址，即可新建空间仓库</div>
      <div class="pdtb-10">空间仓库类型</div>
      
      <div class="pdtb-10 color-orange">仓库：仓库空间用于存储源码文件，可以设置Git管理，仓库空间根目录作为默认项目；</div>
      <div class="pdtb-10 color-orange">产品：产品空间类型，可以在该空间创建子空间仓库，访问格式为：/products/产品名称；</div>
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
