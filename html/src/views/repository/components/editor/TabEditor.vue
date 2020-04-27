<template>
  <div
    v-show="tab.show_editor"
    class="tab-editor"
    v-loading="loading"
    :element-loading-text="loading_text"
  >
    <template v-show="errmsg!=null">
      <div class="text-center ft-20 color-red pdtb-60">{{errmsg}}</div>
    </template>
    <template v-show="errmsg==null">
      <div class="editor-designer-box">
        <template v-for="item in designers">
          <div
            :key="item.key"
            :designer="item.key"
            class="editor-designer"
            :class="{'active':item.active}"
          >
            <div class="editor-designer-toolbar" v-if="item.navs && item.navs.length>0">
              <div
                class="app-nav app-nav-full bg-blue-grey app-nav-full app-nav-vertical app-nav-xs"
              >
                <template v-for="(nav, index) in item.navs">
                  <li :key="index" v-if="nav.line"></li>
                  <li :key="index" v-else v-bind:title="nav.text" @click="nav.onClick()">
                    <a
                      class="active-color-white active-bg-blue-grey-8"
                      :class="{'coos-disabled' : nav.disabled}"
                    >
                      <i
                        v-show="!coos.isEmpty(nav.fonticon)"
                        v-bind:class="'coos-icon coos-icon-'+ nav.fonticon"
                      ></i>
                      {{nav.text}}
                    </a>
                  </li>
                </template>
              </div>
            </div>
            <div
              class="editor-designer-content"
              :style="{'top':(item.navs && item.navs.length>0)?'40px':'0px'}"
            ></div>
          </div>
        </template>
      </div>
      <div class="editor-designer-tab-box">
        <template v-for="item in designers">
          <div
            :key="item.key"
            @click="clickDesigner(item)"
            class="editor-designer-tab"
            :class="{'active':item.active}"
          >{{item.text}}</div>
        </template>
      </div>
    </template>
  </div>
</template>

