(function() {
	var Editor = coos.Editor;

	Editor.prototype.onChange = function() {};

	Editor.prototype.toPreviousStep = function() {
		if (this.historys.length == 0) {
			return;
		}
		if (coos.isEmpty(this.stepindex)) {
			this.recordHistory();
			this.stepindex = this.historys.length - 1;
		}
		if (this.stepindex <= 0) {
			return;
		}
		this.stepindex--;
		this.model = this.historys[this.stepindex];
		this.changeModel();
		if (this.stepindex > 0) {
			this.previous_step_nav.disabled = false;
		} else {
			this.previous_step_nav.disabled = true;
		}
		this.next_step_nav.disabled = false;
	};

	Editor.prototype.toNextStep = function() {
		if (this.historys.length == 0) {
			return;
		}
		if (coos.isEmpty(this.stepindex)) {
			return;
		}
		if (this.stepindex >= this.historys.length - 1) {
			return;
		}
		this.stepindex++;
		this.model = this.historys[this.stepindex];
		this.changeModel();
		if (this.stepindex >= this.historys.length - 1) {
			this.next_step_nav.disabled = true;
		} else {
			this.next_step_nav.disabled = false;
		}
		this.previous_step_nav.disabled = false;
	};

	Editor.prototype.toReset = function() {};

	Editor.prototype.toReload = function() {};

	Editor.prototype.toExport = function() {};

	Editor.prototype.toImport = function() {};

	Editor.prototype.toSave = function() {

		let that = this;
		if (this.options.onSave) {
			if (this.lastModel != null) {
				this.getCodeByModel(this.lastModel, function(res) {
					that.setCode(res);
					that.options.onSave(that.getCode());
				});
			} else {
				this.options.onSave(this.getCode());
			}
		}
	};

	Editor.prototype.toSetting = function() {};

	Editor.prototype.getTableByName = function() {};

	Editor.prototype.hasTest = function() {
		return false;
	};

	Editor.prototype.toTest = function() {};

	Editor.prototype.hasHelp = function() {
		return true;
	};

	Editor.prototype.getHelpHtml = function() {

		return Editor.help_html;
	};

	Editor.prototype.toHelp = function() {
		if (this.$help.hasClass('open-help')) {
			this.$help.removeClass('open-help');
		} else {
			this.$help.addClass('open-help');
		}

	};

	Editor.prototype.hasSetting = function() {
		return false;
	};
	Editor.prototype.getContext = function(type) {
		var context = null;
		if (this.project && this.project.app) {
			context = this.project.app.context;
		}
		return context;
	};
	Editor.prototype.getBeans = function(type) {
		var list = [];
		var context = this.getContext();
		if (context) {
			list = context[type] || [];
		}
		return list;
	};
	Editor.prototype.getOptions = function(type) {
		var list = this.getBeans(type);
		let options = this.appendOptions([], list)

		return options;
	};
	Editor.prototype.getTableByName = function(table) {
		var list = this.getBeans('TABLE');
		let res = [];
		list.forEach((one) => {
			if (one.name == table) {
				res = one;
			}
		});

		return res;
	};
	Editor.prototype.appendOptions = function(options, list) {
		options = options || [];
		list = list || [];
		$(list).each(function(index, one) {
			var text = one.name;
			if (!coos.isEmpty(one.comment)) {
				text += '（' + one.comment + '）';
			}
			options.push({
				text : text,
				value : one.name
			})
		});

		return options;
	};
	Editor.prototype.bindLiEvent = function($li, data, callBuild) {
		var that = this;
		if (data) {
			$li.find('.input').attr('autocomplete', 'off');
			$li.find('.input').each(function(index, input) {
				initWidth(input);
			});
			function initWidth(input) {
				if ($(input)[0].tagName != 'SELECT' && $(input)[0].tagName != 'TEXTAREA') {
					var length = ($(input).val() || '').length;
					var $span = $('<span />');
					$span.text($(input).val() || '');
					$span.css('font-size', '13px');
					$span.css('padding-left', '5px');
					$span.css('padding-right', '5px');
					$('body').append($span);
					var width = $span.outerWidth();
					$(input).css('width', width);
					$span.remove();
				}
			}
			$li.find('.input').on('input', function() {
				initWidth(this);
			});
			$li.find('.input').on('change', function() {
				var name = $(this).attr('name');
				var pattern = $(this).attr('pattern');
				var value = $(this).val() || '';
				if (coos.isEmpty(value)) {
					if (coos.isRequired(this)) {
						$(this).addClass('error');
						return;
					}
				}
				if (!coos.isEmpty(pattern) && !new RegExp(pattern).test(value)) {
					$(this).addClass('error');
					return;
				}
				$(this).removeClass('error');
				if (!coos.isEmpty(name)) {
					that.recordHistory();
					data[name] = value;
					that.changeModel(callBuild);
				}
			});
		}
	};
})();