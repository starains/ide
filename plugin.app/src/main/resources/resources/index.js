(function(window) {
	'use strict';
(function() {


	source.loadApp = function(project) {
		source.plugin.app.event("LOAD_APP", {
			path : project.path
		}, project).then(res => {
			let value = res.value || {};
			source.setProjectApp(project, value.app);
		});
	};


	source.appGenerateSourceCode = function(type, project) {

		source.plugin.app.event("GENERATE_SOURCE_CODE", {
			type : type,
			path : project.path
		}, project).then(res => {
			if (res.errcode == 0) {
				coos.success('生成成功！');
				source.reloadProject(project).then(res => {
					source.loadGitStatus();
				});
			} else {
				coos.error(res.errmsg);
			}
		});
	};
	let AppPlugin = function(options) {
		options = options || {};
		this.options = options;
	};

	window.AppPlugin = AppPlugin;

	function hasPermission(arg) {
		if (source.space.permission == 'MASTER') {
			return true;
		}
		if (source.space.permission == 'DEVELOPER') {
			return true;
		}
		return false;
	}

	AppPlugin.prototype.onContextmenu = function(data, menus) {
		let that = this;
		let project = source.getProjectByPath(data.path);
		let appModel = source.getProjectApp(project);

		if (!appModel) {
			return;
		}
		menus.push({
			header : "应用"
		});
		if (data.isProject || appModel.path == data.path) {
			if (appModel.context.JAVA != null) {
				menus.push({
					text : "生成Java源码",
					disabled : !hasPermission("GENERATE_SOURCE_CODE"),
					onClick () {
						source.appGenerateSourceCode('java', project);
					}
				});
			}
			if (appModel.context.GO != null) {
				menus.push({
					text : "生成Go源码",
					disabled : !hasPermission("GENERATE_SOURCE_CODE"),
					onClick () {
						source.appGenerateSourceCode('go', project);
					}
				});
			}
			if (appModel.context.NODE != null) {
				menus.push({
					text : "生成Node源码",
					disabled : !hasPermission("GENERATE_SOURCE_CODE"),
					onClick () {
						source.appGenerateSourceCode('node', project);
					}
				});
			}
			if (appModel.context.VUE != null) {
				menus.push({
					text : "生成Vue源码",
					disabled : !hasPermission("GENERATE_SOURCE_CODE"),
					onClick () {
						source.appGenerateSourceCode('vue', project);
					}
				});
			}
			menus.push({
				text : "生成库表",
				disabled : !hasPermission("GENERATE_TABLE"),
				onClick () {
					let param = {};
					param.path = appModel.localpath;
					param.type = "DATABASE";
					source.plugin.app.event("doTest", param, project).then(result => {
						if (result.errcode == 0) {
							coos.success("库表创建成功！");
						} else {
							coos.error(result.errmsg);
						}
					});
				}
			});
		}

		if (appModel.path == data.path || data.path.startsWith(appModel.path + "/")) {
			let model = getModelTypeByPath(data.path);
			if (model && model.value == "TABLE") {
				let databases = [];
				databases.push("");

				databases.forEach(database => {
					menus.push({
						text : "导入" + database + "已有表",
						disabled : !hasPermission("IMPORT_TABLE"),
						onClick () {
							source.tableImportForm.show(appModel,
								{
									databasename : database,
									parent : data
								},
								project
							).then(res => {
							});
						}
					});
				});
			}
		}
	};


	let getModelTypeByPath = function(path) {
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

	AppPlugin.prototype.onCreateEditory = function(options) {
		let that = this;
		options = options || {};

		let file = options.file;
		if (!file) {
			return;
		}
		let project = source.getProjectByPath(file.path);

		let appBean = source.getProjectApp(project);
		if (!appBean) {
			return;
		}
		let type = null;
		let model = null;
		if (appBean.path_model_type) {
			Object.keys(appBean.path_model_type).forEach(path => {
				let m = appBean.path_model_type[path];
				if (m.isDirectory) {
					if (file.path.startsWith(path + '/')) {
						model = m;
					}
				} else {
					if (file.path == (path)) {
						model = m;
					}
				}
			});
		}
		if (!model) {
			return;
		}
		if (appBean.path_model_bean) {
			model.bean = appBean.path_model_bean[file.path];
		}
		model.bean = model.bean || {};
		file.model = model;
		type = model.value;

		options = Object.assign({}, options);

		options.type = type;
		options.plugin = this;
		options.project = project;
		let modeDesigner = createEditor(options);

		options.editor.isYaml = true;
		options.editor.addDesigner(modeDesigner);
	};

	let createEditor = function(options) {
		options = options || {};
		options.type = options.type || '';
		var editor = null;
		switch (options.type.toUpperCase()) {
		case "APP":
			editor = new Editor.App(options);
			break;
		case "JDBC":
			editor = new Editor.Database(options);
			break;
		case "DATABASE":
			editor = new Editor.Database(options);
			break;
		case "TABLE":
			editor = new Editor.Table(options);
			break;
		case "DAO":
			editor = new Editor.Dao(options);
			break;
		case "SERVICE":
			editor = new Editor.Service(options);
			break;
		case "CACHE":
			editor = new Editor.Cache(options);
			break;
		case "DICTIONARY":
			editor = new Editor.Dictionary(options);
			break;
		case "CODE":
			editor = new Editor.Code(options);
			break;
		case "THEME":
			editor = new Editor.Theme(options);
			break;
		case "PAGE":
			editor = new Editor.Page(options);
			break;
		case "PAGECOMPONENT":
			editor = new Editor.PageComponent(options);
			break;
		case "RESOURCE":
			editor = new Editor.Resource(options);
			break;
		case "PLUGIN":
			editor = new Editor.Plugin(options);
			break;
		case "ATTRIBUTE":
			editor = new Editor.Attribute(options);
			break;
		case "CONTROL":
			editor = new Editor.Control(options);
			break;
		case "JEXL":
			editor = new Editor.Jexl(options);
			break;
		case "BEAN":
			editor = new Editor.Bean(options);
			break;
		case "JAVA":
			editor = new Editor.Java(options);
			break;
		default:
			editor = new Editor(options);
			break;
		}
		return editor;
	};
})();
var Editor = function(options) {
	options = options || {};
	this.initOptions(options);
	this.init();
};
(function() {

	Editor.prototype.initOptions = function(options) {
		options = options || {};
		this.key = 'APP-MODEL-EDITOR';
		this.text = '模型设计';
		this.options = options;
		this.readyonly = options.readyonly;
		this.type = options.type;
		this.plugin = options.plugin;
		this.editor = options.editor;
		this.model_original = options.model || {};
		this.project = options.project || {};
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
		this.navs = this.getNavs();
	};
	Editor.prototype.getApp = function() {
		return this.project.attribute.app;
	};


	Editor.prototype.init = function() {};


	Editor.prototype.build = function($box) {
		var that = this;
		$box.empty();
		var $design = $('<div class="editor-design app-model-editor  coos-scrollbar" />');
		$box.append($design);
		this.$design = $design;

		this.$design.on('scroll', function() {
			that.designScrollTop = that.$design.scrollTop();
		});
		$design.data('editor', this);


		var $help = $('<div class="editor-help" />');
		$box.append($help);
		this.$help = $help;
		$help.append(this.getHelpHtml());

		this.bindEvent();
	};
	Editor.prototype.getCodeByModel = function(model, callback) {
		let that = this;
		var data = {
			model : model,
			type : this.type,
			filename : this.file.name
		}
		source.plugin.app.event('toText', data, this.project).then(res => {
			callback && callback(res);
		});
	};
	Editor.prototype.loadLastContent = function(callback) {
		let that = this;
		var data = {
			model : this.model,
			type : this.type,
			filename : this.file.name
		}
		this.getCodeByModel(this.model, callback);
	};
	Editor.prototype.show = function(callback) {
		var that = this;
		var that = this;
		function view() {
			that.buildDesignView();
			callback && callback();
		}
		if (this.type) {
			this.editor.loadLastContent(function(content) {
				var data = {
					text : content,
					type : that.type,
					filename : that.file.name
				}
				if (that.lastText && that.lastText == data.text) {
					view();
				} else {
					var changed = false;
					if (that.lastText && that.lastText != data.text) {
						changed = true;
					}
					that.lastText = data.text;

					source.plugin.app.event('toModel', data, that.project).then(res => {
						if (res.errcode == 0) {
							if (res.value) {
								if (changed) {
									that.recordHistory();
								}
								var model = res.value;
								model.name = that.model.name;
								that.model = model;
							}
						} else {
							coos.box.error(res.errmsg);
						}
						view();
					});
				}
			});
		} else {
			view();
		}

	};
	Editor.prototype.buildDesignView = function(callBuild) {
		this.buildDesign();
		if (this.designScrollTop >= 0) {
			this.$design.scrollTop(this.designScrollTop);
		}
	};

	Editor.prototype.changeModel = function(callBuild) {
		var that = this;
		if (coos.isEmpty(callBuild) || coos.isTrue(callBuild)) {
			this.buildDesignView();
		}
		coos.trimObject(this.model, [ undefined, null, "" ], true);
		this.lastModel = $.extend(true, {}, this.model);
		this.editor.onChange(true, this.lastModel, this);
	};


	Editor.prototype.setCacheData = function(data) {
		var that = this;
		this.show(function() {
			that.recordHistory();
			that.model = data;
			that.changeModel(true);
		});
	};

})();
(function() {


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
		if (this.lastModel != null) {
			this.getCodeByModel(this.lastModel, function(res) {
				that.editor.save(res, function(flag) {
					if (flag) {
						that.editor.onChange(false, that.lastModel, that);
					}
				});
			});
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
	Editor.prototype.getContext = function(type) {
		var context = null;
		let app = this.getApp();
		if (app) {
			context = app.context;
		}
		return context;
	};
	Editor.prototype.getBeans = function(type) {
		var list = [];
		var context = this.getContext();
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
				initWidth(this);
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
	Editor.prototype.getNavs = function() {
		var that = this;
		var navs = [];

		this.save_nav = {
			fonticon : 'save',
			disabled : this.options.onSave == null || this.readyonly,
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
		return navs;
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

	Editor.prototype.getBodyHtml = function() {
		return '<div/>';
	};

})();
(function() {
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
	Editor.prototype.bindEvent = function() {

		var that = this;
		if (!window.editor_event_binded) {
			window.editor_event_binded = true;
			$(window).on('mousedown', function(e) {
				e = e || window.event;
				var editor = $(e.target).closest('.app-model-editor').data('editor');
				$('.app-model-editor').each(function(index, $box) {
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
					$('.app-model-editor').each(function(index, $box) {
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
					if ($input.data('data') && $input.attr('form-name')) {
						$input.data('data')[$input.attr('form-name')] = text;
					}
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
			<div class="color-grey pdl-20">$script_base64.encode()：Base64加密。示例：$script_base64.encode(value（必填）)</div>
			<div class="color-grey pdl-20">$script_base64.decode()：Base64解密。示例：$script_base64.decode(value（必填）)</div>
			<div class="color-grey pdl-20">$script_aes.encode()：AES加密。示例：$script_aes.encode(value（必填）, key（必填）)</div>
			<div class="color-grey pdl-20">$script_aes.decode()：AES解密。示例：$script_aes.decode(value（必填）, key（必填）)</div>
			<div class="color-grey pdl-20">$script_util.toStar()：替换星号。示例：$script_util.toStar(value（必填）, start（必填）, end（必填）)</div>
		<div class="color-grey">4：使用说明：</div>
			<div class="color-grey pdl-20">$data.xxx：取到请求的参数</div>
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


	Editor.prototype.buildDesign = function() {


		var that = this;
		var $design = this.$design;
		$design.empty();
		$design.append('<div class="color-orange font-lg pd-80 text-center">暂未提供该文件格式在线设计，敬请期待！</div>');
	};
})();
(function() {

	Editor.prototype.appendCustomProcessors = function($ul, processors, isAfter) {
		processors = processors || [];
		var that = this;
		var $li = $('<li />');
		$ul.append($li);

		if (isAfter) {
			$li.append('<span class="pdr-10 color-orange">后置执行自定义</span>');
		} else {
			$li.append('<span class="pdr-10 color-orange">前置执行自定义</span>');
		}
		$li.append('<span class="pdr-10 color-grey">参数：variableCache（Service变量）,invokeCache（Service结果缓存）,data（Dao变量）,result（Dao结果）,exception（异常）</span>');
		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var processor = {};
			processors.push(processor);
			processor.type = 'CLASS';
			processor.param = 'data';
			that.changeModel();
		});

		$(processors).each(function(index, processor) {

			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);

			var $subUl = $('<ul class=""/>');
			$li.append($subUl);

			var $li = $('<li class=""/>');
			$subUl.append($li);


			if (!coos.isEmpty(processor.ifrule)) {
				$li.append('<span class="pdlr-10">if (</span>');
				var $input = $('<input class="input input-mini" name="ifrule" />');
				$input.val(processor.ifrule);
				$li.append($input);
				$li.append('<span class="pdlr-10">) //不填或值为true、1则为真</span>');

				var $li = $('<li class="pdl-30"/>');
				$subUl.append($li);

			}

			var $input = $('<select class="input mgr-10" name="type" ></select>');
			$li.append($input);
			$input.append('<option value="CLASS">Class</option>');
			$input.append('<option value="CODE">CLASS</option>');
			processor.type = processor.type || 'AND';
			$input.val(processor.type);



			if (processor.type == 'CODE') {
				var $input = $('<textarea class="input" name="processcode" />');
				$input.val(processor.processcode);
				$li.append($input);

			} else {

				$li.append('<span class="pdr-10">Class</span>');
				var $input = $('<input class="input " name="processor" />');
				$input.val(processor.processor);
				$li.append($input);

				$li.append('<span class="pdr-10">Method</span>');
				var $input = $('<input class="input " name="method" />');
				$input.val(processor.method);
				$li.append($input);

				$li.append('<span class="pdr-10">Param</span>');
				var $input = $('<input class="input " name="param" />');
				$input.val(processor.param);
				$li.append($input);

				$li.append('<span class="pdr-10"></span>');
			}



			if (coos.isEmpty(processor.ifrule)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="ifrule" property-value="1=1"  >设置条件</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="ifrule" property-value="">清空条件</a>');
			}

			if (isAfter) {
				if (coos.isTrue(processor.ignoreexception)) {
					$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="ignoreexception" >始终执行</a>');
				} else {
					$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="ignoreexception" >异常终止</a>');
				}
			}

			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				processors.splice(processors.indexOf(processor), 1);
				that.changeModel();
			});

			that.bindPropertyEvent($li, processor);
			that.bindLiEvent($subUl, processor, false);
		});
	};

})();
(function() {
	var AppEditor = coos.createClass(Editor);
	Editor.App = AppEditor;

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
				<el-form-item class label="AES密钥" prop="aeskey">
				  <el-input type="text" v-model="form.aeskey" autocomplete="off" @change="change($event,'aeskey')"></el-input>
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
	var JavaEditor = coos.createClass(Editor);
	Editor.Java = JavaEditor;

	JavaEditor.prototype.isYaml = function() {
		return true;
	};

	JavaEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-java pd-20"></div>');
		$design.append($box);

		var model = this.model;

		let html = `
		<div class="coos-row ">
		
			<h3 class="col-12 pdb-10 color-orange">Java配置</h3>
			
			<strong class="col-12 pdb-20 color-red">
			注意：生成的Java源码需要依赖teamide.base包，源码在
				<a
      href="https://gitee.com/teamide/base"
      class="coos-link color-green"
      target="_blank"
    >https://gitee.com/teamide/base</a>，请自行下载引入。
    teamide.base不定期更新，更新最新代码使用！
			</strong>
			<el-form class="col-12" :model="form" status-icon :rules="rules" ref="form" label-width="150px" size="mini">
				<div class="col-6">
					<el-form-item class label="Java代码目录" prop="javadirectory">
					  <el-input type="text" v-model="form.javadirectory" autocomplete="off" @change="change($event,'javadirectory')" placeholder="默认：src/main/java"></el-input>
					  <span class="color-grey-4">配置Java源码目录，默认：src/main/java</span>
					</el-form-item>
				</div>
				<div class="col-6">
					<el-form-item class label="资源文件目录" prop="resourcesdirectory">
					  <el-input type="text" v-model="form.resourcesdirectory" autocomplete="off" @change="change($event,'resourcesdirectory')" placeholder="默认：src/main/resources"></el-input>
					  <span class="color-grey-4">配置资源文件目录，默认：src/main/resources</span>
					</el-form-item>
				</div>
				<div class="col-12">
					<el-form-item class label="基础包名" prop="basepackage">
					  <el-input type="text" v-model="form.basepackage" autocomplete="off" @change="change($event,'basepackage')" placeholder="默认：com.teamide.app"></el-input>
					  <span class="color-grey-4">配置基础包，生成的源码在基础包下相应位置，默认：com.teamide.app</span>
					</el-form-item>
				</div>
				<div class="col-12">
					<el-form-item class label="合并目录" prop="mergedirectory">
					  <el-switch v-model="form.mergedirectory" autocomplete="off" @change="change($event,'mergedirectory')" ></el-switch>
					  <span class="color-grey-4">如果选中合并，则合并代码到一个文件中，例如：user/insert、user/update，生成的Dao为user/userDao，里边有insert和update方法</span>
					</el-form-item>
				</div>
				<div class="col-12">
					<el-form-item class label="目录包名级别" prop="directorypackagelevel">
					  <el-input type="text" v-model="form.directorypackagelevel" autocomplete="off" @change="change($event,'directorypackagelevel')" placeholder="默认：根据目录名称生成报名"></el-input>
					  <span class="color-grey-4">模型目录名称生成包名级别，如写1则只生成一级报名，子目录下文件都放在该包名下。默认根据模型的目录名称生成包名</span>
					</el-form-item>
				</div>
				<div class="col-12">
					<el-form-item class label="使用Mybatis" prop="usemybatis">
					  <el-switch v-model="form.usemybatis" autocomplete="off" @change="change($event,'usemybatis')" ></el-switch>
					  <span class="color-grey-4">如果选中，则生成mapper目录，创建Mybatis的增删改查xml</span>
					</el-form-item>
				</div>
				<div class="col-6">
					<el-form-item class label="TeamIDE基础包名" prop="teamidepackage">
					  <el-input type="text" v-model="form.teamidepackage" autocomplete="off" @change="change($event,'teamidepackage')" placeholder="默认：基础包名.teamide"></el-input>
					  <span class="color-grey-4">TeamIDE基础包，会生成一些基础文件，默认：基础包名.teamide</span>
					</el-form-item>
				</div>
				<div class="col-6">
					<el-form-item class label="Component包名" prop="componentpackage">
					  <el-input type="text" v-model="form.componentpackage" autocomplete="off" @change="change($event,'componentpackage')" placeholder="默认：基础包名.component"></el-input>
					  <span class="color-grey-4">组件包，生成SpringBoot相应组件，默认：基础包名.component</span>
					</el-form-item>
				</div>
				<div class="col-6">
					<el-form-item class label="Factory包名" prop="factorypackage">
					  <el-input type="text" v-model="form.factorypackage" autocomplete="off" @change="change($event,'factorypackage')" placeholder="默认：基础包名.factory"></el-input>
					</el-form-item>
				</div>
				<div class="col-6">
					<el-form-item class label="Bean包名" prop="beanpackage">
					  <el-input type="text" v-model="form.beanpackage" autocomplete="off" @change="change($event,'beanpackage')" placeholder="默认：基础包名.bean"></el-input>
					</el-form-item>
				</div>
				<div class="col-6">
					<el-form-item class label="Controller包名" prop="controllerpackage">
					  <el-input type="text" v-model="form.controllerpackage" autocomplete="off" @change="change($event,'controllerpackage')" placeholder="默认：基础包名.controller"></el-input>
					</el-form-item>
				</div>
				<div class="col-6">
					<el-form-item class label="Service包名" prop="servicepackage">
					  <el-input type="text" v-model="form.servicepackage" autocomplete="off" @change="change($event,'servicepackage')" placeholder="默认：基础包名.service"></el-input>
					</el-form-item>
				</div>
				<div class="col-6">
					<el-form-item class label="Dao包名" prop="daopackage">
					  <el-input type="text" v-model="form.daopackage" autocomplete="off" @change="change($event,'daopackage')" placeholder="默认：基础包名.dao"></el-input>
					</el-form-item>
				</div>
				<div class="col-6">
					<el-form-item class label="Dao包名" prop="daopackage">
					  <el-input type="text" v-model="form.daopackage" autocomplete="off" @change="change($event,'daopackage')" placeholder="默认：基础包名.dao"></el-input>
					</el-form-item>
				</div>
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
	var DatabaseEditor = coos.createClass(Editor);
	Editor.Database = DatabaseEditor;

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
					<el-form-item class label="拼接库名" prop="mustbringname">
					  <el-switch v-model="form.mustbringname" autocomplete="off" @change="change($event,'mustbringname')" ></el-switch>
					  <span class="color-grey-4">如果选中生产的SQL语句将拼接库名称</span>
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
	var TableEditor = coos.createClass(Editor);
	Editor.Table = TableEditor;

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

		$(this.getBeans('DATABASE')).each(function(index, one) {
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


		var $btn = $('<a class="mglr-10 coos-pointer color-green">导入已存在表字段</a>');
		$li.append($btn);
		$btn.click(function() {
			let app = that.getApp();
			if (app) {
				let data = {};
				data.type = "LOAD_DATABASE_TABLES";
				data.path = app.localpath;
				data.name = model.database;
				data.tablename = model.name;source.service.data.doTest(data).then(result => {
					var value = result.value;
					if (value == null) {
						coos.info('数据库没有该表存在！')
					} else {
						that.recordHistory();
						model.comment = value.comment;
						model.columns = value.columns;
						model.indexs = value.indexs;
						that.changeModel();
					}

				});
			}


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
				if (coos.isTrue(column.autoincrement)) {
					$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="autoincrement"  >是自增</a>');
				} else {
					$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="autoincrement" >非自增</a>');
				}
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

		model.indexs = model.indexs || [];
		let columnIndexs = model.indexs;
		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">索引</span>');
		var $btn = $('<a class="mglr-10 coos-pointer color-green">添加索引</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var columnIndex = {};
			columnIndexs.push(columnIndex);
			columnIndex.name = "";
			that.changeModel();
		});

		$(columnIndexs).each(function(index, columnIndex) {
			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);

			$li.append('<span class="pdlr-10">名称：</span>');
			var $input = $('<input class="input" name="name" required/>');
			$input.val(columnIndex.name);
			$li.append($input);

			$li.append('<span class="pdlr-10">表字段：</span>');
			var $input = $('<input class="input" name="column" />');
			$input.val(columnIndex.column);
			$li.append($input);

			$li.append('<span class="pdlr-10">类型：</span>');
			var $input = $('<input class="input" name="type" />');
			$input.val(columnIndex.type);
			$li.append($input);

			that.bindLiEvent($li, columnIndex, false);

			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				columnIndexs.splice(columnIndexs.indexOf(columnIndex), 1);
				that.changeModel();
			});

			var $btn = $('<a class="mgl-10 coos-pointer color-orange">上移</a>');
			$li.append($btn);
			$btn.click(function() {
				var index = columnIndexs.indexOf(columnIndex);
				if (index > 0) {
					that.recordHistory();
					columnIndexs[index] = columnIndexs.splice(index - 1, 1, columnIndexs[index])[0];
					that.changeModel();
				}
			});

			var $btn = $('<a class="mgl-10 coos-pointer color-orange">下移</a>');
			$li.append($btn);
			$btn.click(function() {
				var index = columnIndexs.indexOf(columnIndex);
				if (index < columnIndexs.length - 1) {
					that.recordHistory();
					columnIndexs[index] = columnIndexs.splice(index + 1, 1, columnIndexs[index])[0];
					that.changeModel();
				}
			});

		});
	};
})();
(function() {
	var BeanEditor = coos.createClass(Editor);
	Editor.Bean = BeanEditor;

	BeanEditor.prototype.isYaml = function() {
		return true;
	};
	BeanEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-bean editor-case"></div>');
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
		$li.append('<span class="pdr-10">绑定表：</span>');

		var $input = $('<input class="input" name="tablename" />');
		app.autocomplete({
			$input : $input,
			datas : that.getOptions('TABLE')
		})
		$input.val(model.tablename);
		$li.append($input);
		that.bindLiEvent($li, model, true);


		model.propertys = model.propertys || [];
		var propertys = model.propertys;

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">属性</span>');
		var $btn = $('<a class="mglr-10 coos-pointer color-green">添加属性</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var property = {};
			property.type = 'String';
			property.name = "";
			propertys.push(property);
			that.changeModel();
		});

		let table = that.getTableByName(model.tablename);
		if (table && table.columns && table.columns.length > 0) {
			var $btn = $('<a class="mglr-10 coos-pointer color-green">自动导入表字段</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				coos.trimArray(propertys);

				table.columns.forEach(one => {
					var property = {};
					var columntype = one.type;
					var type = 'String';
					if (columntype == '4') {
						type = 'Integer';
					} else if (columntype == '16') {
						type = 'Boolean';
					} else if (columntype == '8') {
						type = 'Double';
					} else if (columntype == '-5') {
						type = 'Long';
					}
					property.type = type;
					property.name = one.name;
					property.comment = one.comment;
					property.columnname = one.name;
					propertys.push(property);
				});
				that.changeModel();
			});
		}

		$(propertys).each(function(index, property) {
			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);

			$li.append('<span class="pdlr-10">名称：</span>');
			var $input = $('<input class="input" name="name" required/>');
			$input.val(property.name);
			$li.append($input);

			$li.append('<span class="pdlr-10">注释：</span>');
			var $input = $('<input class="input" name="comment" />');
			$input.val(property.comment);
			$li.append($input);

			$li.append('<span class="pdlr-10">类型：</span>');
			var $input = $('<select class="input" name="type" required></select>');
			$li.append($input);
			$input.append('<option value="String">String</option>');
			$input.append('<option value="Integer">Integer</option>');
			$input.append('<option value="Long">Long</option>');
			$input.append('<option value="Boolean">Boolean</option>');
			$input.append('<option value="Double">Double</option>');
			$input.append('<option value="Byte">Byte</option>');
			$input.append('<option value="Char">Char</option>');
			$input.append('<option value="int">int</option>');
			$input.append('<option value="long">long</option>');
			$input.append('<option value="char">char</option>');
			$input.append('<option value="byte">byte</option>');
			$input.append('<option value="double">double</option>');
			$input.val(property.type);
			$li.append($input);
			that.bindLiEvent($li, property);


			$li.append('<span class="pdlr-10">字段名：</span>');
			var $input = $('<input class="input " name="columnname" />');
			$input.val(property.columnname);
			$li.append($input);
			that.bindLiEvent($li, property);

			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				propertys.splice(propertys.indexOf(property), 1);
				that.changeModel();
			});

			var $btn = $('<a class="mgl-10 coos-pointer color-orange">上移</a>');
			$li.append($btn);
			$btn.click(function() {
				var index = propertys.indexOf(property);
				if (index > 0) {
					that.recordHistory();
					propertys[index] = propertys.splice(index - 1, 1, propertys[index])[0];
					that.changeModel();
				}
			});

			var $btn = $('<a class="mgl-10 coos-pointer color-orange">下移</a>');
			$li.append($btn);
			$btn.click(function() {
				var index = propertys.indexOf(property);
				if (index < propertys.length - 1) {
					that.recordHistory();
					propertys[index] = propertys.splice(index + 1, 1, propertys[index])[0];
					that.changeModel();
				}
			});

		});


	};
})();
(function() {
	var DaoEditor = coos.createClass(Editor);
	Editor.Dao = DaoEditor;

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
		$li.append('<span class="pdr-10 color-red">注意：此处值、默认值等为Jexl表达式，如果写字符串的值请用单引号，示例：\'字符串值\'。</span>');

		$li = $('<li />');
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

		$li.append('<span class="pdr-10">请求方法</span>');
		var $input = $('<select class="input mgr-10" name="requestmethod" ></select>');
		$li.append($input);
		$input.append('<option value="">全部</option>');
		$(that.ENUM_MAP.HTTP_METHOD).each(function(index, one) {
			$input.append('<option value="' + one.value + '">' + one.text + '</option>');
		});
		$input.val(model.requestmethod);
		$li.append($input);

		$li.append('<span class="pdr-10">请求ContentType</span>');
		var $input = $('<input class="input" name="requestcontenttype" />');
		$input.val(model.requestcontenttype);
		$li.append($input);


		$li.append('<span class="pdr-10">响应ContentType</span>');
		var $input = $('<input class="input" name="responsecontenttype" />');
		$input.val(model.responsecontenttype);
		$li.append($input);

		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);

		let $ul_ = $('<ul/>');
		$li.append($ul_);
		model.beforeprocessors = model.beforeprocessors || [];

		this.appendCustomProcessors($ul_, model.beforeprocessors, false);

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
		$ul_ = $('<ul/>');
		$li.append($ul_);
		model.afterprocessors = model.afterprocessors || [];

		this.appendCustomProcessors($ul_, model.afterprocessors, true);


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
	var DaoEditor = Editor.Dao;
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




	DaoEditor.prototype.appendAppends = function($ul, appends, table) {
		var that = this;
		var $li = $('<li />');
		$ul.append($li);

		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加追加SQL</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var append = {};
			appends.push(append);
			append.ifrule = '';
			append.sql = '';
			that.changeModel();
		});

		$(appends).each(function(index, append) {

			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);

			var $subUl = $('<ul class=""/>');
			$li.append($subUl);

			var $li = $('<li class=""/>');
			$subUl.append($li);


			if (!coos.isEmpty(append.ifrule)) {
				$li.append('<span class="pdlr-10">if (</span>');
				var $input = $('<input class="input input-mini" name="ifrule" />');
				$input.val(append.ifrule);
				$li.append($input);
				$li.append('<span class="pdlr-10">) //不填或值为true、1则为真</span>');

				var $li = $('<li class="pdl-30"/>');
				$subUl.append($li);

			}

			var $input = $('<input class="input" name="sql" />');
			$input.val(append.sql);
			$li.append($input);


			if (coos.isEmpty(append.ifrule)) {
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
				appends.splice(appends.indexOf(append), 1);
				that.changeModel();
			});

			that.bindPropertyEvent($li, append);
			that.bindLiEvent($subUl, append, false);
		});
	};
})();
(function() {
	var DaoEditor = Editor.Dao;

	DaoEditor.prototype.createSqlCustomSqlView = function(model) {
		var that = this;
		var $box = $('<li />');
		model = model || {};

		var $ul = $('<ul class="one-sql"/>')
		$box.append($ul);

		var $li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10 color-orange">配置data</span>');
		var $input = $('<input class="input" name="data" />');
		$input.val(model.data);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
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
	var DaoEditor = Editor.Dao;

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



		model.appends = model.appends || [];
		that.appendAppends($ul, model.appends, table);

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

				that.bindLiEvent($li, group);
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

				that.bindLiEvent($li, order);
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
	var DaoEditor = Editor.Dao;
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
		$li.append('<span class="pdr-10 color-orange">配置data</span>');
		var $input = $('<input class="input" name="data" />');
		$input.val(model.data);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10 color-orange">INSERT INTO</span>');

		var $input = $('<input class="input" name="table" />');
		app.autocomplete({
			$input : $input,
			datas : that.getOptions('TABLE')
		})
		$input.val(model.table);
		$li.append($input);
		that.bindLiEvent($li, model, true);

		var $btn = $('<a class="mglr-10 coos-pointer color-green">添加插入字段</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var column = {};
			columns.push(column);
			column.name = "";
			that.changeModel();
		});

		let table = that.getTableByName(model.table);
		if (table && table.columns && table.columns.length > 0) {
			var $btn = $('<a class="mglr-10 coos-pointer color-green">自动导入表字段</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				coos.trimArray(columns);

				table.columns.forEach(one => {
					var column = {};
					column.name = one.name;
					columns.push(column);
				});
				that.changeModel();
			});
		}

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

			if (!coos.isTrue(column.autoincrement)) {
				var $input = $('<input class="input input-mini " name="value" />');
				$input.val(column.value);
				$li.append($input);
				$li.append('<span class="pdr-10">或</span>');
				$li.append('<span class="">自动取名称相同的值  或</span>');
				var $input = $('<input class="input input-mini " name="defaultvalue" />');
				$input.val(column.defaultvalue);
				$li.append($input);
				$li.append('<span class="pdr-10"></span>');
			} else {
				$li.append('<span class="pdlr-10">表字段自增</span>');
			}

			if (coos.isEmpty(column.ifrule)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="ifrule" property-value="1=1"  >设置条件</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="ifrule" property-value="">清空条件</a>');
			}
			if (!coos.isTrue(column.autoincrement)) {
				if (!coos.isTrue(column.required)) {
					$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="required" >设为必填</a>');
				} else {
					$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="required" >设为非必填</a>');
				}
			}

			if (!coos.isTrue(column.autoincrement)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="autoincrement" >设为自增</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="autoincrement" >设为非自增</a>');
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


		model.appends = model.appends || [];
		that.appendAppends($ul, model.appends, table);

		return $box;
	};

})();
(function() {
	var DaoEditor = Editor.Dao;

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
		$li.append('<span class="pdr-10 color-orange">配置data</span>');
		var $input = $('<input class="input" name="data" />');
		$input.val(model.data);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10 color-orange">UPDATE</span>');

		var $input = $('<input class="input" name="table" />');
		app.autocomplete({
			$input : $input,
			datas : that.getOptions('TABLE')
		})
		$input.val(model.table);
		$li.append($input);
		that.bindLiEvent($li, model, true);

		var $btn = $('<a class="mglr-10 coos-pointer color-green">添加更新字段</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var column = {};
			columns.push(column);
			column.name = "";
			that.changeModel();
		});

		let table = that.getTableByName(model.table);
		if (table && table.columns && table.columns.length > 0) {
			var $btn = $('<a class="mglr-10 coos-pointer color-green">自动导入表字段</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				coos.trimArray(columns);

				table.columns.forEach(one => {
					var column = {};
					column.name = one.name;
					columns.push(column);
				});
				that.changeModel();
			});
		}

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

		wheres.is_update = true;
		that.appendWhereLi($ul, wheres, table);

		model.appends = model.appends || [];
		that.appendAppends($ul, model.appends, table);
		return $box;
	};

})();
(function() {
	var DaoEditor = Editor.Dao;

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
		$li.append('<span class="pdr-10 color-orange">配置data</span>');
		var $input = $('<input class="input" name="data" />');
		$input.val(model.data);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
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
		wheres.is_delete = true;
		that.appendWhereLi($ul, wheres, table);

		model.appends = model.appends || [];
		that.appendAppends($ul, model.appends, table);
		return $box;
	};

})();
(function() {
	var DaoEditor = Editor.Dao;

	DaoEditor.prototype.appendWhereLi = function($ul, wheres, table, isPiece) {
		let is_update = wheres.is_update;
		let is_delete = wheres.is_delete;
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
			if (is_update || is_delete) {
				where.tablealias = '';
			} else {
				where.tablealias = 'T1';
			}
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

					if (is_update || is_delete) {

					} else {
						var $input = $('<input class="input input-mini" name="tablealias" />');
						$input.val(where.tablealias);
						$li.append($input);
						$li.append('<span class="pdlr-10">.</span>');
					}
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

				where.wheres.is_delete = is_delete;
				where.wheres.is_update = is_update;
				that.appendWhereLi($ul_, where.wheres, table, true);

				var $li = $('<li class="pdl-30"/>');
				$subUl.append($li);
				$li.append(')');
			}
		});
	};

})();
(function() {
	var DaoEditor = Editor.Dao;

	DaoEditor.prototype.createHttpProcessView = function(process) {
		var that = this;

	};

})();
(function() {
	var ServiceEditor = coos.createClass(Editor);
	Editor.Service = ServiceEditor;

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

		$li.append('<span class="pdr-10">请求方法</span>');
		var $input = $('<select class="input mgr-10" name="requestmethod" ></select>');
		$li.append($input);
		$input.append('<option value="">全部</option>');
		$(that.ENUM_MAP.HTTP_METHOD).each(function(index, one) {
			$input.append('<option value="' + one.value + '">' + one.text + '</option>');
		});
		$input.val(model.requestmethod);
		$li.append($input);

		$li.append('<span class="pdr-10">请求ContentType</span>');
		var $input = $('<input class="input" name="requestcontenttype" />');
		$input.val(model.requestcontenttype);
		$li.append($input);

		$li.append('<span class="pdr-10">响应ContentType</span>');
		var $input = $('<input class="input" name="responsecontenttype" />');
		$input.val(model.responsecontenttype);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		let $ul_ = $('<ul/>');
		$li.append($ul_);
		model.beforeprocessors = model.beforeprocessors || [];

		this.appendCustomProcessors($ul_, model.beforeprocessors);

		$li = $('<li />');
		$ul.append($li);
		$ul_ = $('<ul/>');
		$li.append($ul_);
		model.afterprocessors = model.afterprocessors || [];
		this.appendCustomProcessors($ul_, model.afterprocessors, true);

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
			if ($(e.target).closest('.process-node-toolbar').length > 0) {
				return;
			}
			var $node = $(e.target).closest('.process-node');
			var menus = [];
			menus.push({
				text : "添加",
				onClick : function() {
					var $service = $design.find('.editor-service');
					that.toInsertProcess({
						top : eventData.clientY - $service.offset().top,
						left : eventData.clientX - $service.offset().left
					});
				}
			});

			if ($node.length > 0) {
				var process = $node.data('process');
				//				menus.push({
				//					text : "验证",
				//					onClick : function() {
				//						that.toViewValidate(process);
				//					}
				//				});
				//				menus.push({
				//					text : "变量",
				//					onClick : function() {
				//						that.toViewVariable(process);
				//					}
				//				});
				//				menus.push({
				//					text : "结果",
				//					onClick : function() {
				//						that.toViewResult(process);
				//					}
				//				});
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
			}

			source.repository.contextmenu.menus = menus;
			source.repository.contextmenu.callShow(e);
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
	var ServiceEditor = Editor.Service;


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
	var ServiceEditor = Editor.Service;

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
				"class-name" : ""
			}, {
				"v-if" : "form.type == 'DAO' || form.type == 'SERVICE'",
				label : "设置结果名称",
				name : "resultname",
				info : "设置存入$result的名称"
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
	var ServiceEditor = Editor.Service;

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
				if (that.lastShowProcess == process) {
					$node.addClass('show');
				}
				$node.click(function(e) {
					if ($(e.target).closest('.process-node-toolbar').length == 1) {
						return;
					}
					let $toolbar = $node.find('.process-node-toolbar');
					if ($node.hasClass('show')) {
						$node.removeClass('show');
						that.lastShowProcess = null;
					} else {
						$processBox.find('.process-node').removeClass('show');
						$node.addClass('show');
						that.lastShowProcess = process;
					}
				});
				var $toolbar = $('<div class="process-node-toolbar"/>');
				var $ul = $('<div  />');
				var $btn = $('<a class="coos-btn validate-btn coos-btn-xs color-green" title="验证">验证</a>');
				$btn.click(function(e) {
					that.toViewValidate(process);
				});
				//$ul.append($btn)
				var $btn = $('<a class="coos-btn variable-btn coos-btn-xs color-green" title="变量">变量</a>');
				$btn.click(function(e) {
					that.toViewVariable(process);
				});
				//$ul.append($btn)
				var $btn = $('<a class="coos-btn result-btn coos-btn-xs color-orange" title="结果">结果</a>');
				$btn.click(function(e) {
					that.toViewResult(process);
				});
				//$ul.append($btn);
				if (process.type == 'START' || process.type == 'END') {

				} else {
					var $btn = $('<a class="coos-btn coos-btn-xs color-green" title="设置">设置</a>');
					$btn.click(function(e) {
						that.toUpdateProcess(process);
					});
					//$ul.append($btn)
					var $btn = $('<a class="coos-btn coos-btn-xs color-red" title="删除">删除</a>');
					$btn.click(function(e) {
						that.toDeleteProcess(process);
					});
				//$ul.append($btn)
				}
				$node.on('mousedown', function(e) {
					if ($(e.target).closest('.process-node-toolbar').length > 0 ||

						$(e.target).closest('.process-variable-box').length > 0) {
						e.stopPropagation();
						return false;
					}
				});
				$toolbar.append($ul);

				var $validateBox = $('<div class="process-validate-box"/>');
				$toolbar.append($validateBox);
				that.appendValidates($validateBox, process);

				var $variableBox = $('<div class="process-variable-box"/>');
				$toolbar.append($variableBox);
				that.appendVariables($variableBox, process);

				$node.append($toolbar);
				$node.attr('id', that.getIdByProcess(process));
				$node.data('process', process);
				$node.addClass('process-node-' + process.type);
				$node.append('<div class="ring" />');
				var $content = $('<div class="content" />');
				$node.append($content);
				var $text = $('<div class="text" />');
				switch (process.type) {
				case "START":
					break;
				case "END":
					break;
				case "DECISION":
					$text.append('<span class="pdlr-0 ft-14 color-green">决策：</span>');
					//$content.append('<i class="icon coos-icon coos-icon-branches"></i>');
					break;
				case "CONDITION":
					$text.append('<span class="pdlr-0 ft-14 color-green">条件：</span>');
					//$content.append('<i class="icon coos-icon coos-icon-issuesclose"></i>');
					break;
				case "DAO":
					$text.append('<span class="pdlr-0 ft-14 color-green">Dao：</span>');
					//$content.append('<i class="icon coos-icon coos-icon-database"></i>');
					break;
				case "SUB_SERVICE":
					$text.append('<span class="pdlr-0 ft-14 color-green">子服务：</span>');
					//$content.append('<i class="icon coos-icon coos-icon-sever"></i>');
					break;
				case "ERROR_END":
					$text.append('<span class="pdlr-0 ft-14 color-green">异常结束：</span>');
					//$content.append('<i class="icon coos-icon coos-icon-sever"></i>');
					break;
				}
				if (process.image) {
					var $img = $('<img class="image"/>');
					$img.attr('src', coos.url.format(process.image));
					$content.append($img);
				}

				if (process.text) {
					$text.append(process.text);
				} else if (process.name) {
					$text.append(process.name);
				}
				$content.append($text);

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
					var process = $(arg1.el).data('process');
					if (process.top == $(arg1.el).position().top && process.left == $(arg1.el).position().left) {
						return;
					}
					that.recordHistory();
					process.top = $(arg1.el).position().top;
					process.left = $(arg1.el).position().left;
					that.changeModel(false);
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
	var ServiceEditor = Editor.Service;

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
		$box.append('<div class="title color-orange ft-13">变量（名称:值:默认值）</div>');
		var $list = $('<ul class="coos-list ft-12 pd-5  " />');
		$box.append($list);

		process.variables = process.variables || [];

		$(process.variables).each(function(index, variable) {
			var $li = $('<li class=""></li>');
			$list.append($li)

			var $card = $('<div class="coos-card "></div>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);
			if (!coos.isEmpty(variable.name)) {
				$span.text(variable.name);
			}

			$card.append('<span class="pdlr-2 ">:</span>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);

			if (!coos.isEmpty(variable.value)) {
				$span.text(variable.value);
			}

			$card.append('<span class="pdlr-2 ">:</span>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);
			if (!coos.isEmpty(variable.defaultvalue)) {
				$span.text(variable.defaultvalue);
			}

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
				label : "值（Jexl）",
				name : "value",
				"class-name" : ""
			}, {
				label : "默认值（Jexl）",
				name : "defaultvalue",
				"class-name" : ""
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
	var ServiceEditor = Editor.Service;

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
		$box.append('<div class="title color-orange ft-13">验证（值:必填:类型:表达式:正则）</div>');
		var $list = $('<ul class="coos-list ft-12 pd-5 " />');
		$box.append($list);

		process.validates = process.validates || [];

		$(process.validates).each(function(index, validate) {
			var $li = $('<li ></li>');
			$list.append($li)

			var $card = $('<div class="coos-card "></div>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);

			if (!coos.isEmpty(validate.value)) {
				$span.text(validate.value);
			}

			$card.append('<span class="pdlr-2 ">:</span>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);
			$span.text(coos.isTrue(validate.required) ? '必填' : '非必填');

			$card.append('<span class="pdlr-2 ">:</span>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);

			if (!coos.isEmpty(validate.type)) {
				$span.text(validate.type);
			}

			$card.append('<span class="pdlr-2 ">:</span>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);

			if (!coos.isEmpty(validate.rule)) {
				$span.text(validate.rule);
			}


			$card.append('<span class="pdlr-2 ">:</span>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);

			if (!coos.isEmpty(validate.pattern)) {
				$span.text(validate.pattern);
			}

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
				label : "值（Jexl）",
				name : "value",
				"class-name" : ""
			}, {
				label : "必填",
				name : "required",
				type : "switch"
			}, {
				label : "正则",
				name : "pattern"
			}, {
				label : "表达式（Jexl）",
				name : "rule",
				"class-name" : ""
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
	var ServiceEditor = Editor.Service;

	ServiceEditor.prototype.toViewResult = function(process) {
		var $node = $('#process-node-' + process.name);

	};
})();
(function() {
	var DictionaryEditor = coos.createClass(Editor);
	Editor.Dictionary = DictionaryEditor;

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
	var AttributeEditor = coos.createClass(Editor);
	Editor.Attribute = AttributeEditor;

	AttributeEditor.prototype.isYaml = function() {
		return true;
	};
	AttributeEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-attribute pd-20"></div>');
		$design.append($box);

		var model = this.model;
		let html = `
			<div class="coos-row ">
				<h3 class="pdb-10 color-orange">字段属性定义</h3>
				<el-form :model="form" status-icon :rules="rules" ref="form" label-width="150px" size="mini">
					<el-form-item class="col-6" label="响应错误码名称" prop="responseerrcode">
					  <el-input type="text" v-model="form.responseerrcode" autocomplete="off" @change="change($event,'responseerrcode')"></el-input>
					</el-form-item>
					<el-form-item class="col-6" label="响应错误信息名称" prop="responseerrcode">
					  <el-input type="text" v-model="form.responseerrcode" autocomplete="off" @change="change($event,'responseerrcode')"></el-input>
					</el-form-item>
					<el-form-item class="col-6" label="响应值名称" prop="responsevalue">
					  <el-input type="text" v-model="form.responsevalue" autocomplete="off" @change="change($event,'responsevalue')"></el-input>
					</el-form-item>
					<div class="col-12"></div>
					
					<el-form-item class="col-6" label="请求页码名称" prop="requestpageindex">
					  <el-input type="text" v-model="form.requestpageindex" autocomplete="off" @change="change($event,'requestpageindex')"></el-input>
					</el-form-item>
					<el-form-item class="col-6" label="请求每个数量名称" prop="requestpagesize">
					  <el-input type="text" v-model="form.requestpagesize" autocomplete="off" @change="change($event,'requestpagesize')"></el-input>
					</el-form-item>
					<div class="col-12"></div>
					
					<el-form-item class="col-6" label="响应页码名称" prop="responsepageindex">
					  <el-input type="text" v-model="form.responsepageindex" autocomplete="off" @change="change($event,'responsepageindex')"></el-input>
					</el-form-item>
					<el-form-item class="col-6" label="响应每个数量名称" prop="responsepagesize">
					  <el-input type="text" v-model="form.responsepagesize" autocomplete="off" @change="change($event,'responsepagesize')"></el-input>
					</el-form-item>
					
					<el-form-item class="col-6" label="响应总页数名称" prop="responsetotalpages">
					  <el-input type="text" v-model="form.responsetotalpages" autocomplete="off" @change="change($event,'responsetotalpages')"></el-input>
					</el-form-item>
					<el-form-item class="col-6" label="响应总记录数名称" prop="responsetotalcount">
					  <el-input type="text" v-model="form.responsetotalcount" autocomplete="off" @change="change($event,'responsetotalcount')"></el-input>
					</el-form-item>
					
					<el-form-item class="col-6" label="响应上一页名称" prop="responseuppage">
					  <el-input type="text" v-model="form.responseuppage" autocomplete="off" @change="change($event,'responseuppage')"></el-input>
					</el-form-item>
					<el-form-item class="col-6" label="响应下一页名称" prop="responsenextpage">
					  <el-input type="text" v-model="form.responsenextpage" autocomplete="off" @change="change($event,'responsenextpage')"></el-input>
					</el-form-item>
					
					<el-form-item class="col-12" label="响应分页报文规则" prop="responsepageresultrule">
					  <el-input type="textarea" :autosize="{minRows : 6}" v-model="form.responsepageresultrule" autocomplete="off" @change="change($event,'responsepageresultrule')"></el-input>
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
	var ControlEditor = coos.createClass(Editor);
	Editor.Control = ControlEditor;

	ControlEditor.prototype.isYaml = function() {
		return true;
	};
	ControlEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-control editor-case"></div>');
		$design.append($box);

		var model = this.model;

		var $ul = $('<ul />')
		$box.append($ul);

		var $li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10 color-red">注意：此处值、默认值等为Jexl表达式，如果写字符串的值请用单引号，示例：\'字符串值\'。</span>');

		$li = $('<li />');
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
		$li.append('<span class="pdr-10">@Controller</span>');

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">@RequestMapping("</span>');
		var $input = $('<input class="input" name="requestmapping" />');
		$input.val(model.requestmapping);
		$li.append($input);
		$li.append('<span class="pdr-10">")（不填写则不会加该注解）</span>');

		that.bindLiEvent($li, model, false);


		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">public class Controller {</span>');

		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加方法</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var method = {};
			model.methods.push(method);
			method.name = 'method_' + model.methods.length;
			that.changeModel();
		});

		model.methods = model.methods || [];

		$li = $('<li />');
		$ul.append($li);
		let $methodUl = $('<ul />');
		$li.append($methodUl);
		model.methods.forEach(method => {
			var $view = this.createMethodView(method, model.methods);
			$methodUl.append($view);
		});

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">}</span>');
	};
})();
(function() {
	var ControlEditor = Editor.Control;
	ControlEditor.prototype.createMethodView = function(method, methods) {
		var that = this;
		var $box = $('<li />');

		var $ul = $('<ul class="sub1"/>');
		$box.append($ul);

		var $li = $('<li />');
		$ul.append($li);


		$li.append('<span class="pdr-10">// </span>');
		var $input = $('<input class="input" name="comment" />');
		$input.val(method.comment);
		$li.append($input);
		that.bindLiEvent($li, method, false);


		$li = $('<li />');
		$ul.append($li);


		$li.append('<span class="pdr-10">@RequestMapping(path = "</span>');
		var $input = $('<input class="input" name="requestmapping" />');
		$input.val(method.requestmapping);
		$li.append($input);
		$li.append('<span class="pdr-10">", method = </span>');

		var $input = $('<select class="input mgr-10" name="requestmethod" ></select>');
		$li.append($input);
		$input.append('<option value="">全部</option>');
		$(that.ENUM_MAP.HTTP_METHOD).each(function(index, one) {
			$input.append('<option value="' + one.value + '">' + one.text + '</option>');
		});
		$input.val(method.requestmethod);
		$li.append($input);

		$li.append('<span class="pdr-10">) </span>');

		that.bindLiEvent($li, method, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">@ResponseBody</span>');

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">public Object </span>');
		var $input = $('<input class="input" name="name" />');
		$input.val(method.name);
		$li.append($input);

		if (coos.isTrue(method.userrequestbody)) {
			$li.append('<span class="pdr-10">(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject body) { </span>');
		} else {
			$li.append('<span class="pdr-10">(HttpServletRequest request, HttpServletResponse response) { </span>');
		}


		if (coos.isTrue(method.userrequestbody)) {
			$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="userrequestbody" property-value="false"  >不读取body</a>');
		} else {
			$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="userrequestbody" property-value="true">读取body</a>');
		}

		var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			methods.splice(methods.indexOf(method), 1);
			that.changeModel();
		});

		that.bindPropertyEvent($li, method);

		that.bindLiEvent($li, method, false);


		$li = $('<li />');
		$ul.append($li);

		let $subUl = $('<ul class="sub2"/>');
		$li.append($subUl);

		$li = $('<li />');
		$subUl.append($li);
		$li.append('<span class="pdr-10 ">Object result = null;</span>');

		$li = $('<li />');
		$subUl.append($li);
		$li.append('<span class="pdr-10 ">Object value = null;</span>');


		$li = $('<li />');
		$subUl.append($li);
		$li.append('<span class="pdr-10 ">ClientSession session = ClientHandler.getSession(request);</span>');

		$li = $('<li />');
		$subUl.append($li);
		if (coos.isTrue(method.userrequestbody)) {
			$li.append('<span class="pdr-10 ">DataParam param = new DataParam(body, session);</span>');
		} else {
			$li.append('<span class="pdr-10 ">DataParam param = new DataParam(request, session);</span>');
		}
		$li = $('<li />');
		$subUl.append($li);
		$li.append('<span class="pdr-10 ">JSONObject data = param.getData();</span>');

		$li = $('<li />');
		$subUl.append($li);
		$li.append('<span class="pdr-10 ">JSONObject variableCache = param.toVariableCache(data);</span>');


		method.variables = method.variables || [];
		method.validates = method.validates || [];
		method.processs = method.processs || [];


		$li = $('<li class="mgt-10"/>');
		$subUl.append($li);
		$li.append('<span class="pdr-10 color-orange">// 定义变量存入variableCache对象，后续只需要根据名称就能获取到值</span>');

		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var variable = {};
			method.variables.push(variable);
			that.changeModel();
		});

		method.variables.forEach(variable => {
			$li = $('<li />');
			$subUl.append($li);
			var $view = this.createMethodVariableView(variable, method.variables);
			$li.append($view);
		});



		$li = $('<li class="mgt-10"/>');
		$subUl.append($li);
		$li.append('<span class="pdr-10 color-orange">// 定义一个验证，可以验证参数抛出相应的异常</span>');

		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var validate = {};
			validate.required = true;
			method.validates.push(validate);
			that.changeModel();
		});

		method.validates.forEach(validate => {
			$li = $('<li />');
			$subUl.append($li);
			var $view = this.createMethodValidateView(validate, method.validates);
			$li.append($view);
		});


		$li = $('<li />');
		$subUl.append($li);
		$li.append('<span class="pdr-10 color-orange">// 执行Service 或者 Dao</span>');

		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var process = {};
			method.processs.push(process);
			that.changeModel();
		});

		method.processs.forEach(process => {
			$li = $('<li />');
			$subUl.append($li);
			var $view = this.createMethodProcessView(process, method.processs);
			$li.append($view);
		});


		$li = $('<li />');
		$subUl.append($li);
		$li.append('<span class="pdr-10">reutrn AppFactory.toStatus(result, exception);</span>');

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">} </span>');
		return $box;

	};
})();
(function() {
	var ControlEditor = Editor.Control;

	ControlEditor.prototype.createMethodVariableView = function(variable, variables) {
		var that = this;
		var $box = $('<li />');

		var $ul = $('<ul class=""/>');
		$box.append($ul);


		var $li = $('<li />');
		$ul.append($li);


		$li.append('<span class="pdr-10">variableCache.put("</span>');
		var $input = $('<input class="input" name="name" />');
		$input.val(variable.name);
		$li.append($input);
		$li.append('<span class="pdr-10">", AppFactory.getValueByJexlScript("</span>');

		$li.append('<span class="pdr-10"></span>');
		var $input = $('<input class="input" name="value" />');
		$input.val(variable.value);
		$li.append($input);

		$li.append('<span class="pdr-10">或默认值</span>');
		var $input = $('<input class="input" name="defaultvalue" />');
		$input.val(variable.defaultvalue);
		$li.append($input);

		$li.append('<span class="pdr-10">", variableCache) 自定义类取值</span>');
		var $input = $('<input class="input" name="valuer" />');
		$input.val(variable.valuer);
		$li.append($input);

		$li.append('<span class="pdr-10">);</span>');

		that.bindLiEvent($li, variable, false);

		var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			variables.splice(variables.indexOf(variable), 1);
			that.changeModel();
		});
		return $box;

	};
})();
(function() {
	var ControlEditor = Editor.Control;
	ControlEditor.prototype.createMethodValidateView = function(validate, validates) {
		var that = this;
		var $box = $('<li />');

		var $ul = $('<ul class="sub2"/>');
		$box.append($ul);

		var $li = $('<li />');
		$ul.append($li);

		let $baseUl = $ul;

		//		$li.append('<span class="pdr-10">判断表达式（Jexl）</span>');
		//		var $input = $('<input class="input" name="rule" />');
		//		$input.val(validate.rule);
		//		$li.append($input);

		if (!coos.isEmpty(validate.rule)) {
			$li.append('<span class="pdr-10">if (</span>');
			var $input = $('<input class="input " name="rule" />');
			$input.val(validate.rule);
			$li.append($input);
			$li.append('<span class="pdlr-10">) {//值为true、1则为真</span>');

			that.bindLiEvent($li, validate, false);

			$li = $('<li class="pdl-30"/>');
			$ul.append($li);
			$ul = $('<ul />');
			$li.append($ul);
			$li = $('<li />');
			$ul.append($li);
		} else {
			$li.append('<span class="pdr-10">value = AppFactory.getValueByJexlScript("</span>');
			var $input = $('<input class="input" name="value" />');
			$input.val(validate.value);
			$li.append($input);
			that.bindLiEvent($li, validate, false);

			$li.append('<span class="pdr-10">", variableCache);</span>');
			$li = $('<li />');
			$ul.append($li);

			$li.append('<span class="pdr-10">if (</span>');
			if (coos.isTrue(validate.required)) {
				$li.append('<span class="pdlr-10">(value == null || StringUtil.isEmptyIfStr(value))</span>');
			} else {

			}

			if (!coos.isEmpty(validate.pattern)) {
				if (coos.isTrue(validate.required)) {
					$li.append('<span class="pdlr-10"> || </span>');
				} else {
					$li.append('<span class="pdlr-10">value != null && </span>');
				}
				$li.append('<span class="pdr-10">!Pattern.matches("</span>');
				var $input = $('<input class="input" name="pattern" />');
				$input.val(validate.pattern);
				$li.append($input);
				$li.append('<span class="pdlr-10">", String.valueOf(value))</span>');
			}

			$li.append('<span class="pdlr-10">) {(</span>');

			that.bindLiEvent($li, validate, false);

			$li = $('<li class="pdl-30"/>');
			$ul.append($li);
			$ul = $('<ul />');
			$li.append($ul);
			$li = $('<li />');
			$ul.append($li);
		}

		$li.append('<span class="pdr-10">throw new FieldValidateException("</span>');
		var $input = $('<input class="input" name="errcode" />');
		$input.val(validate.errcode);
		$li.append($input);

		$li.append('<span class="pdr-10">" 或  "-1", "</span>');
		var $input = $('<input class="input" name="errmsg" />');
		$input.val(validate.errmsg);
		$li.append($input);

		that.bindLiEvent($li, validate, false);

		$li.append('<span class="pdr-10">");</span>');

		if (coos.isEmpty(validate.rule)) {
			$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="rule" property-value="1=1"  >设值表达式</a>');

			if (coos.isTrue(validate.required)) {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="required" property-value="false"  >设为非必填</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="required" property-value="true">设为必填</a>');
			}
			if (coos.isEmpty(validate.pattern)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="pattern" property-value="^*$"  >设置正则</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="pattern" property-value="">去掉正则</a>');
			}
		} else {
			$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="rule" property-value=""  >去掉表达式</a>');

		}

		that.bindPropertyEvent($li, validate);

		var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			validates.splice(validates.indexOf(validate), 1);
			that.changeModel();
		});

		$li = $('<li />');
		$baseUl.append($li);
		$li.append('<span class="pdr-10">}</span>');
		return $box;

	};
})();
(function() {
	var ControlEditor = Editor.Control;
	ControlEditor.prototype.createMethodProcessView = function(process, processs) {
		var that = this;
		var $box = $('<li />');

		var $ul = $('<ul class="sub2"/>');
		$box.append($ul);

		var $li = $('<li />');
		$ul.append($li);

		if (!coos.isEmpty(process.ifrule)) {

			$li.append('<span class="pdlr-10">if (</span>');
			var $input = $('<input class="input " name="ifrule" />');
			$input.val(process.ifrule);
			$li.append($input);
			$li.append('<span class="pdlr-10">) //不填或值为true、1则为真</span>');

			$li = $('<li class="pdl-30"/>');
			$ul.append($li);
			$ul = $('<ul />');
			$li.append($ul);
			$li = $('<li />');
			$ul.append($li);
		}

		$li.append('<span class="pdr-10">// </span>');
		var $input = $('<input class="input" name="comment" />');
		$input.val(process.comment);
		$li.append($input);
		that.bindLiEvent($li, process, true);

		$li = $('<li />');
		$ul.append($li);

		process.type = process.type || 'SERVICE';
		$li.append('<span class="pdr-10">result = </span>');
		var $input = $('<select class="input mgr-10" name="type" ></select>');
		$li.append($input);
		$input.append('<option value="SERVICE">执行Service</option>');
		$input.append('<option value="DAO">执行Dao</option>');
		$input.val(process.type);
		$li.append($input);

		if (process.type == 'SERVICE') {
			$li.append('<span class="pdr-10"></span>');
			var $input = $('<input class="input" name="servicename" />');
			app.autocomplete({
				$input : $input,
				datas : that.getOptions('SERVICE')
			})
			$input.val(process.servicename);
			$li.append($input);
		} else if (process.type == 'DAO') {
			$li.append('<span class="pdr-10"></span>');
			var $input = $('<input class="input" name="daoname" />');
			app.autocomplete({
				$input : $input,
				datas : that.getOptions('DAO')
			})
			$input.val(process.daoname);
			$li.append($input);

		}

		$li.append('<span class="pdr-10">.invoke(variableCache);</span>');

		if (coos.isEmpty(process.ifrule)) {
			$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="ifrule" property-value="1=1"  >设置条件</a>');
		} else {
			$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="ifrule" property-value="">清空条件</a>');
		}

		var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			processs.splice(processs.indexOf(process), 1);
			that.changeModel();
		});


		that.bindPropertyEvent($li, process);


		that.bindLiEvent($li, process, true);

		return $box;

	};
})();
(function() {
	var JexlEditor = coos.createClass(Editor);
	Editor.Jexl = JexlEditor;

	JexlEditor.prototype.isYaml = function() {
		return true;
	};
	JexlEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-jexl pd-20"></div>');
		$design.append($box);

		var model = this.model;

	};
})();
(function() {
	var PageEditor = coos.createClass(Editor);
	Editor.Page = PageEditor;

	PageEditor.prototype.buildDesign = function() {
		var that = this;
		var $design = this.$design;
		$design.empty();

		let model = this.model;

		let layout = model.layout || {};

		var $box = $('<div class="page-design-box"></div>');
		$design.append($box);

		this.bindPageDesignEvent($box);

	};

	PageEditor.prototype.bindPageDesignEvent = function($box) {
		let that = this;
		$box.on('contextmenu', function(e) {
			e = e || window.event;

			that.showPageDesignContextmenu(e);
			e.preventDefault();
		});


	};

	PageEditor.prototype.showPageDesignContextmenu = function(event) {
		var eventData = {
			clientX : event.clientX,
			clientY : event.clientY
		};
		var menus = [];

		menus.push({
			text : "添加",
			onClick : function() {}
		});


		source.repository.contextmenu.menus = menus;
		source.repository.contextmenu.callShow(e);
	};
})();

