<template>
  <div class="coos-form app-min-page">
    <div class="title font-md pdb-20">
      <span>{{form.id?'编辑成员':'添加成员'}}</span>
    </div>
    <div class="form">
      <el-form :model="form" ref="form" :rules="rules" label-position="left" label-width="100px">
        <el-form-item label="成员来源" prop="type">
          <el-select v-model="form.type" placeholder="请选择" @change="typeChange($event)" clearable>
            <el-option
              v-for="one in source.ENUM_MAP.SPACE_TEAM_TYPE"
              :key="one.value"
              :label="one.text"
              :value="one.value"
              :disabled="one.value != 'USERS'"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="成员" prop="recordid">
          <el-select v-model="form.recordid" placeholder="请选择" style="width:400px" clearable>
            <el-option
              v-for="one in records"
              :key="one.id"
              :label="one.name"
              :value="one.id"
              :disabled="itemrecordids.indexOf(one.id)>=0"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="权限" prop="permission">
          <el-radio-group v-model="form.permission">
            <el-radio
              v-for="one in source.ENUM_MAP.SPACE_PERMISSION"
              :key="one.value"
              :label="one.value"
            >{{one.text}}</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="doSubmit('form')">提交</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "TeamForm",
  data() {
    return {
      source: source,
      teams: [],
      itemrecordids: [],
      form: { type: "USERS", permission: "MASTER" },
      records: [],
      rules: {
        type: [{ required: true, message: "请选择成员来源", trigger: "blur" }],
        recordid: [{ required: true, message: "请选择成员", trigger: "blur" }],
        permission: [{ required: true, message: "请选择权限", trigger: "blur" }]
      }
    };
  },
  methods: {
    goBack() {
      this.$router.go(-1);
    },
    typeChange() {
      this.loadRecords(this.form.type);
    },
    loadRecords(type) {
      if (type == "USERS") {
        let data = { pagesize: 1000, pageindex: 1 };
        source.load("USERS", data).then(res => {
          this.records = res.value || [];
        });
      } else {
        this.records = [];
      }
    },
    doSubmit(form) {
      this.$refs[form].validate(valid => {
        if (valid) {
          this.form.spaceid = this.source.spaceid;
          if (this.form.id) {
            source.do("SPACE_TEAM_UPDATE", this.form).then(res => {
              if (res.errcode == 0) {
                coos.success("修改成功！");
                this.$router.go(-1);
              } else {
                coos.error(res.errmsg);
              }
            });
          } else {
            source.do("SPACE_TEAM_INSERT", this.form).then(res => {
              if (res.errcode == 0) {
                coos.success("新增成功！");
                this.$router.go(-1);
              } else {
                coos.error(res.errmsg);
              }
            });
          }
        } else {
          coos.error("请正确填写表单！");
          return false;
        }
      });
    },
    loadSpaceTeams(pageindex) {
      let data = { pagesize: 1000, pageindex: pageindex };
      source.load("SPACE_TEAMS", data).then(res => {
        let teams = res.value || [];
        teams.forEach(one => {
          this.teams.push(one);
          this.itemrecordids.push(one.recordid);
        });
      });
    }
  },
  mounted() {
    this.loadSpaceTeams(1);
    const id = this.$route.query.id;
    if (id) {
      source.get("SPACE_TEAM", id).then(res => {
        if (res) {
          this.form = res;
        } else {
        }
        this.typeChange();
      });
    } else {
      this.typeChange();
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.form {
  overflow: hidden;
}
</style>
