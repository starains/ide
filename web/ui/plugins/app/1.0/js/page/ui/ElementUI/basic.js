
(function() {
	var UI = Editor.Page.UI;
	var BasicGroup = coos.createClass(UI.Group);
	UI.ElementUI.BasicGroup = BasicGroup;

	BasicGroup.prototype.getName = function() {
		return 'basic';
	};

	BasicGroup.prototype.getTitle = function() {
		return '基础';
	};

	BasicGroup.prototype.getTemplates = function() {
		let templates = [];

		templates.push(new Header());
		templates.push(new Body());
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
			options : UI.colors,
			isStyle : true
		});
		attrs.push({
			name : 'pd',
			text : '内边距',
			type : 'select',
			custom : true,
			options : UI.distances,
			isStyle : true
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
			name : 'pd',
			text : '内边距',
			type : 'select',
			custom : true,
			options : UI.distances,
			isStyle : true
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
			text : '文案'
		});
		attrs.push({
			name : 'html',
			text : '内容',
			type : "textarea"
		});
		attrs.push({
			name : 'color',
			text : '颜色',
			type : 'select',
			custom : true,
			options : UI.colors,
			isStyle : true
		});
		attrs.push({
			name : 'bg',
			text : '背景色',
			type : 'select',
			custom : true,
			options : UI.bgs,
			isStyle : true
		});
		attrs.push({
			name : 'size',
			text : '尺寸',
			type : 'select',
			options : UI.sizes,
			isStyle : true
		});
		attrs.push({
			name : 'circle',
			text : '圆形',
			type : 'switch',
			isStyle : true
		});
		attrs.push({
			name : 'rd',
			text : '边框圆角',
			type : 'select',
			custom : true,
			options : UI.distances,
			isStyle : true
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
				name : 'html',
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
						name : 'html',
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
						name : 'html',
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
					name : 'html',
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
			text : '文案'
		});
		attrs.push({
			name : 'html',
			text : '内容',
			type : "textarea"
		});
		attrs.push({
			name : 'color',
			text : '颜色',
			type : 'select',
			custom : true,
			options : UI.colors,
			isStyle : true
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
				name : 'html',
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
						name : 'html',
						value : '链接'
					} ]
				});
			}
		});

		return demos;
	};

})();