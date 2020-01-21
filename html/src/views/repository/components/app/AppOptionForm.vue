<template>
  <div class>
    <el-dialog :title="title" :visible.sync="show_form" :close-on-click-modal="false" width="900px">
      <el-form
        :model="form"
        :rules="form_rules"
        ref="form"
        :label-width="labelWidth"
        class
        :size="size"
      >
        <el-form-item>
          <strong class="pdb-10 color-red">
            注意：项目需要依赖
            <a
              href="https://gitee.com/teamide/base"
              class="coos-link color-green"
              target="_blank"
            >https://gitee.com/teamide/base</a>，请自行下载引入。
          </strong>
        </el-form-item>
        <el-form-item label="模型路径" prop="modelpath">
          <el-input v-model="form.modelpath" type="text" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="Java代码路径" prop="javapath">
          <el-input v-model="form.javapath" type="text" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="基础包名" prop="basepackage">
          <el-input
            v-model="form.basepackage"
            type="text"
            autocomplete="off"
            placeholder="示例：com.teamide.web"
          ></el-input>
        </el-form-item>
        <el-form-item label="Factory包名" prop="factorypackage">
          <el-input
            v-model="form.factorypackage"
            type="text"
            autocomplete="off"
            placeholder="默认：基础包名.factory"
          ></el-input>
        </el-form-item>
        <el-form-item label="Controller包名" prop="controllerpackage">
          <el-input
            v-model="form.controllerpackage"
            type="text"
            autocomplete="off"
            placeholder="默认：基础包名.controller"
          ></el-input>
        </el-form-item>
        <el-form-item label="Service包名" prop="servicepackage">
          <el-input
            v-model="form.servicepackage"
            type="text"
            autocomplete="off"
            placeholder="默认：基础包名.service"
          ></el-input>
        </el-form-item>
        <el-form-item label="Dao包名" prop="daopackage">
          <el-input
            v-model="form.daopackage"
            type="text"
            autocomplete="off"
            placeholder="默认：基础包名.dao"
          ></el-input>
        </el-form-item>

        <el-form-item label="配置文件路径" prop="resourcepath">
          <el-input v-model="form.resourcepath" type="text" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="应用配置文件路径" prop="apppath">
          <el-input
            v-model="form.apppath"
            type="text"
            autocomplete="off"
            placeholder="默认：配置文件路径/app.properties"
          ></el-input>
        </el-form-item>

        <el-form-item label="默认JDBC路径" prop="jdbcpath">
          <el-input
            v-model="form.jdbcpath"
            type="text"
            autocomplete="off"
            placeholder="默认：配置文件路径/jdbc.properties"
          ></el-input>
        </el-form-item>

        <el-form-item label="JDBC目录" prop="jdbcdirectorypath">
          <el-input
            v-model="form.jdbcdirectorypath"
            type="text"
            autocomplete="off"
            placeholder="默认：配置文件路径/jdbcs/"
          ></el-input>
        </el-form-item>

        <el-form-item label="使用Spring注解" prop="usespringannotation">
          <el-switch v-model="form.usespringannotation"></el-switch>
        </el-form-item>

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
      title: "应用配置",
      labelWidth: "160px",
      size: "mini",
      source: source,
      show_form: false,
      form: {
        modelpath: "",
        javapath: "",
        resourcepath: "",
        codepackage: "",
        controllerpackage: "",
        basepackage: "",
        daopackage: "",
        servicepackage: "",
        factorypackage: "",
        jdbcpath: "",
        jdbcdirectorypath: "",
        apppath: "",
        usespringannotation: false
      },
      form_rules: {
        modelpath: [
          {
            required: true,
            message: "请输入模型路径",
            trigger: "blur"
          }
        ],
        javapath: [
          {
            required: true,
            message: "请输入代码路径",
            trigger: "blur"
          }
        ],
        resourcepath: [
          {
            required: true,
            message: "请输入配置文件路径",
            trigger: "blur"
          }
        ],
        basepackage: [
          {
            required: true,
            message: "请输入基础包名",
            trigger: "blur"
          }
        ]
      }
    };
  },
  methods: {
    show(path, data) {
      this.showForm(path, data);
    },
    showForm(path, data) {
      this.path = path;
      data = data || {
        modelpath: "src/main/resources/app",
        javapath: "src/main/java",
        resourcepath: "src/main/resources",
        usespringannotation: false
      };
      this.show_form = true;
      for (var key in this.form) {
        if (coos.isArray(this.form[key])) {
          this.form[key] = [];
        } else {
          this.form[key] = "";
        }
      }
      this.form.usespringannotation = false;
      for (var key in data) {
        this.form[key] = data[key];
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
          let data = { path: this.path, option: option };
          this.hideForm();

          source.do("APP_SET_OPTION", data);
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
