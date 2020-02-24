(function () {

    var websocket = null;
    let open_callbacks = [];

    let call_opened = function () {

        open_callbacks.forEach(open_callback => {
            open_callback && open_callback();
        });
        open_callbacks = [];
    }


    window.onbeforeunload = function () {
        websocket.close(3000, "强制关闭");
    }
    source.websocket = {
        opened: false,
        opening: false,
        sendText(message) {

            websocket.send(message);
        },
        open() {
            return new Promise((resolve, reject) => {
                if (source.websocket.opened) {
                    resolve && resolve();
                    return;
                }

                open_callbacks.push(resolve);
                if (source.websocket.opening) {
                    return;
                }
                source.websocket.opening = true;

                if ('WebSocket' in window) {
                    let url = window._SERVER_URL.replace('http', 'ws') + '/websocket/' + source.token;
                    websocket = new WebSocket(url);
                } else {
                    $('.app-loading-text').empty();
                    $('.app-loading-text').html('您的浏览器不支持WebSocket，请使用高版本浏览器！');
                    return;
                }
                websocket.onerror = function (error) {
                    console.error(error);
                }
                websocket.onopen = function () {
                    source.websocket.opening = false;
                    source.websocket.opened = true;
                    call_opened();

                }
                websocket.onclose = function (e) {
                    source.websocket.opening = false;
                    source.websocket.opened = false;
                    source.server.status().then((res) => {
                        if (res) {
                            source.websocket.open();
                        } else {
                            source.server.wait().then(() => {
                                source.websocket.open();
                            });
                        }
                    });
                }
                websocket.onmessage = function (event) {
                    let message = event.data;
                    if (message == '-1') {
                    }
                }
            });
        }
    };



})();

export default source;