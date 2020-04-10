
(function() {
	var BaseGroup = coos.createClass(Editor.Page.UI.Group);

	Editor.Page.UI.CoosXUI.BaseGroup = BaseGroup;

	BaseGroup.prototype.getName = function() {
		return 'base';
	};

	BaseGroup.prototype.getTitle = function() {
		return '基础';
	};

	BaseGroup.prototype.getTemplates = function() {
		let templates = [];

		templates.push(new Btn());

		return templates;
	};

	var Btn = coos.createClass(Editor.Page.UI.Template);

	Btn.prototype.getName = function() {
		return 'btn';
	};

	Btn.prototype.getTitle = function() {
		return '按钮';
	};

	Btn.prototype.getCode = function() {
		return `<a class="coos-btn" v-bind:class="classObject" >按钮</a>`;
	};

	Btn.prototype.getOption = function() {
		let option = {};
		return option;
	};

	Btn.prototype.getDemos = function() {
		let demos = [];

		let computed = {
			classObject : function() {
				let res = {};
				if (coos.isNotEmpty(this.color)) {
					res['color-' + this.color] = true;
				}
				if (coos.isNotEmpty(this.bg)) {
					res['bg-' + this.bg] = true;
				}
				if (coos.isNotEmpty(this.size)) {
					res['coos-btn-' + this.size] = true;
				}
				return res;
			}
		}

		demos.push({
			computed ,
			data : {
				color : null,
				size : null
			}
		});

		coos.style.config.colors.forEach(color => {
			if (coos.isNotEmpty(color.value) && color.value != 'white') {
				demos.push({
					computed ,
					data : {
						color : color.value
					}
				});
			}
		});

		demos.push({
			divider : true
		});

		coos.style.config.colors.forEach(color => {
			if (coos.isNotEmpty(color.value) && color.value != 'white') {
				demos.push({
					computed ,
					data : {
						bg : color.value
					}
				});
			}
		});

		demos.push({
			divider : true
		});

		coos.style.config.sizes.forEach(size => {
			if (coos.isNotEmpty(size.name)) {
				demos.push({
					computed ,
					data : {
						size : size.name
					}
				});

			}
		});

		return demos;
	};
})();