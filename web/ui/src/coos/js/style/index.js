(function() {
	let style = co.style;
	style.initStyle = function(arg) {

		if (window._COOS_STYLE_DOCUMENT == null) {
			let heads = document.getElementsByTagName('head');
			if (heads != null && heads.length > 0) {
				let head = heads[0];
				let dom = document.createElement('style');
				dom.setAttribute('id', 'COOS-STYLE');
				dom.setAttribute('type', 'text/css');
				if (head.firstChild) {
					head.insertBefore(dom, head.firstChild);
				} else {
					head.appendChild(dom);
				}
				window._COOS_STYLE_DOCUMENT = dom;
			}

			if (window._COOS_STYLE_DOCUMENT != null) {
				window._COOS_STYLE_BASE_NODE = document.createTextNode("");
				window._COOS_STYLE_DOCUMENT.appendChild(window._COOS_STYLE_BASE_NODE);

				window._COOS_STYLE_COMPONENT_NODE = document.createTextNode("");
				window._COOS_STYLE_DOCUMENT.appendChild(window._COOS_STYLE_COMPONENT_NODE);
			}
		}
	};
	style.appendBase = function(css) {
		style.initStyle();
		if (window._COOS_STYLE_BASE_NODE != null && css) {
			window._COOS_STYLE_BASE_NODE.appendData(css);
		}
	};
	style.appendComponent = function(css) {
		style.initStyle();
		if (window._COOS_STYLE_COMPONENT_NODE != null && css) {
			window._COOS_STYLE_COMPONENT_NODE.appendData(css);
		}
	};

	style.init = function() {
		var css = '';
		css += style.getBaseCSS();
		css += style.getSizesCSS();
		css += style.getDistancesCSS();
		css += style.getColsCSS();
		css += style.getColorsCSS();
		style.appendBase(css);
		css = style.getComponentsCSS();
		style.appendComponent(css);
	};

	style.writeColorCSS = function(one) {
		var css = style.getColorCSS(one);
		style.appendBase(css);
		css = style.getComponentCSS(one);
		style.appendComponent(css);
	};
	var map = {};
	style.addColor = function(main) {
		if (map[main]) {
			return map[main];
		}
		if (co.isEmpty(main) || main.indexOf('#') != 0) {
			return;
		}
		var one = {};
		var color = new co.Color({
			main : main
		});
		one.colors = color.getColors();
		one.text = main;
		one.value = main.substring(1);
		style.writeColorCSS(one);
		map[main] = one;
		return one;
	};

	style.getColorClass = function(color) {
		if (co.isEmpty(color) || color.indexOf('{') >= 0) {
			return "";
		}
		if (color.indexOf('#') == 0) {
			style.addColor(color);
			color = color.substring('1');
		}
		return 'color-' + color;
	};
	style.getBGColorClass = function(color) {
		if (co.isEmpty(color) || color.indexOf('{') >= 0) {
			return "";
		}
		if (color.indexOf('#') == 0) {
			style.addColor(color);
			color = color.substring('1');
		}
		return 'bg-' + color;
	};

	style.getActiveColorClass = function(color) {
		if (co.isEmpty(color) || color.indexOf('{') >= 0) {
			return "";
		}
		if (color.indexOf('#') == 0) {
			style.addColor(color);
			color = color.substring('1');
		}
		return 'active-color-' + color;
	};
	style.getActiveBGColorClass = function(color) {
		if (co.isEmpty(color) || color.indexOf('{') >= 0) {
			return "";
		}
		if (color.indexOf('#') == 0) {
			style.addColor(color);
			color = color.substring('1');
		}
		return 'active-bg-' + color;
	};
	style.init();
})();