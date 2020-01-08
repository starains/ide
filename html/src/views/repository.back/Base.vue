
<script>
import tool from "@/common/js";
import http from "@/common/js/service";

export default {
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
  getReturnNode(node, _array, value) {
    let name = node.data.name.toLowerCase();
    let isPass = name.indexOf(value) !== -1;
    isPass ? _array.push(isPass) : "";
    this.index++;
    if (!isPass && node.level != 1 && node.parent) {
      this.getReturnNode(node.parent, _array, value);
    }
  },
  searchRepository(value) {
    this.$refs.tree.filter(value);
    if (coos.isEmpty(value)) {
      this.open_folder_keys.splice(0, this.open_folder_keys.length);
      for (let path in this.tab_map) {
        this.open_folder_keys.push(path);
      }
    } else {
    }
  },
  handleProjectNodeContextMenu($event, data) {
    this.showContextMenu($event, data);
  },
  handleProjectNodeClick(data, node) {
    let json = {};
    json.type = "COPY_FILE";
    json.file = {
      name: data.name,
      path: data.path,
      isFile: data.isFile,
      length: data.length
    };
    app.clipboardSelect(JSON.stringify(json));
    this.lastSelectFile = data;
    this.destroyContextmenu();
    return true;
  },
  onPaste(text) {
    if (coos.isEmpty(text)) {
      return;
    }
    try {
      let json = JSON.parse(text);
      if (json.type == "COPY_FILE" && json.file) {
        if (this.lastSelectFile) {
          this.pasteFile(json.file, this.lastSelectFile);
        }
      }
    } catch (e) {}
  },
  hasPermission(arg) {
    return app.hasPermission(arg);
  },
  load() {
    this.loading = true;
    this.loading_msg = "";
    http.load("FILE_OPENS", {}).then(result => {
      if (result.value) {
        result.value.forEach((file_open, index) => {
          let path = file_open.path;
          let folder = path;
          if (folder.indexOf("/") >= 0) {
            folder = folder.substring(0, folder.lastIndexOf("/"));
          }
          this.open_folder_keys.push(folder);
          this.open_file_keys.push(path);
        });
      }
      http.load("REPOSITORY", {}).then(result => {
        this.loading = false;
        var repository = result.value || {};
        this.git = repository.git;
        if (repository.git && repository.git.status) {
          let s = repository.git.status;
          for (let k in s) {
            this.file_status[k] = s[k];
          }
        }
        this.repository.projects.forEach(project => {
          this.removeFileByPath(project.path);
        });
        this.repository.projects.splice(0, this.repository.projects.length);

        repository.projects = repository.projects || [];
        repository.projects.forEach(project => {
          project.loading = false;
          project.isProject = true;
          project.modified = false;
          project.untracked = false;
          project.conflicting = false;
          project.files = [];
          this.file_map[project.path] = project;
          this.repository.projects.push(project);
          this.loadProject(project);
        });

        this.loadTerminals();
        this.loadRunners();
      });
    });
  },
  isModified(data) {
    var modified = false;
    if (this.git && this.git.status && this.git.status.modified) {
      if (data.isProject) {
        this.git.status.modified.forEach(path => {
          if (path.startsWith(data.path)) {
            modified = true;
          }
        });
      } else if (data.isDirectory) {
        this.git.status.modified.forEach(path => {
          if (path.startsWith(data.path)) {
            modified = true;
          }
        });
      } else if (data.isFile) {
        if (this.git.status.modified.indexOf(data.path) >= 0) {
          modified = true;
        }
      }
    }
    return modified;
  },
  isUntracked(data) {
    var untracked = false;
    if (this.git && this.git.status && this.git.status.untracked) {
      if (data.isProject) {
        this.git.status.untracked.forEach(path => {
          if (path.startsWith(data.path)) {
            untracked = true;
          }
        });
      } else if (data.isDirectory) {
        this.git.status.untracked.forEach(path => {
          if (path.startsWith(data.path)) {
            untracked = true;
          }
        });
      } else if (data.isFile) {
        if (this.git.status.untracked.indexOf(data.path) >= 0) {
          untracked = true;
        }
      }
    }
    return untracked;
  },
  isConflicting(data) {
    var conflicting = false;
    if (this.git && this.git.status && this.git.status.conflicting) {
      if (data.isProject) {
        this.git.status.conflicting.forEach(path => {
          if (path.startsWith(data.path)) {
            conflicting = true;
          }
        });
      } else if (data.isDirectory) {
        this.git.status.conflicting.forEach(path => {
          if (path.startsWith(data.path)) {
            conflicting = true;
          }
        });
      } else if (data.isFile) {
        if (this.git.status.conflicting.indexOf(data.path) >= 0) {
          conflicting = true;
        }
      }
    }
    return conflicting;
  },
  renderContent(h, { node, data, store }) {
    if (data.isProject) {
      return (
        <span style="flex: 1; display: flex; align-items: center; justify-content: space-between; font-size: 14px; padding-right: 8px;">
          <span class="color-orange">
            {data.modified || data.untracked || data.conflicting ? (
              <i
                class="coos-icon mgr-5 coos-icon-right ft-12"
                style="vertical-align: 1px;color: #bfbcbc;"
              />
            ) : (
              <i />
            )}
            <i class="coos-icon coos-icon-appstore mgr-5" />
            <span>{node.label}</span>
            <span>
              <a
                class="color-orange mgl-20 "
                title="刷新"
                on-click={() => this.reloadProject(data)}
              >
                {data.loading ? (
                  <i class="coos-icon el-icon-loading " />
                ) : (
                  <i class="coos-icon coos-icon-reload " />
                )}
              </a>
            </span>
          </span>
        </span>
      );
    }
    if (data.directory) {
      return (
        <span style="flex: 1; display: flex; align-items: center; justify-content: space-between; font-size: 14px; padding-right: 8px;">
          <span class="">
            {data.modified || data.untracked || data.conflicting ? (
              <i
                class="coos-icon mgr-5 coos-icon-right ft-12"
                style="vertical-align: 1px;color: #bfbcbc;"
              />
            ) : (
              <i />
            )}
            <i class="coos-icon coos-icon-folder-fill mgr-5 color-orange" />
            <span>{node.label}</span>
          </span>
          <span />
        </span>
      );
    }
    return (
      <span
        style="flex: 1; display: flex; align-items: center; justify-content: space-between; font-size: 14px; padding-right: 8px;"
        on-dblclick={() => this.openFileByPath(data.path)}
      >
        <span class="">
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
          <i class={"coos-icon coos-icon-file mgr-5 coos-icon-" + data.type} />
          <span>{node.label}</span>
        </span>
        <span />
      </span>
    );
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style  >
</style>
