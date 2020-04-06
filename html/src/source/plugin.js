
(function () {
    source.plugin = new Object();

    source.plugins = [];

    source.plugin.init = function (plugin) {
        let obj = new PluginObj(plugin);
        source.plugin[obj.namespace] = obj;

        let old = null;
        source.plugins.forEach(one => {
            if (one.name == plugin.name) {
                old = one;
            }
        });
        if (old == null) {
            source.plugins.push(obj);
        } else {
            source.plugins.splice(source.plugins.indexOf(old), 1, obj);
        }
    };
    source.plugin.onCreateEditory = function (options) {
        source.plugins.forEach(one => {
            if (one.customPlugin && one.customPlugin.onCreateEditory) {
                try {
                    one.customPlugin.onCreateEditory(options);
                } catch (e) {
                    console.error(e);
                }
            }
        });
    };
    source.plugin.onContextmenu = function (data, menus) {
        source.plugins.forEach(one => {
            if (one.customPlugin && one.customPlugin.onContextmenu) {
                try {
                    one.customPlugin.onContextmenu(data, menus);
                } catch (e) {
                    console.error(e);
                }
            }
        });
    };

    let PluginObj = function (plugin) {
        this.plugin = plugin;
        this.namespace = plugin.name;
        this.name = plugin.name;
        this.text = plugin.text;
        this.version = plugin.version;
        if (!coos.isEmpty(plugin.scriptClassName)) {
            try {
                let options = {};
                this.customPlugin = eval('(new ' + plugin.scriptClassName + '(options))')
            } catch (e) {
                console.error(e);
            }
        }
    }

    PluginObj.prototype.event = function (type, data, project) {
        return source.server.event(this.name, this.version, type, data, project);
    };

})();
export default source;