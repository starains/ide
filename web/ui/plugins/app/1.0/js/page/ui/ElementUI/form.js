
(function() {
	var UI = Editor.Page.UI;
	var FormGroup = coos.createClass(UI.Group);
	UI.ElementUI.FormGroup = FormGroup;

	FormGroup.prototype.getName = function() {
		return 'form';
	};

	FormGroup.prototype.getTitle = function() {
		return '表单';
	};

	FormGroup.prototype.getTemplates = function() {
		let templates = [];

		templates.push(new Form());
		templates.push(new FormItem());
		templates.push(new Radio());
		templates.push(new Checkbox());
		templates.push(new Input());
		templates.push(new InputNumber());
		templates.push(new Select());
		templates.push(new Switch());
		templates.push(new Slider());
		templates.push(new TimeSelect());
		templates.push(new DatePicker());
		templates.push(new Rate());
		templates.push(new ColorPicker());

		return templates;
	};

	var Form = coos.createClass(Editor.Page.UI.Template);
	Form.prototype.init = function() {
		this.isSlot = true;
		this.isBlock = true;
	};
	Form.prototype.getName = function() {
		return 'form';
	};
	Form.prototype.getTitle = function() {
		return '表单';
	};
	Form.prototype.getCode = function() {
		return `<el-form ></el-form>`;
	};
	Form.prototype.getOption = function() {
		let option = {};
		return option;
	};
	Form.prototype.getNavs = function() {
		let navs = [];

		let list = [];
		list.push(new Radio());
		list.push(new Checkbox());
		list.push(new Input());
		list.push(new InputNumber());
		list.push(new Select());
		list.push(new Switch());
		list.push(new Slider());
		list.push(new TimeSelect());
		list.push(new DatePicker());
		list.push(new Rate());
		list.push(new ColorPicker());

		list.forEach((one) => {
			navs.push({
				text : "添加" + one.title,
				onClick (designer, layout) {
					designer.recordHistory();
					layout.layouts = layout.layouts || [];

					let inputLayout = {
						key : "element-ui-" + one.name,
						option : {
							attrs : []
						}
					};
					if (one.name == 'radio' || one.name == 'checkbox') {
						inputLayout.option.slot = '选项1';
						inputLayout.option.attrs.push({
							name : 'label',
							value : '值1'
						});
					}
					layout.layouts.push({
						key : "element-ui-form-item",
						option : {
							attrs : [ {
								name : "label",
								value : "标签"
							} ]
						},
						layouts : [ inputLayout ]
					});
					designer.changeModel();
				}
			});
		});
		return navs;
	};
	Form.prototype.getAttrs = function() {
		let attrs = [];
		attrs.push({
			name : 'label-width',
			text : '标签宽度'
		});
		return attrs;
	};
	Form.prototype.getDemos = function() {
		let demos = [];
		demos.push({});
		return demos;
	};

	var FormItem = coos.createClass(Editor.Page.UI.Template);
	FormItem.prototype.init = function() {
		this.isSlot = true;
		this.isBlock = true;
	};
	FormItem.prototype.getName = function() {
		return 'form-item';
	};
	FormItem.prototype.getTitle = function() {
		return '表单元素';
	};
	FormItem.prototype.getCode = function() {
		return `<el-form-item ></el-form-item>`;
	};
	FormItem.prototype.getOption = function() {
		let option = {};
		return option;
	};
	FormItem.prototype.getAttrs = function() {
		let attrs = [];
		attrs.push({
			name : 'label',
			text : '标签'
		});
		attrs.push({
			name : 'label-width',
			text : '标签宽度'
		});
		return attrs;
	};
	FormItem.prototype.getDemos = function() {
		let demos = [];
		return demos;
	};

	var Radio = coos.createClass(Editor.Page.UI.Template);
	Radio.prototype.init = function() {
		this.isSlot = true;
	};
	Radio.prototype.getName = function() {
		return 'radio';
	};
	Radio.prototype.getTitle = function() {
		return '单选框';
	};
	Radio.prototype.getCode = function() {
		return `<el-radio ></el-radio>`;
	};
	Radio.prototype.getOption = function() {
		let option = {};
		return option;
	};
	Radio.prototype.getAttrs = function() {
		let attrs = [];
		attrs.push({
			name : 'label',
			text : '值'
		});
		return attrs;
	};
	Radio.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			slot : '选项1',
			attrs : [ {
				name : 'label',
				value : '值1'
			} ]
		});

		return demos;
	};


	var Checkbox = coos.createClass(Editor.Page.UI.Template);
	Checkbox.prototype.init = function() {
		this.isSlot = true;
	};
	Checkbox.prototype.getName = function() {
		return 'checkbox';
	};
	Checkbox.prototype.getTitle = function() {
		return '复选框';
	};
	Checkbox.prototype.getCode = function() {
		return `<el-checkbox ></el-checkbox>`;
	};
	Checkbox.prototype.getOption = function() {
		let option = {};
		return option;
	};
	Checkbox.prototype.getAttrs = function() {
		let attrs = [];
		attrs.push({
			name : 'label',
			text : '值'
		});
		return attrs;
	};
	Checkbox.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			slot : '选项1',
			attrs : [ {
				name : 'label',
				value : '值1'
			} ]
		});

		return demos;
	};

	var Input = coos.createClass(Editor.Page.UI.Template);
	Input.prototype.init = function() {
		this.isSlot = true;
	};
	Input.prototype.getName = function() {
		return 'input';
	};
	Input.prototype.getTitle = function() {
		return '输入框';
	};
	Input.prototype.getCode = function() {
		return `<el-input ></el-input>`;
	};
	Input.prototype.getOption = function() {
		let option = {};
		return option;
	};
	Input.prototype.getAttrs = function() {
		let attrs = [];
		return attrs;
	};
	Input.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attrs : []
		});

		return demos;
	};


	var InputNumber = coos.createClass(Editor.Page.UI.Template);
	InputNumber.prototype.init = function() {
		this.isSlot = true;
	};
	InputNumber.prototype.getName = function() {
		return 'input-number';
	};
	InputNumber.prototype.getTitle = function() {
		return '计数器';
	};
	InputNumber.prototype.getCode = function() {
		return `<el-input-number ></el-input-number>`;
	};
	InputNumber.prototype.getOption = function() {
		let option = {};
		return option;
	};
	InputNumber.prototype.getAttrs = function() {
		let attrs = [];
		return attrs;
	};
	InputNumber.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attrs : []
		});

		return demos;
	};

	var Select = coos.createClass(Editor.Page.UI.Template);
	Select.prototype.init = function() {
		this.isSlot = true;
	};
	Select.prototype.getName = function() {
		return 'select';
	};
	Select.prototype.getTitle = function() {
		return '选择器';
	};
	Select.prototype.getCode = function() {
		return `<el-select ></el-select>`;
	};
	Select.prototype.getOption = function() {
		let option = {};
		return option;
	};
	Select.prototype.getAttrs = function() {
		let attrs = [];
		return attrs;
	};
	Select.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attrs : []
		});

		return demos;
	};


	var Switch = coos.createClass(Editor.Page.UI.Template);
	Switch.prototype.init = function() {
		this.isSlot = true;
	};
	Switch.prototype.getName = function() {
		return 'switch';
	};
	Switch.prototype.getTitle = function() {
		return '开关';
	};
	Switch.prototype.getCode = function() {
		return `<el-switch ></el-switch>`;
	};
	Switch.prototype.getOption = function() {
		let option = {};
		return option;
	};
	Switch.prototype.getAttrs = function() {
		let attrs = [];
		return attrs;
	};
	Switch.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attrs : []
		});

		return demos;
	};

	var Slider = coos.createClass(Editor.Page.UI.Template);
	Slider.prototype.init = function() {
		this.isSlot = true;
		this.isBlock = true;
	};
	Slider.prototype.getName = function() {
		return 'slider';
	};
	Slider.prototype.getTitle = function() {
		return '滑块';
	};
	Slider.prototype.getCode = function() {
		return `<el-slider ></el-slider>`;
	};
	Slider.prototype.getOption = function() {
		let option = {};
		return option;
	};
	Slider.prototype.getAttrs = function() {
		let attrs = [];
		return attrs;
	};
	Slider.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attrs : []
		});

		return demos;
	};

	var TimeSelect = coos.createClass(Editor.Page.UI.Template);
	TimeSelect.prototype.init = function() {
		this.isSlot = true;
	};
	TimeSelect.prototype.getName = function() {
		return 'time-select';
	};
	TimeSelect.prototype.getTitle = function() {
		return '时间选择器';
	};
	TimeSelect.prototype.getCode = function() {
		return `<el-time-select ></el-time-select>`;
	};
	TimeSelect.prototype.getOption = function() {
		let option = {};
		return option;
	};
	TimeSelect.prototype.getAttrs = function() {
		let attrs = [];
		return attrs;
	};
	TimeSelect.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attrs : []
		});

		return demos;
	};

	var DatePicker = coos.createClass(Editor.Page.UI.Template);
	DatePicker.prototype.init = function() {
		this.isSlot = true;
	};
	DatePicker.prototype.getName = function() {
		return 'date-picker';
	};
	DatePicker.prototype.getTitle = function() {
		return '日期选择器';
	};
	DatePicker.prototype.getCode = function() {
		return `<el-date-picker ></el-date-picker>`;
	};
	DatePicker.prototype.getOption = function() {
		let option = {};
		return option;
	};
	DatePicker.prototype.getAttrs = function() {
		let attrs = [];
		return attrs;
	};
	DatePicker.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attrs : []
		});

		return demos;
	};

	var Rate = coos.createClass(Editor.Page.UI.Template);
	Rate.prototype.init = function() {
		this.isSlot = true;
	};
	Rate.prototype.getName = function() {
		return 'rate';
	};
	Rate.prototype.getTitle = function() {
		return '评分';
	};
	Rate.prototype.getCode = function() {
		return `<el-rate ></rate>`;
	};
	Rate.prototype.getOption = function() {
		let option = {};
		return option;
	};
	Rate.prototype.getAttrs = function() {
		let attrs = [];
		return attrs;
	};
	Rate.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attrs : []
		});

		return demos;
	};

	var ColorPicker = coos.createClass(Editor.Page.UI.Template);
	ColorPicker.prototype.init = function() {
		this.isSlot = true;
	};
	ColorPicker.prototype.getName = function() {
		return 'color-picker';
	};
	ColorPicker.prototype.getTitle = function() {
		return '颜色选择器';
	};
	ColorPicker.prototype.getCode = function() {
		return `<el-color-picker ></color-picker>`;
	};
	ColorPicker.prototype.getOption = function() {
		let option = {};
		return option;
	};
	ColorPicker.prototype.getAttrs = function() {
		let attrs = [];
		return attrs;
	};
	ColorPicker.prototype.getDemos = function() {
		let demos = [];

		demos.push({
			attrs : []
		});

		return demos;
	};
})();