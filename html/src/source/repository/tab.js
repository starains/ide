source.repository.tabs = [];
source.repository.activeTab = null;

(function () {
    source.changeTab = function () {
        let tab = source.getTab(source.repository.activeTab);
        if (tab == null) {
            return;
        }
        if (tab.$editor == null) {
            tab.$editor = $('<div class="repository-tab-editor"/>');
            $(".repository-tab-editor-box")
                .append(tab.$editor);
        }
        $(".repository-tab-editor.show")
            .each(function (index, $editor) {
                if ($editor != tab.$editor[0]) {
                    $($editor).removeClass("show");
                }
            });
        tab.$editor.addClass("show");
        if (tab.editor == null) {
            source.createTabEditor(tab);
        } else {
            if (tab.isLoadChanged) {
                source.callLoadChanged(tab);
            }
        }
    };

    source.getModelTypeByPath = function (path) {
        let project = source.getProjectByPath(path);

        let model = null;
        let app = source.getProjectApp(project);
        if (app) {

            if (app.path_model_type) {
                Object.keys(app.path_model_type).forEach(model_path => {
                    let m = app.path_model_type[model_path];
                    if (m.isDirectory) {
                        if (model_path == path || path.startsWith(model_path + '/')) {
                            model = m;
                        }
                    } else {
                        if (model_path == path) {
                            model = m;
                        }
                    }
                });
            }

        }
        return model;
    }
    source.callLoadChanged = function (tab) {
        if (tab.isLoadChanged && tab.changed) {
            let file_date = tab.file_date;
            delete tab.isLoadChanged;
            delete tab.file_date;
            coos.confirm('文件[' + tab.path + ']内容发生更改，是否显示最新内容？').then(() => {
                tab.changed = false;
                tab.$editor.empty();
                delete tab.editor;
                source.buildFileEditor(file_date);
            }).catch(() => {
                tab.changed = tab.oldChanged;
            });
        }
    };
    source.buildFileEditor = function (file_date) {
        if (file_date == null) {
            return;
        }
        let path = file_date.path;
        let tab = source.getTab(file_date.path);

        if (tab == null) {
            return;
        }
        if (!coos.isEmpty(file_date.errmsg)) {

            tab.$editor.empty().append('<div class="text-center ft-20 color-red pdtb-60">' + file_date.errmsg + '</div>');
            return;
        }
        if (tab.editor != null) {
            if (file_date.content == tab.editor.file.content) {
                return;
            }
            if (file_date.content == tab.editor.getCode()) {
                tab.editor.file.content = file_date.content;
                tab.changed = false;
                return;
            }
            tab.editor.file.content = file_date.content;
            tab.oldChanged = tab.changed;
            tab.changed = true;
            tab.isLoadChanged = true;
            tab.file_date = file_date;
            if (source.repository.activeTab == tab.path) {
                source.callLoadChanged(tab);
            }
            return;
        }
        let project = source.getProjectByPath(file_date.path);

        let type = null;
        let appBean = source.getProjectApp(project);
        if (appBean) {
            let model = null;
            if (appBean.path_model_type) {
                Object.keys(appBean.path_model_type).forEach(path => {
                    let m = appBean.path_model_type[path];
                    if (m.isDirectory) {
                        if (file_date.path.startsWith(path + '/')) {
                            model = m;
                        }
                    } else {
                        if (file_date.path == (path)) {
                            model = m;
                        }
                    }
                });
            }
            if (model) {
                if (appBean.path_model_bean) {
                    model.bean = appBean.path_model_bean[file_date.path];
                }
                model.bean = model.bean || {};
                file_date.model = model;
                type = model.value;
            }
        }
        let editor = coos.editor({
            type: type,
            project: project,
            file: file_date,
            readyonly: !app.hasPermission(),
            onSave(content, callback) {
                source.saveFile(path, content, callback);
            },
            onTest(data, callback) {
                let app = source.getProjectApp(project);
                if (app && app.localpath && file_date.model) {
                    data = data || {};
                    data.type = type;
                    data.path = app.localpath;
                    data.name = file_date.model.bean.name;

                    source.plugin.app.event('doTest', data, project).then(result => {
                        callback && callback(result);
                    });
                }
            },
            toText(data, callback) {
                source.plugin.app.event('toText', data, project).then(result => {
                    callback && callback(result);
                });
            },
            toModel(data, callback) {
                source.plugin.app.event('toModel', data, project).then(result => {
                    callback && callback(result);
                });
            }, load(callback) {
                source.loadFile(path);
            }, onChange(changed) {
                tab.changed = changed;
            }
        });
        editor.build(tab.$editor);
        tab.editor = editor;

    };
    source.createTabEditor = function (tab) {
        if (tab.isFile) {
            let path = tab.path;

            tab.$editor.empty().append('<div class="text-center ft-20 color-grey pdtb-60">文件加载中，请稍后...</div>');
            source.loadFile(path);

        }
    };
    source.getTab = function (name) {
        let tab = null;
        source.repository.tabs.forEach((one, i) => {
            if (one.name == name) {
                tab = one;
            }
        });
        return tab;
    };
    source.getTabIndex = function (name) {
        let tab = source.getTab(name);

        if (tab != null) {
            return source.repository.tabs.indexOf(tab);
        }
        return -1;
    };
    source.closeOtherTab = function (name) {
        let index = source.getTabIndex(name);
        let tab = source.getTab(name);
        let tabs = [];
        source.repository.tabs.forEach((one, i) => {
            tabs.push(one);
        });
        tabs.forEach((one, i) => {
            if (one != tab) {
                source.removeTab(one);
            }
        });
        source.openTabByIndex(index);
    };
    source.closeRightTab = function (name) {
        let index = source.getTabIndex(name);
        let activeIndex = source.getTabIndex(source.repository.activeTab);
        let tabs = [];
        source.repository.tabs.forEach((one, i) => {
            tabs.push(one);
        });
        for (let i = index + 1; i < tabs.length; i++) {
            let one = tabs[i];
            if (one != null) {
                source.removeTab(one);
            }
        }
        if (activeIndex > index) {
            source.openTabByIndex(source.repository.tabs.length - 1);
        }
    };
    source.closeLeftTab = function (name) {
        let index = source.getTabIndex(name);
        let activeIndex = source.getTabIndex(source.repository.activeTab);
        let tabs = [];
        source.repository.tabs.forEach((one, i) => {
            tabs.push(one);
        });
        for (let i = 0; i < index; i++) {
            let one = tabs[i];
            if (one != null) {
                source.removeTab(one);
            }
        }
        if (activeIndex < index) {
            source.openTabByIndex(0);
        }
    };
    source.closeAllTab = function (name) {
        let tabs = [];
        source.repository.tabs.forEach((one, i) => {
            tabs.push(one);
        });
        for (let i = 0; i < tabs.length; i++) {
            let one = tabs[i];
            if (one != null) {
                source.removeTab(one);
            }
        }
        source.openTabByIndex(0);
    };
    source.closeTab = function (name) {
        let tab = source.getTab(name);
        if (tab != null) {
            let index = source.getTabIndex(source.repository.activeTab);
            source.removeTab(tab);
            source.openTabByIndex(index);

        }
    };
    source.removeTab = function (tab) {
        if (tab != null) {
            if (tab.editor) {
                tab.editor.destroy();
                delete tab.editor;
            }
            if (tab.$editor) {
                tab.$editor.remove();
                delete tab.$editor;
            }
            let index = source.repository.tabs.indexOf(tab);
            source.repository.tabs.splice(index, 1);
            if (tab.isFile) {
                source.closeFileByPath(tab.path);
            }
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
        if (source.repository.tabs.length == 0) {
            source.repository.activeTab = null;
        }
    };
    source.createTabByPath = function (path) {
        if (path == null) {
            return;
        }
        let tab = source.getTab(path);
        if (tab == null) {
            tab = {};
            let text = path;
            if (text.lastIndexOf('/') >= 0) {
                text = text.substring(text.lastIndexOf('/') + 1);
            }
            tab.name = path;
            tab.title = path;
            tab.path = path;
            tab.text = text;
            tab.isFile = true;
            tab.changed = false;
            source.repository.tabs.push(tab);
        }
        source.repository.activeTab = path;

    };

    source.reloadTab = function (path) {
        let tab = source.getTab(path);
        if (tab == null) {
            return;
        }
        if (tab.editor == null) {
            return;
        }
        if (tab.changed) {
            return;
        }
        source.loadFile(tab.path);
    };
})();

export default source;