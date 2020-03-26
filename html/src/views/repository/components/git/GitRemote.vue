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
    show() {
      let option = source.repository.git.option || {};
      this.showForm(option);
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
      if (coos.isEmpty(this.form.gitRemoteName)) {
        this.form.gitRemoteName = "origin";
      }
      if (coos.isEmpty(this.form.gitRemoteBranch)) {
        this.form.gitRemoteBranch = "master";
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

          let isChange = true;
          let isNew = true;
          if (source.repository.git && source.repository.git.option) {
            let option = source.repository.git.option;
            if (
              option.gitRemoteBranch == data.gitRemoteBranch &&
              option.gitRemoteName == data.gitRemoteName &&
              option.url == data.url
            ) {
              isChange = false;
            }
            isNew = false;
          }
          data.needclean = true;

          if (isChange) {
            let msg = "";

            if (isNew) {
              msg =
                "设置远程仓库[" +
                data.gitRemoteName +
                "]下[" +
                data.gitRemoteBranch +
                "]版本，设置后将清空仓库文件，是否设置？";
            } else {
              msg =
                "切换至远程仓库[" +
                data.gitRemoteName +
                "]下[" +
                data.gitRemoteBranch +
                "]版本，切换操作将清空仓库文件，是否切换？";
            }

            source.gitCertificateForm.show().then(certificate => {
              data.certificate = certificate;
              coos.confirm(msg).then(
                res => {
                  source.do("GIT_REMOTE_ADD", data).then(res => {
                    if (res.errcode == 0) {
                      source.loadGitWorkStatus();
                    } else {
                      coos.error(res.errmsg);
                    }
                  });
                  this.resolve && this.resolve(data);
                },
                () => {}
              );
            });
          }
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
