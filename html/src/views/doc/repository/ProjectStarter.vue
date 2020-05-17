<template>
  <div class="doc-page">
    <div class="doc-header">项目运行</div>
    <div class="doc-body">
      <div class="pdtb-10">
        进入仓库目录，
        <span class="color-orange">右击项目</span> 》 菜单项 》 项目部署运行 》 添加配置
      </div>
      <div class="pdtb-10">
        第一次启动项目
        <span class="color-orange">右击项目</span> 》 菜单项 》 项目部署运行 》 找到配置的启动 》 部署
      </div>
      <div class="pdtb-10">
        重启部署项
        <span class="color-orange">打开控制台</span> 》 点击配置的启动项 》 可以
        <span class="color-orange pdlr-5">停止</span>
        <span class="color-orange pdlr-5">启动</span>
        <span class="color-orange pdlr-5">查看日志</span>
        等
      </div>
      <div class="pdtb-10">
        <span class="color-red">Team IDE暂时可以直接配置Java项目启动</span>，其它项目可以自己命令进行启动
      </div>
      <div class="pdtb-10">
        <span class="color-red">
          如果在teamide.com服务器启动项目，因服务器未开放端口，所有如需访问，请打开
          <router-link :to="{path:'/doc/nginx'}">
            <a class="coos-link color-green pdlr-5">Nginx申请</a>
          </router-link>，Team IDE管理员将后台进行审批，通过后即可通过外网访问
        </span>
      </div>

      <div class="pdtb-10">
        <div class="pd-30 coos-row">
          <a :href="_SERVER_URL + 'resources/doc/starter-1.png'" target="_blank">
            <img :src="_SERVER_URL + 'resources/doc/starter-1.png'" class="col-12 bd-1 bd-grey" />
          </a>
        </div>
        <div class="pd-30 coos-row mgt-10">
          <a :href="_SERVER_URL + 'resources/doc/starter-2.png'" target="_blank">
            <img :src="_SERVER_URL + 'resources/doc/starter-2.png'" class="col-12 bd-1 bd-grey" />
          </a>
        </div>
        <div class="pd-30 coos-row mgt-10">
          <a :href="_SERVER_URL + 'resources/doc/starter-3.png'" target="_blank">
            <img :src="_SERVER_URL + 'resources/doc/starter-3.png'" class="col-12 bd-1 bd-grey" />
          </a>
        </div>
        <div class="pd-30 coos-row mgt-10">
          <a :href="_SERVER_URL + 'resources/doc/starter-4.png'" target="_blank">
            <img :src="_SERVER_URL + 'resources/doc/starter-4.png'" class="col-12 bd-1 bd-grey" />
          </a>
        </div>
        <div class="pd-30 coos-row mgt-10">
          <a :href="_SERVER_URL + 'resources/doc/starter-5.png'" target="_blank">
            <img :src="_SERVER_URL + 'resources/doc/starter-5.png'" class="col-12 bd-1 bd-grey" />
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Index",
  components: {},
  data() {
    return { source: source };
  }
};
</script>