(function() {
	var PageEditor = Editor.Page;

	let BEAN_START = "<!-- page bean start -->";
	let BEAN_END = "<!-- page bean end -->";

	let TEMPLATE_START = "<!-- page template start -->";
	let TEMPLATE_END = "<!-- page template end -->";

	let SCRIPT_START = "<!-- page script start -->";
	let SCRIPT_END = "<!-- page script end -->";

	let OPTION_START = "<!-- vue option start -->";
	let OPTION_END = "<!-- vue option end -->";

	let STYLE_START = "<!-- page style start -->";
	let STYLE_END = "<!-- page style end -->";

	PageEditor.prototype.toCode = function(page) {
		page = page || {};
		let html = '';

		html += BEAN_START + '\n';
		html += '<page>\n';
		if (!coos.isEmpty(page.comment)) {
			html += '\t<comment>' + page.comment + '</comment>\n';
		}
		if (!coos.isEmpty(page.requestmapping)) {
			html += '\t<requestmapping>' + page.requestmapping + '</requestmapping>\n';
		}
		html += '</page>\n';
		html += BEAN_END + '\n\n';

		html += TEMPLATE_START + '\n';
		html += '<template>\n';
		if (!coos.isEmpty(page.template)) {
			html += page.template;
		}
		html += '</template>\n';
		html += TEMPLATE_END + '\n\n';

		html += SCRIPT_START + '\n';
		html += '<script>\n';
		if (!coos.isEmpty(page.script)) {
			html += page.script;
		}
		html += '</script>\n';
		html += SCRIPT_END + '\n\n';

		html += STYLE_START + '\n';
		html += '<style>\n';
		if (!coos.isEmpty(page.style)) {
			html += page.style;
		}
		html += '</style>\n';
		html += STYLE_END + '\n\n';
		return html;
	};


	PageEditor.prototype.getScriptOption = function(script) {
		let optionStr = "let option = {};";
		script = script || '';
		let start = script.indexOf(OPTION_START);
		let end = script.indexOf(OPTION_END);
		if (start >= 0 && end > start) {
			optionStr = script.substring(start + OPTION_START.length, end);
		}
		let result = toOption(optionStr);

		return result;
	};

	let toOption = function(optionStr) {
		try {
			return eval('(function(){' + optionStr + ';return option;})()');
		} catch (e) {
			console.log(e);
			coos.error(e.message);
		}
		return {};
	};
})();

