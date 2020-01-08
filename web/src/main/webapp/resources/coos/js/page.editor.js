(function(window) {
	'use strict';

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
		this.context = options.context || {};
		this.source = options.source || {};
		this.ENUM_MAP = this.source.ENUM_MAP || {};

		this.data = {
			context : this.context,
			source : this.source,
			readyonly : this.readyonly,
			ENUM_MAP : this.ENUM_MAP,
			script : {
				datas : [],
				methods : [],
				services : []
			}
		};
	};

	Editor.prototype.init = function() {};


	Editor.prototype.initBox = function($box) {
		if (this.$editorBox != null) {
			return;
		}

		let html = `
		<div class="page-editor-box" >
			<div class="page-editor-header" >
			
			</div>
			<div class="page-editor-body" >
				<div class="page-editor-model-box" >
				` + this.getModelBoxHtml() + `
				</div>
				<div class="page-editor-tab page-editor-tab-page bg-blue-grey" >
					<ul class="page-editor-tab-nav " >
						<li><a class="coos-btn bg-blue-grey coos-btn-sm" @click="active_page_tab='page'" 
						:class="{'coos-active' : active_page_tab == 'page'}">页面</a></li>
						<li><a class="coos-btn bg-blue-grey coos-btn-sm" @click="active_page_tab='data'" 
						:class="{'coos-active' : active_page_tab == 'data'}">数据</a></li>
						<li><a class="coos-btn bg-blue-grey coos-btn-sm" @click="active_page_tab='method'" 
						:class="{'coos-active' : active_page_tab == 'method'}">方法</a></li>
						<li><a class="coos-btn bg-blue-grey coos-btn-sm" @click="active_page_tab='service'" 
						:class="{'coos-active' : active_page_tab == 'service'}">服务</a></li>
					</ul>
					<div class="page-editor-tab-panels" >
						<div class="page-editor-page-box coos-scrollbar" v-show="active_page_tab == 'page'">
						</div>
						<div class="page-editor-data-box coos-scrollbar" v-show="active_page_tab == 'data'">
							` + this.getVueDataHtml() + `
						</div>
						<div class="page-editor-method-box coos-scrollbar" v-show="active_page_tab == 'method'">
							` + this.getVueMethodHtml() + `
						</div>
						<div class="page-editor-service-box coos-scrollbar" v-show="active_page_tab == 'service'">
							` + this.getVueServiceHtml() + `
						</div>
					</div>
				</div>
				<div class="page-editor-tab page-editor-tab-setting bg-blue-grey" >
					<ul class="page-editor-tab-nav " >
						<li><a class="coos-btn bg-blue-grey coos-btn-sm" @click="active_setting_tab='form'" 
						:class="{'coos-active' : active_setting_tab == 'form'}">基础</a></li>
						<li><a class="coos-btn bg-blue-grey coos-btn-sm" @click="active_setting_tab='extend'" 
						:class="{'coos-active' : active_setting_tab == 'extend'}">扩展</a></li>
						<li><a class="coos-btn bg-blue-grey coos-btn-sm" @click="active_setting_tab='event'" 
						:class="{'coos-active' : active_setting_tab == 'event'}">事件</a></li>
					</ul>
					<div class="page-editor-tab-panels" >
						<div class="page-editor-form-box coos-scrollbar" v-show="active_setting_tab == 'form'">
							<div class="text-center pdtb-50 ft-20 color-orange">请选择元素</div>
						</div>
						<div class="page-editor-extend-box coos-scrollbar" v-show="active_setting_tab == 'extend'">
						</div>
						<div class="page-editor-event-box coos-scrollbar" v-show="active_setting_tab == 'event'">
						</div>
					</div>
				</div>
			</div>
		</div>
		`;
		var $editorBox = $(html);
		this.$editorBox = $editorBox;
		$box.empty();
		$box.append(this.$editorBox);
		this.initVue();
	};

	Editor.prototype.initVue = function() {
		var that = this;
		this.data.uis = this.getUIList();
		this.data.ui_active_name = this.data.uis[0].name;
		this.data.ui_group_active_name = this.data.ui_active_name + '-' + this.data.uis[0].groups[0].name;
		this.data.active_page_tab = 'page';
		this.data.active_setting_tab = 'form';
		this.vue = new Vue({
			data : that.data,
			el : that.$editorBox[0],
			mounted () {
				let height = $(this.$el).find('.ui-list').height();
				this.uis.forEach((ui, index) => {
					let groupList = $(this.$el).find('.ui-group-list')[index];
					$(groupList).find('.ui-model-list').css('height', height - 30 * this.uis.length - 30 * ui.groups.length)
				});
			},
			methods : {
				chooseModel (ui, group, model, demo) {
					that.chooseModel(ui, group, model, demo);
				},
				closeModelBox () {
					that.closeAppendModel();
				},
				ui_active_change (activeName) {
					this.uis.forEach(ui => {
						if (activeName == (ui.name)) {
							window.setTimeout(() => {
								if (ui.lastActiveName) {
									this.ui_group_active_name = ui.lastActiveName;
								} else {
									this.ui_group_active_name = ui.name + '-' + ui.groups[0].name;
								}
							}, 300);
						}
					});
				},
				ui_group_active_change (activeName) {}
			}
		});
		let $el = $(this.vue.$el);

		this.$header = $el.find('.page-editor-header');
		this.$body = $el.find('.page-editor-body');

		this.$modelBox = $el.find('.page-editor-model-box');

		this.$pageBox = $el.find('.page-editor-page-box');
		this.$dataBox = $el.find('.page-editor-data-box');
		this.$methodBox = $el.find('.page-editor-method-box');
		this.$serviceBox = $el.find('.page-editor-service-box');

		this.$formBox = $el.find('.page-editor-form-box');
		this.$eventBox = $el.find('.page-editor-event-box');
	};

	Editor.prototype.build = function($box, page) {
		this.initBox($box);

		this.buildModel();
		this.buildPage(page);
	};
	coos.PageEditor = Editor;
})();

