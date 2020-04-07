source.repository.tabs = [];
source.repository.activeTab = null;

(function () {
    source.changeTab = function () {
        let tab = source.getTab(source.repository.activeTab);
        if (tab == null) {
            return;
        }
        source.repository.tabs.forEach(one => {
            if (one != tab) {
                one.show_editor = false;
            }
        });
        tab.show_editor = true;
    };
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
    source.createTabByPath = function (path, openTab) {
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
            tab.show_editor = false;
            tab.loading = false;
            source.repository.tabs.push(tab);
        }
        if (openTab) {
            source.repository.activeTab = path;
        }

    };

    source.reloadTab = function (path) {
        let tab = source.getTab(path);
        if (tab == null) {
            return;
        }
        if (tab.editor == null) {
            return;
        }
        tab.editor.callReload();
    };
})();

export default source;