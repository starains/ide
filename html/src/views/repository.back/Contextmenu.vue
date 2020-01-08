
<script>
import tool from "@/common/js";
import http from "@/common/js/service";

export default {
  getContextMenus(event, data) {
    var that = this;
    var menus = [];
    var createMenus = [];
    if (data.isProject || data.isDirectory) {
      if (this.hasPermission()) {
        menus.push({
          text: "新建文件",
          onClick: function() {
            that.toCreateFile(data);
          }
        });
      }
    }
    if (data.isProject) {
      /* if (data.isMaven && data.packaging != "pom") {
        menus.push({
          text: "新建",
          subs: createMenus
        });
        createMenus.push({
          text: "页面",
          onClick: function() {}
        });
        createMenus.push({
          text: "控制器",
          onClick: function() {}
        });
        createMenus.push({
          text: "服务",
          onClick: function() {}
        });
        createMenus.push({
          text: "表",
          onClick: function() {}
        });
        createMenus.push({
          text: "库",
          onClick: function() {}
        });
        createMenus.push({
          text: "插件",
          onClick: function() {}
        });
      } */

      if (this.hasPermission()) {
        if (data.packaging != "pom") {
          if (!data.runOptions || data.runOptions.length == 0) {
            menus.push({
              text: "运行配置",
              onClick: function() {
                that.toSetRunOption(data);
              }
            });
          } else {
            let runs = [];
            data.runOptions.forEach(runOption => {
              runs.push({
                text: runOption.name,
                subs: [
                  {
                    text: "Start",
                    onClick: function() {
                      that.runOptionStart(data, runOption);
                    }
                  },
                  {
                    text: "修改配置",
                    onClick: function() {
                      that.toSetRunOption(data, runOption);
                    }
                  },
                  {
                    text: "删除配置",
                    onClick: function() {
                      that.toDeleteRunOption(data, runOption);
                    }
                  }
                ]
              });
            });
            runs.push({
              text: "添加配置",
              onClick: function() {
                that.toSetRunOption(data);
              }
            });
            menus.push({
              text: "运行",
              subs: runs
            });
          }
        }

        if (!data.runnerOptions || data.runnerOptions.length == 0) {
          menus.push({
            text: "远程部署配置",
            onClick: function() {
              that.toSetRunnerOption(data);
            }
          });
        } else {
          let runners = [];
          data.runnerOptions.forEach(runnerOption => {
            runners.push({
              text: runnerOption.name,
              subs: [
                {
                  text: "部署",
                  onClick: function() {
                    that.runnerOptionDeploy(data, runnerOption);
                  }
                },
                {
                  text: "修改配置",
                  onClick: function() {
                    that.toSetRunnerOption(data, runnerOption);
                  }
                },
                {
                  text: "删除配置",
                  onClick: function() {
                    that.toDeleteRunnerOption(data, runnerOption);
                  }
                }
              ]
            });
          });
          runners.push({
            text: "添加配置",
            onClick: function() {
              that.toSetRunnerOption(data);
            }
          });
          menus.push({
            text: "远程部署",
            subs: runners
          });
        }
      }

      menus.push({
        text: "刷新",
        onClick: function() {
          that.reloadProject(data);
        }
      });

      if (this.hasPermission()) {
        var menu = {
          text: "GIT",
          subs: []
        };
        menus.push(menu);

        if (that.git && that.git.option) {
          var pulls = [];

          if (
            that.git.option.gitRemoteName &&
            that.git.option.gitRemoteBranch
          ) {
            pulls.push({
              text:
                that.git.option.gitRemoteName +
                "/" +
                that.git.option.gitRemoteBranch,
              onClick: function() {
                that.gitPull(
                  {
                    gitRemote: that.git.option.gitRemoteName,
                    gitRemoteBranchName: that.git.option.gitRemoteBranch
                  },
                  function() {
                    that.load();
                  }
                );
              }
            });
          }
          if (pulls.length > 0) {
            menu.subs.push({
              text: "pull（拉取）",
              subs: pulls
            });
          }
          if (this.git.findGit) {
            menu.subs.push({
              text: "push（推送）",
              onClick: function() {
                that.toGitPush();
              }
            });
            menu.subs.push({
              text: "还原",
              onClick: function() {
                that.gitRevert([data.path], function(res) {
                  console.log(res);
                });
              }
            });
          }
          menu.subs.push({
            text: "设置",
            onClick: function() {
              that.toCreateGitRemote();
            }
          });
        } else {
          menu.subs.push({
            text: "设置",
            onClick: function() {
              that.toCreateGitRemote();
            }
          });
        }
        if (data.isMaven) {
          let mavenSubs = [];
          menus.push({
            text: "MAVEN",
            subs: mavenSubs
          });
          mavenSubs.push({
            text: "clean",
            onClick: function() {
              that.mavenClean(data);
            }
          });
          mavenSubs.push({
            text: "compile",
            onClick: function() {
              that.mavenCompile(data);
            }
          });
          mavenSubs.push({
            text: "package",
            onClick: function() {
              that.mavenPackage(data);
            }
          });
          mavenSubs.push({
            text: "install",
            onClick: function() {
              that.mavenInstall(data);
            }
          });
          mavenSubs.push({
            text: "deploy",
            onClick: function() {
              that.mavenDeploy(data);
            }
          });
        }
      }
    }
    if (data.isFile) {
      if (this.hasPermission()) {
        if (that.git) {
          menus.push({
            text: "还原",
            onClick: function() {
              that.gitRevert([data.path], function(res) {
                console.log(res);
              });
            }
          });
        }
      }
      menus.push({
        text: "下载",
        onClick: function() {
          that.toDownloadFile(data);
        }
      });
      if (this.hasPermission()) {
        menus.push({
          text: "重命名",
          onClick: function() {
            that.toRenameFile(data);
          }
        });
      }
    }

    if (data.isDirectory || data.isFile) {
      if (this.hasPermission()) {
        menus.push({
          text: "删除",
          onClick: function() {
            that.deleteFile(data);
          }
        });
      }
    }
    if (data.isProject && data.isMaven) {
    }
    return menus;
  },
  showFileTabContextmenu(path) {
    var $contextmenuBox = $(".contextmenu-box");
    var fadeSpeed = 100;
    var menus = [];
    let that = this;

    menus.push({
      text: "关闭",
      onClick: function() {
        that.closeTabByPath(path);
      }
    });
    menus.push({
      text: "关闭其他",
      onClick: function() {
        that.closeOtherTab(path);
      }
    });
    menus.push({
      text: "全部关闭",
      onClick: function() {
        that.closeAllTab(path);
      }
    });
    var $contextmenu = app.contextmenu.createMenu(menus);
    this.$contextmenu = $contextmenu;
    $contextmenuBox.empty();
    $contextmenuBox.append($contextmenu);

    $contextmenu.removeClass("coos-contextmenu-up");
    var autoH = $contextmenu.height() + 12;
    var e = window.event;
    if (e.pageY + autoH > $("html").height()) {
      $contextmenu
        .addClass("coos-contextmenu-up")
        .css({
          top: e.pageY - 20 - autoH,
          left: e.pageX - 13
        })
        .fadeIn(fadeSpeed);
    } else {
      $contextmenu
        .css({
          top: e.pageY + 10,
          left: e.pageX - 13
        })
        .fadeIn(fadeSpeed);
    }
    e.preventDefault();
    e.stopPropagation();
  },
  showContextMenu(event, data) {
    var $contextmenuBox = $(".contextmenu-box");
    var fadeSpeed = 100;
    var menus = this.getContextMenus(event, data);
    if (menus.length == 0) {
      return;
    }

    var $contextmenu = app.contextmenu.createMenu(menus);
    this.$contextmenu = $contextmenu;
    $contextmenuBox.empty();
    $contextmenuBox.append($contextmenu);

    $contextmenu.removeClass("coos-contextmenu-up");
    var autoH = $contextmenu.height() + 12;
    var e = window.event;
    if (e.pageY + autoH > $("html").height()) {
      $contextmenu
        .addClass("coos-contextmenu-up")
        .css({
          top: e.pageY - 20 - autoH,
          left: e.pageX - 13
        })
        .fadeIn(fadeSpeed);
    } else {
      $contextmenu
        .css({
          top: e.pageY + 10,
          left: e.pageX - 13
        })
        .fadeIn(fadeSpeed);
    }
    e.preventDefault();
    e.stopPropagation();
  },
  destroyContextmenu() {
    var that = this;
    if (this.$contextmenu) {
      this.$contextmenu.fadeOut(100, function() {
        that.$contextmenu
          .css({
            display: ""
          })
          .find(".drop-left")
          .removeClass("drop-left");
      });
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style  >
</style>
