<template>
  <div class="app-min-page" style="width: 1200px;">
    <div class="coos-row">
      <h3 class="bd-0 bdb bdb-orange bdb-3 pd-10 color-orange">NginxConfig管理</h3>
      <div class="coos-row pd-10">
        <div class="float-right">
          <a class="coos-btn bg-green" @click="toInsert()">新增</a>
        </div>
      </div>
      <div class="coos-row pd-10">
        <el-table :data="list" style="width: 100%">
          <el-table-column prop="name" label="名称" width="100"></el-table-column>
          <el-table-column prop="type" label="类型" width="100"></el-table-column>
          <el-table-column prop="content" label="内容"></el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <a class="coos-btn bg-blue" @click="toUpdate(scope.row)">修改</a>
              <a class="coos-btn bg-red" @click="doDelete(scope.row)">删除</a>
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
        <el-form-item label="状态" prop="type">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option label="server路由" value="SERVER_ROUTE"></el-option>
            <el-option label="server" value="SERVER"></el-option>
            <el-option label="config" value="CONFIG"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" autocomplete="off"></el-input>
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
  name: "NginxConfigIndex",
  data() {
    return {
      source: source,
      list: [],
      title: "编辑",
      labelWidth: "120px",
      size: "mini",
      show_form: false,
      form: {
        id: "",
        name: "",
        type: "",
        content: ""
      },
      form_rules: {
        name: [
          {
            required: true,
            message: "请输入名称",
            trigger: "blur"
          }
        ],
        type: [
          {
            required: true,
            message: "请选择类型",
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
            source.do("NGINX_CONFIG_CREATE", data).then(res => {
              if (res.errcode == 0) {
                coos.success("新增成功！");
                this.initData();
              } else {
                coos.error(res.errmsg);
              }
            });
          } else {
            source.do("NGINX_CONFIG_UPDATE", data).then(res => {
              if (res.errcode == 0) {
                coos.success("修改成功！");
                this.initData();
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
    initData() {
      let data = {};
      source.load("NGINX_CONFIGS", data).then(res => {
        coos.trimList(this.list);
        if (res.value) {
          res.value.forEach(one => {
            this.list.push(one);
          });
        }
      });
    },
    doDelete(data) {
      coos
        .confirm("确定删除该配置？")
        .then(res => {
          source.do("NGINX_CONFIG_DELETE", { id: data.id }).then(res => {
            if (res.errcode == 0) {
              coos.success("删除成功！");
              this.initData();
            } else {
              coos.error(res.errmsg);
            }
          });
        })
        .catch(() => {});
    }
  },
  mounted() {
    this.initData();
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
