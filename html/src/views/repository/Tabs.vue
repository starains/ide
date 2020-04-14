<template>
  <div class="repository-tab-box">
    <div class="repository-file-tabs">
      <el-tabs
        v-model="repository.activeTab"
        type="card"
        closable
        @tab-remove="removeTab"
        @tab-click="clickTab"
        class="repository-tabs"
      >
        <el-tab-pane
          class="repository-tab-span"
          v-for="(item) in repository.tabs"
          :key="item.name"
          :name="item.name"
        >
          <span slot="label" :title="item.title" :path="item.path">
            {{item.text}}
            <i
              v-show="item.changed"
              class="tab-need-save-icon color-red"
              style="font-size: 16px;vertical-align: -5px;"
            >*</i>
          </span>
          <TabEditor :tab="item"></TabEditor>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import TabEditor from "@/views/repository/components/editor/TabEditor";
export default {
  components: { TabEditor },
  props: ["repository"],
  data() {
    return { source: source };
  },
  beforeMount() {},
  watch: {
    "repository.activeTab": function(activeTab) {
      this.$nextTick(res => {
        source.changeTab(activeTab);
        let tab = source.getTab(activeTab);
        if (tab != null) {
          if (source.isLogin) {
            let project = source.getProjectByPath(tab.path);
            source.do("FILE_OPEN", { path: tab.path }, project);
          }
        }
      });
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
      ".repository-tabs > .el-tabs__header .el-tabs__item",
      function(e) {
        let $item = $(e.target).closest(".el-tabs__item");
        let $span = $item.find("span:first");
        that.showContextmenu(e, $span.attr("path"));
      }
    );

    $(document).on(
      "mouseup",
      ".repository-tabs > .el-tabs__header .el-tabs__item",
      function(e) {
        if (e.button == 1) {
          let $item = $(e.target).closest(".el-tabs__item");
          let $span = $item.find("span:first");
          source.closeTab($span.attr("path"));
        }
      }
    );
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style  >
.repository-tab-box {
  margin-right: 25px;
}
.repository-tabs > .el-tabs__header {
  margin: 0px;
}
.repository-tabs > .el-tabs__header .el-tabs__nav-next,
.repository-tabs > .el-tabs__header .el-tabs__nav-prev {
  height: 25px;
  line-height: 25px;
}
.repository-tabs > .el-tabs__header .el-tabs__nav {
  height: 25px;
}
.repository-tabs > .el-tabs__header .el-tabs__nav-scroll {
  margin-left: -1px;
}
.repository-tabs > .el-tabs__header .el-tabs__nav:first-child {
  border-left: 0px;
}
.repository-tabs > .el-tabs__header .el-tabs__item {
  font-size: 12px;
  padding-left: 10px !important;
  padding-right: 10px !important;
  height: 25px;
  line-height: 25px;
}
.repository-tabs > .el-tabs__header .el-tabs__item.is-active,
.repository-tabs > .el-tabs__header .el-tabs__item:hover {
  color: #008992;
}
.repository-tabs > .el-tabs__content {
  position: absolute;
  bottom: 0px;
  top: 25px;
  left: 0px;
  right: 0px;
}
.repository-tabs > .el-tabs__content > .el-tab-pane {
  width: 100%;
  height: 100%;
}
.repository-tab-editor-box {
  position: absolute;
  bottom: 0px;
  top: 25px;
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
