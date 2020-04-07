
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