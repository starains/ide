(function() {

	var app = window.app = new Object();

	app.appendCSS = function(css) {
		document.writeln('<link rel="stylesheet" type="text/css" href="' + _ROOT_URL + css + '" />');
	};

	app.appendJS = function(js) {
		document.writeln('<script type="text/javascript" src="' + _ROOT_URL + js + '"></script>');
	};
	app.appendCSS('resources/coos/merge/ide.css');
	app.appendJS('resources/coos/merge/ide.js');
	app.appendCSS('resources/coos/merge/editor.css');
	app.appendJS('resources/coos/merge/editor.js');

})();