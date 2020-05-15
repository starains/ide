<template>
  <div class="doc-page">
    <div class="doc-header">App Designer 说明</div>
    <div class="doc-body">
      <div
        class="pdtb-10"
      >App Designer是在线应用设计器，开发人员可以借助该设计器，在线设计Database、Table、Dao、Service、Control、Page等</div>

      <div class="pdtb-10">App Designer是Team IDE的一个插件，该插件默认安装</div>

      <div class="pdtb-10">
        首先
        <router-link :to="{path:'/doc/app/app'}">
          <a class="coos-link color-green pdlr-5">设置模型</a>
        </router-link>
      </div>
      <div class="pdtb-10">在模型目录中配置相应的模型文件和目录，App Designer插件将自动解析相应模型文件，模型文件说明如下：</div>
      <div class="pdtb-10 pdlr-10">
        <div class="pdtb-10">
          <router-link :to="{path:'/doc/app/java'}">
            <a class="coos-link color-green">java</a>
          </router-link>：新建文件名为java的文件，此模型配置后
          <span class="color-orange">右击项目</span>或者
          <span class="color-orange">右击模型目录</span>，可以生成Java源码
        </div>
        <div class="pdtb-10">
          <router-link :to="{path:'/doc/app/jdbc'}">
            <a class="coos-link color-green">jdbc</a>
          </router-link>：新建文件名为jdbc的文件，此模型配置后，点击
          <span class="color-orange">dao</span>和
          <span class="color-orange">service</span>模型测试将使用该驱动配置
        </div>
        <div class="pdtb-10">
          <router-link :to="{path:'/doc/app/table'}">
            <a class="coos-link color-green">table</a>
          </router-link>：新建文件夹名为table的文件夹，在此文件夹下新建文件，文件名作为为表名，此模型配置后，
          <span class="color-orange">dao</span>模型配置可以快速选择表，
          <span class="color-orange">右击模型目录</span>可以点击生成表，则可以生成表到相应数据库中。
          <span class="color-orange">右击table目录</span>可以导入表，将数据库中表信息读取导入到table模型目录中。
        </div>
        <div class="pdtb-10">
          <router-link :to="{path:'/doc/app/dao'}">
            <a class="coos-link color-green">dao</a>
          </router-link>：新建文件夹名为dao的文件夹，在此文件夹下新建文件，可以配置dao模型，配置数据库的增删改查等业务
        </div>
        <div class="pdtb-10">
          <router-link :to="{path:'/doc/app/service'}">
            <a class="coos-link color-green">service</a>
          </router-link>：新建文件夹名为service的文件夹，在此文件夹下新建文件，可以配置service模型，可以配置service调用dao，或者一些逻辑运算等
        </div>
        <div class="pdtb-10">
          <router-link :to="{path:'/doc/app/control'}">
            <a class="coos-link color-green">control</a>
          </router-link>：新建文件夹名为control的文件夹，在此文件夹下新建文件，可以配置control模型，配置接口地址，调用相应的dao或service等
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
