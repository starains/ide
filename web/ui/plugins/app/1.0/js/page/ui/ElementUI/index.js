
(function() {
	var UI = Editor.Page.UI;
	var ElementUI = coos.createClass(UI);
	UI.ElementUI = ElementUI;

	ElementUI.prototype.getName = function() {
		return 'element-ui';
	};

	ElementUI.prototype.getTitle = function() {
		return 'Element UI';
	};

	ElementUI.prototype.getGroups = function() {
		let groups = [];

		groups.push(new ElementUI.BasicGroup());
		groups.push(new ElementUI.FormGroup());
		groups.push(new ElementUI.PanelGroup());
		groups.push(new ElementUI.DataGroup());

		return groups;
	};
})();