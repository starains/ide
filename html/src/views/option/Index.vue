<template>
  <div class="app-min-page">
    <el-collapse class="bd" v-model="activeName" accordion>
      <el-collapse-item title name="1">
        <template slot="title">
          <span class="color-grey pdlr-10">
            基本设置
            <i class="coos-icon coos-icon-setting"></i>
          </span>
        </template>
        <div class="pd-10">基本设置</div>
      </el-collapse-item>
      <el-collapse-item name="2">
        <template slot="title">
          <span class="color-orange pdlr-10">
            转移
            <i class="coos-icon coos-icon-drag"></i>
          </span>
        </template>
        <div class="pd-10">转移</div>
      </el-collapse-item>
      <el-collapse-item name="3">
        <template slot="title">
          <span class="color-red pdlr-10">
            删除
            <i class="coos-icon coos-icon-delete"></i>
          </span>
        </template>
        <div class="pd-10">删除</div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
export default {
  name: "OptionIndex",
  data() {
    return {
      activeName: "1"
    };
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
