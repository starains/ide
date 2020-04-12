(function() {
	var PageEditor = Editor.Page;

	PageEditor.prototype.getLayoutID = function(layout) {
		let root = this.getLayoutRoot();
		if (root == layout) {
			return 0;
		} else {
			let id = null;
			Object.keys(this.layout_map).forEach(key => {
				if (this.layout_map[key] == layout) {
					id = key;
				}
			});
			if (id == null) {
				return coos.getNumber();
			}
			return id;
		}
	};

	PageEditor.prototype.getTemplateFromLayout = function(layout) {
		if (layout == null || layout.key == null) {
			return null;
		}

		return this.template_map[layout.key];
	};

	PageEditor.prototype.getLayoutFromEvent = function(e) {
		if (e == null || e.target == null) {
			return null;
		}

		return this.getLayoutFromEl(e.target);
	};

	PageEditor.prototype.getLayoutFromEl = function(el) {
		el = $(el);
		if (el.length == 0) {
			return null;
		}
		if (coos.isNotEmpty(el.attr(this.layout_id_name))) {
			return this.layout_map[el.attr(this.layout_id_name)];
		}
		return this.getLayoutFromEl(el.parent());
	};

	PageEditor.prototype.getLayoutRoot = function() {
		this.model.layout = this.model.layout || {};
		return this.model.layout;
	};

	PageEditor.prototype.getLayoutParent = function(layout, p) {
		let root = this.getLayoutRoot();

		if (layout == root) {
			return null;
		}
		if (p == null) {
			p = root;
		}
		let res = null;
		if (p.layouts) {
			if (p.layouts.indexOf(layout) >= 0) {
				res = p;
			} else {
				p.layouts.forEach(one => {
					if (res == null) {
						res = this.getLayoutParent(layout, one);
					}
				});
			}
		}
		return res;
	};
})();