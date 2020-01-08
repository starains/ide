
<script>
import tool from "@/common/js";
import http from "@/common/js/service";

export default {
  data: {},
  toSetRunOption(project, option) {
    let data = {};

    data.useinternal = true;
    data.internaltomcat = "tomcat-8.0.53";
    data.contextpath = "/" + project.name;
    data.port = 8080;

    this.project = project;
    if (project.isMaven) {
      data.language = "JAVA";
    }
    if (project.packaging == "war") {
      data.mode = "TOMCAT";
    } else {
      data.mode = "MAIN";
    }

    if (option) {
      for (var key in option) {
        if (key == "useinternal") {
          data[key] = coos.isTrue(option[key]);
        } else if (key == "port") {
          data[key] = Number(option[key]);
        } else {
          data[key] = option[key];
        }
      }
    }

    this.$refs["run-form"].showForm(data).then(data => {
      var that = this;
      this.setRunOption(this.project, data, function() {
        that.loadRunOptions(that.project);
      });
    });
  },
  setRunOption(project, option, callback) {
    var that = this;
    var data = {};
    data.path = project.path;
    data.option = option;
    http.work("SET_RUN_OPTION", data).then(result => {
      var value = result.value;
      callback && callback(result);
    });
  },
  runOptionStart(project, option, callback) {
    var that = this;
    var data = {};
    data.path = project.path;
    data.option = option;
    http.work("TERMINAL_START", data).then(result => {
      var value = result.value;
      that.loadTerminals();
      callback && callback(result);
    });
  },
  loadRunOptions(project, callback) {
    var that = this;
    var data = {};
    data.path = project.path;
    http.load("RUN_OPTIONS", data).then(result => {
      var value = result.value;
      project.runOptions = value;
      callback && callback(result);
    });
  },
  toDeleteRunOption(project, option, callback) {
    var that = this;
    coos
      .confirm("确定删除该配置？")
      .then(() => {
        that.deleteRunOption(project, option, function() {
          that.loadRunOptions(project);
        });
      })
      .catch(() => {});
  },
  deleteRunOption(project, option, callback) {
    var that = this;
    var data = {};
    data.path = project.path;
    data.option = option;
    http.work("DELETE_RUN_OPTION", data).then(result => {
      var value = result.value;
      callback && callback(result);
    });
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style  >
</style>
