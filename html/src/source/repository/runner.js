
source.repository.runner_options = [];
source.repository.runner_show = false;
source.repository.runners = [];
source.repository.runnerActive = "0";

(function () {

    let status_loading = false;
    source.loadRunnerStatus = function () {
        if (status_loading) {
            return;
        }
        status_loading = true;
        if (!source.repository.runner_show) {
            window.setTimeout(() => {
                status_loading = false;
                source.loadRunnerStatus();
            }, 1000);
            return;
        }
        let runner = source.getRunner(source.repository.runnerActive);
        if (runner == null || runner.token == "0") {
            window.setTimeout(() => {
                status_loading = false;
                source.loadRunnerStatus();
            }, 1000);
            return;
        }
        source.load('TERMINAL_STATUS', { token: runner.token });
        status_loading = false;
    };

    let log_loading = false;
    source.loadRunnerLog = function () {
        if (log_loading) {
            return;
        }
        log_loading = true;
        if (!source.repository.runner_show) {
            window.setTimeout(() => {
                log_loading = false;
                source.loadRunnerLog();
            }, 1000);
            return;
        }

        let runner = source.getRunner(source.repository.runnerActive);
        if (runner == null) {
            window.setTimeout(() => {
                log_loading = false;
                source.loadRunnerLog();
            }, 1000);
            return;
        }
        let data = { token: runner.token };
        data.lastIndex = runner.lastIndex;
        data.lastLine = runner.lastLine;
        source.load('TERMINAL_LOG', data);
        log_loading = false;
    };
    source.loadRunnerStatus();
    source.loadRunnerLog();


    source.startRunner = function (runner) {
        if (runner.token != '0') {
            source.do('TERMINAL_START', { token: runner.token }).then(() => {
            });
        }
    };

    source.stopRunner = function (runner) {
        if (runner.token != '0') {
            source.do('TERMINAL_STOP', { token: runner.token }).then(() => {
            });
        }
    };

    source.destroyRunner = function (runner) {
        if (runner.token != '0') {
            source.do('TERMINAL_DESTROY', { token: runner.token }).then(() => {
            });
        }
    };
    source.removeRunner = function (runner) {
        if (runner.token != '0') {
            source.do('TERMINAL_REMOVE', { token: runner.token }).then(() => {
            });
        }
    };



    source.cleanRunnerLog = function (runner) {
        source.do('TERMINAL_LOG_CLEAN', { token: runner.token }).then(() => {
            delete runner.lastIndex;
            delete runner.lastLine;
            coos.trimList(runner.logs);
        });
    };
    source.openRunnerBox = function () {
        source.repository.runner_show = true;
    };

    source.closeRunnerBox = function () {
        source.repository.runner_show = false;
    };

    source.onRunOptions = function (value) {
        value = value || [];
        coos.trimList(source.repository.run_options);
        value.forEach(one => {
            source.repository.run_options.push(one);
        });
    };

    source.onRunners = function (value) {
        value = value || [];
        if (source.repository.runners.length == 0) {
            source.repository.runners.push({
                text: "库控制台信息", token: "0", logs: [], status: null, option: null
            })
        }
        value.forEach(one => {
            let find = source.getRunner(one.token);
            if (find == null) {
                one.logs = [];
                one.text = '' + one.token;
                if (one.option) {
                    one.text = '' + one.option.name;
                }
                source.repository.runners.push(one);
            } else {
                Object.assign(find, one);
            }
        });
        let need_removed = [];
        source.repository.runners.forEach(t => {
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
            source.repository.runners.splice(source.repository.runners.indexOf(one), 1);
        });
        let runner = source.getRunner(source.repository.runnerActive);
        if (runner == null) {
            if (source.repository.runners.length > 0) {
                source.repository.runnerActive = source.repository.runners[0].token;
            } else {
                source.repository.runnerActive = null;
            }
        }

    };


    source.getRunner = function (token) {
        let find = null;
        source.repository.runners.forEach(t => {
            if (t.token == token) {
                find = t;
            }
        });
        return find;
    };
    source.onRunnerLog = function (value) {
        value = value || {};

        let runner = source.getRunner(value.token);
        var time = 1000;
        if (runner != null) {
            if (coos.isEmpty(value.lastLine)) {
                time = 1000;
            } else {
                time = 200;
                runner.lastIndex = value.lastIndex;
                runner.lastLine = value.lastLine;
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
                runner.logs.push(log);
            });
        }

        window.setTimeout(() => {
            source.loadRunnerLog();
        }, time);
    };
    source.onRunnerStatus = function (value) {
        value = value || {};

        let runner = source.getRunner(value.token);
        var time = 1000;
        if (runner != null) {
            runner.status = value.status;
        }
        window.setTimeout(() => {
            source.loadRunnerStatus();
        }, time);
    };


    source.runStart = function (path, option) {
        source.do('TERMINAL_START', { path: path, option: option })
    };
})();

export default source;