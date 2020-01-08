<template>
  <div class="workspace" v-if="source.repository!=null" v-loading="loading">
    <div class="top"></div>
    <div class="left">
      <el-input placeholder="输入关键字进行过滤" size="mini" v-model="filterText"></el-input>
      <div class="left-content">
        <div class="projects-box coos-scrollbar">
          <el-tree
            ref="tree"
            :data="source.repository.projects"
            :props="defaultProps"
            @node-click="handleProjectNodeClick"
            @node-contextmenu="handleProjectNodeContextMenu"
            :render-content="renderContent"
            :expand-on-click-node="true"
            node-key="path"
            :default-expanded-keys="open_folder_keys"
            :filter-node-method="filterNode"
          ></el-tree>
        </div>
      </div>
    </div>
    <div class="center">
      <div class="file-tabs">
        <el-tabs
          v-model="activeTab"
          type="card"
          closable
          @tab-remove="removeTab"
          @tab-click="clickTab"
        >
          <el-tab-pane v-for="(item) in tabs" :key="item.path" :name="item.path">
            <span slot="label" :title="item.path" :path="item.path">
              {{item.name}}
              <i
                v-show="item.changed"
                class="tab-need-save-icon color-red"
                style="font-size: 16px;vertical-align: -5px;"
              >*</i>
            </span>
          </el-tab-pane>
        </el-tabs>
      </div>
      <div class="file-editor-box"></div>
    </div>
    <div class="contextmenu-box" style="position: fixed;left:0px;top:0px;z-index:100;"></div>
    <el-badge :value="terminal_tabs.length >1?terminal_tabs.length-1:''" class="terminal-btn">
      <el-button
        size="mini"
        icon="coos-icon coos-icon-terminal ft-12"
        circle
        :class="show_terminal?'bg-green color-white':'bg-grey color-white'"
        @click="clickTerminalBtn()"
        title="控制台，可查看启动项目日志"
      ></el-button>
    </el-badge>
    <div class="terminal" v-show="show_terminal">
      <div class="color-grey ft-15 pdtb-5 pdlr-10">
        控制台，可查看启动项目日志
        <el-link size="mini" class="float-right" type="warning" @click="clickTerminalBtn()">关闭</el-link>
      </div>
      <div class="terminal-tabs">
        <el-tabs v-model="activeTerminalTab">
          <el-tab-pane v-for="(item) in terminal_tabs" :key="item.id" :name="item.id">
            <span
              slot="label"
              :title="item.title"
            >{{item.title}}{{item.data!=null?('（'+item.data.status+'）') : ''}}</span>
            <div class="float-right pdt-2 pdr-5">
              <template v-if="item.data !=null">
                <el-button
                  size="mini"
                  class="pdtb-3"
                  type="success"
                  @click="terminalStart(item)"
                  :class="{'coos-disabled':item.data.status != 'STOPPED' || !hasPermission()}"
                >
                  <i class="coos-icon coos-icon-play-circle ft-12"></i>
                  启动
                </el-button>
                <el-button
                  size="mini"
                  class="pdtb-3"
                  type="danger"
                  :class="{'coos-disabled':item.data.status != 'STARTED' || !hasPermission()}"
                  @click="terminalStop(item)"
                >
                  <i class="coos-icon coos-icon-circle ft-12"></i>停止
                </el-button>
                <el-button
                  size="mini"
                  class="pdtb-3 coos-disabled"
                  type="info"
                  v-if="item.data.status == 'STARTING'"
                >启动中</el-button>
                <el-button
                  size="mini"
                  class="pdtb-3 coos-disabled"
                  type="info"
                  v-if="item.data.status == 'STOPPING'"
                >停止中</el-button>
                <el-button
                  size="mini"
                  class="pdtb-3"
                  type="danger"
                  :class="{'coos-disabled':item.data.status != 'STOPPED' || !hasPermission()}"
                  @click="terminalRemove(item)"
                >
                  <i class="coos-icon coos-icon-delete ft-12"></i>移除
                </el-button>
              </template>
              <el-button
                size="mini"
                class="pdtb-3"
                type="warning"
                @click="terminalLogClean(item)"
                :class="{'coos-disabled':!hasPermission()}"
              >
                <i class="coos-icon coos-icon-rest ft-12"></i>清理日志
              </el-button>
            </div>
            <div class="log-box" :id="'log-box-terminal-'+item.id">
              <div
                v-for="log in item.logs"
                :key="log.id"
                class
                :class="{'color-red':log.level == 'ERROR','color-orange':log.level == 'WARN'}"
              >{{log.msg}}</div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <el-badge :value="runner_tabs.length >0?runner_tabs.length:''" class="runner-btn">
      <el-button
        size="mini"
        icon="coos-icon coos-icon-sever ft-12"
        circle
        :class="show_runner?'bg-green color-white':'bg-gray color-white'"
        @click="clickRunnerBtn()"
        title="服务器，可远程部署项目"
      ></el-button>
    </el-badge>
    <div class="runner" v-show="show_runner">
      <div class="color-grey ft-15 pdtb-5 pdlr-10">
        服务器，可远程部署项目
        <el-link size="mini" class="float-right" type="warning" @click="clickRunnerBtn()">关闭</el-link>
      </div>
      <div class="runner-tabs">
        <el-tabs v-model="activeRunnerTab">
          <el-tab-pane v-for="(item) in runner_tabs" :key="item.id" :name="item.id">
            <span slot="label" :title="item.title">{{item.title}}</span>
            <div class="float-right pdt-2 pdr-5">
              <el-button
                size="mini"
                class="pdtb-3"
                type="success"
                @click="runnerDeploy(item)"
                :class="{'coos-disabled':item.data.deploystatus == 'DEPLOYING' || !hasPermission()}"
              >
                <i class="coos-icon coos-icon-play-circle ft-12"></i>
                部署
              </el-button>
              <el-button
                size="mini"
                class="pdtb-3 coos-disabled"
                type="info"
                v-if="item.data.deploystatus == 'DEPLOYING'"
              >部署中</el-button>
              <el-button
                size="mini"
                class="pdtb-3"
                type="success"
                @click="runnerDeploy(item)"
                :class="{'coos-disabled':item.data.status != 'STOPPED' || !hasPermission()}"
              >
                <i class="coos-icon coos-icon-play-circle ft-12"></i>
                启动
              </el-button>
              <el-button
                size="mini"
                class="pdtb-3 coos-disabled"
                type="info"
                v-if="item.data.deploystatus == 'STARTING'"
              >启动中</el-button>
              <el-button
                size="mini"
                class="pdtb-3"
                type="danger"
                :class="{'coos-disabled':item.data.status != 'STARTED' || !hasPermission()}"
                @click="runnerStop(item)"
              >
                <i class="coos-icon coos-icon-play-circle ft-12"></i>
                停止
              </el-button>
              <el-button
                size="mini"
                class="pdtb-3 coos-disabled"
                type="info"
                v-if="item.data.deploystatus == 'STOPPING'"
              >停止中</el-button>
              <el-button
                size="mini"
                class="pdtb-3"
                type="danger"
                :class="{'coos-disabled':item.data.status != 'STOPPED' || !hasPermission()}"
                @click="runnerRemove(item)"
              >
                <i class="coos-icon coos-icon-play-delete ft-12"></i>
                移除
              </el-button>
              <el-button
                size="mini"
                class="pdtb-3"
                type="warning"
                @click="runnerLogClean(item)"
                :class="{'coos-disabled':!hasPermission()}"
              >
                <i class="coos-icon coos-icon-rest ft-12"></i>清理日志
              </el-button>
            </div>
            <div class="log-box" :id="'log-box-runner-'+item.id">
              <div
                v-for="log in item.logs"
                :key="log.id"
                class
                :class="{'color-red':log.level == 'ERROR','color-orange':log.level == 'WARN'}"
              >{{log.msg}}</div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <GitCertificate ref="git-certificate"></GitCertificate>
    <GitRemote ref="git-remote"></GitRemote>
    <GitPush ref="git-push"></GitPush>
    <FileCreate ref="file-create"></FileCreate>
    <FileRename ref="file-rename"></FileRename>
    <FilePaste ref="file-paste"></FilePaste>
    <RunForm ref="run-form"></RunForm>
    <RunnerForm ref="runner-form"></RunnerForm>
    <BranchCreate ref="branch-create"></BranchCreate>
  </div>
