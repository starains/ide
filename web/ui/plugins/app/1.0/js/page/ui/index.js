
(function() {
	var UI = function(options) {
		options = options || {};
		this.options = options;
		this.init();
	};
	Editor.Page.UI = UI;

	UI.prototype.init = function() {
		this.name = this.getName();
		this.title = this.getTitle();
		this.groups = this.getGroups();
	};

	var Group = function(options) {
		options = options || {};
		this.options = options;
		this.init();
	};

	Group.prototype.init = function() {
		this.name = this.getName();
		this.title = this.getTitle();
		this.templates = this.getTemplates();
	};

	var Template = function(options) {
		options = options || {};
		this.options = options;
		this.init();
	};

	Template.prototype.init = function() {
		this.name = this.getName();
		this.title = this.getTitle();
		this.code = this.getCode();
		this.option = this.getOption();
		this.demos = this.getDemos();
	};

	UI.Group = Group;
	UI.Template = Template;
})();