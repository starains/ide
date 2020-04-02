

(function () {

    source.loadSession = function () {

        return source.load("SESSION");
    };
    source.ready = function () {
        return new Promise((resolve, reject) => {
            if (source.readyed) {
                resolve && resolve();
            } else {
                source.server.session().then(() => {
                    source.websocket.open();
                    source.loadSession().then(res => {
                        source.readyed = true;
                        resolve && resolve();
                    });

                });
            }
        });
    };
    source.validate = function () {
        source.do('VALIDATE', {});
    };
    source.onValidate = function () {
        window.setTimeout(() => {
            source.validate();
        }, 1000 * 10);
    };
    source.doLogout = function () {
        source.do('LOGOUT').then(res => {
            if (res.errcode == 0) {
                coos.info("退出成功！");
                window.setTimeout(() => {
                    window.location.reload();
                }, 300);
            } else {
                coos.error(res.errmsg);
            }
        });
    };
})();
export default source;