<template>
  <div class="login-page">
    <div class="login-box">
      <div class="login-content">
        <h2 class="text-center pd-10 color-green mgb-10">用户登录</h2>
        <el-form :model="form" status-icon :rules="rules" ref="login-form">
          <el-form-item class label prop="loginname">
            <el-input type="text" v-model="form.loginname" autocomplete="off">
              <i slot="prefix" class="el-input__icon coos-icon coos-icon-user ft-20"></i>
            </el-input>
          </el-form-item>
          <el-form-item class="mgb-10" label prop="password">
            <el-input type="password" v-model="form.password" autocomplete="off">
              <i slot="prefix" class="el-input__icon coos-icon coos-icon-lock ft-20"></i>
            </el-input>
          </el-form-item>
          <el-form-item class="aaaaa">
            <el-checkbox v-model="form.rememberpassword" class="rememberpassword">记住密码</el-checkbox>
          </el-form-item>
          <a class="coos-btn bg-green coos-btn-block" @click="doLogin()">登录</a>
        </el-form>
        <div class="mgt-10 pdb-20" v-if="source.CONFIGURE.openregister">
          <div class="float-right">
            暂无账号？
            <a class="coos-link color-orange" @click="toRegister()">注册账号</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import store from "@/store";

export default {
  name: "Login",
  components: {},
  data() {
    let loginname = "";
    let password = "";
    let rememberpassword = "";
    if (window.localStorage) {
      rememberpassword = localStorage.getItem("rememberpassword");
      if (coos.isTrue(rememberpassword)) {
        loginname = localStorage.getItem("loginname");
        password = localStorage.getItem("password");
      }
    }
    return {
      source: source,
      form: {
        loginname: loginname,
        password: password,
        rememberpassword: coos.isTrue(rememberpassword)
      },
      rules: {
        loginname: [
          { required: true, message: "请输入登录名称！", trigger: "blur" }
        ],
        password: [
          { required: true, message: "请输入登录密码！", trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    toRegister() {
      source.toRegister = true;
    },
    doLogin() {
      this.$refs["login-form"].validate(valid => {
        if (valid) {
          let data = this.form;
          source.server.do("LOGIN", data).then(res => {
            if (res.errcode == 0) {
              coos.success("登录成功.");
              try {
                if (window.localStorage) {
                  if (coos.isTrue(data.rememberpassword)) {
                    localStorage.setItem("loginname", data.loginname);
                    localStorage.setItem("password", data.password);
                    localStorage.setItem("rememberpassword", "true");
                  } else {
                    localStorage.removeItem("loginname");
                    localStorage.removeItem("password");
                    localStorage.removeItem("rememberpassword");
                  }
                }
              } catch (e) {}
              window.setTimeout(() => {
                window.location.reload();
              }, 300);
            } else {
              coos.error(res.errmsg);
            }
          });
        } else {
          return false;
        }
      });
    },
    init() {
      let that = this;
      $(".login-page").on("keydown", function(e) {
        var theEvent = window.event || e;
        var code = theEvent.keyCode || theEvent.which;
        if (code == 13) {
          that.doLogin();
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
.login-page {
  position: fixed;
  width: 100%;
  height: 100%;
  background-color: #ddd;
  z-index: 1;
  left: 0px;
  top: 0px;
}
.login-page .login-box {
  position: relative;
  left: 50%;
  top: 50%;
  width: 350px;
}
.login-page .login-content {
  padding: 15px;
  background-color: #f7f7f7;
  box-shadow: 1px 1px 7px #d0d0d0;
  margin-left: -50%;
  margin-top: -60%;
  width: 350px;
}
.login-page .el-checkbox.is-checked .el-checkbox__inner {
  background-color: #4caf50;
  border-color: #4caf50;
}
.login-page .el-checkbox.is-checked .el-checkbox__label {
  color: #4caf50;
}
</style>
