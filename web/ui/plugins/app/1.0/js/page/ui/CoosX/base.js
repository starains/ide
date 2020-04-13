
(function() {
	var UI = Editor.Page.UI;
	var BaseGroup = coos.createClass(UI.Group);
	UI.CoosXUI.BaseGroup = BaseGroup;

	BaseGroup.prototype.getName = function() {
		return 'base';
	};

	BaseGroup.prototype.getTitle = function() {
		return '基础';
	};

	BaseGroup.prototype.getTemplates = function() {
		let templates = [];

		templates.push(new Header());
		templates.push(new Body());
		templates.push(new Panel());
		templates.push(new Row());
		templates.push(new Col());
		templates.push(new Layout());
		templates.push(new Btn());
		templates.push(new Link());

		return templates;
	};



	var Header = coos.createClass(Editor.Page.UI.Template);

	Header.prototype.init = function() {
		this.isBlock = true;
		this.hasSlot = true;
	};

	Header.prototype.getName = function() {
		return 'header';
	};

	Header.prototype.getTitle = function() {
		return '头部';
	};

	Header.prototype.getCode = function() {
		return `<c-header ></c-header>`;
	};

	Header.prototype.getOption = function() {
		let option = {};
		return option;
	};

	Header.prototype.getAttrs = function() {
		let attrs = [];
		attrs.push({
			name : 'title',
			text : '标题',
			type : 'textarea'
		});
		attrs.push({
			name : 'bdcolor',
			text : '边框配色',
			type : 'select',
			custom : true,
			options : UI.colors
		});
		attrs.push({
			name : 'bdwidth',
			text : '边框宽度',
			type : 'select',
			custom : true,
			options : UI.distances
		});
		attrs.push({
			name : 'pd',
			text : '内边距',
			type : 'select',
			custom : true,
			options : UI.distances
		});
		return attrs;
	};

	Header.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attrs : [ {
				name : 'title',
				value : '标题'
			} ]
		});

		return demos;
	};

	var Body = coos.createClass(Editor.Page.UI.Template);

	Body.prototype.init = function() {
		this.isBlock = true;
		this.hasSlot = true;
	};

	Body.prototype.getName = function() {
		return 'body';
	};

	Body.prototype.getTitle = function() {
		return '体部';
	};

	Body.prototype.getCode = function() {
		return `<c-body ></c-body>`;
	};

	Body.prototype.getOption = function() {
		let option = {};
		return option;
	};

	Body.prototype.getAttrs = function() {
		let attrs = [];
		attrs.push({
			name : 'bdcolor',
			text : '边框配色',
			type : 'select',
			custom : true,
			options : UI.colors
		});
		attrs.push({
			name : 'pd',
			text : '内边距',
			type : 'select',
			custom : true,
			options : UI.distances
		});
		return attrs;
	};

	Body.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attrs : []
		});

		return demos;
	};

	var Btn = coos.createClass(Editor.Page.UI.Template);

	Btn.prototype.init = function() {};

	Btn.prototype.getName = function() {
		return 'btn';
	};

	Btn.prototype.getTitle = function() {
		return '按钮';
	};

	Btn.prototype.getCode = function() {
		return `<c-btn ></c-btn>`;
	};

	Btn.prototype.getOption = function() {
		let option = {};
		return option;
	};

	Btn.prototype.getAttrs = function() {
		let attrs = [];
		attrs.push({
			name : 'text',
			text : '文案',
			type : "textarea"
		});
		attrs.push({
			name : 'color',
			text : '颜色',
			type : 'select',
			custom : true,
			options : UI.colors
		});
		attrs.push({
			name : 'bg',
			text : '背景色',
			type : 'select',
			custom : true,
			options : UI.bgs
		});
		attrs.push({
			name : 'size',
			text : '尺寸',
			type : 'select',
			options : UI.sizes
		});
		attrs.push({
			name : 'disabled',
			text : '禁用',
			type : 'switch'
		});
		return attrs;
	};

	Btn.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attrs : [ {
				name : 'text',
				value : '按钮'
			} ]
		});

		UI.colors.forEach(color => {
			if (coos.isNotEmpty(color.value) && color.value != 'white') {
				demos.push({
					attrs : [ {
						name : 'color',
						value : color.value
					}, {
						name : 'text',
						value : '按钮'
					} ]
				});
			}
		});

		demos.push({
			divider : true
		});

		UI.colors.forEach(color => {
			if (coos.isNotEmpty(color.value) && color.value != 'white') {
				demos.push({
					attrs : [ {
						name : 'bg',
						value : color.value
					}, {
						name : 'text',
						value : '按钮'
					} ]
				});
			}
		});

		demos.push({
			divider : true
		});

		UI.sizes.forEach(size => {
			demos.push({
				attrs : [ {
					name : 'size',
					value : size.value
				}, {
					name : 'text',
					value : '按钮'
				} ]
			});
		});

		return demos;
	};


	var Link = coos.createClass(Editor.Page.UI.Template);

	Link.prototype.init = function() {};

	Link.prototype.getName = function() {
		return 'link';
	};

	Link.prototype.getTitle = function() {
		return '链接';
	};

	Link.prototype.getCode = function() {
		return `<c-link ></c-link>`;
	};

	Link.prototype.getOption = function() {
		let option = {};
		return option;
	};

	Link.prototype.getAttrs = function() {
		let attrs = [];
		attrs.push({
			name : 'text',
			text : '文案',
			type : "textarea"
		});
		attrs.push({
			name : 'color',
			text : '颜色',
			type : 'select',
			custom : true,
			options : UI.colors
		});
		attrs.push({
			name : 'disabled',
			text : '禁用',
			type : 'switch'
		});
		return attrs;
	};

	Link.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attrs : [ {
				name : 'text',
				value : '链接'
			} ]
		});

		UI.colors.forEach(color => {
			if (coos.isNotEmpty(color.value) && color.value != 'white') {
				demos.push({
					attrs : [ {
						name : 'color',
						value : color.value
					}, {
						name : 'text',
						value : '链接'
					} ]
				});
			}
		});

		return demos;
	};

	var Layout = coos.createClass(Editor.Page.UI.Template);

	Layout.prototype.init = function() {
		this.isBlock = true;
		this.hasSlot = true;
	};

	Layout.prototype.getName = function() {
		return 'layout';
	};

	Layout.prototype.getTitle = function() {
		return '布局';
	};

	Layout.prototype.getCode = function() {
		return `<c-layout ></c-layout>`;
	};

	Layout.prototype.getOption = function() {
		let option = {};
		return option;
	};

	Layout.prototype.getAttrs = function() {
		let attrs = [];
		attrs.push({
			name : 'bdcolor',
			text : '边框配色',
			type : 'select',
			custom : true,
			options : UI.colors
		});
		attrs.push({
			name : 'pd',
			text : '内边距',
			type : 'select',
			custom : true,
			options : UI.distances
		});
		return attrs;
	};

	Layout.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attrs : []
		});

		return demos;
	};



	var Row = coos.createClass(Editor.Page.UI.Template);

	Row.prototype.init = function() {
		this.isBlock = true;
		this.hasSlot = true;
	};

	Row.prototype.getName = function() {
		return 'row';
	};

	Row.prototype.getTitle = function() {
		return '行';
	};

	Row.prototype.getCode = function() {
		return `<c-row ></c-row>`;
	};

	Row.prototype.getOption = function() {
		let option = {};
		return option;
	};

	Row.prototype.getAttrs = function() {
		let attrs = [];
		attrs.push({
			name : 'bdcolor',
			text : '边框配色',
			type : 'select',
			custom : true,
			options : UI.colors
		});
		attrs.push({
			name : 'pd',
			text : '内边距',
			type : 'select',
			custom : true,
			options : UI.distances
		});
		return attrs;
	};

	Row.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attr : {}
		});

		return demos;
	};



	var Col = coos.createClass(Editor.Page.UI.Template);

	Col.prototype.init = function() {
		this.isBlock = true;
		this.hasSlot = true;
	};

	Col.prototype.getName = function() {
		return 'col';
	};

	Col.prototype.getTitle = function() {
		return '列';
	};

	Col.prototype.getCode = function() {
		return `<c-col ></c-col>`;
	};

	Col.prototype.getOption = function() {
		let option = {};
		return option;
	};

	Col.prototype.getAttrs = function() {
		let attrs = [];
		attrs.push({
			name : 'col',
			text : '列',
			type : 'number'
		});
		attrs.push({
			name : 'bdcolor',
			text : '边框配色',
			type : 'select',
			custom : true,
			options : UI.colors
		});
		attrs.push({
			name : 'pd',
			text : '内边距',
			type : 'select',
			custom : true,
			options : UI.distances
		});
		return attrs;
	};

	Col.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attrs : [ {
				name : 'col',
				value : 6
			} ]
		});

		demos.push({
			attrs : [ {
				name : 'col',
				value : 12
			} ]
		});

		return demos;
	};




	var Panel = coos.createClass(Editor.Page.UI.Template);

	Panel.prototype.init = function() {
		this.isBlock = true;
		this.hasSlot = true;
	};

	Panel.prototype.getName = function() {
		return 'panel';
	};

	Panel.prototype.getTitle = function() {
		return '面板';
	};

	Panel.prototype.getCode = function() {
		return `<c-panel ></c-panel>`;
	};

	Panel.prototype.getOption = function() {
		let option = {};
		return option;
	};

	Panel.prototype.getAttrs = function() {
		let attrs = [];
		attrs.push({
			name : 'title',
			text : '标题',
			type : 'textarea'
		});
		attrs.push({
			name : 'bdcolor',
			text : '边框配色',
			type : 'select',
			custom : true,
			options : UI.colors
		});
		attrs.push({
			name : 'bdplace',
			text : '边框位置',
			type : 'select',
			multiple : true,
			options : [ {
				text : '顶部',
				value : 'top'
			}, {
				text : '左侧',
				value : 'left'
			}, {
				text : '右侧',
				value : 'right'
			}, {
				text : '底部',
				value : 'bottom'
			} ]
		});
		attrs.push({
			name : 'bdwidth',
			text : '边框宽度',
			type : 'select',
			custom : true,
			options : UI.distances
		});
		return attrs;
	};

	Panel.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attrs : [ {
				name : 'title',
				value : '标题'
			} ]
		});

		return demos;
	};
})();