(function() {
	var PageEditor = Editor.Page;
	PageEditor.prototype.bindSortable = function() {
		coos.plugin.add({
			sortable : {
				js : [ _SERVER_URL + "resources/plugins/sortable/sortable.js" ],
				css : []
			}
		});

		let that = this;
		coos.plugin.load('sortable', function() {
			that.data.uis.forEach((ui) => {
				//that.bindUISortable(ui);
			});
		});
	};

	PageEditor.prototype.bindUISortable = function(ui) {
		ui.groups.forEach((group) => {
			this.bindUIGroupSortable(ui, group);
		});
	};

	PageEditor.prototype.bindUIGroupSortable = function(ui, group) {
		group.models.forEach((model) => {
			this.bindUIModelSortable(ui, group, model);
		});
	};

	PageEditor.prototype.bindUIModelSortable = function(ui, group, model) {
		let className = ui.name + '-' + group.name + '-' + model.name;
		let $model = this.$modelBox.find('.' + className);
		let name = ui.name + '-' + group.name + '-' + model.name;
		let options = {
			sort : false,
			draggable : '>*',
			group : {
				name : name,
				pull : 'clone'
			},
			onStart : function(arg1) {
				console.log("onStart", arg1);
			},
			onEnd : function(arg1) {
				console.log("onEnd", arg1);
			},
			onSort : function(arg1) {
				console.log("onSort", arg1);
			}
		};
		$model.each(function(index, el) {

			Sortable.create(el, options);
		});


	};
})();

