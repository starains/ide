
source.repository.starter_options = [];
source.repository.starter_show = false;
source.repository.starters = [];
source.repository.starterActive = "0";

(function () {

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
        let data = { token: starter.token };
        data.lastIndex = starter.lastIndex;
        source.load('STARTER_LOG', data);
        log_loading = false;
    };
    source.loadStarterStatus();
    source.loadStarterLog();


    source.startStarter = function (starter) {
        if (starter.token != '0') {
            source.do('STARTER_START', { token: starter.token }).then((res) => {
                if (res.errcode == 0) {
                    coos.success('启动成功！');
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

    source.destroyStarter = function (starter) {
        if (starter.token != '0') {
            source.do('STARTER_DESTROY', { token: starter.token }).then((res) => {
                if (res.errcode == 0) {
                    coos.success('销毁成功！');
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
                    coos.success('移除成功！');
                    source.load('STARTERS');
                } else {
                    coos.error(res.errmsg);
                }
            });
        }
    };



    source.cleanStarterLog = function (starter) {
        source.do('STARTER_LOG_CLEAN', { token: starter.token }).then((res) => {
            delete starter.lastIndex;
            coos.trimList(starter.logs);
        });
    };
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
                text: "库控制台信息", token: "0", logs: [], status: null, option: null
            })
        }
        value.forEach(one => {
            let find = source.getStarter(one.token);
            if (find == null) {
                one.logs = [];
                one.text = '' + one.token;
                if (one.option) {
                    one.text = '' + one.option.name;
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
    source.onStarterLog = function (value) {
        value = value || {};

        let starter = source.getStarter(value.token);
        var time = 1000;
        if (starter != null) {
            starter.lastIndex = value.lastIndex;
            if (value.hasNext) {
                time = 1000;
            } else {
                time = 200;
            }
            var lines = value.lines || [];
            lines.forEach(line => {
                var log = {};
                log.msg = line;
                if (line) {
                    if (line.indexOf(">>] [<<") > 0) {
                        var groups = line.split(">>] [<<");
                        if (groups.length > 0) {
                            log.time = groups[0].replace(">>]", "").replace("[<<", "");
                        }
                        if (groups.length > 1) {
                            log.thread = groups[1].replace(">>]", "").replace("[<<", "");
                        }
                        if (groups.length > 2) {
                            log.level = groups[2].replace(">>]", "").replace("[<<", "");
                        }
                        if (groups.length > 3) {
                            log.msg = groups[3].replace(">>]", "").replace("[<<", "");
                        }
                    }
                }
                starter.logs.push(log);
            });
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
            starter.status = value.status;
        }
        window.setTimeout(() => {
            source.loadStarterStatus();
        }, time);
    };


    source.starterStart = function (path, option) {
        source.do('STARTER_START', { path: path, option: option }).then((res) => {
            if (res.errcode == 0) {
                coos.success('启动命令提交成功！');
                source.load('STARTERS');
            } else {
                coos.error(res.errmsg);
            }
        });
    };
})();

export default source;