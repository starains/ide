(function() {
	var Editor = coos.Editor;


	Editor.prototype.isHtml = function() {
		return false;
	};
	Editor.prototype.isYaml = function() {
		return false;
	};


	Editor.prototype.getCode = function() {
		return this.codeMirror.getValue();
	};
	Editor.prototype.setCode = function(code) {
		return this.codeMirror.setValue(code);
	};
	Editor.prototype.buildCode = function() {
		if (this.code_builded) {
			return;
		}
		this.code_builded = true;
		var that = this;
		var $code = this.$code;
		var tab = this.tab;
		var file = this.file;
		$code.empty();
		var $pre = $("<textarea ></textarea>");
		$pre.css("width", "100%");
		$pre.css("height", "100%");
		$code.append($pre);
		let mode = 'htmlmixed';
		if (file.type == "css") {
			mode = 'css';
		} else if (file.type == "js") {
			mode = 'javascript';
		} else if (file.type == "html") {
			mode = 'htmlmixed';
		} else if (file.type == "xml") {
			mode = 'xml';
		} else if (file.type == "java") {
			mode = 'text/x-java';
		} else if (file.type == "properties") {
			mode = 'properties';
		} else if (file.type == "md") {
			mode = 'markdown';
		} else if (file.type == "vue") {
			mode = 'vue';
		} else if (file.type == "gitignore") {
			mode = 'markdown';
		} else if (file.type == "go") {
			mode = 'text/x-go';
		} else if (file.type == "cmd") {
			mode = 'powershell';
		} else if (file.type == "sh" || file.type == "shell") {
			mode = 'shell';
		} else if (file.type == "python") {
			mode = 'python';
		} else if (file.type == "php") {
			mode = 'php';
		} else {
			if (that.isYaml()) {
				mode = 'yaml';
			} else if (that.isHtml()) {
				mode = 'htmlmixed';
			} else {
				mode = 'htmlmixed';
			}
		}

		var myCodeMirror = CodeMirror.fromTextArea($pre[0], {
			mode : mode, // 语言模式
			theme : "lesser-dark", // 主题
			keyMap : "sublime", // 快键键风格
			lineNumbers : true, // 显示行号
			smartIndent : true, //智能缩进
			indentUnit : 4, // 智能缩进单位为4个空格长度
			indentWithTabs : true, // 使用制表符进行智能缩进
			lineWrapping : true, //
			// 在行槽中添加行号显示器、折叠器、语法检测器`
			gutters : [ "CodeMirror-linenumbers", "CodeMirror-foldgutter", "CodeMirror-lint-markers" ],
			foldGutter : true, // 启用行槽中的代码折叠
			autofocus : true, //自动聚焦
			matchBrackets : true, // 匹配结束符号，比如"]、}"
			autoCloseBrackets : true, // 自动闭合符号
			styleActiveLine : true, // 显示选中行的样式
			readOnly : coos.isTrue(that.readyonly),
			extraKeys : {
				"Alt-/" : "autocomplete",
				"Alt-Q" : function() {
					myCodeMirror.showHint();
				}
			},
			hintOptions : {
				hint (editor, options) {
					let hint = CodeMirror.hint.anyword(editor, options);

					return hint;
				}
			}
		}); // 设置初始文本，这个选项也可以在fromTextArea中配置`
		this.codeMirror = myCodeMirror;
		this.codeMirror.setValue(file.content);
		myCodeMirror.on("change", function() {
			//			myCodeMirror.showHint();
			that.changeCode(that.getCode());
		});
	};
})();