</template>

<script>
import tool from "@/common/js";
import http from "@/common/js/service";

import Base from "@/views/repository/Base";
import Git from "@/views/repository/Git";
import ContextMenu from "@/views/repository/ContextMenu";
import Project from "@/views/repository/Project";
import File from "@/views/repository/File";
import Maven from "@/views/repository/Maven";
import Run from "@/views/repository/Run";
import Terminal from "@/views/repository/Terminal";
import Branch from "@/views/repository/Branch";
import Tab from "@/views/repository/Tab";
import Runner from "@/views/repository/Runner";
import RunnerOption from "@/views/repository/RunnerOption";

let methods = {};
let data = {
  filterText: "",
  left_mouse_over: false,
  change_files: [],
  defaultProps: {
    children: "files",
    label: "name"
  },
  loading: false,
  file_map: {},
  open_folder_keys: [],
  open_file_keys: [],
  file_status: {
    added: [],
    changed: [],
    conflicting: [],
    conflictingStageState: [],
    ignoredNotInIndex: [],
    missing: [],
    modified: [],
    removed: [],
    uncommittedChanges: [],
    untracked: [],
    untrackedFolders: []
  }
};
function addMethod(objs) {
  objs.forEach(obj => {
    for (let key in obj) {
      if (key == "data") {
        let d = obj[key];
        for (let dkey in d) {
          data[dkey] = d[dkey];
        }
      } else {
        if (
          key == "beforeCreate" ||
          key == "__file" ||
          key == "beforeDestroy"
        ) {
        } else {
          methods[key] = obj[key];
        }
      }
    }
  });
}
addMethod([
  Base,
  Git,
  Terminal,
  Project,
  File,
  ContextMenu,
  Maven,
  Run,
  Branch,
  Tab,
  Runner,
  RunnerOption
]);

