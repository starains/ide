<template>
  <div class="app-min-page">
    <el-collapse v-if="source.space != null" class="bd" v-model="activeName" accordion>
      <el-collapse-item title name="1">
        <template slot="title">
          <span class="color-grey pdlr-10">
            基本设置
            <i class="coos-icon coos-icon-setting"></i>
          </span>
        </template>
        <div class="pd-10">
          <el-form :model="form" :rules="form_rules" ref="form" class>
            <el-form-item label="开放类型" prop="publictype">
              <el-radio-group v-model="source.space.publictype">
                <el-radio
                  v-for="one in source.ENUM_MAP.PUBLIC_TYPE"
                  :key="one.value"
                  :label="one.value"
                >{{one.text}}</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="描述" prop="comment">
              <el-input v-model="source.space.comment" type="textarea" autocomplete="off" :rows="5"></el-input>
            </el-form-item>
          </el-form>
          <div
            v-if="source.hasPermission('SPACE_UPDATE')"
            class="coos-btn bg-green"
            @click="update"
          >保存</div>
        </div>
      </el-collapse-item>
      <el-collapse-item name="2">
        <template slot="title">
          <span class="color-orange pdlr-10">重命名</span>
        </template>
        <div class="pd-10">
          <el-form :model="form" :rules="form_rules" ref="form" class>
            <el-form-item label="名称" prop="name">
              <el-input v-model="source.space.name" autocomplete="off" :rows="5"></el-input>
            </el-form-item>
          </el-form>
          <div
            v-if="source.hasPermission('SPACE_RENAME')"
            class="coos-btn bg-green"
            @click="rename"
          >保存</div>
        </div>
      </el-collapse-item>
      <el-collapse-item name="3">
        <template slot="title">
          <span class="color-orange pdlr-10">
            转移
            <i class="coos-icon coos-icon-drag"></i>
          </span>
        </template>
        <div class="pd-10">转移</div>
      </el-collapse-item>
      <el-collapse-item name="4">
        <template slot="title">
          <span class="color-red pdlr-10">
            删除
            <i class="coos-icon coos-icon-delete"></i>
          </span>
        </template>
        <div class="pd-10">
          <div class="color-red pd-10 bd bd-red mgb-10">删除空间，将物理删除该空间所有数据！</div>
          <div class="color-red pd-10 bd bd-red mgb-10">此操作无法恢复！请慎重操作！</div>
          <div v-if="source.hasPermission('SPACE_DELETE')" class="coos-btn bg-red" @click="del">删除</div>
        </div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
export default {
  name: "OptionIndex",
  data() {
    return {
      activeName: "1",
      form: {},
      form_rules: {}
    };
  },
  methods: {
    del() {
      let that = this;
      coos
        .confirm("此操作无法恢复！确定删除？")
        .then(() => {
          source.do("SPACE_DELETE", { id: source.spaceid }).then(res => {
            if (res.errcode == 0) {
              coos.success("删除成功！");
              app.toSpace(source.LOGIN_USER.space);
            } else {
              coos.error(res.errmsg);
            }
          });
        })
        .catch(() => {});
    },
    update() {
      let that = this;
      source
        .do("SPACE_UPDATE", {
          id: source.space.id,
          publictype: source.space.publictype,
          comment: source.space.comment
        })
        .then(res => {
          if (res.errcode == 0) {
            coos.success("修改成功！");
            location.reload();
          } else {
            coos.error(res.errmsg);
          }
        });
    },
    rename() {
      let that = this;
      source
        .do("SPACE_RENAME", {
          id: source.space.id,
          name: source.space.name
        })
        .then(res => {
          if (res.errcode == 0) {
            coos.success("修改成功！");

            let url = location.href;
            if (url.indexOf("#") > 0) {
              url = url.substring(0, url.indexOf("#"));
            }
            if (url.lastIndexOf("/") > 0) {
              url = url.substring(0, url.lastIndexOf("/"));
            }
            url = url + "/" + source.space.name;
            coos.toURL(url);
          } else {
            coos.error(res.errmsg);
          }
        });
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
