
(function() {
	var PageEditor = Editor.Page;

	PageEditor.prototype.initScript = function() {
		this.scriptOption = this.getScriptOption(this.page.script);
		this.scriptOption = this.scriptOption || {};
	};


})();