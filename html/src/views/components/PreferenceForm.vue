<template>
  <div class>
    <el-dialog :title="title" :visible.sync="show_form" :close-on-click-modal="false">
      <el-form
        :model="form"
        :rules="form_rules"
        ref="form"
        :label-width="labelWidth"
        class
        :size="size"
      >
        <el-form-item label="编辑器字体大小" prop="fontsize">
          <el-input-number v-model="form.fontsize" :min="1" :max="50"></el-input-number>
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
      title: "用户偏好设置",
      labelWidth: "120px",
      size: "mini",
      source: source,
      show_form: false,
      form: { fontsize: 13 },
      form_rules: {}
    };
  },
  methods: {
    show() {
      let preference = source.preference || {};
      return this.showForm(preference);
    },
    showForm(data) {
      data = data || {};
      this.show_form = true;
      for (var key in this.form) {
        if (coos.isArray(this.form[key])) {
          this.form[key] = [];
        } else {
          this.form[key] = "";
        }
      }
      this.form.fontsize = 13;
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

          this.hideForm();

          source
            .do("PREFERENCE", { id: source.LOGIN_USER.id, option: option })
            .then(res => {
              if (res.errcode == 0) {
                coos.success("修改成功！");
                Object.assign(source.preference, option);
                source.changePreference();
              } else {
                coos.error(res.errmsg);
              }
            });
          this.resolve && this.resolve(option);
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