(function() {
	var PageEditor = Editor.Page;


	PageEditor.prototype.getUIList = function() {
		let list = [];
		list.push(this.getTagUI());
		list.push(this.getElementUI());

		return list;
	};
})();

(function() {
	var PageEditor = Editor.Page;

	PageEditor.prototype.getTagUI = function() {
		let ui = {};
		ui.name = 'base';
		ui.title = 'Base UI';

		ui.groups = this.getBaseUIGroups();

		return ui;
	};

	let item_map = {};

	item_map.text = {
		label : "文案",
		name : "text"
	};
	item_map.title = {
		label : "标题",
		name : "title"
	};
	item_map.label = {
		label : "标签",
		name : "label"
	};
	item_map.color = {
		label : "颜色",
		name : "color",
		type : "color"
	};
	item_map.bgcolor = {
		label : "背景颜色",
		name : "bgcolor",
		type : "color"
	};
	item_map.bdcolor = {
		label : "边框颜色",
		name : "bdcolor",
		type : "color"
	};
	PageEditor.prototype.getBaseUIGroups = function() {
		let groups = [];
		groups.push({
			name : 'base',
			title : '基础',
			models : this.getBaseUIBaseModels()
		});


		return groups;
	};
	let data_names = [ 'class', ':class', 'style', ':style', 'name', ':name', 'v-for', 'v-show', 'v-if', 'v-else-if', 'v-else' ];
	let getData = function(el) {
		let data = {};
		if (el == null) {
			return data;
		}
		let $el = $(el);
		data_names.forEach((data_name) => {
			data[data_name] = $el.attr(data_name);
		});
		return data;
	};
	let setData = function(el, data) {
		if (el == null || data == null) {
			return;
		}
		let $el = $(el);
		data_names.forEach((data_name) => {
			if (!coos.isUndefined(data[data_name])) {
				$el.attr(data_name, data[data_name])
			}
		});
	};


	PageEditor.prototype.getBaseUIBaseModels = function() {
		let models = [];
		models.push({
			name : 'layout',
			title : '布局',
			items : [ item_map.color, item_map.bgcolor, item_map.bdcolor ],
			demos : [ {
				isBlock : true,
				html : '<div class="coos-layout"></div>'
			} ],
			eq (el) {
				if ($(el).hasClass('coos-layout')) {
					return true;
				}
			},
			getData (el) {
				let data = getData(el);

				return data;
			},
			setData (el, data) {
				setData(el, data);
			}
		});
		models.push({
			name : 'panel',
			title : '面板',
			items : [ item_map.title, item_map.color, item_map.bgcolor, item_map.bdcolor ],
			demos : [ {
				isBlock : true,
				html : `
				<div class="coos-panel">
					<div class="coos-panel-header">标题</div>
					<div class="coos-panel-body">内容</div>
					<div class="coos-panel-footer">底部</div>
				</div>
				`
			} ],
			eq (el) {
				if ($(el).hasClass('coos-panel')) {
					return true;
				}
			},
			getData (el) {
				let data = getData(el);

				return data;
			},
			setData (el, data) {
				setData(el, data);
			}
		});
		let demos = [];
		let tags = [ 'h1', 'h2', 'h3', 'h4', 'h5', 'div', 'span', 'p', 'a' ];
		models.push({
			name : 'base',
			title : '基础标签',
			demos : demos,
			eq (el) {
				let flag = false;
				tags.forEach((tag) => {
					if ($(el).hasClass('coos-tag-' + tag)) {
						flag = true;
					}
				});
				return flag;
			},
			getData (el) {
				let data = getData(el);

				return data;
			},
			setData (el, data) {
				setData(el, data);
			}
		});
		tags.forEach((tag) => {
			demos.push({
				html : '<' + tag + ' class="coos-tag-' + tag + '">这是一个' + tag + '</' + tag + '>'
			});
		});

		models.push(this.getBaseUIButton());
		models.push(this.getBaseUILink());
		return models;
	};

	PageEditor.prototype.getBaseUIButton = function() {
		let demos = [];

		let list = [ {
			text : '默认'
		}, {
			text : 'grey',
			color : 'grey'
		}, {
			text : 'green',
			color : 'green'
		}, {
			text : 'blue',
			color : 'blue'
		}, {
			text : 'orange',
			color : 'orange'
		}, {
			text : 'red',
			color : 'red'
		} ];

		[ 'xs', 'sm', '', 'md', 'lg' ].forEach(one => {
			let template = '';
			template += '<a class="coos-btn color-grey ';
			if (one != null) {
				template += 'coos-btn-' + one + ' ';
			}
			template += ' " >';
			template += one || '默认';
			template += '</a>';
			demos.push({
				data : null,
				html : template
			})
		});

		list.forEach(one => {
			let template = '';
			template += '<a class="coos-btn ';
			if (one.color) {
				template += 'color-' + one.color + ' ';
			}
			template += ' " >';
			template += one.text;
			template += '</a>';
			demos.push({
				data : null,
				html : template
			})
		});

		list.forEach(one => {
			let template = '';
			template += '<a class="coos-btn ';
			if (one.color) {
				template += 'bg-' + one.color + ' ';
			}
			template += ' " >';
			template += one.text;
			template += '</a>';
			demos.push({
				data : null,
				html : template
			})
		});

		return {
			name : 'button',
			title : '按钮',
			demos : demos,
			eq (el) {
				if ($(el).hasClass('coos-btn')) {
					return true;
				}
				if ($(el).hasClass('coos-link')) {
					return true;
				}
			},
			getData (el) {
				let data = getData(el);

				return data;
			},
			setData (el, data) {
				setData(el, data);
			}
		};
	};


	PageEditor.prototype.getBaseUILink = function() {
		let demos = [];

		let list = [ {
			text : '默认'
		}, {
			text : 'grey',
			color : 'grey'
		}, {
			text : 'green',
			color : 'green'
		}, {
			text : 'blue',
			color : 'blue'
		}, {
			text : 'orange',
			color : 'orange'
		}, {
			text : 'red',
			color : 'red'
		} ];

		list.forEach(one => {
			let template = '';
			template += '<a class="coos-btn ';
			if (one.color) {
				template += 'color-' + one.color + ' ';
			}
			template += ' " >';
			template += one.text;
			template += '</a>';
			demos.push({
				data : null,
				html : template
			})
		});

		list.forEach(one => {
			let template = '';
			template += '<a class="coos-btn ';
			if (one.color) {
				template += 'bg-' + one.color + ' ';
			}
			template += ' " >';
			template += one.text;
			template += '</a>';
			demos.push({
				data : null,
				html : template
			})
		});



		list.forEach(one => {
			let template = '';
			template += '<a class="coos-link ';
			if (one.color) {
				template += 'color-' + one.color + ' ';
			}
			template += ' " >';
			template += one.text;
			template += '</a>';
			demos.push({
				data : null,
				html : template
			})
		});

		return {
			name : 'link',
			title : '链接',
			demos : demos,
			eq (el) {
				if ($(el).hasClass('coos-link')) {
					return true;
				}
			},
			getData (el) {
				let data = getData(el);

				return data;
			},
			setData (el, data) {
				setData(el, data);
			}
		};
	};
})();