<script>
import CodeEditor from "@/views/repository/components/editor/CodeEditor";
export default {
  props: ["tab"],
  components: {},
  data() {
    return {
      loading: false,
      errmsg: null,
      designers: [],
      isYaml: false,
      loading_text: ""
    };
  },
  watch: {
    "tab.show_editor": function(show) {
      if (show) {
        if (this.inited) {
          if (this.need_reload) {
            delete this.need_reload;
            this.reload();
          }
        } else {
          if (!this.initing) {
            this.initing = true;
            this.$nextTick(res => {
              delete this.need_reload;
              this.loading = true;
              this.loading_text = "编辑器依赖组件加载中...";
              source.loadEditorRely(res => {
                this.loading = false;
                this.init();
              });
            });
          }
        }
      }
    }
  },
  methods: {
    destroy() {},
    callReload() {
      if (!this.inited) {
        return;
      }
      if (!this.tab.show_editor) {
        this.need_reload = true;
        return;
      }
      this.reload();
    },
    reload() {
      delete this.need_reload;
      if (!this.file) {
        return;
      }
      source.loadFile(this.tab.path).then(res => {
        let value = res.value;
        if (!coos.isEmpty(value.errmsg)) {
          this.errmsg = value.errmsg;
          return;
        }
        if (this.file.content == value.content) {
          return;
        }
        coos
          .confirm("文件发生变更是否载入最新文件数据？")
          .then(res => {
            this.file = value;
            this.initEditor();
          })
          .catch(res => {});
      });
    },
    save(content, callback) {
      let readyonly = !source.hasPermission("FILE_SAVE");
      if (readyonly) {
        coos.error("暂无保存权限，请联系仓库管理员！");
        return;
      }
      this.loading = true;
      this.loading_text = "文件保存中...";
      let that = this;
      let start = new Date().getTime();
      let wait = 300;
      source.saveFile(this.file.path, content, function(flag) {
        let end = new Date().getTime();
        wait = wait - (end - start);
        wait = wait < 0 ? 0 : wait;
        setTimeout(res => {
          that.loading = false;
          if (flag) {
            that.file.content = content;
          }
          callback && callback(flag);
        }, wait);
      });
    },
    init() {
      let start = new Date().getTime();
      let wait = 300;
      this.loading = true;
      this.loading_text = "文件加载中...";
      source.loadFile(this.tab.path).then(res => {
        let value = res.value;
        let end = new Date().getTime();
        wait = wait - (end - start);
        wait = wait < 0 ? 0 : wait;
        setTimeout(res => {
          this.loading = false;
          if (!coos.isEmpty(value.errmsg)) {
            this.errmsg = value.errmsg;
            return;
          }
          this.file = value;
          this.initEditor();
          this.initing = false;
          this.inited = true;
        }, wait);
      });
    },
    addDesigner(designer) {
      if (designer) {
        designer.key = designer.key || "key-" + coos.getNumber();
        designer.text = designer.text || "Designer";
        designer.active = false;
        this.designers.push(designer);
      }
    },
    getDesigner(key) {
      let designer = null;
      this.designers.forEach(one => {
        if (one.key == key) {
          designer = one;
        }
      });
      return designer;
    },
    initEditor() {
      this.tab.changed = false;
      coos.trimList(this.designers);
      delete this.lastChangeData;
      delete this.lastChangeDesigner;

      let options = {};
      options.editor = this;
      options.file = this.file;
      let readyonly = !source.hasPermission("FILE_SAVE");
      options.readyonly = readyonly;

      let codeDesigner = new CodeEditor(Object.assign({}, options));
      this.addDesigner(codeDesigner);

      source.plugin.onCreateEditory(Object.assign({}, options));

      this.$nextTick(res => {
        this.clickDesigner(codeDesigner);
        let cache = this.getCache();
        if (cache != null) {
          let lastCacheDesigner = this.getDesigner(cache.key);

          if (lastCacheDesigner && lastCacheDesigner.setCacheData) {
            coos
              .confirm(
                "检测到上次未保存的变更，是否加载到编辑器？（注：不会自动保存）"
              )
              .then(res => {
                this.clickDesigner(lastCacheDesigner);
                lastCacheDesigner.setCacheData(cache.data);
              })
              .catch(res => {
                this.removeCache();
              });
          }
        }
      });
      return;
      this.buildCodeEditor();
    },
    getDesingerContent(type) {
      return $(this.$el).find(
        '[designer="' + type + '"].editor-designer .editor-designer-content'
      );
    },
    clickDesigner(designer) {
      let flag = false;
      this.designers.forEach(one => {
        if (one.active && one == designer) {
          flag = true;
        }
      });
      if (flag) {
        return;
      }
      this.designers.forEach(one => {
        one.active = false;
      });
      designer.active = true;
      if (!designer.builded) {
        designer.builded = true;
        this.$nextTick(res => {
          var $box = this.getDesingerContent(designer.key);
          designer.build && designer.build($box);
          designer.show && designer.show();
        });
      } else {
        this.$nextTick(res => {
          designer.show && designer.show();
        });
      }
    },
    loadLastContent(callback) {
      if (this.lastChangeDesigner && this.lastChangeDesigner.loadLastContent) {
        this.lastChangeDesigner.loadLastContent(callback);
      } else {
        callback && callback(this.file.content);
      }
    },
    onChange(changed, data, designer) {
      this.tab.changed = changed;
      this.lastChangeData = data;
      this.lastChangeDesigner = designer;
      if (changed) {
        this.setCache(data, designer);
      } else {
        this.removeCache();
      }
    },
    removeCache() {
      let key = this.getCacheKey();
      localStorage.removeItem(key);
    },
    getCache() {
      let key = this.getCacheKey();
      let data = localStorage.getItem(key);
      if (coos.isEmpty(data)) {
        return null;
      } else {
        return JSON.parse(data);
      }
    },
    setCache(obj, designer) {
      if (!designer) {
        return;
      }
      let data = {};
      data.key = designer.key;
      data.data = obj;
      let key = this.getCacheKey();
      localStorage.setItem(key, JSON.stringify(data));
    },
    getCacheKey(obj) {
      return (
        "EDITOR-CACHE-FILE-" +
        source.spaceid +
        "-" +
        source.branch +
        this.file.path
      );
    }
  },
  mounted() {
    this.tab.editor = this;
  },
  beforeCreate() {}
};
</script>

<style >
.editor-designer-box .CodeMirror {
  height: 100%;
  font-size: 14px;
}
</style>
<style scoped>
.tab-editor {
  width: 100%;
  height: 100%;
}
.editor-designer-box {
  position: absolute;
  top: 0px;
  left: 0px;
  bottom: 25px;
  width: 100%;
  background: #262626;
}
.editor-designer-box .editor-designer {
  height: 100%;
  width: 100%;
  display: none;
}
.editor-designer-box .editor-designer.active {
  height: 100%;
  width: 100%;
  display: block;
}
.editor-designer-box .editor-designer .editor-designer-content {
  position: absolute;
  top: 0px;
  left: 0px;
  bottom: 0px;
  width: 100%;
}
.editor-designer-tab-box {
  position: absolute;
  left: 0px;
  bottom: 0px;
  width: 100%;
  font-size: 12px;
  height: 25px;
  line-height: 25px;
  background-color: #93a6bd;
}
.editor-designer-tab-box .editor-designer-tab {
  display: inline-block;
  padding: 0px 10px;
  cursor: pointer;
  background-color: #607d8b;
  color: #ffffff;
}
.editor-designer-tab-box .editor-designer-tab.active {
  background-color: #37474f;
}
</style>
