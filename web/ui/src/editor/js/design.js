(function() {
	var Editor = coos.Editor;


	Editor.prototype.buildDesign = function() {


		var that = this;
		var $design = this.$design;
		$design.empty();
		$design.append('<div class="color-orange font-lg pd-80 text-center">暂未提供该文件格式在线设计，敬请期待！</div>');
	};
})();