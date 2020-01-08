// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import App from './App'
import router from './router'
import store from './store'
import app from "@/common/js/app";
import source from "@/source/index";


Vue.use(ElementUI);
Vue.config.productionTip = false;

/* eslint-disable no-new */
window.app_main = function () {
  coos.startZIndex = 2000;
  Vue.prototype.coos = coos;
  Vue.prototype.app = app;
  Vue.prototype.source = source;
  Vue.prototype._SERVER_URL = window._SERVER_URL;
  Vue.prototype._ROOT_URL = window._ROOT_URL;

  source.ready().then(() => {
    //app.frame.build($('#app-container'));
    new Vue({
      el: '#main',
      router,
      store,
      components: { App },
      mounted() {
      },
      template: '<App/>'
    });
    $('.app-loading-box').addClass('hide');
    window.setTimeout(function () {
      $('.app-loading-box').remove();
    }, 310);
  });
};

document.writeln('<script type="text/javascript" src="' + _SERVER_URL + '/resources/app.resources.js"></script>');
document.writeln('<script type="text/javascript" src="' + _SERVER_URL + '/resources/app.main.js"></script>');
