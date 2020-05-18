<template>
  <div class="doc-page">
    <div class="doc-header">Team IDE 说明</div>
    <div class="doc-body">
      <div
        class="pdtb-10"
      >Team IDE 是团队在线项目开发平台，项目成员无需在自己电脑安装开发环境，直接在Team IDE上进行源码编辑，使用Git管理源码版本，在线运行测试，部署项目等</div>

      <div class="pdtb-10">Team IDE中，以空间和仓库来映射路由</div>
      <div class="pdtb-10">
        <span class="color-red">空间：注册的用户，创建的产品仅仅作为空间目录，在空间目录下可以创建库</span>
      </div>
      <div class="pdtb-10">
        <span class="color-red">库：用户可以在自己的用户空间下或产品空间下新建仓库，仓库可以设置Git地址管理源码，也可以直接上传源码</span>
      </div>
      <div class="pdtb-10">
        <span
          class="color-red"
        >库项目：每个库的根路径会作为默认项目展示，也可以右击文件夹设为项目，当设为项目，则在仓库编辑页会将项目作为树的根，Team IDE也会读取项目路径下pom.xml文件识别Java工程等</span>
      </div>

      <div class="pdtb-10">App Designer说明</div>
      <div class="pdtb-10">
        <span
          class="color-red"
        >App Designer是在线用于设计器，借助该设计器可以在线编辑Database、Table、Dao、Service、Controller等，并且可以生成Java源码，其它语言正在扩展中</span>
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
