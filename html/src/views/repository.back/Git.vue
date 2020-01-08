
<script>
import tool from "@/common/js";
import http from "@/common/js/service";

export default {
  data: {},
  toCreateGitRemote() {
    let data = {};
    data.gitRemoteName = "origin";
    data.gitRemoteBranch = "master";
    if (this.git && this.git.option) {
      data.gitRemoteName = this.git.option.gitRemoteName;
      data.gitRemoteBranch = this.git.option.gitRemoteBranch;
      data.url = this.git.option.url;
    }

    let that = this;
    this.$refs["git-remote"].showForm(data).then(res => {
      coos
        .confirm(
          "设置远程仓库，现在将切换至远程仓库[" +
            res.gitRemoteName +
            "]下[" +
            res.gitRemoteBranch +
            "]版本"
        )
        .then(() => {
          that.createGitRemote(res, function() {
            that.gitPull(
              {
                gitRemote: res.gitRemoteName,
                gitRemoteBranchName: res.gitRemoteBranch
              },
              function() {
                that.load();
              }
            );
          });
        })
        .catch(() => {});
    });
  },
  createGitRemote(data, callback) {
    http.work("GIT_REMOTE_ADD", data).then(result => {
      var value = result.value;
      callback && callback(result);
    });
  },

  toSetCertificate(callback) {
    let data = {};
    data.username = "";
    data.password = "";
    if (this.git && this.git.certificate) {
      data.username = this.git.certificate.username;
      data.password = this.git.certificate.password;
    }
    this.$refs["git-certificate"].showForm(data).then(res => {
      callback && callback(res);
    });
  },

  gitPull(data, callback) {
    var that = this;
    this.toSetCertificate(function(certificate) {
      data.certificate = certificate;
      http.work("GIT_PULL", data).then(result => {
        var value = result.value;

        var token = value.token;
        var listenStatus = function() {
          that.loading = true;
          that.loading_msg = "Git Pull...";
          that.gitPullStatus(token, function(result) {
            if (result.value) {
              if (result.value.status == 0) {
                window.setTimeout(function() {
                  listenStatus();
                }, 500);
              } else if (result.value.status == 1) {
                that.loading = false;
                coos.success("拉取成功！");
                callback && callback(result);
              } else {
                that.loading = false;
                coos.alert(result.value.status);
                callback && callback(result);
              }
            }
          });
        };
        listenStatus();
      });
    });
  },
  gitPullStatus(token, callback) {
    http.work("GIT_PULL_STATUS", { token: token }).then(result => {
      var value = result.value;
      callback && callback(result);
    });
  },
  loadGit(callback) {
    http.load("GIT", {}).then(result => {
      var value = result.value;
      callback && callback(result);
    });
  },
  loadGitStatus() {
    http.load("GIT_STATUS", {}).then(result => {
      var value = result.value;
      let status = value.status;
      this.updateGitStatus(status);
    });
  },
  formatHeadBranchName(branch) {
    var name = branch.name.substring("refs/heads/".length);
    return name;
  },
  isHeadBranchName(branch) {
    return branch.name.startsWith("refs/heads/");
  },
  formatRemoteBranchName(remoteName, branch) {
    var name = branch.name.substring(
      ("refs/remotes/" + remoteName + "/").length
    );
    return name;
  },
  isRemoteBranchName(remoteName, branch) {
    return branch.name.startsWith("refs/remotes/" + remoteName + "/");
  },
  updateGitStatus(status) {
    if (!status) {
      return;
    }
    var that = this;
    this.change_files = [];
    this.git = this.git || {};
    this.git.status = status;
    that.git.status.modified.forEach(path => {
      that.change_files.push({
        key: path,
        status: "modified",
        label: path + "(更改)"
      });
    });
    that.git.status.untracked.forEach(path => {
      that.change_files.push({
        key: path,
        status: "untracked",
        label: path + "(添加)"
      });
    });
    that.git.status.missing.forEach(path => {
      that.change_files.push({
        key: path,
        status: "missing",
        label: path + "(移除)"
      });
    });
    that.git.status.conflicting.forEach(path => {
      that.change_files.push({
        key: path,
        status: "conflicting",
        label: path + "(冲突)"
      });
    });
    let keys = Object.keys(this.file_map);
    keys.forEach(key => {
      let file = this.file_map[key];
      if (file) {
        file.modified = this.isModified(file);
        file.untracked = this.isUntracked(file);
        file.conflicting = this.isConflicting(file);
      }
    });
  },
  toGitPush() {
    var that = this;
    let form = this.git_push_form;
    this.loadGit(function(result) {
      let data = {};
      that.git = result.value;
      that.updateGitStatus(that.git.status);

      that.$refs["git-push"].showForm(data, that.git).then(res => {
        res.modified = [];
        res.untracked = [];
        res.missing = [];
        res.conflicting = [];
        res.paths.forEach(path => {
          that.change_files.forEach(change_file => {
            if (change_file.key == path) {
              res[change_file.status].push(path);
            }
          });
        });

        that.gitPush(res, function() {
          that.load();
        });
      });
    });
  },
  gitPush(data, callback) {
    var that = this;
    this.toSetCertificate(function(certificate) {
      data.certificate = certificate;
      http.work("GIT_PUSH", data).then(result => {
        var value = result.value;

        var token = value.token;
        var listenStatus = function() {
          that.loading = true;
          that.loading_msg = "Git Push...";
          that.gitPushStatus(token, function(result) {
            if (result.value) {
              if (result.value.status == 0) {
                window.setTimeout(function() {
                  listenStatus();
                }, 500);
              } else if (result.value.status == 1) {
                that.loading = false;
                coos.success("推送成功！");
                callback && callback(result);
              } else {
                that.loading = false;
                coos.alert(result.value.status);
                callback && callback(result);
              }
            }
          });
        };
        listenStatus();
      });
    });
  },
  gitPushStatus(token, callback) {
    http.work("GIT_PUSH_STATUS", { token: token }).then(result => {
      var value = result.value;
      callback && callback(result);
    });
  },
  gitRevert(paths, callback) {
    let that = this;
    if (paths == null || paths.length == 0) {
      return;
    }
    if (coos.isEmpty(paths[0])) {
      paths[0] = ".";
    }
    coos.confirm(
      '确定还原路径<span class="pdlr-10 color-green">[' +
        paths.join("；") +
        "]</span>下文件？",
      function() {
        var data = {};
        data.paths = paths;
        http.work("GIT_REVERT", data).then(result => {
          callback && callback(result);
          that.loadGitStatus();
        });
      }
    );
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style  >
</style>
