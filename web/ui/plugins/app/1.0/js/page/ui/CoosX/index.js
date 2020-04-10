
(function() {
	var CoosXUI = coos.createClass(Editor.Page.UI);

	Editor.Page.UI.CoosXUI = CoosXUI;

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