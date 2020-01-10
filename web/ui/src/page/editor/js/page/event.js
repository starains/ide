
(function() {
	var Editor = coos.PageEditor;


	Editor.prototype.bindPageEvent = function() {
		let $pageBox = this.$pageBox;
		let $template = this.$template;
		let that = this;
		$pageBox.unbind('click').on('click', function() {
			that.clickPage($pageBox.find('.coos-page:first'), "0");
		});

	};

	Editor.prototype.add_child = function() {
		this.showAppendModel($(this.lastChoosePageTemplate));
	};
	Editor.prototype.add_before = function() {
		this.showAppendModel($(this.lastChoosePageTemplate));
	};
	Editor.prototype.add_after = function() {
		this.showAppendModel($(this.lastChoosePageTemplate));
	};
	Editor.prototype.choose_pro = function() {
		this.showAppendModel($(this.lastChoosePageTemplate));
	};
	Editor.prototype.choose_next = function() {
		this.showAppendModel($(this.lastChoosePageTemplate));
	};
	Editor.prototype.move_up = function() {
		let $el = $(this.lastChoosePageTemplate);
		var list = $el.parent().children();
		var thisLocation = list.index($el);
		if (thisLocation < 1) {

		} else {
			let $prev = $el.prev();
			$prev.before($el); //上移动
			this.changePage();
		}
	};
	Editor.prototype.move_dw = function() {
		let $el = $(this.lastChoosePageTemplate);
		var list = $el.parent().children();
		var thisLocation = list.index($el);
		if (thisLocation >= list.length - 1) {

		} else {

			let $next = $el.next();
			$next.after($el); //下移动
			this.changePage();
		}
	};
	Editor.prototype.remove = function() {
		$(this.lastChoosePageTemplate).remove();
		this.changePage();
	};

	Editor.prototype.initChoosePlaces = function(element) {
		let places = this.form_data.places;
		places.splice(0, places.length);
		function addPlace(el) {
			let $el = $(el);
			if ($el.length == 0) {

				return;
			}
			if ($el.parent().hasClass('page-editor-page-box')) {
				return;
			}
			addPlace($el.parent());
			places.push({
				name : $el[0].tagName,
				el : $el[0]
			});
		}

		addPlace(element);
	};
	Editor.prototype.initChooseAction = function() {};

	Editor.prototype.clickPage = function(el, index) {
		let remark = '';
		let model = null;
		let $el = null;
		if (index) {
			$el = this.$template.find('[coos-index="' + index + '"]');
			if (index == '0') {
				$el = this.$template;
			}
			model = $el.data('model');
			let ui = $el.data('ui');
			let group = $el.data('group');
			if (ui) {
				remark += ui.title + "/";
			}
			if (group) {
				remark += group.title + "/";
			}
			if (model) {
				remark += model.title;
			}
		}
		this.choosePageTemplate($el, model);
		this.form_data.remark = remark;

		this.$template.find('.coos-choose-page-model').removeClass('coos-choose-page-model');
		this.$template.removeClass('coos-choose-page-model');
		$el.addClass('coos-choose-page-model');

		this.$pageBox.find('.coos-choose-page-model').removeClass('coos-choose-page-model');
		$(el).addClass('coos-choose-page-model');
	};
	Editor.prototype.choosePageTemplate = function($el, model) {
		this.lastChoosePageTemplate = $el;
		model = model || {};
		let data = {};
		let items = [];
		if (model.items) {
			items = model.items;
		}
		if (model.getData) {
			data = model.getData($el);
		}
		let $form = $(this.getFormBoxHtml());
		this.$formBox.empty();
		this.$formBox.append($form);
		this.form_data = {
			remark : "",
			data : data,
			rules : {},
			items : items,
			places : [],
			has_add_child : true,
			has_add_before : true,
			has_add_after : true,
			has_move_up : true,
			has_move_dw : true,
			has_remove : true,
			open_base : false
		}
		let that = this;
		let vue = new Vue({
			el : $form[0],
			data : this.form_data,
			methods : {
				add_child () {
					that.add_child();
				},
				add_before () {
					that.add_before();
				},
				add_after () {
					that.add_after();
				},
				move_up () {
					that.move_up();
				},
				move_dw () {
					that.move_dw();
				},
				remove () {
					that.remove();
				},
				formDataChange (value, name) {
					that.formDataChange(value, name);
				},
				clickPlace (place) {
					console.log(place);
				}
			}
		});

		this.initChoosePlaces($el);
		this.initChooseAction($el);
	};

	Editor.prototype.formDataChange = function(value, name) {
		let model = $(this.lastChoosePageTemplate).data('model');
		if (model && model.setData) {
			model.setData(this.lastChoosePageTemplate, this.form_data.data);
			this.changePage();
		}
	};

})();