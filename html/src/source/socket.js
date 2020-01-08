(function () {
    let opened = false;
    let reconnectCount = 0;

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

                if (reconnectCount > 0) {
                    source.screen.error('断线重连中，请稍后...')
                }
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
                    if (reconnectCount > 0) {
                        source.screen.remove()
                    }
                    reconnectCount = 0;
                    source.validate();
                    source.load('SESSION');
                    call_opened();

                }
                websocket.onclose = function (e) {
                    source.websocket.opening = false;
                    source.websocket.opened = false;
                    return;
                    source.server.status().then((res) => {
                        if (res) {
                            reconnectCount++;
                            source.websocket.open().then(resolve);
                        } else {
                            source.server.wait().then(() => {
                                reconnectCount++;
                                source.websocket.open().then(resolve);
                            });
                        }
                    });
                    // coos.error('WebSocket closed!');
                }
                websocket.onmessage = function (event) {
                    let message = event.data;
                    message = JSON.parse(message);
                    let messageID = message.messageID;
                    source.on(messageID, message, event);
                }
            });
        }
    };



})();

export default source;