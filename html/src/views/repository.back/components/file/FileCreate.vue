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
        <el-form-item label="目录" prop="folder">
          <el-input v-model="form.folder" type="text" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" type="text" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio v-model="form.type" label="JAVA" value="JAVA"></el-radio>
          <el-radio v-model="form.type" label="HTML" value="HTML"></el-radio>
          <el-radio v-model="form.type" label="JS" value="JS"></el-radio>
          <el-radio v-model="form.type" label="CSS" value="CSS"></el-radio>
          <el-radio v-model="form.type" label="IMAGE" value="IMAGE"></el-radio>
          <el-radio v-model="form.type" label="OTHER" value="OTHER"></el-radio>
        </el-form-item>

        <el-form-item label="上传" prop="upload">
          <el-upload
            class="avatar-uploader mgb-10"
            :action="_SERVER_URL+('/coos/file/upload')"
            :show-file-list="false"
            :on-success="handleFileSuccess"
          >
            <a class="coos-btn coos-btn-sm coos-bg-green">
              <i class="coos-icon coos-icon-upload"></i>
            </a>
          </el-upload>
          <el-input v-model="form.upload"></el-input>
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
      title: "新建文件",
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
        folder: [
          {
            required: true,
            message: "请输入文件目录",
            trigger: "blur"
          }
        ],
        newpath: [
          {
            required: true,
            message: "新路径不允许为空",
            trigger: "blur"
          }
        ]
      }
    };
  },
  methods: {
    handleFileSuccess(res, file) {
      this.form.upload = res.path;
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
