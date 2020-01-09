<template>
  <div class="app-min-page">
    <div class="coos-row">
      <h3 class="pd-10 color-orange">环境配置</h3>
      <div class="coos-row pd-10">
        <div class="float-right">
          <a class="coos-btn bg-green" @click="toInsert()">新增</a>
        </div>
      </div>
      <div class="coos-row pd-10">
        <el-table :data="source.data.ENVIRONMENTS" style="width: 100%">
          <el-table-column prop="name" label="名称" width="100"></el-table-column>
          <el-table-column prop="type" label="类型" width="100"></el-table-column>
          <el-table-column prop="version" label="版本" width="100"></el-table-column>
          <el-table-column prop="path" label="路径"></el-table-column>
          <el-table-column>
            <template slot-scope="scope">
              <a class="coos-btn bg-green" @click="toUpdate(scope.row)">修改</a>
              <a class="coos-btn bg-red" @click="toDelete(scope.row)">删除</a>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <el-dialog :title="title" :visible.sync="show_form" :close-on-click-modal="false">
      <el-form
        :model="form"
        :rules="form_rules"
        ref="form"
        :label-width="labelWidth"
        class
        :size="size"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" type="text" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option label="Java" value="JAVA"></el-option>
            <el-option label="Tomcat" value="TOMCAT"></el-option>
            <el-option label="Git" value="GIT"></el-option>
            <el-option label="Maven" value="MAVEN"></el-option>
            <el-option label="Node" value="NODE"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="版本" prop="version">
          <el-input v-model="form.version" type="text" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="路径" prop="path">
          <el-input v-model="form.path" type="text" autocomplete="off"></el-input>
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
  name: "EnvironmentIndex",
  data() {
    return {
      source: source,
      list: null,
      title: "编辑",
      labelWidth: "120px",
      size: "mini",
      source: source,
      show_form: false,
      form: {
        id: "",
        name: "",
        path: "",
        type: "",
        version: ""
      },
      form_rules: {
        name: [
          {
            required: true,
            message: "请输入名称",
            trigger: "blur"
          }
        ],
        version: [
          {
            required: true,
            message: "请输入版本",
            trigger: "blur"
          }
        ],
        path: [
          {
            required: true,
            message: "请填写路径",
            trigger: "blur"
          }
        ],
        type: [
          {
            required: true,
            message: "请选中类型",
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
    },
    hideForm() {
      this.show_form = false;
    },
    formSave() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          var data = this.form;

          this.hideForm();

          if (coos.isEmpty(data.id)) {
            source.do("ENVIRONMENT_CREATE", data).then(res => {
              if (res.errcode == 0) {
                coos.success("新增成功！");
                source.load("ENVIRONMENTS", {});
              } else {
                coos.error(res.errmsg);
              }
            });
          } else {
            source.do("ENVIRONMENT_UPDATE", data).then(res => {
              if (res.errcode == 0) {
                coos.success("修改成功！");
                source.load("ENVIRONMENTS", {});
              } else {
                coos.error(res.errmsg);
              }
            });
          }
        } else {
          return false;
        }
      });
    },
    formCancel() {
      this.hideForm();
    },
    toInsert(data) {
      this.showForm(data);
    },
    toUpdate(data) {
      this.showForm(data);
    },
    toDelete(data) {
      let that = this;

      coos
        .confirm("确定删除该记录？")
        .then(res => {
          source.do("ENVIRONMENT_DELETE", { id: data.id }).then(res => {
            if (res.errcode == 0) {
              coos.success("删除成功！");
              source.load("ENVIRONMENTS", {});
            } else {
              coos.error(res.errmsg);
            }
          });
        })
        .catch(() => {});
    },
    init() {
      source.load("ENVIRONMENTS", {});
    }
  },
  mounted() {
    this.init();
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
