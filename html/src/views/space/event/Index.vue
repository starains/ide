<template>
  <div class="app-min-page space-event-page">
    <ul class="coos-list pdtb-20">
      <template v-if="list == null">
        <div class="text-center pd-50 font-lg color-orange">数据加载中...</div>
      </template>
      <template v-else>
        <li v-if="list.length == 0">
          <div class="text-center pd-50 font-lg color-orange">暂无数据！</div>
        </li>
        <div class="block">
          <el-timeline>
            <el-timeline-item
              v-for="( date , index ) in list"
              :key="index"
              :timestamp="date.date"
              placement="top"
            >
              <el-card
                v-for="( one , index_ ) in date.list"
                :key="index + '-' + index_"
                class="mgb-10"
              >
                <div class="pdb-5">
                  <span class="color-orange">{{one.name}}</span>
                </div>
                <p class="font-xs">
                  <template
                    v-if="one.type == 'SPACE_CREATE' || one.type == 'SPACE_UPDATE' || one.type == 'SPACE_DELETE'"
                  >
                    仓库
                    <span class="color-green">{{one.data.name}}</span>
                  </template>

                  <template v-if="one.type == 'BRANCH_CREATE'">
                    新建版本
                    <span class="color-green">{{one.data.branch}}</span>
                    ，复制版本
                    <span class="color-green">{{one.data.frombranch}}</span>
                  </template>

                  <template
                    v-if="one.type == 'FILE_CREATE' || one.type == 'FILE_DELETE' || one.type == 'FILE_SAVE' || one.type == 'FILE_RENAME'"
                  >
                    文件路径
                    <span class="color-green">{{one.data.path}}</span>
                  </template>

                  <template
                    v-if="one.type == 'MAVEN_CLEAN' || one.type == 'MAVEN_DEPLOY' || one.type == 'MAVEN_INSTALL' || one.type == 'MAVEN_PACKAGE' || one.type == 'MAVEN_COMPILE'"
                  >
                    项目路径
                    <span class="color-green">{{one.data.path}}</span>
                  </template>

                  <template
                    v-if="one.type == 'GIT_BRANCH_CREATE' || one.type == 'GIT_BRANCH_DELETE'"
                  >
                    Git版本
                    <span class="color-green">{{one.data.branch}}</span>
                  </template>

                  <template v-if="one.type == 'GIT_BRANCH_RENAME'">
                    老版本
                    <span class="color-green">{{one.data.oldbranch}}</span>
                    ，新版本
                    <span class="color-green">{{one.data.newbranch}}</span>
                  </template>

                  <template v-if="one.type == 'GIT_CHECKOUT'">
                    检出版本
                    <span class="color-green">{{one.data.branch}}</span>
                  </template>

                  <template v-if="one.type == 'GIT_CLONE'">
                    地址
                    <span class="color-green">{{one.data.url}}</span>
                    ，远程名称
                    <span class="color-green">{{one.data.remote}}</span>
                    ，远程版本
                    <span class="color-green">{{one.data.branch}}</span>
                  </template>

                  <template v-if="one.type == 'GIT_PULL'">
                    远程名称
                    <span class="color-green">{{one.data.remote}}</span>
                    ，远程版本
                    <span class="color-green">{{one.data.remotebranch}}</span>
                  </template>

                  <template v-if="one.type == 'GIT_PUSH'">
                    远程名称
                    <span class="color-green">{{one.data.remote}}</span>
                    ，远程版本
                    <span class="color-green">{{one.data.remotebranch}}</span>
                    ，本地版本
                    <span class="color-green">{{one.data.branchname}}</span>
                    ，备注
                    <span class="color-green">{{one.data.message}}</span>
                  </template>

                  <template v-if="one.type == 'GIT_REVERT'">
                    路径
                    <span class="color-green">{{one.data.paths}}</span>
                  </template>

                  <template v-if="one.type == 'GIT_REMOTE_ADD' || one.type == 'GIT_REMOTE_REMOVE'">
                    远程名称
                    <span class="color-green">{{one.data.remote}}</span>
                  </template>

                  <template v-if="one.type == 'GIT_REMOTE_SETURL'">
                    远程名称
                    <span class="color-green">{{one.data.remote}}</span>
                    ，地址
                    <span class="color-green">{{one.data.url}}</span>
                  </template>

                  <template v-if="one.type == 'SET_RUN_OPTION' || one.type == 'SET_RUNNER_OPTION'">
                    路径
                    <span class="color-green">{{one.data.path}}</span>
                    ，配置
                    <span class="color-green">{{one.data.option}}</span>
                  </template>

                  <template
                    v-if="one.type == 'DELETE_RUN_OPTION' || one.type == 'DELETE_RUNNER_OPTION'"
                  >
                    路径
                    <span class="color-green">{{one.data.path}}</span>
                  </template>

                  <template
                    v-if="one.type == 'SPACE_TEAM_INSERT' || one.type == 'SPACE_TEAM_UPDATE' || one.type == 'SPACE_TEAM_DELETE'"
                  >
                    成员名称
                    <span class="color-green">{{one.data.id}}</span>
                    ，成员类型
                    <span class="color-green">{{one.data.type}}</span>
                  </template>

                  <template
                    v-if="one.type == 'RUNNER_SERVER_SAVE' || one.type == 'RUNNER_SERVER_DELETE'"
                  >
                    部署远程服务器名称
                    <span class="color-green">{{one.data.name}}</span>
                    ，部署远程服务器信息
                    <span class="color-green">{{one.data}}</span>
                  </template>

                  <template
                    v-if="one.type == 'RUNNER_CLIENT_SAVE' || one.type == 'RUNNER_CLIENT_DELETE'"
                  >
                    部署客户端名称
                    <span class="color-green">{{one.data.name}}</span>
                    ，部署客户端信息
                    <span class="color-green">{{one.data}}</span>
                  </template>
                </p>
                <p class="mgt-5">
                  <template v-if="one.user != null ">
                    <span class="color-green pdr-5">{{one.user.name}}</span>
                  </template>
                  执行与
                  <span class="coos-grey pdlr-5">{{one.datetime}}</span>
                </p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>
      </template>
    </ul>
  </div>
