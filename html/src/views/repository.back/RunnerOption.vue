
<script>
import tool from "@/common/js";
import http from "@/common/js/service";

export default {
  data: {},
  toSetRunnerOption(project, option) {
    var data = {};
    data.files = [];
    this.project = project;
    if (option) {
      for (var key in option) {
        if (key == "files") {
          if (option.files.length > 0) {
            option.files.forEach(f => {
              data.files.push(f);
            });
          }
        } else {
          data[key] = option[key];
        }
      }
    }

    this.$refs["runner-form"].showForm(data).then(data => {
      var that = this;
      this.setRunnerOption(this.project, data, function() {
        that.loadRunnerOptions(that.project);
      });
    });
  },
  setRunnerOption(project, option, callback) {
    var that = this;
    var data = {};
    data.path = project.path;
    let fs = [];
    option.files.forEach(f => {
      if (!coos.isEmpty(f.sourcepath)) {
        fs.push(f);
      }
    });
    option.files = fs;
    data.option = option;
    http.work("SET_RUNNER_OPTION", data).then(result => {
      var value = result.value;
      callback && callback(result);
    });
  },
  runnerOptionDeploy(project, option, callback) {
    var that = this;
    var data = {};
    data.path = project.path;
    data.option = option;
    http.work("RUNNER_DEPLOY", data).then(result => {
      var value = result.value;
      that.loadRunners();
      callback && callback(result);
    });
  },
  loadRunnerOptions(project, callback) {
    var that = this;
    var data = {};
    data.path = project.path;
    http.load("RUNNER_OPTIONS", data).then(result => {
      var value = result.value;
      project.runnerOptions = value;
      callback && callback(result);
    });
  },
  toDeleteRunnerOption(project, option, callback) {
    var that = this;

    coos
      .confirm("确定删除该配置？")
      .then(() => {
        that.deleteRunnerOption(project, option, function() {
          that.loadRunnerOptions(project);
        });
      })
      .catch(() => {});
  },
  deleteRunnerOption(project, option, callback) {
    var that = this;
    var data = {};
    data.path = project.path;
    data.option = option;
    http.work("DELETE_RUNNER_OPTION", data).then(result => {
      var value = result.value;
      callback && callback(result);
    });
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style  >
</style>
