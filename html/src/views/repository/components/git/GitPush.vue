<template>
  <div class>
    <el-dialog
      :title="title"
      :visible.sync="show_form"
      :close-on-click-modal="false"
      width="1000px"
      top="50px"
      append-to-body
      zIndex="100"
    >
      <el-form
        :model="form"
        :rules="form_rules"
        ref="form"
        :label-width="labelWidth"
        class
        :size="size"
        @submit.native.prevent
      >
        <el-form-item label="本地分支" prop="branchName">
          <el-radio
            v-for="branch in form.branchList"
            :key="branch.name"
            v-model="form.branchName"
            v-show="source.isHeadBranchName(branch)"
            :label="source.formatHeadBranchName(branch)"
          >{{source.formatHeadBranchName(branch)}}</el-radio>
        </el-form-item>

        <el-form-item label="远程仓库" prop="gitRemote">
          <el-radio
            v-for="remote in form.remoteList"
            :key="remote.name"
            v-model="form.gitRemoteName"
            :label="remote.name"
            :value="remote.name"
          ></el-radio>
        </el-form-item>

        <el-form-item label="远程分支" prop="remoteBranchName">
          <el-radio
            v-for="branch in form.branchList"
            :key="branch.name"
            v-model="form.gitRemoteBranch"
            v-show="source.isRemoteBranchName(form.gitRemoteName, branch)"
            :label="source.formatRemoteBranchName(form.gitRemoteName, branch)"
          >{{source.formatRemoteBranchName(form.gitRemoteName, branch)}}</el-radio>
        </el-form-item>

        <el-form-item label="未暂存文件" prop="not_staged">
          <div class="coos-row col-8">
            <el-input
              type="input"
              placeholder="输入搜索"
              v-model="search_not_staged"
              autocomplete="off"
            ></el-input>
          </div>
          <div class="coos-row col-4 text-right">
            <a class="coos-link color-grey ft-12 mgr-10" @click="stagedAll()">暂存所有</a>
            <a class="coos-link color-grey ft-12" @click="stagedChecked()">暂存选定</a>
          </div>
          <div class="col-12 mgt-5 coos-row file-box coos-scrollbar">
            <div v-show="not_staged.length == 0" class="text-center">暂无数据</div>
            <el-checkbox-group v-model="not_staged_checked">
              <el-checkbox
                v-for="one in not_staged"
                :label="one.path"
                :key="one.path"
                v-show="coos.isEmpty(search_not_staged) || one.path.toLowerCase().indexOf(search_not_staged.toLowerCase()) >= 0"
              >{{one.text}}</el-checkbox>
            </el-checkbox-group>
          </div>
        </el-form-item>
        <el-form-item label="已暂存文件" prop="staged">
          <div class="coos-row col-8">
            <el-input type="input" placeholder="输入搜索" v-model="search_staged" autocomplete="off"></el-input>
          </div>
          <div class="coos-row col-4 text-right">
            <a class="coos-link color-grey ft-12 mgr-10" @click="cancelAll()">取消所有暂存</a>
            <a class="coos-link color-grey ft-12" @click="cancelChecked()">取消选定暂存</a>
          </div>
          <div class="col-12 mgt-5 coos-row file-box coos-scrollbar">
            <div v-show="staged.length == 0" class="text-center">暂无数据</div>
            <el-checkbox-group v-model="staged_checked">
              <el-checkbox
                v-for="one in staged"
                :label="one.path"
                :key="one.path"
                v-show="coos.isEmpty(search_staged) || one.path.toLowerCase().indexOf(search_staged.toLowerCase()) >= 0"
              >{{one.text}}</el-checkbox>
            </el-checkbox-group>
          </div>
        </el-form-item>

        <el-form-item label="未推送成功">{{not_push_message}}</el-form-item>

        <el-form-item label="备注" prop="message">
          <el-input type="textarea" v-model="form.message" autocomplete="off"></el-input>
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
      title: "Git Push (推送)",
      labelWidth: "120px",
      not_push_message: "暂无",
      size: "mini",
      source: source,
      show_form: false,
      staged: [],
      staged_checked: [],
      not_staged: [],
      not_staged_checked: [],
      search_not_staged: "",
      search_staged: "",
      form: {
        gitRemoteName: "",
        gitRemoteBranch: "",
        branchName: "",
        message: "",
        paths: [],
        remoteList: [],
        branchList: []
      },
      form_rules: {
        gitRemoteName: [
          {
            required: true,
            message: "请选择远程名称",
            trigger: "blur"
          }
        ],
        branchName: [
          {
            required: true,
            message: "请选择本地分支",
            trigger: "blur"
          }
        ],
        gitRemoteBranch: [
          {
            required: true,
            message: "请选择远程分支",
            trigger: "blur"
          }
        ],
        paths_: [
          {
            required: true,
            message: "请选择需要提交的文件",
            trigger: "blur"
          }
        ],
        message: [
          {
            required: true,
            message: "请输入备注信息",
            trigger: "blur"
          }
        ]
      }
    };
  },
  methods: {
    show() {
      let data = {};

      this.change_files = [];

      source.repository.change_files.forEach(one => {
        this.change_files.push({
          path: one.key,
          value: one.path,
          text: one.label,
          status: one.status
        });
      });
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
      let form = this.form;
      let git = source.repository.git;
      if (git) {
        if (git.reflogs && git.reflogs.length > 0) {
          // this.not_push_message = "";
          // git.reflogs.forEach(one => {
          //   let comment = one.comment;
          //   if (comment && comment.startsWith("commit:")) {
          //     this.not_push_message += "[" + comment + "]";
          //   }
          // });
          // if (this.not_push_message.length == 0) {
          //   this.not_push_message = "暂无";
          // }
        }

        let remoteList = git.remoteList || [];
        let branchList = git.branchList || [];

        coos.trimList(form.remoteList);
        remoteList.forEach(one => {
          form.remoteList.push(one);
        });
        coos.trimList(form.branchList);
        branchList.forEach(one => {
          form.branchList.push(one);
        });

        remoteList.forEach(one => {
          if (git.option && git.option.gitRemoteName == one.name) {
            form.gitRemoteName = one.name;

            branchList.forEach(b => {
              if (source.isRemoteBranchName(one.name, b)) {
                let gitRemoteBranch = source.formatRemoteBranchName(
                  one.name,
                  b
                );
                if (
                  git.option &&
                  git.option.gitRemoteBranch == gitRemoteBranch
                ) {
                  form.gitRemoteBranch = gitRemoteBranch;
                }
              }
            });
          }
        });
        branchList.forEach(one => {
          let branchName = source.formatHeadBranchName(one);
          if (branchName == source.branch) {
            form.branchName = branchName;
          }
        });
      }

      this.refresh();
      return new Promise((resolve, reject) => {
        this.resolve = resolve;
        this.reject = reject;
      });
    },
    stagedAll() {
      coos
        .confirm("此操作将暂存所有未提交的文件，确定暂存所有文件？")
        .then(res => {
          this.not_staged.forEach(one => {
            this.staged.push(one);
          });
          coos.trimList(this.not_staged);
          coos.trimList(this.not_staged_checked);
        });
    },
    stagedChecked() {
      this.not_staged_checked.forEach(path => {
        this.not_staged.forEach(one => {
          if (one.path == path) {
            this.staged.push(one);
            this.not_staged.splice(this.not_staged.indexOf(one), 1);
          }
        });
      });
      coos.trimList(this.not_staged_checked);
    },
    cancelAll() {
      coos.confirm("确定取消所有文件的暂存？").then(res => {
        this.staged.forEach(one => {
          this.not_staged.push(one);
        });
        coos.trimList(this.staged);
        coos.trimList(this.staged_checked);
      });
    },
    cancelChecked() {
      this.staged_checked.forEach(path => {
        this.staged.forEach(one => {
          if (one.path == path) {
            this.not_staged.push(one);
            this.staged.splice(this.staged.indexOf(one), 1);
          }
        });
      });
      coos.trimList(this.staged_checked);
    },
    refresh() {
      coos.trimList(this.staged);
      coos.trimList(this.staged_checked);
      coos.trimList(this.not_staged);
      coos.trimList(this.not_staged_checked);
      let added = [];
      if (source.repository.git.status) {
        added = source.repository.git.status.added || [];
      }
      added.forEach(one => {
        this.staged.push({
          path: one,
          value: one,
          text: one + "（新增）"
        });
      });

      let changed = [];
      if (source.repository.git.status) {
        changed = source.repository.git.status.changed || [];
      }
      changed.forEach(one => {
        this.staged.push({
          path: one,
          value: one,
          text: one + "（更改）"
        });
      });

      let removed = [];
      if (source.repository.git.status) {
        removed = source.repository.git.status.removed || [];
      }
      removed.forEach(one => {
        this.staged.push({
          path: one,
          value: one,
          text: one + "（移除）"
        });
      });

      this.change_files.forEach(one => {
        if (this.staged.indexOf(one) < 0) {
          this.not_staged.push(one);
        }
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

          let paths = [];

          this.staged.forEach(one => {
            paths.push(one);
          });

          data.paths = paths;

          source.certificateChoose.show().then(certificateid => {
            data.certificateid = certificateid;
            source.do("GIT_PUSH", data).then(res => {
              if (res.errcode == 0) {
                source.loadGitWorkStatus();
              } else {
                coos.error(res.errmsg);
              }
            });
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
.el-checkbox {
  display: block;
}
.file-box {
  border: 1px solid #ddd;
  padding: 10px;
  height: 180px;
  overflow: auto;
}
</style>
