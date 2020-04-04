<template>
  <div class="app-min-page">
    <div class="coos-row pd-20">
      <div class="coos-row">
        <h3 class="bd-0 bdb bdb-orange bdb-3 pd-10 color-orange">用户中心</h3>

        <el-form
          class="coos-row mgt-20"
          :model="form"
          status-icon
          :rules="rules"
          ref="form"
          label-width="100px"
        >
          <el-form-item class label="原密码" prop="oldpassword">
            <el-input type="password" v-model="form.oldpassword" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item class label="新密码" prop="newpassword">
            <el-input type="password" v-model="form.newpassword" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item class label="确认密码" prop="repassword">
            <el-input type="password" v-model="form.repassword" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item>
            <a class="coos-btn bg-green" @click="doSave()">修改密码</a>
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
        oldpassword: "",
        newpassword: "",
        repassword: ""
      },
      rules: {
        oldpassword: [
          { required: true, message: "请输入原密码！", trigger: "blur" }
        ],
        newpassword: [
          { required: true, message: "请输入新密码！", trigger: "blur" }
        ],
        repassword: [
          { required: true, message: "请确认新密码！", trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    doSave() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          let data = this.form;
          source.do("UPDATE_PASSWORD", data).then(res => {
            if (res.errcode == 0) {
              coos.success("修改成功！");
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
