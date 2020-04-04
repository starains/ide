<template>
  <div class="app-min-page">
    <template v-if="source.data.JOIN_SPACES == null">
      <div class="text-center pd-50 font-lg color-orange">仓库数据加载中...</div>
    </template>
    <template v-else>
      <template v-if="source.data.JOIN_SPACES.list != null">
        <SpaceList :list="source.data.JOIN_SPACES.list"></SpaceList>
        <template v-if="source.data.JOIN_SPACES.hasNext">
          <div class="text-center pd-10">
            <a class="coos-link pdlr-10 color-green" @click="loadJoinSpaces()">加载更多数据</a>
          </div>
        </template>
        <template
          v-if="!source.data.JOIN_SPACES.hasNext && source.data.JOIN_SPACES.list.length > 0"
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
  name: "JoinIndex",
  components: { SpaceList },
  data() {
    return {
      source: source,
      spaces: null
    };
  },
  methods: {
    loadJoinSpaces(pageindex) {
      let data = { pagesize: 5, pageindex: pageindex };
      source.load("JOIN_SPACES", data);
    }
  },
  mounted() {
    this.loadJoinSpaces(1);
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
