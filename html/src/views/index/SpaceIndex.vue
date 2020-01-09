<template>
  <div class="app-min-page">
    <div class="coos-row">
      <div class="float-right">
        <a
          class="coos-link color-green"
          :class="{'coos-disabled':(!source.space || source.space.permission != 'MASTER')}"
          @click="toSpaceCreate()"
        >新建仓库</a>
      </div>
    </div>
    <template v-if="source.data.VISIBLE_SPACES == null">
      <div class="text-center pd-50 font-lg color-orange">仓库数据加载中...</div>
    </template>
    <template v-else>
      <template v-if="source.data.VISIBLE_SPACES.list != null">
        <SpaceList :list="source.data.VISIBLE_SPACES.list"></SpaceList>
        <template v-if="source.data.VISIBLE_SPACES.hasNext">
          <div class="text-center pd-10">
            <a class="coos-link pdlr-10 color-green" @click="loadVisibleSpaces()">加载更多数据</a>
          </div>
        </template>
        <template
          v-if="!source.data.VISIBLE_SPACES.hasNext && source.data.VISIBLE_SPACES.list.length > 0"
        >
          <div class="text-center pd-10 color-grey">暂无更多数据</div>
        </template>
      </template>
    </template>
  </div>
</template>

<script>
import SpaceList from "@/views/components/SpaceList";
export default {
  name: "SpaceIndex",
  components: { SpaceList },
  data() {
    return {
      source: source
    };
  },
  methods: {
    loadVisibleSpaces(pageindex) {
      let data = { pagesize: 5, pageindex: pageindex };
      source.load("VISIBLE_SPACES", data);
    },
    toSpaceCreate() {
      this.$router.push("/space/create");
    }
  },
  mounted() {
    this.loadVisibleSpaces(0);
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