(function() {
	var PageEditor = Editor.Page;

	PageEditor.prototype.getElementUI = function() {
		let ui = {};
		ui.name = 'element-ui';
		ui.title = 'Element UI';


		ui.groups = this.getElementUIGroups();

		return ui;
	};
	PageEditor.prototype.getComponentHTML = function(template, data, callback) {
		data = data || {};
		return new Vue({
			data : data,
			el : $('<div />').append(template)[0],
			mounted () {
				this.$nextTick(() => {
					callback && callback(this.$el.innerHTML);
				});
			}
		}).$el.innerHTML;
	};

	PageEditor.prototype.getElementUIGroups = function() {
		let groups = [];
		groups.push({
			name : 'base',
			title : '基础',
			models : [ this.getElementUIButton(), this.getElementUILink() ]
		});

		groups.push({
			name : 'form',
			title : '表单',
			models : [
				this.getElementUIInput()
				, this.getElementUISelect()
				, this.getElementUIRadio()
				, this.getElementUICheckbox()
				, this.getElementUIInputNumber()
				, this.getElementUISwitch()
				, this.getElementUISlider()
				, this.getElementUITimePicker()
				, this.getElementUIDatePicker()
				, this.getElementUIRate()
				, this.getElementUIColorPicker()
				, this.getElementUIForm()
			]
		});

		groups.push({
			name : 'data',
			title : '数据',
			models : [
				this.getElementUITable()
				, this.getElementUITag()
				, this.getElementUIProgress()
				, this.getElementUITree()
				, this.getElementUIPagination()
				, this.getElementUIBadge()
			]
		});
		return groups;
	};

	PageEditor.prototype.getElementUIButton = function() {
		let demos = [];

		let list = [ {
			text : '默认'
		}, {
			text : '主要',
			type : 'primary'
		}, {
			text : '成功',
			type : 'success'
		}, {
			text : '信息',
			type : 'info'
		}, {
			text : '警告',
			type : 'warning'
		}, {
			text : '危险',
			type : 'danger'
		} ];


		list.forEach(one => {
			let template = '';
			template += '<el-button size="mini" ';
			if (one.type) {
				template += 'type="' + one.type + '" ';
			}
			template += ' >';
			template += one.text;
			template += '</el-button>';
			demos.push({
				template : template,
				data : null,
				html : this.getComponentHTML(template)
			})
		});
		let template = '<el-button plain size="mini">朴素</el-button>';
		demos.push({
			template : template,
			data : null,
			html : this.getComponentHTML(template)
		})
		template = '<el-button type="primary" plain size="mini">主要</el-button>';
		demos.push({
			template : template,
			data : null,
			html : this.getComponentHTML(template)
		})

		return {
			name : 'button',
			title : '按钮',
			demos : demos,
			eq (el) {
				if ($(el)[0].tagName.toLowerCase() == ('el-button')) {
					return true;
				}
			}
		};
	};
	PageEditor.prototype.getElementUILink = function() {

		let demos = [];

		let list = [ {
			text : '默认'
		}, {
			text : '主要',
			type : 'primary'
		}, {
			text : '成功',
			type : 'success'
		}, {
			text : '信息',
			type : 'info'
		}, {
			text : '警告',
			type : 'warning'
		}, {
			text : '危险',
			type : 'danger'
		} ];
		list.forEach(one => {
			let template = '';
			template += '<el-link ';
			if (one.type) {
				template += 'type="' + one.type + '" ';
			}
			template += ' >';
			template += one.text;
			template += '</el-link>';
			demos.push({
				template : template,
				data : null,
				html : this.getComponentHTML(template)
			})
		});


		return {
			name : 'link',
			title : '文字链接',
			demos : demos,
			eq (el) {
				if ($(el)[0].tagName.toLowerCase() == ('el-link')) {
					return true;
				}
			}
		};
	};
	PageEditor.prototype.getElementUIRadio = function() {
		let demos = [];
		let template = '<el-radio label="1">选项</el-radio>';
		demos.push({
			template : template,
			data : null,
			html : this.getComponentHTML(template)
		});
		return {
			name : 'radio',
			title : '单选框',
			demos : demos,
			eq (el) {
				if ($(el)[0].tagName.toLowerCase() == ('el-radio')) {
					return true;
				}
			}
		};
	};

	PageEditor.prototype.getElementUICheckbox = function() {
		let demos = [];
		let template = '<el-checkbox label="1">选项</el-checkbox>';
		demos.push({
			template : template,
			data : null,
			html : this.getComponentHTML(template)
		});
		return {
			name : 'checkbox',
			title : '复选框',
			demos : demos,
			eq (el) {
				if ($(el)[0].tagName.toLowerCase() == ('el-checkbox')) {
					return true;
				}
			}
		};
	};
	PageEditor.prototype.getElementUIInput = function() {
		let demos = [];
		let template = '<el-input placeholder="请输入内容"></el-input>';
		demos.push({
			template : template,
			data : null,
			html : this.getComponentHTML(template)
		});
		return {
			name : 'input',
			title : '输入框',
			demos : demos,
			eq (el) {
				if ($(el)[0].tagName.toLowerCase() == ('el-input')) {
					return true;
				}
			}
		};
	};
	PageEditor.prototype.getElementUIInputNumber = function() {
		let demos = [];
		let template = '<el-input-number style="width: 100%;" :min="1" :max="10" label="描述文字"></el-input-number>';
		demos.push({
			template : template,
			data : null,
			html : this.getComponentHTML(template)
		});
		return {
			name : 'input-number',
			title : '计数器',
			demos : demos,
			eq (el) {
				if ($(el)[0].tagName.toLowerCase() == ('el-input-number')) {
					return true;
				}
			}
		};
	};
	PageEditor.prototype.getElementUISelect = function() {
		let demos = [];
		let template = '<el-select placeholder="请选择"><el-option label="选项1" value="值1"></el-option></el-select>';
		demos.push({
			template : template,
			data : null,
			html : this.getComponentHTML(template)
		});
		return {
			name : 'select',
			title : '选择器',
			demos : demos,
			eq (el) {
				if ($(el)[0].tagName.toLowerCase() == ('el-select')) {
					return true;
				}
			}
		};
	};

	PageEditor.prototype.getElementUISwitch = function() {
		let demos = [];
		let template = '<el-switch active-color="#13ce66" inactive-color="#ff4949"></el-switch>';
		demos.push({
			template : template,
			data : null,
			html : this.getComponentHTML(template)
		});
		return {
			name : 'switch',
			title : '开关',
			demos : demos,
			eq (el) {
				if ($(el)[0].tagName.toLowerCase() == ('el-switch')) {
					return true;
				}
			}
		};
	};

	PageEditor.prototype.getElementUISlider = function() {
		let demos = [];
		let template = '<el-slider ></el-slider>';
		demos.push({
			isBlock : true,
			template : template,
			data : null,
			html : this.getComponentHTML(template)
		});
		return {
			name : 'slider',
			title : '滑块',
			demos : demos,
			eq (el) {
				if ($(el)[0].tagName.toLowerCase() == ('el-slider')) {
					return true;
				}
			}
		};
	};

	PageEditor.prototype.getElementUITimePicker = function() {
		let demos = [];
		let template = '<el-time-select style="width: 100%;" placeholder="选择时间"></el-time-select>';
		demos.push({
			template : template,
			data : null,
			html : this.getComponentHTML(template)
		});
		return {
			name : 'time-select',
			title : '时间选择器',
			demos : demos,
			eq (el) {
				if ($(el)[0].tagName.toLowerCase() == ('el-time-select')) {
					return true;
				}
			}
		};
	};

	PageEditor.prototype.getElementUIDatePicker = function() {
		let demos = [];
		let template = '<el-date-picker style="width: 100%;" placeholder="选择日期"></el-date-picker>';
		demos.push({
			template : template,
			data : null,
			html : this.getComponentHTML(template)
		});
		return {
			name : 'date-picker',
			title : '日期选择器',
			demos : demos,
			eq (el) {
				if ($(el)[0].tagName.toLowerCase() == ('el-date-picker')) {
					return true;
				}
			}
		};
	};

	PageEditor.prototype.getElementUIRate = function() {
		let demos = [];
		let template = '<el-rate value="1"></el-rate>';
		demos.push({
			template : template,
			data : null,
			html : this.getComponentHTML(template)
		});
		return {
			name : 'rate',
			title : '评分',
			demos : demos,
			eq (el) {
				if ($(el)[0].tagName.toLowerCase() == ('el-rate')) {
					return true;
				}
			}
		};
	};


	PageEditor.prototype.getElementUIColorPicker = function() {
		let demos = [];
		let template = '<el-color-picker value="#409EFF"></el-color-picker>';
		demos.push({
			template : template,
			data : null,
			html : this.getComponentHTML(template)
		});
		return {
			name : 'color-picker',
			title : '颜色选择器',
			demos : demos,
			eq (el) {
				if ($(el)[0].tagName.toLowerCase() == ('el-color-picker')) {
					return true;
				}
			}
		};
	};

	PageEditor.prototype.getElementUIForm = function() {
		let demos = [];
		let template = '';
		template += '<el-form ref="form" label-width="60px">';
		template += '<el-form-item label="文本">';
		template += '<el-input ></el-input>';
		template += '</el-form-item>';
		template += '</el-form>';
		demos.push({
			template : template,
			data : null,
			html : this.getComponentHTML(template)
		});
		return {
			name : 'form',
			title : '表单',
			demos : demos,
			eq (el) {
				if ($(el)[0].tagName.toLowerCase() == ('el-form')) {
					return true;
				}
			}
		};
	};
	PageEditor.prototype.getElementUITable = function() {
		let demos = [];
		let template = '';
		let data = `
		[ {
			value1 : '值1',
			value2 : '值2'
		}, {
			value1 : '值1',
			value2 : '值2'
		}, {
			value1 : '值1',
			value2 : '值2'
		} ]
		`;
		template += '<el-table :data="' + data + '" style="width: 100%">';
		template += '<el-table-column prop="value1" label="标题1" ></el-table-column>';
		template += '<el-table-column prop="value2" label="标题2" ></el-table-column>';
		template += '</el-table>';
		demos.push({
			isBlock : true,
			template : template,
			data : null,
			html : this.getComponentHTML(template, {}, function(html) {
				demos[0].html = html;
			})
		});
		return {
			name : 'table',
			title : '表格',
			demos : demos,
			eq (el) {
				if ($(el)[0].tagName.toLowerCase() == ('el-table')) {
					return true;
				}
			}
		};
	};


	PageEditor.prototype.getElementUITag = function() {
		let demos = [];
		let list = [ {
			text : '标签'
		}, {
			text : '标签',
			type : 'primary'
		}, {
			text : '标签',
			type : 'success'
		}, {
			text : '标签',
			type : 'info'
		}, {
			text : '标签',
			type : 'warning'
		}, {
			text : '标签',
			type : 'danger'
		} ];
		list.forEach(one => {
			let template = '';
			template += '<el-tag ';
			if (one.type) {
				template += 'type="' + one.type + '" ';
			}
			template += ' >';
			template += one.text;
			template += '</el-tag>';
			demos.push({
				template : template,
				data : null,
				html : this.getComponentHTML(template)
			})
		});

		return {
			name : 'tag',
			title : '标签',
			demos : demos,
			eq (el) {
				if ($(el)[0].tagName.toLowerCase() == ('el-tag')) {
					return true;
				}
			}
		};
	};


	PageEditor.prototype.getElementUIProgress = function() {
		let demos = [];
		let list = [ {
			text : '标签'
		}, {
			text : '标签',
			type : 'success'
		}, {
			text : '标签',
			type : 'warning'
		}, {
			type : 'exception'
		} ];

		list.forEach(one => {
			let template = '';
			template += '<el-progress percentage="50">';
			if (one.type) {
				template += 'status="' + one.type + '" ';
			}
			template += ' >';
			template += '</el-progress>';
			demos.push({
				isBlock : true,
				template : template,
				data : null,
				html : this.getComponentHTML(template)
			})
		});

		return {
			name : 'progress',
			title : '进度条',
			demos : demos,
			eq (el) {
				if ($(el)[0].tagName.toLowerCase() == ('el-progress')) {
					return true;
				}
			}
		};
	};


	PageEditor.prototype.getElementUITree = function() {
		let data = `
		[ {
			label : '一级 1',
			children : [ {
				label : '二级 1-1',
				children : [ {
					label : '三级 1-1-1'
				} ]
			} ]
		}, {
			label : '一级 2',
			children : [ {
				label : '二级 2-1',
				children : [ {
					label : '三级 2-1-1'
				} ]
			}, {
				label : '二级 2-2',
				children : [ {
					label : '三级 2-2-1'
				} ]
			} ]
		}, {
			label : '一级 3',
			children : [ {
				label : '二级 3-1',
				children : [ {
					label : '三级 3-1-1'
				} ]
			}, {
				label : '二级 3-2',
				children : [ {
					label : '三级 3-2-1'
				} ]
			} ]
		} ]
		`;
		let defaultProps = `
			{
				children : 'children',
				label : 'label'
			}
			`;
		let template = '<el-tree :data="' + data + '" :props="' + defaultProps + '" ></el-tree>';
		let demos = [];
		demos.push({
			isBlock : true,
			template : template,
			data : null,
			html : this.getComponentHTML(template, {})
		});
		return {
			name : 'tree',
			title : '树',
			demos : demos,
			eq (el) {
				if ($(el)[0].tagName.toLowerCase() == ('el-tree')) {
					return true;
				}
			}
		};
	};
	PageEditor.prototype.getElementUIPagination = function() {
		let demos = [];
		let template = '<el-pagination layout="prev, pager, next" :total="50"></el-pagination>';
		demos.push({
			template : template,
			data : null,
			html : this.getComponentHTML(template, {})
		});
		template = '<el-pagination layout="prev, pager, next" :total="1000"></el-pagination>';
		demos.push({
			isBlock : true,
			template : template,
			data : null,
			html : this.getComponentHTML(template, {})
		});
		return {
			name : 'pagination',
			title : '分页',
			demos : demos,
			eq (el) {
				if ($(el)[0].tagName.toLowerCase() == ('el-pagination')) {
					return true;
				}
			}
		};
	};

	PageEditor.prototype.getElementUIBadge = function() {
		let demos = [];
		let template = '<el-badge :value="12" class="item"><el-button size="small">评论</el-button></el-badge>';
		demos.push({
			template : template,
			data : null,
			html : this.getComponentHTML(template, {})
		});
		template = '<el-badge :value="12" class="item" type="primary"><el-button size="small">评论</el-button></el-badge>';
		demos.push({
			template : template,
			data : null,
			html : this.getComponentHTML(template, {})
		});
		template = '<el-badge :value="12" class="item" type="warning"><el-button size="small">评论</el-button></el-badge>';
		demos.push({
			template : template,
			data : null,
			html : this.getComponentHTML(template, {})
		});
		return {
			name : 'badge',
			title : '标记',
			demos : demos,
			eq (el) {
				if ($(el)[0].tagName.toLowerCase() == ('el-badge')) {
					return true;
				}
			}
		};
	};


})();

