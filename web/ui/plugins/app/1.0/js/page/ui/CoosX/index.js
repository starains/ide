
(function() {
	var UI = Editor.Page.UI;
	var CoosUI = coos.createClass(UI);
	UI.CoosUI = CoosUI;

	CoosUI.prototype.getName = function() {
		return 'coos-ui';
	};

	CoosUI.prototype.getTitle = function() {
		return 'Coos UI';
	};

	CoosUI.prototype.getGroups = function() {
		let groups = [];

		groups.push(new CoosUI.BasicGroup());
		groups.push(new CoosUI.PanelGroup());

		return groups;
	};


})();