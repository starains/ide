(function() {

	var app = window.app = new Object();

	app.appendCSS = function(css) {
		document.writeln('<link rel="stylesheet" type="text/css" href="' + _ROOT_URL + css + '" />');
	};

	app.appendJS = function(js) {
		document.writeln('<script type="text/javascript" src="' + _ROOT_URL + js + '"></script>');
	};
	app.appendCSS('resources/ide/css/ide.css');
	app.appendJS('resources/ide/js/ide.js');
	app.appendCSS('resources/editor/css/editor.css');
	app.appendJS('resources/editor/js/editor.js');

})();