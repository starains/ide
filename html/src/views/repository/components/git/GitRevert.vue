<template>
  <div class>
    <el-dialog
      :title="title"
      :visible.sync="show_form"
      :close-on-click-modal="false"
      width="1000px"
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
        <el-form-item label="选择" prop="not_staged">
          <div class="coos-row col-8">
            <el-input
              type="input"
              placeholder="输入搜索"
              v-model="search_not_staged"
              autocomplete="off"
            ></el-input>
          </div>
          <div class="coos-row col-4 text-right"></div>
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
      title: "Git Revert（还原文件）",
      labelWidth: "120px",
      size: "mini",
      source: source,
      show_form: false,
      staged: [],
      staged_checked: [],
      not_staged: [],
      not_staged_checked: [],
      form: {},
      form_rules: {},
      search_not_staged: ""
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

      this.refresh();
      return new Promise((resolve, reject) => {
        this.resolve = resolve;
        this.reject = reject;
      });
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

          this.not_staged_checked.forEach(one => {
            paths.push(one);
          });

          data.paths = paths;
          if (paths.length > 0) {
            source.gitRevert(paths);
          }

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
  height: 400px;
  overflow: auto;
}
</style>
