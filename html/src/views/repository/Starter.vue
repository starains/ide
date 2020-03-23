<template>
  <div class="repository-starter-box" :class="{'show' : repository.starter_show}">
    <div class="coos-row bdb-1 bdb-grey-2 mgb-0">
      <div class="ft-20 pd-10 color-green">
        控制台
        <small class="ft-12 color-grey">用于在本机部署、运行、停止项目，可以查看当前库日志和项目启动控制台日志</small>
      </div>
      <a
        class="coos-pointer color-grey"
        @click="source.closeStarterBox()"
        style="position: absolute;right: 10px;top: 10px;"
      >
        <i class="coos-icon coos-icon-close ft-25"></i>
      </a>
    </div>
    <div class="repository-starter-tabs">
      <el-tabs v-model="repository.starterActive" @tab-click="clickTab">
        <el-tab-pane v-for="(item) in repository.starters" :key="item.token" :name="item.token">
          <span slot="label" :title="item.title">{{item.text}}</span>
          <div class>
            <div class="coos-row text-right pdr-10 pdt-5 starter-btns">
              <template v-if="item.option != null">
                <template v-if="item.deploy_status == 'INSTALL_STARER'">
                  <a class="color-green starter-status">终端程序安装</a>
                </template>
                <template v-if="item.deploy_status == 'STARTING_STARTER'">
                  <a class="color-green starter-status">终端启动中</a>
                </template>
                <template v-if="item.deploy_status == 'STARTED_STARTER'">
                  <a class="color-green starter-status">终端启动完成</a>
                </template>
                <template v-if="item.deploy_status == 'INSTALL_SERVER'">
                  <a class="color-green starter-status">复制服务</a>
                </template>
                <template v-if="item.deploy_status == 'COMPILE'">
                  <a class="color-green starter-status">编译</a>
                </template>
                <template v-if="item.deploy_status == 'INSTALL_SHELL'">
                  <a class="color-green starter-status">生成Shell</a>
                </template>
                <template v-if="item.deploy_status == 'STARTING_TERMINAL'">
                  <a class="color-green starter-status">终端启动中...</a>
                </template>
                <template v-if="item.deploy_status == 'STARTED_TERMINAL'">
                  <a class="color-green starter-status">终端启动成功</a>
                </template>
                <template v-if="item.deploy_status == 'DESTROYING'">
                  <a class="color-red starter-status">终端销毁中...</a>
                </template>
                <template v-if="item.deploy_status == 'DESTROYED'">
                  <a class="color-red starter-status">终端已销毁</a>
                </template>
                <template v-if="item.deploy_status == 'DEPLOYING'">
                  <a class="color-orange starter-status">终端部署中</a>
                </template>
                <template v-if="item.deploy_status == 'DEPLOYED'">
                  <a class="color-green starter-status">终端部署成功</a>
                </template>

                <template v-if="item.deploy_status != 'DEPLOYING'">
                  <template v-if="item.now_timestamp - item.starter_timestamp > 5000">
                    <a class="color-red starter-status">终端已停止，请移除</a>
                  </template>
                  <template v-else>
                    <template v-if="item.status == 'STARTING'">
                      <a class="color-green starter-status">启动中</a>
                    </template>
                    <template v-if="item.status == 'STARTED'">
                      <a class="color-green starter-status">已启动</a>
                    </template>
                    <template v-if="item.status == 'STOPPING'">
                      <a class="color-red starter-status">停止中</a>
                    </template>
                    <template v-if="item.status == 'STOPPED'">
                      <a class="color-red starter-status">已停止</a>
                    </template>
                  </template>

                  <template
                    v-if="item.status == 'STOPPED' || item.deploy_status == 'DEPLOYED' || item.deploy_status == 'DESTROYED' "
                  >
                    <a
                      class="coos-btn coos-btn-xs bg-green"
                      @click="source.deployStarter(item)"
                    >重新部署</a>
                  </template>
                  <template v-if="item.status == 'STOPPED' || item.deploy_status == 'DEPLOYED' ">
                    <a class="coos-btn coos-btn-xs bg-green" @click="source.startStarter(item)">启动</a>
                  </template>
                  <template v-if="item.status == 'STARTED'">
                    <a
                      class="coos-btn coos-btn-xs bg-red"
                      :class="{'coos-disabled' : item.status != 'STARTED'}"
                      @click="source.stopStarter(item)"
                    >停止</a>
                  </template>
                  <template
                    v-if="item.status == 'STARTED' || item.status == 'STOPPED' || item.deploy_status == 'DEPLOYED'"
                  >
                    <a
                      class="coos-btn coos-btn-xs bg-orange"
                      @click="source.destroyStarter(item)"
                    >销毁</a>
                  </template>
                  <template
                    v-if="item.deploy_status == 'DESTROYED' || item.now_timestamp - item.starter_timestamp > 5000"
                  >
                    <a class="coos-btn coos-btn-xs bg-orange" @click="source.removeStarter(item)">移除</a>
                  </template>
                </template>
              </template>
              <a class="coos-btn coos-btn-xs bg-orange" @click="source.cleanStarterLog(item)">清理日志</a>
              <a class="coos-btn coos-btn-xs bg-blue" @click="source.reloadStarterLog(item)">重新加载日志</a>
            </div>
            <div class="repository-starter-log-box coos-scrollbar">
              <div
                v-for="log in item.logs"
                :key="log.id"
                class
                :class="{'color-red':log.level == 'ERROR','color-orange':log.level == 'WARN'}"
                v-show="log.show"
              >
                <span class="pdr-10 color-grey">{{log.index + 1}}</span>
                {{log.line}}
              </div>
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
    "repository.starterActive": function(activeTab) {}
  },
  methods: {
    clickTab() {}
  },
  mounted() {}
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style  >
.repository-starter-box {
  position: absolute;
  top: -105px;
  left: 0px;
  bottom: 0px;
  right: 0px;
  background-color: #fff;
  -webkit-box-shadow: 0px 0px 10px #ddd;
  box-shadow: 0px 0px 10px #ddd;
  display: none;
  z-index: 10;
}
.repository-starter-box.show {
  display: block;
}
.repository-starter-box .el-tabs__header {
  margin: 0px;
}
.repository-starter-box .el-tabs__nav-scroll {
  padding-left: 10px;
}
.repository-starter-box .el-tabs__nav-next,
.repository-starter-box .el-tabs__nav-prev {
  height: 30px;
  line-height: 30px;
}
.repository-starter-box .el-tabs__nav {
  height: 30px;
}
.repository-starter-box .el-tabs__nav:first-child {
  border-left: 0px;
}
.repository-starter-box .el-tabs__header .el-tabs__item {
  font-size: 12px;
  height: 30px;
  line-height: 30px;
}
.repository-starter-box .el-tabs__item.is-active,
.repository-starter-box .el-tabs__item:hover {
  color: #008992;
}
.repository-starter-box .starter-btns > a {
  padding: 4px 8px !important;
  margin-left: 5px;
}
.repository-starter-box .starter-status {
  font-size: 12px;
}
.repository-starter-box .el-tabs__content {
  position: absolute;
  left: 0px;
  top: 75px;
  right: 0px;
  bottom: 0px;
}
.repository-starter-box .el-tab-pane {
  position: absolute;
  width: 100%;
  height: 100%;
}
.repository-starter-box .repository-starter-log-box {
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
