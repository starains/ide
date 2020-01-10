
(function() {
	var Editor = coos.PageEditor;

	Editor.prototype.initScript = function() {
		this.scriptOption = this.getScriptOption(this.page.script);
		this.scriptOption = this.scriptOption || {};
	};


})();