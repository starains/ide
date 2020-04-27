

let resources_map = {};

let css = [];
let js = [];

js = [];
css = [];

css.push(_SERVER_URL + "resources/plugins/jquery/jquery-ui.css");
js.push(_SERVER_URL + "resources/plugins/jquery/jquery-ui.js");

js.push(_SERVER_URL + "resources/plugins/jsplumb/jsplumb.min.js");


css.push(_SERVER_URL + "resources/plugins/codemirror/lib/codemirror.css");
css.push(_SERVER_URL + "resources/plugins/codemirror/theme/lesser-dark.css");

css.push(_SERVER_URL + "resources/plugins/codemirror/addon/hint/show-hint.css");

js.push(_SERVER_URL + "resources/plugins/codemirror/lib/codemirror.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/keymap/sublime.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/addon/hint/show-hint.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/addon/hint/anyword-hint.js");


js.push(_SERVER_URL + "resources/plugins/codemirror/addon/hint/css-hint.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/addon/hint/html-hint.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/addon/hint/javascript-hint.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/addon/hint/sql-hint.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/addon/hint/xml-hint.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/addon/comment/comment.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/addon/mode/overlay.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/addon/mode/simple.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/addon/selection/selection-pointer.js");
// mode
js.push(_SERVER_URL + "resources/plugins/codemirror/mode/css/css.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/mode/javascript/javascript.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/mode/htmlmixed/htmlmixed.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/mode/xml/xml.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/mode/clike/clike.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/mode/properties/properties.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/mode/markdown/markdown.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/mode/yaml/yaml.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/mode/vue/vue.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/mode/sql/sql.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/mode/shell/shell.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/mode/sass/sass.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/mode/python/python.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/mode/powershell/powershell.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/mode/php/php.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/mode/nginx/nginx.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/mode/http/http.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/mode/nginx/nginx.js");
js.push(_SERVER_URL + "resources/plugins/codemirror/mode/go/go.js");

resources_map.editor = { js, css };

js = [];
css = [];

css.push(_SERVER_URL + "resources/plugins/dropzone/basic.min.css");
css.push(_SERVER_URL + "resources/plugins/dropzone/dropzone.min.css");
js.push(_SERVER_URL + "resources/plugins/dropzone/dropzone.min.js");
js.push(_SERVER_URL + "resources/plugins/dropzone/dropzone-amd-module.min.js");

resources_map.dropzone = { js, css };



source.loadEditorRely = function (callback) {
    source.loadResourcesByName('editor', callback);
};

source.loadDropzoneRely = function (callback) {
    source.loadResourcesByName('dropzone', callback);
};
source.loadResources = function (resources, callback) {
    resources = resources || {};
    let js = resources.js || [];
    let css = resources.css || [];
    loadCSS(css);
    loadJS(js, callback);
};
source.loadResourcesByName = function (name, callback) {
    let resources = resources_map[name];
    if (resources == null || resources.loaded) {
        callback && callback();
        return;
    }
    if (resources.loading) {
        resources.callbacks.push(callback);
        return;
    }
    resources.callbacks = [];
    resources.callbacks.push(callback);
    resources.loading = true;

    source.loadResources(resources, (res => {
        resources.loaded = true;
        resources.loading = false;

        resources.callbacks.forEach(callback => {
            callback && callback();
        });
        resources.callbacks = [];
    }));

};

let loadCSS = function (css, callback) {
    var csss = [css];
    if (coos.isArray(css)) {
        csss = css;
    }
    var head = document.getElementsByTagName("head")[0];
    // 加载csss数组
    csss.forEach(css => {
        // 如果此css已加载，创建下个css
        if (url_loaded[css]) {
            return;
        }
        url_loaded[css] = true;
        var link = document.createElement("link");
        head.appendChild(link);
        link.type = "text/css";
        link.rel = "styleSheet";
        link.href = css;

    });
};
let loadJS = function (js, callback) {
    var jss = [js];
    if (coos.isArray(js)) {
        jss = js;
    }
    loadJSByIndex(jss, 0, callback);
};

function loadJSByIndex(jss, index, callback) {
    if (index >= (jss.length)) {
        if (callback) {
            callback();
        }
        return;
    }
    var js = jss[index];
    doLoadJS(js, function () {
        loadJSByIndex(jss, index + 1, callback);
    });
}

let url_callbacks = {};
let url_loaded = {};
let doLoadJS = function (url, callback) {
    if (url_loaded[url]) {
        callback && callback();
        return;
    }
    if (url_callbacks[url]) {
        url_callbacks[url].push(callback);
        return;
    }
    url_callbacks[url] = [callback];
    var head = document.getElementsByTagName("head")[0], script = document.createElement("script");
    head.appendChild(script);
    script.src = url;
    script.charset = "utf-8";
    script.onload = script.onreadystatechange = function () {
        if (!this.readyState || this.readyState === "loaded") {
            script.onload = script.onreadystatechange = null;
            if ($('[src="' + url + '"]').length > 1) {
                head.removeChild(script);
            }
            url_loaded[url] = true;
            let callbacks = url_callbacks[url];
            delete url_callbacks[url];
            callbacks.forEach(callback => {
                callback && callback();
            });
        }
    };
}