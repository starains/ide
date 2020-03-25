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
                <template
                  v-if="item.deploy_status == 'UPLOADING' ||item.deploy_status == 'DEPLOYING' "
                >
                  <template v-if="item.deploy_status == 'UPLOADING'">
                    <a class="color-orange starter-status">启动资源上传中</a>
                  </template>
                  <template v-if="item.deploy_status == 'DEPLOYING'">
                    <a class="color-orange starter-status">启动资源安装中</a>
                  </template>
                  <template v-if="item.install_status == 'INSTALL_STARER'">
                    <a class="color-orange starter-status">安装启动器</a>
                  </template>
                  <template v-if="item.install_status == 'INSTALL_STARER_ERROR'">
                    <a class="color-red starter-status">安装启动器出错</a>
                  </template>
                  <template v-if="item.install_status == 'INSTALL_SERVER'">
                    <a class="color-orange starter-status">安装服务</a>
                  </template>
                  <template v-if="item.install_status == 'INSTALL_SERVER_ERROR'">
                    <a class="color-red starter-status">安装服务出错</a>
                  </template>
                  <template v-if="item.install_status == 'INSTALL_PROJECT_ING'">
                    <a class="color-orange starter-status">安装工程中</a>
                  </template>
                  <template v-if="item.install_status == 'INSTALL_PROJECT_ED'">
                    <a class="color-green starter-status">安装工程成功</a>
                  </template>
                  <template v-if="item.install_status == 'INSTALL_PROJECT_ERROR'">
                    <a class="color-red starter-status">安装出错</a>
                  </template>
                </template>
                <template
                  v-if="item.deploy_status == 'UPLOADED' || item.deploy_status == 'DEPLOYED' "
                >
                  <template v-if="item.install_status == 'INSTALL_PROJECT_ING'">
                    <a class="color-orange starter-status">安装工程中</a>
                  </template>
                  <template v-if="item.install_status == 'INSTALL_PROJECT_ED'">
                    <a class="color-green starter-status">安装工程成功</a>
                  </template>
                  <template v-if="item.install_status == 'INSTALL_PROJECT_ERROR'">
                    <a class="color-red starter-status">安装出错</a>
                  </template>
                  <template v-if="item.install_status == 'WORK_UPLOADING'">
                    <a class="color-orange starter-status">启动资源上传中...</a>
                  </template>
                  <template v-if="item.install_status == 'WORK_UPLOADED'">
                    <a class="color-orange starter-status">启动资源上传成功</a>
                  </template>
                  <template v-if="item.status == 'DESTROYING'">
                    <a class="color-green starter-status">销毁中</a>
                  </template>
                  <template v-if="item.status == 'DESTROYED'">
                    <a class="color-green starter-status">已销毁</a>
                  </template>
                  <template v-if="item.status == 'STARTING'">
                    <a class="color-green starter-status">启动中</a>
                  </template>
                  <template v-if="item.status == 'STARTED'">
                    <a class="color-green starter-status">已启动</a>
                  </template>
                  <template v-if="item.status == 'STOPPING'">
                    <a class="color-red starter-status">停止中</a>
                  </template>
                  <template v-if="item.status == 'STOPPED' || coos.isEmpty(item.status)">
                    <a class="color-red starter-status">已停止</a>
                  </template>

                  <template v-if="item.status == 'STOPPED' || coos.isEmpty(item.status)">
                    <a class="coos-btn coos-btn-xs bg-green" @click="source.startStarter(item)">启动</a>
                  </template>
                  <template v-if="item.status == 'STARTED'">
                    <a
                      class="coos-btn coos-btn-xs bg-red"
                      :class="{'coos-disabled' : item.status != 'STARTED'}"
                      @click="source.stopStarter(item)"
                    >停止</a>
                  </template>

                  <template v-if="item.status == 'STOPPED' ||  coos.isEmpty(item.status)">
                    <a
                      class="coos-btn coos-btn-xs bg-orange"
                      @click="source.deployStarter(item)"
                    >重新部署</a>
                  </template>
                </template>
                <template v-if="item.status == 'STOPPED' ||  coos.isEmpty(item.status)">
                  <a class="coos-btn coos-btn-xs bg-orange" @click="source.removeStarter(item)">移除</a>
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
