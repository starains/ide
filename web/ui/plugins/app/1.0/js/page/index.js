(function() {
	var PageEditor = coos.createClass(Editor);
	Editor.Page = PageEditor;

	PageEditor.prototype.buildPageView = function($box) {
		let model = this.model;
		model.layout = model.layout || {};
		let layout = model.layout;
		$box.data('layout', layout);
		$box.append(JSON.stringify(layout));
	};
	PageEditor.prototype.buildDesign = function() {
		var that = this;
		var $design = this.$design;
		$design.empty();


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
		if (el.data('layout')) {
			return el.data('layout');
		}
		return this.getLayoutFromEl(el.parent());
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

		menus.push({
			text : "添加",
			onClick : function() {
				that.choosePageModel(function() {
					that.recordHistory();
					layout.layouts = layout.layouts || [];
					layout.layouts.push({});
					that.changeModel();
				});

			}
		});


		source.repository.contextmenu.menus = menus;
		source.repository.contextmenu.callShow(event);
	};
})();