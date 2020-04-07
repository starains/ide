
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