var app = window.app || new Object();
window.app = app;
(function() {
	var Editor = function(options) {
		options = options || {};
		this.initOptions(options);
		this.init();
	};

	Editor.prototype.initOptions = function(options) {
		options = options || {};
		this.options = options;
		this.readyonly = options.readyonly;
		this.type = options.type;
		this.model_original = options.model || {};
		this.project = options.project || {};
		this.file = options.file;
		this.ENUM_MAP = source.ENUM_MAP || {};
		if (this.file && this.file.model && this.file.model.bean) {
			this.model = $.extend(true, {}, this.file.model.bean);
		} else {
			this.model = $.extend(true, {});
		}
		this.historys = [];
		this.data = {
			view : '',
			file : this.file
		};
	};

	Editor.prototype.init = function() {
		this.initView();
	};


	Editor.prototype.initView = function() {
		var $editorBox = $('<div class="editor-box" />');
		this.$editorBox = $editorBox;

		var $header = $('<div class="editor-header" />');
		var $body = $('<div class="editor-body" />');
		var $footer = $('<div class="editor-footer" />');
		var $code = $('<div class="editor-code" />');
		var $design = $('<div class="editor-design coos-scrollbar" />');
		var $help = $('<div class="editor-help" />');


		$editorBox.append($header);
		$editorBox.append($body);
		$editorBox.append($footer);

		$body.append($code);
		$body.append($design);
		$body.append($help);


		$header.append(this.getHeaderHtml());
		$body.append(this.getBodyHtml());
		$footer.append(this.getFooterHtml());
		$help.append(this.getHelpHtml());
	};

	Editor.prototype.viewCode = function() {
		if (this.data.view == 'code') {
			return;
		}
		this.data.view = 'code';
		this.$code.show();
		this.$design.hide();
		this.buildCode();


		if (this.lastModel != null) {
			let that = this;
			this.getCodeByModel(this.lastModel, function(res) {
				that.lastModel = null;
				that.lastText = res;
				that.setCode(res);
			});
		}

	};
	Editor.prototype.getCodeByModel = function(model, callback) {
		let that = this;
		var data = {
			model : model,
			type : this.type,
			filename : this.file.name
		}
		this.options.toText(data, function(res) {
			callback && callback(res);
		});
	};

	Editor.prototype.viewDesign = function(callback) {
		if (this.data.view == 'design') {
			return;
		}
		var that = this;
		function view() {
			that.data.view = 'design';
			that.$design.show();
			that.$code.hide();
			that.buildDesignView();
			callback && callback();
		}
		if (this.type) {
			var data = {
				text : this.getCode(),
				type : this.type,
				filename : this.file.name
			}
			if (that.lastText && that.lastText == data.text) {
				view();
			} else {
				var changed = false;
				if (that.lastText && that.lastText != data.text) {
					changed = true;
				}
				that.lastText = data.text;
				that.options.toModel(data, function(res) {
					if (res.errcode == 0) {
						if (changed) {
							that.recordHistory();
						}
						var model = res.value;
						model.name = that.model.name;
						that.model = model;
					} else {
						coos.box.error(res.errmsg);
					}
					view();
				});
			}
		} else {
			view();
		}

	};

	Editor.prototype.build = function($box) {
		var that = this;
		$box.empty();
		$box.append(this.$editorBox);

		this.vue = new Vue({
			data : this.data,
			el : this.$editorBox[0],
			methods : {
				viewCode : function() {
					that.viewCode();
				},
				viewDesign : function() {
					that.viewDesign();
				},
			}
		});
		this.$el = $(this.vue.$el);

		this.$el.data('editor', this);


		this.$header = this.$el.find('.editor-header');
		this.$body = this.$el.find('.editor-body');
		this.$footer = this.$el.find('.editor-footer');
		this.$code = this.$el.find('.editor-code');
		this.$design = this.$el.find('.editor-design');
		this.$help = this.$el.find('.editor-help');
		this.$design.on('scroll', function() {
			that.designScrollTop = that.$design.scrollTop();
		});

		this.bindEvent();
		this.viewCode();

	};

	Editor.prototype.buildDesignView = function(callBuild) {
		this.buildDesign();
		if (this.designScrollTop >= 0) {
			this.$design.scrollTop(this.designScrollTop);
		}
	};

	Editor.prototype.changeModel = function(callBuild, code) {
		var that = this;
		if (coos.isEmpty(callBuild) || coos.isTrue(callBuild)) {
			this.buildDesignView();
		}
		coos.trimObject(this.model, [ undefined, null, "" ], true);
		var data = {
			model : this.model,
			type : this.type,
			filename : this.file.name
		}
		this.lastModel = $.extend(true, {}, this.model);
		this.onChange('model', true);
	};


	Editor.prototype.changeCode = function(content) {
		if (coos.isEmpty(content) && coos.isEmpty(this.file.content)) {
			this.onChange('code', false);
		} else if (content == this.file.content) {
			this.onChange('code', false);
		} else {
			this.onChange('code', true);
		}
	};

	Editor.prototype.destroy = function() {
		this.codeMirror.doc.remove();
		$(this.$el).remove();
	};


	coos.editor = function(options) {
		options = options || {};
		options.type = options.type || '';
		var editor = null;
		switch (options.type.toUpperCase()) {
		case "APP":
			editor = new coos.Editor.App(options);
			break;
		case "JDBC":
			editor = new coos.Editor.Database(options);
			break;
		case "DATABASE":
			editor = new coos.Editor.Database(options);
			break;
		case "TABLE":
			editor = new coos.Editor.Table(options);
			break;
		case "DAO":
			editor = new coos.Editor.Dao(options);
			break;
		case "SERVICE":
			editor = new coos.Editor.Service(options);
			break;
		case "CACHE":
			editor = new coos.Editor.Cache(options);
			break;
		case "DICTIONARY":
			editor = new coos.Editor.Dictionary(options);
			break;
		case "CODE":
			editor = new coos.Editor.Code(options);
			break;
		case "THEME":
			editor = new coos.Editor.Theme(options);
			break;
		case "PAGE":
			editor = new coos.Editor.Page(options);
			break;
		case "PAGECOMPONENT":
			editor = new coos.Editor.PageComponent(options);
			break;
		case "RESOURCE":
			editor = new coos.Editor.Resource(options);
			break;
		case "PLUGIN":
			editor = new coos.Editor.Plugin(options);
			break;
		case "ATTRIBUTE":
			editor = new coos.Editor.Attribute(options);
			break;
		case "CONTROL":
			editor = new coos.Editor.Control(options);
			break;
		case "JEXL":
			editor = new coos.Editor.Jexl(options);
			break;
		default:
			editor = new coos.Editor(options);
			break;
		}
		return editor;
	};
	coos.Editor = Editor;
})();