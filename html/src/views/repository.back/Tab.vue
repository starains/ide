
<script>
import tool from "@/common/js";
import http from "@/common/js/service";

export default {
  data: {
    activeTab: null,
    tabs: [],
    tab_map: {}
  },
  closeTabByPath(path) {
    this.removeTab(path);
  },
  closeOtherTab(path) {
    let tabs = this.tabs;
    tabs.forEach(tab => {
      if (tab.path != path) {
        this.removeTab(tab.path);
      }
    });
  },
  closeAllTab(path) {
    let tabs = this.tabs;
    tabs.forEach(tab => {
      this.removeTab(tab.path);
    });
  },
  removeTab(targetName) {
    let tabs = this.tabs;
    let tab = this.tab_map[targetName];
    if (!tab) {
      return;
    }
    let activeName = this.activeTab;
    if (activeName === targetName) {
      activeName = null;
      tabs.forEach((t, index) => {
        if (t.path === targetName) {
          let nextTab = tabs[index + 1] || tabs[index - 1];
          if (nextTab) {
            activeName = nextTab.path;
          }
        }
      });
    }
    let that = this;
    function call() {
      tab.$view.remove();
      tab.changed = false;
      tab.loaded = false;
      delete that.tab_map[targetName];

      let index = that.open_file_keys.indexOf(targetName);
      if (index >= 0) {
        that.open_file_keys.splice(index, 1);
      }

      that.activeTab = activeName;
      that.tabs = tabs.filter(tab => tab.path !== targetName);
      http.work("FILE_CLOSE", { path: targetName }).then(result => {});
    }
    if (tab.changed) {
      coos.confirm(
        '文件<span class="pdlr-5 color-green">' +
          tab.name +
          "</span>未保存，是否保存？",
        function() {
          tab.editor.toSave();
          call();
        },
        function() {
          call();
        }
      );
    } else {
      call();
    }
  },
  addTab(file) {
    let tab = this.tab_map[file.path];
    if (!tab) {
      tab = { name: file.name, path: file.path };
      tab.file = file;
      tab.changed = false;
      tab.loaded = false;
      this.tab_map[file.path] = tab;
      tab.$view = $('<div class="file-editor-one"/>');
      tab.$view.attr("path", tab.path);

      $(".file-editor-box").append(tab.$view);

      tab.onLoad = function(res) {
        if (!tab.loaded) {
          let project = that.findProjectByPath(file.path);
          tab.loaded = true;
          tab.content = res.content;
          if (project.app && project.app.context) {
            tab.context = project.app.context;
          }
          let type = null;
          let model = project.getModelByPath(file.path);
          let localpath = null;
          if (model) {
            localpath = project.app.localpath;
            type = model.MODEL_TYPE;
          }

          tab.editor = coos.editor({
            type: type,
            model: model,
            project: project,
            tab: tab,
            file: tab.file,
            context: this.context,
            readyonly: !that.hasPermission(),
            source: source,
            onSave(content) {
              tab.save(content);
            },
            onTest(data, callback) {
              data = data || {};
              data.type = type;
              data.path = localpath;
              data.name = model.name;
              http.data.doTest(data).then(result => {
                var value = result.value;
                callback && callback(result);
              });
            },
            toText(data, callback) {
              http.data.toText(data).then(result => {
                var value = result.value;
                callback && callback(result);
              });
            },
            toModel(data, callback) {
              http.data.toModel(data).then(result => {
                var value = result.value;
                callback && callback(result);
              });
            }
          });
          tab.editor.build(tab.$view);
        } else {
        }
      };
      let that = this;

      tab.change = function(content) {
        if (content != tab.content) {
          tab.changed = true;
        } else {
          tab.changed = false;
        }
      };
      tab.save = function(content) {
        tab.content = content;
        that.saveFile(tab);
      };
      this.tabs.push(tab);
    }
    this.activeTab = file.path;
    return tab;
  },
  clickTab(vTab) {
    if (this.tab_map[vTab.name]) {
      this.openFile(this.tab_map[vTab.name]);
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style  >
</style>
