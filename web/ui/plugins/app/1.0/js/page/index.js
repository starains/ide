(function() {
	var PageEditor = coos.createClass(Editor);
	Editor.Page = PageEditor;

	let layout_map = {};
	PageEditor.prototype.buildPageView = function($box) {
		layout_map = {};
		let root = this.getLayoutRoot();
		let id = this.getLayoutID(root);
		$box.attr('layout-id', id);
		layout_map[id] = root;
		let $view = this.appendLayoutView($box, root);

		new Vue({
			el : $view[0],
			data : {},
			mounted () {}
		});
		$box.on('mouseover', function(e) {
			let $layout = $(e.target).closest('[layout-id]');
			$box.find('.page-design-layout-over').removeClass('page-design-layout-over');
			$layout.addClass('page-design-layout-over');
		});
	};

	PageEditor.prototype.getLayoutID = function(layout) {
		let root = this.getLayoutRoot();
		if (root == layout) {
			return 0;
		} else {
			return coos.getNumber();
		}
	};

	PageEditor.prototype.appendLayoutView = function($parent, layout) {
		if (!layout) {
			return;
		}
		let $view = null;
		let id = this.getLayoutID(layout);
		layout_map[id] = layout;
		if (coos.isEmpty(layout.template)) {
			if (coos.isEmpty(layout.html)) {
				$view = $('<div ></div>');
			} else {
				$view = $(layout.html);
			}
		} else {
			$view = $(layout.template);
		}
		$view.attr('layout-id', id);

		$parent.append($view);

		if (layout.layouts) {
			layout.layouts.forEach(one => {
				this.appendLayoutView($view, one);
			});
		}
		return $view;
	};
	PageEditor.prototype.buildDesign = function() {
		var that = this;
		var $design = this.$design;

		$design.find('.page-design-box:first').remove();
		var $box = $('<div class="page-design-box"></div>');
		$design.append($box);

		this.buildPageModel($design);
		this.buildPageView($box);
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

	PageEditor.prototype.getLayoutFromEl = function(el) {
		el = $(el);
		if (el.length == 0) {
			return null;
		}
		if (coos.isNotEmpty(el.attr('layout-id'))) {
			return layout_map[el.attr('layout-id')];
		}
		return this.getLayoutFromEl(el.parent());
	};
	PageEditor.prototype.getLayoutRoot = function() {
		this.model.layout = this.model.layout || {};
		return this.model.layout;
	};
	PageEditor.prototype.getLayoutParent = function(layout, p) {
		let root = this.getLayoutRoot();

		if (layout == root) {
			return null;
		}
		if (p == null) {
			p = root;
		}
		let res = null;
		if (p.layouts) {
			if (p.layouts.indexOf(layout) >= 0) {
				res = p;
			} else {
				p.layouts.forEach(one => {
					if (res == null) {
						res = this.getLayoutParent(layout, one);
					}
				});
			}
		}
		return res;
	};
	PageEditor.prototype.showPageDesignContextmenu = function(event) {
		let that = this;
		var eventData = {
			clientX : event.clientX,
			clientY : event.clientY
		};
		var menus = [];

		let model = this.model;

		let layout = this.getLayoutFromEl(event.target);
		if (layout == null) {
			return;
		}
		let parentLayout = this.getLayoutParent(layout);

		menus.push({
			text : "添加",
			onClick : function() {
				that.choosePageModel(function(res) {
					that.recordHistory();
					layout.layouts = layout.layouts || [];
					layout.layouts.push(res);
					that.changeModel();
				});

			}
		});
		if (parentLayout) {
			menus.push({
				text : "前边添加",
				onClick : function() {
					that.choosePageModel(function(res) {
						that.recordHistory();
						parentLayout.layouts.splice(0, 0, res);
						that.changeModel();
					});

				}
			});
			menus.push({
				text : "后边添加",
				onClick : function() {
					that.choosePageModel(function(res) {
						that.recordHistory();
						parentLayout.layouts.push(res);
						that.changeModel();
					});

				}
			});
			menus.push({
				text : "上移",
				onClick : function() {
					let list = parentLayout.layouts;
					let index = list.indexOf(layout);
					if (index > 0) {
						that.recordHistory();
						list[index] = list.splice(index - 1, 1, list[index])[0];
						that.changeModel();
					}
				}
			});
			menus.push({
				text : "下移",
				onClick : function() {
					let list = parentLayout.layouts;
					let index = list.indexOf(layout);
					if (index < list.length - 1) {
						that.recordHistory();

						list[index] = list.splice(index + 1, 1, list[index])[0];

						that.changeModel();
					}
				}
			});
			menus.push({
				text : "删除",
				onClick : function() {
					that.recordHistory();

					let index = parentLayout.layouts.indexOf(layout);
					parentLayout.layouts.splice(index, 1);

					that.changeModel();

				}
			});
		}

		source.repository.contextmenu.menus = menus;
		source.repository.contextmenu.callShow(event);
	};
})();