import axios from 'axios'

import config from './config'
import store from '../../store'
import Vue from 'vue'
import { Message, Notification, MessageBox, Loading } from 'element-ui';

axios.defaults.withCredentials = true;

const httpClient = axios.create({
  baseURL: config.baseServiceURL,
  timeout: config.timeout,
  headers: config.headers
});

let loading = null;
httpClient.interceptors.request.use(function (config) {
  if (config.loading == true) {
    loading = base.loading();
  }
  config.headers = config.headers || {};
  config.headers.LOGIN_TOKEN = sessionStorage.getItem("LOGIN_TOKEN");
  return config;
}, function (error) {
  base.show({ type: 'error', message: '请求异常！' });
  if (loading) { loading.close() }
  return Promise.reject(error);
});

httpClient.interceptors.response.use(function (response) {
  if (loading) { loading.close() }
  if (response.headers['content-type'] && response.headers['content-type'].indexOf('text/html') != -1) {
    document.write(response.data)
  } else {
    if (response.data) {
      let data = response.data;
      try {
        const errcode = data.errcode;
        switch (errcode) {
          case 0:
            return data;
          case 10000:
            sessionStorage.removeItem("LOGIN_TOKEN");
            store.commit('session', null)
            base.show({ type: 'error', message: '暂无登录信息，请先登录！' });
            return data;
          default:
            return data;
        }
      } catch (err) {
        base.show({ type: 'error', message: err });
        return Promise.reject(err)
      }
    } else {
      base.show({ type: 'error', message: '返回数据格式错误！' });
      return Promise.resolve('返回数据格式错误！')
    }
  }
}, function (error) {
  if (loading) { loading.close() }
  if (error.toString().indexOf('Network Error') != -1 || error.toString().indexOf('timeout') != -1) {
    base.show({ type: 'error', message: '请求超时,请稍后重试！' });
  } else {
    base.show({ type: 'error', message: '系统内部错误！' });
  }
  return Promise.reject(error);
});




var base = {

  http: httpClient,
  show: (obj) => {
    return Message(obj);
  },
  notify: (obj) => {
    Notification(obj);
  },
  alert: (msg, title, config) => {
    return MessageBox.alert(msg, title, config);
  },
  confirm: (msg, title, config) => {
    return MessageBox.confirm(msg, title, config);
  },
  prompt: (msg, title, config) => {
    return MessageBox.prompt(msg, title, config);
  },
  loading: (options) => {
    return Loading.service(options);
  },

}

export default base;