(function() {
	var PageEditor = Editor.Page;

	PageEditor.prototype.getModelBoxHtml = function() {
		let html = `
		<div class="title">UI 模板  <a @click="closeModelBox()" class="ft-12 float-right coos-pointer">关闭</a></div>

		<div class="ui-list">
		<el-collapse v-for="ui in uis" v-model="ui_active_name" @change="ui_active_change" accordion>
		<el-collapse-item :title="ui.title" :name="ui.name" >

		<div class="ui-group-list">

		<el-collapse v-for="group in ui.groups" :class="ui.name + '-' + group.name" v-model="ui_group_active_name" @change="ui_group_active_change" accordion>
		<el-collapse-item :title="group.title" :name="ui.name + '-' + group.name">

		<div class="ui-model-list coos-scrollbar" >

		<div v-for="model in group.models" class="ui-model-one" :class="ui.name + '-' + group.name + '-' + model.name">
		<div v-if="model.demos != null" v-for="demo in model.demos" class="ui-model-demo" @dblclick="chooseModel(ui, group, model ,demo)" :class="{'ui-model-demo-block' : demo.isBlock}" v-html="demo.html">
		</div>
		<div class="ui-model-title"><el-link type="success">{{model.title}}</el-link></div>
		</div>

		</div>

		</el-collapse-item>
		</el-collapse>

		</div>
		</el-collapse-item>
		</el-collapse>
		</div>
		`;
		return html;
	};

	PageEditor.prototype.buildModel = function() {};

	PageEditor.prototype.showAppendModel = function($parent) {
		this.lastAppendParent = $parent;
		this.$modelBox.addClass('show');
	};

	PageEditor.prototype.closeAppendModel = function() {
		this.$modelBox.removeClass('show');
	};

	PageEditor.prototype.chooseModel = function(ui, group, model, demo) {
		let $parent = $(this.lastAppendParent);
		if (coos.isEmpty(demo.template)) {
			$parent.append(demo.html);
		} else {
			$parent.append(demo.template);
		}

		this.changePage();
		//		this.closeAppendModel();


	};
})();

