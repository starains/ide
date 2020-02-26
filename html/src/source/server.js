
(function () {
    source.server = {
        post(action, data) {
            return new Promise((resolve, reject) => {
                data = data || {};
                let url = _SERVER_URL + action;
                $.ajax({
                    url: url,
                    data: data,
                    type: "post",
                    beforeSend: function () { },
                    success: function (status) {
                        resolve && resolve(status);
                    },
                    complete: function (XMLHttpRequest, textStatus) { },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        reject && reject();
                    }
                });

            });
        },
        session(check) {
            return new Promise((resolve, reject) => {
                let action = '/api/data/session';

                var SPACE_PATH = window.location.href;
                if (SPACE_PATH.indexOf('#') > 0) {
                    SPACE_PATH = SPACE_PATH.split('#')[0];
                }
                SPACE_PATH = SPACE_PATH.substring(_ROOT_URL.length);
                source.server.post(action, { SPACE_PATH: SPACE_PATH }).then((status) => {
                    let value = status.value || {};

                    if (check) {
                        if (source.installed) {
                            delete value['installed'];
                        }
                    }

                    $(Object.keys(value)).each(function (index, key) {
                        source[key] = value[key];
                    });
                    source.CONFIGURE = value.CONFIGURE || {};

                    if (!check) {
                        source.data = {};
                        if (value.ENUM_MAP && value.ENUM_MAP.MODEL_TYPE) {
                            $(value.ENUM_MAP.MODEL_TYPE).each(function (index, one) {
                                source.data[one.value] = null;
                            });
                        }
                    }
                    resolve && resolve(status);
                });
            });
        },
        load(type, data) {
            return new Promise((resolve, reject) => {
                data = data || {};
                let action = '/api/workspace/load/' + type + '/' + source.token;
                source.server.post(action, JSON.stringify(data)).then(res => {
                    source.onData(type, res);
                    resolve && resolve(res);
                });
            });
        },
        do(type, data) {
            data = data || {};
            let action = '/api/workspace/do/' + type + '/' + source.token;
            return source.server.post(action, JSON.stringify(data));
        },
        restart() {
            return new Promise((resolve, reject) => {
                window.setTimeout(function () {
                    source.screen.error('服务器重启中，请稍后...');
                    source.server.status().then((res) => {
                        if (res) {
                            source.screen.success('服务器启动成功，即将刷新页面！');
                            window.setTimeout(function () {
                                source.screen.remove();
                                resolve && resolve();
                            }, 500);
                        } else {
                            window.setTimeout(function () {
                                source.server.restart().then(resolve);
                            }, 3000);
                        }
                    });
                }, 5000);
            });
        },
        status() {
            return new Promise((resolve, reject) => {
                $.ajax({
                    url: _SERVER_URL + "/api/validate",
                    data: {},
                    type: "post",
                    beforeSend: function () { },
                    success: function (o) {
                        if (o.value && !o.value.isLogin && source.LOGIN_USER_TOKEN) {
                            source.server.session(true).then(() => {
                                source.do('AUTO_LOGIN', { token: source.LOGIN_USER_TOKEN }).then(res => {
                                    resolve && resolve(true);
                                });
                            });
                        } else {
                            resolve && resolve(true);
                        }
                    },
                    complete: function (XMLHttpRequest, textStatus) { },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        resolve && resolve(false);
                    }
                });
            });
        },
        wait() {
            return new Promise((resolve, reject) => {
                // source.screen.error('服务器连接异常，重连中，请稍后...');
                source.server.status().then((res) => {
                    if (res) {
                        wait_check_count = 0;
                        // source.screen.success('服务器连接成功！');
                        window.setTimeout(function () {
                            // source.screen.remove();
                            resolve && resolve();
                        }, 500);
                    } else {
                        wait_check_count++;
                        let second = 3;
                        if (wait_check_count >= 3) {
                            second = 5;
                        }
                        if (wait_check_count >= 5) {
                            second = 10;
                        }
                        if (wait_check_count >= 10) {
                            second = 30;
                        }
                        if (wait_check_count >= 20) {
                            // source.screen.error('服务器连接异常，请联系管理员！');
                            return;
                        }
                        window.setTimeout(function () {
                            source.server.wait().then(resolve);
                        }, second * 1000);
                    }
                });
            });
        }
    };
    let wait_check_count = 0;


    let listen_error_count = 0;
    let listen = function () {
        $.ajax({
            url: _SERVER_URL + "/api/listen",
            data: {},
            type: "post",
            beforeSend: function () { },
            success: function (o) {
                console.log(o);
                listen_error_count = 0;
                listen();
            },
            complete: function (XMLHttpRequest, textStatus) { },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                let second = 3;
                if (listen_error_count >= 3) {
                    second = 5;
                }
                if (listen_error_count >= 5) {
                    second = 10;
                }
                if (listen_error_count >= 10) {
                    second = 30;
                }
                if (listen_error_count >= 20) {
                    // source.screen.error('服务器连接异常，请联系管理员！');
                    second = 60;
                }
                listen_error_count++;
                window.setTimeout(() => {
                    listen();
                }, second * 1000);
            }
        });
    };
    //listen();
})();
export default source;