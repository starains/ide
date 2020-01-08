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
        <el-form-item label="版本名称" prop="branch">
          <el-input v-model="form.branch" type="text" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="拷贝版本" prop="frombranch">
          <el-input v-model="form.frombranch" autocomplete="off"></el-input>
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
import tool from "@/common/js";

export default {
  components: {},
  data() {
    return {
      title: "新建版本",
      labelWidth: "120px",
      size: "mini",
      source: source,
      show_form: false,
      form: {
        branch: "",
        frombranch: ""
      },
      form_rules: {
        branch: [
          {
            required: true,
            message: "请输入版本名称",
            trigger: "blur"
          }
        ],
        frombranch: [
          {
            required: true,
            message: "请填写拷贝版本",
            trigger: "blur"
          }
        ]
      }
    };
  },
  methods: {
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
    }
  },
  mounted() {},
  beforeCreate() {}
};
</script>

<style  scoped="scoped">
</style>
