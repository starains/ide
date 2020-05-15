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
            <a class="coos-btn bg-green mgl-10" @click="toInsert()">新增</a>
          </div>
        </div>
        <div class="coos-row pd-10">
          <el-table :data="list" style="width: 100%">
            <el-table-column prop="name" label="名称" width="200"></el-table-column>
            <el-table-column prop="type" label="类型" width="100"></el-table-column>
            <el-table-column label="配置">
              <template slot-scope="scope">
                <div class="ft-12" v-if="scope.row.option_json != null">
                  <div>
                    url：
                    <span class="color-grey">{{scope.row.option_json.url}}</span>
                  </div>
                  <div>
                    username：
                    <span class="color-grey">{{scope.row.option_json.username}}</span>
                  </div>
                  <div>
                    password：
                    <span class="color-grey">{{scope.row.option_json.password}}</span>
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
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option label="JDBC" value="JDBC"></el-option>
          </el-select>
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
      title: "数据库资源",
      form_title: "用户数据库资源设置",
      labelWidth: "120px",
      size: "mini",
      list: [],
      source: source,
      show_choose: false,
      show_form: false,
      form: { id: "", name: "", type: "" },
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
    show(data) {
      this.show_choose = true;
      return new Promise((resolve, reject) => {
        this.resolve = resolve;
        this.reject = reject;
      });
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
          source.do("DATABASE_DELETE", { id: data.id }).then(res => {
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
    },
    hideForm() {
      this.show_form = false;
    },
    formSave() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          var data = this.form;

          if (coos.isEmpty(data.id)) {
            source.do("DATABASE_CREATE", data).then(res => {
              if (res.errcode == 0) {
                coos.success("新增成功！");
                this.load();
                this.hideForm();
              } else {
                coos.error(res.errmsg);
              }
            });
          } else {
            source.do("DATABASE_UPDATE", data).then(res => {
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
      source.load("DATABASES", {}).then(res => {
        res.value = res.value || [];
        res.value.forEach(one => {
          one.option_json = null;
          if (coos.isNotEmpty(one.option)) {
            one.option_json = JSON.parse(one.option);
          }
          this.list.push(one);
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
