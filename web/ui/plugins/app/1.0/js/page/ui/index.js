
(function() {
	var UI = function(options) {
		options = options || {};
		this.options = options;
		this.init();
	};
	Editor.Page.UI = UI;

	UI.colors = [];
	UI.bgs = [];
	coos.style.config.colors.forEach(color => {
		UI.colors.push({
			text : color.text,
			value : color.value,
			colors : color.colors,
			color : color.value,
			bg : color.value == 'white' ? 'black' : ''
		});
		UI.bgs.push({
			text : color.text,
			value : color.value,
			colors : color.colors,
			color : color.value == 'white' ? 'black' : 'white',
			bg : color.value
		});
	});

	UI.sizes = [];
	coos.style.config.sizes.forEach(size => {
		UI.sizes.push({
			text : size.text,
			value : size.name,
			size : size
		});
	});

	UI.distances = [];
	coos.style.config.distances.forEach(distance => {
		UI.distances.push({
			text : distance + 'px',
			value : distance
		});
	});

	UI.cols = [];
	coos.style.config.cols.forEach(col => {
		UI.cols.push({
			text : col.text,
			value : col.value
		});
	});

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
		this._init();
	};

	Template.prototype._init = function() {
		this.name = this.getName();
		this.title = this.getTitle();
		this.code = this.getCode();
		this.option = this.getOption();
		this.demos = this.getDemos();
		this.attrs = this.getAttrs();
		if (this.getNavs) {
			this.navs = this.getNavs();
		}
		this.init();
	};

	Template.prototype.init = function() {};

	UI.Group = Group;
	UI.Template = Template;
})();