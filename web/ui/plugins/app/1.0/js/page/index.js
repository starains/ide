(function() {
	var PageEditor = coos.createClass(Editor);
	Editor.Page = PageEditor;

	PageEditor.prototype.buildDesign = function() {
		var that = this;
		var $design = this.$design;

		if ($design.find('.page-design-box').length > 0) {
			$design.find('.page-design-view-box').remove();
			var $pageBox = $('<div class="page-design-view-box"></div>');
			$design.find('.page-design-option-box').before($pageBox);
			this.buildPageView($pageBox);
		} else {
			var $box = $('<div class="page-design-box"></div>');
			var $pageBox = $('<div class="page-design-view-box"></div>');
			$box.append($pageBox);
			$design.append($box);
			this.buildPageUI($design);
			this.buildPageOption($design);
			this.buildPageView($pageBox);
		}
	};

})();