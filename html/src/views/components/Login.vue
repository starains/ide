<template>
  <div class="login-page" :class="{'show-login-page':source.show_login}">
    <div class="login-box">
      <div class="login-content">
        <h3 class="text-center pd-10 color-white mgb-10">用户登录</h3>
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

          <a class="coos-btn bg-green coos-btn-block coos-disabled" v-if="loading">登录中</a>
          <a class="coos-btn bg-green coos-btn-block" @click="doLogin()" v-if="!loading">登录</a>
        </el-form>
        <div
          class="mgt-10 pdb-20"
          v-if="source.CONFIGURE.account!=null && source.CONFIGURE.account.openregister"
        >
          <div class="float-right">
            暂无账号？
            <a class="coos-link color-white" @click="toRegister()">注册账号</a>
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
      loading: false,
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
      source.toRegister();
    },
    doLogin() {
      this.$refs["login-form"].validate(valid => {
        if (valid) {
          let data = this.form;
          this.loading = true;
          source.do("LOGIN", data).then(res => {
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
                this.loading = false;
                window.location.reload();
              }, 300);
            } else {
              coos.error(res.errmsg);
              this.loading = false;
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
    let that = this;
    $(this.$el).click(function(e) {
      if ($(e.target).closest(".login-box").length > 0) {
        return;
      }
      source.show_login = false;
    });
  },
  beforeCreate() {}
};
</script>

<style  >
.login-page {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 1000000000;
  top: 0px;
  left: 0px;
  transition: all 0.3s;
  transform: scale(0);
}
.login-page.show-login-page {
  transform: scale(1);
}
.login-page .login-box {
  position: relative;
  width: 360px;
  background-color: rgba(0, 0, 0, 0.5);
  -webkit-box-shadow: 1px 10px 30px -10px rgba(0, 0, 0, 0.5);
  box-shadow: 1px 10px 30px -10px rgba(0, 0, 0, 0.5);
  background: #aac4bc;
  background: linear-gradient(135deg, #aac4bc 0%, #eca8a8 100%, #eed5a9 100%);
  filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#cfd8dc', endColorstr='#b0bec5',GradientType=1 );
  left: 50%;
  top: 50%;
  margin-left: -180px;
  margin-top: -200px;
}
.login-page .login-content {
  padding: 15px;
}
.login-page .el-checkbox.is-checked .el-checkbox__inner {
  background-color: #4caf50;
  border-color: #4caf50;
}
.login-page .el-checkbox.is-checked .el-checkbox__label {
  color: #4caf50;
}
</style>
