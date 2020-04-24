<template>
  <div class="app-min-page" style="width: 1024px;">
    <div class="coos-row">
      <h3 class="bd-0 bdb bdb-orange bdb-3 pd-10 color-orange">用户管理</h3>
      <div class="coos-row pd-10">
        <div class="float-right">
          <a class="coos-btn bg-green" @click="toInsert()">新增</a>
        </div>
      </div>
      <div class="coos-row pd-10">
        <el-table :data="list" style="width: 100%">
          <el-table-column prop="photo" label="照片" width="100"></el-table-column>
          <el-table-column prop="name" label="名称" width="100"></el-table-column>
          <el-table-column prop="loginname" label="登录名称" width="100"></el-table-column>
          <el-table-column prop="email" label="邮箱"></el-table-column>
          <el-table-column label="状态">
            <template slot-scope="scope">
              <template v-for="one in source.ENUM_MAP.USER_STATUS">
                <div :key="one.value" v-if="scope.row.status == one.value">{{one.text}}</div>
              </template>
            </template>
          </el-table-column>
          <el-table-column prop="activestatus" label="激活状态">
            <template slot-scope="scope">
              <template v-for="one in source.ENUM_MAP.USER_ACTIVE_STATUS">
                <div :key="one.value" v-if="scope.row.activestatus == one.value">{{one.text}}</div>
              </template>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <a class="coos-btn coos-btn-xs bg-blue" @click="toUpdate(scope.row)">修改</a>
              <a
                v-if="scope.row.status == 1"
                class="coos-btn coos-btn-xs bg-red"
                @click="doDisable(scope.row)"
              >禁用</a>
              <a
                v-if="scope.row.status == 1"
                class="coos-btn coos-btn-xs bg-orange"
                @click="doLock(scope.row)"
              >锁定</a>
              <a
                v-if="scope.row.status == 2"
                class="coos-btn coos-btn-xs bg-green"
                @click="doUnlock(scope.row)"
              >解锁</a>
              <a
                v-if="scope.row.status == 3"
                class="coos-btn coos-btn-xs bg-green"
                @click="doEnable(scope.row)"
              >启用</a>
              <a
                v-if="scope.row.activestatus != 1"
                class="coos-btn coos-btn-xs bg-green"
                @click="doActive(scope.row)"
              >激活</a>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          layout="prev, pager, next"
          :total="totalcount"
          :page-size="pagesize"
          @current-change="initData"
        ></el-pagination>
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
        <el-form-item label="头像" prop="photo">
          <el-input v-model="form.photo" type="text" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="登录名称" prop="loginname">
          <el-input v-model="form.loginname" type="text" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" type="text" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="type">
          <el-select v-model="form.status" placeholder="请选择">
            <template v-for="one in source.ENUM_MAP.USER_STATUS">
              <el-option :key="one.value" :label="one.text" :value="one.value"></el-option>
            </template>
          </el-select>
        </el-form-item>
        <el-form-item label="激活状态" prop="type">
          <el-select v-model="form.activestatus" placeholder="请选择">
            <template v-for="one in source.ENUM_MAP.USER_ACTIVE_STATUS">
              <el-option :key="one.value" :label="one.text" :value="one.value"></el-option>
            </template>
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
  name: "UserIndex",
  data() {
    return {
      source: source,
      list: [],
      pageindex: 1,
      pagesize: 10,
      totalcount: 0,
      title: "编辑",
      labelWidth: "120px",
      size: "mini",
      show_form: false,
      form: {
        id: "",
        name: "",
        photo: "",
        loginname: "",
        email: "",
        status: "",
        activestatus: ""
      },
      form_rules: {
        name: [
          {
            required: true,
            message: "请输入名称",
            trigger: "blur"
          }
        ],
        loginname: [
          {
            required: true,
            message: "请输入登录名称",
            trigger: "blur"
          }
        ],
        email: [
          {
            required: true,
            message: "请输入邮箱",
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
            source.do("USER_CREATE", data).then(res => {
              if (res.errcode == 0) {
                coos.success("新增成功！");
                this.initData(this.pageindex);
              } else {
                coos.error(res.errmsg);
              }
            });
          } else {
            source.do("USER_UPDATE", data).then(res => {
              if (res.errcode == 0) {
                coos.success("修改成功！");
                this.initData(this.pageindex);
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
    initData(pageindex) {
      pageindex = pageindex || 1;
      let data = {};
      data.pageindex = pageindex;
      data.pagesize = this.pagesize;
      source.load("USERS", data).then(res => {
        this.pageindex = res.pageindex;
        this.totalcount = res.totalcount;
        coos.trimList(this.list);
        if (res.value) {
          res.value.forEach(one => {
            this.list.push(one);
          });
        }
      });
    },
    doDisable(data) {
      coos
        .confirm("确定禁用该用户？")
        .then(res => {
          source.do("USER_DISABLE", { id: data.id }).then(res => {
            if (res.errcode == 0) {
              coos.success("禁用成功！");
              this.initData(this.pageindex);
            } else {
              coos.error(res.errmsg);
            }
          });
        })
        .catch(() => {});
    },
    doEnable(data) {
      source.do("USER_ENABLE", { id: data.id }).then(res => {
        if (res.errcode == 0) {
          coos.success("启用成功！");
          this.initData(this.pageindex);
        } else {
          coos.error(res.errmsg);
        }
      });
    },
    doLock(data) {
      coos
        .confirm("确定锁定该用户？")
        .then(res => {
          source.do("USER_LOCK", { id: data.id }).then(res => {
            if (res.errcode == 0) {
              coos.success("锁定成功！");
              this.initData(this.pageindex);
            } else {
              coos.error(res.errmsg);
            }
          });
        })
        .catch(() => {});
    },
    doUnlock(data) {
      source.do("USER_UNLOCK", { id: data.id }).then(res => {
        if (res.errcode == 0) {
          coos.success("解锁成功！");
          this.initData(this.pageindex);
        } else {
          coos.error(res.errmsg);
        }
      });
    },
    doActive(data) {
      source.do("USER_ACTIVE", { id: data.id }).then(res => {
        if (res.errcode == 0) {
          coos.success("激活成功！");
          this.initData(this.pageindex);
        } else {
          coos.error(res.errmsg);
        }
      });
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
