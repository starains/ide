(function() {
	var DaoEditor = coos.Editor.Dao;

	DaoEditor.prototype.createSqlUpdateView = function(model) {
		var that = this;
		var $box = $('<li />');
		model = model || {};
		model.columns = model.columns || [];
		model.wheres = model.wheres || [];
		var columns = model.columns;
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
		$li.append('<span class="pdr-10 color-orange">UPDATE</span>');

		var $input = $('<input class="input" name="table" />');
		app.autocomplete({
			$input : $input,
			datas : that.getOptions('TABLE')
		})
		$input.val(model.table);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		var $btn = $('<a class="mglr-10 coos-pointer color-green">添加更新字段</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var column = {};
			columns.push(column);
			column.name = "";
			that.changeModel();
		});


		$(columns).each(function(index, column) {
			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);

			var $subUl = $('<ul class=""/>');
			$li.append($subUl);

			var $li = $('<li class=""/>');
			$subUl.append($li);

			if (!coos.isEmpty(column.ifrule)) {
				$li.append('<span class="pdlr-10">if (</span>');
				var $input = $('<input class="input input-mini" name="ifrule" />');
				$input.val(column.ifrule);
				$li.append($input);
				$li.append('<span class="pdlr-10">) //不填或值为true、1则为真</span>');

				var $li = $('<li class="pdl-30"/>');
				$subUl.append($li);
			}

			var $input = $('<input class="input" name="name" />');
			app.autocomplete({
				$input : $input,
				datas : that.getModelColumnOptions()
			})
			$input.val(column.name);
			$li.append($input);
			$li.append('<span class="mglr-10">=</span>');

			var $input = $('<input class="input input-mini " name="value" />');
			$input.val(column.value);
			$li.append($input);
			$li.append('<span class="pdr-10">或</span>');
			$li.append('<span class="">自动取名称相同的值  或</span>');
			var $input = $('<input class="input input-mini " name="defaultvalue" />');
			$input.val(column.defaultvalue);
			$li.append($input);
			$li.append('<span class="pdr-10"></span>');


			if (coos.isEmpty(column.ifrule)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="ifrule" property-value="1=1"  >设置条件</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="ifrule" property-value="">清空条件</a>');
			}

			if (!coos.isTrue(column.required)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="required" >设为必填</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="required" >设为非必填</a>');
			}
			var $btn = $('<a class="mgl-10 coos-pointer color-orange">修改</a>');
			$li.append($btn);
			$btn.click(function() {});
			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				columns.splice(columns.indexOf(column), 1);
				that.changeModel();
			});

			that.bindPropertyEvent($li, column);
			that.bindLiEvent($subUl, column, false);
		});

		var table = that.getTableByName(model.table);
		that.appendWhereLi($ul, wheres, table);

		return $box;
	};

})();