(function() {
	var PageEditor = Editor.Page;

	PageEditor.prototype.getPageBoxHtml = function() {};

	PageEditor.prototype.buildPage = function(page) {
		if (this.page && page) {
			if (this.page.template == page.template
				&& this.page.script == page.script
				&& this.page.style == page.style) {
				return;
			}
		}
		let index = '0';
		if (this.$template) {
			if (this.$template.hasClass('coos-choose-page-model')) {
				index = this.$template.attr('coos-index');
			} else {
				index = this.$template.find('.coos-choose-page-model').attr('coos-index');
			}
		}
		if (page != null) {
			this.page = page ;
			this.initTemplate();
			this.initScript();
		}

		let that = this;

		let $pageBox = this.$pageBox;
		$pageBox.empty();
		$pageBox.append(this.$template);

		let el = this.$template[0];
		this.scriptOption.el = el;
		let vue = new Vue(this.scriptOption);

		this.bindPageEvent();
		let e;
		if (index == '0') {
			e = $(vue.$el);
		} else {
			e = $(vue.$el).find('[coos-index="' + index + '"]');
		}
		if (e.length > 0) {
			this.clickPage(e[0], index);
		}

	};

	PageEditor.prototype.changePage = function() {
		let that = this;

		this.bindTemplateEvent(this.$template);

		let $t = that.$template.clone();

		$t.find('.coos-choose-page-model').removeClass('coos-choose-page-model');
		$t.removeClass('coos-choose-page-model');

		this.removeTemplateEvent($t);

		let template = $t[0].outerHTML;
		template = '\t' + template + '\n';
		let script = that.page.script;
		let style = that.page.style;

		let page = {};
		Object.assign(page, that.page);
		page.template = template;
		page.style = style;
		page.script = script;
		let code = that.toCode(page);

		//let page_ = that.options.onChange(page, code);
		that.refreshPage();
	};


	PageEditor.prototype.refreshPage = function() {
		this.buildPage();
	};


})();

