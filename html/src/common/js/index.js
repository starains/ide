import config from '@/common/js/config'
import common from '@/common/js/common'
import service from '@/common/js/service'

export default {
  config: config,
  common: common,
  service: service,
  info(text) {
    common.show({ type: '', message: text })
  },
  success(text) {
    common.show({ type: 'success', message: text })
  },
  warn(text) {
    common.show({ type: 'warning', message: text })
  },
  error(text) {
    common.show({ type: 'error', message: text })
  },
  alert(msg) {
    return common.alert(msg, ' 提示', { confirmButtonText: '确定', type: 'warning', center: true })
  },
  confirm(msg) {
    return common.confirm(msg, ' 提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning', center: true })
  },
  prompt(msg) {
    return common.prompt(msg, ' 提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'info', center: true })
  }
}

