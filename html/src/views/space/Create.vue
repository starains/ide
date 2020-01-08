<template>
  <div class="coos-form app-min-page">
    <div class="title font-md pdb-20">
      <span>{{form.id?'编辑仓库':'新建仓库'}}</span>
    </div>
    <div class="form">
      <el-form :model="form" ref="form" :rules="rules" label-position="left" label-width="100px">
        <el-form-item label="名称" prop="name">
          <el-input
            v-model="form.name"
            placeholder="1-20个字符，可包含字母、数字、下划线（_）、中划线（-）、英文点（.），不能以符号开头或结尾！"
          ></el-input>
        </el-form-item>

        <el-form-item v-show="parents.length > 0" label="归属" :prop="parents.length>0?'parentid':''">
          <el-select
            v-model="form.parentid"
            placeholder="请选择"
            @change="parentChange($event)"
            clearable
          >
            <el-option
              v-for="one in parents"
              :key="one.id"
              :value="one.id"
              :label="one.servletpath"
            >
              {{one.servletpath}}
              <template v-for=" type in source.ENUM_MAP.SPACE_TYPE">
                <template v-if="type.value==one.type">（{{type.text}}）</template>
              </template>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type" @change="typeChange($event)">
            <el-radio
              v-for="one in source.ENUM_MAP.SPACE_TYPE"
              :key="one.value"
              :label="one.value"
              v-show="one.value !='USERS' "
            >{{one.text}}</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="开放类型" prop="publictype">
          <el-radio-group v-model="form.publictype">
            <el-radio
              v-for="one in source.ENUM_MAP.PUBLIC_TYPE"
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
import tool from "@/common/js";

export default {
  name: "SpaceForm",
  data() {
    return {
      parents: [],
      source: source,
      form: { name: "", parentid: "", type: "REPOSITORYS", publictype: "OPEN" },
      records: [],
      rules: {
        name: [
          { required: true, message: "请输入仓库名称", trigger: "blur" },
          {
            pattern: /^(?!_)(?!.*?_$)(?!\.)(?!.*?\.$)(?!-)(?!.*?-$)[a-zA-Z0-9_\.\-]{1,20}$/,
            message:
              "1-20个字符，可包含字母、数字、下划线（_）、中划线（-）、英文点（.），不能以符号开头或结尾！"
          }
        ],
        type: [{ required: true, message: "请选择仓库类型", trigger: "blur" }],
        publictype: [
          { required: true, message: "请选择开放类型", trigger: "blur" }
        ],
        parentid: [
          { required: true, message: "请选择仓库归属", trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    goBack() {
      this.$router.go(-1);
    },
    parentChange() {},
    typeChange() {
      var type = this.form.type;
      this.parents = [];
      if (type == "REPOSITORYS") {
        this.parents.push({
          id: null,
          servletpath: "请选择",
          type: ""
        });
        this.master_spaces.forEach(space => {
          if (space.type != "REPOSITORYS") {
            this.parents.push(space);
          }
        });
      } else if (type == "ORGANIZATIONS") {
        this.parents.push({
          id: null,
          servletpath: "/organizations",
          type: "ORGANIZATIONS"
        });
        this.master_spaces.forEach(space => {
          if (space.type == "ORGANIZATIONS" || space.type == "ENTERPRISES") {
            this.parents.push(space);
          }
        });
      } else if (type == "PRODUCTS") {
        this.parents.push({
          id: null,
          servletpath: "/products",
          type: "PRODUCTS"
        });
        this.master_spaces.forEach(space => {
          if (
            space.type == "ORGANIZATIONS" ||
            space.type == "ENTERPRISES" ||
            space.type == "PRODUCTS"
          ) {
            this.parents.push(space);
          }
        });
      } else if (type == "ENTERPRISES") {
        this.parents.push({
          id: null,
          servletpath: "/enterprises",
          type: "ENTERPRISES"
        });
      }

      let find = false;
      this.parents.forEach(space => {
        if (space.id == this.form.parentid) {
          find = true;
        }
      });
      if (!find) {
        this.form.parentid = null;
      }
      if (this.source.space) {
        this.parents.forEach(space => {
          if (space.id == this.source.space.id) {
            this.form.parentid = space.id;
          }
        });
      }
    },
    doSubmit(form) {
      this.$refs[form].validate(valid => {
        if (valid) {
          if (this.form.id) {
            source.do("SPACE_UPDATE", this.form).then(result => {
              //tool.success("修改成功！");
              this.$router.go(-1);
            });
          } else {
            source.do("SPACE_CREATE", this.form).then(result => {
              //tool.success("新增成功！");
              this.$router.go(-1);
            });
          }
        } else {
          tool.error("请正确填写表单！");
          return false;
        }
      });
    }
  },
  mounted() {
    this.master_spaces = [];
    this.typeChange();
    const id = this.$route.query.id;

    let data = { pagesize: 1000 };
    source.load("MASTER_SPACES", data).then(result => {
      console.log(result);
      this.master_spaces = result.value || [];
      this.typeChange();
    });
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
