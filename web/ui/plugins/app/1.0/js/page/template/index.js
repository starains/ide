
(function() {
	var PageEditor = Editor.Page;

	PageEditor.prototype.initTemplate = function() {

		this.$template = $('<div class="coos-page"/>');
		this.$template.html(this.page.template);
		if (this.$template.find('.coos-page').length > 0) {
			this.$template = $(this.page.template);
		}


		let that = this;

		let click_page_method_name = 'click_page_' + coos.getNumber();
		this.click_page_method_name = click_page_method_name;
		window[click_page_method_name] = function(event, index) {
			window.event.preventDefault();
			window.event.stopPropagation();
			that.clickPage(event.currentTarget, index);
		};
		this.bindTemplateEvent(this.$template);


	};

	PageEditor.prototype.bindTemplateEvent = function($template) {
		this.removeTemplateEvent($template);
		let that = this;
		let uis = this.data.uis;
		let index = 0;
		function bind(el) {
			let $el = $(el);
			uis.forEach((ui) => {
				ui.groups.forEach((group) => {
					group.models.forEach((model) => {
						if (model.eq && model.eq($el)) {
							index++;
							$el.data("ui", ui);
							$el.data("group", group);
							$el.data("model", model);
							let oldClick = $el.attr('v-on:click');
							$el.attr('coos-index', '' + index);
							$el.attr('v-on:click', that.click_page_method_name + '($event, "' + index + '")');
							if (!coos.isEmpty(oldClick)) {
								$el.attr('v-on:old-click', oldClick)
							}
						}
					});
				});
			});
			$el.children().each(function(i, element) {
				bind(element);
			});
		}
		$template.attr('coos-index', '' + index);
		$template.data("model", {
			title : "页面"
		});
		bind($template);
	};

	PageEditor.prototype.removeTemplateEvent = function($template) {
		function remove(el) {
			let $el = $(el);
			if ($el.attr('coos-index')) {
				$el.removeAttr('coos-index')
				$el.removeAttr('v-on:click')
				let oldClick = $el.attr('v-on:old-click');
				if (!coos.isEmpty(oldClick)) {
					$el.attr('v-on:click', oldClick);
				}
			}
			$el.children().each(function(i, element) {
				remove(element);
			});
		}

		remove($template);
	};


})();