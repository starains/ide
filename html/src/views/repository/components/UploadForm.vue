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
        <el-form-item label="传入到目录" prop="parent">
          <el-input v-model="form.parent"></el-input>
        </el-form-item>
        <el-form-item label="重复文件" prop="repeat">
          <el-radio v-model="form.repeat" label="IGNORE">忽略</el-radio>
          <el-radio v-model="form.repeat" label="COVER">覆盖</el-radio>
        </el-form-item>

        <el-form-item label="上传文件">
          <div class="dropzone-box"></div>
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
      title: "上传",
      labelWidth: "120px",
      size: "mini",
      source: source,
      show_form: false,
      is_update: false,
      form: {
        parent: "",
        repeat: "COVER"
      },
      form_rules: {}
    };
  },
  methods: {
    show(data) {
      this.showForm(data);
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
      if (coos.isEmpty(this.form.repeat)) {
        this.form.repeat = "IGNORE";
      }
      let form = this.form;
      this.$nextTick(res => {
        let $box = window.jQuery(this.$el).find(".dropzone-box");
        $box.empty();
        $box.append(
          '<div class="dropzone"><div class="am-text-success dz-message">将文件拖拽到此处<br />或点此打开文件管理器选择文件</div></div>'
        );
        let action = "/api/upload/" + source.token + "?";
        if (action.startsWith("/")) {
          action = action.substring(1);
        }
        let url = _SERVER_URL + action;

        source.loadDropzoneRely(res => {
          $box.find(".dropzone").dropzone({
            url: url,
            addRemoveLinks: true,
            method: "post",
            filesizeBase: 1024,
            sending: function(file, xhr, formData) {
              formData.append("fullPath", file.fullPath);
              formData.append("filesize", file.size);
              formData.append("repeat", form.repeat);
              formData.append("parent", form.parent);
            },
            success: function(file, response, e) {}
          });
        });
      });

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

<style  >
.dropzone-box .dropzone {
  border: 1px solid #efefef;
}
</style>
