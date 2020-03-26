<template>
  <div class="repository-project-box" :class="{'to-rename':toRename}">
    <div class="repository-project-header">
      <div class="repository-project-nav-group">
        <a class title="刷新" @click="reloadRepository">
          <i
            class="coos-icon"
            style="font-size:16px;"
            :class="{' el-icon-loading ' : repository.loading,' coos-icon-reload ' : !repository.loading }"
          />
        </a>
        <a class title="上传" @click="source.uploadRepository()">
          <i class="coos-icon coos-icon-cloud-upload" />
        </a>
        <a class title="下载" @click="source.downloadRepository()">
          <i class="coos-icon coos-icon-cloud-download" />
        </a>
        <a class="repository-project-header-line"></a>
        <template v-for="(nav,index) in repository.navs">
          <a class :key="index" :class="{'coos-disabled' : nav.disabled}" @click="nav.onClick()">
            <el-badge
              :value="nav.value>0?nav.value:''"
              :title="nav.title"
              :is-dot="nav.dot"
              :type="nav.type"
            >
              <a class="coos-icon" :class="nav.icon" :style="nav.icon_style"></a>
            </el-badge>
          </a>
        </template>
      </div>
      <div class="repository-project-nav-group">
        <a class title="控制台，可查看启动项目日志" @click="source.openStarterBox()">
          <el-badge :is-dot="repository.starters.length >1">
            <a
              class="coos-icon coos-icon-terminal"
              :class="repository.starter_show?'color-green':''"
            ></a>
          </el-badge>
        </a>
      </div>
    </div>
    <el-input placeholder="输入关键字进行过滤" size="mini" v-model="searchText"></el-input>
    <div class="repository-project-tree-box">
      <div class="repository-project-tree">
        <el-tree
          ref="tree"
          draggable
          :data="repository.projects"
          :props="defaultProps"
          @node-click="nodeClick"
          @node-contextmenu="nodeContextmenu"
          :render-content="renderContent"
          :expand-on-click-node="true"
          node-key="path"
          :default-expanded-keys="repository.open_folders"
          :filter-node-method="filterNode"
          :indent="16"
          :highlight-current="true"
          @node-expand="nodeExpand"
          @node-collapse="nodeCollapse"
          @node-drop="nodeDrop"
        ></el-tree>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  components: {},
  props: ["repository"],
  data() {
    return {
      defaultProps: {
        children: "files",
        label: "name"
      },
      searchText: null,
      toRename: false,
      source: source
    };
  },
  beforeMount() {},
  watch: {
    searchText(val) {
      this.searchRepository(val);
    }
  },
  methods: {
    reloadRepository() {
      source.loadRepository();
    },
    searchRepository(value) {
      this.$refs.tree.filter(value);
      if (coos.isEmpty(value)) {
        let folders = [];
        this.repository.open_folders.forEach(one => {
          folders.push(one);
        });
        this.repository.open_folders.splice(
          0,
          this.repository.open_folders.length
        );
        folders.forEach(one => {
          this.repository.open_folders.push(one);
        });
      } else {
      }
    },
    //优化之后的代码 不管有几级都可以适用,不过用了递归，量太大还是会崩溃，这个后续需要优化
    filterNode(value, data, node) {
      if (value == null || value == "") {
        node.expanded = false;
        return true;
      }
      value = ("" + value).toLowerCase();

      let name = node.data.name.toLowerCase();
      let isPass = name.indexOf(value) !== -1;
      return isPass;

      let level = node.level;
      let _array = []; //这里使用数组存储 只是为了存储值。
      this.getReturnNode(node, _array, value);
      let result = false;
      _array.forEach(item => {
        result = result || item;
      });
      return result;
    },
    nodeContextmenu(event, data) {
      let that = this;
      event = event || window.event;
      let menus = [];
      if (data.isProject) {
        menus.push({
          header: "应用模型"
        });
        let appModelMenu = { text: "应用模型", menus: [] };
        menus.push(appModelMenu);
        appModelMenu.menus.push({
          header: "应用模型"
        });

        if (data.app != null) {
          if (data.app.context.JAVA == null) {
            appModelMenu.menus.push({
              text: "请在模型目录创建java文件，配置源码相关设置",
              onClick() {}
            });
          } else {
            appModelMenu.menus.push({
              text: "生成源码",
              onClick() {
                source.appGenerateSourceCode(data);
              }
            });
          }
          appModelMenu.menus.push({
            text: "生成库表",
            onClick() {
              let param = {};
              param.path = data.app.localpath;
              param.type = "DATABASE";
              source.service.data.doTest(param).then(result => {
                if (result.errcode == 0) {
                  coos.success("库表创建成功！");
                } else {
                  coos.error(result.errmsg);
                }
              });
            }
          });
          appModelMenu.menus.push({
            text: "修改配置",
            onClick() {
              source.appOptionForm.show(data.path, data.app.option);
            }
          });
          appModelMenu.menus.push({
            text: "删除",
            onClick() {
              source.do("APP_DELETE_OPTION", {
                path: data.path
              });
            }
          });
        } else {
          appModelMenu.menus.push({
            text: "配置",
            onClick() {
              source.appOptionForm.show(data.path);
            }
          });
        }

        menus.push({
          header: "项目"
        });
        let starterMenu = { text: "项目部署运行", menus: [] };
        menus.push(starterMenu);
        starterMenu.menus.push({
          header: "项目部署运行"
        });

        source.repository.starter_options.forEach(one => {
          if (
            (coos.isEmpty(one.path) && coos.isEmpty(data.path)) ||
            one.path == data.path
          ) {
            let option = {};
            if (!coos.isEmpty(one.option)) {
              option = JSON.parse(one.option);
            }
            let oMenu = {
              text: one.name,
              menus: []
            };
            starterMenu.menus.push(oMenu);

            let startting = false;
            source.repository.starters.forEach(s => {
              if (s && s.option && s.option.name == one.name) {
                startting = true;
              }
            });
            if (startting) {
              oMenu.menus.push({
                text: "已部署，打开控制台",
                onClick() {
                  source.openStarterBox();
                }
              });
            } else {
              oMenu.menus.push({
                text: "部署",
                onClick() {
                  source.starterDeploy(data.path, option);
                }
              });
            }
            oMenu.menus.push({
              text: "修改配置",
              onClick() {
                source.starterForm.show(data.path, option);
              }
            });
            oMenu.menus.push({
              text: "删除配置",
              onClick() {
                source
                  .do("DELETE_STARTER_OPTION", {
                    path: data.path,
                    option: option
                  })
                  .then(res => {
                    source.load("STARTER_OPTIONS");
                  });
              }
            });
          }
        });
        starterMenu.menus.push({
          header: "运行配置"
        });
        starterMenu.menus.push({
          text: "添加配置",
          onClick() {
            source.starterForm.show(data.path);
          }
        });

        if (data.isMaven) {
          let mavenSubs = [];
          menus.push({
            text: "MAVEN",
            menus: mavenSubs
          });
          mavenSubs.push({
            text: "clean",
            onClick: function() {
              source.mavenClean(data);
            }
          });
          mavenSubs.push({
            text: "compile",
            onClick: function() {
              source.mavenCompile(data);
            }
          });
          mavenSubs.push({
            text: "package",
            onClick: function() {
              source.mavenPackage(data);
            }
          });
          mavenSubs.push({
            text: "install",
            onClick: function() {
              source.mavenInstall(data);
            }
          });
          mavenSubs.push({
            text: "deploy",
            onClick: function() {
              source.mavenDeploy(data);
            }
          });
        }
      }
      let project = source.getProjectByPath(data.path);
      if (
        project &&
        project.app &&
        (project.app.path == data.path ||
          data.path.startsWith(project.app.path + "/"))
      ) {
        menus.push({
          header: "模型"
        });
        let model = source.getModelTypeByPath(data.path);
        if (project.app.path == data.path) {
          if (project.app.context.JAVA == null) {
            menus.push({
              text: "请在模型目录创建java文件，配置源码相关设置",
              onClick() {}
            });
          } else {
            menus.push({
              text: "生成源码",
              onClick() {
                source.appGenerateSourceCode(project);
              }
            });
          }
          menus.push({
            text: "生成库表",
            onClick() {
              let param = {};
              param.path = project.app.localpath;
              param.type = "DATABASE";
              source.service.data.doTest(param).then(result => {
                if (result.errcode == 0) {
                  coos.success("库表创建成功！");
                } else {
                  coos.error(result.errmsg);
                }
              });
            }
          });
        }

        if (model) {
          if (model.value == "TABLE") {
            let databases = [];
            databases.push("");

            databases.forEach(database => {
              menus.push({
                text: "导入" + database + "已有表",
                onClick() {
                  source.tableImportForm
                    .show(project.app, { databasename: database, parent: data })
                    .then(res => {});
                }
              });
            });
          }
        }
      }
      menus.push({
        header: "文件"
      });
      if (data.isProject || data.isDirectory) {
        menus.push({
          text: "新建文件",
          onClick() {
            source.openFolder(data.path);
            let file = {
              parentPath: data.path,
              path: data.path + "/" + coos.getNumber(),
              name: "",
              isNew: true,
              toRename: false,
              file: true
            };
            source.formatFile(file, data);
            data.files.push(file);
            that.rename(file);
          }
        });
        menus.push({
          text: "新建文件夹",
          onClick() {
            source.openFolder(data.path);
            let file = {
              parentPath: data.path,
              path: data.path + "/" + coos.getNumber(),
              name: "",
              isNew: true,
              toRename: false,
              directory: true,
              files: []
            };
            source.formatFile(file, data);
            data.files.push(file);
            that.rename(file);
          }
        });
      }
      if (!data.isProject) {
        menus.push({
          text: "重命名",
          onClick() {
            that.rename(data);
          }
        });
        menus.push({
          text: "删除",
          onClick() {
            that.remove(data);
          }
        });
      }

      if (data.isFile || data.isDirectory) {
        menus.push({
          text: "下载",
          onClick() {
            source.downloadFile(data.path);
          }
        });
      }
      if (data.isDirectory) {
        menus.push({
          text: "上传",
          onClick() {
            source.uploadRepository({ parent: data.path });
          }
        });
      }
      if (data.modified || data.untracked || data.conflicting) {
        menus.push({
          header: "Git"
        });
        menus.push({
          text: "还原",
          onClick() {
            source.gitRevert([data.path]);
          }
        });
      }

      if (data.isProject) {
        menus.push({
          text: "刷新",
          onClick() {
            source.reloadProject(data);
          }
        });
      }
      this.repository.contextmenu.menus = menus;
      this.repository.contextmenu.callShow(event);
      event.preventDefault();
    },
    nodeExpand(data, node) {
      source.openFolder(data.path);
    },
    nodeCollapse(data, node) {
      source.closeFolder(data.path);
    },
    nodeDrop(node, node2, place, event) {
      if (node2) {
        let newParent = null;
        if (place == "before" || place == "after") {
          newParent = node2.data.parent;
        } else if (place == "inner") {
          newParent = node2.data;
        }
        if (newParent == null) {
          return;
        }
        if (node.data.parent == newParent) {
          return;
        }
        let file = node.data;
        let path = file.path;
        let to = newParent.path;
        source.fileMove(file, newParent);
        source
          .do("FILE_MOVE", {
            path: path,
            to: to
          })
          .then(res => {
            if (res.errcode == 0) {
              coos.success("移动成功！");
            } else {
              coos.error(res.errmsg);
            }
          });
      }
    },
    nodeClick(data, node) {
      this.repository.contextmenu.show = false;
      if (window.event) {
        window.event.preventDefault();
        window.event.stopPropagation();
        return;
      }
      let json = {};
      json.type = "COPY_FILE";
      json.file = {
        name: data.name,
        path: data.path,
        isFile: data.isFile,
        length: data.length
      };
      app.clipboardSelect(JSON.stringify(json));
    },
    reloadProject(data) {
      source.reloadProject(data);
      if (window.event) {
        window.event.preventDefault();
        window.event.stopPropagation();
      }
    },
    openFile(file) {
      source.openFileByPath(file.path);
    },
    remove(file) {
      if (file == null) {
        return;
      }
      if (file.parent == null) {
        return;
      }
      source
        .do("FILE_DELETE", {
          path: file.path
        })
        .then(res => {
          if (res.errcode == 0) {
            if (file.isNew) {
            } else {
              coos.success("删除成功！");
            }
            let index = file.parent.files.indexOf(file);
            file.parent.files.splice(index, 1);
          } else {
            coos.error(res.errmsg);
          }
        });
    },
    rename(file) {
      if (file != null) {
        this.toRename = true;
        file.toRename = true;
        window.setTimeout(() => {
          let $input = $(this.$el).find("[id='input-" + file.path + "']");
          $input.focus();
        }, 10);
      }
    },
    clickInput(file) {
      if (window.event) {
        window.event.preventDefault();
        window.event.stopPropagation();
      }
    },
    doRename(file, new_name) {},
    changeInput(file) {},
    blurInput(file) {
      let $input = $(this.$el).find("[id='input-" + file.path + "']");
      let new_name = $input.val();
      if (new_name != null) {
        new_name = new_name.trim();
      }
      if (coos.isEmpty(new_name)) {
        if (file.isNew) {
          file.toRename = false;
          this.toRename = false;
          this.remove(file);
          return;
        }
        coos.error("文件名称不能为空！");
        $input.focus();
        return;
      }

      if (file.name == new_name) {
        file.toRename = false;
        this.toRename = false;
        return;
      }
      if (file.parent) {
        let find = false;
        file.parent.files.forEach(f => {
          if (f != file && f.name == new_name) {
            find = true;
          }
        });
        if (find) {
          coos.error("文件名称已存在！");
          $input.focus();
          return;
        }
      }
      file.toRename = true;
      this.toRename = true;
      if (file.isNew) {
        let path = file.parentPath + "/" + new_name;
        if (coos.isEmpty(file.parentPath)) {
          path = new_name;
        }
        file.path = path;
        let date = {
          parentPath: file.parentPath,
          name: new_name,
          isFile: file.isFile,
          isDirectory: file.isDirectory
        };

        source.updateFileName(file, new_name);
        source.do("FILE_CREATE", date).then(res => {
          if (res.errcode == 0) {
            delete file.isNew;
            file.toRename = false;
            this.toRename = false;

            if (file.parent) {
              source.sortFolderFiles(file.parent);
            }
          } else {
            coos.error(res.errmsg);
          }
        });
      } else {
        let date = {
          path: file.path,
          name: new_name
        };

        source.updateFileName(file, new_name);
        source.do("FILE_RENAME", date).then(res => {
          if (res.errcode == 0) {
            file.toRename = false;
            this.toRename = false;

            if (file.parent) {
              source.sortFolderFiles(file.parent);
            }
          } else {
            coos.error(res.errmsg);
          }
        });
      }
    },
    renderContent(h, { node, data, store }) {
      if (data.isProject) {
        return (
          <span
            style="flex: 1; display: flex; align-items: center; justify-content: space-between; font-size: 14px; padding-right: 8px;"
            class={{ "rename-file": data.toRename }}
          >
            <span class="color-orange">
              <i class="coos-icon coos-icon-appstore mgr-5" />
              {data.modified || data.untracked || data.conflicting ? (
                <i
                  class="coos-icon mgr-5 coos-icon-right ft-12"
                  style="vertical-align: 1px;color: #bfbcbc;"
                />
              ) : (
                <i />
              )}

              <input
                v-show={data.toRename}
                value={data.name}
                id={"input-" + data.path}
                on-click={() => this.clickInput(data)}
                on-blur={() => this.blurInput(data)}
                on-change={() => this.changeInput(data)}
                on-keyup={() => this.keyup(data, event)}
              />
              <span v-show={!data.toRename}>{data.name}</span>
              {data.app != null ? (
                <i
                  class="mgl-10 ft-12"
                  style="vertical-align: 1px;color: #bfbcbc;"
                >
                  [应用模型]
                </i>
              ) : (
                <i />
              )}
              <span>
                <a
                  class="color-orange mgl-20 "
                  title="刷新"
                  on-click={() => this.reloadProject(data)}
                  style="vertical-align: -2px;"
                >
                  {data.loading ? (
                    <i class="coos-icon el-icon-loading " />
                  ) : (
                    <i class="coos-icon coos-icon-reload ft-14" />
                  )}
                </a>
              </span>
            </span>
          </span>
        );
      }
      if (data.isDirectory) {
        return (
          <span
            style="flex: 1; display: flex; align-items: center; justify-content: space-between; font-size: 14px; padding-right: 8px;"
            class={{ "rename-file": data.toRename }}
          >
            <span class="">
              <i class="coos-icon coos-icon-folder-fill mgr-5 color-orange" />
              {data.modified || data.untracked || data.conflicting ? (
                <i
                  class="coos-icon mgr-5 coos-icon-right ft-12"
                  style="vertical-align: 1px;color: #bfbcbc;"
                />
              ) : (
                <i />
              )}
              <input
                v-show={data.toRename}
                value={data.name}
                id={"input-" + data.path}
                on-click={() => this.clickInput(data)}
                on-blur={() => this.blurInput(data)}
                on-change={() => this.changeInput(data)}
                on-keyup={() => this.keyup(data, event)}
              />
              <span v-show={!data.toRename}>{data.name}</span>
            </span>
            <span />
          </span>
        );
      }
      return (
        <span
          style="flex: 1; display: flex; align-items: center; justify-content: space-between; font-size: 14px; padding-right: 8px;"
          class={{ "rename-file": data.toRename }}
          on-dblclick={() => this.openFile(data)}
        >
          <span class="">
            <i
              class={"coos-icon coos-icon-file mgr-5 coos-icon-" + data.type}
            />
            {data.modified || data.conflicting ? (
              <i
                class="coos-icon mgr-5 coos-icon-right ft-12"
                style="vertical-align: 1px;color: #bfbcbc;"
              />
            ) : (
              <i />
            )}
            {data.untracked ? (
              <i
                class="coos-icon mgr-5 coos-icon-plus ft-12"
                style="vertical-align: 1px;color: #bfbcbc;"
              />
            ) : (
              <i />
            )}
            <input
              v-show={data.toRename}
              value={data.name}
              id={"input-" + data.path}
              on-click={() => this.clickInput(data)}
              on-blur={() => this.blurInput(data)}
              on-change={() => this.changeInput(data)}
              on-keyup={() => this.keyup(data, event)}
            />
            <span v-show={!data.toRename}>{data.name}</span>
          </span>
          <span />
        </span>
      );
    },
    keyup(data, event) {
      event = event || window.event;
      //ESC
      if (event.keyCode == "27") {
        if (data != null) {
          this.toRename = false;
          data.toRename = false;
        }
      }
      //ESC
      if (event.keyCode == "13") {
        if (data != null) {
          this.blurInput(data);
        }
      }
    }
  },
  mounted() {
    document.addEventListener("paste", event => {
      if (event && event.target && event.target.id == "coos-for-copy-area") {
        app.clipboardGet(event, text => {
          this.onPaste(text);
        });
        event.preventDefault();
        event.stopPropagation();
      }
    });
    document.addEventListener("keyup", event => {
      //F2
      if (event.keyCode == "113") {
        let node = this.$refs.tree.getCurrentNode();
        if (node != null) {
          this.rename(node);
        }
      }
    });
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style  >
.repository-project-box {
  width: 100%;
  height: 100%;
}
.repository-project-header {
  padding: 0px;
  height: 35px;
}
.repository-project-nav-group > a {
  height: 35px;
  width: 35px;
  display: block;
  text-align: center;
  color: #9e9e9e;
  line-height: 35px;
  border: 0px;
  float: left;
  cursor: pointer;
}
.repository-project-nav-group > a .el-badge {
  vertical-align: inherit;
}
.repository-project-nav-group > a:hover {
  background-color: #ddd;
}
.repository-project-nav-group .repository-project-header-line {
  border: 0px;
  border-left: 1px solid #ddd;
  height: 21px;
  width: 0px;
  margin: 7px 5px;
}
.repository-project-nav-group > a .coos-icon {
  font-size: 20px;
}
.repository-project-nav-group > a .el-badge__content.is-fixed.is-dot {
  top: 7px;
}
.repository-project-nav-group .el-badge__content {
  height: 16px;
  line-height: 16px;
  min-width: 6px;
  padding: 0 5px;
}
.repository-project-box .el-input__inner {
  border-left: 0px;
  border-right: 0px;
  border-radius: 0px;
}
.repository-project-tree-box {
  position: absolute;
  top: 98px;
  left: 0px;
  bottom: 0px;
  right: 0px;
}
.repository-project-tree {
  overflow: auto;
  width: 100%;
  height: 100%;
}
.repository-project-tree:hover {
  overflow: auto;
}
.repository-project-tree .el-tree {
  min-width: 100%;
  background: transparent;
  padding-bottom: 100px;
}
.el-tree .el-tree-node__children {
  overflow: visible !important;
}
.el-tree .el-tree-node__children.v-leave-active,
.el-tree .el-tree-node__children.v-enter-active {
  overflow: hidden !important;
}
.repository-project-tree .el-tree-node__content input {
  border: 1px solid #008992;
  outline: none;
  height: 24px;
  vertical-align: 1px;
  background: #ffffff;
  margin-left: -5px;
  padding: 0px 5px;
  pointer-events: auto;
}
.repository-project-box.to-rename {
  background: #b5b5b5;
  pointer-events: none;
}
.repository-project-box.to-rename
  .el-tree--highlight-current
  .el-tree-node.is-current
  > .el-tree-node__content {
  background-color: #b5b5b5 !important;
}

.repository-project-box.to-rename .el-tree-node__content:hover,
.repository-project-box.to-rename .el-tree-node:focus > .el-tree-node__content {
  background-color: transparent !important;
}
</style>
