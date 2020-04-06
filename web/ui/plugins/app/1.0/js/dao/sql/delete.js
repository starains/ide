(function() {
	var DaoEditor = Editor.Dao;

	DaoEditor.prototype.createSqlDeleteView = function(model) {
		var that = this;
		var $box = $('<li />');
		model = model || {};
		model.wheres = model.wheres || [];
		var wheres = model.wheres;

		var $ul = $('<ul class="sub1"/>')
		$box.append($ul);

		var $li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10 color-orange">配置data</span>');
		var $input = $('<input class="input" name="data" />');
		$input.val(model.data);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10 color-orange">DELETE FROM</span>');

		var $input = $('<input class="input" name="table" />');
		app.autocomplete({
			$input : $input,
			datas : that.getOptions('TABLE')
		})
		$input.val(model.table);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		var table = that.getTableByName(model.table);
		wheres.is_delete = true;
		that.appendWhereLi($ul, wheres, table);

		model.appends = model.appends || [];
		that.appendAppends($ul, model.appends, table);
		return $box;
	};

})();