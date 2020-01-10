
(function() {
	var Editor = coos.PageEditor;


	Editor.prototype.getUIList = function() {
		let list = [];
		list.push(this.getTagUI());
		list.push(this.getElementUI());

		return list;
	};
})();