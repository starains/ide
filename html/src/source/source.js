

(function () {

    source.loadSession = function () {

        source.load("SESSION").then(res => {
        });
    };
    source.ready = function () {
        return new Promise((resolve, reject) => {
            if (source.readyed) {
                resolve && resolve();
            } else {
                source.server.session().then(() => {
                    source.readyed = true;
                    source.loadSession();
                    // source.websocket.open();
                    resolve && resolve();
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
                    source.loadSession();
                }, 300);
            } else {
                coos.error(res.errmsg);
            }
        });
    };
})();
export default source;