
source.repository.starter_options = [];
source.repository.starter_show = false;
source.repository.starters = [];
source.repository.starterActive = "0";

(function () {


    source.starterDeploy = function (path, option) {
        source.do('STARTER_DEPLOY', { path: path, option: option }).then((res) => {
            if (res.errcode == 0) {
                coos.success('部署命令提交成功，正在部署！');
                source.load('STARTERS');
            } else {
                coos.error(res.errmsg);
            }
        });
    };

    source.deployStarter = function (starter) {
        if (starter.token != '0') {
            source.do('STARTER_DEPLOY', { token: starter.token }).then((res) => {
                if (res.errcode == 0) {
                    coos.success('部署命令提交成功，正在部署！');
                } else {
                    coos.error(res.errmsg);
                }
            });
        }
    };

    source.startStarter = function (starter) {
        if (starter.token != '0') {
            source.do('STARTER_START', { token: starter.token }).then((res) => {
                if (res.errcode == 0) {
                    coos.success('启动命令提交成功，正在启动！');
                } else {
                    coos.error(res.errmsg);
                }
            });
        }
    };

    source.stopStarter = function (starter) {
        if (starter.token != '0') {
            source.do('STARTER_STOP', { token: starter.token }).then((res) => {
                if (res.errcode == 0) {
                    coos.success('停止成功！');
                } else {
                    coos.error(res.errmsg);
                }
            });
        }
    };

    source.removeStarter = function (starter) {
        if (starter.token != '0') {
            source.do('STARTER_REMOVE', { token: starter.token }).then((res) => {
                if (res.errcode == 0) {
                    coos.success('移除命令已提交，正在移除！');
                } else {
                    coos.error(res.errmsg);
                }
            });
        }
    };


    let status_loading = false;
    source.loadStarterStatus = function () {
        if (status_loading) {
            return;
        }
        status_loading = true;
        if (!source.repository.starter_show) {
            window.setTimeout(() => {
                status_loading = false;
                source.loadStarterStatus();
            }, 1000);
            return;
        }
        let starter = source.getStarter(source.repository.starterActive);
        if (starter == null || starter.token == "0") {
            window.setTimeout(() => {
                status_loading = false;
                source.loadStarterStatus();
            }, 1000);
            return;
        }
        source.load('STARTER_STATUS', { token: starter.token });
        status_loading = false;
    };

    let log_loading = false;
    source.loadStarterLog = function () {
        if (log_loading) {
            return;
        }
        log_loading = true;
        if (!source.repository.starter_show) {
            window.setTimeout(() => {
                log_loading = false;
                source.loadStarterLog();
            }, 1000);
            return;
        }

        let starter = source.getStarter(source.repository.starterActive);
        if (starter == null) {
            window.setTimeout(() => {
                log_loading = false;
                source.loadStarterLog();
            }, 1000);
            return;
        }
        let start = starter.logStart;
        let end = starter.logEnd;
        let timestamp = starter.logTimestamp;
        start = start || 0;
        end = end || 0;
        if (start > 0) {
            let data = { token: starter.token };
            data.start = 0;
            if (start > load_size) {
                data.start = start - load_size;
            }
            data.end = start - 1;
            data.timestamp = timestamp;
            data.isloadold = true;
            source.load('STARTER_LOG', data);
        }
        if (start == 0 && end == 0) {
            let data = { token: starter.token };
            source.load('STARTER_LOG', data);
        } else {
            let data = { token: starter.token };
            data.start = end + 1;
            data.end = end + load_size;
            data.timestamp = timestamp;
            source.load('STARTER_LOG', data);
        }
        log_loading = false;
    };
    source.loadStarterStatus();
    source.loadStarterLog();



    source.openStarterBox = function () {
        source.repository.starter_show = true;
    };

    source.closeStarterBox = function () {
        source.repository.starter_show = false;
    };

    source.onStarterOptions = function (value) {
        value = value || [];
        coos.trimList(source.repository.starter_options);
        value.forEach(one => {
            source.repository.starter_options.push(one);
        });
    };

    source.onStarters = function (value) {
        value = value || [];
        if (source.repository.starters.length == 0) {
            source.repository.starters.push({
                text: "库控制台信息", token: "0", logs: [], status: null
            })
        }
        value.forEach(one => {
            let find = source.getStarter(one.token);
            if (find == null) {
                one.logs = [];
                one.text = '' + one.token;
                one.removed = false;
                if (one.name) {
                    one.text = '' + one.name;
                }
                source.repository.starters.push(one);
            } else {
                Object.assign(find, one);
            }
        });
        let need_removed = [];
        source.repository.starters.forEach(t => {
            if (t.token != '0') {
                let find = null;
                value.forEach(one => {
                    if (t.token == one.token) {
                        find = one;
                    }
                });
                if (find == null) {
                    need_removed.push(t);
                }
            }
        });
        need_removed.forEach(one => {
            source.repository.starters.splice(source.repository.starters.indexOf(one), 1);
        });
        let starter = source.getStarter(source.repository.starterActive);
        if (starter == null) {
            if (source.repository.starters.length > 0) {
                source.repository.starterActive = source.repository.starters[0].token;
            } else {
                source.repository.starterActive = null;
            }
        }

    };


    source.getStarter = function (token) {
        let find = null;
        source.repository.starters.forEach(t => {
            if (t.token == token) {
                find = t;
            }
        });
        return find;
    };
    source.cleanStarterLog = function (starter) {
        source.do('STARTER_LOG_CLEAN', { token: starter.token }).then((res) => {
            source.reloadStarterLog(starter);
        });
    };
    source.reloadStarterLog = function (starter) {

        delete starter.logStart;
        delete starter.logEnd;
        delete starter.logTimestamp;
        coos.trimArray(starter.logs);
        source.loadStarterLog();
    };

    let load_size = 50;
    source.onStarterLog = function (value) {
        value = value || {};

        let starter = source.getStarter(value.token);
        if (value.timestamp == null) {
            return;
        }

        var time = 1000;
        if (starter != null) {
            if (starter.logTimestamp == null) {
                starter.logTimestamp = value.timestamp;
            }

            if (starter.logTimestamp != value.timestamp) {
                coos.confirm('日志文件已被清理，是否重新读取？').then(res => {
                    source.reloadStarterLog(starter);
                });
                return;
            }

            var logs = value.logs || [];
            if (logs.length < load_size) {
                time = 1000;
            } else {
                time = 200;
            }
            if (logs.length > 0) {
                if (coos.isEmpty(starter.logStart)) {
                    starter.logStart = logs[0].index;
                } else {
                    if (starter.logStart > logs[0].index) {
                        starter.logStart = logs[0].index;
                    }
                }

                if (coos.isEmpty(starter.logEnd)) {
                    starter.logEnd = logs[logs.length - 1].index;
                } else {
                    if (starter.logEnd < logs[logs.length - 1].index) {
                        starter.logEnd = logs[logs.length - 1].index;
                    }
                }
                if (starter.logs.length == 0) {
                    for (let i = 0; i < starter.logStart; i++) {
                        starter.logs.push({ line: null, index: i, show: false });
                    }
                }
            }
            logs.forEach((log, i) => {
                if (starter.logs[log.index]) {
                    starter.logs[log.index].show = true;
                    starter.logs[log.index].line = log.line;
                    if (log.line) {
                        if (log.line.indexOf(' ERROR ') >= 0 || log.line.indexOf(':ERROR:') >= 0) {
                            starter.logs[log.index].level = 'ERROR';
                        } else if (log.line.indexOf(' WARN ') >= 0 || log.line.indexOf(':WARN:') >= 0 || log.line.indexOf(':WARN :') >= 0) {
                            starter.logs[log.index].level = 'WARN';
                        }
                    }
                } else {
                    log.show = true;
                    starter.logs.push(log);
                }
            });
        }
        if (value.isloadold) {
            return;
        }

        window.setTimeout(() => {
            source.loadStarterLog();
        }, time);
    };
    source.onStarterStatus = function (value) {
        value = value || {};

        let starter = source.getStarter(value.token);
        var time = 1000;
        if (starter != null) {
            if (value.removed) {
                starter.removed = true;
                source.load('STARTERS');
            } else {
                starter.status = value.status;
                starter.deploy_status = value.deploy_status;
                starter.install_status = value.install_status;
            }
        }
        window.setTimeout(() => {
            source.loadStarterStatus();
        }, time);
    };


})();

export default source;