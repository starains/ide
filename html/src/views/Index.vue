<template>
  <div class>
    <template v-if="source.space == null">
      <template v-if="source.NOT_PERMISSION != null">
        <div class="font-lg color-orange text-center pd-50">暂无访问该仓库的权限，请联系仓库管理员！</div>
      </template>
      <template v-else>
        <template v-if="source.NOT_FOUND_SPACE != null">
          <div class="font-lg color-orange text-center pd-50">仓库不存在，请检查输入的仓库名称是否正确！</div>
        </template>
        <template v-else>
          <template v-if="source.SPACE_TYPE == null">
            <IndexIndex></IndexIndex>
          </template>
          <template v-else>
            <SpaceIndex></SpaceIndex>
          </template>
        </template>
      </template>
    </template>
    <template v-else>
      <template v-if="source.NOT_FOUND_REPOSITORY_BRANCH != null">
        <div class="font-lg color-orange text-center pd-50">仓库版本不存在，请检查输入的版本是否正确！</div>
      </template>
      <template v-else>
        <template v-if="source.SPACE_TYPE =='REPOSITORYS'">
          <RepositoryIndex></RepositoryIndex>
        </template>
        <template v-else>
          <SpaceIndex></SpaceIndex>
        </template>
      </template>
    </template>
  </div>
</template>

<script>
import IndexIndex from "@/views/index/IndexIndex";
import SpaceIndex from "@/views/index/SpaceIndex";
import RepositoryIndex from "@/views/index/RepositoryIndex";

export default {
  name: "Index",
  components: { IndexIndex, SpaceIndex, RepositoryIndex },
  data() {
    return { source: source };
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
