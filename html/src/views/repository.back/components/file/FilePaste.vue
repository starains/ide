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
        <el-form-item label="源路径" prop="source">
          <el-input v-model="form.source" type="text" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="目录" prop="folder">
          <el-input v-model="form.folder" type="text" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" type="text" autocomplete="off"></el-input>
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
      title: "粘贴文件",
      labelWidth: "120px",
      size: "mini",
      source: source,
      show_form: false,
      form: {
        name: "",
        type: "",
        upload: "",
        folder: "",
        projectpath: ""
      },
      form_rules: {
        name: [
          {
            required: true,
            message: "请输入文件名称",
            trigger: "blur"
          }
        ],
        source: [
          {
            required: true,
            message: "源路径不允许为空",
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
          data.path = data.folder + "/" + data.name;
          data.path = coos.replaceAll(data.path, "//", "/");
          if (data.path == data.source) {
            coos.error("文件已存在！");
            return;
          }

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
