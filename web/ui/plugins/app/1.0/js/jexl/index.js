(function() {
	var JexlEditor = coos.createClass(Editor);
	Editor.Jexl = JexlEditor;

	JexlEditor.prototype.isYaml = function() {
		return true;
	};
	JexlEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-jexl pd-20"></div>');
		$design.append($box);

		var model = this.model;

	};
})();