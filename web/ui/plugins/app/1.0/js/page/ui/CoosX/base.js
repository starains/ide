
(function() {
	var BaseGroup = coos.createClass(Editor.Page.UI.Group);

	Editor.Page.UI.CoosXUI.BaseGroup = BaseGroup;

	BaseGroup.prototype.getName = function() {
		return 'base';
	};

	BaseGroup.prototype.getTitle = function() {
		return '基础';
	};

	BaseGroup.prototype.getTemplates = function() {
		let templates = [];

		templates.push(new Row());
		templates.push(new Col());
		templates.push(new Layout());
		templates.push(new Panel());
		templates.push(new Btn());
		templates.push(new Link());

		return templates;
	};

	var Btn = coos.createClass(Editor.Page.UI.Template);

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

	Btn.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attr : {},
			slot : '按钮'
		});

		coos.style.config.colors.forEach(color => {
			if (coos.isNotEmpty(color.value) && color.value != 'white') {
				demos.push({
					attr : {
						color : color.value
					},
					slot : '按钮'
				});
			}
		});

		demos.push({
			divider : true
		});

		coos.style.config.colors.forEach(color => {
			if (coos.isNotEmpty(color.value) && color.value != 'white') {
				demos.push({
					attr : {
						bg : color.value
					},
					slot : '按钮'
				});
			}
		});

		demos.push({
			divider : true
		});

		coos.style.config.sizes.forEach(size => {
			demos.push({
				attr : {
					size : size.name
				},
				slot : '按钮'
			});
		});

		return demos;
	};


	var Link = coos.createClass(Editor.Page.UI.Template);

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

	Link.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attr : {},
			slot : '链接'
		});

		coos.style.config.colors.forEach(color => {
			if (coos.isNotEmpty(color.value) && color.value != 'white') {
				demos.push({
					attr : {
						color : color.value
					},
					slot : '链接'
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

	Layout.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attr : {}
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

	Col.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attr : {
				col : 6
			}
		});

		demos.push({
			attr : {
				col : 12
			}
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

	Panel.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attr : {
				title : '标题'
			}
		});

		return demos;
	};
})();