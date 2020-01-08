

(function () {
    source.ready = function () {
        return new Promise((resolve, reject) => {
            if (source.websocket.opened) {
                resolve && resolve();
            } else {
                source.server.session().then(() => {
                    source.websocket.open().then(resolve);
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
})();
export default source;