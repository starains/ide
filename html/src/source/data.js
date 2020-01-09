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
                    one && one(res.value);
                })
            });
        });
    };
    source.do = function (type, data) {
        return source.server.do(type, data);
    };
    source.load = function (type, data) {
        data = data || {};
        let model = source.getModel(type);

        if (model && model.type == 'PAGE') {
            if (coos.isEmpty(data.pageindex)) {
                if (source.data[type]) {
                    data.pageindex = Number(source.data[type].pageindex) + 1;
                }
            }
            if (coos.isEmpty(data.pagesize)) {
                if (source.data[type]) {
                    data.pagesize = Number(source.data[type].pagesize);
                }
            }
        }
        return source.server.load(type, data);
    };


    source.onData = function (type, status) {
        status = status || {};
        let value = status.value;
        if (type == 'SESSION') {
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
        } else if (type == 'CONFIGURE') {
            value = value || {};
            Object.assign(source.CONFIGURE, value);
            return;
        } else if (type == 'UPGRADE_DOWNLOAD') {
            value = value || {};
            Object.assign(source.UPGRADE_DOWNLOAD, value);
            return;
        } else if (type == 'UPGRADE_STATUS') {
            source.UPGRADE_STATUS = value;
            return;
        } else if (type == 'SPACE') {
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
        } else if (type == 'REPOSITORY') {
            source.onLoadRepository(value);
            return;
        } else if (type == 'PROJECT') {
            source.onLoadProject(value);
            return;
        } else if (type == 'APP') {
            source.onApp(value);
            return;
        } else if (type == 'FILE') {
            source.onLoadFile(value);
            return;
        } else if (type == 'FILES') {
            source.onLoadFiles(value);
            return;
        } else if (type == 'GIT') {
            source.onLoadGit(value);
            return;
        } else if (type == 'STARTER_OPTIONS') {
            source.onStarterOptions(value);
            return;
        } else if (type == 'STARTERS') {
            source.onStarters(value);
            return;
        } else if (type == 'STARTER_LOG') {
            source.onStarterLog(value);
            return;
        } else if (type == 'STARTER_STATUS') {
            source.onStarterStatus(value);
            return;
        } else if (type == 'RUNNER_OPTIONS') {
            source.onRunnerOptions(value);
            return;
        } else if (type == 'PARENTS') {
            value = value || [];
            value.push(source.space);
            value.forEach((one, index) => {
                if ((index + 1) < value.length) {
                    value[index + 1].levels = one.childs;
                }
            });

        } else if (type == 'REPOSITORY_STATUS') {
            source.onRepositoryStatus(value);
        } else if (type == 'SPACE_TEAMS') {
            if (status.value) {
                status.value.forEach(one => {
                    one.data = null;
                    if (one.type == "USERS") {
                        source.get("USER", one.recordid).then(res => {
                            one.data = { name: res.name };
                        });
                    }
                });
            }
        } else if (type == 'SPACE_EVENTS') {
            if (status.value) {
                status.value.forEach(one => {
                    one.user = null;
                    one.datetime = coos.formatDate(one.createtime);
                    one.date = one.datetime.split(" ")[0];
                    let data = {};
                    if (!coos.isEmpty(one.data)) {
                        data = JSON.parse(one.data);
                    }
                    one.data = data;
                    one.user = null;

                    source.get("USER", one.createuserid).then(res => {
                        if (res) {
                            one.user = { name: res.name };
                        }
                    });
                });
            }
        }
        let model = source.getModel(type);
        if (model == null) {
            return;
        }
        if (model.type == 'ONE') {
            if (source.data[type] == null) {
                source.data[type] = value;
            } else {
                if (coos.isObject(value)) {
                    Object.assign(source.data[type], value);
                } else {
                    source.data[type] = value;
                }
            }
        } else if (model.type == 'LIST') {
            if (source.data[type] == null) {
                source.data[type] = [];
            }
            source.data[type].splice(0, source.data[type].length);
            value = value || [];
            value.forEach(one => {
                source.data[type].push(one);
            });
        } else if (model.type == 'PAGE') {
            if (source.data[type] == null) {
                source.data[type] = {
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
            source.data[type].pageindex = status.pageindex;
            source.data[type].totalpages = status.totalpages;
            source.data[type].totalcount = status.totalcount;
            source.data[type].pagesize = status.pagesize;
            source.data[type].uppage = status.uppage;
            source.data[type].nextpage = status.nextpage;
            source.data[type].hasNext = status.pageindex < status.totalpages;
            source.data[type].value.splice(0, source.data[type].value.length);
            if (status.pageindex == 1) {
                source.data[type].list.splice(0, source.data[type].list.length);
            }

            status.value = status.value || [];
            status.value.forEach(one => {
                source.data[type].value.push(one);
                source.data[type].list.push(one);
            });
        }

    };

})();

export default source;