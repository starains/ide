<template>
  <div class="doc-page">
    <div class="doc-header">仓库说明</div>
    <div class="doc-body">
      <div class="pdtb-10">新建空间仓库为库类型，则该空间可以存储代码等</div>
      <div class="pdtb-10">
        <div class="pd-30 coos-row">
          <a :href="_SERVER_URL + 'resources/doc/r-1.png'" target="blank_">
            <img :src="_SERVER_URL + 'resources/doc/r-1.png'" class="col-12 bd-1 bd-grey" />
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
