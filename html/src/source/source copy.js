
import tool from "@/common/js";

var source = window.source = new Object();
source.isLogin = false;
source.LOGIN_USER = null;
source.isManager = false;
source.roles = [];
source.toRegister = false;
source.SPACE_TYPE = null;
source.space = null;
source.branch = null;
source.repository = {
    loading: false,
    branchs: [],
    branch: null,
    projects: [],
    git: null,
    terminals: [],
    runners: [],
    open_files: [],
    open_folders: [],
    activeTab: null,
    tabs: [],
    contextmenu: {
        show: false, menus: []
    },
};
source.UPGRADE_STATUS = null;
source.UPGRADE_DOWNLOAD = { count: 0, downloaded_count: 0, downloading_path: '' };
source.cache = {};
window.source = source;
(function () {
    let source_ready_callbacks = [];
    let source_readyed = false;
    let source_readying = false;
    let maven_format_folders = ["src/main/java", "src/main/resources", "src/main/webapp"];

    source.removeTab = function (name) {
        let index = -1;
        source.repository.tabs.forEach((one, i) => {
            if (one.name == name) {
                index = i;
            }
        });

        if (index > -1) {
            source.repository.tabs.splice(index, 1);
            source.openTabByIndex(index);
        }
    };
    source.openTabByIndex = function (index) {
        if (coos.isEmpty(index) || index < 0) {
            index = 0;
        }
        if (index >= source.repository.tabs.length) {
            index = source.repository.tabs.length - 1;
        }
        if (index > -1) {
            source.repository.activeTab = source.repository.tabs[index].name;
        }
    };
    source.createTabByPath = function (path) {
        if (path == null) {
            return;
        }
        let tab = null;
        source.repository.tabs.forEach(one => {
            if (one.name == path) {
                tab = one;
            }
        });

        if (tab == null) {
            tab = {};
            tab.name = path;
            tab.title = path;
            tab.isFile = true;
            source.repository.tabs.push(tab);
        }
        source.repository.activeTab = path;

    };

    source.reloadProject = function (project) {

        project.loading = true;
        source.load('PROJECT_FILES', { path: project.path });
    };
    source.loadRepository = function (value) {
        coos.trimList(source.repository.branchs);
        coos.trimList(source.repository.projects);
        coos.trimList(source.repository.terminals);
        coos.trimList(source.repository.runners);

        if (value.branchs) {
            value.branchs.forEach(one => {
                source.repository.branchs.push(one);
            });
        } if (value.projects) {
            value.projects.forEach(one => {
                source.formatFile(one);
                one.isProject = true;
                one.loading = true;
                if (one.root) {
                    one.name = source.space.name;
                }
                one.files = [];
                if (one.maven && one.packaging != 'pom') {
                    one.format_folders = maven_format_folders;
                }
                source.repository.projects.push(one);
                source.load('PROJECT_FILES', { path: one.path });
            });
        }
        if (value.terminals) {
            value.terminals.forEach(one => {
                source.repository.terminals.push(one);
            });
        }
        if (value.runners) {
            value.runners.forEach(one => {
                source.repository.runners.push(one);
            });
        }
        source.loadGit(value.git);
    };
    source.loadFiles = function (value) {
        console.log(value);
    };
    source.loadGit = function (value) {
        if (value == null) {
            return;
        }
        if (source.repository.git == null) {
            source.repository.git = value;

        }
    };
    source.loadProjectFiles = function (value) {
        if (value == null) {
            return;
        }
        let project = source.getProjectByPath(value.path);
        if (project == null) {
            return;
        }
        project.files = project.files || [];
        coos.trimList(project.files);
        value.files = value.files || [];
        source.formatFiles(value.files, project);

        if (project.format_folders && project.format_folders.length > 0) {
            let rootPath = '';
            if (!project.root) {
                rootPath = project.path + '/';
            }
            project.format_folders.forEach(format_folder => {
                let format_file = { name: format_folder, path: rootPath + format_folder, directory: true, files: [] };

                let file = source.getFileFromFiles(format_file.path, value.files);
                if (file != null) {
                    if (file.parent != null) {
                        file.parent.files.splice(file.parent.files.indexOf(file), 1);
                    }
                    if (file.files != null) {
                        file.files.forEach(f => {
                            format_file.files.push(f);
                        });
                        coos.trimList(file.files);
                    }
                }

                source.formatFile(format_file, project);
                project.files.push(format_file);
            });
            value.files.forEach(file => {
                project.files.push(file);
            });
        } else {
            value.files.forEach(file => {
                project.files.push(file);
            });
        }
        project.loading = false;
    };
    source.getFileFromFiles = function (path, files) {
        if (coos.isEmpty(path)) {
            path == null;
        }
        if (files == null || files.length == 0) {
            return null;
        }
        let result = null;
        files.forEach(file => {
            if (result == null) {

                if (file.path == path) {
                    result = file;
                }
                if (result == null && file.files != null) {
                    result = source.getFileFromFiles(path, file.files);
                }
            }
        });
        return result;

    };
    source.formatFiles = function (files, parent) {
        if (files == null) {
            return;
        }
        files.forEach(file => {
            source.formatFile(file, parent);
            if (file.files) {
                source.formatFiles(file.files, file);
            }
        });
    };
    source.formatFile = function (file, parent) {
        if (file == null) {
            return;
        }
        if (!file.formated) {
            file.modified = false;
            file.untracked = false;
            file.conflicting = false;
            file.isProject = false;
            file.isRoot = false;
            file.isMaven = false;
            file.isDirectory = false;
            file.isFile = false;
            file.changed = false;
            file.saveing = false;
            file.loading = false;
            file.toRename = false;
            file.formated = true;
        }
        file.parent = parent;
        file.isProject = file.isProject;
        file.isDirectory = file.directory;
        file.isFile = file.file;
        file.isRoot = file.root;
        file.isMaven = file.maven;
    };
    source.updateFileName = function (file, new_name) {
        let old_path = file.path;
        let parentPath = old_path.substring(0, old_path.lastIndexOf(file.name));
        let new_path = parentPath + new_name;
        file.name = new_name;
        file.path = new_path;
        source.updateFilesPath(file.files, old_path, new_path);

    };
    source.updateFilesPath = function (files, old_p_path, new_p_path) {
        if (files == null) {
            return;
        }
        files.forEach(file => {
            file.path = file.path.replace(old_p_path, new_p_path);
            source.updateFilesPath(file.files);
        });

    };

    source.parentFile = function (path) {
        if (path == null) {
            return null;
        }
        let rootName = path;
        if (path.indexOf('/') > 0) {
            rootName = path.substring(0, path.indexOf('/'));
        }
        let project = source.getProjectByPath(rootName);

        project.files.forEach(file => {

        });
    };
    source.getProjectByPath = function (path) {
        let result = null;
        if (source.repository) {
            if (source.repository.projects) {
                source.repository.projects.forEach(project => {
                    if (!project.root) {
                        let projectPath = project.path;
                        if (path == projectPath) {
                            result = project;
                        }
                        else {
                            if (!projectPath.endsWith("/")) {
                                projectPath += "/";
                            }
                            if (path.startsWith(projectPath)) {
                                result = project;
                            }
                        }
                    }
                });

                if (result == null) {
                    source.repository.projects.forEach(project => {
                        if (project.root) {
                            result = project;
                        }
                    });
                }
            }
        }
        return result;
    };

    source.get = function (type, id) {
        return new Promise((resolve, reject) => {
            if (coos.isEmpty(type)) {
                resolve && resolve(null);
                return;
            }
            let map = source.cache[type] || {};
            source.cache[type] = map;
            if (map[id] != null) {
                resolve && resolve(map[id]);
                return;
            } else {
                loadOne(type, id).then(res => {
                    map[id] = res;
                    resolve && resolve(map[id]);
                });
            }
        });
    };
    let LOADONE_LOADING = {};
    let loadOne = function (type, id) {

        return new Promise((resolve, reject) => {
            if (coos.isEmpty(type)) {
                resolve && resolve(null);
                return;
            }
            let key = type + '-' + id;
            if (LOADONE_LOADING[key]) {
                LOADONE_LOADING[key].push(resolve);
                return;
            } else {
                LOADONE_LOADING[key] = [];
                LOADONE_LOADING[key].push(resolve);
            }
            source.load('ONE', { type: type, id: id }).then(res => {
                let list = LOADONE_LOADING[key];
                delete LOADONE_LOADING[key];
                list.forEach(one => {
                    one && one(res);
                })
            });
        });
    };
    source.ready = function (callback) {

        if (source_readyed) {
            callback && callback();
        } else {
            source_ready_callbacks.push(callback);
        }
        if (!source_readying) {
            init();
        }
    };
    source.getModel = function (type) {
        let model = null;
        source.ENUM_MAP.MODEL_TYPE.forEach(one => {
            if (one.value == type) {
                model = one;
            }
        });
        return model;
    };

    source.load = function (model, data) {
        data = data || {};
        data.model = model;
        model = source.getModel(model);

        if (model && model.type == 'PAGE') {
            if (coos.isEmpty(data.pageindex)) {
                if (source.data[data.model]) {
                    data.pageindex = Number(source.data[data.model].pageindex) + 1;
                }
            }
            if (coos.isEmpty(data.pagesize)) {
                if (source.data[data.model]) {
                    data.pagesize = Number(source.data[data.model].pagesize);
                }
            }
        }
        return source.do('DATA', data);
    };


    source.sendMessage = function (messageID, message) {
        message = message || {};
        websocket.ready(function () {
            if (typeof (message) == 'string') {
                websocket.sendText(messageID, message);
            } else {
                websocket.sendText(messageID, JSON.stringify(message));
            }
        });
    };
    source.on = function (messageID, message) {
        if (coos.isEmpty(message)) {
            return;
        }
        message = JSON.parse(message);

        if (message.type == 'DATA') {
            resolve_map[messageID] && resolve_map[messageID](message.data.value, message);
            delete resolve_map[messageID];
        } else {
            resolve_map[messageID] && resolve_map[messageID](message.data, message);
            delete resolve_map[messageID];
        }

        on_do_cache[message.type] = on_do_cache[message.type] || [];

        on_do_cache[message.type].forEach(callback => {
            callback && callback(message.data);
        });

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
        }

    };
    let resolve_map = {};
    let reject_map = {};
    source.do = function (type, data) {
        let messageID = coos.getNumber();
        source.sendMessage(messageID, { type: type, data: data });

        return new Promise((resolve, reject) => {
            resolve_map[messageID] = resolve;
            reject_map[messageID] = reject;
        });
    };
    source.onData = function (data) {
        data = data || {};
        on_load_cache[data.model] = on_load_cache[data.model] || [];

        on_load_cache[data.model].forEach(callback => {
            callback && callback(data.value);
        });
        let value = data.value;
        if (data.model == 'SESSION') {
            value = value || {};
            source.LOGIN_USER = value.LOGIN_USER;
            source.isLogin = source.LOGIN_USER != null;
            source.isManager = value.isManager;
            coos.trimList(source.roles);
            if (value.roles) {
                value.roles.forEach(role => {
                    source.roles.push(role);
                });
            }
            if (source.isLogin) {
                if (!coos.isEmpty(source.spaceid)) {
                    source.load('SPACE');
                }
            }
            return;
        } else if (data.model == 'CONFIGURE') {
            value = value || {};
            Object.assign(source.CONFIGURE, value);
            return;
        } else if (data.model == 'UPGRADE_DOWNLOAD') {
            value = value || {};
            Object.assign(source.UPGRADE_DOWNLOAD, value);
            return;
        } else if (data.model == 'UPGRADE_STATUS') {
            source.UPGRADE_STATUS = value;
            return;
        } else if (data.model == 'SPACE') {
            if (value != null) {
                value.levels = null;
            }
            if (source.space == null) {
                source.space = value;
            }
            if (source.space == null && value == null) {
                return;
            } else {
                Object.assign(source.space, value);
            }
            source.load('PARENTS');
            if (source.space.type == "REPOSITORYS") {
                source.load('REPOSITORY');
                source.load('BRANCHS');
            }
            return;
        } else if (data.model == 'REPOSITORY') {
            source.loadRepository(value);
            return;
        } else if (data.model == 'FILES') {
            source.loadFiles(value);
            return;
        } else if (data.model == 'PROJECT_FILES') {
            source.loadProjectFiles(value);
            return;
        } else if (data.model == 'GIT') {
            source.loadGit(value);
            return;
        } else if (data.model == 'PARENTS') {
            value = value || [];
            value.push(source.space);
            value.forEach((one, index) => {
                if ((index + 1) < value.length) {
                    value[index + 1].levels = one.childs;
                }
            });

        }
        let model = source.getModel(data.model);
        if (model == null) {
            return;
        }
        if (model.type == 'ONE') {
            if (source.data[data.model] == null) {
                source.data[data.model] = value;
            } else {
                if (coos.isObject(value)) {
                    Object.assign(source.data[data.model], value);
                } else {
                    source.data[data.model] = value;
                }
            }
        } else if (model.type == 'LIST') {
            if (source.data[data.model] == null) {
                source.data[data.model] = [];
            }
            source.data[data.model].splice(0, source.data[data.model].length);
            value = value || [];
            value.forEach(one => {
                source.data[data.model].push(one);
            });
        } else if (model.type == 'PAGE') {
            if (source.data[data.model] == null) {
                source.data[data.model] = {
                    pageindex: 1,
                    totalpages: 0,
                    totalcount: 0,
                    pagesize: 10,
                    uppage: 1,
                    nextpage: 1,
                    hasNext: false,
                    value: [],
                    list: []
                };
            }
            value = value || {};
            source.data[data.model].pageindex = value.pageindex;
            source.data[data.model].totalpages = value.totalpages;
            source.data[data.model].totalcount = value.totalcount;
            source.data[data.model].pagesize = value.pagesize;
            source.data[data.model].uppage = value.uppage;
            source.data[data.model].nextpage = value.nextpage;
            source.data[data.model].hasNext = value.hasNext;
            source.data[data.model].value.splice(0, source.data[data.model].value.length);
            if (value.pageindex == 1) {
                source.data[data.model].list.splice(0, source.data[data.model].list.length);
            }

            value.value = value.value || [];
            value.value.forEach(one => {
                source.data[data.model].value.push(one);
                source.data[data.model].list.push(one);
            });
        }

    };

    let on_load_cache = {};

    source.onLoad = function (type, callback) {
        on_load_cache[type] = on_load_cache[type] || [];
        on_load_cache[type].push(callback);
    };
    let on_do_cache = {};
    source.onDo = function (type, callback) {
        on_do_cache[type] = on_do_cache[type] || [];
        on_do_cache[type].push(callback);
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
    source.onLogin = function (data) {
        coos.success("登录成功.");
        try {
            if (window.localStorage) {
                if (coos.isTrue(data.rememberpassword)) {
                    localStorage.setItem("loginname", data.loginname);
                    localStorage.setItem("password", data.password);
                    localStorage.setItem("rememberpassword", "true");
                } else {
                    localStorage.removeItem("loginname");
                    localStorage.removeItem("password");
                    localStorage.removeItem("rememberpassword");
                }
            }
        } catch (e) { }
        window.setTimeout(() => {
            source.load('SESSION');
        }, 300);
    }
    source.onLogout = function () {
        coos.info("退出成功！");
        window.setTimeout(() => {
            source.load('SESSION');
        }, 300);
    }

    let checkServerStatus = function () {
        window.setTimeout(function () {
            $.ajax({
                url: _SERVER_URL + "/api/validate",
                data: {},
                type: "post",
                beforeSend: function () { },
                success: function (o) {
                    coos.success('系统启动成功，即将刷新页面！')
                    window.setTimeout(() => {
                        window.location.reload();
                    }, 800);
                },
                complete: function (XMLHttpRequest, textStatus) { },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    checkServerStatus();
                }
            });
        }, 5000);
    };
    let serverStatus = function (callback) {
        $.ajax({
            url: _SERVER_URL + "/api/validate",
            data: {},
            type: "post",
            beforeSend: function () { },
            success: function (o) {
                callback && callback(true);
            },
            complete: function (XMLHttpRequest, textStatus) { },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                callback && callback(false);
            }
        });
    };
    let waitServer = function (callback) {
        $socketbox.find('.socket-connect-text').text('服务器重启中，请稍后...');
        $("body").append($socketbox);
        serverStatus(function (res) {
            if (res) {
                $socketbox.find('.socket-connect-text').text('服务器启动成功！');
                window.setTimeout(function () {
                    callback && callback();
                }, 500);
            } else {
                window.setTimeout(function () {
                    waitServer(callback);
                }, 3000);
            }
        });
    };
    let ready = function () {
        source_ready_callbacks.forEach(ready_callback => {
            ready_callback && ready_callback();
        });
        source_ready_callbacks = [];
    };
    var websocket = null;

    let websocket_opened = false;
    let reconnectCount = 0;

    let $socketbox = $('<div class="socket-connect-box"/>');
    $socketbox.append('<div class="socket-connect" />');
    $socketbox.find('.socket-connect').append('<div class="socket-connect-img" ></div>');
    $socketbox.find('.socket-connect').append('<div class="socket-connect-text" >断线重连中，请稍后！</div>');


    let openWebsocket = function () {

        if (reconnectCount > 0) {
            $socketbox.find('.socket-connect-text').text('断线重连中，请稍后...');
            $("body").append($socketbox);
        }
        let url = window._SERVER_URL.replace('http', 'ws');
        url = url + '/websocket/' + source.token;
        if ('WebSocket' in window) {
            websocket = new WebSocket(url);
        } else {
            $('.app-loading').empty();
            $('.app-loading').append('<div class="color-orange ft-20">您的浏览器不支持WebSocket，请使用高版本浏览器！</div>');
            return;
        }
        websocket.onerror = function (error) {
            console.error(error);
        }
        websocket.onopen = function () {
            reconnectCount = 0;
            $socketbox.remove();
            websocket_opened = true;
            opened();
            source.load('SESSION');
        }
        websocket.onclose = function (e) {
            websocket_opened = false;
            serverStatus(function (res) {
                if (res) {
                    reconnectCount++;
                    openWebsocket();
                } else {
                    waitServer(function () {
                        reconnectCount++;
                        openWebsocket();
                    });
                }
            });
            // coos.error('WebSocket closed!');
        }
        let in_cache = {};
        websocket.onmessage = function (event) {
            let message = event.data;
            let index = message.indexOf(source.MESSAGE_HEADER_SUFFIX);
            let messageID = null;
            if (index > 0) {
                let str = message.substring(0, index + 1);
                let values = str.split(source.MESSAGE_HEADER_PART_SUFFIX);
                messageID = values[0];
                let msg = in_cache[messageID];
                if (msg == null) {
                    msg = "";
                }
                msg = msg + message.substring(index + 1);
                if ("end" == (values[1]).toLowerCase()) {
                    message = msg;
                    in_cache[messageID] = null;
                } else {
                    in_cache[messageID] = msg;
                    return;
                }
            }
            source.on(messageID, message, event);
        }
        let websocket_ready_callbacks = [];
        websocket.ready = function (callback) {
            if (websocket_opened) {
                callback && callback();
            } else {
                websocket_ready_callbacks.push(callback);
            }
        }
        websocket.sendText = function (messageID, message) {

            let length = message.length;
            let outs = [];
            if (length > source.MESSAGE_FLOW) {
                while (message.length > 0) {
                    length = message.length;
                    if (length > source.MESSAGE_FLOW) {
                        let tem = message.substring(0, source.MESSAGE_FLOW);
                        outs.push(tem);
                        message = message.substring(source.MESSAGE_FLOW);
                    } else {
                        outs.push(message);
                        message = "";
                    }
                }
            } else {
                outs.push(message);
            }
            for (let i = 0; i < outs.length; i++) {

                let msg = outs[i];
                let info = messageID + source.MESSAGE_HEADER_PART_SUFFIX;
                if (i == outs.length - 1) {
                    info += "end";
                } else if (i == 0) {
                    info += "start";
                } else {
                    info += "on";
                }
                info += source.MESSAGE_HEADER_PART_SUFFIX;

                message = info + source.MESSAGE_HEADER_SUFFIX + msg;

                websocket.send(message);
            }
        };
        let opened = function () {
            websocket_ready_callbacks.forEach(ready_callback => {
                ready_callback && ready_callback();
            });
            websocket_ready_callbacks = [];
        }

        window.onbeforeunload = function () {
            websocket.close(3000, "强制关闭");
        }
    }

    let init = function () {
        source_readying = true;
        tool.loadSession(function () {
            source_readying = false;

            openWebsocket();

            ready();

        });
    };


})();
export default source;