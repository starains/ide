<template>
  <div class="app-min-page">
    <div class="coos-row">
      <h3 class="bd-0 bdb bdb-orange bdb-3 pd-10 color-orange">部署服务器配置</h3>
      <div class="coos-row pd-10">
        <div class="float-right">
          <a class="coos-btn bg-green" @click="toInsert()">新增</a>
        </div>
      </div>
      <div class="coos-row pd-10">
        <el-table :data="source.data.REMOTES" style="width: 100%">
          <el-table-column prop="name" label="名称" width="100"></el-table-column>
          <el-table-column prop="token" label="令牌" width="100"></el-table-column>
          <el-table-column prop="mode" label="模式" width="100"></el-table-column>
          <el-table-column prop="server" label="地址" width="100"></el-table-column>
          <el-table-column prop="status" label="连接状态" width="100"></el-table-column>

          <el-table-column prop="errmsg" label="错误信息" width="100"></el-table-column>
          <el-table-column label="操作">
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
        <el-form-item label="令牌" prop="token">
          <el-input v-model="form.token" type="text" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="模式" prop="mode">
          <el-select v-model="form.mode" placeholder="请选择">
            <el-option label="服务模式（由TeamIDE服务器连接部署服务器）" value="SERVER"></el-option>
            <el-option label="客户端模式（由部署服务器连接TeamIDE服务器）" value="CLIENT"></el-option>
          </el-select>
        </el-form-item>
        <template v-if="form.mode == 'SERVER'">
          <el-form-item label="部署服务器地址" prop="server">
            <el-input v-model="form.server" type="text" autocomplete="off"></el-input>
          </el-form-item>
        </template>
        <template v-if="form.mode == 'CLIENT'">
          <el-form-item label="TeamIDE服务器地址" prop="server">
            <el-input v-model="form.server" type="text" autocomplete="off"></el-input>
          </el-form-item>
        </template>

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
  name: "RemoteIndex",
  data() {
    return {
      source: source,
      list: null,
      title: "编辑",
      labelWidth: "170px",
      size: "mini",
      source: source,
      show_form: false,
      form: {
        id: "",
        name: "",
        token: "",
        mode: "",
        server: ""
      },
      form_rules: {
        name: [
          {
            required: true,
            message: "请输入名称",
            trigger: "blur"
          }
        ],
        token: [
          {
            required: true,
            message: "请输入令牌",
            trigger: "blur"
          }
        ],
        mode: [
          {
            required: true,
            message: "请选中模式",
            trigger: "blur"
          }
        ],
        server: [
          {
            required: true,
            message: "请填写部署服务器地址",
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
            source.do("REMOTE_CREATE", data).then(res => {
              if (res.errcode == 0) {
                coos.success("新增成功！");
                source.load("REMOTES", {});
              } else {
                coos.error(res.errmsg);
                window.setTimeout(res => {
                  this.show_form = true;
                }, 300);
              }
            });
          } else {
            source.do("REMOTE_UPDATE", data).then(res => {
              if (res.errcode == 0) {
                coos.success("修改成功！");
                source.load("REMOTES", {});
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
          source.do("REMOTE_DELETE", { id: data.id }).then(res => {
            if (res.errcode == 0) {
              coos.success("删除成功！");
              source.load("REMOTES", {});
            } else {
              coos.error(res.errmsg);
            }
          });
        })
        .catch(() => {});
    },
    init() {
      source.load("REMOTES", {});
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
