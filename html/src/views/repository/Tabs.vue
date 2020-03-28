<template>
  <div class="repository-tab-box">
    <div class="repository-file-tabs">
      <el-tabs
        v-model="repository.activeTab"
        type="card"
        closable
        @tab-remove="removeTab"
        @tab-click="clickTab"
      >
        <el-tab-pane v-for="(item) in repository.tabs" :key="item.name" :name="item.name">
          <span slot="label" :title="item.title" :path="item.path">
            {{item.text}}
            <i
              v-show="item.changed"
              class="tab-need-save-icon color-red"
              style="font-size: 16px;vertical-align: -5px;"
            >*</i>
          </span>
        </el-tab-pane>
      </el-tabs>
    </div>
    <div class="repository-tab-editor-box"></div>
  </div>
</template>

<script>
export default {
  components: {},
  props: ["repository"],
  data() {
    return { source: source };
  },
  beforeMount() {},
  watch: {
    "repository.activeTab": function(activeTab) {
      source.changeTab(activeTab);
      let tab = source.getTab(activeTab);
      if (tab != null) {
        let project = source.getProjectByPath(tab.path);
        source.do("FILE_OPEN", { path: tab.path }, project);
      }
    }
  },
  methods: {
    removeTab(name) {
      source.closeTab(name);
    },
    clickTab() {},
    showContextmenu(e, path) {
      let event = e || window.event;
      this.repository.contextmenu.menus = [
        { header: "基本" },
        {
          text: "关闭",
          onClick() {
            source.closeTab(path);
          }
        },
        {
          text: "关闭其它",
          onClick() {
            source.closeOtherTab(path);
          }
        },
        {
          text: "关闭左侧",
          onClick() {
            source.closeLeftTab(path);
          }
        },
        {
          text: "关闭右侧",
          onClick() {
            source.closeRightTab(path);
          }
        },
        {
          text: "全部关闭",
          onClick() {
            source.closeAllTab(path);
          }
        },
        { divider: true },
        { text: "复制名称" },
        { text: "复制路径" }
      ];
      this.repository.contextmenu.show = true;
      this.repository.contextmenu.left = event.pageX + "px";
      this.repository.contextmenu.top = event.pageY + "px";
      event.preventDefault();
    }
  },
  mounted() {
    let that = this;
    $(document).on(
      "contextmenu",
      ".repository-file-tabs .el-tabs__item",
      function(e) {
        let $item = $(e.target).closest(".el-tabs__item");
        let $span = $item.find("span:first");
        that.showContextmenu(e, $span.attr("path"));
      }
    );

    $(document).on("mouseup", ".repository-file-tabs .el-tabs__item", function(
      e
    ) {
      if (e.button == 1) {
        let $item = $(e.target).closest(".el-tabs__item");
        let $span = $item.find("span:first");
        source.closeTab($span.attr("path"));
      }
    });
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style  >
.repository-tab-box {
  margin-right: 30px;
}
.repository-tab-box .el-tabs__header {
  margin: 0px;
}
.repository-tab-box .el-tabs__nav-next,
.repository-tab-box .el-tabs__nav-prev {
  height: 30px;
  line-height: 30px;
}
.repository-tab-box .el-tabs__nav {
  height: 30px;
}
.repository-tab-box .el-tabs__nav-scroll {
  margin-left: -1px;
}
.repository-tab-box .el-tabs__nav:first-child {
  border-left: 0px;
}
.repository-tab-box .el-tabs--card > .el-tabs__header .el-tabs__item {
  font-size: 12px;
  padding-left: 10px;
  padding-right: 10px;
  height: 30px;
  line-height: 30px;
}
.repository-tab-box .el-tabs__item.is-active,
.repository-tab-box .el-tabs__item:hover {
  color: #008992;
}
.repository-tab-editor-box {
  position: absolute;
  bottom: 0px;
  top: 30px;
  left: 0px;
  right: 0px;
}
.repository-tab-editor {
  position: relative;
  width: 100%;
  height: 100%;
  display: none;
  background: #f3f3f3;
}
.repository-tab-editor.show {
  display: block;
}
</style>
