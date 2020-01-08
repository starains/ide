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
        <el-form-item label="设置远程名称" prop="gitRemoteName">
          <el-input v-model="form.gitRemoteName" type="text" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="仓库地址" prop="url">
          <el-input v-model="form.url" type="text" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="检出远程分支" prop="gitRemoteBranch">
          <el-input v-model="form.gitRemoteBranch" type="text" autocomplete="off"></el-input>
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
      title: "Git远程仓库设置",
      labelWidth: "120px",
      size: "mini",
      source: source,
      show_form: false,
      form: {
        gitRemoteName: "",
        url: "",
        gitRemoteBranch: ""
      },
      form_rules: {
        gitRemoteName: [
          {
            required: true,
            message: "请输入远程名称",
            trigger: "blur"
          }
        ],
        url: [
          {
            required: true,
            message: "请输入远程地址",
            trigger: "blur"
          }
        ],
        gitRemoteBranch: [
          {
            required: true,
            message: "请输入远程分支",
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
