
<script>
import tool from "@/common/js";
import http from "@/common/js/service";

export default {
  data: {
    activeTerminalTab: "",
    show_terminal: false,
    terminal_tabs: []
  },
  clickTerminalBtn() {
    if (this.show_terminal) {
      this.show_terminal = false;
    } else {
      this.show_terminal = true;
      if (this.show_runner) {
        this.show_runner = false;
      }
    }
  },
  loadTerminals() {
    var that = this;
    var data = {};
    this.terminal_tabs.splice(0, this.terminal_tabs.length);
    let terminal = { id: "1", title: "Terminal", logs: [] };
    this.terminal_tabs.push(terminal);
    http.load("TERMINALS", data).then(result => {
      var value = result.value || [];
      var tabs = [];
      value.forEach(one => {
        var terminal = {};
        terminal.id = one.token;
        terminal.title = one.projectPath + "（" + one.runName + "）";
        terminal.data = one;
        terminal.logs = [];
        this.terminal_tabs.push(terminal);
      });
      var activeTerminalTab = null;
      this.terminal_tabs.forEach(terminal => {
        if (terminal.id == this.activeTerminalTab) {
          activeTerminalTab = terminal.id;
        }
        this.readTerminalLog(terminal);
        if (terminal.data) {
          this.loadTerminalStatus(terminal);
        }
      });
      if (activeTerminalTab == null && this.terminal_tabs.length > 0) {
        activeTerminalTab = this.terminal_tabs[this.terminal_tabs.length - 1]
          .id;
      }
      this.activeTerminalTab = activeTerminalTab;
    });
  },
  loadTerminalStatus(terminal) {
    if (terminal.removed) {
      return;
    }
    var that = this;
    if (
      !this.show_terminal ||
      this.activeTerminalTab != terminal.id ||
      terminal.removing ||
      terminal.removed
    ) {
      window.setTimeout(function() {
        that.loadTerminalStatus(terminal);
      }, 1000);
      return;
    }
    if (terminal.data == null) {
      return;
    }
    var data = {};
    data.token = terminal.data.token;
    http.load("TERMINAL_STATUS", data).then(res => {
      var result = res.value || {};
      terminal.data.status = result.status;

      window.setTimeout(function() {
        that.loadTerminalStatus(terminal);
      }, 1000);
    });
  },
  readTerminalLog(terminal) {
    if (terminal.removed) {
      return;
    }
    var that = this;
    if (
      !this.show_terminal ||
      this.activeTerminalTab != terminal.id ||
      terminal.removing
    ) {
      window.setTimeout(function() {
        that.readTerminalLog(terminal);
      }, 1000);
      return;
    }
    var data = {};
    let type = "LOG";
    if (terminal.data) {
      data.token = terminal.data.token;
    }
    data.lastIndex = terminal.lastIndex;
    data.lastLine = terminal.lastLine;

    let $box = $("#log-box-terminal-" + terminal.id);
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

    http.load("TERMINAL_LOG", data).then(res => {
      var result = res.value || {};
      var time = 200;
      if (coos.isEmpty(result.lastLine)) {
        time = 5000;
      } else {
        terminal.lastIndex = result.lastIndex;
        terminal.lastLine = result.lastLine;
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
        terminal.logs.push(log);
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
        that.readTerminalLog(terminal);
      }, time);
    });
  },
  terminalStop(terminal, callback) {
    if (terminal.data == null) {
      return;
    }
    var that = this;
    var data = {};
    data.token = terminal.data.token;
    http.work("TERMINAL_STOP", data).then(result => {
      var value = result.value;
      callback && callback(result);
    });
  },
  terminalStart(terminal, callback) {
    if (terminal.data == null) {
      return;
    }
    var that = this;
    var data = {};
    data.token = terminal.data.token;
    http.work("TERMINAL_START", data).then(result => {
      var value = result.value;
      callback && callback(result);
    });
  },
  terminalRemove(terminal, callback) {
    if (terminal.data == null) {
      return;
    }
    var that = this;
    if (terminal.data.status != "STOPPED") {
      coos.error("该运行控制台未停止，请先停止该控制台！");
      return;
    }
    terminal.removing = true;
    coos
      .confirm("确定删除运行控制台，删除后将无法恢复？")
      .then(() => {
        var data = {};
        data.token = terminal.data.token;
        http.work("TERMINAL_REMOVE", data).then(result => {
          var value = result.value;
          terminal.removed = true;
          terminal.removing = false;
          that.removeTerminalTab(terminal.id);
          callback && callback(result);
        });
      })
      .catch(() => {
        terminal.removing = false;
      });
  },
  terminalLogClean(terminal, callback) {
    var that = this;
    var data = {};
    if (terminal.data) {
      data.token = terminal.data.token;
    }
    http.work("TERMINAL_LOG_CLEAN", data).then(result => {
      var value = result.value;
      terminal.logs.splice(0, terminal.logs.length);
      callback && callback(result);
    });
  },
  removeTerminalTab(targetName) {
    let tabs = this.terminal_tabs;
    let activeName = this.activeTerminalTab;
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
    this.activeTerminalTab = activeName;
    this.terminal_tabs = tabs.filter(tab => tab.id !== targetName);
    if (this.terminal_tabs.length == 0) {
      this.show_terminal = false;
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style  >
</style>
