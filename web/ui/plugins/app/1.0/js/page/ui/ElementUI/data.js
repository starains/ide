
(function() {
	var UI = Editor.Page.UI;
	var DataGroup = coos.createClass(UI.Group);
	UI.ElementUI.DataGroup = DataGroup;

	DataGroup.prototype.getName = function() {
		return 'data';
	};

	DataGroup.prototype.getTitle = function() {
		return '数据';
	};

	DataGroup.prototype.getTemplates = function() {
		let templates = [];

		templates.push(new Row());
		templates.push(new Col());
		templates.push(new Layout());
		templates.push(new Panel());

		return templates;
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
		attrs.push({
			name : 'rd',
			text : '边框圆角',
			type : 'select',
			custom : true,
			options : UI.distances,
			isStyle : true
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
		attrs.push({
			name : 'rd',
			text : '边框圆角',
			type : 'select',
			custom : true,
			options : UI.distances,
			isStyle : true
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
			type : 'select',
			options : UI.cols
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
		attrs.push({
			name : 'rd',
			text : '边框圆角',
			type : 'select',
			custom : true,
			options : UI.distances,
			isStyle : true
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
			options : UI.colors,
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