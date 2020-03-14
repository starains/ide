<template>
  <div class>
    <el-dialog :title="title" :visible.sync="show_form" :close-on-click-modal="false">
      <el-form
        :model="form"
        :rules="form_rules"
        ref="form"
        :label-width="labelWidth"
        :size="size"
        append-to-body
        zIndex="100"
        @submit.native.prevent
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" type="text" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item prop="serverid" label="远程服务器">
          <el-radio
            v-for="one in runner_servers"
            :key="one.id"
            v-model="form.serverid"
            :label="one.id"
          >
            {{one.name}}（{{one.server}}）
            <a
              class="coos-btn-link coos-orange pdl-5"
              @click="toSetRunnerServer(one)"
            >修改</a>
            <a class="coos-btn-link coos-red pdl-5" @click="toDeleteRunnerServerFromForm(one)">删除</a>
          </el-radio>
          <div class="pd-0" v-show="runner_servers.length==0">暂无远程服务器</div>
          <div>
            <a class="coos-btn-link coos-green" @click="toSetRunnerServer()">添加远程服务器</a>
          </div>
        </el-form-item>

        <el-form-item label="编译脚本" prop="compilescript">
          <el-input v-model="form.compilescript" type="textarea" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="启动脚本" prop="startscript">
          <el-input v-model="form.startscript" type="textarea" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="停止脚本" prop="stopscript">
          <el-input v-model="form.stopscript" type="textarea" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="PID路径" prop="pidpath">
          <el-input v-model="form.pidpath" type="text" autocomplete="off"></el-input>
        </el-form-item>

        <div class="pd-10">
          <h4 class="pdtb-5 coos-orange">
            文件配置（以'/'结尾作为目录使用）
            <a
              class="coos-green coos-btn-link coos-btn-xs"
              @click="addRunnerFormFile()"
            >添加</a>
          </h4>
          <template v-for="(file,index) in form.files">
            <div class="coos-row" :key="'file-'+index">
              <div class="col-6">
                <el-form-item label="文件路径" prop="sourcepath" :key="'sourcepath-'+index">
                  <el-input v-model="file.sourcepath" type="text" autocomplete="off"></el-input>
                </el-form-item>
              </div>
              <div class="col-6">
                <el-form-item label="部署路径" prop="targetpath" :key="'targetpath-'+index">
                  <el-input v-model="file.targetpath" type="text" autocomplete="off"></el-input>
                </el-form-item>
              </div>
            </div>
          </template>
        </div>

        <el-form-item>
          <el-button type="primary" @click="formSave()" :size="size">确定</el-button>
          <el-button @click="formCancel()" :size="size">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <RunnerServerForm ref="runner-server-form"></RunnerServerForm>
  </div>
</template>

<script>
import RunnerServerForm from "@/views/repository/components/runner/RunnerServerForm";

export default {
  components: { RunnerServerForm },
  data() {
    return {
      runner_servers: [],
      title: "远程部署配置",
      labelWidth: "120px",
      size: "mini",
      source: source,
      show_form: false,
      form: {
        name: "",
        serverid: "",
        compilescript: "",
        startscript: "",
        stopscript: "",
        pidpath: "",
        files: []
      },
      form_rules: {
        name: [
          {
            required: true,
            message: "请输入名称",
            trigger: "blur"
          }
        ],
        serverid: [
          {
            required: true,
            message: "请选中远程服务器",
            trigger: "blur"
          }
        ]
      }
    };
  },
  methods: {
    showForm(data) {
      this.loadRunnerServers();
      data = data || {};
      this.form.files.splice(0, this.form.files.length);
      this.form.files.push({ sourcepath: "", targetpath: "" });
      this.show_form = true;
      for (var key in this.form) {
        if (key == "files") {
        } else if (coos.isArray(this.form[key])) {
          this.form[key] = [];
        } else {
          this.form[key] = "";
        }
      }
      for (var key in data) {
        if (key == "files") {
          if (data.files.length > 0) {
            this.form.files.splice(0, this.form.files.length);
            data.files.forEach(f => {
              this.form.files.push(f);
            });
          }
        } else {
          this.form[key] = data[key];
        }
      }
      return new Promise((resolve, reject) => {
        this.resolve = resolve;
        this.reject = reject;
      });
    },
    hideForm() {
      this.show_form = false;
    },
    formSave() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          var data = this.form;

          this.hideForm();

          this.resolve && this.resolve(data);
        } else {
          return false;
        }
      });
    },
    formCancel() {
      this.hideForm();
    },
    addRunnerFormFile() {
      this.form.files.push({ sourcepath: "", targetpath: "" });
    },
    loadRunnerServers(callback) {
      var that = this;
      var data = {};
      source.load("RUNNER_SERVERS", data).then(result => {
        result = result || [];
        this.runner_servers.splice(0, this.runner_servers.length);
        result.forEach(one => {
          this.runner_servers.push(one);
        });
        callback && callback(result);
      });
    },
    toSetRunnerServer(option) {
      let data = {};

      if (option) {
        for (var key in option) {
          data[key] = option[key];
        }
      }

      this.$refs["runner-server-form"].showForm(data).then(data => {
        var that = this;
        this.runnerServerSave(data, function() {
          that.loadRunnerServers();
        });
      });
    },
    runnerServerSave(option, callback) {
      var that = this;
      var data = option || {};
      http.work("RUNNER_SERVER_SAVE", data).then(result => {
        var value = result.value;
        callback && callback(result);
      });
    },
    toDeleteRunnerServerFromForm(option) {
      let that = this;
      this.show_form = false;
      this.toDeleteRunnerServer(option, function() {
        that.show_form = true;
      });
    },
    toDeleteRunnerServer(option, callback) {
      var that = this;
      coos
        .confirm("确定删除该配置？")
        .then(() => {
          that.deleteRunnerServer(option, function() {
            that.loadRunnerServers();
            callback && callback();
          });
        })
        .catch(() => {
          callback && callback();
        });
    },
    deleteRunnerServer(option, callback) {
      var that = this;
      var data = option || {};
      http.work("RUNNER_SERVER_DELETE", data).then(result => {
        var value = result.value;
        callback && callback(result);
      });
    }
  },
  mounted() {},
  beforeCreate() {}
};
</script>

<style  scoped="scoped">
</style>
