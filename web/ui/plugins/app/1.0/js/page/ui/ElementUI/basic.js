
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

		templates.push(new Button());
		templates.push(new Link());

		return templates;
	};


	var Button = coos.createClass(Editor.Page.UI.Template);

	Button.prototype.init = function() {
		this.isSlot = true;
	};

	Button.prototype.getName = function() {
		return 'button';
	};

	Button.prototype.getTitle = function() {
		return '按钮';
	};

	Button.prototype.getCode = function() {
		return `<el-button ></el-button>`;
	};

	Button.prototype.getOption = function() {
		let option = {};
		return option;
	};
	let types = [ {
		text : '文字',
		value : 'text'
	}, {
		text : '主要',
		value : 'primary'
	}, {
		text : '成功',
		value : 'success'
	}, {
		text : '信息',
		value : 'info'
	}, {
		text : '警告',
		value : 'warning'
	}, {
		text : '危险',
		value : 'danger'
	} ];
	let sizes = [ {
		text : '中等',
		value : 'medium'
	}, {
		text : '小型',
		value : 'small'
	}, {
		text : '超小',
		value : 'mini'
	} ];
	Button.prototype.getAttrs = function() {
		let attrs = [];
		attrs.push({
			name : 'type',
			text : '类型',
			type : 'select',
			custom : true,
			options : types,
			isStyle : true
		});
		attrs.push({
			name : 'size',
			text : '尺寸',
			type : 'select',
			custom : true,
			options : sizes,
			isStyle : true
		});
		attrs.push({
			name : 'plain',
			text : '朴素',
			type : 'switch'
		});
		attrs.push({
			name : 'round',
			text : '圆角',
			type : 'switch'
		});
		attrs.push({
			name : 'circle',
			text : '圆形',
			type : 'switch'
		});
		attrs.push({
			name : 'icon',
			text : '图标',
			type : 'text'
		});
		attrs.push({
			name : 'loading',
			text : '加载中',
			type : 'switch'
		});
		return attrs;
	};

	Button.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			slot : "默认按钮",
			attrs : []
		});

		types.forEach(type => {
			demos.push({
				slot : type.text + "按钮",
				attrs : [ {
					name : 'type',
					value : type.value
				} ]
			});
		});

		demos.push({
			divider : true
		});

		demos.push({
			slot : "朴素按钮",
			attrs : [ {
				name : "plain",
				value : true
			} ]
		});

		types.forEach(type => {
			demos.push({
				slot : type.text + "按钮",
				attrs : [ {
					name : 'type',
					value : type.value
				}, {
					name : "plain",
					value : true
				} ]
			});
		});

		demos.push({
			divider : true
		});

		demos.push({
			slot : "圆角按钮",
			attrs : [ {
				name : "round",
				value : true
			} ]
		});

		types.forEach(type => {
			demos.push({
				slot : type.text + "按钮",
				attrs : [ {
					name : 'type',
					value : type.value
				}, {
					name : "round",
					value : true
				} ]
			});
		});



		demos.push({
			divider : true
		});

		demos.push({
			slot : "圆角按钮",
			attrs : [ {
				name : "round",
				value : true
			}, {
				name : "icon",
				value : "el-icon-search"
			} ]
		});

		types.forEach(type => {
			demos.push({
				attrs : [ {
					name : 'type',
					value : type.value
				}, {
					name : "circle",
					value : true
				}, {
					name : "icon",
					value : "el-icon-search"
				} ]
			});
		});

		demos.push({
			divider : true
		});


		demos.push({
			slot : "默认按钮",
			attrs : []
		});

		sizes.forEach(size => {
			demos.push({
				slot : size.text + "按钮",
				attrs : [ {
					name : 'size',
					value : size.value
				} ]
			});
		});


		return demos;
	};


	var Link = coos.createClass(Editor.Page.UI.Template);

	Link.prototype.init = function() {
		this.isSlot = true;
	};

	Link.prototype.getName = function() {
		return 'link';
	};

	Link.prototype.getTitle = function() {
		return '链接';
	};

	Link.prototype.getCode = function() {
		return `<el-link ></el-link>`;
	};

	Link.prototype.getOption = function() {
		let option = {};
		return option;
	};

	Link.prototype.getAttrs = function() {
		let attrs = [];
		attrs.push({
			name : 'type',
			text : '类型',
			type : 'select',
			custom : true,
			options : types,
			isStyle : true
		});
		attrs.push({
			name : 'size',
			text : '尺寸',
			type : 'select',
			custom : true,
			options : sizes,
			isStyle : true
		});
		attrs.push({
			name : 'plain',
			text : '朴素',
			type : 'switch'
		});
		attrs.push({
			name : 'round',
			text : '圆角',
			type : 'switch'
		});
		attrs.push({
			name : 'circle',
			text : '圆形',
			type : 'switch'
		});
		attrs.push({
			name : 'icon',
			text : '图标',
			type : 'text'
		});
		attrs.push({
			name : 'loading',
			text : '加载中',
			type : 'switch'
		});
		return attrs;
	};

	Link.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			slot : "默认按钮",
			attrs : []
		});

		types.forEach(type => {
			demos.push({
				slot : type.text + "按钮",
				attrs : [ {
					name : 'type',
					value : type.value
				} ]
			});
		});

		return demos;
	};

})();