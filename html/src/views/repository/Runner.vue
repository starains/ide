<template>
  <div class="repository-runner-box" :class="{'show' : repository.runner_show}">
    <div class="coos-row bdb-1 bdb-grey-2 mgb-0">
      <div class="ft-20 pd-10 color-green">
        服务器
        <small class="ft-12 color-grey">可以添加服务器配置，远程部署、运行、停止项目</small>
      </div>
      <a
        class="coos-pointer color-grey"
        @click="source.closeRunnerBox()"
        style="position: absolute;right: 10px;top: 10px;"
      >
        <i class="coos-icon coos-icon-close ft-25"></i>
      </a>
    </div>
    <div class="repository-runner-tabs">
      <el-tabs v-model="repository.runnerActive" @tab-click="clickTab">
        <el-tab-pane v-for="(item) in repository.runners" :key="item.token" :name="item.token">
          <span slot="label" :title="item.title">{{item.text}}</span>
          <div class>
            <div class="coos-row text-right pdr-10 pdt-5 runner-btns">
              <template v-if="item.option != null">
                <template v-if="item.status == 'INSTALL'">
                  <a class="color-green runner-status">终端程序安装</a>
                </template>
                <template v-if="item.status == 'INSTALL_SERVER'">
                  <a class="color-green runner-status">复制服务</a>
                </template>
                <template v-if="item.status == 'INSTALL_SOURCE'">
                  <a class="color-green runner-status">编译</a>
                </template>
                <template v-if="item.status == 'INSTALL_SHELL'">
                  <a class="color-green runner-status">生成Shell</a>
                </template>
                <template v-if="item.status == 'STARTING_RUNNER'">
                  <a class="color-green runner-status">终端启动中...</a>
                </template>
                <template v-if="item.status == 'STARTED_RUNNER'">
                  <a class="color-green runner-status">终端启动成功</a>
                </template>

                <template v-if="item.status == 'STARTING'">
                  <a class="color-green runner-status">启动中</a>
                </template>
                <template v-if="item.status == 'STARTED'">
                  <a class="color-green runner-status">已启动</a>
                </template>
                <template v-if="item.status == 'STOPPING'">
                  <a class="color-red runner-status">停止中</a>
                </template>
                <template v-if="item.status == 'STOPPED'">
                  <a class="color-red runner-status">已停止</a>
                </template>
                <template v-if="item.status == 'DESTROYING'">
                  <a class="color-red runner-status">终端销毁中...</a>
                </template>
                <template v-if="item.status == 'DESTROYED'">
                  <a class="color-red runner-status">终端销毁成功</a>
                </template>

                <template
                  v-if="item.status == 'STOPPED' || item.status == 'DESTROYED' || item.status == 'STARTED_RUNNER'"
                >
                  <a class="coos-btn coos-btn-xs bg-green" @click="source.startrunner(item)">启动</a>
                </template>
                <template v-if="item.status == 'STARTED'">
                  <a
                    class="coos-btn coos-btn-xs bg-red"
                    :class="{'coos-disabled' : item.status != 'STARTED'}"
                    @click="source.stoprunner(item)"
                  >停止</a>
                </template>
                <template
                  v-if="item.status == 'STARTED' || item.status == 'STOPPED' || item.status == 'STARTED_RUNNER'"
                >
                  <a class="coos-btn coos-btn-xs bg-orange" @click="source.destroyrunner(item)">销毁</a>
                </template>
                <template v-if="item.status == 'DESTROYED'">
                  <a class="coos-btn coos-btn-xs bg-orange" @click="source.removerunner(item)">移除</a>
                </template>
              </template>
              <a class="coos-btn coos-btn-xs bg-orange" @click="source.cleanrunnerLog(item)">清理日志</a>
            </div>
            <div class="repository-runner-log-box coos-scrollbar">
              <div
                v-for="log in item.logs"
                :key="log.id"
                class
                :class="{'color-red':log.level == 'ERROR','color-orange':log.level == 'WARN'}"
              >{{log.msg}}</div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
export default {
  components: {},
  props: ["repository"],
  data() {
    return { source: source };
  },
  beforeMount() {},
  watch: {
    "repository.runnerActive": function(activeTab) {}
  },
  methods: {
    clickTab() {}
  },
  mounted() {}
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style  >
.repository-runner-box {
  position: absolute;
  top: 0px;
  left: 0px;
  bottom: 0px;
  right: 0px;
  background-color: #fff;
  -webkit-box-shadow: 0px 0px 10px #ddd;
  box-shadow: 0px 0px 10px #ddd;
  display: none;
  z-index: 10;
}
.repository-runner-box.show {
  display: block;
}
.repository-runner-box .el-tabs__header {
  margin: 0px;
}
.repository-runner-box .el-tabs__nav-scroll {
  padding-left: 10px;
}
.repository-runner-box .el-tabs__nav-next,
.repository-runner-box .el-tabs__nav-prev {
  height: 30px;
  line-height: 30px;
}
.repository-runner-box .el-tabs__nav {
  height: 30px;
}
.repository-runner-box .el-tabs__nav:first-child {
  border-left: 0px;
}
.repository-runner-box .el-tabs__header .el-tabs__item {
  font-size: 12px;
  height: 30px;
  line-height: 30px;
}
.repository-runner-box .el-tabs__item.is-active,
.repository-runner-box .el-tabs__item:hover {
  color: #008992;
}
.repository-runner-box .runner-btns > a {
  padding: 4px 8px !important;
  margin-left: 5px;
}
.repository-runner-box .runner-status {
  font-size: 12px;
}
.repository-runner-box .el-tabs__content {
  position: absolute;
  left: 0px;
  top: 75px;
  right: 0px;
  bottom: 0px;
}
.repository-runner-box .el-tab-pane {
  position: absolute;
  width: 100%;
  height: 100%;
}
.repository-runner-box .repository-runner-log-box {
  position: absolute;
  left: 0px;
  top: 33px;
  right: 0px;
  bottom: 0px;
  overflow-y: auto;
  background-color: #3a3a3a;
  color: #ffffff;
  font-size: 13px;
  line-height: 18px;
  padding: 10px;
}
</style>
