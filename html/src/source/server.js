
(function () {
    source.server = {
        session() {
            return new Promise((resolve, reject) => {
                var SPACE_PATH = window.location.href;
                if (SPACE_PATH.indexOf('#') > 0) {
                    SPACE_PATH = SPACE_PATH.split('#')[0];
                }
                SPACE_PATH = SPACE_PATH.substring(_ROOT_URL.length);
                let url = _SERVER_URL + '/api/data/session?SPACE_PATH=' + SPACE_PATH;
                $.ajax({
                    url: url,
                    data: {},
                    type: "post",
                    beforeSend: function () { },
                    success: function (status) {
                        let value = status.value || {};

                        $(Object.keys(value)).each(function (index, key) {
                            source[key] = value[key];
                        });
                        source.CONFIGURE = value.CONFIGURE || {};
                        source.data = {};
                        if (value.ENUM_MAP && value.ENUM_MAP.MODEL_TYPE) {
                            $(value.ENUM_MAP.MODEL_TYPE).each(function (index, one) {
                                source.data[one.value] = null;
                            });
                        }
                        resolve && resolve(value);
                    },
                    complete: function (XMLHttpRequest, textStatus) { },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                    }
                });

            });
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
                        resolve && resolve(true);
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
                source.screen.error('服务器连接异常，重连中，请稍后...');
                source.server.status().then((res) => {
                    if (res) {
                        wait_check_count = 0;
                        source.screen.success('服务器连接成功！');
                        window.setTimeout(function () {
                            source.screen.remove();
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
                            source.screen.error('服务器连接异常，请联系管理员！');
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

})();
export default source;