
<script>
import tool from "@/common/js";
import http from "@/common/js/service";

export default {
  data: {
    activeRunnerTab: "",
    show_runner: false,
    runner_tabs: []
  },
  clickRunnerBtn() {
    if (this.show_runner) {
      this.show_runner = false;
    } else {
      this.show_runner = true;
      if (this.show_terminal) {
        this.show_terminal = false;
      }
    }
  },
  loadRunners() {
    var that = this;
    var data = {};
    this.runner_tabs.splice(0, this.runner_tabs.length);
    http.load("RUNNERS", data).then(result => {
      var value = result.value || [];
      var tabs = [];
      value.forEach(one => {
        var runner = {};
        runner.id = one.token;
        runner.title = one.projectPath + "（" + one.runName + "）";
        runner.data = one;
        runner.logs = [];
        this.runner_tabs.push(runner);
      });
      var activeRunnerTab = null;
      this.runner_tabs.forEach(runner => {
        if (runner.id == this.activeRunnerTab) {
          activeRunnerTab = runner.id;
        }
        this.readRunnerLog(runner);
        this.loadRunnerStatus(runner);
      });
      if (activeRunnerTab == null && this.runner_tabs.length > 0) {
        activeRunnerTab = this.runner_tabs[this.runner_tabs.length - 1].id;
      }
      this.activeRunnerTab = activeRunnerTab;
    });
  },
  loadRunnerStatus(runner) {
    if (runner.removed) {
      return;
    }
    var that = this;
    if (!this.show_runner || this.activeRunnerTab != runner.id) {
      window.setTimeout(function() {
        that.loadRunnerStatus(runner);
      }, 1000);
      return;
    }
    if (runner.data == null) {
      return;
    }
    var data = {};
    data.token = runner.data.token;
    http.load("RUNNER_STATUS", data).then(res => {
      var result = res.value || {};
      runner.data.status = result.status;

      window.setTimeout(function() {
        that.loadRunnerStatus(runner);
      }, 1000);
    });
  },
  readRunnerLog(runner) {
    if (runner.removed) {
      return;
    }
    var that = this;
    if (!this.show_runner || this.activeRunnerTab != runner.id) {
      window.setTimeout(function() {
        that.readRunnerLog(runner);
      }, 1000);
      return;
    }
    var data = {};
    let type = "LOG";
    if (runner.data) {
      data.token = runner.data.token;
    }
    data.lastIndex = runner.lastIndex;
    data.lastLine = runner.lastLine;

    let $box = $("#log-box-runner-" + runner.id);
    let needScroll = false;
    let scrollHeight = $box.prop("scrollHeight");
    let scrollTop = $box.scrollTop();
    let height = $box.outerHeight();
    if (scrollHeight == scrollTop + height) {
      needScroll = true;
    } else {
      if (scrollHeight <= height) {
        needScroll = true;
      }
    }

    http.load("RUNNER_LOG", data).then(res => {
      var result = res.value || {};
      var time = 200;
      if (coos.isEmpty(result.lastLine)) {
        time = 5000;
      } else {
        runner.lastIndex = result.lastIndex;
        runner.lastLine = result.lastLine;
      }
      var lines = result.lines || [];
      lines.forEach(line => {
        var log = {};
        log.msg = line;
        if (line) {
          if (line.indexOf(">>] [<<") > 0) {
            var groups = line.split(">>] [<<");
            if (groups.length > 0) {
              log.time = groups[0].replace(">>]", "").replace("[<<", "");
            }
            if (groups.length > 1) {
              log.thread = groups[1].replace(">>]", "").replace("[<<", "");
            }
            if (groups.length > 2) {
              log.level = groups[2].replace(">>]", "").replace("[<<", "");
            }
            if (groups.length > 3) {
              log.msg = groups[3].replace(">>]", "").replace("[<<", "");
            }
          }
        }
        runner.logs.push(log);
      });

      that.$nextTick(function() {
        if (needScroll) {
          $box.animate(
            {
              scrollTop: $box.prop("scrollHeight")
            },
            150
          );
        }
      });

      window.setTimeout(function() {
        that.readRunnerLog(runner);
      }, time);
    });
  },
  runnerStop(runner, callback) {
    if (runner.data == null) {
      return;
    }
    var that = this;
    var data = {};
    data.token = runner.data.token;
    http.work("RUNNER_STOP", data).then(result => {
      var value = result.value;
      callback && callback(result);
    });
  },
  runnerStart(runner, callback) {
    if (runner.data == null) {
      return;
    }
    var that = this;
    var data = {};
    data.token = runner.data.token;
    http.work("RUNNER_START", data).then(result => {
      var value = result.value;
      callback && callback(result);
    });
  },
  runnerDeploy(runner, callback) {
    if (runner.data == null) {
      return;
    }
    var that = this;
    var data = {};
    data.token = runner.data.token;
    http.work("RUNNER_DEPLOY", data).then(result => {
      var value = result.value;
      callback && callback(result);
    });
  },
  runnerRemove(runner, callback) {
    if (runner.data == null) {
      return;
    }
    var that = this;
    if (runner.data.status != "STOPPED") {
      coos.error("该运行控制台未停止，请先停止该控制台！");
      return;
    }

    coos
      .confirm("确定删除运行控制台，删除后将无法恢复？")
      .then(() => {
        var data = {};
        data.token = runner.data.token;
        http.work("RUNNER_REMOVE", data).then(result => {
          var value = result.value;
          runner.removed = true;
          that.removeRunnerTab(runner.id);
          callback && callback(result);
        });
      })
      .catch(() => {});
  },
  runnerLogClean(runner, callback) {
    var that = this;
    var data = {};
    if (runner.data) {
      data.token = runner.data.token;
    }
    http.work("RUNNER_LOG_CLEAN", data).then(result => {
      var value = result.value;
      runner.logs.splice(0, runner.logs.length);
      callback && callback(result);
    });
  },
  removeRunnerTab(targetName) {
    let tabs = this.runner_tabs;
    let activeName = this.activeRunnerTab;
    if (activeName === targetName) {
      activeName = null;
      tabs.forEach((t, index) => {
        if (t.id === targetName) {
          let nextTab = tabs[index + 1] || tabs[index - 1];
          if (nextTab) {
            activeName = nextTab.id;
          }
        }
      });
    }
    this.activeRunnerTab = activeName;
    this.runner_tabs = tabs.filter(tab => tab.id !== targetName);
    if (this.runner_tabs.length == 0) {
      this.show_runner = false;
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style  >
</style>
