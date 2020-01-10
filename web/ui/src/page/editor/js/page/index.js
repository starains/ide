
(function() {
	var Editor = coos.PageEditor;

	Editor.prototype.getPageBoxHtml = function() {};

	Editor.prototype.buildPage = function(page) {
		if (this.page && page) {
			if (this.page.template == page.template
				&& this.page.script == page.script
				&& this.page.style == page.style) {
				return;
			}
		}
		let index = '0';
		if (this.$template) {
			if (this.$template.hasClass('coos-choose-page-model')) {
				index = this.$template.attr('coos-index');
			} else {
				index = this.$template.find('.coos-choose-page-model').attr('coos-index');
			}
		}
		if (page != null) {
			this.page = page ;
			this.initTemplate();
			this.initScript();
		}

		let that = this;

		let $pageBox = this.$pageBox;
		$pageBox.empty();
		$pageBox.append(this.$template);

		let el = this.$template[0];
		this.scriptOption.el = el;
		let vue = new Vue(this.scriptOption);

		this.bindPageEvent();
		let e;
		if (index == '0') {
			e = $(vue.$el);
		} else {
			e = $(vue.$el).find('[coos-index="' + index + '"]');
		}
		if (e.length > 0) {
			this.clickPage(e[0], index);
		}

	};

	Editor.prototype.changePage = function() {
		let that = this;

		this.bindTemplateEvent(this.$template);

		let $t = that.$template.clone();

		$t.find('.coos-choose-page-model').removeClass('coos-choose-page-model');
		$t.removeClass('coos-choose-page-model');

		this.removeTemplateEvent($t);

		let template = $t[0].outerHTML;
		template = '\t' + template + '\n';
		let script = that.page.script;
		let style = that.page.style;

		let page = {};
		Object.assign(page, that.page);
		page.template = template;
		page.style = style;
		page.script = script;
		let code = that.toCode(page);

		let page_ = that.options.onChange(page, code);
		that.refreshPage();
	};


	Editor.prototype.refreshPage = function() {
		this.buildPage();
	};


})();