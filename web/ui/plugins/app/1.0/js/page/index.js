(function() {
	var PageEditor = coos.createClass(Editor);
	Editor.Page = PageEditor;

	PageEditor.prototype.isHtml = function() {
		return true;
	};

	PageEditor.prototype.buildDesign = function() {

		if (!this.pageEditor) {
			var that = this;
			this.pageEditor = new coos.PageEditor({
				onChange : function(page, code) {
					that.recordHistory();
					$.extend(true, that.model, page);
					that.changeModel(false, code);
					return that.model;
				}
			});
		}
		this.pageEditor.build(this.$design, this.model);
	};
})();