(function() {
	var BeanEditor = coos.createClass(Editor);
	Editor.Bean = BeanEditor;

	BeanEditor.prototype.isYaml = function() {
		return true;
	};
	BeanEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-bean editor-case"></div>');
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
		$li.append('<span class="pdr-10">绑定表：</span>');

		var $input = $('<input class="input" name="tablename" />');
		app.autocomplete({
			$input : $input,
			datas : that.getOptions('TABLE')
		})
		$input.val(model.tablename);
		$li.append($input);
		that.bindLiEvent($li, model, true);


		model.propertys = model.propertys || [];
		var propertys = model.propertys;

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">属性</span>');
		var $btn = $('<a class="mglr-10 coos-pointer color-green">添加属性</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var property = {};
			property.type = 'String';
			property.name = "";
			propertys.push(property);
			that.changeModel();
		});

		let table = that.getTableByName(model.tablename);
		if (table && table.columns && table.columns.length > 0) {
			var $btn = $('<a class="mglr-10 coos-pointer color-green">自动导入表字段</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				coos.trimArray(propertys);

				table.columns.forEach(one => {
					var property = {};
					var columntype = one.type;
					var type = 'String';
					if (columntype == '4') {
						type = 'Integer';
					} else if (columntype == '16') {
						type = 'Boolean';
					} else if (columntype == '8') {
						type = 'Double';
					} else if (columntype == '-5') {
						type = 'Long';
					}
					property.type = type;
					property.name = one.name;
					property.comment = one.comment;
					property.columnname = one.name;
					propertys.push(property);
				});
				that.changeModel();
			});
		}

		$(propertys).each(function(index, property) {
			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);

			$li.append('<span class="pdlr-10">名称：</span>');
			var $input = $('<input class="input" name="name" required/>');
			$input.val(property.name);
			$li.append($input);

			$li.append('<span class="pdlr-10">注释：</span>');
			var $input = $('<input class="input" name="comment" />');
			$input.val(property.comment);
			$li.append($input);

			$li.append('<span class="pdlr-10">类型：</span>');
			var $input = $('<select class="input" name="type" required></select>');
			$li.append($input);
			$input.append('<option value="String">String</option>');
			$input.append('<option value="Integer">Integer</option>');
			$input.append('<option value="Long">Long</option>');
			$input.append('<option value="Boolean">Boolean</option>');
			$input.append('<option value="Double">Double</option>');
			$input.append('<option value="Byte">Byte</option>');
			$input.append('<option value="Char">Char</option>');
			$input.append('<option value="int">int</option>');
			$input.append('<option value="long">long</option>');
			$input.append('<option value="char">char</option>');
			$input.append('<option value="byte">byte</option>');
			$input.append('<option value="double">double</option>');
			$input.val(property.type);
			$li.append($input);
			that.bindLiEvent($li, property);


			$li.append('<span class="pdlr-10">字段名：</span>');
			var $input = $('<input class="input " name="columnname" />');
			$input.val(property.columnname);
			$li.append($input);
			that.bindLiEvent($li, property);

			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				propertys.splice(propertys.indexOf(property), 1);
				that.changeModel();
			});

			var $btn = $('<a class="mgl-10 coos-pointer color-orange">上移</a>');
			$li.append($btn);
			$btn.click(function() {
				var index = propertys.indexOf(property);
				if (index > 0) {
					that.recordHistory();
					propertys[index] = propertys.splice(index - 1, 1, propertys[index])[0];
					that.changeModel();
				}
			});

			var $btn = $('<a class="mgl-10 coos-pointer color-orange">下移</a>');
			$li.append($btn);
			$btn.click(function() {
				var index = propertys.indexOf(property);
				if (index < propertys.length - 1) {
					that.recordHistory();
					propertys[index] = propertys.splice(index + 1, 1, propertys[index])[0];
					that.changeModel();
				}
			});

		});


	};
})();