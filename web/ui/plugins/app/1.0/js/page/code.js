
(function() {
	var PageEditor = Editor.Page;

	let BEAN_START = "<!-- page bean start -->";
	let BEAN_END = "<!-- page bean end -->";

	let TEMPLATE_START = "<!-- page template start -->";
	let TEMPLATE_END = "<!-- page template end -->";

	let SCRIPT_START = "<!-- page script start -->";
	let SCRIPT_END = "<!-- page script end -->";

	let OPTION_START = "<!-- vue option start -->";
	let OPTION_END = "<!-- vue option end -->";

	let STYLE_START = "<!-- page style start -->";
	let STYLE_END = "<!-- page style end -->";

	PageEditor.prototype.toCode = function(page) {
		page = page || {};
		let html = '';

		html += BEAN_START + '\n';
		html += '<page>\n';
		if (!coos.isEmpty(page.comment)) {
			html += '\t<comment>' + page.comment + '</comment>\n';
		}
		if (!coos.isEmpty(page.requestmapping)) {
			html += '\t<requestmapping>' + page.requestmapping + '</requestmapping>\n';
		}
		html += '</page>\n';
		html += BEAN_END + '\n\n';

		html += TEMPLATE_START + '\n';
		html += '<template>\n';
		if (!coos.isEmpty(page.template)) {
			html += page.template;
		}
		html += '</template>\n';
		html += TEMPLATE_END + '\n\n';

		html += SCRIPT_START + '\n';
		html += '<script>\n';
		if (!coos.isEmpty(page.script)) {
			html += page.script;
		}
		html += '</script>\n';
		html += SCRIPT_END + '\n\n';

		html += STYLE_START + '\n';
		html += '<style>\n';
		if (!coos.isEmpty(page.style)) {
			html += page.style;
		}
		html += '</style>\n';
		html += STYLE_END + '\n\n';
		return html;
	};


	PageEditor.prototype.getScriptOption = function(script) {
		let optionStr = "let option = {};";
		script = script || '';
		let start = script.indexOf(OPTION_START);
		let end = script.indexOf(OPTION_END);
		if (start >= 0 && end > start) {
			optionStr = script.substring(start + OPTION_START.length, end);
		}
		let result = toOption(optionStr);

		return result;
	};

	let toOption = function(optionStr) {
		try {
			return eval('(function(){' + optionStr + ';return option;})()');
		} catch (e) {
			console.log(e);
			coos.error(e.message);
		}
		return {};
	};
})();