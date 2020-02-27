(function() {
	var ControlEditor = coos.createClass(coos.Editor);
	coos.Editor.Control = ControlEditor;

	ControlEditor.prototype.isYaml = function() {
		return true;
	};
	ControlEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-control pd-20"></div>');
		$design.append($box);

		var model = this.model;

	};
})();