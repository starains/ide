<template>
  <div class>
    <el-dialog
      :title="title"
      :visible.sync="show_form"
      :close-on-click-modal="false"
      width="1000px"
    >
      <el-form
        :model="form"
        :rules="form_rules"
        ref="form"
        :label-width="labelWidth"
        :size="size"
        append-to-body
        zIndex="100"
      >
        <div v-if="loading">
          <div class="color-orange ft-20 text-center pdtb-20">表信息加载中...</div>
        </div>
        <el-collapse class="table-collapse" v-if="!loading" accordion>
          <el-collapse-item v-for="table in tables" :key="table.name">
            <div slot="title" class="ft-13" style="width:100%">
              {{table.name}}
              <span
                v-if="table.comment!=null && table.comment!=''"
                class="pdl-30"
              >[{{table.comment}}]</span>
              <a
                class="coos-btn coos-link coos-btn-xs color-green float-right mgt-7 mgr-10"
                @click="doImport(table)"
                v-if="!table.find"
              >导入</a>
              <span v-if="table.find" class="color-green float-right mgr-10">已存在</span>
              <span v-if="!table.find" class="color-orange float-right mgr-10">不存在</span>
            </div>
            <div class="bd-1 pd-10 bd-grey-2">
              <div class="ft-14">字段信息</div>
              <div class="ft-12" v-for="column in table.columns" :key="column.name">
                <span class="pdr-10">{{column.name}}</span>
                <span class="pdr-10">{{column.comment}}</span>
                <span class="pdr-10 color-green" v-if="column.primarykey">主键</span>
                <span class="pdr-10 color-orange" v-if="!column.primarykey">非主键</span>
                <span class="pdr-10 color-green" v-if="column.nullable">可空</span>
                <span class="pdr-10 color-orange" v-if="!column.nullable">不可空</span>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
export default {
  components: {},
  data() {
    return {
      title: "导入表",
      labelWidth: "120px",
      size: "mini",
      source: source,
      show_form: false,
      databasename: "",
      loading: false,
      tables: [],
      app: null,
      form: {
        name: ""
      },
      form_rules: {}
    };
  },
  methods: {
    show(app, data) {
      return this.showForm(app, data);
    },
    showForm(app, data) {
      data = data || {};
      this.app = app;
      this.databasename = data.databasename;
      this.parent = data.parent;
      this.loading = true;
      this.show_form = true;
      this.loadTables();
      return new Promise((resolve, reject) => {
        this.resolve = resolve;
        this.reject = reject;
      });
    },
    hideForm() {
      this.show_form = false;
    },
    formSave() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          var option = this.form;
          let data = { path: this.path, option: option };
          this.hideForm();

          this.resolve && this.resolve(data);
        } else {
          return false;
        }
      });
    },
    formCancel() {
      this.hideForm();
    },
    doImport(table) {
      table.find = true;
      source.server
        .event("app.plugin", "1.0", "toText", {
          type: "table",
          model: table,
          filename: table.name
        })
        .then(result => {
          let file = {
            parentPath: this.parent.path,
            path: this.parent + "/" + table.name,
            name: table.name,
            toRename: false,
            file: true
          };
          source.formatFile(file, this.parent);
          this.parent.files.push(file);

          let date = {
            parentPath: file.parentPath,
            name: file.name,
            isFile: file.isFile,
            isDirectory: file.isDirectory,
            content: result
          };

          source.do("FILE_CREATE", date).then(res => {
            if (res.errcode == 0) {
              if (file.parent) {
                source.sortFolderFiles(file.parent);
              }
            } else {
              coos.error(res.errmsg);
            }
          });
        });

      window.event && window.event.stopPropagation();
    },
    loadTables() {
      let data = {};
      data.type = "LOAD_DATABASE_TABLES";
      data.path = this.app.localpath;
      data.name = this.databasename;
      this.loading = true;
      coos.trimArray(this.tables);
      source.server.event("app.plugin", "1.0", "doTest", data).then(result => {
        var value = result.value || [];
        value.forEach(table => {
          table.find = false;
          if (this.app.context.TABLE) {
            this.app.context.TABLE.forEach(one => {
              if (one.name == table.name) {
                table.find = true;
              }
            });
          }
          this.tables.push(table);
        });
        this.loading = false;
      });
    }
  },
  mounted() {},
  beforeCreate() {}
};
</script>

<style  >
.table-collapse .el-collapse-item__header {
  height: 30px;
  line-height: 30px;
}
</style>
