<template>
  <div
    class="contextmenu-box"
    :class="{'show' :contextmenu.show }"
    :style="{'top' :contextmenu.top ,'left' :contextmenu.left ,'z-index' :contextmenu.zIndex }"
  >
    <template v-if="contextmenu.menus != null">
      <ContextmenuMenus :menus="contextmenu.menus" :contextmenu="contextmenu"></ContextmenuMenus>
    </template>
  </div>
</template>

<script>
import ContextmenuMenus from "@/views/components/ContextmenuMenus";
export default {
  components: { ContextmenuMenus },
  props: ["contextmenu"],
  data() {
    return {
      source: source
    };
  },
  beforeMount() {},
  watch: {},
  methods: {},
  mounted() {
    $(document).on("click", "html", e => {
      this.contextmenu.show = false;
    });
    // $(document).on("contextmenu", e => {
    //   this.contextmenu.show = false;
    // });
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style  scoped>
.contextmenu-box {
  position: fixed;
  top: 0px;
  left: 0;
  z-index: 1000;
  display: none;
  float: left;
  min-width: 160px;
  padding: 5px 0;
  margin: 2px 0 0;
  list-style: none;
  background-color: #fff;
  border: 1px solid #ccc;
  border: 1px solid rgba(0, 0, 0, 0.2);
  font-family: helvetica neue, Helvetica, Arial, sans-serif;
  font-size: 14px;
  *border-right-width: 2px;
  *border-bottom-width: 2px;
  -webkit-border-radius: 6px;
  -moz-border-radius: 6px;
  border-radius: 6px;
  -webkit-box-shadow: 0 5px 10px rgba(0, 0, 0, 0.2);
  -moz-box-shadow: 0 5px 10px rgba(0, 0, 0, 0.2);
  box-shadow: 0 5px 10px rgba(0, 0, 0, 0.2);
  -webkit-background-clip: padding-box;
  -moz-background-clip: padding;
  background-clip: padding-box;
  text-align: left;
}
.contextmenu-box.show {
  display: block;
}
</style>