(function() {
	var PageEditor = Editor.Page;


	PageEditor.prototype.bindPageEvent = function() {
		let $pageBox = this.$pageBox;
		let $template = this.$template;
		let that = this;
		$pageBox.unbind('click').on('click', function() {
			that.clickPage($pageBox.find('.coos-page:first'), "0");
		});

	};

	PageEditor.prototype.add_child = function() {
		this.showAppendModel($(this.lastChoosePageTemplate));
	};
	PageEditor.prototype.add_before = function() {
		this.showAppendModel($(this.lastChoosePageTemplate));
	};
	PageEditor.prototype.add_after = function() {
		this.showAppendModel($(this.lastChoosePageTemplate));
	};
	PageEditor.prototype.choose_pro = function() {
		this.showAppendModel($(this.lastChoosePageTemplate));
	};
	PageEditor.prototype.choose_next = function() {
		this.showAppendModel($(this.lastChoosePageTemplate));
	};
	PageEditor.prototype.move_up = function() {
		let $el = $(this.lastChoosePageTemplate);
		var list = $el.parent().children();
		var thisLocation = list.index($el);
		if (thisLocation < 1) {

		} else {
			let $prev = $el.prev();
			$prev.before($el); //上移动
			this.changePage();
		}
	};
	PageEditor.prototype.move_dw = function() {
		let $el = $(this.lastChoosePageTemplate);
		var list = $el.parent().children();
		var thisLocation = list.index($el);
		if (thisLocation >= list.length - 1) {

		} else {

			let $next = $el.next();
			$next.after($el); //下移动
			this.changePage();
		}
	};
	PageEditor.prototype.remove = function() {
		$(this.lastChoosePageTemplate).remove();
		this.changePage();
	};

	PageEditor.prototype.initChoosePlaces = function(element) {
		let places = this.form_data.places;
		places.splice(0, places.length);
		function addPlace(el) {
			let $el = $(el);
			if ($el.length == 0) {

				return;
			}
			if ($el.parent().hasClass('page-editor-page-box')) {
				return;
			}
			addPlace($el.parent());
			places.push({
				name : $el[0].tagName,
				el : $el[0]
			});
		}

		addPlace(element);
	};
	PageEditor.prototype.initChooseAction = function() {};

	PageEditor.prototype.clickPage = function(el, index) {
		let remark = '';
		let model = null;
		let $el = null;
		if (index) {
			$el = this.$template.find('[coos-index="' + index + '"]');
			if (index == '0') {
				$el = this.$template;
			}
			model = $el.data('model');
			let ui = $el.data('ui');
			let group = $el.data('group');
			if (ui) {
				remark += ui.title + "/";
			}
			if (group) {
				remark += group.title + "/";
			}
			if (model) {
				remark += model.title;
			}
		}
		this.choosePageTemplate($el, model);
		this.form_data.remark = remark;

		this.$template.find('.coos-choose-page-model').removeClass('coos-choose-page-model');
		this.$template.removeClass('coos-choose-page-model');
		$el.addClass('coos-choose-page-model');

		this.$pageBox.find('.coos-choose-page-model').removeClass('coos-choose-page-model');
		$(el).addClass('coos-choose-page-model');
	};
	PageEditor.prototype.choosePageTemplate = function($el, model) {
		this.lastChoosePageTemplate = $el;
		model = model || {};
		let data = {};
		let items = [];
		if (model.items) {
			items = model.items;
		}
		if (model.getData) {
			data = model.getData($el);
		}
		let $form = $(this.getFormBoxHtml());
		this.$formBox.empty();
		this.$formBox.append($form);
		this.form_data = {
			remark : "",
			data : data,
			rules : {},
			items : items,
			places : [],
			has_add_child : true,
			has_add_before : true,
			has_add_after : true,
			has_move_up : true,
			has_move_dw : true,
			has_remove : true,
			open_base : false
		}
		let that = this;
		let vue = new Vue({
			el : $form[0],
			data : this.form_data,
			methods : {
				add_child () {
					that.add_child();
				},
				add_before () {
					that.add_before();
				},
				add_after () {
					that.add_after();
				},
				move_up () {
					that.move_up();
				},
				move_dw () {
					that.move_dw();
				},
				remove () {
					that.remove();
				},
				formDataChange (value, name) {
					that.formDataChange(value, name);
				},
				clickPlace (place) {
					console.log(place);
				}
			}
		});

		this.initChoosePlaces($el);
		this.initChooseAction($el);
	};

	PageEditor.prototype.formDataChange = function(value, name) {
		let model = $(this.lastChoosePageTemplate).data('model');
		if (model && model.setData) {
			model.setData(this.lastChoosePageTemplate, this.form_data.data);
			this.changePage();
		}
	};

})();

(function() {
	var PageEditor = Editor.Page;

	PageEditor.prototype.initTemplate = function() {

		this.$template = $('<div class="coos-page"/>');
		this.$template.html(this.page.template);
		if (this.$template.find('.coos-page').length > 0) {
			this.$template = $(this.page.template);
		}


		let that = this;

		let click_page_method_name = 'click_page_' + coos.getNumber();
		this.click_page_method_name = click_page_method_name;
		window[click_page_method_name] = function(event, index) {
			window.event.preventDefault();
			window.event.stopPropagation();
			that.clickPage(event.currentTarget, index);
		};
		this.bindTemplateEvent(this.$template);


	};

	PageEditor.prototype.bindTemplateEvent = function($template) {
		this.removeTemplateEvent($template);
		let that = this;
		let uis = this.data.uis;
		let index = 0;
		function bind(el) {
			let $el = $(el);
			uis.forEach((ui) => {
				ui.groups.forEach((group) => {
					group.models.forEach((model) => {
						if (model.eq && model.eq($el)) {
							index++;
							$el.data("ui", ui);
							$el.data("group", group);
							$el.data("model", model);
							let oldClick = $el.attr('v-on:click');
							$el.attr('coos-index', '' + index);
							$el.attr('v-on:click', that.click_page_method_name + '($event, "' + index + '")');
							if (!coos.isEmpty(oldClick)) {
								$el.attr('v-on:old-click', oldClick)
							}
						}
					});
				});
			});
			$el.children().each(function(i, element) {
				bind(element);
			});
		}
		$template.attr('coos-index', '' + index);
		$template.data("model", {
			title : "页面"
		});
		bind($template);
	};

	PageEditor.prototype.removeTemplateEvent = function($template) {
		function remove(el) {
			let $el = $(el);
			if ($el.attr('coos-index')) {
				$el.removeAttr('coos-index')
				$el.removeAttr('v-on:click')
				let oldClick = $el.attr('v-on:old-click');
				if (!coos.isEmpty(oldClick)) {
					$el.attr('v-on:click', oldClick);
				}
			}
			$el.children().each(function(i, element) {
				remove(element);
			});
		}

		remove($template);
	};


})();

(function() {
	var PageEditor = Editor.Page;

	PageEditor.prototype.getFormBoxHtml = function() {
		let html = `
		<div class="">
		<div class="title">设置</div>
		<div class="pd-5">
			<div class="remark color-grey">UI：{{remark}}
			</div>
			<div class="place color-grey">位置：HTML
				<template v-for="place in places"><span>&gt;</span><a class="coos-link color-green" @click="clickPlace(place)">{{place.name}}</a></template>
			</div>
			<div class="">
				<a class="coos-btn coos-btn-xs color-green mgr-5 mgb-5" :class="{'coos-disabled' : !has_add_child}" @click="add_child()">添加子模块</a>
				<a class="coos-btn coos-btn-xs color-green mgr-5 mgb-5" :class="{'coos-disabled' : !has_add_before}" @click="add_before()">之前添加</a>
				<a class="coos-btn coos-btn-xs color-green mgr-5 mgb-5" :class="{'coos-disabled' : !has_add_after}" @click="add_after()">之后添加</a>
			</div>
			<div class="">
				<a class="coos-btn coos-btn-xs color-orange mgr-5 mgb-5" :class="{'coos-disabled' : !has_move_up}" @click="move_up()">上移</a>
				<a class="coos-btn coos-btn-xs color-orange mgr-5 mgb-5" :class="{'coos-disabled' : !has_move_dw}" @click="move_dw()">下移</a>
				<a class="coos-btn coos-btn-xs color-red mgr-5 mgb-5" :class="{'coos-disabled' : !has_remove}" @click="remove()">删除</a>
			</div>
			<div class="">
				<el-form :model="data" size="mini" :rules="rules" ref="form" label-width="60px">
					<h4 class="color-grey ft-12">基础属性</h4>
					<el-form-item class label="name" prop="name">
						<el-input type="text" v-model="data.name" @change="formDataChange($event,'name')" autocomplete="off">
						</el-input>
					</el-form-item>
					<el-form-item class label="class" prop="class">
						<el-input type="textarea" autosize v-model="data.class" @change="formDataChange($event,'class')" autocomplete="off">
						</el-input>
					</el-form-item>
					<el-form-item class label="style" prop="style">
						<el-input type="textarea" autosize v-model="data.style" @change="formDataChange($event,'style')" autocomplete="off">
						</el-input>
					</el-form-item>
					<el-form-item class label="v-if" prop="v-if">
						<el-input type="textarea" autosize v-model="data['v-if']" @change="formDataChange($event,'v-if')" autocomplete="off">
						</el-input>
					</el-form-item>
					<el-form-item class label="v-for" prop="v-for">
						<el-input type="textarea" autosize v-model="data['v-for']" @change="formDataChange($event,'v-for')" autocomplete="off">
						</el-input>
					</el-form-item>
					<el-form-item class label="v-show" prop="v-show">
						<el-input type="textarea" autosize v-model="data['v-show']" @change="formDataChange($event,'v-show')" autocomplete="off">
						</el-input>
					</el-form-item>
					<h4 class="color-grey ft-12">基础属性扩展<a class="coos-link color-orange float-right" v-on:click="open_base=(open_base?false:true)">{{open_base?'收起':'展开'}}</a></h4>
					<div class="" v-show="open_base">
						<el-form-item class label=":class" prop=":class">
							<el-input type="textarea" autosize v-model="data[':class']" @change="formDataChange($event,':class')" autocomplete="off">
							</el-input>
						</el-form-item>
						<el-form-item class label=":style" prop=":style">
							<el-input type="textarea" autosize v-model="data[':style']" @change="formDataChange($event,':style')" autocomplete="off">
							</el-input>
						</el-form-item>
						<el-form-item class label="v-else-if" prop="v-else-if">
							<el-input type="textarea" autosize v-model="data['v-else-if']" @change="formDataChange($event,'v-else-if')" autocomplete="off">
							</el-input>
						</el-form-item>
						<el-form-item class label="v-else" prop="v-else">
							<el-input type="textarea" autosize v-model="data['v-else']" @change="formDataChange($event,'v-else')" autocomplete="off">
							</el-input>
						</el-form-item>
					</div>
					<h4 class="color-grey ft-12">组件属性</h4>
					<template v-for="item in items">
						<template v-if="item.type == 'textarea'">
							<el-form-item class :label="item.label" :prop="item.prop">
								<el-input type="textarea" autosize v-model="data[item.name]" @change="formDataChange($event, item.name)" autocomplete="off"></el-input>
							</el-form-item>
						</template>
						<template v-else-if="item.type == 'color'">
							<el-form-item class :label="item.label" :prop="item.prop">
								<el-color-picker v-model="data[item.name]" @change="formDataChange($event, item.name)"></el-color-picker>
							</el-form-item>
						</template>
						<template v-else-if="item.type == 'switch'">
							<el-form-item class :label="item.label" :prop="item.prop">
								<el-switch v-model="data[item.name]" @change="formDataChange($event, item.name)"></el-switch>
							</el-form-item>
						</template>
						<template v-else-if="item.type == 'slider'">
							<el-form-item class :label="item.label" :prop="item.prop">
								<el-slider v-model="data[item.name]" @change="formDataChange($event, item.name)"></el-slider>
							</el-form-item>
						</template>
						<template v-else-if="item.type == 'select'">
							<el-form-item class :label="item.label" :prop="item.prop">
								<el-select v-model="data[item.name]" @change="formDataChange($event, item.name)" placeholder="请选择">
									<el-option v-for="option in item.options" :key="option.value" :value="option.value" :label="option.text" />
								</el-select>
							</el-form-item>
						</template>
						<template v-else-if="item.type == 'radio'">
							<el-form-item class :label="item.label" :prop="item.prop">
								<el-radio-group v-model="data[item.name]" @change="formDataChange($event, item.name)">
									<el-radio v-for="option in item.options" :key="option.value" :label="option.value" >{{option.text}}</el-radio>
								</el-radio-group>
							</el-form-item>
						</template>
						<template v-else-if="item.type == 'checkbox'">
							<el-form-item class :label="item.label" :prop="item.prop">
								<el-checkbox-group v-model="data[item.name]" @change="formDataChange($event, item.name)">
									<el-checkbox v-for="option in item.options" :key="option.value" :label="option.value" >{{option.text}}</el-checkbox>
								</el-checkbox-group>
							</el-form-item>
						</template>
						<template v-else>
							<el-form-item class :label="item.label" :prop="item.prop">
								<el-input v-model="data[item.name]" @change="formDataChange($event, item.name)" autocomplete="off"></el-input>
							</el-form-item>
						</template>
					</template>
				</el-form>
			</div>
		</div>
		</div>
		`;
		return html;
	};

	PageEditor.prototype.buildForm = function() {};

})();

(function() {
	var PageEditor = Editor.Page;

	PageEditor.prototype.initScript = function() {
		this.scriptOption = this.getScriptOption(this.page.script);
		this.scriptOption = this.scriptOption || {};
	};


})();

(function() {
	var PageEditor = Editor.Page;

	PageEditor.prototype.getVueDataHtml = function() {
		let html = `
		
		`;

		return html;
	};


})();

(function() {
	var PageEditor = Editor.Page;

	PageEditor.prototype.getVueMethodHtml = function() {
		let html = `
		
		`;

		return html;
	};


})();

(function() {
	var PageEditor = Editor.Page;

	PageEditor.prototype.getVueServiceHtml = function() {
		let html = `
		
		`;

		return html;
	};


})();

(function() {
	var PageEditor = Editor.Page;

	PageEditor.prototype.initStyle = function() {};


})();

})(window);