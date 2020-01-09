<template>
  <div class="app-min-page">
    <ul class="coos-list pdtb-20">
      <template v-if="source.data.SPACE_TEAMS == null">
        <div class="text-center pd-50 font-lg color-orange">成员数据加载中...</div>
      </template>
      <template v-else>
        <li v-if="source.data.SPACE_TEAMS.list.length == 0">
          <div class="text-center pd-50 font-lg color-orange">
            暂无成员数据
            <template v-if="app.hasPermission('MANAGER_TEAM')">
              ，可以点击
              <router-link to="/team/form">
                <a class="coos-link pdlr-10 color-green">添加成员</a>
              </router-link>！
            </template>
          </div>
        </li>
        <li v-else>
          <div class="coos-item">
            <div class="coos-content"></div>
            <div class="coos-flex-right">
              <template v-if="app.hasPermission('MANAGER_TEAM')">
                <router-link to="/team/form">
                  <a class="coos-link color-green mgl-5" title="添加人员">
                    <i class="coos-icon coos-icon-addteam"></i> 添加成员
                  </a>
                </router-link>
              </template>
            </div>
          </div>
        </li>
        <li
          v-for="( one , index ) in source.data.SPACE_TEAMS.list"
          :key="index"
          class
          style="border-bottom: 1px solid #ddd;"
        >
          <div class="coos-item">
            <div class="coos-index">
              <i v-if="one.type == 'USERS'" title="用户" class="coos-icon coos-icon-user font-md"></i>
            </div>
            <div class="coos-content">
              <div class>
                <span v-if="one.data" class="color-green font-md">{{one.data.name}}</span>
              </div>
            </div>
            <div class="coos-flex-right">
              <a v-if="one.permission == 'MASTER'" class="color-orange mgl-5" title="管理员权限">
                <i class="coos-icon coos-icon-manager"></i>
              </a>
              <a v-if="one.permission == 'DEVELOPER'" class="color-orange mgl-5" title="开发权限">
                <i class="coos-icon coos-icon-code"></i>
              </a>
              <a v-if="one.permission == 'VIEWER'" class="color-orange mgl-5" title="只读权限">
                <i class="coos-icon coos-icon-eye"></i>
              </a>

              <a v-if="one.publictype == 'PRIVATE'" class="color-orange mgl-5" title="私人">
                <i class="coos-icon coos-icon-lock"></i>
              </a>
              <router-link :to="'/team/form?id=' + one.id">
                <a
                  v-if="app.hasPermission('MANAGER_TEAM')"
                  class="coos-link color-green mgl-5"
                  title="修改"
                >
                  <i class="coos-icon coos-icon-edit"></i>
                </a>
              </router-link>
              <a
                v-if="app.hasPermission('MANAGER_TEAM')"
                class="coos-link color-red mgl-5"
                title="删除"
                @click="doDelete(one)"
              >
                <i class="coos-icon coos-icon-delete"></i>
              </a>
            </div>
          </div>
        </li>
      </template>
    </ul>
  </div>
</template>

<script>
export default {
  name: "TeamIndex",
  data() {
    return { source: source };
  },
  methods: {
    doDelete(team) {
      let that = this;
      coos
        .confirm("确定删除该成员？")
        .then(() => {
          source.do("SPACE_TEAM_DELETE", { id: team.id }).then(res => {
            if (res.errcode == 0) {
              coos.success("删除成功！");
              that.loadSpaceTeams(1);
            } else {
              coos.error(res.errmsg);
            }
          });
        })
        .catch(() => {});
    },
    loadSpaceTeams(pageindex) {
      let data = { pagesize: 100, pageindex: pageindex };
      source.load("SPACE_TEAMS", data);
    }
  },
  mounted() {
    this.loadSpaceTeams(1);
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
