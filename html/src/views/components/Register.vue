<template>
  <div class="register-page">
    <div class="register-box">
      <div class="register-content">
        <h2 class="text-center pd-10 color-orange mgb-10">账号注册</h2>
        <el-form :model="form" status-icon :rules="rules" ref="register-form" label-width="100px">
          <el-form-item class label="用户名称" prop="name">
            <el-input type="text" v-model="form.name" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item class label="登录账号" prop="loginname">
            <el-input type="text" v-model="form.loginname" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input type="text" v-model="form.email" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="登录密码" prop="password">
            <el-input type="password" v-model="form.password" autocomplete="off"></el-input>
          </el-form-item>
          <a class="coos-btn bg-orange coos-btn-block" @click="doRegister()">注册</a>
        </el-form>
        <div class="mgt-10 pdb-20">
          <div class="float-right">
            已有账号？
            <a class="coos-link color-green" @click="toLogin()">现在登录</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import store from "@/store";

export default {
  name: "Res",
  components: {},
  data() {
    return {
      form: {
        name: "",
        email: "",
        loginname: "",
        password: ""
      },
      rules: {
        name: [
          { required: true, message: "请输入用户名称！", trigger: "blur" }
        ],
        loginname: [
          { required: true, message: "请输入登录账号！", trigger: "blur" }
        ],
        email: [{ required: true, message: "请输入邮箱！", trigger: "blur" }],
        password: [
          { required: true, message: "请输入登录密码！", trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    toLogin() {
      source.toRegister = false;
    },
    doRegister() {
      let that = this;
      this.$refs["register-form"].validate(valid => {
        if (valid) {
          let data = this.form;
          source.do("REGISTER", data).then(status => {
            coos.success("注册成功，正在跳转到登录页.");
            window.setTimeout(function() {
              that.toLogin();
            }, 500);
          });
        } else {
          return false;
        }
      });
    },
    init() {
      let that = this;
      $(".register-page").on("keydown", function(e) {
        var theEvent = window.event || e;
        var code = theEvent.keyCode || theEvent.which;
        if (code == 13) {
          that.doRegister();
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
.register-page {
  position: fixed;
  width: 100%;
  height: 100%;
  background-color: #ddd;
  z-index: 1;
  left: 0px;
  top: 0px;
}
.register-page .register-box {
  position: relative;
  left: 50%;
  top: 50%;
  width: 400px;
}
.register-page .register-content {
  width: 400px;
  padding: 15px;
  background-color: #f7f7f7;
  box-shadow: 1px 1px 7px #d0d0d0;
  margin-left: -50%;
  margin-top: -60%;
}
</style>
