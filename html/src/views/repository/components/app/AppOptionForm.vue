<template>
  <div class>
    <el-dialog :title="title" :visible.sync="show_form" :close-on-click-modal="false" width="900px">
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
        <el-form-item label="模型路径" prop="modelpath">
          <el-input v-model="form.modelpath" type="text" autocomplete="off"></el-input>
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
        modelpath: ""
      },
      form_rules: {
        modelpath: [
          {
            required: true,
            message: "请输入模型路径",
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
        modelpath: "src/main/resources/app"
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
