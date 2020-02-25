(function() {
	var DaoEditor = coos.Editor.Dao;
	DaoEditor.prototype.getModelColumnOptions = function() {
		let process = this.model.process || {};
		var sqlType = process.sqlType;
		let that = this;
		let options = [];

		let tables = [];

		if (sqlType.indexOf("SELECT") >= 0 || sqlType.indexOf("SAVE") >= 0) {
			$(process.select.froms).each(function(i, from) {
				let table = that.getTableByName(from.table);
				if (table && tables.indexOf(table) < 0) {
					tables.push(table);
				}
			});
		}
		if (sqlType.indexOf("INSERT") >= 0 || sqlType.indexOf("SAVE") >= 0) {
			let table = that.getTableByName(process.insert.table);
			if (table && tables.indexOf(table) < 0) {
				tables.push(table);
			}
		}
		if (sqlType.indexOf("UPDATE") >= 0 || sqlType.indexOf("SAVE") >= 0) {
			let table = that.getTableByName(process.update.table);
			if (table && tables.indexOf(table) < 0) {
				tables.push(table);
			}
		}
		if (sqlType.indexOf("DELETE") >= 0) {
			let table = that.getTableByName(process.delete.table);
			if (table && tables.indexOf(table) < 0) {
				tables.push(table);
			}
		}
		$(tables).each(function(i, table) {
			that.appendOptions(options, table.columns);
		});
		return options;
	};

	DaoEditor.prototype.createSqlProcessView = function(process) {
		var that = this;
		process.sqlType = process.sqlType || 'CUSTOM_SQL';
		var sqlType = process.sqlType;
		var $box = $('<li />');

		var $ul = $('<ul />');
		$box.append($ul);
		var $li = $('<li />');
		$ul.append($li);

		var $input = $('<select class="input" name="sqlType" required="1" ></select>');
		$li.append($input);
		$(this.ENUM_MAP.SQL_TYPE).each(function(index, one) {
			$input.append('<option value="' + one.value + '">' + one.text + '</option>');
		});
		$input.val(sqlType);

		that.bindLiEvent($li, process, true);

		if (sqlType.indexOf("SAVE") >= 0) {
			process.select = process.select || {};
			process.insert = process.insert || {};
			process.update = process.update || {};
		}
		if (sqlType.indexOf("CUSTOM") >= 0) {
			process.customSql = process.customSql || {};
			$li = that.createSqlCustomSqlView(process.customSql);
			$ul.append($li);
		}
		if (sqlType.indexOf("SELECT") >= 0 || sqlType.indexOf("SAVE") >= 0) {
			process.select = process.select || {};
			$li = that.createSqlSelectView(process.select);
			$ul.append($li);
		}
		if (sqlType.indexOf("INSERT") >= 0 || sqlType.indexOf("SAVE") >= 0) {
			process.insert = process.insert || {};
			$li = that.createSqlInsertView(process.insert);
			$ul.append($li);
		}
		if (sqlType.indexOf("UPDATE") >= 0 || sqlType.indexOf("SAVE") >= 0) {
			process.update = process.update || {};
			$li = that.createSqlUpdateView(process.update);
			$ul.append($li);
		}
		if (sqlType.indexOf("DELETE") >= 0) {
			process['delete'] = process['delete'] || {};
			$li = that.createSqlDeleteView(process['delete']);
			$ul.append($li);
		}

		return $box;

	};




	DaoEditor.prototype.appendAppends = function($ul, appends, table) {
		var that = this;
		var $li = $('<li />');
		$ul.append($li);

		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加追加SQL</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var append = {};
			appends.push(append);
			append.ifrule = '';
			append.sql = '';
			that.changeModel();
		});

		$(appends).each(function(index, append) {

			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);

			var $subUl = $('<ul class=""/>');
			$li.append($subUl);

			var $li = $('<li class=""/>');
			$subUl.append($li);


			if (!coos.isEmpty(append.ifrule)) {
				$li.append('<span class="pdlr-10">if (</span>');
				var $input = $('<input class="input input-mini" name="ifrule" />');
				$input.val(append.ifrule);
				$li.append($input);
				$li.append('<span class="pdlr-10">) //不填或值为true、1则为真</span>');

				var $li = $('<li class="pdl-30"/>');
				$subUl.append($li);

			}

			var $input = $('<input class="input" name="sql" />');
			$input.val(append.sql);
			$li.append($input);


			if (coos.isEmpty(append.ifrule)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="ifrule" property-value="1=1"  >设置条件</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="ifrule" property-value="">清空条件</a>');
			}


			var $btn = $('<a class="mgl-10 coos-pointer color-orange">修改</a>');
			$li.append($btn);
			$btn.click(function() {});
			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				appends.splice(appends.indexOf(append), 1);
				that.changeModel();
			});

			that.bindPropertyEvent($li, append);
			that.bindLiEvent($subUl, append, false);
		});
	};
})();