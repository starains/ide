var Editor = function(options) {
	options = options || {};
	this.initOptions(options);
	this.init();
};
(function() {

	Editor.prototype.initOptions = function(options) {
		options = options || {};
		this.key = 'APP-MODEL-EDITOR';
		this.text = '模型设计';
		this.options = options;
		this.readyonly = options.readyonly;
		this.type = options.type;
		this.plugin = options.plugin;
		this.editor = options.editor;
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
		this.navs = this.getNavs();
	};

	Editor.prototype.getVersion = function() {
		return '1.0';
	};

	Editor.prototype.getSupportVersions = function() {
		return [ '1.0', '1.1', '1.2' ];
	};

	Editor.prototype.getApp = function() {
		return this.project.attribute.app;
	};


	Editor.prototype.init = function() {};


	Editor.prototype.build = function($box) {
		var that = this;
		$box.empty();
		var $design = $('<div class="editor-design app-model-editor  coos-scrollbar" />');
		$box.append($design);
		this.$design = $design;

		this.$design.on('scroll', function() {
			that.designScrollTop = that.$design.scrollTop();
		});
		$design.data('editor', this);


		var $help = $('<div class="editor-help" />');
		$box.append($help);
		this.$help = $help;
		$help.append(this.getHelpHtml());

		this.bindEvent();
	};
	Editor.prototype.getCodeByModel = function(model, callback) {
		let that = this;
		var data = {
			model : model,
			type : this.type,
			filename : this.file.name
		}
		source.plugin.app.event('toText', data, this.project).then(res => {
			callback && callback(res);
		});
	};
	Editor.prototype.loadLastContent = function(callback) {
		let that = this;
		var data = {
			model : this.model,
			type : this.type,
			filename : this.file.name
		}
		this.getCodeByModel(this.model, callback);
	};
	Editor.prototype.show = function(callback) {
		var that = this;
		var that = this;
		function view() {
			that.buildDesignView();
			callback && callback();
		}
		if (this.type) {
			this.editor.loadLastContent(function(content) {
				var data = {
					text : content,
					type : that.type,
					filename : that.file.name
				}
				if (that.lastText && that.lastText == data.text) {
					view();
				} else {
					var changed = false;
					if (that.lastText && that.lastText != data.text) {
						changed = true;
					}
					that.lastText = data.text;

					source.plugin.app.event('toModel', data, that.project).then(res => {
						if (res.errcode == 0) {
							if (res.value) {
								if (changed) {
									that.recordHistory();
								}
								var model = res.value;
								model.name = that.model.name;
								that.model = model;
							}
						} else {
							coos.box.error(res.errmsg);
						}
						view();
					});
				}
			});
		} else {
			view();
		}

	};
	Editor.prototype.buildDesignView = function(callBuild) {


		let version = this.getVersion();

		if (coos.isEmpty(this.model.designerversion)) {
			this.model.designerversion = version;
		}
		let supportVersions = this.getSupportVersions();

		if (supportVersions.indexOf(this.model.designerversion) < 0) {
			let error = '设计器不识别模型版本，当前设计器支持版本<span class="color-green pdlr-10">' + supportVersions.join('，') + '</span>！';
			var $design = this.$design;
			$design.empty();
			$design.append('<div class="color-red font-lg pdtb-80 text-center">' + error + '</div>');
			this.notSupportVersion = true;
			return;
		} else {
			if (this.notSupportVersion) {
				var $design = this.$design;
				$design.empty();
				this.notSupportVersion = false;
			}
		}

		this.buildDesign();
		if (this.designScrollTop >= 0) {
			this.$design.scrollTop(this.designScrollTop);
		}
	};

	Editor.prototype.changeModel = function(callBuild) {
		var that = this;
		if (coos.isEmpty(callBuild) || coos.isTrue(callBuild)) {
			this.buildDesignView();
		}
		coos.trimObject(this.model, [ undefined, null, "" ], true);
		this.lastModel = $.extend(true, {}, this.model);
		this.editor.onChange(true, this.lastModel, this);
	};


	Editor.prototype.setCacheData = function(data) {
		var that = this;
		this.show(function() {
			that.recordHistory();
			that.model = data;
			that.changeModel(true);
		});
	};

})();