import GitCertificate from "@/views/repository/components/git/GitCertificate";
import GitRemote from "@/views/repository/components/git/GitRemote";
import GitPush from "@/views/repository/components/git/GitPush";
import FileCreate from "@/views/repository/components/file/FileCreate";
import FileRename from "@/views/repository/components/file/FileRename";
import FilePaste from "@/views/repository/components/file/FilePaste";
import RunForm from "@/views/repository/components/run/RunForm";
import RunnerForm from "@/views/repository/components/runner/RunnerForm";
import BranchCreate from "@/views/repository/components/branch/BranchCreate";

export default {
  components: {
    GitCertificate,
    GitRemote,
    GitPush,
    FileCreate,
    FileRename,
    FilePaste,
    RunForm,
    RunnerForm,
    BranchCreate
  },
  data() {
    data.source = source;
    return data;
  },
  beforeMount() {},
  watch: {
    filterText(val) {
      this.searchRepository(val);
    },
    activeTab: newActiveTab => {
      var $show = $(".file-editor-one.show");

      if ($show.length > 0 && $show.attr("path") == newActiveTab) {
      } else {
        $(".file-editor-one.show").removeClass("show");
        $("[path='" + newActiveTab + "'].file-editor-one").addClass("show");
      }
    }
  },
  methods: methods,
  mounted() {
    let that = this;
    document.addEventListener("paste", function(event) {
      if (event && event.target && event.target.id == "coos-for-copy-area") {
        app.clipboardGet(event, function(text) {
          that.onPaste(text);
        });
        event.preventDefault();
        event.stopPropagation();
      }
    });
    $(document).on("click", "html", function() {
      that.destroyContextmenu();
    });
    $(document).on("contextmenu", ".file-tabs .el-tabs__item", function(e) {
      let $item = $(e.target).closest(".el-tabs__item");
      let $span = $item.find("span:first");
      that.showFileTabContextmenu($span.attr("path"));
    });
    $(document).on("mouseup", ".file-tabs .el-tabs__item", function(e) {
      if (e.button == 1) {
        let $item = $(e.target).closest(".el-tabs__item");
        let $span = $item.find("span:first");
        that.closeTabByPath($span.attr("path"));
      }
    });
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style  >
.workspace {
  position: relative;
  background-color: #ddd;
  box-shadow: 0px 0px 10px #ddd;
  width: 100%;
  height: 100%;
}
.workspace .top {
  position: absolute;
  top: 5px;
  left: 0px;
  right: 0px;
  height: 35px;
  background-color: #fff;
  display: none;
}
.workspace .left {
  position: absolute;
  top: 0px;
  left: 0px;
  bottom: 0px;
  width: 310px;
  background-color: #fff;
  box-shadow: 0px 0px 10px #ddd;
}
.workspace .left .left-content {
  position: absolute;
  top: 30px;
  left: 0px;
  bottom: 0px;
  right: 0px;
}
.workspace .center {
  position: absolute;
  top: 0px;
  left: 310px;
  bottom: 0px;
  right: 0px;
  background-color: #fff;
  box-shadow: 0px 0px 10px #ddd;
}
.workspace .file-editor-box {
  position: absolute;
  bottom: 0px;
  top: 30px;
  left: 0px;
  right: 0px;
}
.workspace .file-editor-box .file-editor-one {
  width: 100%;
  height: 100%;
  display: none;
}
.workspace .file-editor-box .file-editor-one.show {
  display: block;
}
.projects-box {
  overflow: hidden;
  width: 100%;
  height: 100%;
}
.projects-box:hover {
  overflow: auto;
}
.projects-box .el-tree {
  min-width: 100%;
  font-size: 14px;
  display: inline-block;
}
.workspace .file-tabs .el-tabs__nav-next,
.workspace .file-tabs .el-tabs__nav-prev {
  height: 30px;
  line-height: 30px;
}
.workspace .file-tabs .el-tabs__nav {
  height: 30px;
}
.workspace .file-tabs .el-tabs__nav-scroll {
  margin-left: -1px;
}
.workspace .file-tabs .el-tabs__nav:first-child {
  border-left: 0px;
}
.workspace .file-tabs .el-tabs--card > .el-tabs__header .el-tabs__item {
  font-size: 12px;
  padding-left: 10px;
  padding-right: 10px;
  height: 30px;
  line-height: 30px;
}
.workspace .file-tabs .el-tabs__item.is-active,
.workspace .file-tabs .el-tabs__item:hover {
  color: #008992;
}
.workspace .el-transfer-panel {
  width: 375px;
}
.workspace
  .el-transfer-panel
  .el-transfer-panel__item.el-checkbox
  .el-checkbox__label {
  overflow: visible;
}
.workspace .el-transfer__buttons {
  padding: 0 10px;
}

.workspace .terminal {
  position: fixed;
  background-color: #ffffff;
  z-index: 10;
  top: 110px;
  left: 0px;
  right: 0px;
  bottom: 0px;
  -webkit-box-shadow: 0px 0px 10px #ddd;
  box-shadow: 0px 0px 10px #ddd;
}
.workspace .terminal-btn {
  position: fixed;
  z-index: 0;
  top: 70px;
  right: 70px;
}

.workspace .terminal-tabs .el-tabs__nav-scroll {
  padding-left: 10px !important;
  padding-right: 10px !important;
}
.workspace .terminal-tabs .el-tabs__item.is-active,
.workspace .terminal-tabs .el-tabs__item:hover {
  color: #008992;
}
.workspace .terminal-tabs .el-tabs__active-bar {
  background-color: #008992;
}
.workspace .terminal-tabs .el-tabs__content {
  position: absolute;
  left: 0px;
  top: 70px;
  right: 0px;
  bottom: 0px;
}
.workspace .terminal-tabs .el-tab-pane {
  position: absolute;
  width: 100%;
  height: 100%;
}
.workspace .terminal-tabs .log-box {
  position: absolute;
  left: 0px;
  top: 30px;
  right: 0px;
  bottom: 0px;
  overflow-y: auto;
  background-color: #3a3a3a;
  color: #ffffff;
  font-size: 13px;
  line-height: 18px;
  padding: 10px;
}

.workspace .runner {
  position: fixed;
  background-color: #ffffff;
  z-index: 10;
  top: 110px;
  left: 0px;
  right: 0px;
  bottom: 0px;
  -webkit-box-shadow: 0px 0px 10px #ddd;
  box-shadow: 0px 0px 10px #ddd;
}
.workspace .runner-btn {
  position: fixed;
  z-index: 0;
  top: 70px;
  right: 20px;
}

.workspace .runner-tabs .el-tabs__item {
  padding-left: 10px !important;
  padding-right: 10px !important;
}
.workspace .runner-tabs .el-tabs__item.is-active,
.workspace .runner-tabs .el-tabs__item:hover {
  color: #008992;
}
.workspace .runner-tabs .el-tabs__active-bar {
  background-color: #008992;
}
.workspace .runner-tabs .el-tabs__content {
  position: absolute;
  left: 0px;
  top: 70px;
  right: 0px;
  bottom: 0px;
}
.workspace .runner-tabs .el-tab-pane {
  position: absolute;
  width: 100%;
  height: 100%;
}
.workspace .log-box {
  position: absolute;
  left: 0px;
  top: 30px;
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
