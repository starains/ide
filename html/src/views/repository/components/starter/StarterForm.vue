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
          <el-input
            v-model="form.name"
            type="text"
            :readonly="is_update?'readonly':false"
            autocomplete="off"
          ></el-input>
        </el-form-item>

        <el-form-item label="语言" prop="language">
          <el-radio v-model="form.language" label="JAVA">Java</el-radio>
          <el-radio v-model="form.language" label="OTHER">其它</el-radio>
        </el-form-item>

        <template v-if="form.language == 'OTHER'">
          <el-form-item>
            <small class="pdb-10 color-red" style="line-height: 14px;">
              环境变量：
              <div>$SOURCE_PATH：为代码源文件目录</div>
              <div>$STARTER_LOG_PATH：为进程启动日志路径</div>
              <div>$STARTER_PID_PATH：为进程启动PID路径</div>
            </small>
          </el-form-item>
        </template>
        <el-form-item label="远程服务器">
          <el-radio v-model="form.remoteid" label>本地运行</el-radio>
          <template v-if="form.language == 'JAVA'">
            <el-radio
              v-for="one in source.data.REMOTES"
              :key="one.id"
              v-model="form.remoteid"
              :label="one.id"
            >{{one.name}}</el-radio>
          </template>
        </el-form-item>

        <template v-if="form.language == 'JAVA'">
          <el-form-item label="Java环境">
            <el-radio v-model="form.javaenvironmentid" label>读取系统</el-radio>
            <el-radio
              v-for="one in source.data.ENVIRONMENTS"
              :key="one.id"
              v-model="form.javaenvironmentid"
              v-show="one.type == 'JAVA'"
              :label="one.id"
            >{{one.name}}({{one.version}})</el-radio>
          </el-form-item>

          <el-form-item label="Maven环境">
            <el-radio v-model="form.mavenenvironmentid" label>读取系统</el-radio>
            <el-radio
              v-for="one in source.data.ENVIRONMENTS"
              :key="one.id"
              v-model="form.mavenenvironmentid"
              v-show="one.type == 'MAVEN'"
              :label="one.id"
            >{{one.name}}({{one.version}})</el-radio>
          </el-form-item>

          <el-form-item label="Maven运行变量">
            <el-input v-model="form.mavenenvp" type="text" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="Java运行变量">
            <el-input v-model="form.javaenvp" type="text" autocomplete="off"></el-input>
          </el-form-item>

          <el-form-item label="运行模式" :prop="form.language == 'JAVA'?'mode':''">
            <el-radio v-model="form.mode" label="MAIN">Main</el-radio>
            <el-radio v-model="form.mode" label="TOMCAT">Tomcat</el-radio>
            <el-radio v-model="form.mode" label="JAR">Jar</el-radio>
            <el-radio v-model="form.mode" label="WAR">War</el-radio>
          </el-form-item>

          <el-form-item
            v-show="form.mode == 'MAIN'"
            label="Main Class"
            :prop="form.mode == 'MAIN'?'main':''"
          >
            <el-input v-model="form.main" type="text" autocomplete="off"></el-input>
          </el-form-item>

          <el-form-item v-show="form.mode == 'JAR'" label="Jar" :prop="form.mode == 'JAR'?'jar':''">
            <el-input v-model="form.jar" type="text" autocomplete="off"></el-input>
          </el-form-item>
          <template v-if="form.mode == 'TOMCAT' || form.mode == 'WAR'">
            <el-form-item
              :prop="(form.mode == 'TOMCAT'|| form.mode == 'WAR')?'internaltomcat':''"
              label="内置Tomcat"
            >
              <el-radio v-model="form.internaltomcat" label="tomcat-8.5.41">tomcat-8.5.41</el-radio>
            </el-form-item>

            <el-form-item
              :prop="(form.mode == 'TOMCAT'|| form.mode == 'WAR')?'port':''"
              label="请输入端口"
            >
              <el-input v-model="form.port" type="text" autocomplete="off"></el-input>
            </el-form-item>

            <el-form-item
              label="访问路径"
              :prop="(form.mode == 'TOMCAT'|| form.mode == 'WAR' )?'contextpath':''"
            >
              <el-input v-model="form.contextpath" type="text" autocomplete="off"></el-input>
            </el-form-item>
          </template>
        </template>

        <template v-if="form.language == 'OTHER'">
          <el-form-item
            v-show="form.language == 'OTHER'"
            label="编译命令"
            :prop="form.language == 'OTHER'?'compilecommand':''"
          >
            <el-input v-model="form.compilecommand" type="textarea" autocomplete="off" :rows="5"></el-input>
            <div>示例：go install，npm install</div>
          </el-form-item>

          <el-form-item
            v-show="form.language == 'OTHER'"
            label="启动命令"
            :prop="form.language == 'OTHER'?'startcommand':''"
          >
            <el-input v-model="form.startcommand" type="textarea" autocomplete="off" :rows="5"></el-input>
            <div>示例：go run xxx.go，npm run dev</div>
            <div>linux使用进程ID示例：</div>
            <div class="ft-12 color-red">nohup 启动命令 >> $STARTER_LOG_PATH 2>&1 &</div>
            <div class="ft-12 color-red">echo $! > $STARTER_PID_PATH</div>
          </el-form-item>

          <el-form-item
            v-show="form.language == 'OTHER'"
            label="停止命令"
            :prop="form.language == 'OTHER'?'stopcommand':''"
          >
            <el-input v-model="form.stopcommand" type="textarea" autocomplete="off" :rows="5"></el-input>
          </el-form-item>

          <el-form-item
            v-show="form.language == 'OTHER'"
            label="PID文件"
            :prop="form.language == 'OTHER'?'pidfile':''"
          >
            <el-input v-model="form.pidfile" type="text" autocomplete="off"></el-input>
          </el-form-item>
        </template>

        <el-form-item>
          <el-button type="primary" @click="formSave()" :size="size">确定</el-button>
          <el-button @click="formCancel()" :size="size">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
