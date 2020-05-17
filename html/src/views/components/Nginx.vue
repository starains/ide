<template>
  <div class>
    <el-dialog
      :title="title"
      :visible.sync="show_choose"
      width="1000px"
      :close-on-click-modal="false"
    >
      <div class="coos-row">
        <div class="coos-row pd-10">
          <div class="float-right">
            <a class="coos-btn bg-green mgl-10" @click="toApply()">申请</a>
          </div>
        </div>
        <div class="coos-row pd-10">
          <el-table :data="list" style="width: 100%">
            <el-table-column prop="name" label="名称" width="200"></el-table-column>
            <el-table-column label="访问地址" width="300">
              <template slot-scope="scope">
                <a class="color-green white-space" :href="scope.row.server" target="blank_">{{scope.row.server}}</a>
              </template>
            </el-table-column>
            <el-table-column label="配置">
              <template slot-scope="scope">
                <div class="ft-12" v-if="scope.row.option_json != null">
                  <div>
                    contextpath：
                    <span class="color-grey">{{scope.row.option_json.contextpath}}</span>
                  </div>
                  <div>
                    port：
                    <span class="color-grey">{{scope.row.option_json.port}}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template slot-scope="scope">
                <a class="coos-btn bg-green coos-btn-sm" @click="toUpdate(scope.row)">修改</a>
                <a class="coos-btn bg-red coos-btn-sm" @click="toDelete(scope.row)">删除</a>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="coos-row pd-10">
          <div class="float-right">
            <a class="coos-btn bg-grey" @click="chooseCancel()">取消</a>
          </div>
        </div>
      </div>
    </el-dialog>
    <el-dialog :title="form_title" :visible.sync="show_form" :close-on-click-modal="false">
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
        <el-form-item label="名称（注记）" prop="name">
          <el-input v-model="form.name" type="text" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="Port" prop="port">
          <el-input v-model="form.port" type="text" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="ContextPath" prop="contextpath">
          <el-input v-model="form.contextpath" type="text" autocomplete="off"></el-input>
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
      title: "Nginx资源",
      form_title: "用户Nginx资源设置",
      labelWidth: "120px",
      size: "mini",
      list: [],
      source: source,
      show_choose: false,
      show_form: false,
      form: { id: "", name: "", port: "", contextpath: "" },
      form_rules: {
        name: [
          {
            required: true,
            message: "请输入名称",
            trigger: "blur"
          }
        ],
        port: [
          {
            required: true,
            message: "请输入端口",
            trigger: "blur"
          }
        ],
        contextpath: [
          {
            required: true,
            message: "请输入项目路径",
            trigger: "blur"
          }
        ]
      }
    };
  },
  methods: {
    show(data) {
      this.show_choose = true;
      return new Promise((resolve, reject) => {
        this.resolve = resolve;
        this.reject = reject;
      });
    },
    toApply(data) {
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
          source.do("NGINX_DELETE", { id: data.id }).then(res => {
            if (res.errcode == 0) {
              coos.success("删除成功！");
              this.load();
            } else {
              coos.error(res.errmsg);
            }
          });
        })
        .catch(() => {});
    },
    chooseCancel() {
      this.show_choose = false;
    },
    choose() {
      this.show_choose = false;

      this.resolve && this.resolve(this.choose_id);
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
      if (data.option_json) {
        for (var key in data.option_json) {
          this.form[key] = data.option_json[key];
        }
      }
    },
    hideForm() {
      this.show_form = false;
    },
    formSave() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          var data = this.form;

          data.option = JSON.stringify({
            port: data.port,
            contextpath: data.contextpath
          });
          if (coos.isEmpty(data.id)) {
            source.do("NGINX_APPLY", data).then(res => {
              if (res.errcode == 0) {
                coos.success("申请成功！");
                this.load();
                this.hideForm();
              } else {
                coos.error(res.errmsg);
              }
            });
          } else {
            source.do("NGINX_UPDATE", data).then(res => {
              if (res.errcode == 0) {
                coos.success("修改成功！");
                this.load();
                this.hideForm();
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
      this.show_choose = true;
    },
    load() {
      coos.trimList(this.list);
      let server = "";
      let wildcarddomain = source.CONFIGURE.nginx.wildcarddomain;
      if (coos.isEmpty(wildcarddomain)) {
        wildcarddomain = "localhost";
      }
      source.load("NGINXS", {}).then(res => {
        res.value = res.value || [];
        res.value.forEach(one => {
          one.option_json = null;
          if (coos.isNotEmpty(one.option)) {
            one.option_json = JSON.parse(one.option);
          }
          if (one.type == "SERVER") {
            if (wildcarddomain.indexOf("*") >= 0) {
              server = wildcarddomain.replace("*", one.domainprefix);
            } else {
              server = one.domainprefix + "." + wildcarddomain;
            }
          } else {
            if (one.option_json) {
              let contextpath = one.option_json.contextpath;
              if (coos.isEmpty(contextpath)) {
                contextpath = "/";
              }
              if (!contextpath.startsWith("/")) {
                contextpath = "/" + contextpath;
              }

              one.server = "http://" + server + "" + contextpath;
            }
            this.list.push(one);
          }
        });
      });
    }
  },
  mounted() {
    this.load();
  },
  beforeCreate() {}
};
</script>

<style  scoped="scoped">
</style>
