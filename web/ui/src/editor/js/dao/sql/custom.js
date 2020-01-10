(function() {
	var DaoEditor = coos.Editor.Dao;

	DaoEditor.prototype.createSqlCustomSqlView = function(model) {
		var that = this;
		var $box = $('<li />');
		model = model || {};

		var $ul = $('<ul class="one-sql"/>')
		$box.append($ul);

		var $li = $('<li />');
		$ul.append($li);
		$li.append('<span ">自定义SQL类型</span>');

		var $input = $('<select class="input mgr-10" name="customsqltype" ></select>');
		$li.append($input);
		$input.append('<option value="">非查询（execute sql）</option>');
		$input.append('<option value="SELECT_ONE">查询单个</option>');
		$input.append('<option value="SELECT_LIST">查询列表</option>');
		$input.append('<option value="SELECT_PAGE">分页查询</option>');
		model.customsqltype = model.customsqltype || '';
		$input.val(model.customsqltype);

		that.bindLiEvent($li, model);


		var $li = $('<li />');
		$ul.append($li);
		$li.append('<span ">SQL</span>');

		var $li = $('<li class="pdl-10"/>');
		$ul.append($li);
		var $input = $('<textarea class="input" name="sql" />');
		$input.val(model.sql);
		$li.append($input);
		that.bindLiEvent($li, model, false);


		var $li = $('<li />');
		$ul.append($li);
		$li.append('<span ">COUNT SQL</span>');

		var $li = $('<li class="pdl-10"/>');
		$ul.append($li);
		var $input = $('<textarea class="input" name="countsql" />');
		$input.val(model.countsql);
		$li.append($input);
		that.bindLiEvent($li, model, false);



		return $box;
	};

})();