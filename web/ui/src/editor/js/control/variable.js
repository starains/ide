(function() {
	var ControlEditor = coos.Editor.Control;

	ControlEditor.prototype.createMethodVariableView = function(variable, variables) {
		var that = this;
		var $box = $('<li />');

		var $ul = $('<ul class=""/>');
		$box.append($ul);


		var $li = $('<li />');
		$ul.append($li);


		$li.append('<span class="pdr-10">variableCache.put("</span>');
		var $input = $('<input class="input" name="name" />');
		$input.val(variable.name);
		$li.append($input);
		$li.append('<span class="pdr-10">", AppFactory.getValueByJexlScript("</span>');

		$li.append('<span class="pdr-10"></span>');
		var $input = $('<input class="input" name="value" />');
		$input.val(variable.value);
		$li.append($input);

		$li.append('<span class="pdr-10">或默认值</span>');
		var $input = $('<input class="input" name="defaultvalue" />');
		$input.val(variable.defaultvalue);
		$li.append($input);

		$li.append('<span class="pdr-10">", variableCache) 自定义类取值</span>');
		var $input = $('<input class="input" name="valuer" />');
		$input.val(variable.valuer);
		$li.append($input);

		$li.append('<span class="pdr-10">);</span>');

		that.bindLiEvent($li, variable, false);

		var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			variables.splice(variables.indexOf(variable), 1);
			that.changeModel();
		});
		return $box;

	};
})();