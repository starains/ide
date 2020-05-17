<template>
  <div class="doc-page">
    <div class="doc-header">Nginx资源</div>
    <div class="doc-body">
      <div class="pdtb-10">申请Nginx资源，可以外网访问启动项目</div>

      <div class="pdtb-10">
        <div class="pd-30 coos-row">
          <a :href="_SERVER_URL + 'resources/doc/nginx-1.png'" target="_blank">
            <img :src="_SERVER_URL + 'resources/doc/nginx-1.png'" class="col-12 bd-1 bd-grey" />
          </a>
        </div>
        <div class="pd-30 coos-row mgt-10">
          <a :href="_SERVER_URL + 'resources/doc/nginx-2.png'" target="_blank">
            <img :src="_SERVER_URL + 'resources/doc/nginx-2.png'" class="col-12 bd-1 bd-grey" />
          </a>
        </div>
        <div class="pd-30 coos-row mgt-10">
          <a :href="_SERVER_URL + 'resources/doc/nginx-3.png'" target="_blank">
            <img :src="_SERVER_URL + 'resources/doc/nginx-3.png'" class="col-12 bd-1 bd-grey" />
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
