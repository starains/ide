(function () {

    let resolve_map = {};
    let reject_map = {};
    source.do = function (type, data) {
        return source.send({ type: type, data: data });
    };
    source.on = function (messageID, message) {
        if (coos.isEmpty(message)) {
            return;
        }

        if (message.type == 'DATA') {
            resolve_map[messageID] && resolve_map[messageID](message.data.value, message);
            delete resolve_map[messageID];
        } else {
            resolve_map[messageID] && resolve_map[messageID](message.data, message);
            delete resolve_map[messageID];
        }
        switch (message.type) {
            case "DATA":
                source.onData(message.data, message);
                break;
            case "MESSAGE":
                source.onMessage(message.data, message);
                break;
            case "LOGIN":
                source.onLogin(message.data, message);
                break;
            case "LOGOUT":
                source.onLogout(message.data, message);
                break;
            case "VALIDATE":
                source.onValidate(message.data, message);
                break;
        }

    };
    source.send = function (message) {
        return new Promise((resolve, reject) => {
            let messageID = coos.getNumber();
            message = message || {};
            message.messageID = messageID;
            resolve_map[messageID] = resolve;
            reject_map[messageID] = reject;

            source.websocket.sendText(JSON.stringify(message));
        });
    };
    source.onMessage = function (data) {
        data = data || {};
        switch (data.level) {
            case "INFO":
                coos.info(data.message);
                break;
            case "WARN":
                coos.warn(data.message);
                break;
            case "ERROR":
                coos.error(data.message);
                break;
            case "SUCCESS":
                coos.success(data.message);
                break;
        }
    };
})();

export default source;