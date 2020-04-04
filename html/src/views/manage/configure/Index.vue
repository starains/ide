<template>
  <div class="app-min-page">
    <div class="coos-row pd-20">
      <div class="coos-row">
        <h3 class="bd-0 bdb bdb-orange bdb-3 pd-10 color-orange">系统设置</h3>

        <el-form
          class="coos-row mgt-20"
          :model="form"
          status-icon
          :rules="rules"
          ref="form"
          label-width="100px"
        >
          <el-form-item class label="开启注册" prop="name">
            <el-switch type="text" v-model="form.openregister"></el-switch>
          </el-form-item>
          <el-form-item class label="默认密码" prop="defaultpassword">
            <el-input type="text" v-model="form.defaultpassword" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item>
            <a class="coos-btn bg-green" @click="doSave()">保存</a>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ConfigureIndex",
  data() {
    return {
      source,
      form: {
        openregister: source.CONFIGURE.openregister,
        defaultpassword: source.CONFIGURE.defaultpassword
      },
      rules: {
        defaultpassword: [
          { required: true, message: "请输入默认密码！", trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    doSave() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          let data = this.form;
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
