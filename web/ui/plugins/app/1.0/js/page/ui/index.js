
(function() {
	var PageEditor = Editor.Page;


	PageEditor.prototype.getUIList = function() {
		let list = [];
		list.push(this.getTagUI());
		list.push(this.getElementUI());

		return list;
	};
})();