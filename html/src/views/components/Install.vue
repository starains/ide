<template>
  <div class="install-page">
    <div class="coos-row mgt-100">
      <div class="install-form-box coos-row">
        <div
          class="coos-row install-form-one install-form-database"
          :class="{'show':show == 'database'}"
        >
          <h3 class="bd-0 bdb bdb-orange bdb-3 pd-10 color-orange mgb-20">配置平台数据库</h3>
          <el-form
            :model="database.form"
            status-icon
            :rules="database.rules"
            ref="database-form"
            label-width="100px"
          >
            <el-form-item class label="驱动" prop="driver">
              <el-input type="text" v-model="database.form.driver" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item class label="连接" prop="url">
              <el-input type="text" v-model="database.form.url" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item class label="用户名" prop="username">
              <el-input type="text" v-model="database.form.username" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item class label="密码" prop="password">
              <el-input type="text" v-model="database.form.password" autocomplete="off"></el-input>
            </el-form-item>
          </el-form>
          <div class="coos-row">
            <a class="float-right coos-btn bg-green" @click="submitDatabase()">确认，下一步</a>
          </div>
        </div>

        <div class="coos-row install-form-one install-form-admin" :class="{'show':show == 'admin'}">
          <h3 class="bd-0 bdb bdb-orange bdb-3 pd-10 color-orange mgb-20">配置平台管理员</h3>
          <el-form
            :model="admin.form"
            status-icon
            :rules="admin.rules"
            ref="admin-form"
            label-width="100px"
          >
            <el-form-item class label="登录账号" prop="loginname">
              <el-input type="text" v-model="admin.form.loginname" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item class label="登录密码" prop="password">
              <el-input type="text" v-model="admin.form.password" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item class label="确认密码" prop="repassword">
              <el-input type="text" v-model="admin.form.repassword" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item class label="邮箱" prop="email">
              <el-input type="text" v-model="admin.form.email" autocomplete="off"></el-input>
            </el-form-item>
          </el-form>
          <div class="coos-row">
            <a class="float-left coos-btn bg-orange" @click="back()">上一步</a>

            <a class="float-right coos-btn bg-green" @click="submitAdmin()">确认，下一步</a>
          </div>
        </div>
        <div
          class="coos-row install-form-one install-form-configure"
          :class="{'show':show == 'configure'}"
        >
          <h3 class="bd-0 bdb bdb-orange bdb-3 pd-10 color-orange mgb-20">系统设置</h3>
          <el-form
            :model="configure.form"
            status-icon
            :rules="configure.rules"
            ref="configure-form"
            label-width="100px"
          >
            <el-form-item class label="开启注册" prop="openregister">
              <el-switch v-model="configure.form.openregister"></el-switch>
            </el-form-item>
            <el-form-item class label="默认密码" prop="defaultpassword">
              <el-input type="text" v-model="configure.form.defaultpassword" autocomplete="off"></el-input>
            </el-form-item>
          </el-form>
          <div class="coos-row">
            <a class="float-left coos-btn bg-orange" @click="back()">上一步</a>

            <a class="float-right coos-btn bg-green" @click="submitConfigure()">安装</a>
          </div>
        </div>
        <div class="coos-row install-form-one" :class="{'show':show == 'install'}">
          <h3 class="bd-0 bdb bdb-orange bdb-3 pd-10 color-orange mgb-20">平台初始化</h3>

          <div class="coos-form coos-input-md pd-20">
            <div class="ft-30 pd-20 text-center color-orange install-message">{{message}}</div>
          </div>
          <div class="coos-row">
            <a class="float-left coos-btn bg-orange" @click="back()" :show="installError">上一步</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import tool from "@/common/js";
import store from "@/store";

export default {
  name: "Install",
  components: {},
  data() {
    return {
      installError: false,
      message: "平台初始化中...",
      show: "database",
      database: {
        form: {
          driver: "com.mysql.jdbc.Driver",
          url:
            "jdbc:mysql://127.0.0.1:3306/COOS?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT",
          username: "root",
          password: "123456"
        },
        rules: {
          driver: [
            { required: true, message: "请输入驱动！", trigger: "blur" }
          ],
          url: [{ required: true, message: "请输入连接！", trigger: "blur" }],
          username: [
            { required: true, message: "请输入用户名！", trigger: "blur" }
          ],
          password: [
            { required: true, message: "请输入密码！", trigger: "blur" }
          ]
        }
      },
      admin: {
        form: {
          loginname: "admin",
          password: "123456",
          repassword: "123456",
          email: "admin@admin.com"
        },
        rules: {
          loginname: [
            { required: true, message: "请输入登录名称！", trigger: "blur" }
          ],
          password: [
            { required: true, message: "请输入密码！", trigger: "blur" }
          ],
          repassword: [
            { required: true, message: "请确认密码！", trigger: "blur" }
          ],
          email: [{ required: true, message: "请输入邮箱！", trigger: "blur" }]
        }
      },
      configure: {
        form: {
          openregister: true,
          defaultpassword: "123456"
        },
        rules: {
          defaultpassword: [
            { required: true, message: "请输入默认密码！", trigger: "blur" }
          ]
        }
      }
    };
  },
  methods: {
    back() {
      if (this.show == "admin") {
        this.show = "database";
      } else if (this.show == "configure") {
        this.show = "admin";
      } else if (this.show == "install") {
        this.show = "configure";
      }
    },
    submitDatabase() {
      this.$refs["database-form"].validate(valid => {
        if (valid) {
          this.show = "admin";
        } else {
          return false;
        }
      });
    },
    submitAdmin() {
      this.$refs["admin-form"].validate(valid => {
        if (valid) {
          this.show = "configure";
        } else {
          return false;
        }
      });
    },
    submitConfigure() {
      this.$refs["configure-form"].validate(valid => {
        if (valid) {
          this.show = "install";
          this.doInstall();
        } else {
          return false;
        }
      });
    },
    doInstall() {
      var data = {};
      data.database = this.database.form;
      data.admin = this.admin.form;
      data.configure = this.configure.form;

      source.do("INSTALL", data).then(res => {
        if (res) {
          window.setTimeout(() => {
            window.location.reload();
          }, 500);
        } else {
          this.show = "database";
        }
      });
    },
    init() {
      source.load("JDBC", {}).then(res => {
        if (res && Object.keys(res).length > 0) {
          for (let key in this.database.form) {
            this.database.form[key] = res[key];
          }
        }
      });
    }
  },
  mounted() {
    this.init();
  },
  beforeCreate() {}
};
</script>

<style  >
.install-page {
  position: fixed;
  width: 100%;
  height: 100%;
  background-color: #ddd;
  z-index: 1;
  left: 0px;
  top: 0px;
}

.install-page .install-form-box {
  position: relative;
  width: 60%;
  margin: 0px auto;
}

.install-page .install-form-one {
  opacity: 0;
  transition: ease-in-out 300ms;
  position: absolute;
  background-color: white;
  z-index: 0;
  padding: 20px;
}

.install-page .install-form-one.show {
  opacity: 1;
  z-index: 1;
}
</style>
