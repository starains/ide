
(function() {
	var UI = Editor.Page.UI;
	var CoosXUI = coos.createClass(UI);
	UI.CoosXUI = CoosXUI;

	CoosXUI.prototype.getName = function() {
		return 'coosx-ui';
	};

	CoosXUI.prototype.getTitle = function() {
		return 'CoosX UI';
	};

	CoosXUI.prototype.getGroups = function() {
		let groups = [];

		groups.push(new CoosXUI.BaseGroup());

		return groups;
	};


})();