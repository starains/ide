
<script>
import tool from "@/common/js";
import http from "@/common/js/service";

export default {
  data: {},

  initContextView(tab) {},
  openDirectory(file) {
    let index = this.open_folder_keys.indexOf(file.path);
    if (index >= 0) {
      this.open_folder_keys.splice(index, 1);
    } else {
      this.open_folder_keys.push(file.path);
    }
  },
  closeFile(file) {
    let files = file.files || [];
    files.forEach(f => {
      this.closeFile(f);
    });
    this.removeTab(file.path);
    let index = this.open_file_keys.indexOf(file.path);
    if (index >= 0) {
      this.open_file_keys.splice(index, 1);
    }
  },
  closeLoseFile(path) {
    http.work("FILE_CLOSE", { path: path }).then(result => {});
  },
  closeFileByPath(path) {
    let file = this.findFileByPath(path);
    if (file) {
      this.closeFile(file);
    }
  },
  openFileByPath(path, recordopen) {
    let file = this.findFileByPath(path);
    if (file) {
      this.openFile(file, recordopen);
    }
  },
  openFile(file, recordopen) {
    let index = this.open_file_keys.indexOf(file.path);
    if (index < 0) {
      this.open_file_keys.push(file.path);
    }
    let tab = this.tab_map[file.path];
    if (tab) {
      if (tab.path != this.activeTab) {
        this.activeTab = file.path;
        this.tab_map[this.activeTab].show = "content";
      }
    } else {
      tab = this.addTab(file);
    }
    if (coos.isEmpty(recordopen)) {
      recordopen = true;
    }
    http
      .work("FILE_OPEN", {
        path: file.path,
        recordopen: recordopen
      })
      .then(result => {
        tab.onLoad(result.value);
      });
  },
  clickTab(vTab) {
    let tab = this.tab_map[vTab.name];
    if (tab) {
      this.openFile(tab.file);
    }
  },
  toDownloadFile(file) {
    let url =
      _SERVER_URL +
      "/api/workspace/fileDownload?spaceid=" +
      source.spaceid +
      "&branch=" +
      source.branch +
      "&path=" +
      file.path;
    window.location.href = url;
  },
  saveFile(file) {
    let project = this.findProjectByPath(file.path);
    http
      .work("FILE_SAVE", {
        path: file.path,
        content: file.content,
        projectpath: project.path
      })
      .then(result => {
        file.content = result.value.content;
        file.changed = false;
        this.loadGitStatus();
      });
  },
  deleteFile(file) {
    var that = this;
    let project = this.findProjectByPath(file.path);
    let apppath = null;
    if (project.app) {
      apppath = project.app.path;
    }
    coos
      .confirm("确定删除该文件？")
      .then(() => {
        http
          .work("FILE_DELETE", {
            path: file.path,
            projectpath: project.path,
            apppath: apppath
          })
          .then(result => {
            let value = result.value;
            that.closeFileByPath(file.path);
            that.removeFileByPath(file.path);
            that.updateFilesByFiles(value.files);
            let project = this.findProjectByPath(file.path);
            this.loadProjectApp(project, function() {});
            that.loadGitStatus();
          });
      })
      .catch(() => {});
  },
  toRenameFile(file) {
    let data = {};
    data.oldpath = file.path;
    data.newpath = file.path;
    let project = this.findProjectByPath(file.path);
    data.projectpath = project.path;
    if (project.app) {
      data.apppath = project.app.path;
    }
    let that = this;

    this.$refs["file-rename"].showForm(data).then(data => {
      if (data.oldpath == data.newpath) {
        return;
      }
      http.work("FILE_RENAME", data).then(result => {
        var value = result.value;
        var files = value.files;

        this.removeFileByPath(data.oldpath);
        this.updateFilesByFiles(files);

        this.closeFileByPath(data.oldpath);

        let project = this.findProjectByPath(files[0].path);
        this.loadProjectApp(project, function() {
          that.openFileByPath(data.newpath);
        });
        this.loadGitStatus();
      });
    });
  },
  pasteFile(file, toFile) {
    if (!file || !toFile) {
      return;
    }
    let that = this;
    let data = {};
    data.source = file.path;
    data.folder = toFile.path;
    data.name = file.name;
    if (toFile.path.indexOf("/") > 0) {
      data.folder = toFile.path.substring(0, toFile.path.lastIndexOf("/"));
    } else {
      data.folder = "";
    }

    this.$refs["file-paste"].showForm(data).then(data => {
      let project = this.findProjectByPath(data.path);
      data.projectpath = project.path;
      if (project.app) {
        data.apppath = project.app.path;
      }

      http.work("FILE_PASTE", data).then(result => {
        var value = result.value;
        var files = value.files || [];

        this.updateFilesByFiles(files);

        this.show_file_create_form = false;
        let project = this.findProjectByPath(files[0].path);
        this.loadProjectApp(project, function() {
          that.openFileByPath(value.file.path);
        });
        that.loadGitStatus();
      });
    });
  },
  toCreateFile(file) {
    let data = {};
    data.folder = "/" + file.path;
    data.name = "new";
    let project = this.findProjectByPath(file.path);
    data.projectpath = project.path;
    if (project.app) {
      data.apppath = project.app.path;
    }
    let that = this;

    this.$refs["file-create"].showForm(data).then(data => {
      if (!data.folder.endsWith("/")) {
        data.folder += "/";
      }
      if (data.type == "JAVA" && !data.name.endsWith(".java")) {
        data.name += ".java";
      } else if (data.type == "JS" && !data.name.endsWith(".js")) {
        data.name += ".js";
      } else if (data.type == "CSS" && !data.name.endsWith(".css")) {
        data.name += ".css";
      }
      data.path = data.folder + data.name;
      var content = "";
      if (
        data.folder.indexOf("/src/main/java/") == 0 &&
        data.name.endsWith(".java")
      ) {
        var className = data.name;
        className = className.substring(0, className.lastIndexOf(".java"));
        var pack = data.folder.substring("/src/main/java/".length);
        if (pack.startsWith("/")) {
          pack = pack.substring(1);
        }
        if (pack.endsWith("/")) {
          pack = pack.substring(0, pack.length - 1);
        }
        pack = pack.replace(new RegExp("/", "gm"), ".");
        content += "package " + pack + ";\n";
        content += "\n";
        content += "public class " + className + " {\n";
        content += "\n";
        content += "\n";
        content += "\n";
        content += "}\n";
      }
      data.content = content;

      http.work("FILE_CREATE", data).then(result => {
        var value = result.value;
        var files = value.files || [];

        this.updateFilesByFiles(files);

        this.show_file_create_form = false;
        let project = this.findProjectByPath(files[0].path);
        this.loadProjectApp(project, function() {
          that.openFileByPath(value.file.path);
        });
        that.loadGitStatus();
      });
    });
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style  >
</style>
