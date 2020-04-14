(function() {
	var PageEditor = Editor.Page;

	PageEditor.prototype.buildPageView = function($box) {
		let that = this;
		this.layout_id_name = 'layout-id';
		this.layout_map = {};
		let root = this.getLayoutRoot();
		root.id = 0;
		$box.attr(this.layout_id_name, root.id);
		this.layout_map[root.id] = root;
		let data = {};
		let $view = this.appendLayoutView($box, root, data);

		new Vue({
			el : $view[0],
			data : data,
			mounted () {
				that.$pageBox = $box;
				that.bindPageLayoutNav($box);
				that.bindPageEvent($box);
			}
		});
	};

	PageEditor.prototype.appendLayoutView = function($parent, layout, data) {
		if (!layout) {
			return;
		}
		let $view = null;
		layout.id = coos.getNumber();
		this.layout_map[layout.id] = layout;
		let template = this.getTemplateFromLayout(layout);
		if (template && coos.isNotEmpty(template.code)) {
			$view = $(template.code);
		} else {
			$view = $('<div ></div>');
		}
		$view.attr(this.layout_id_name, layout.id);
		if (template) {
			if (template.isBlock) {
				$view.addClass('page-design-layout-block');
			}
		}

		$parent.append($view);
		this.formatTemplateView($view, template, layout);

		if (layout.layouts) {
			layout.layouts.forEach(one => {
				this.appendLayoutView($view, one);
			});
		}
		return $view;
	};

	PageEditor.prototype.bindPageLayoutNav = function($box) {
		let that = this;
		$box.find('.page-design-layout-has-nav').each((index, el) => {
			let $el = $(el);
			if ($el.attr(this.layout_id_name) == null) {
				return;
			}
			let layout = this.getLayoutFromEl($el);
			let template = this.getTemplateFromLayout(layout);

			let $nav = $(`
			<el-dropdown class="page-design-layout-nav" @command="clickNav">
			      <span class="el-dropdown-link coos-pointer">
			        <i class="el-icon-arrow-down el-icon--right"></i>
			      </span>
			      <el-dropdown-menu slot="dropdown">
			        <el-dropdown-item v-for="nav in navs" :command="nav">{{nav.text}}</el-dropdown-item>
			      </el-dropdown-menu>
			    </el-dropdown>
			`);
			let vue = new Vue({
				el : $nav[0],
				data : {
					navs : template.navs
				},
				methods : {
					clickNav (nav) {
						if (nav.onClick) {
							nav.onClick(that, layout);
						}
					}
				}
			});
			$el.append(vue.$el);
		});

	};
	PageEditor.prototype.bindPageEvent = function($box) {
		let that = this;

		$box.on('mouseover', function(e) {
			//let $layout = $(e.target).closest('[layout-id]');
			//$box.find('.page-design-layout-over').removeClass('page-design-layout-over');
			//$layout.addClass('page-design-layout-over');
		});
		$box.on('contextmenu', function(e) {
			e = e || window.event;

			that.onPageContextmenu(e);
			e.preventDefault();
		});
		$box.on('click', function(e) {
			e = e || window.event;
			that.onPageClick(e);
			e.preventDefault();
		});
		$box.on('scroll', function(e) {
			e = e || window.event;
			that.lastScrollTop = $box.scrollTop();
		});
		this.clickElByLayout(this.lastClickLayout);
		$box.scrollTop(that.lastScrollTop);
	};

	PageEditor.prototype.clickElByLayout = function(layout) {
		if (layout == null) {
			return;
		}
		let id = layout.id;
		let $el = this.$pageBox.find('[' + this.layout_id_name + '="' + id + '"]');
		if ($el.length > 0) {
			this.onPageClickEl($el);
		} else {
			this.lastClickLayout = null;
			this.onSelectPageLayout(null, null);
		}

	};

	PageEditor.prototype.onPageClick = function(event) {

		this.onPageClickEl(event.target);

	};
	PageEditor.prototype.onPageClickEl = function(el) {

		let layout = this.getLayoutFromEl(el);
		this.lastClickLayout = layout;
		if (layout == null) {
			return;
		}

		let $layout = $(el).closest('[' + this.layout_id_name + ']');
		this.$pageBox.find('.page-design-layout-selected').removeClass('page-design-layout-selected');
		$layout.addClass('page-design-layout-selected');

		let template = this.getTemplateFromLayout(layout);
		this.onSelectPageLayout(layout, template);

	};
	PageEditor.prototype.onPageContextmenu = function(event) {
		let that = this;
		var eventData = {
			clientX : event.clientX,
			clientY : event.clientY
		};
		var menus = [];


		let layout = this.getLayoutFromEvent(event);
		if (layout == null) {
			return;
		}
		let parentLayout = this.getLayoutParent(layout);

		menus.push({
			text : "添加",
			onClick : function() {
				that.addPageLayoutChild(layout);
			}
		});
		if (parentLayout) {
			menus.push({
				text : "前边添加",
				onClick : function() {
					that.addPageLayoutBefore(layout, parentLayout);
				}
			});
			menus.push({
				text : "后边添加",
				onClick : function() {
					that.addPageLayoutAfter(layout, parentLayout);
				}
			});
			menus.push({
				text : "上移",
				onClick : function() {
					that.moveUpPageLayout(layout, parentLayout);
				}
			});
			menus.push({
				text : "下移",
				onClick : function() {
					that.moveDwPageLayout(layout, parentLayout);
				}
			});
			menus.push({
				text : "删除",
				onClick : function() {
					that.removePageLayout(layout, parentLayout);
				}
			});
		}

		source.repository.contextmenu.menus = menus;
		source.repository.contextmenu.callShow(event);
	};
})();