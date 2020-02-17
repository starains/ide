(function() {
	var TableEditor = coos.createClass(coos.Editor);
	coos.Editor.Table = TableEditor;

	TableEditor.prototype.isYaml = function() {
		return true;
	};
	TableEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-table editor-case"></div>');
		$design.append($box);

		var model = this.model;

		var $ul = $('<ul />')
		$box.append($ul);


		var $li;

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">名称：</span>');
		var $input = $('<input class="input" name="name" readonly="readonly"/>');
		$input.val(model.name);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">注释：</span>');
		var $input = $('<input class="input" name="comment" />');
		$input.val(model.comment);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">所属库：</span>');

		var $input = $('<select class="input" name="databasename" ></select>');
		$li.append($input);
		$input.append('<option value="">默认库</option>');

		$(this.getBeans('DATABASE')).each(function(index, one) {
			$input.append('<option value="' + one.name + '">' + one.name + '</option>');
		});
		$input.val(model.databasename);
		$li.append($input);
		that.bindLiEvent($li, model);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">分表规则：</span>');
		var $input = $('<input class="input" name="partitiontablerule" />');
		$input.val(model.partitiontablerule);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		model.columns = model.columns || [];
		var columns = model.columns;

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">字段</span>');
		var $btn = $('<a class="mglr-10 coos-pointer color-green">添加字段</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var column = {};
			column.nullable = true;
			column.type = 12;
			column.length = 250;
			columns.push(column);
			column.name = "";
			that.changeModel();
		});

		$(columns).each(function(index, column) {
			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);

			$li.append('<span class="pdlr-10">名称：</span>');
			var $input = $('<input class="input" name="name" required/>');
			$input.val(column.name);
			$li.append($input);

			$li.append('<span class="pdlr-10">注释：</span>');
			var $input = $('<input class="input" name="comment" />');
			$input.val(column.comment);
			$li.append($input);

			$li.append('<span class="pdlr-10">类型：</span>');
			var $input = $('<select class="input" name="type" required></select>');
			$li.append($input);
			$(that.ENUM_MAP.COLUMN_TYPE).each(function(index, one) {
				$input.append('<option value="' + one.value + '">' + one.text + '</option>');
			});
			$input.val(column.type);
			$li.append($input);
			that.bindLiEvent($li, column);


			$li.append('<span class="pdlr-10">长度：</span>');
			var $input = $('<input class="input input-mini" name="length" required/>');
			$input.val(column.length);
			$li.append($input);
			that.bindLiEvent($li, column);

			$li.append('<span class="pdlr-10">数字位数：</span>');
			var $input = $('<input class="input input-mini" name="precision" />');
			$input.val(column.precision);
			$li.append($input);
			that.bindLiEvent($li, column);

			$li.append('<span class="pdlr-10">小数位数：</span>');
			var $input = $('<input class="input input-mini" name="scale" />');
			$input.val(column.scale);
			$li.append($input);
			that.bindLiEvent($li, column, false);


			if (coos.isTrue(column.primarykey)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="primarykey"  >主键</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="primarykey" >非主键</a>');
			}
			if (coos.isTrue(column.nullable)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="nullable"  >可空</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="nullable" >不可空</a>');
			}
			$li.find('.updatePropertyBtn').click(function() {
				var type = $(this).attr('property-type');
				if (!coos.isEmpty(type)) {
					var value = column[type];
					if (coos.isTrue(value)) {
						value = false;
					} else {
						value = true;
					}
					that.recordHistory();
					column[type] = value;
					that.changeModel();
				}
			});

			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				columns.splice(columns.indexOf(column), 1);
				that.changeModel();
			});

			var $btn = $('<a class="mgl-10 coos-pointer color-orange">上移</a>');
			$li.append($btn);
			$btn.click(function() {
				var index = columns.indexOf(column);
				if (index > 0) {
					that.recordHistory();
					columns[index] = columns.splice(index - 1, 1, columns[index])[0];
					that.changeModel();
				}
			});

			var $btn = $('<a class="mgl-10 coos-pointer color-orange">下移</a>');
			$li.append($btn);
			$btn.click(function() {
				var index = columns.indexOf(column);
				if (index < columns.length - 1) {
					that.recordHistory();
					columns[index] = columns.splice(index + 1, 1, columns[index])[0];
					that.changeModel();
				}
			});

		});


	};
})();