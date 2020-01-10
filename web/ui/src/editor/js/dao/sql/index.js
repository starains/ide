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

})();