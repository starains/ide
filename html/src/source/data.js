(function () {
    source.getModel = function (type) {
        let model = null;
        source.ENUM_MAP.MODEL_TYPE.forEach(one => {
            if (one.value == type) {
                model = one;
            }
        });
        return model;
    };
    source.get = function (type, id) {
        return new Promise((resolve, reject) => {
            if (coos.isEmpty(type)) {
                resolve && resolve(null);
                return;
            }
            source.cache[type] = source.cache[type] || {};
            let map = source.cache[type];
            if (map[id] != null) {
                resolve && resolve(map[id]);
            } else {
                loadOne(type, id).then(res => {
                    map[id] = Object.assign({}, res);
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


    source.onData = function (data) {
        data = data || {};
        let value = data.value;
        if (data.model == 'SESSION') {
            value = value || {};
            source.LOGIN_USER = value.LOGIN_USER;
            source.isLogin = source.LOGIN_USER != null;
            source.isManager = value.isManager;
            source.preference = value.preference || {};
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
            source.initPreferenceStyle();
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
                source.loadRepository();
                source.load('BRANCHS');
            }
            return;
        } else if (data.model == 'REPOSITORY') {
            source.onLoadRepository(value);
            return;
        } else if (data.model == 'PROJECT') {
            source.onLoadProject(value);
            return;
        } else if (data.model == 'APP') {
            source.onApp(value);
            return;
        } else if (data.model == 'FILE') {
            source.onLoadFile(value);
            return;
        } else if (data.model == 'FILES') {
            source.onLoadFiles(value);
            return;
        } else if (data.model == 'GIT') {
            source.onLoadGit(value);
            return;
        } else if (data.model == 'STARTER_OPTIONS') {
            source.onStarterOptions(value);
            return;
        } else if (data.model == 'STARTERS') {
            source.onStarters(value);
            return;
        } else if (data.model == 'STARTER_LOG') {
            source.onStarterLog(value);
            return;
        } else if (data.model == 'STARTER_STATUS') {
            source.onStarterStatus(value);
            return;
        } else if (data.model == 'RUNNER_OPTIONS') {
            source.onRunnerOptions(value);
            return;
        } else if (data.model == 'PARENTS') {
            value = value || [];
            value.push(source.space);
            value.forEach((one, index) => {
                if ((index + 1) < value.length) {
                    value[index + 1].levels = one.childs;
                }
            });

        } else if (data.model == 'REPOSITORY_STATUS') {
            source.onRepositoryStatus(value);
        } else if (data.model == 'SPACE_TEAMS') {
            if (value.value) {
                value.value.forEach(one => {
                    one.data = null;
                    if (one.type == "USERS") {
                        source.get("USER", one.recordid).then(res => {
                            one.data = { name: res.name };
                        });
                    }
                });
            }
        } else if (data.model == 'SPACE_EVENTS') {
            if (value.value) {
                value.value.forEach(one => {

                });
            }
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
            source.data[data.model].hasNext = value.pageindex < value.totalpages;
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

})();

export default source;