export default {
  components: {},
  data() {
    return {
      title: "运行配置",
      labelWidth: "120px",
      size: "mini",
      source: source,
      show_form: false,
      is_update: false,
      form: {
        name: "",
        language: "",
        remoteid: "",
        mode: "",
        jar: "",
        main: "",
        environmentid: "",
        mavenenvp: "",
        javaenvp: "",
        javaenvironmentid: "",
        mavenenvironmentid: "",
        internaltomcat: "tomcat-8.0.53",
        port: 8080,
        contextpath: "/",
        nodeenvironmentid: "",
        nodecommand: "",
        compilecommand: "",
        startcommand: "",
        stopcommand: "",
        pidfile: ""
      },
      form_rules: {
        nodecommand: [
          {
            required: true,
            message: "请输入启动命令",
            trigger: "blur"
          }
        ],
        name: [
          {
            required: true,
            message: "请输入名称",
            trigger: "blur"
          }
        ],
        contextpath: [
          {
            required: true,
            message: "请输入访问路径",
            trigger: "blur"
          }
        ],
        language: [
          {
            required: true,
            message: "请选择语言",
            trigger: "blur"
          }
        ],
        mode: [
          {
            required: true,
            message: "请选择运行模式",
            trigger: "blur"
          }
        ],
        main: [
          {
            required: true,
            message: "请输入MainClass",
            trigger: "blur"
          }
        ],
        environmentid: [
          {
            required: true,
            message: "请选择容器环境",
            trigger: "blur"
          }
        ],
        internaltomcat: [
          {
            required: true,
            message: "请选择内置Tomcat",
            trigger: "blur"
          }
        ],
        port: [
          {
            required: true,
            message: "请输入端口号",
            trigger: "blur",
            validator: (rule, value, callback) => {
              if (!value) {
                callback(new Error("请输入端口号"));
              }
              value = Number(value);
              if (typeof value === "number" && !isNaN(value)) {
                if (value < 1 || value > 50000) {
                  callback(new Error("端口号在 1 至 50000 之间"));
                } else {
                  callback();
                }
              } else {
                callback(new Error("端口号必须为数字"));
              }
            }
          }
        ]
      }
    };
  },
  methods: {
    show(projectPath, data) {
      this.showForm(projectPath, data);
    },
    showForm(projectPath, data) {
      this.projectPath = projectPath;
      data = data || {};
      this.show_form = true;
      for (var key in this.form) {
        if (coos.isArray(this.form[key])) {
          this.form[key] = [];
        } else {
          this.form[key] = "";
        }
      }
      for (var key in data) {
        this.form[key] = data[key];
      }
      if (coos.isEmpty(data.name)) {
        this.is_update = false;
      } else {
        this.is_update = true;
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
          var option = this.form;
          let data = {
            projectPath: this.projectPath,
            name: option.name,
            option: option
          };
          this.hideForm();

          source.do("SET_STARTER_OPTION", data).then(res => {
            source.load("STARTER_OPTIONS");
          });
          this.resolve && this.resolve(data);
        } else {
          return false;
        }
      });
    },
    formCancel() {
      this.hideForm();
    }
  },
  mounted() {},
  beforeCreate() {}
};
</script>

<style  scoped="scoped">
</style>