(function() {
	var Editor = coos.PageEditor;

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

	Editor.prototype.toCode = function(page) {
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


	Editor.prototype.getScriptOption = function(script) {
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
	var Editor = coos.PageEditor;
	Editor.prototype.bindSortable = function() {
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

	Editor.prototype.bindUISortable = function(ui) {
		ui.groups.forEach((group) => {
			this.bindUIGroupSortable(ui, group);
		});
	};

	Editor.prototype.bindUIGroupSortable = function(ui, group) {
		group.models.forEach((model) => {
			this.bindUIModelSortable(ui, group, model);
		});
	};

	Editor.prototype.bindUIModelSortable = function(ui, group, model) {
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
	var Editor = coos.PageEditor;


	Editor.prototype.getUIList = function() {
		let list = [];
		list.push(this.getTagUI());
		list.push(this.getElementUI());

		return list;
	};
})();

(function() {
	var Editor = coos.PageEditor;

	Editor.prototype.getTagUI = function() {
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
	Editor.prototype.getBaseUIGroups = function() {
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


	Editor.prototype.getBaseUIBaseModels = function() {
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

	Editor.prototype.getBaseUIButton = function() {
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


	Editor.prototype.getBaseUILink = function() {
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
	var Editor = coos.PageEditor;

	Editor.prototype.getElementUI = function() {
		let ui = {};
		ui.name = 'element-ui';
		ui.title = 'Element UI';


		ui.groups = this.getElementUIGroups();

		return ui;
	};
	Editor.prototype.getComponentHTML = function(template, data, callback) {
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

	Editor.prototype.getElementUIGroups = function() {
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

	Editor.prototype.getElementUIButton = function() {
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
	Editor.prototype.getElementUILink = function() {

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
	Editor.prototype.getElementUIRadio = function() {
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

	Editor.prototype.getElementUICheckbox = function() {
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
	Editor.prototype.getElementUIInput = function() {
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
	Editor.prototype.getElementUIInputNumber = function() {
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
	Editor.prototype.getElementUISelect = function() {
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

	Editor.prototype.getElementUISwitch = function() {
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

	Editor.prototype.getElementUISlider = function() {
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

	Editor.prototype.getElementUITimePicker = function() {
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

	Editor.prototype.getElementUIDatePicker = function() {
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

	Editor.prototype.getElementUIRate = function() {
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


	Editor.prototype.getElementUIColorPicker = function() {
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

	Editor.prototype.getElementUIForm = function() {
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
	Editor.prototype.getElementUITable = function() {
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


	Editor.prototype.getElementUITag = function() {
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


	Editor.prototype.getElementUIProgress = function() {
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


	Editor.prototype.getElementUITree = function() {
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
	Editor.prototype.getElementUIPagination = function() {
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

	Editor.prototype.getElementUIBadge = function() {
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
	var Editor = coos.PageEditor;

	Editor.prototype.getModelBoxHtml = function() {
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

	Editor.prototype.buildModel = function() {};

	Editor.prototype.showAppendModel = function($parent) {
		this.lastAppendParent = $parent;
		this.$modelBox.addClass('show');
	};

	Editor.prototype.closeAppendModel = function() {
		this.$modelBox.removeClass('show');
	};

	Editor.prototype.chooseModel = function(ui, group, model, demo) {
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
	var Editor = coos.PageEditor;

	Editor.prototype.getPageBoxHtml = function() {};

	Editor.prototype.buildPage = function(page) {
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

	Editor.prototype.changePage = function() {
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

		let page_ = that.options.onChange(page, code);
		that.refreshPage();
	};


	Editor.prototype.refreshPage = function() {
		this.buildPage();
	};


})();

(function() {
	var Editor = coos.PageEditor;


	Editor.prototype.bindPageEvent = function() {
		let $pageBox = this.$pageBox;
		let $template = this.$template;
		let that = this;
		$pageBox.unbind('click').on('click', function() {
			that.clickPage($pageBox.find('.coos-page:first'), "0");
		});

	};

	Editor.prototype.add_child = function() {
		this.showAppendModel($(this.lastChoosePageTemplate));
	};
	Editor.prototype.add_before = function() {
		this.showAppendModel($(this.lastChoosePageTemplate));
	};
	Editor.prototype.add_after = function() {
		this.showAppendModel($(this.lastChoosePageTemplate));
	};
	Editor.prototype.choose_pro = function() {
		this.showAppendModel($(this.lastChoosePageTemplate));
	};
	Editor.prototype.choose_next = function() {
		this.showAppendModel($(this.lastChoosePageTemplate));
	};
	Editor.prototype.move_up = function() {
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
	Editor.prototype.move_dw = function() {
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
	Editor.prototype.remove = function() {
		$(this.lastChoosePageTemplate).remove();
		this.changePage();
	};

	Editor.prototype.initChoosePlaces = function(element) {
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
	Editor.prototype.initChooseAction = function() {};

	Editor.prototype.clickPage = function(el, index) {
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
	Editor.prototype.choosePageTemplate = function($el, model) {
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

	Editor.prototype.formDataChange = function(value, name) {
		let model = $(this.lastChoosePageTemplate).data('model');
		if (model && model.setData) {
			model.setData(this.lastChoosePageTemplate, this.form_data.data);
			this.changePage();
		}
	};

})();

(function() {
	var Editor = coos.PageEditor;

	Editor.prototype.initTemplate = function() {

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

	Editor.prototype.bindTemplateEvent = function($template) {
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

	Editor.prototype.removeTemplateEvent = function($template) {
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
	var Editor = coos.PageEditor;

	Editor.prototype.getFormBoxHtml = function() {
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

	Editor.prototype.buildForm = function() {};

})();

(function() {
	var Editor = coos.PageEditor;

	Editor.prototype.initScript = function() {
		this.scriptOption = this.getScriptOption(this.page.script);
		this.scriptOption = this.scriptOption || {};
	};


})();

(function() {
	var Editor = coos.PageEditor;

	Editor.prototype.getVueDataHtml = function() {
		let html = `
		
		`;

		return html;
	};


})();

(function() {
	var Editor = coos.PageEditor;

	Editor.prototype.getVueMethodHtml = function() {
		let html = `
		
		`;

		return html;
	};


})();

(function() {
	var Editor = coos.PageEditor;

	Editor.prototype.getVueServiceHtml = function() {
		let html = `
		
		`;

		return html;
	};


})();

(function() {
	var Editor = coos.PageEditor;

	Editor.prototype.initStyle = function() {};


})();

})(window);