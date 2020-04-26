<template>
  <div class="app-min-page">
    <div class="coos-row pd-20">
      <div class="coos-row">
        <h3 class="bd-0 bdb bdb-orange bdb-3 pd-10 color-orange">系统设置</h3>

        <h4 class="pd-10 color-grey">账号设置</h4>
        <el-form
          class="coos-row"
          :model="account"
          status-icon
          :rules="rules"
          ref="account"
          label-width="150px"
          size="mini"
        >
          <el-form-item class label="开启注册" prop="name">
            <el-switch type="text" v-model="account.openregister"></el-switch>
          </el-form-item>
          <el-form-item class label="开启激活" prop="name">
            <el-switch type="text" v-model="account.openactivation"></el-switch>
          </el-form-item>
          <el-form-item class label="默认密码" prop="defaultpassword">
            <el-input type="text" v-model="account.defaultpassword" autocomplete="off"></el-input>
          </el-form-item>
        </el-form>

        <h4 class="pd-10 color-grey">登录设置</h4>
        <el-form
          class="coos-row"
          :model="login"
          status-icon
          :rules="rules"
          ref="login"
          label-width="150px"
          size="mini"
        >
          <el-form-item class label="开启锁定" prop>
            <el-switch type="text" v-model="login.openlock"></el-switch>
          </el-form-item>
          <el-form-item class label="失败次数" prop>
            <el-input type="number" v-model="login.faillimit"></el-input>
          </el-form-item>
          <el-form-item class label="自动解锁分钟" prop>
            <el-input type="number" v-model="login.unlockminute"></el-input>
          </el-form-item>
          <el-form-item class label="开启锁定IP" prop>
            <el-switch type="text" v-model="login.openlockip"></el-switch>
          </el-form-item>
          <el-form-item class label="自动解锁IP分钟" prop>
            <el-input type="number" v-model="login.unlockipminute"></el-input>
          </el-form-item>
        </el-form>

        <h4 class="pd-10 color-grey">空间设置</h4>
        <el-form
          class="coos-row"
          :model="space"
          status-icon
          :rules="rules"
          ref="space"
          label-width="150px"
          size="mini"
        >
          <el-form-item class label="禁止创建" prop>
            <el-switch type="text" v-model="space.prohibitcreate"></el-switch>
          </el-form-item>
          <el-form-item class label="最大数量" prop>
            <el-input type="number" v-model="space.maxquantity"></el-input>
          </el-form-item>
        </el-form>

        <h4 class="pd-10 color-grey">仓库设置</h4>
        <el-form
          class="coos-row"
          :model="repository"
          status-icon
          :rules="rules"
          ref="repository"
          label-width="150px"
          size="mini"
        >
          <el-form-item class label="禁止启动" prop>
            <el-switch type="text" v-model="repository.prohibitstarter"></el-switch>
          </el-form-item>
        </el-form>

        <h4 class="pd-10 color-grey">Nginx设置</h4>
        <el-form
          class="coos-row"
          :model="nginx"
          status-icon
          :rules="rules"
          ref="nginx"
          label-width="150px"
          size="mini"
        >
          <el-form-item class label="重启命令" prop>
            <el-input type="text" v-model="nginx.reloadcommand"></el-input>
          </el-form-item>
        </el-form>
        <div class="pd-10">
          <a class="coos-btn bg-green" @click="doSave()">保存</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ConfigureIndex",
  data() {
    let account = {};
    Object.assign(account, source.CONFIGURE.account);
    let login = {};
    Object.assign(login, source.CONFIGURE.login);
    let mailbox = {};
    Object.assign(mailbox, source.CONFIGURE.mailbox);
    let space = {};
    Object.assign(space, source.CONFIGURE.space);
    let repository = {};
    Object.assign(repository, source.CONFIGURE.repository);
    let nginx = {};
    Object.assign(nginx, source.CONFIGURE.nginx);
    return {
      source,
      account,
      login,
      mailbox,
      space,
      repository,
      nginx,
      rules: {
        defaultpassword: [
          { required: true, message: "请输入默认密码！", trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    doSave() {
      this.$refs["account"].validate(valid => {
        if (valid) {
          let data = {};
          data.account = this.account;
          data.login = this.login;
          data.mailbox = this.mailbox;
          data.space = this.space;
          data.repository = this.repository;
          data.nginx = this.nginx;
          source.do("CONFIGURE_UPDATE", data).then(res => {
            if (res.errcode == 0) {
              coos.success("修改成功！");
              source.load("CONFIGURE", {});
            } else {
              coos.error(res.errmsg);
            }
          });
        } else {
          return false;
        }
      });
    },
    init() {}
  },
  mounted() {
    this.init();
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
