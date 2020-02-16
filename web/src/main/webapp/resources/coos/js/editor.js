(function(window) {
	'use strict';
var app = window.app || new Object();
window.app = app;
(function() {
	var Editor = function(options) {
		options = options || {};
		this.initOptions(options);
		this.init();
	};

	Editor.prototype.initOptions = function(options) {
		options = options || {};
		this.options = options;
		this.readyonly = options.readyonly;
		this.type = options.type;
		this.model_original = options.model || {};
		this.context = options.context || {};
		this.file = options.file;
		this.ENUM_MAP = source.ENUM_MAP || {};
		if (this.file && this.file.model && this.file.model.bean) {
			this.model = $.extend(true, {}, this.file.model.bean);
		} else {
			this.model = $.extend(true, {});
		}
		this.historys = [];
		this.data = {
			view : '',
			file : this.file
		};
	};

	Editor.prototype.init = function() {
		this.initView();
	};


	Editor.prototype.initView = function() {
		var $editorBox = $('<div class="editor-box" />');
		this.$editorBox = $editorBox;

		var $header = $('<div class="editor-header" />');
		var $body = $('<div class="editor-body" />');
		var $footer = $('<div class="editor-footer" />');
		var $code = $('<div class="editor-code" />');
		var $design = $('<div class="editor-design coos-scrollbar" />');
		var $help = $('<div class="editor-help" />');


		$editorBox.append($header);
		$editorBox.append($body);
		$editorBox.append($footer);

		$body.append($code);
		$body.append($design);
		$body.append($help);


		$header.append(this.getHeaderHtml());
		$body.append(this.getBodyHtml());
		$footer.append(this.getFooterHtml());
		$help.append(this.getHelpHtml());
	};

	Editor.prototype.viewCode = function() {
		if (this.data.view == 'code') {
			return;
		}
		this.data.view = 'code';
		this.$code.show();
		this.$design.hide();
		this.buildCode();


		if (this.lastModel != null) {
			let that = this;
			this.getCodeByModel(this.lastModel, function(res) {
				that.lastModel = null;
				that.lastText = res;
				that.setCode(res);
			});
		}

	};
	Editor.prototype.getCodeByModel = function(model, callback) {
		let that = this;
		var data = {
			model : model,
			type : this.type,
			filename : this.file.name
		}
		this.options.toText(data, function(res) {
			callback && callback(res);
		});
	};

	Editor.prototype.viewDesign = function() {
		if (this.data.view == 'design') {
			return;
		}
		var that = this;
		function view() {
			that.data.view = 'design';
			that.$design.show();
			that.$code.hide();
			that.buildDesignView();
		}
		if (this.type) {
			var data = {
				text : this.getCode(),
				type : this.type,
				filename : this.file.name
			}
			if (that.lastText && that.lastText == data.text) {
				view();
			} else {
				var changed = false;
				if (that.lastText && that.lastText != data.text) {
					changed = true;
				}
				that.lastText = data.text;
				that.options.toModel(data, function(res) {
					if (res.errcode == 0) {
						if (changed) {
							that.recordHistory();
						}
						var model = res.value;
						model.name = that.model.name;
						that.model = model;
					} else {
						coos.box.error(res.errmsg);
					}
					view();
				});
			}
		} else {
			view();
		}

	};

	Editor.prototype.build = function($box) {
		var that = this;
		$box.empty();
		$box.append(this.$editorBox);

		this.vue = new Vue({
			data : this.data,
			el : this.$editorBox[0],
			methods : {
				viewCode : function() {
					that.viewCode();
				},
				viewDesign : function() {
					that.viewDesign();
				},
			}
		});
		this.$el = $(this.vue.$el);

		this.$el.data('editor', this);


		this.$header = this.$el.find('.editor-header');
		this.$body = this.$el.find('.editor-body');
		this.$footer = this.$el.find('.editor-footer');
		this.$code = this.$el.find('.editor-code');
		this.$design = this.$el.find('.editor-design');
		this.$help = this.$el.find('.editor-help');
		this.$design.on('scroll', function() {
			that.designScrollTop = that.$design.scrollTop();
		});

		this.bindEvent();
		this.viewCode();

	};

	Editor.prototype.buildDesignView = function(callBuild) {
		this.buildDesign();
		if (this.designScrollTop >= 0) {
			this.$design.scrollTop(this.designScrollTop);
		}
	};

	Editor.prototype.changeModel = function(callBuild, code) {
		var that = this;
		if (coos.isEmpty(callBuild) || coos.isTrue(callBuild)) {
			this.buildDesignView();
		}
		coos.trimObject(this.model, [ undefined, null, "" ], true);
		var data = {
			model : this.model,
			type : this.type,
			filename : this.file.name
		}
		this.lastModel = $.extend(true, {}, this.model);
		this.options.onChange && this.options.onChange(true);
	};


	Editor.prototype.changeCode = function(content) {
		if (coos.isEmpty(content) && coos.isEmpty(this.file.content)) {
			this.options.onChange && this.options.onChange(false);
		} else if (content == this.file.content) {
			this.options.onChange && this.options.onChange(false);
		} else {
			this.options.onChange && this.options.onChange(true);
		}
	};

	Editor.prototype.destroy = function() {
		this.codeMirror.doc.remove();
		$(this.$el).remove();
	};


	coos.editor = function(options) {
		options = options || {};
		options.type = options.type || '';
		var editor = null;
		switch (options.type.toUpperCase()) {
		case "APP":
			editor = new coos.Editor.App(options);
			break;
		case "JDBC":
			editor = new coos.Editor.Database(options);
			break;
		case "DATABASE":
			editor = new coos.Editor.Database(options);
			break;
		case "TABLE":
			editor = new coos.Editor.Table(options);
			break;
		case "DAO":
			editor = new coos.Editor.Dao(options);
			break;
		case "SERVICE":
			editor = new coos.Editor.Service(options);
			break;
		case "CACHE":
			editor = new coos.Editor.Cache(options);
			break;
		case "DICTIONARY":
			editor = new coos.Editor.Dictionary(options);
			break;
		case "CODE":
			editor = new coos.Editor.Code(options);
			break;
		case "THEME":
			editor = new coos.Editor.Theme(options);
			break;
		case "PAGE":
			editor = new coos.Editor.Page(options);
			break;
		case "PAGECOMPONENT":
			editor = new coos.Editor.PageComponent(options);
			break;
		case "RESOURCE":
			editor = new coos.Editor.Resource(options);
			break;
		case "PLUGIN":
			editor = new coos.Editor.Plugin(options);
			break;
		default:
			editor = new coos.Editor(options);
			break;
		}
		return editor;
	};
	coos.Editor = Editor;
})();
(function() {
	var Editor = coos.Editor;

	Editor.prototype.onChange = function() {};

	Editor.prototype.toPreviousStep = function() {
		if (this.historys.length == 0) {
			return;
		}
		if (coos.isEmpty(this.stepindex)) {
			this.recordHistory();
			this.stepindex = this.historys.length - 1;
		}
		if (this.stepindex <= 0) {
			return;
		}
		this.stepindex--;
		this.model = this.historys[this.stepindex];
		this.changeModel();
		if (this.stepindex > 0) {
			this.previous_step_nav.disabled = false;
		} else {
			this.previous_step_nav.disabled = true;
		}
		this.next_step_nav.disabled = false;
	};

	Editor.prototype.toNextStep = function() {
		if (this.historys.length == 0) {
			return;
		}
		if (coos.isEmpty(this.stepindex)) {
			return;
		}
		if (this.stepindex >= this.historys.length - 1) {
			return;
		}
		this.stepindex++;
		this.model = this.historys[this.stepindex];
		this.changeModel();
		if (this.stepindex >= this.historys.length - 1) {
			this.next_step_nav.disabled = true;
		} else {
			this.next_step_nav.disabled = false;
		}
		this.previous_step_nav.disabled = false;
	};

	Editor.prototype.toReset = function() {};

	Editor.prototype.toReload = function() {};

	Editor.prototype.toExport = function() {};

	Editor.prototype.toImport = function() {};

	Editor.prototype.toSave = function() {

		let that = this;
		if (this.options.onSave) {
			if (this.lastModel != null) {
				this.getCodeByModel(this.lastModel, function(res) {
					that.setCode(res);
					that.options.onSave(that.getCode());
				});
			} else {
				this.options.onSave(this.getCode());
			}
		}
	};

	Editor.prototype.toSetting = function() {};

	Editor.prototype.getTableByName = function() {};

	Editor.prototype.hasTest = function() {
		return false;
	};

	Editor.prototype.toTest = function() {};

	Editor.prototype.hasHelp = function() {
		return true;
	};

	Editor.prototype.getHelpHtml = function() {

		return Editor.help_html;
	};

	Editor.prototype.toHelp = function() {
		if (this.$help.hasClass('open-help')) {
			this.$help.removeClass('open-help');
		} else {
			this.$help.addClass('open-help');
		}

	};

	Editor.prototype.hasSetting = function() {
		return false;
	};

	Editor.prototype.getBeans = function(type) {
		var list = [];
		var context = this.context;
		if (context) {
			list = context[type] || [];
		}
		return list;
	};
	Editor.prototype.getOptions = function(type) {
		var list = this.getBeans(type);
		let options = this.appendOptions([], list)

		return options;
	};
	Editor.prototype.getTableByName = function(table) {
		var list = this.getBeans('TABLE');
		let res = [];
		list.forEach((one) => {
			if (one.name == table) {
				res = one;
			}
		});

		return res;
	};
	Editor.prototype.appendOptions = function(options, list) {
		options = options || [];
		list = list || [];
		$(list).each(function(index, one) {
			var text = one.name;
			if (!coos.isEmpty(one.comment)) {
				text += '（' + one.comment + '）';
			}
			options.push({
				text : text,
				value : one.name
			})
		});

		return options;
	};
	Editor.prototype.bindLiEvent = function($li, data, callBuild) {
		var that = this;
		if (data) {
			$li.find('.input').attr('autocomplete', 'off');
			$li.find('.input').each(function(index, input) {
				initWidth(input);
			});
			function initWidth(input) {
				if ($(input)[0].tagName != 'SELECT' && $(input)[0].tagName != 'TEXTAREA') {
					var length = ($(input).val() || '').length;
					var $span = $('<span />');
					$span.text($(input).val() || '');
					$span.css('font-size', '13px');
					$span.css('padding-left', '5px');
					$span.css('padding-right', '5px');
					$('body').append($span);
					var width = $span.outerWidth();
					$(input).css('width', width);
					$span.remove();
				}
			}
			$li.find('.input').on('input', function() {
				initWidth(this);
			});
			$li.find('.input').on('change', function() {
				var name = $(this).attr('name');
				var pattern = $(this).attr('pattern');
				var value = $(this).val() || '';
				if (coos.isEmpty(value)) {
					if (coos.isRequired(this)) {
						$(this).addClass('error');
						return;
					}
				}
				if (!coos.isEmpty(pattern) && !new RegExp(pattern).test(value)) {
					$(this).addClass('error');
					return;
				}
				$(this).removeClass('error');
				if (!coos.isEmpty(name)) {
					that.recordHistory();
					data[name] = value;
					that.changeModel(callBuild);
				}
			});
		}
	};
})();
(function() {
	var Editor = coos.Editor;
	Editor.prototype.initHeaderNav = function() {
		var that = this;
		var navs = [];
		this.data.navs = navs;

		this.save_nav = {
			fonticon : 'save',
			disabled : this.options.onSave == null,
			text : "保存",
			onClick : function() {
				that.toSave();
			}
		};
		navs.push(this.save_nav);


		this.saveas_nav = {
			fonticon : 'file-copy',
			disabled : this.options.onSaveas == null,
			text : "另存为",
			onClick : function() {
				that.toSaveas();
			}
		};
		navs.push(this.saveas_nav);

		navs.push({
			line : true
		});

		this.reset_nav = {
			fonticon : 'undo',
			disabled : false,
			text : "还原",
			onClick : function() {
				that.toReset();
			}
		};
		navs.push(this.reset_nav);

		this.reload_nav = {
			fonticon : 'reload',
			disabled : false,
			text : "刷新",
			onClick : function() {
				that.toReload();
			}
		};
		navs.push(this.reload_nav);


		navs.push({
			line : true
		});

		this.previous_step_nav = {
			fonticon : 'left',
			disabled : true,
			text : "上一步",
			onClick : function() {
				that.toPreviousStep();
			}
		};
		navs.push(this.previous_step_nav);

		this.next_step_nav = {
			fonticon : 'right',
			disabled : true,
			text : "下一步",
			onClick : function() {
				that.toNextStep();
			}
		};
		navs.push(this.next_step_nav);

		navs.push({
			line : true
		});

		this.setting_nav = {
			fonticon : 'setting',
			disabled : !this.hasSetting(),
			text : "设置",
			onClick : function() {
				that.toSetting();
			}
		};
		navs.push(this.setting_nav);

		navs.push({
			line : true
		});

		this.test_nav = {
			fonticon : 'sliders',
			disabled : !this.hasTest(),
			text : "测试",
			onClick : function() {
				that.toTest();
			}
		};
		navs.push(this.test_nav);



		this.help_nav = {
			fonticon : 'info-circle',
			disabled : !this.hasHelp(),
			text : "帮助",
			onClick : function() {
				that.toHelp();
			}
		};
		navs.push(this.help_nav);
	};
	Editor.prototype.getHeaderHtml = function() {

		this.initHeaderNav();

		return html;

	};

	var html = '';
	html += '<div class="coos-nav coos-nav-full bg-blue-grey coos-nav-full coos-nav-vertical coos-nav-xs">';
	html += '<template v-for="nav in navs">';
	html += '<li v-if="nav.line">';
	html += '</li>';
	html += '<li v-else v-bind:title="nav.text" v-on:click="nav.onClick()">';
	html += '<a class="active-color-white active-bg-blue-grey-8" :class="{\'coos-disabled\' : nav.disabled}">';
	html += '<i v-show="!coos.isEmpty(nav.fonticon)" v-bind:class="\'coos-icon coos-icon-\'+ nav.fonticon"></i>';
	html += '{{nav.text}}';
	html += '</a>';
	html += '</li>';
	html += '</template>';
	html += '</div>';
})();
(function() {
	var Editor = coos.Editor;

	Editor.prototype.getBodyHtml = function() {
		return '<div/>';
	};

})();
(function() {
	var Editor = coos.Editor;
	Editor.prototype.getFooterHtml = function() {

		return html;
	};

	var html = '';
	html += '<div class="bg-blue-grey ft-12 " style="line-height: 25px;">';
	html += '<a class="pdlr-5 coos-btn bg-blue-grey" style="display: inline-block;" :class="{\'coos-active\' : view==\'code\'}" @click="viewCode">';
	html += '代码';
	html += '</a>';
	html += '<a class="pdlr-5 coos-btn bg-blue-grey" style="display: inline-block;" :class="{\'coos-active\' : view==\'design\'}" @click="viewDesign">';
	html += '设计';
	html += '</a>';
	html += '</div>';
})();
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
			styleActiveLine : true, // 当前行背景高亮
			lineWrapping : false, //
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
(function() {
	var Editor = coos.Editor;

	Editor.prototype.bindPropertyEvent = function($li, model) {
		model = model || {};
		var that = this;
		$li.find('.updatePropertyBtn').click(function() {
			var type = $(this).attr('property-type');
			var value = $(this).attr('property-value');
			if (!coos.isEmpty(type)) {
				that.recordHistory();
				if (typeof (value) == 'undefined') {
					value = model[type];
					if (coos.isTrue(value)) {
						value = false;
					} else {
						value = true;
					}
				}
				model[type] = value;
				that.changeModel();
			}
		});
	};
	Editor.prototype.bindEvent = function($box) {

		var that = this;
		if (!window.editor_event_binded) {
			window.editor_event_binded = true;
			$(window).on('mousedown', function(e) {
				e = e || window.event;
				var editor = $(e.target).closest('.editor-box').data('editor');
				$('.editor-box').each(function(index, $box) {
					$($box).data('editor').mousefocus = false;
				});
				if (editor) {
					editor.mousefocus = true;
				}
			});
			$(window).on('keydown', function(e) {
				e = e || window.event;
				if (e.ctrlKey == true && e.keyCode == 83) { //Ctrl+S
					var editor = null;
					$('.editor-box').each(function(index, $box) {
						if ($($box).data('editor').mousefocus) {
							editor = $($box).data('editor');
						}
					});
					if (editor) {
						e.preventDefault();
						editor.toSave();
					}
				}
			});
		}
	};

	$(function() {
		$(window.document).on('click', '.setJexlScriptBtn', function(e) {
			var $target = $(e.target);
			var $input = null;
			if (e.target.tagName == 'INPUT' || e.target.tagName == 'TEXTAREA') {
				$input = $target;
			} else {
				var $group = $target.closest('.coos-input-group');
				if ($group.length > 0) {
					$input = $group.find('.coos-input');
				}
			}
			if ($input != null && $input.length > 0) {
				setJexlScript($input.val(), function(text) {
					$input.val(text);
					$input.change();
				});
			}
		});
	});

	Editor.help_html = `
	<div class="pdlr-10 mgb-20 ft-13" >
		<h4 class="color-orange">说明</h4>
		<div class="color-grey">1：数据格式以传输的报文为基础</div>
			<div class="color-grey pdl-20">例如：传输{key1:value1,key2:value2}</div>
			<div class="color-grey pdl-20">则映射变量：key1=value1,key2=value2。</div>
		<div class="color-grey">2：内置变量映射：</div>
			<div class="color-grey pdl-20">$data：为传输的数据</div>
			<div class="color-grey pdl-20">$body：为requestBody</div>
			<div class="color-grey pdl-20">$session：为AppSession</div>
			<div class="color-grey pdl-20">$reqeust：为请求参数</div>
			<div class="color-grey pdl-20">$header：为请求Header</div>
			<div class="color-grey pdl-20">$user：为登录用户</div>
			<div class="color-grey pdl-20">$cache：为AppSession缓存数据</div>
			<div class="color-grey pdl-20">$result：为执行节点产生的结果，可以使用$result.节点名称</div>
		<div class="color-grey">3：扩展函数：</div>
			<div class="color-grey pdl-20">$script_id.generate()：生成ID。示例：$script_id.generate(12~32（非必填）)</div>
			<div class="color-grey pdl-20">$script_date.now()：获取当前时间。示例：$script_date.now("yyyy-MM-dd HH:mm:ss"（非必填）)</div>
			<div class="color-grey pdl-20">$script_date.format()：格式化日期字符串。示例：$script_date.format(value（必填，可以是Long类型，Date类型，String类型）,"yyyy-MM-dd HH:mm:ss"（非必填）)</div>
			<div class="color-grey pdl-20">$script_md5.MD5()：MD5加密。示例：$script_md5.MD5(value（必填）)</div>
			<div class="color-grey pdl-20">$script_json.to_json()：转为JSON。示例：$script_json.to_json(value（必填）)</div>
			<div class="color-grey pdl-20">$script_tree.to_tree()：转为Tree结构。示例：$script_tree.to_tree(value（必填）,"id"（ID名称非必填）,"parentid"（父ID名称非必填）,"children"（子名称非必填）)</div>
		<div class="color-grey">4：使用说明：</div>
			<div class="color-grey pdl-20">$user.user：可以取到登录用户ID</div>
	</div>
	`;
	var setJexlScript = function(text, callback) {
		text = text || '';

		let data = {
			text : text
		};
		app.formDialog({
			title : 'JexlScript配置',
			width : "800px",
			"before-html" : Editor.help_html,
			"label-width" : '0px',
			items : [ {
				label : "",
				name : "text",
				type : "textarea"
			} ],
			data : data
		}).then(() => {
			callback && callback(data.text);
		}).catch(() => {
		});

	};
})();
(function() {
	var Editor = coos.Editor;


	Editor.prototype.recordHistory = function(isStep) {
		var index = this.historys.indexOf(this.model);
		if (index >= 0) {
			this.historys.splice(index, this.historys.length);
		} else {
		}
		this.historys.push($.extend(true, {}, this.model));
		this.stepindex = null;
		this.previous_step_nav.disabled = false;
		this.next_step_nav.disabled = true;
	};

})();
(function() {
	var Editor = coos.Editor;


	Editor.prototype.buildDesign = function() {


		var that = this;
		var $design = this.$design;
		$design.empty();
		$design.append('<div class="color-orange font-lg pd-80 text-center">暂未提供该文件格式在线设计，敬请期待！</div>');
	};
})();
(function() {
	var AppEditor = coos.createClass(coos.Editor);
	coos.Editor.App = AppEditor;

	AppEditor.prototype.isYaml = function() {
		return true;
	};

	AppEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-app pd-20"></div>');
		$design.append($box);

		var model = this.model;
		model.error = model.error || {};

		let html = `
		<div class="coos-row ">
			<h3 class="pdb-10 color-orange">应用信息配置</h3>
			<el-form :model="form" status-icon :rules="rules" ref="form" label-width="150px" size="mini">
				<el-form-item class label="名称" prop="name">
				  <el-input type="text" v-model="form.name" autocomplete="off" @change="change($event,'name')"></el-input>
				</el-form-item>
				<el-form-item class label="首页地址" prop="indexurl">
				  <el-input type="text" v-model="form.indexurl" autocomplete="off" @change="change($event,'indexurl')"></el-input>
				</el-form-item>
				<el-form-item class label="登录地址" prop="loginurl">
				  <el-input type="text" v-model="form.loginurl" autocomplete="off" @change="change($event,'loginurl')"></el-input>
				</el-form-item>
				<el-form-item class label="登录后跳转地址" prop="afterlogintourl">
				  <el-input type="text" v-model="form.afterlogintourl" autocomplete="off" @change="change($event,'afterlogintourl')"></el-input>
				</el-form-item>
				<el-form-item class label="退出后跳转" prop="afterlogouttourl">
				  <el-input type="text" v-model="form.afterlogouttourl" autocomplete="off" @change="change($event,'afterlogouttourl')"></el-input>
				</el-form-item>
				
				<el-form-item class label="忽略地址" prop="ignoreurl">
				  <el-input type="text" v-model="form.ignoreurl" autocomplete="off" @change="change($event,'ignoreurl')"></el-input>
				  <span>检查到匹配地址将直接放行，多个地址以“;”隔开如：/res/*;/abc/*。</span>
				</el-form-item>
				<el-form-item class label="登录忽略地址" prop="loginignoreurl">
				  <el-input type="text" v-model="form.loginignoreurl" autocomplete="off" @change="change($event,'loginignoreurl')"></el-input>
				  <span>检查到匹配地址将不检测是否登录，多个地址以“;”隔开如：/res/*;/abc/*。</span>
				</el-form-item>
				<el-form-item class label="必须登录" prop="requiredlogin">
				  <el-switch v-model="form.requiredlogin" @change="change($event,'requiredlogin')"></el-switch>
				</el-form-item>
				<el-form-item class label="登录匹配地址" prop="loginmatchurl">
				  <el-input type="text" v-model="form.loginmatchurl" autocomplete="off" @change="change($event,'loginmatchurl')"></el-input>
				  <span>检查到匹配地址将需要登录，多个地址以“;”隔开如：/res/*;/abc/*。</span>
				</el-form-item>
				
				<h3 class="pdtb-10 color-orange">错误页面配置</h3>
				<el-form-item class label="404地址" prop="error_notfound">
				  <el-input type="text" v-model="form.error_notfound" autocomplete="off" @change="change($event,'error_notfound')"></el-input>
				</el-form-item>
				<el-form-item class label="500地址" prop="error_error">
				  <el-input type="text" v-model="form.error_error" autocomplete="off" @change="change($event,'error_error')"></el-input>
				</el-form-item>
				<el-form-item class label="无权限地址" prop="error_nopermission">
				  <el-input type="text" v-model="form.error_nopermission" autocomplete="off" @change="change($event,'error_nopermission')"></el-input>
				</el-form-item>
			</el-form>
		</div>
		`;

		$box.append(html);
		let form = {};
		Object.assign(form, model);
		for (let key in model.error) {
			form['error_' + key] = model.error[key];
		}
		new Vue({
			el : $box[0],
			data () {
				return {
					form : form,
					rules : {}
				}
			},
			methods : {
				change (value, name) {
					if (!coos.isEmpty(name)) {
						that.recordHistory();
						if (name.startsWith("error_")) {
							name = name.replace("error_", "");
							model.error[name] = value;
						} else {
							model[name] = value;
						}
						that.changeModel(false);
					}
				}
			}
		});

	};
})();
(function() {
	var DatabaseEditor = coos.createClass(coos.Editor);
	coos.Editor.Database = DatabaseEditor;

	DatabaseEditor.prototype.isYaml = function() {
		return true;
	};
	DatabaseEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-database pd-20"></div>');
		$design.append($box);

		var model = this.model;
		let html = `
			<div class="coos-row ">
				<h3 class="pdb-10 color-orange">库信息配置</h3>
				<el-form :model="form" status-icon :rules="rules" ref="form" label-width="150px" size="mini">
					<el-form-item class label="驱动" prop="driver">
					  <el-input type="text" v-model="form.driver" autocomplete="off" @change="change($event,'driver')"></el-input>
					</el-form-item>
					<el-form-item class label="地址" prop="url">
					  <el-input type="text" v-model="form.url" autocomplete="off" @change="change($event,'url')"></el-input>
					</el-form-item>
					<el-form-item class label="用户名" prop="username">
					  <el-input type="text" v-model="form.username" autocomplete="off" @change="change($event,'username')"></el-input>
					</el-form-item>
					<el-form-item class label="密码" prop="password">
					  <el-input type="text" v-model="form.password" autocomplete="off" @change="change($event,'password')"></el-input>
					</el-form-item>
					<el-form-item class label="显示SQL" prop="showsql">
					  <el-switch v-model="form.showsql" @change="change($event,'showsql')"></el-switch>
					</el-form-item>
					<el-form-item class label="initialsize" prop="initialsize">
					  <el-input type="text" v-model="form.initialsize" autocomplete="off" @change="change($event,'initialsize')"></el-input>
					</el-form-item>
					<el-form-item class label="maxtotal" prop="maxtotal">
					  <el-input type="text" v-model="form.maxtotal" autocomplete="off" @change="change($event,'maxtotal')"></el-input>
					</el-form-item>
					<el-form-item class label="minidle" prop="minidle">
					  <el-input type="text" v-model="form.minidle" autocomplete="off" @change="change($event,'minidle')"></el-input>
					</el-form-item>
					<el-form-item class label="maxidle" prop="maxidle">
					  <el-input type="text" v-model="form.maxidle" autocomplete="off" @change="change($event,'maxidle')"></el-input>
					</el-form-item>
					<el-form-item class label="maxwaitmillis" prop="maxwaitmillis">
					  <el-input type="text" v-model="form.maxwaitmillis" autocomplete="off" @change="change($event,'maxwaitmillis')"></el-input>
					</el-form-item>
					<el-form-item class label="maxactive" prop="maxactive">
					  <el-input type="text" v-model="form.maxactive" autocomplete="off" @change="change($event,'maxactive')"></el-input>
					</el-form-item>
					<el-form-item class label="maxwait" prop="maxwait">
					  <el-input type="text" v-model="form.maxwait" autocomplete="off" @change="change($event,'maxwait')"></el-input>
					</el-form-item>
					<el-form-item class label="validationquery" prop="validationquery">
					  <el-input type="text" v-model="form.validationquery" autocomplete="off" @change="change($event,'validationquery')"></el-input>
					  <span>输入一段SQL以此判断连接是否正常，如：SELECT 1</span>
					</el-form-item>
					<el-form-item class label="数据库初始化实现" prop="initializeclass">
					  <el-input type="text" v-model="form.initializeclass" autocomplete="off" @change="change($event,'initializeclass')"></el-input>
					  <span>需要实现com.teamide.db.ifaces.IDatabaseInitialize接口，返回Database</span>
					</el-form-item>
				</el-form>
			</div>
			`;

		$box.append(html);
		let form = {};
		Object.assign(form, model);
		new Vue({
			el : $box[0],
			data () {
				return {
					form : form,
					rules : {}
				}
			},
			methods : {
				change (value, name) {
					if (!coos.isEmpty(name)) {
						that.recordHistory();
						model[name] = value;
						that.changeModel(false);
					}
				}
			}
		});


	};
})();
(function() {
	var TableEditor = coos.createClass(coos.Editor);
	coos.Editor.Table = TableEditor;

	TableEditor.prototype.isYaml = function() {
		return true;
	};
	TableEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-table editor-case"></div>');
		$design.append($box);

		var model = this.model;

		var $ul = $('<ul />')
		$box.append($ul);


		var $li;

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">名称：</span>');
		var $input = $('<input class="input" name="name" readonly="readonly"/>');
		$input.val(model.name);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">注释：</span>');
		var $input = $('<input class="input" name="comment" />');
		$input.val(model.comment);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">所属库：</span>');

		var $input = $('<select class="input" name="databasename" ></select>');
		$li.append($input);
		$input.append('<option value="">默认库</option>');
		$(that.context.DATABASE).each(function(index, one) {
			$input.append('<option value="' + one.name + '">' + one.name + '</option>');
		});
		$input.val(model.databasename);
		$li.append($input);
		that.bindLiEvent($li, model);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">分表规则：</span>');
		var $input = $('<input class="input" name="partitiontablerule" />');
		$input.val(model.partitiontablerule);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		model.columns = model.columns || [];
		var columns = model.columns;

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">字段</span>');
		var $btn = $('<a class="mglr-10 coos-pointer color-green">添加字段</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var column = {};
			column.nullable = true;
			column.type = 12;
			column.length = 250;
			columns.push(column);
			column.name = "";
			that.changeModel();
		});

		$(columns).each(function(index, column) {
			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);

			$li.append('<span class="pdlr-10">名称：</span>');
			var $input = $('<input class="input" name="name" required/>');
			$input.val(column.name);
			$li.append($input);

			$li.append('<span class="pdlr-10">注释：</span>');
			var $input = $('<input class="input" name="comment" />');
			$input.val(column.comment);
			$li.append($input);

			$li.append('<span class="pdlr-10">类型：</span>');
			var $input = $('<select class="input" name="type" required></select>');
			$li.append($input);
			$(that.ENUM_MAP.COLUMN_TYPE).each(function(index, one) {
				$input.append('<option value="' + one.value + '">' + one.text + '</option>');
			});
			$input.val(column.type);
			$li.append($input);
			that.bindLiEvent($li, column);


			$li.append('<span class="pdlr-10">长度：</span>');
			var $input = $('<input class="input input-mini" name="length" required/>');
			$input.val(column.length);
			$li.append($input);
			that.bindLiEvent($li, column);

			$li.append('<span class="pdlr-10">数字位数：</span>');
			var $input = $('<input class="input input-mini" name="precision" />');
			$input.val(column.precision);
			$li.append($input);
			that.bindLiEvent($li, column);

			$li.append('<span class="pdlr-10">小数位数：</span>');
			var $input = $('<input class="input input-mini" name="scale" />');
			$input.val(column.scale);
			$li.append($input);
			that.bindLiEvent($li, column, false);


			if (coos.isTrue(column.primarykey)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="primarykey"  >主键</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="primarykey" >非主键</a>');
			}
			if (coos.isTrue(column.nullable)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="nullable"  >可空</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="nullable" >不可空</a>');
			}
			$li.find('.updatePropertyBtn').click(function() {
				var type = $(this).attr('property-type');
				if (!coos.isEmpty(type)) {
					var value = column[type];
					if (coos.isTrue(value)) {
						value = false;
					} else {
						value = true;
					}
					that.recordHistory();
					column[type] = value;
					that.changeModel();
				}
			});

			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				columns.splice(columns.indexOf(column), 1);
				that.changeModel();
			});

			var $btn = $('<a class="mgl-10 coos-pointer color-orange">上移</a>');
			$li.append($btn);
			$btn.click(function() {
				var index = columns.indexOf(column);
				if (index > 0) {
					that.recordHistory();
					columns[index] = columns.splice(index - 1, 1, columns[index])[0];
					that.changeModel();
				}
			});

			var $btn = $('<a class="mgl-10 coos-pointer color-orange">下移</a>');
			$li.append($btn);
			$btn.click(function() {
				var index = columns.indexOf(column);
				if (index < columns.length - 1) {
					that.recordHistory();
					columns[index] = columns.splice(index + 1, 1, columns[index])[0];
					that.changeModel();
				}
			});

		});


	};
})();
(function() {
	var DaoEditor = coos.createClass(coos.Editor);
	coos.Editor.Dao = DaoEditor;

	DaoEditor.prototype.isYaml = function() {
		return true;
	};

	DaoEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-dao editor-case"></div>');
		$design.append($box);

		var model = this.model;

		var $ul = $('<ul />')
		$box.append($ul);

		var $li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">名称</span>');
		var $input = $('<input class="input" name="name" />');
		$input.val(model.name);
		$li.append($input);

		$li.append('<span class="pdr-10">说明</span>');
		var $input = $('<input class="input" name="comment" />');
		$input.val(model.comment);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">映射地址</span>');
		var $input = $('<input class="input" name="requestmapping" />');
		$input.val(model.requestmapping);
		$li.append($input);

		$li.append('<span class="pdr-10">ContentType</span>');
		var $input = $('<input class="input" name="contenttype" />');
		$input.val(model.contenttype);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">执行前Class</span>');
		var $input = $('<input class="input" name="beforeprocessor" />');
		$input.val(model.beforeprocessor);
		$li.append($input);

		$li.append('<span class="pdr-10">验证Class</span>');
		var $input = $('<input class="input" name="validator" />');
		$input.val(model.validator);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		model.process = model.process || {};

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">类型</span>');
		var $input = $('<select class="input mgr-10" name="type" ></select>');
		$li.append($input);
		$(that.ENUM_MAP.DAO_PROCESS_TYPE).each(function(index, one) {
			$input.append('<option value="' + one.value + '">' + one.text + '</option>');
		});
		$input.val(model.process.type);
		$li.append($input);
		that.bindLiEvent($li, model.process);

		model.process.type = model.process.type || 'SQL';
		if (model.process.type == 'SQL') {
			$li = $('<li />');
			$ul.append($li);
			var $view = this.createSqlProcessView(model.process);
			$li.append($view);
		} else if (model.process.type == 'HTTP') {
			$li = $('<li />');
			$ul.append($li);
			var $view = this.createHttpProcessView(model.process);
			$li.append($view);
		}

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">执行后Class</span>');
		var $input = $('<input class="input" name="afterprocessor" />');
		$input.val(model.afterprocessor);
		$li.append($input);
		that.bindLiEvent($li, model, false);


		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">格式化结果</span>');
		var $input = $('<input class="input setJexlScriptBtn" name="resultresolve" />');
		$input.val(model.resultresolve);
		$li.append($input);
		that.bindLiEvent($li, model, false);
	};



	DaoEditor.prototype.hasTest = function() {
		return true;
	};


	DaoEditor.prototype.toTest = function() {
		var that = this;
		let data = {
			body : "{\n}",
			result : ""
		};
		app.formDialog({
			title : '测试数据',
			width : "800px",
			"label-width" : " ",
			"before-html" : '<h4 class="color-orange">请填写JSON格式数据作为参数</h4>',
			items : [ {
				label : "数据",
				name : "data",
				type : "textarea"
			}, {
				label : "结果",
				name : "result",
				type : "textarea"
			} ],
			data : data,
			onClose (arg) {
				if (arg) {
					data.result = '';
					that.options.onTest(data, function(res) {
						data.result = (JSON.stringify(res));
					});
					return false;
				}
			}
		}).then(() => {

		}).catch(() => {
		});

	};



	DaoEditor.prototype.createSelectTableBtn = function() {};



})();
(function() {
	var DaoEditor = coos.Editor.Dao;
	DaoEditor.prototype.getModelColumnOptions = function() {
		let process = this.model.process || {};
		var sqlType = process.sqlType;
		let that = this;
		let options = [];

		let tables = [];

		if (sqlType.indexOf("SELECT") >= 0 || sqlType.indexOf("SAVE") >= 0) {
			$(process.select.froms).each(function(i, from) {
				let table = that.getTableByName(from.table);
				if (table && tables.indexOf(table) < 0) {
					tables.push(table);
				}
			});
		}
		if (sqlType.indexOf("INSERT") >= 0 || sqlType.indexOf("SAVE") >= 0) {
			let table = that.getTableByName(process.insert.table);
			if (table && tables.indexOf(table) < 0) {
				tables.push(table);
			}
		}
		if (sqlType.indexOf("UPDATE") >= 0 || sqlType.indexOf("SAVE") >= 0) {
			let table = that.getTableByName(process.update.table);
			if (table && tables.indexOf(table) < 0) {
				tables.push(table);
			}
		}
		if (sqlType.indexOf("DELETE") >= 0) {
			let table = that.getTableByName(process.delete.table);
			if (table && tables.indexOf(table) < 0) {
				tables.push(table);
			}
		}
		$(tables).each(function(i, table) {
			that.appendOptions(options, table.columns);
		});
		return options;
	};

	DaoEditor.prototype.createSqlProcessView = function(process) {
		var that = this;
		process.sqlType = process.sqlType || 'CUSTOM_SQL';
		var sqlType = process.sqlType;
		var $box = $('<li />');

		var $ul = $('<ul />');
		$box.append($ul);
		var $li = $('<li />');
		$ul.append($li);

		var $input = $('<select class="input" name="sqlType" required="1" ></select>');
		$li.append($input);
		$(this.ENUM_MAP.SQL_TYPE).each(function(index, one) {
			$input.append('<option value="' + one.value + '">' + one.text + '</option>');
		});
		$input.val(sqlType);

		that.bindLiEvent($li, process, true);

		if (sqlType.indexOf("SAVE") >= 0) {
			process.select = process.select || {};
			process.insert = process.insert || {};
			process.update = process.update || {};
		}
		if (sqlType.indexOf("CUSTOM") >= 0) {
			process.customSql = process.customSql || {};
			$li = that.createSqlCustomSqlView(process.customSql);
			$ul.append($li);
		}
		if (sqlType.indexOf("SELECT") >= 0 || sqlType.indexOf("SAVE") >= 0) {
			process.select = process.select || {};
			$li = that.createSqlSelectView(process.select);
			$ul.append($li);
		}
		if (sqlType.indexOf("INSERT") >= 0 || sqlType.indexOf("SAVE") >= 0) {
			process.insert = process.insert || {};
			$li = that.createSqlInsertView(process.insert);
			$ul.append($li);
		}
		if (sqlType.indexOf("UPDATE") >= 0 || sqlType.indexOf("SAVE") >= 0) {
			process.update = process.update || {};
			$li = that.createSqlUpdateView(process.update);
			$ul.append($li);
		}
		if (sqlType.indexOf("DELETE") >= 0) {
			process['delete'] = process['delete'] || {};
			$li = that.createSqlDeleteView(process['delete']);
			$ul.append($li);
		}

		return $box;

	};

})();
(function() {
	var DaoEditor = coos.Editor.Dao;

	DaoEditor.prototype.createSqlCustomSqlView = function(model) {
		var that = this;
		var $box = $('<li />');
		model = model || {};

		var $ul = $('<ul class="one-sql"/>')
		$box.append($ul);

		var $li = $('<li />');
		$ul.append($li);
		$li.append('<span ">自定义SQL类型</span>');

		var $input = $('<select class="input mgr-10" name="customsqltype" ></select>');
		$li.append($input);
		$input.append('<option value="">非查询（execute sql）</option>');
		$input.append('<option value="SELECT_ONE">查询单个</option>');
		$input.append('<option value="SELECT_LIST">查询列表</option>');
		$input.append('<option value="SELECT_PAGE">分页查询</option>');
		model.customsqltype = model.customsqltype || '';
		$input.val(model.customsqltype);

		that.bindLiEvent($li, model);


		var $li = $('<li />');
		$ul.append($li);
		$li.append('<span ">SQL</span>');

		var $li = $('<li class="pdl-10"/>');
		$ul.append($li);
		var $input = $('<textarea class="input" name="sql" />');
		$input.val(model.sql);
		$li.append($input);
		that.bindLiEvent($li, model, false);


		var $li = $('<li />');
		$ul.append($li);
		$li.append('<span ">COUNT SQL</span>');

		var $li = $('<li class="pdl-10"/>');
		$ul.append($li);
		var $input = $('<textarea class="input" name="countsql" />');
		$input.val(model.countsql);
		$li.append($input);
		that.bindLiEvent($li, model, false);



		return $box;
	};

})();
(function() {
	var DaoEditor = coos.Editor.Dao;

	DaoEditor.prototype.createSqlSelectView = function(model) {
		var that = this;
		var $box = $('<li />');
		model = model || {};
		model.columns = model.columns || [];
		var columns = model.columns;
		model.froms = model.froms || [];
		var froms = model.froms;
		model.leftjoins = model.leftjoins || [];
		var leftjoins = model.leftjoins;
		model.wheres = model.wheres || [];
		var wheres = model.wheres;
		model.groups = model.groups || [];
		var groups = model.groups;
		model.havings = model.havings || [];
		var havings = model.havings;
		model.orders = model.orders || [];
		var orders = model.orders;
		model.unions = model.unions || [];
		var unions = model.unions;
		model.subselects = model.subselects || [];
		var subselects = model.subselects;

		var $ul = $('<ul class="sub1"/>')
		$box.append($ul);


		var $li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10 color-orange">SELECT</span>');

		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加查询字段</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var column = {};
			columns.push(column);
			column.tablealias = "T1";
			column.name = "";
			column.alias = "";
			that.changeModel();
		});

		var $li = $('<li />');
		$ul.append($li);
		var $columnUl = $('<ul />')
		$li.append($columnUl);
		$(columns).each(function(index, column) {
			var $li = $('<li class="pdl-10"/>');
			$columnUl.append($li);



			var $subUl = $('<ul class=""/>');
			$li.append($subUl);

			var $li = $('<li class=""/>');
			$subUl.append($li);

			if (!coos.isEmpty(column.ifrule)) {
				$li.append('<span class="pdlr-10">if (</span>');
				var $input = $('<input class="input input-mini" name="ifrule" />');
				$input.val(column.ifrule);
				$li.append($input);
				$li.append('<span class="pdlr-10">) //不填或值为true、1则为真</span>');

				$li = $('<li class="pdl-30"/>');
				$subUl.append($li);
			}

			if (coos.isTrue(column.custom)) {
				var $input = $('<input class="input" name="customsql" />');
				$input.val(column.customsql);
				$li.append($input);

			} else {

				var $input = $('<input class="input input-mini" name="tablealias" />');
				$input.val(column.tablealias);
				$li.append($input);
				$li.append('<span class="pdlr-10">.</span>');
				var $input = $('<input class="input" name="name" />');

				app.autocomplete({
					$input : $input,
					datas : that.getModelColumnOptions()
				})
				$input.val(column.name);
				$li.append($input);
			}
			$li.append('<span class="pdlr-10">AS</span>');
			var $input = $('<input class="input" name="alias" />');

			app.autocomplete({
				$input : $input,
				datas : that.getModelColumnOptions()
			})
			$input.val(column.alias);
			$li.append($input);

			$li.append('<span class="pdlr-10">格式化值</span>');
			var $input = $('<input class="input" name="formatvalue" />');
			$input.val(column.formatvalue);
			$li.append($input);

			if (!coos.isTrue(column.custom)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="custom"  >设为自定义</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="custom" >设为非自定义</a>');
			}
			if (coos.isEmpty(column.ifrule)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="ifrule" property-value="1=1"  >设置条件</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="ifrule" property-value="">清空条件</a>');
			}
			var $btn = $('<a class="mgl-10 coos-pointer color-orange">修改</a>');
			$li.append($btn);
			$btn.click(function() {});
			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				columns.splice(columns.indexOf(column), 1);
				that.changeModel();
			});

			that.bindPropertyEvent($subUl, column);
			that.bindLiEvent($subUl, column, false);
		});

		var $li = $('<li />');
		$ul.append($li);

		var $subSelectUl = $('<ul class="sub2" />')
		$li.append($subSelectUl);

		var $li = $('<li />');
		$subSelectUl.append($li);

		if (subselects.length > 0) {
			$li.append('<span class="pdlr-10 color-orange">子查询模型</span>');
		}
		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加子查询</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var subselect = {};
			subselects.push(subselect);
			subselect.name = 'name_' + subselects.length;
			subselect.select = {};
			that.changeModel();
		});
		var $btn = that.createSelectTableBtn(function(table) {
			that.recordHistory();
			var subselect = {};
			subselects.push(subselect);
			subselect.name = table.name.toLocaleString();
			subselect.select = app.design.service.createSelectByTable(table);
			that.changeModel();

		});
		$li.append($btn);

		$(subselects).each(function(index, subselect) {
			var $li = $('<li class="pdl-10"/>');
			$subSelectUl.append($li);

			if (!coos.isEmpty(subselect.ifrule)) {
				$li.append('<span class="">if (</span>');
				var $input = $('<input class="input input-mini" name="ifrule" />');
				$input.val(subselect.ifrule);
				$li.append($input);
				$li.append('<span class="pdlr-10">) //不填或值true、1则为真</span>');
				that.bindLiEvent($li, subselect);

				$li = $('<li class="pdl-30"/>');
				$subSelectUl.append($li);
			}

			var $input = $('<input class="input" name="name" />');
			$input.val(subselect.name);
			$li.append($input);


			var $btn = $('<a class="mgl-10 coos-pointer color-orange">修改</a>');
			$li.append($btn);
			$btn.click(function() {});
			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				subselects.splice(subselects.indexOf(subselect), 1);
				that.changeModel();
			});
			if (coos.isTrue(subselect.queryone)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="queryone"  >查询单个</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="queryone" >查询列表</a>');
			}
			if (coos.isEmpty(subselect.ifrule)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="ifrule" property-value="1=1"  >设置条件</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="ifrule" property-value="">清空条件</a>');
			}

			var $btn = $('<a class="mgl-10 coos-pointer color-green">展开</a>');
			$li.append($btn);
			that.subselect_opens = that.subselect_opens || [];
			$btn.click(function() {
				var _i = that.subselect_opens.indexOf(subselect.name);
				if (_i >= 0) {
					that.subselect_opens.splice(_i, 1);
				} else {
					that.subselect_opens.push(subselect.name);
				}
				if ($(this).data('isOpen')) {
					$subSelectLi.hide();
					$(this).data('isOpen', false);
					$btn.text('展开');
				} else {
					$subSelectLi.show();
					$(this).data('isOpen', true);
					$btn.text('收起');
				}
			});
			that.bindPropertyEvent($li, subselect);
			that.bindLiEvent($li, subselect);
			var $subSelectLi = that.createSqlSelectView(subselect.select);
			$subSelectLi.hide();
			$subSelectLi.addClass('pdl-30 pdb-10');
			$subSelectUl.append($subSelectLi);
			if (that.subselect_opens.indexOf(subselect.name) >= 0) {
				$subSelectLi.show();
			}

		});

		var $li = $('<li />');
		$ul.append($li);

		if (froms.length > 0) {
			$li.append('<span class="pdr-10 color-orange">FROM </span>');
		}
		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加FROM</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var from = {};
			froms.push(from);
			from.table = "";
			from.alias = 'T' + (froms.length + leftjoins.length);
			that.changeModel();
		});

		$(froms).each(function(index, from) {
			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);

			var $input = $('<input class="input" name="table" />');
			app.autocomplete({
				$input : $input,
				datas : that.getOptions('TABLE')
			})
			$input.val(from.table);
			$li.append($input);
			$li.append('<span class="pdlr-10">AS</span>');
			var $input = $('<input class="input input-mini" name="alias" />');
			$input.val(from.alias);
			$li.append($input);

			var $btn = $('<a class="mgl-10 coos-pointer color-orange">修改</a>');
			$li.append($btn);
			$btn.click(function() {});
			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				froms.splice(froms.indexOf(from), 1);
				that.changeModel();
			});
			that.bindLiEvent($li, from);
		});
		var $li = $('<li />');
		$ul.append($li);

		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加LEFT JOIN</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var leftjoin = {};
			leftjoins.push(leftjoin);
			leftjoin.table = "TABLE";
			leftjoin.alias = 'T' + (froms.length + leftjoins.length);
			leftjoin.on = 'T1.id = ' + leftjoin.alias + '.id';
			that.changeModel();
		});
		$(leftjoins).each(function(index, leftjoin) {
			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);
			$li.append('<span class="pdr-10 color-orange">LEFT JOIN</span>');

			var $input = $('<input class="input" name="table" />');
			app.autocomplete({
				$input : $input,
				datas : that.getOptions('TABLE')
			})
			$input.val(leftjoin.table);
			$li.append($input);
			$li.append('<span class="pdlr-10">AS</span>');
			var $input = $('<input class="input input-mini" name="alias" />');
			$input.val(leftjoin.alias);
			$li.append($input);
			$li.append('<span class="pdlr-10">ON</span>');
			var $input = $('<input class="input" name="on" />');
			$input.val(leftjoin.on);
			$li.append($input);

			var $btn = $('<a class="mgl-10 coos-pointer color-orange">修改</a>');
			$li.append($btn);
			$btn.click(function() {});
			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				leftjoins.splice(leftjoins.indexOf(leftjoin), 1);
				that.changeModel();
			});
			that.bindLiEvent($li, leftjoin);
		});

		var table = null;
		if (froms.length > 0) {
			table = that.getTableByName(froms[0].table);
		}
		that.appendWhereLi($ul, wheres, table);

		that.appendGroupBy($ul, groups, froms);


		if (havings.length > 0) {
			var $li = $('<li />');
			$ul.append($li);

			$li.append('HAVING ');

		}
		that.appendOrderBy($ul, orders, froms);
		if (unions.length > 0) {
			var $li = $('<li />');
			$ul.append($li);

			$li.append('UNION ');

		}
		return $box;
	};
	DaoEditor.prototype.appendGroupBy = function($ul, groups, froms) {
		var that = this;
		var $li = $('<li />');
		$ul.append($li);
		if (groups.length > 0) {
			$li.append('<span class="pdr-10 color-orange">GROUP BY</span>');
		}
		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加分组</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var group = {};
			groups.push(group);
			group.tablealias = 'T1';
			group.name = '';
			that.changeModel();
		});
		$(groups).each(function(index, group) {
			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);

			if (!coos.isEmpty(group.ifrule)) {
				$li.append('<span class="pdlr-10">if (</span>');
				var $input = $('<input class="input input-mini" name="ifrule" />');
				$input.val(group.ifrule);
				$li.append($input);
				$li.append('<span class="pdlr-10">) //不填或值为true、1则为真</span>');

				var $li = $('<li class="pdl-30"/>');
				$ul.append($li);
			}

			var $input = $('<input class="input input-mini" name="tablealias" />');
			$input.val(group.tablealias);
			$li.append($input);
			$li.append('<span class="pdlr-10">.</span>');
			var $input = $('<input class="input" name="name" />');
			app.autocomplete({
				$input : $input,
				datas : that.getModelColumnOptions()
			})
			$input.val(group.name);
			$li.append($input);


			if (coos.isEmpty(group.ifrule)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="ifrule" property-value="1=1"  >设置条件</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="ifrule" property-value="">清空条件</a>');
			}
			var $btn = $('<a class="mgl-10 coos-pointer color-orange">修改</a>');
			$li.append($btn);
			$btn.click(function() {});
			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				groups.splice(groups.indexOf(group), 1);
				that.changeModel();
			});
			that.bindPropertyEvent($li, group);
			that.bindLiEvent($li, group);
		});
	};

	DaoEditor.prototype.appendOrderBy = function($ul, orders, froms) {
		var that = this;
		var $li = $('<li />');
		$ul.append($li);
		if (orders.length > 0) {
			$li.append('<span class="pdr-10 color-orange">ORDER BY</span>');
		}
		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加排序</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var order = {};
			orders.push(order);
			order.tablealias = 'T1';
			order.name = 'name';
			order.order = 'DESC';
			that.changeModel();
		});
		$(orders).each(function(index, order) {
			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);

			if (!coos.isEmpty(order.ifrule)) {
				$li.append('<span class="pdlr-10">if (</span>');
				var $input = $('<input class="input input-mini" name="ifrule" />');
				$input.val(order.ifrule);
				$li.append($input);
				$li.append('<span class="pdlr-10">) //不填或值为true、1则为真</span>');

				var $li = $('<li class="pdl-30"/>');
				$ul.append($li);
			}

			var $input = $('<input class="input input-mini" name="tablealias" />');
			$input.val(order.tablealias);
			$li.append($input);
			$li.append('<span class="pdlr-10">.</span>');
			var $input = $('<input class="input" name="name" />');
			app.autocomplete({
				$input : $input,
				datas : that.getModelColumnOptions()
			})
			$input.val(order.name);
			$li.append($input);
			$li.append('<span class="pdlr-10"></span>');
			var $input = $('<input class="input" name="order" />');
			$input.val(order.order);
			$li.append($input);

			if (coos.isEmpty(order.ifrule)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="ifrule" property-value="1=1"  >设置条件</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="ifrule" property-value="">清空条件</a>');
			}
			var $btn = $('<a class="mgl-10 coos-pointer color-orange">修改</a>');
			$li.append($btn);
			$btn.click(function() {});
			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				orders.splice(orders.indexOf(order), 1);
				that.changeModel();
			});
			that.bindPropertyEvent($li, order);
			that.bindLiEvent($li, order);
		});
	};

})();
(function() {
	var DaoEditor = coos.Editor.Dao;
	DaoEditor.prototype.createSqlInsertView = function(model) {
		var that = this;
		var $box = $('<li />');
		model = model || {};
		model.columns = model.columns || [];
		var columns = model.columns;

		var $ul = $('<ul class="sub1"/>')
		$box.append($ul);

		var $li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10 color-orange">INSERT INTO</span>');

		var $input = $('<input class="input" name="table" />');
		app.autocomplete({
			$input : $input,
			datas : that.getOptions('TABLE')
		})
		$input.val(model.table);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		var $btn = $('<a class="mglr-10 coos-pointer color-green">添加插入字段</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var column = {};
			columns.push(column);
			column.name = "";
			that.changeModel();
		});

		$(columns).each(function(index, column) {
			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);

			var $subUl = $('<ul class=""/>');
			$li.append($subUl);

			var $li = $('<li class=""/>');
			$subUl.append($li);

			if (!coos.isEmpty(column.ifrule)) {

				$li.append('<span class="pdlr-10">if (</span>');
				var $input = $('<input class="input input-mini" name="ifrule" />');
				$input.val(column.ifrule);
				$li.append($input);
				$li.append('<span class="pdlr-10">) //不填或值为true、1则为真</span>');

				var $li = $('<li class="pdl-30"/>');
				$subUl.append($li);

			}


			var $input = $('<input class="input" name="name" />');
			app.autocomplete({
				$input : $input,
				datas : that.getModelColumnOptions()
			})
			$input.val(column.name);
			$li.append($input);
			$li.append('<span class="mglr-10">=</span>');

			var $input = $('<input class="input input-mini " name="value" />');
			$input.val(column.value);
			$li.append($input);
			$li.append('<span class="pdr-10">或</span>');
			$li.append('<span class="">自动取名称相同的值  或</span>');
			var $input = $('<input class="input input-mini " name="defaultvalue" />');
			$input.val(column.defaultvalue);
			$li.append($input);
			$li.append('<span class="pdr-10"></span>');

			if (coos.isEmpty(column.ifrule)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="ifrule" property-value="1=1"  >设置条件</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="ifrule" property-value="">清空条件</a>');
			}
			if (!coos.isTrue(column.required)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="required" >设为必填</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="required" >设为非必填</a>');
			}
			var $btn = $('<a class="mgl-10 coos-pointer color-orange">修改</a>');
			$li.append($btn);
			$btn.click(function() {});
			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				columns.splice(columns.indexOf(column), 1);
				that.changeModel();
			});

			that.bindPropertyEvent($li, column);
			that.bindLiEvent($subUl, column, false);
		});
		return $box;
	};

})();
(function() {
	var DaoEditor = coos.Editor.Dao;

	DaoEditor.prototype.createSqlUpdateView = function(model) {
		var that = this;
		var $box = $('<li />');
		model = model || {};
		model.columns = model.columns || [];
		model.wheres = model.wheres || [];
		var columns = model.columns;
		var wheres = model.wheres;

		var $ul = $('<ul class="sub1"/>')
		$box.append($ul);

		var $li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10 color-orange">UPDATE</span>');

		var $input = $('<input class="input" name="table" />');
		app.autocomplete({
			$input : $input,
			datas : that.getOptions('TABLE')
		})
		$input.val(model.table);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		var $btn = $('<a class="mglr-10 coos-pointer color-green">添加更新字段</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var column = {};
			columns.push(column);
			column.name = "";
			that.changeModel();
		});


		$(columns).each(function(index, column) {
			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);

			var $subUl = $('<ul class=""/>');
			$li.append($subUl);

			var $li = $('<li class=""/>');
			$subUl.append($li);

			if (!coos.isEmpty(column.ifrule)) {
				$li.append('<span class="pdlr-10">if (</span>');
				var $input = $('<input class="input input-mini" name="ifrule" />');
				$input.val(column.ifrule);
				$li.append($input);
				$li.append('<span class="pdlr-10">) //不填或值为true、1则为真</span>');

				var $li = $('<li class="pdl-30"/>');
				$subUl.append($li);
			}

			var $input = $('<input class="input" name="name" />');
			app.autocomplete({
				$input : $input,
				datas : that.getModelColumnOptions()
			})
			$input.val(column.name);
			$li.append($input);
			$li.append('<span class="mglr-10">=</span>');

			var $input = $('<input class="input input-mini " name="value" />');
			$input.val(column.value);
			$li.append($input);
			$li.append('<span class="pdr-10">或</span>');
			$li.append('<span class="">自动取名称相同的值  或</span>');
			var $input = $('<input class="input input-mini " name="defaultvalue" />');
			$input.val(column.defaultvalue);
			$li.append($input);
			$li.append('<span class="pdr-10"></span>');


			if (coos.isEmpty(column.ifrule)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="ifrule" property-value="1=1"  >设置条件</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="ifrule" property-value="">清空条件</a>');
			}

			if (!coos.isTrue(column.required)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="required" >设为必填</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="required" >设为非必填</a>');
			}
			var $btn = $('<a class="mgl-10 coos-pointer color-orange">修改</a>');
			$li.append($btn);
			$btn.click(function() {});
			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				columns.splice(columns.indexOf(column), 1);
				that.changeModel();
			});

			that.bindPropertyEvent($li, column);
			that.bindLiEvent($subUl, column, false);
		});

		var table = that.getTableByName(model.table);
		that.appendWhereLi($ul, wheres, table);

		return $box;
	};

})();
(function() {
	var DaoEditor = coos.Editor.Dao;

	DaoEditor.prototype.createSqlDeleteView = function(model) {
		var that = this;
		var $box = $('<li />');
		model = model || {};
		model.wheres = model.wheres || [];
		var wheres = model.wheres;

		var $ul = $('<ul class="sub1"/>')
		$box.append($ul);

		var $li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10 color-orange">DELETE FROM</span>');

		var $input = $('<input class="input" name="table" />');
		app.autocomplete({
			$input : $input,
			datas : that.getOptions('TABLE')
		})
		$input.val(model.table);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		var table = that.getTableByName(model.table);
		that.appendWhereLi($ul, wheres, table);

		return $box;
	};

})();
(function() {
	var DaoEditor = coos.Editor.Dao;

	DaoEditor.prototype.appendWhereLi = function($ul, wheres, table, isPiece) {
		var that = this;
		var $li = $('<li />');
		$ul.append($li);

		if (wheres.length > 0) {
			if (!isPiece) {
				$li.append('<span class="pdr-10 color-orange">WHERE 1=1</span>');
			} else {
				$li.append('<span class="pdr-10 color-orange">1=1</span>');
			}
		}
		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加条件</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var where = {};
			wheres.push(where);
			where.tablealias = 'T1';
			where.name = '';
			where.comparisonoperator = '=';
			where.relationaloperation = 'AND';
			where.nullable = true;
			that.changeModel();
		});
		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加条件块</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var where = {};
			wheres.push(where);
			where.piece = true;
			where.relationaloperation = 'AND';
			that.changeModel();
		});

		$(wheres).each(function(index, where) {

			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);

			var $subUl = $('<ul class=""/>');
			$li.append($subUl);

			var $li = $('<li class=""/>');
			$subUl.append($li);


			if (!coos.isEmpty(where.ifrule)) {
				$li.append('<span class="pdlr-10">if (</span>');
				var $input = $('<input class="input input-mini" name="ifrule" />');
				$input.val(where.ifrule);
				$li.append($input);
				$li.append('<span class="pdlr-10">) //不填或值为true、1则为真</span>');

				var $li = $('<li class="pdl-30"/>');
				$subUl.append($li);

			}

			var $input = $('<select class="input mgr-10" name="relationaloperation" ></select>');
			$li.append($input);
			$input.append('<option value="AND">AND</option>');
			$input.append('<option value="OR">OR</option>');
			where.relationaloperation = where.relationaloperation || 'AND';
			$input.val(where.relationaloperation);

			if (coos.isTrue(where.piece)) {
				//				var $ulS = $('<ul/>');
				//				$li.append($ulS);
				//				where.wheres = where.wheres || [];
				//				that.appendWhereLi($ulS, where.wheres, table, true);
			} else {

				if (coos.isTrue(where.custom)) {
					var $input = $('<input class="input" name="customsql" />');
					$input.val(where.customsql);
					$li.append($input);

				} else {
					var $input = $('<input class="input input-mini" name="tablealias" />');
					$input.val(where.tablealias);
					$li.append($input);
					$li.append('<span class="pdlr-10">.</span>');
					var $input = $('<input class="input" name="name" />');
					app.autocomplete({
						$input : $input,
						datas : that.getModelColumnOptions()
					})
					$input.val(where.name);
					$li.append($input);
					var $input = $('<select class="input input-mini mglr-10" name="comparisonoperator" ></select>');
					$li.append($input);
					$(that.ENUM_MAP.COMPARISON_OPERATOR).each(function(index, one) {
						$input.append('<option value="' + one.value + '">' + one.text + '</option>');
					});
					where.comparisonoperator = where.comparisonoperator || '=';
					$input.val(where.comparisonoperator);

					var $input = $('<input class="input input-mini " name="value" />');
					$input.val(where.value);
					$li.append($input);
					$li.append('<span class="pdr-10">或</span>');
					$li.append('<span class="">自动取名称相同的值  或</span>');
					var $input = $('<input class="input input-mini " name="defaultvalue" />');
					$input.val(where.defaultvalue);
					$li.append($input);
					$li.append('<span class="pdr-10"></span>');
				}

				if (!coos.isTrue(where.custom)) {
					$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="custom"  >设为自定义</a>');
				} else {
					$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="custom" >设为非自定义</a>');
				}

			}

			if (coos.isEmpty(where.ifrule)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="ifrule" property-value="1=1"  >设置条件</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="ifrule" property-value="">清空条件</a>');
			}

			if (!coos.isTrue(where.required)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="required" >设为必填</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="required" >设为非必填</a>');
			}

			var $btn = $('<a class="mgl-10 coos-pointer color-orange">修改</a>');
			$li.append($btn);
			$btn.click(function() {});
			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				wheres.splice(wheres.indexOf(where), 1);
				that.changeModel();
			});

			that.bindPropertyEvent($li, where);
			that.bindLiEvent($subUl, where, false);
			if (coos.isTrue(where.piece)) {
				where.wheres = where.wheres || [];

				var $li = $('<li class="pdl-30"/>');
				$subUl.append($li);
				$li.append('(');

				var $li = $('<li class="pdl-30"/>');
				$subUl.append($li);

				var $ul_ = $('<ul class="pdl-30"/>');
				$li.append($ul_);

				that.appendWhereLi($ul_, where.wheres, table, true);

				var $li = $('<li class="pdl-30"/>');
				$subUl.append($li);
				$li.append(')');
			}
		});
	};

})();
(function() {
	var DaoEditor = coos.Editor.Dao;

	DaoEditor.prototype.createHttpProcessView = function(process) {
		var that = this;

	};

})();
(function() {
	var ServiceEditor = coos.createClass(coos.Editor);
	coos.Editor.Service = ServiceEditor;

	ServiceEditor.prototype.isYaml = function() {
		return true;
	};

	ServiceEditor.prototype.createProcessChoose = function() {

		var $box = $('<div class="editor-process-choose"></div>');


	};

	ServiceEditor.prototype.buildServiceView = function($box, model) {
		var that = this;
		$box.find('.service-view').remove();
		var $case = $('<div class="service-view editor-case"></div>');
		var $a = $('<a class="coos-link color-green mgr-5">收起</a>');
		$a.click(function() {
			if (!that.closeServiceView) {
				that.closeServiceView = true;
				$case.addClass('close');
				$a.text('展开');
			} else {
				that.closeServiceView = false;
				$case.removeClass('close');
				$a.text('收起');
			}
		});
		$case.append($a);
		if (that.closeServiceView) {
			$case.addClass('close');
			$a.text('展开');
		}
		$box.append($case);

		var $ul = $('<ul />')
		$case.append($ul);

		var $li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">名称</span>');
		var $input = $('<input class="input" name="name" />');
		$input.val(model.name);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">说明</span>');
		var $input = $('<input class="input" name="comment" />');
		$input.val(model.comment);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">映射地址</span>');
		var $input = $('<input class="input" name="requestmapping" />');
		$input.val(model.requestmapping);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">ContentType</span>');
		var $input = $('<input class="input" name="contenttype" />');
		$input.val(model.contenttype);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">执行前Class</span>');
		var $input = $('<input class="input" name="beforeprocessor" />');
		$input.val(model.beforeprocessor);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">验证Class</span>');
		var $input = $('<input class="input" name="validator" />');
		$input.val(model.validator);
		$li.append($input);
		that.bindLiEvent($li, model, false);


		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">执行后Class</span>');
		var $input = $('<input class="input" name="afterprocessor" />');
		$input.val(model.afterprocessor);
		$li.append($input);
		that.bindLiEvent($li, model, false);


		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">格式化结果</span>');
		var $input = $('<input class="input setJexlScriptBtn" name="resultresolve" />');
		$input.val(model.resultresolve);
		$li.append($input);
		that.bindLiEvent($li, model, false);
	};

	ServiceEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-service"></div>');
		$design.append($box);

		var model = this.model;
		model.processs = model.processs || [];
		var hasStart = false;
		var hasEnd = false;
		$(model.processs).each(function(index, process) {
			if (process.type == 'START') {
				hasStart = true;
			} else if (process.type == 'END') {
				hasEnd = true;
			}
		});
		if (!hasStart) {
			model.processs.push({
				type : 'START',
				name : 'start',
				text : '开始',
				left : 30,
				top : 300
			});
		}
		if (!hasEnd) {
			model.processs.push({
				type : 'END',
				name : 'end',
				text : '结束',
				left : 300,
				top : 300
			});
		}
		that.buildServiceView($box, model);
		that.buildProcesssView($box, model.processs);
		$(document).on("click", "html", function() {
			that.destroyContextmenu();
		});
		$box.on("contextmenu", function(e) {
			e = e || window.e;
			var eventData = {
				clientX : e.clientX,
				clientY : e.clientY
			};
			var $processBox = $(e.target).closest('.process-box');
			if ($processBox.length == 0) {
				return;
			}
			var $node = $(e.target).closest('.process-node');
			var menus = [];
			if ($node.length > 0) {
				var process = $node.data('process');
				menus.push({
					text : "验证",
					onClick : function() {
						that.toViewValidate(process);
					}
				});
				menus.push({
					text : "变量",
					onClick : function() {
						that.toViewVariable(process);
					}
				});
				menus.push({
					text : "结果",
					onClick : function() {
						that.toViewResult(process);
					}
				});
				if (process.type == 'START' || process.type == 'END') {

				} else {
					menus.push({
						text : "修改",
						onClick : function() {
							that.toUpdateProcess(process);
						}
					});
					menus.push({
						text : "删除",
						onClick : function() {
							that.toDeleteProcess(process);
						}
					});
				}
			} else {
				menus.push({
					text : "添加",
					onClick : function() {

						var $service = that.$el.find('.editor-service');
						that.toInsertProcess({
							top : eventData.clientY - $service.offset().top,
							left : eventData.clientX - $service.offset().left
						});
					}
				});

			}

			source.repository.contextmenu.menus = menus;
			source.repository.contextmenu.show = true;
			source.repository.contextmenu.left = e.pageX + "px";
			source.repository.contextmenu.top = e.pageY + "px";
			e.preventDefault();
		});
	};

	ServiceEditor.prototype.onView = function() {
		this.render();
	};

	ServiceEditor.prototype.destroyContextmenu = function() {
		var that = this;
		if (this.$contextmenu) {
			this.$contextmenu.fadeOut(100, function() {
				that.$contextmenu
					.css({
						display : ""
					})
					.find(".drop-left")
					.removeClass("drop-left");
			});
		}
	};

	ServiceEditor.prototype.hasSetting = function() {
		return true;
	};

	ServiceEditor.prototype.toSetting = function() {

		var model = this.model;
		var that = this;

		let data = {};
		Object.assign(data, model);
		app.formDialog({
			title : '配置服务',
			width : "800px",
			items : [ {
				label : "映射路径",
				name : "requestmapping"
			}, {
				label : "前置处理器",
				name : "beforeprocessor"
			}, {
				label : "验证器",
				name : "validator"
			}, {
				label : "后置处理器",
				name : "afterprocessor"
			}, {
				label : "结果报文配置",
				name : "setting",
				"class-name" : "setJexlScriptBtn"
			} ],
			data : data
		}).then(() => {
			that.recordHistory();
			$.extend(true, model, data);
			that.changeModel();
		}).catch(() => {
		});

	};



})();
(function() {
	var ServiceEditor = coos.Editor.Service;


	ServiceEditor.prototype.hasTest = function() {
		return true;
	};

	ServiceEditor.prototype.toTest = function() {
		var that = this;
		let data = {
			body : "{\n}",
			result : ""
		};
		app.formDialog({
			title : '测试服务',
			width : "800px",
			"label-width" : " ",
			"before-html" : '<h4 class="color-orange">请填写JSON格式数据作为参数</h4>',
			items : [ {
				label : "数据",
				name : "data",
				type : "textarea"
			}, {
				label : "结果",
				name : "result",
				type : "textarea"
			} ],
			data : data,
			onClose (arg) {
				if (arg) {
					data.result = '';
					that.options.onTest(data, function(res) {
						data.result = (JSON.stringify(res));
					});
					return false;
				}
			}
		}).then(() => {

		}).catch(() => {
		});

	};
})();
(function() {
	var ServiceEditor = coos.Editor.Service;

	ServiceEditor.prototype.getIdByProcess = function(process) {
		var id = this.id_prefix + process.name;
		return id.replace(/[^0-9a-zA-Z_]/g, '');
	};

	ServiceEditor.prototype.getNodeByProcess = function(process) {
		var id = this.getIdByProcess(process);
		return $('#' + id);
	};


	ServiceEditor.prototype.getProcessFormOptions = function(data) {
		let daos = [];
		let daoOptions = this.getOptions('DAO');
		let serviceOptions = this.getOptions('SERVICE');

		return {
			width : "800px",
			items : [ {
				label : "名称",
				name : "name",
				info : "名称可包含英文、数字、（_）、（-）",
				pattern : "^[a-zA-Z0-9_\-]{0,20}$",
				required : true
			}, {
				label : "显示文案",
				name : "text",
				required : true
			}, {
				"v-if" : "form.type != 'START' || form.type != 'END'",
				label : "类型",
				name : "type",
				required : true,
				type : "radio",
				options : [ {
					text : "决策节点",
					value : "DECISION"
				}, {
					text : "条件节点",
					value : "CONDITION"
				}, {
					text : "数据节点",
					value : "DAO"
				}, {
					text : "子服务节点",
					value : "SUB_SERVICE"
				}, {
					text : "异常结束节点",
					value : "ERROR_END"
				} ]
			}, {
				"v-if" : "form.type == 'DAO'",
				label : "数据访问名称",
				name : "daoname",
				type : "select",
				required : true,
				options : daoOptions
			}, {
				"v-if" : "form.type == 'SUB_SERVICE'",
				label : "服务名称",
				name : "servicename",
				type : "select",
				required : true,
				options : serviceOptions
			}, {
				"v-if" : "form.type == 'DAO' || form.type == 'SERVICE'",
				label : "配置数据",
				name : "data",
				info : "此处配置传入数据，默认使用$data解析的数据",
				"class-name" : "setJexlScriptBtn"
			}, {
				"v-if" : "form.type == 'CONDITION'",
				label : "条件",
				name : "condition"
			}, {
				"v-if" : "form.type == 'ERROR_END'",
				label : "错误码",
				name : "errcode"
			}, {
				"v-if" : "form.type == 'ERROR_END'",
				label : "错误信息",
				name : "errmsg"
			} ],
			data : data
		};

	};
	ServiceEditor.prototype.toInsertProcess = function(process) {
		process = process || {};
		process.type = process.type || 'DAO';

		let that = this;
		let data = {};
		Object.assign(data, process);

		let options = this.getProcessFormOptions(data);
		options.title = '添加流程';
		app.formDialog(options).then(() => {
			data.name.trim();
			var find = false;
			$(that.model.processs).each(function(index, process) {
				if (process.name == data.name) {
					find = true;
				}
			});
			if (find) {
				coos.error('名称已存在，请重新输入！');
				that.toInsertProcess(data);
				return;
			}
			if (data.name == 'start' || data.name == 'end') {
				coos.error('名称不能定义为start或end!');
				that.toInsertProcess(data);
				return;
			}

			that.recordHistory();

			$.extend(true, process, data);
			that.model.processs.push(process);
			that.changeModel();
		}).catch(() => {
		});

	};

	ServiceEditor.prototype.toUpdateProcess = function(process) {
		let that = this;
		let data = {};
		Object.assign(data, process);

		let options = this.getProcessFormOptions(data);
		options.title = '修改流程';
		app.formDialog(options).then(() => {
			data.name.trim();
			var find = false;
			$(that.model.processs).each(function(index, one) {
				if (one.name == data.name && one != process) {
					find = true;
				}
			});
			if (find) {
				coos.error('名称已存在，请重新输入！');
				that.toUpdateProcess(data);
				return;
			}
			if (data.name == 'start' || data.name == 'end') {
				coos.error('名称不能定义为start或end!');
				that.toUpdateProcess(data);
				return;
			}
			$(that.model.processs).each(function(index, one) {
				if (one.to == process.name) {
					one.to = data.name;
				}
			});

			that.recordHistory();
			$.extend(true, process, data);
			that.changeModel();
		}).catch(() => {
		});

	};



	ServiceEditor.prototype.toDeleteProcess = function(process, eventData) {
		var that = this;
		if (process.type == 'START') {
			coos.error('开始节点不能删除！');
			return;
		}
		if (process.type == 'END') {
			coos.error('结束节点不能删除！');
			return;
		}
		coos.confirm('确定删除' + process.name + '节点？', function() {
			$(that.model.processs).each(function(index, one) {
				if (one.to == one.name) {
					delete one.to;
				}
			});
			that.recordHistory();
			that.model.processs.splice(that.model.processs.indexOf(process), 1);
			that.changeModel();
		});
	};
})();
(function() {
	var ServiceEditor = coos.Editor.Service;

	ServiceEditor.prototype.buildProcesssView = function($box, processs) {
		var that = this;
		this.id_prefix = this.id_prefix || ('' + coos.getNumber() + '-');
		$box.find('.process-box').remove();
		var $processBox = $('<div class="process-box"/>');
		$box.append($processBox);
		jsPlumb.ready(function() {
			var design_ = jsPlumb.getInstance();
			$(processs).each(function(index, process) {
				var $node = $('<div class="process-node"/>');
				var $toolbar = $('<div class="process-node-toolbar"/>');
				var $ul = $('<div  />');
				var $btn = $('<a class="coos-btn validate-btn coos-btn-xs color-green" title="验证">验证</a>');
				$btn.click(function(e) {
					that.toViewValidate(process);
				});
				$ul.append($btn)
				var $btn = $('<a class="coos-btn variable-btn coos-btn-xs color-green" title="变量">变量</a>');
				$btn.click(function(e) {
					that.toViewVariable(process);
				});
				$ul.append($btn)
				var $btn = $('<a class="coos-btn result-btn coos-btn-xs color-orange" title="结果">结果</a>');
				$btn.click(function(e) {
					that.toViewResult(process);
				});
				$ul.append($btn);
				if (process.type == 'START' || process.type == 'END') {

				} else {
					var $btn = $('<a class="coos-btn coos-btn-xs color-green" title="设置">设置</a>');
					$btn.click(function(e) {
						that.toUpdateProcess(process);
					});
					$ul.append($btn)
					var $btn = $('<a class="coos-btn coos-btn-xs color-red" title="删除">删除</a>');
					$btn.click(function(e) {
						that.toDeleteProcess(process);
					});
					$ul.append($btn)
				}
				$node.on('mousedown', function(e) {
					if ($(e.target).closest('.process-node-toolbar').length > 0 ||

						$(e.target).closest('.process-variable-box').length > 0) {
						e.stopPropagation();
						return false;
					}
				});
				$toolbar.append($ul);
				$node.append($toolbar);
				$node.attr('id', that.getIdByProcess(process));
				$node.data('process', process);
				$node.addClass('process-node-' + process.type);
				$node.append('<div class="ring" />');
				var $content = $('<div class="content" />');
				$node.append($content);

				switch (process.type) {
				case "START":
					break;
				case "END":
					break;
				case "DECISION":
					//$content.append('<i class="icon coos-icon coos-icon-branches"></i>');
					break;
				case "CONDITION":
					//$content.append('<i class="icon coos-icon coos-icon-issuesclose"></i>');
					break;
				case "DAO":
					//$content.append('<i class="icon coos-icon coos-icon-database"></i>');
					break;
				case "SUB_SERVICE":
					//$content.append('<i class="icon coos-icon coos-icon-sever"></i>');
					break;
				}
				if (process.image) {
					var $img = $('<img class="image"/>');
					$img.attr('src', coos.url.format(process.image));
					$content.append($img);
				}

				if (process.text) {
					var $text = $('<div class="text" />');
					$text.text(process.text);
					$content.append($text);
				} else if (process.name) {
					var $text = $('<div class="text" />');
					$text.text(process.name);
					$content.append($text);
				}

				$node.css('left', (process.left || 0) + 'px');
				$node.css('top', (process.top || 0) + 'px');
				$processBox.append($node);

				process.tos = process.tos || [];

			});
			design_.importDefaults(defaultStyle);
			$(processs).each(function(index, process) {
				if (process.tos) {
					var tos = [];
					$(process.tos).each(function(index, to) {
						var toProcess = null;
						$(processs).each(function(index, p) {
							if (p.name == to) {
								toProcess = p;
							}
						});
						if (toProcess) {
							tos.push(to);
							design_.connect({
								source : that.getIdByProcess(process),
								target : that.getIdByProcess(toProcess)
							});
						}
					});
					process.tos = tos;
				}
			});
			$processBox.find(".process-node .ring").each(function(i, e) {
				var p = $(e).parent();
				design_.makeSource($(e));
			});

			design_.makeTarget($processBox.find(".process-node .ring"), {
				endpoint : defaultStyle.Endpoints[0],
				connectionOverlays : defaultStyle.ConnectionOverlays,
				anchor : defaultStyle.Anchor,
				connector : defaultStyle.Connector,
				paintStyle : defaultStyle.PaintStyle,
				dragOptions : defaultStyle.DragOptions,
				beforeDrop : function(params) {
					if (params.sourceId == params.targetId) {
						coos.warn('不能连线自己！');
						return false; /* 不能链接自己 */
					}
					var sourceProcess = $processBox.find('#' + params.sourceId).closest('.process-node').data('process');
					var targetProcess = $processBox.find('#' + params.targetId).closest('.process-node').data('process');
					if (sourceProcess.type == 'END') {
						coos.warn('无法从结束节点开始连线！');
						return false;
					}
					if (sourceProcess.type == 'ERROR_END') {
						coos.warn('异常结束节点不能连接其他节点！');
						return false;
					}
					if (targetProcess.type == 'START') {
						coos.warn('无法连线到开始节点！');
						return false;
					}


					sourceProcess.tos = sourceProcess.tos || [];
					targetProcess.tos = targetProcess.tos || [];
					if (sourceProcess.tos.indexOf(targetProcess.name) >= 0) {
						coos.warn('节点已有连线，无法重复连线！');
						return false;
					} else if (targetProcess.tos.indexOf(sourceProcess.name) >= 0) {
						coos.warn('节点已有连线，无法重复连线！');
						return false;
					}
					if (sourceProcess.type != 'DECISION') {
						if (sourceProcess.tos.length > 0) {
							coos.warn('该节点无法建立多条连线！');
							return false;
						}
					} else {
						if (targetProcess.type != 'CONDITION') {
							coos.warn('决策节点只能与条件节点建立连线！');
							return false;
						}
					}
					that.recordHistory();
					sourceProcess.tos.push(targetProcess.name);
					that.changeModel();
					return false;
				}
			});
			design_.draggable($processBox.find(".process-node "), {
				rightButtonCanDrag : false,
				stop : function(arg1, arg2) {
					that.recordHistory();
					var process = $(arg1.el).data('process');
					process.top = $(arg1.el).position().top;
					process.left = $(arg1.el).position().left;
					that.changeModel();
				}
			});
			// 监听所有的连接事件
			design_.bind("connection", function(info, event) {
				// console.log(info);
			});
			// 监听所有的删除连接事件
			design_.bind("connectionDetached", function(params, event) {});

			function connectionDetached(params) {
				// console.log(info);
				var sourceProcess = $processBox.find('#' + params.sourceId).closest('.process-node').data('process');
				var targetProcess = $processBox.find('#' + params.targetId).closest('.process-node').data('process');
				if (!sourceProcess || !targetProcess) {
					return false;
				}
				that.recordHistory();

				$(sourceProcess.tos).each(function(index, to) {
					if (to == targetProcess.name) {
						sourceProcess.tos.splice(index, 1);
					}
				});

				that.changeModel();

				return false;
			}
			// 点击连接时的触发事件
			design_.bind("click", function(component) {
				coos.confirm('是否删除连线？', function() {
					connectionDetached(component);
				});

				return false;
			});

			that.viewValidates = that.viewValidates || [];
			$(that.viewValidates).each(function(index, name) {
				var process = null;
				$(processs).each(function(index, p) {
					if (p.name == name) {
						process = p;
					}
				});
				if (process == null) {
					var index = that.viewValidates.indexOf(name);
					that.viewValidates.splice(index, 1);
					return;
				}
				that.toViewValidate(process);
			});

			that.viewVariables = that.viewVariables || [];
			$(that.viewVariables).each(function(index, name) {
				var process = null;
				$(processs).each(function(index, p) {
					if (p.name == name) {
						process = p;
					}
				});
				if (process == null) {
					var index = that.viewVariables.indexOf(name);
					that.viewVariables.splice(index, 1);
					return;
				}
				that.toViewVariable(process);
			});

			that.viewResults = that.viewResults || [];
			$(that.viewResults).each(function(index, name) {
				var process = null;
				$(processs).each(function(index, p) {
					if (p.name == name) {
						process = p;
					}
				});
				if (process == null) {
					var index = that.viewResults.indexOf(name);
					that.viewResults.splice(index, 1);
					return;
				}
				that.toViewResult(process);
			});
		});

	};
	var defaultStyle = {
		DragOptions : {
			cursor : 'pointer',
		},
		// 端点样式
		Endpoint : [ "Dot", {
			radius : 1
		} ],
		// 端点样式
		Endpoints : [ [ "Dot", {
			radius : 1,
			cssClass : "display-none"
		} ], [ "Dot", {
			radius : 1,
			cssClass : "display-none"
		} ] ],
		// 箭头
		ConnectionOverlays : [ [ "Arrow", {
			width : 10,
			length : 10,
			location : 1,
			foldback : 0.8,
			visible : true
		} ], [ "Label", {
			location : 0.1,
			id : "label",
			cssClass : "aLabel"
		} ] ],
		Anchor : 'Continuous',
		// 连接器的类型
		Connector : [ 'Flowchart', {} ],
		// 连线样式
		PaintStyle : {
			strokeWidth : 2,
			stroke : "#456"
		}
	};
})();
(function() {
	var ServiceEditor = coos.Editor.Service;

	ServiceEditor.prototype.toViewVariable = function(process) {
		this.viewVariables = this.viewVariables || [];
		var $node = this.getNodeByProcess(process);
		var index = this.viewVariables.indexOf(process.name);
		if (this.viewValidates.indexOf(process.name) >= 0) {
			this.toViewValidate(process);
		}
		if (index >= 0) {
			if ($node.find('.process-variable-box').length > 0) {
				this.viewVariables.splice(index, 1);
				$node.find('.process-variable-box').remove();
				$node.find('.process-node-toolbar').removeClass('show');
				$node.find('.process-node-toolbar').find('.variable-btn').removeClass('coos-active');
				return;
			}
		}
		if (index < 0) {
			this.viewVariables.push(process.name);
		}
		var $box = $('<div class="process-variable-box"/>');
		$node.find('.process-node-toolbar').append($box);
		$node.find('.process-node-toolbar').addClass('show');
		$node.find('.process-node-toolbar').find('.variable-btn').addClass('coos-active');
		this.appendVariables($box, process);

	};
	ServiceEditor.prototype.appendVariables = function($box, process) {
		var that = this;
		$box.append('<h4 class="title color-orange">变量（名称:值:默认值）</h4>');
		var $list = $('<ul class="coos-list ft-13 pd-5  " />');
		$box.append($list);

		process.variables = process.variables || [];

		$(process.variables).each(function(index, variable) {
			var $li = $('<li class=""></li>');
			$list.append($li)

			var $card = $('<div class="coos-card "></div>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);
			$span.text(variable.name);

			$card.append('<span class="pdlr-2 ">:</span>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);
			$span.text(variable.value);

			$card.append('<span class="pdlr-2 ">:</span>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);
			$span.text(variable.defaultvalue);

			$li.append($card)

			var $btn = $('<a class="coos-link color-green">修改</a>');
			$btn.click(function() {
				that.toUpdateVariable(variable);
			});
			$card.append($btn)

			var $btn = $('<a class="coos-link color-red">删除</a>');
			$btn.click(function() {
				that.toDeleteVariable(process, variable);
			});
			$card.append($btn)
		});
		var $li = $('<li class=""></li>');
		$list.append($li)

		var $btn = $('<a class="coos-link color-green">添加变量</a>');
		$btn.click(function() {
			that.toInsertVariable(process);
		});
		$li.append($btn)

	};

	ServiceEditor.prototype.getVariableFormOptions = function(data) {

		return {
			width : "800px",
			items : [ {
				label : "名称",
				name : "name"
			}, {
				label : "值",
				name : "value",
				"class-name" : "setJexlScriptBtn"
			}, {
				label : "默认值",
				name : "defaultvalue",
				"class-name" : "setJexlScriptBtn"
			}, {
				label : "取值器",
				name : "valuer"
			} ],
			data : data
		};

	};
	ServiceEditor.prototype.toInsertVariable = function(process) {
		process = process || {};
		process.variables = process.variables || [];
		var variable = {};

		let that = this;
		let data = {};
		Object.assign(data, variable);

		let options = this.getVariableFormOptions(data);
		options.title = '添加变量';
		app.formDialog(options).then(() => {
			data.name.trim();
			that.recordHistory();
			$.extend(true, variable, data);
			process.variables.push(variable);
			that.changeModel();
			editBox.destroy();
		}).catch(() => {
		});

	};

	ServiceEditor.prototype.toUpdateVariable = function(variable) {
		let that = this;
		let data = {};
		Object.assign(data, variable);

		let options = this.getVariableFormOptions(data);
		options.title = '修改变量';
		app.formDialog(options).then(() => {
			data.name.trim();
			that.recordHistory();
			$.extend(true, variable, data);
			that.changeModel();
		}).catch(() => {
		});

	};

	ServiceEditor.prototype.toDeleteVariable = function(process, variable) {
		var that = this;
		coos.confirm('确定删除该变量？', function() {
			that.recordHistory();
			process.variables.splice(process.variables.indexOf(variable), 1);
			that.changeModel();
		});
	};
})();
(function() {
	var ServiceEditor = coos.Editor.Service;

	ServiceEditor.prototype.toViewValidate = function(process) {
		this.viewValidates = this.viewValidates || [];
		var $node = this.getNodeByProcess(process);
		if (this.viewVariables.indexOf(process.name) >= 0) {
			this.toViewVariable(process);
		}
		var index = this.viewValidates.indexOf(process.name);
		if (index >= 0) {
			if ($node.find('.process-validate-box').length > 0) {
				this.viewValidates.splice(index, 1);
				$node.find('.process-validate-box').remove();
				$node.find('.process-node-toolbar').removeClass('show');
				$node.find('.process-node-toolbar').find('.validate-btn').removeClass('coos-active');
				return;
			}
		}
		if (index < 0) {
			this.viewValidates.push(process.name);
		}
		var $box = $('<div class="process-validate-box"/>');
		$node.find('.process-node-toolbar').append($box);
		$node.find('.process-node-toolbar').addClass('show');
		$node.find('.process-node-toolbar').find('.validate-btn').addClass('coos-active');
		this.appendValidates($box, process);

	};
	ServiceEditor.prototype.appendValidates = function($box, process) {
		var that = this;
		$box.append('<h4 class="title color-orange">验证（值:必填:类型:表达式:正则）</h4>');
		var $list = $('<ul class="coos-list ft-13 pd-5 " />');
		$box.append($list);

		process.validates = process.validates || [];

		$(process.validates).each(function(index, validate) {
			var $li = $('<li ></li>');
			$list.append($li)

			var $card = $('<div class="coos-card "></div>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);
			$span.text(validate.value);

			$card.append('<span class="pdlr-2 ">:</span>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);
			$span.text(coos.isTrue(validate.required));

			$card.append('<span class="pdlr-2 ">:</span>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);
			$span.text(validate.type);

			$card.append('<span class="pdlr-2 ">:</span>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);
			$span.text(validate.rule);

			$card.append('<span class="pdlr-2 ">:</span>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);
			$span.text(validate.pattern);

			$li.append($card)


			var $btn = $('<a class="coos-link color-green">修改</a>');
			$btn.click(function() {
				that.toUpdateValidate(validate);
			});
			$card.append($btn)

			var $btn = $('<a class="coos-link color-red">删除</a>');
			$btn.click(function() {
				that.toDeleteValidate(process, validate);
			});
			$card.append($btn)
		});
		var $li = $('<li ></li>');
		$list.append($li)

		var $btn = $('<a class="coos-link color-green">添加验证</a>');
		$btn.click(function() {
			that.toInsertValidate(process);
		});
		$li.append($btn)

	};

	ServiceEditor.prototype.getValidateFormOptions = function(data) {

		let types = [];
		$(source.ENUM_MAP.VALUE_TYPE).each(function(index, one) {
			types.push({
				text : one.text,
				value : one.value
			})
		});

		return {
			width : "800px",
			items : [ {
				label : "值",
				name : "value",
				"class-name" : "setJexlScriptBtn"
			}, {
				label : "必填",
				name : "required",
				type : "switch"
			}, {
				label : "正则",
				name : "pattern"
			}, {
				label : "表达式",
				name : "rule",
				"class-name" : "setJexlScriptBtn"
			}, {
				label : "类型",
				name : "type",
				type : "select",
				options : types
			}, {
				label : "验证器",
				name : "validator"
			}, {
				label : "错误码",
				name : "errcode"
			}, {
				label : "错误信息",
				name : "errmsg"
			} ],
			data : data
		};


	};
	ServiceEditor.prototype.toInsertValidate = function(process) {
		process = process || {};
		process.validates = process.validates || [];
		var validate = {};

		let that = this;
		let data = {};
		Object.assign(data, validate);

		let options = this.getValidateFormOptions(data);
		options.title = '添加验证';
		app.formDialog(options).then(() => {
			that.recordHistory();
			$.extend(true, validate, data);
			process.validates.push(validate);
			that.changeModel();
		}).catch(() => {
		});

	};

	ServiceEditor.prototype.toUpdateValidate = function(validate) {
		let that = this;
		let data = {};
		Object.assign(data, validate);

		let options = this.getValidateFormOptions(data);
		options.title = '修改验证';
		app.formDialog(options).then(() => {
			that.recordHistory();
			$.extend(true, validate, data);
			that.changeModel();
			editBox.destroy();
		}).catch(() => {
		});

	};

	ServiceEditor.prototype.toDeleteValidate = function(process, validate) {
		var that = this;
		coos.confirm('确定删除该验证？', function() {
			that.recordHistory();
			process.validates.splice(process.validates.indexOf(validate), 1);
			that.changeModel();
		});
	};
})();
(function() {
	var ServiceEditor = coos.Editor.Service;

	ServiceEditor.prototype.toViewResult = function(process) {
		var $node = $('#process-node-' + process.name);

	};
})();
(function() {
	var DictionaryEditor = coos.createClass(coos.Editor);
	coos.Editor.Dictionary = DictionaryEditor;

	DictionaryEditor.prototype.isYaml = function() {
		return true;
	};
	DictionaryEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class=""></div>');
		$design.append($box);

		var model = this.model;
		model.options = model.options || [];

		model.options.forEach(option => {
			let html = `
				<div class="coos-row " style="width: 50%;display: inline-block;">
				<div class="pdlr-10 mg-5 bd">
					<h3 class="pd-10 color-orange font-sm">选项—【{{form.text || form.value}}】
					<a class="float-right coos-link color-red font-xs" @click="remove()">删除</a>
					</h3>
					<el-form :model="form" class="overflow-auto" status-icon :rules="rules" ref="form" label-width="70px" size="mini">
						
						<el-form-item :class="{'col-12' : form.type == '','col-6' : form.type != ''}" label="类型" prop="username">
						  <el-select v-model="form.type" placeholder="请选择" class="col-12">
				            <el-option label="使用当前配置数据" value=""></el-option>
				            <el-option label="读Table数据" value="TABLE"></el-option>
				            <el-option label="读Dao数据" value="DAO"></el-option>
				            <el-option label="读Service数据" value="SERVICE"></el-option>
				          </el-select>
						</el-form-item>
						
						<el-form-item class="col-6" label="读取表" v-if="form.type == 'TABLE'" prop="tablename">
						  <el-input type="text" v-model="form.tablename" autocomplete="off" @change="change($event,'tablename')"></el-input>
						</el-form-item>
						<el-form-item class="col-6" label="读取Dao" v-if="form.type == 'DAO'" prop="daoname">
						  <el-input type="text" v-model="form.daoname" autocomplete="off" @change="change($event,'daoname')"></el-input>
						</el-form-item>
						<el-form-item class="col-6" label="读取Service" v-if="form.type == 'SERVICE'" prop="servicename">
						  <el-input type="text" v-model="form.servicename" autocomplete="off" @change="change($event,'servicename')"></el-input>
						</el-form-item>
						
						<el-form-item class="col-4" label="文案" prop="text">
						  <el-input type="text" v-model="form.text" autocomplete="off" @change="change($event,'text')"></el-input>
						</el-form-item>
						<el-form-item class="col-4" label="值" prop="value">
						  <el-input type="text" v-model="form.value" autocomplete="off" @change="change($event,'value')"></el-input>
						</el-form-item>
						<el-form-item class="col-4" label="父" prop="parent">
						  <el-input type="text" v-model="form.parent" autocomplete="off" @change="change($event,'parent')"></el-input>
						</el-form-item>
						
						<el-form-item class="col-4" label="字体图标" prop="fonticon">
						  <el-input type="text" v-model="form.fonticon" autocomplete="off" @change="change($event,'fonticon')"></el-input>
						</el-form-item>
						<el-form-item class="col-4" label="颜色" prop="color">
						  <el-input type="text" v-model="form.color" autocomplete="off" @change="change($event,'color')"></el-input>
						</el-form-item>
						<el-form-item class="col-4" label="地址" prop="url">
						  <el-input type="text" v-model="form.url" autocomplete="off" @change="change($event,'url')"></el-input>
						</el-form-item>
						<el-form-item class="col-4" label="HTML" prop="html">
						  <el-input type="text" v-model="form.html" autocomplete="off" @change="change($event,'html')"></el-input>
						</el-form-item>
						<el-form-item class="col-4" label="图片" prop="image">
						  <el-input type="text" v-model="form.image" autocomplete="off" @change="change($event,'image')"></el-input>
						</el-form-item>
						<el-form-item class="col-4" label="说明" prop="info">
						  <el-input type="text" v-model="form.info" autocomplete="off" @change="change($event,'info')"></el-input>
						</el-form-item>
					</el-form>
				</div>
				</div>
				`;
			option.type = option.type || '';
			let $html = $(html);
			$box.append($html);
			let form = {};
			Object.assign(form, option);
			new Vue({
				el : $html[0],
				data () {
					return {
						form : form,
						rules : {}
					}
				},
				methods : {
					change (value, name) {
						if (!coos.isEmpty(name)) {
							that.recordHistory();
							option[name] = value;
							that.changeModel(false);
						}
					},
					remove () {
						that.recordHistory();
						model.options.splice(model.options.indexOf(option), 1);
						that.changeModel();
					}
				}
			});

		});

		var $btn = $('<a class="coos-link color-green">添加选项</a>');
		$btn.click(function() {
			that.recordHistory();
			model.options.push({});
			that.changeModel();
		});

		var $row = $('<div class="coos-row text-center pdtb-10"/>');
		$row.append($btn);

		$box.append($row);
	};
})();
(function() {
	var PageEditor = coos.createClass(coos.Editor);
	coos.Editor.Page = PageEditor;

	PageEditor.prototype.isHtml = function() {
		return true;
	};

	PageEditor.prototype.buildDesign = function() {

		if (!this.pageEditor) {
			var that = this;
			this.pageEditor = new coos.PageEditor({
				onChange : function(page, code) {
					that.recordHistory();
					$.extend(true, that.model, page);
					that.changeModel(false, code);
					return that.model;
				}
			});
		}
		this.pageEditor.build(this.$design, this.model);
	};
})();

})(window);