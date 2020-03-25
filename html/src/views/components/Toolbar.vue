<template>
  <div class="toolbar">
    <el-breadcrumb separator-class="el-icon-arrow-right" style="padding: 10px 0px;">
      <el-breadcrumb-item :to="{path:''}">
        <span @click="toIndex">首页</span>
      </el-breadcrumb-item>
      <template v-if="source.data.PARENTS != null">
        <template v-for="one in source.data.PARENTS">
          <el-breadcrumb-item :to="{path:''}" :key="one.id">
            <template v-if="one.levels ==null ||  one.levels.length==0">
              <span @click="toSpace(one)">{{one.name}}</span>
            </template>
            <template v-else>
              <el-dropdown size="mini" @command="toSpace">
                <span class="el-dropdown-link" @click="toSpace(one)">{{one.name}}</span>
                <el-dropdown-menu slot="dropdown" class="toolbar-menu">
                  <template v-for="level in one.levels">
                    <el-dropdown-item :key="level.id+'-level'" :command="level">
                      <span class>{{level.name}}</span>
                    </el-dropdown-item>
                  </template>
                </el-dropdown-menu>
              </el-dropdown>
            </template>
          </el-breadcrumb-item>
        </template>
      </template>
      <template v-if="source.branch != null">
        <el-breadcrumb-item :to="{path:''}">
          <el-dropdown size="mini" @command="toBranch">
            <span class="el-dropdown-link coos-pointer">{{source.branch}}</span>
            <el-dropdown-menu slot="dropdown" class="toolbar-menu">
              <template v-if="source.data.BRANCHS != null">
                <el-dropdown-item
                  v-for="branch in source.data.BRANCHS"
                  :key="branch.id"
                  :command="branch.name"
                >
                  <span class="coos-pointer">{{branch.name}}</span>
                </el-dropdown-item>
              </template>
              <el-dropdown-item
                v-if="source.space.permission == 'MASTER'"
                command="TO_CREATE_BRANCH_EVENT"
              >
                <span>新建版本</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-breadcrumb-item>
      </template>
    </el-breadcrumb>
    <el-tabs v-model="activeName" @tab-click="onTabClick">
      <template v-for="tab in tabs">
        <el-tab-pane :key="tab.name" :name="tab.name">
          <span slot="label">
            <i class="coos-icon" :class="tab.icon"></i>
            {{tab.text}}
          </span>
        </el-tab-pane>
      </template>
    </el-tabs>
  </div>
</template>

<script>
export default {
  name: "Toolbar",
  data() {
    var tabs = [];
    if (source.space == null && source.SPACE_TYPE != null) {
      tabs.push({
        icon: "coos-icon-database",
        name: "/",
        text: "仓库"
      });
    }
    if (source.space) {
      if (source.space.type == "REPOSITORYS") {
        tabs.push({
          icon: "coos-icon-code",
          name: "/",
          text: "代码"
        });
      } else {
        tabs.push({
          icon: "coos-icon-database",
          name: "/",
          text: "仓库"
        });
      }
      tabs.push({
        icon: "coos-icon-linechart",
        name: "/event",
        text: "动态"
      });
      if (source.space.type != "USERS") {
        tabs.push({
          icon: "coos-icon-team",
          name: "/team",
          text: "成员"
        });
        tabs.push({
          icon: "coos-icon-setting",
          name: "/option",
          text: "设置"
        });
      } else {
        tabs.push({
          icon: "coos-icon-deploymentunit",
          name: "/join",
          text: "参与"
        });
        tabs.push({
          icon: "coos-icon-star",
          name: "/star",
          text: "星标"
        });
      }
    }
    var activeName = this.$route.path;
    var tab = null;
    tabs.forEach(t => {
      if (t.name == activeName || activeName.indexOf(t.name) == 0) {
        tab = t;
      }
    });
    if (tabs.length > 0) {
      if (tab == null) {
        activeName = tabs[0].name;
      }
    }

    if (tab == null) {
      // this.$router.push(activeName);
    }
    return { source: source, activeName: activeName, tabs: tabs };
  },
  beforeMount() {},
  watch: {
    activeName: function(val) {
      this.$router.push(val);
    }
  },
  methods: {
    toIndex() {
      location.href = _ROOT_URL;
    },
    toSpace(space) {
      app.toSpace(space);
    },
    toBranch(branch) {
      if (branch == "TO_CREATE_BRANCH_EVENT") {
        this.toCreateBranch();
        return;
      }
      app.toBranch(branch);
    },
    onTabClick(tab, event) {},
    toCreateBranch() {
      source.branchCreateForm.show();
    }
  },
  computed: {}
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style >
.toolbar .el-tabs__nav-wrap::after {
  z-index: 0;
}
.toolbar .el-tabs__nav {
  z-index: 0;
}
.toolbar-menu .el-dropdown-menu__item:focus,
.toolbar-menu .el-dropdown-menu__item:not(.is-disabled):hover {
  background-color: #ffffff !important;
  color: #009688 !important;
}
.toolbar-menu .el-dropdown-menu__item {
  border-bottom: 1px solid #f1f1f1;
}

.toolbar .el-breadcrumb__inner a:hover,
.toolbar .el-breadcrumb__inner.is-link:hover {
  color: #009688;
}

.toolbar .el-tabs__item {
  height: 28px;
  line-height: 28px;
}
.toolbar .el-tabs__item:hover {
  color: #009688;
}

.toolbar .el-tabs__item.is-active {
  color: #009688;
}

.toolbar .el-tabs__active-bar {
  background-color: #009688;
}
</style>
