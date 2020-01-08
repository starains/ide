<template>
  <div class>
    <el-dialog
      :title="title"
      :visible.sync="show_form"
      :close-on-click-modal="false"
      width="1000px"
    >
      <el-form
        :model="form"
        :rules="form_rules"
        ref="form"
        :label-width="labelWidth"
        class
        :size="size"
      >
        <el-form-item label="本地分支" prop="branchName">
          <el-radio
            v-for="branch in form.branchList"
            :key="branch.name"
            v-model="form.branchName"
            v-show="$parent.isHeadBranchName(branch)"
            :label="$parent.formatHeadBranchName(branch)"
          >{{$parent.formatHeadBranchName(branch)}}</el-radio>
        </el-form-item>

        <el-form-item label="远程仓库" prop="gitRemote">
          <el-radio
            v-for="remote in form.remoteList"
            :key="remote.name"
            v-model="form.gitRemote"
            :label="remote.name"
            :value="remote.name"
          ></el-radio>
        </el-form-item>

        <el-form-item label="远程分支" prop="remoteBranchName">
          <el-radio
            v-for="branch in form.branchList"
            :key="branch.name"
            v-model="form.remoteBranchName"
            v-show="$parent.isRemoteBranchName(form.gitRemote, branch)"
            :label="$parent.formatRemoteBranchName(form.gitRemote, branch)"
          >{{$parent.formatRemoteBranchName(form.gitRemote, branch)}}</el-radio>
        </el-form-item>

        <el-form-item label="暂存文件" prop="paths">
          <el-transfer
            v-model="form.paths"
            :data="change_files"
            :titles="['未暂存文件', '已暂存文件']"
            filterable
            filter-placeholder="请输入名称检索"
            :button-texts="['移除','添加']"
          ></el-transfer>
        </el-form-item>

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
import tool from "@/common/js";

export default {
  components: {},
  data() {
    return {
      title: "Git Push (推送)",
      labelWidth: "120px",
      size: "mini",
      source: source,
      show_form: false,
      change_files: [],
      form: {
        gitRemote: "",
        remoteBranchName: "",
        branchName: "",
        message: "",
        paths: [],
        remoteList: [],
        branchList: []
      },
      form_rules: {
        gitRemote: [
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
        remoteBranchName: [
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
    showForm(data, git) {
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

      let parent = this.$parent;
      this.change_files.splice(0, this.change_files.length);
      parent.change_files.forEach(change_file => {
        this.change_files.push(change_file);
      });

      if (git) {
        let remoteList = git.remoteList || [];
        let branchList = git.branchList || [];
        form.remoteList = remoteList;
        form.branchList = branchList;

        remoteList.forEach(one => {
          if (git.option && git.option.gitRemoteName == one.name) {
            form.gitRemote = one.name;

            branchList.forEach(b => {
              if (parent.isRemoteBranchName(one.name, b)) {
                let rmoteBranchName = parent.formatRemoteBranchName(
                  one.name,
                  b
                );
                if (
                  git.option &&
                  git.option.gitRemoteBranch == rmoteBranchName
                ) {
                  form.remoteBranchName = rmoteBranchName;
                }
              }
            });
          }
        });
        branchList.forEach(one => {
          let branchName = parent.formatHeadBranchName(one);
          if (branchName == this.source.branch) {
            form.branchName = branchName;
          }
        });
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