</template>

<script>
export default {
  name: "JoinIndex",
  data() {
    return {
      source: source,
      list: null
    };
  },
  methods: {
    formatDateStr(time) {
      if (time == null || time == "") {
        return time;
      }
      return coos.date.formatByStr(time, "yyyy/MM/dd hh:mm");
    },
    load() {
      if (this.loading) {
        return;
      }
      let data = {};
      this.pageindex = this.pageindex || 0;
      this.pageindex++;

      data.pageindex = this.pageindex;
      data.pagesize = 20;
      if (this.totalpages) {
        if (this.totalpages < data.pageindex) {
          return;
        }
      }
      this.loading = true;

      source.load("SPACE_EVENTS", data).then(result => {
        this.list = this.list || [];
        this.totalpages = result.totalpages || 0;
        let list = result.value || [];
        list.forEach(one => {
          let dateOne = null;
          this.list.forEach(dateOne_ => {
            if (dateOne_.date == one.date) {
              dateOne = dateOne_;
            }
          });
          if (dateOne == null) {
            dateOne = { date: one.date, list: [] };
            this.list.push(dateOne);
          }
          dateOne.list.push(one);
        });
        this.loading = false;
      });
    }
  },
  mounted() {
    this.load();
    let that = this;
    $(document).on("scroll", function() {
      if (that.$route.path != "/event") {
        return;
      }
      var scroH = $(document).scrollTop(); //滚动高度
      var viewH = $(window).height(); //可见高度
      var contentH = $(document).height(); //内容高度

      if (scroH > 100) {
        //距离顶部大于100px时
      }
      if (contentH - (scroH + viewH) <= 100) {
        //距离底部高度小于100px
        that.load();
      }
      if ((contentH = scroH + viewH)) {
        //滚动条滑到底部啦
      }
    });
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style >
.space-event-page .el-timeline-item__content {
  background: #efefef;
  padding: 10px;
  border-radius: 5px;
}
.space-event-page .el-card__body{
  padding: 10px 10px 0px 10px;
}
</style>
