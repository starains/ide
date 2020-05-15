<template>
  <div class="doc-page">
    <div class="doc-header">Java</div>
    <div class="doc-body">
      <div class="pdtb-10">
        首先
        <router-link :to="{path:'/doc/app/app'}">
          <a class="coos-link color-green pdlr-5">设置模型</a>
        </router-link>
      </div>
      <div class="pdtb-10">打开已配置的模型目录，右击创建文件，文件名定义为java，打开java文件设置</div>
      <div class="pdtb-10">
        Java项目，可以参照
        <a
          class="coos-link color-green pdlr-5"
          target="blank_"
          href="https://gitee.com/teamide/demo.java"
        >demo.java Git地址</a>，
        <a
          class="coos-link color-green pdlr-5"
          target="blank_"
          href="https://ide.teamide.com/demo/java"
        >demo.java Team IDE地址</a>，此工程使用App Designer设计一套简单的增删改查业务服务
      </div>
      <div class="pdtb-10">生成的源码适配Spring Boot项目，需要在源码中实现以下组件</div>
      <div class="pdtb-10">
        <el-tabs v-model="activeName">
          <el-tab-pane label="pom.xml" name="pom">
            <span class="color-orange pdtb-10 ft-15">pom.xml，Maven项目基础配置文件，配置Spring Boot依赖和基础jar的依赖</span>
            <pre class="doc-code">{{pom}}</pre>
          </el-tab-pane>
          <el-tab-pane label="ApplicationMain.java" name="ApplicationMain">
            <span class="color-orange pdtb-10 ft-15">ApplicationMain.java，为项目文件启动入口</span>
            <pre class="doc-code">{{ApplicationMain}}</pre>
          </el-tab-pane>
          <el-tab-pane label="DaoComponent.java" name="DaoComponent">
            <span class="color-orange pdtb-10 ft-15">DaoComponent.java，实现IDao接口，处理事务等</span>
            <pre class="doc-code">{{DaoComponent}}</pre>
          </el-tab-pane>
          <el-tab-pane label="FactoryComponent.java" name="FactoryComponent">
            <span
              class="color-orange pdtb-10 ft-15"
            >FactoryComponent.java，可以根据调用Dao过程中传入JSON，实现动态数据源等</span>
            <pre class="doc-code">{{FactoryComponent}}</pre>
          </el-tab-pane>
          <el-tab-pane label="TransactionComponent.java" name="TransactionComponent">
            <span class="color-orange pdtb-10 ft-15">TransactionComponent.java，事务组件</span>
            <pre class="doc-code">{{TransactionComponent}}</pre>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script>
import pom from "./code/pom.txt";
import ApplicationMain from "./code/ApplicationMain.txt";
import DaoComponent from "./code/DaoComponent.txt";
import FactoryComponent from "./code/FactoryComponent.txt";
import TransactionComponent from "./code/TransactionComponent.txt";

export default {
  name: "Index",
  components: {},
  data() {
    return {
      activeName: "pom",
      source: source,
      pom,
      ApplicationMain,
      DaoComponent,
      FactoryComponent,
      TransactionComponent
    };
  }
};
</script>

<style >
.doc-code {
  background-color: #cacaca;
  padding: 10px;
  color: #232323;
}
</style>
