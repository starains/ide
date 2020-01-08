
<script>
import tool from "@/common/js";
import http from "@/common/js/service";

export default {
  loadProject(project) {
    if (project.loading) {
      return;
    }
    project.loading = true;
    let start = new Date().getTime();
    http.load("PROJECT", { path: project.path }).then(result => {
      let end = new Date().getTime();
      this.refreshProject(project, result.value);
      window.setTimeout(function() {
        project.loading = false;
      }, 600 - (end - start));
    });
  },
  loadProjectApp(project, callback) {
    http.load("APP", { apppath: project.app.localpath }).then(result => {
      project.app = project.app || {};
      $.extend(project.app, result.value);
      callback && callback();
    });
  },
  reloadProject(project) {
    this.loadProject(project);
    // $event.stopPropagation();
    return false;
  },
  updateFilesByFiles(files) {
    files = files || [];
    let path = "";
    if (files.length > 0) {
      path = files[0].path;
      if (path.indexOf("/") >= 0) {
        path = path.substring(0, path.lastIndexOf("/"));
      }
    }
    this.updateFiles(path, files);
  },
  updateFiles(path, files) {
    this.removeFileChildren(path);
    this.addFiles(files);
    this.$refs.tree.updateKeyChildren(path, files);
  },
  removeFileByPath(path) {
    let keys = Object.keys(this.file_map);
    keys.forEach(key => {
      if (key == path) {
        delete this.file_map[key];
        this.removeFileChildren(path);
      }
    });
  },
  removeFileChildren(path) {
    let keys = Object.keys(this.file_map);
    keys.forEach(key => {
      if (key.startsWith(path + "/")) {
        delete this.file_map[key];
      }
    });
  },
  addFiles(files) {
    files = files || [];
    files.forEach(file => {
      file.modified = false;
      file.untracked = false;
      file.conflicting = false;
      this.file_map[file.path] = file;
      this.addFiles(file.files);
    });
  },
  refreshProject(project, value) {
    project.loaded = true;
    project.app = project.app || {};
    $.extend(project.app, value.app);
    project.getModelByPath = function(path) {
      let model = null;
      if (project.app && project.app.context) {
        let context = project.app.context;
        Object.keys(context).forEach(key => {
          let obj = context[key];
          if (coos.isArray(obj)) {
            obj.forEach(one => {
              if (one.localfilepath == path) {
                model = one;
              }
            });
          } else {
            if (obj.localfilepath == path) {
              model = obj;
            }
          }
        });
      }
      return model;
    };
    this.updateFiles(value.path, value.files);
    if (this.git && this.git.status) {
      this.updateGitStatus(this.git.status);
    }
    this.open_file_keys.forEach(path => {
      let projectPath = project.path;
      if (projectPath != "") {
        projectPath = projectPath + "/";
      }
      if (path.startsWith(projectPath)) {
        this.openFileByPath(path, false);
      }
    });
    let flag = true;
    this.repository.projects.forEach(pro => {
      if (!pro.loaded) {
        flag = false;
      }
    });
    if (flag) {
      this.open_file_keys.forEach(path => {
        let file = this.findFileByPath(path);
        if (!file) {
          this.closeLoseFile(path);
        }
      });
    }
  },
  findProjectByPath(path) {
    let project = null;
    this.repository.projects.forEach((one, index) => {
      if (one.path != "") {
        if (path.startsWith(one.path + "/") || path == one.path) {
          project = one;
        }
      }
    });
    if (project == null) {
      this.repository.projects.forEach((one, index) => {
        if (one.path == "") {
          project = one;
        }
      });
    }
    return project;
  },
  findFileByPath(path) {
    return this.file_map[path];
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style  >
</style>
