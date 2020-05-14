let CodeEditor = function (options) {
    options = options || {};
    this.options = options;
    this.file = options.file;
    this.editor = options.editor;
    this.readyonly = options.readyonly;
    this.init();
};

CodeEditor.prototype.init = function () {
    this.text = '代码';
    this.key = 'CODE-EDITOR';
};


CodeEditor.prototype.getCode = function () {
    return this.lastText;
};
CodeEditor.prototype.setCode = function (code) {
    this.lastText = code;
    return this.codeMirror.setValue(code);
};

CodeEditor.prototype.setCacheData = function (code) {
    this.setCode(code);
};
CodeEditor.prototype.loadLastContent = function (callback) {
    callback && callback(this.getCode());
};
CodeEditor.prototype.callChange = function () {
    let code = this.getCode();
    let content = this.file.content;
    if (code == content) {
        this.editor.onChange(false, code, this);
    } else {
        this.editor.onChange(true, code, this);
    }
};

CodeEditor.prototype.show = function () {
    let that = this;
    this.editor.loadLastContent(function (content) {
        let code = that.getCode();
        if (code != content) {
            that.setCode(content);
        }
    });
};
CodeEditor.prototype.build = function ($box) {
    var that = this;
    let file = this.file;
    $box.empty();
    var $pre = $("<textarea></textarea>");
    $pre.val(file.content);
    $pre.css("width", "100%");
    $pre.css("height", "100%");
    $box.append($pre);
    let mode = "htmlmixed";
    if (file.type == "css") {
        mode = "css";
    } else if (file.type == "js") {
        mode = "javascript";
    } else if (file.type == "html") {
        mode = "htmlmixed";
    } else if (file.type == "xml") {
        mode = "xml";
    } else if (file.type == "java") {
        mode = "text/x-java";
    } else if (file.type == "properties") {
        mode = "properties";
    } else if (file.type == "md") {
        mode = "markdown";
    } else if (file.type == "vue") {
        mode = "vue";
    } else if (file.type == "gitignore") {
        mode = "markdown";
    } else if (file.type == "go") {
        mode = "text/x-go";
    } else if (file.type == "cmd") {
        mode = "powershell";
    } else if (file.type == "sh" || file.type == "shell") {
        mode = "shell";
    } else if (file.type == "python") {
        mode = "python";
    } else if (file.type == "php") {
        mode = "php";
    } else if (file.type == "json") {
        mode = "application/json";
    } else {
        if (this.editor.isVue) {
            mode = "vue";
        } else if (this.editor.isYaml) {
            mode = "yaml";
        } else {
            mode = "htmlmixed";
        }
    }

    this.codeMirror = CodeMirror.fromTextArea($pre[0], {
        mode: mode, // 语言模式
        theme: "lesser-dark", // 主题
        keyMap: "sublime", // 快键键风格
        lineNumbers: true, // 显示行号
        smartIndent: true, //智能缩进
        indentUnit: 4, // 智能缩进单位为4个空格长度
        indentWithTabs: true, // 使用制表符进行智能缩进
        styleActiveLine: true, // 当前行背景高亮
        lineWrapping: false, // 在行槽中添加行号显示器、折叠器、语法检测器`
        gutters: [
            "CodeMirror-linenumbers",
            "CodeMirror-foldgutter",
            "CodeMirror-lint-markers"
        ],
        foldGutter: true, // 启用行槽中的代码折叠
        autofocus: true, //自动聚焦
        matchBrackets: true, // 匹配结束符号，比如"]、}"
        autoCloseBrackets: true, // 自动闭合符号
        styleActiveLine: true, // 显示选中行的样式
        readOnly: this.readyonly,
        extraKeys: {
            "Alt-/": "autocomplete",
            "Alt-Q": function () {
                that.codeMirror.showHint();
            },
            'Ctrl-S': function () {
                that.editor.save(that.getCode(), function (flag) {
                    if (flag) {
                        that.callChange();
                    }
                });
            }
        },
        hintOptions: {
            hint(editor, options) {
                let hint = CodeMirror.hint.anyword(editor, options);
                return hint;
            }
        }
    });
    // 设置初始文本，这个选项也可以在fromTextArea中配置`
    this.codeMirror.on("change", function () {
        that.lastText = that.codeMirror.getValue();
        that.callChange();

    });
    this.lastText = file.content;
};

export default CodeEditor;