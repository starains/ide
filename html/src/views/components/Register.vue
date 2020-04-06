<template>
  <div class="register-page" :class="{'show-register-page':source.show_register}">
    <div class="register-box">
      <div class="register-content">
        <h3 class="text-center pd-10 color-white mgb-10">账号注册</h3>
        <el-form
          class="coos-row"
          :model="form"
          status-icon
          :rules="rules"
          ref="register-form"
          label-width="100px"
        >
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
        </el-form>
        <a class="coos-btn bg-orange coos-btn-block mgt-10" @click="doRegister()">注册</a>
        <div class="mgt-10 pdb-20">
          <div class="float-right">
            已有账号？
            <a class="coos-link color-white" @click="toLogin()">现在登录</a>
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
          { required: true, message: "请输入登录账号！", trigger: "blur" },
          {
            pattern: /^(?!_)(?!.*?_$)(?!\.)(?!.*?\.$)(?!-)(?!.*?-$)[a-zA-Z0-9_\.\-]{1,20}$/,
            message: "请输入正确的账号格式！"
          }
        ],
        email: [
          { required: true, message: "请输入邮箱！", trigger: "blur" },
          {
            pattern: /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/,
            message: "请输入正确的邮箱格式！"
          }
        ],
        password: [
          { required: true, message: "请输入登录密码！", trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    toLogin() {
      source.toLogin();
    },
    doRegister() {
      let that = this;
      this.$refs["register-form"].validate(valid => {
        if (valid) {
          let data = this.form;
          source.do("REGISTER", data).then(res => {
            if (res.errcode == 0) {
              coos.success("注册成功，正在跳转到登录页.");
              window.setTimeout(function() {
                that.toLogin();
              }, 500);
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
    let that = this;
    $(this.$el).click(function(e) {
      if ($(e.target).closest(".register-box").length > 0) {
        return;
      }
      source.show_register = false;
    });
  },
  beforeCreate() {}
};
</script>

<style  >
.register-page {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 100;
  top: 0px;
  left: 0px;
  transition: all 0.3s;
  transform: scale(0);
}
.register-page.show-register-page {
  transform: scale(1);
}
.register-page .register-box {
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
  margin-top: -230px;
}
.register-page .register-content {
  padding: 15px;
}
</style>
