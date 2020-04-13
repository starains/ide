(function() {
	var DaoEditor = Editor.Dao;

	DaoEditor.prototype.createSqlSelectView = function(model) {
		var that = this;
		var $box = $('<li />');
		model = model || {};
		model.columns = model.columns || [];
		var columns = model.columns;
		model.froms = model.froms || [];
		var froms = model.froms;
		model.leftjoins = model.leftjoins || [];
		var leftjoins = model.leftjoins;
		model.wheres = model.wheres || [];
		var wheres = model.wheres;
		model.groups = model.groups || [];
		var groups = model.groups;
		model.havings = model.havings || [];
		var havings = model.havings;
		model.orders = model.orders || [];
		var orders = model.orders;
		model.unions = model.unions || [];
		var unions = model.unions;
		model.subselects = model.subselects || [];
		var subselects = model.subselects;

		var $ul = $('<ul class="sub1"/>')
		$box.append($ul);


		var $li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10 color-orange">SELECT</span>');

		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加查询字段</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var column = {};
			columns.push(column);
			column.tablealias = "T1";
			column.name = "";
			column.alias = "";
			that.changeModel();
		});

		var $li = $('<li />');
		$ul.append($li);
		var $columnUl = $('<ul />')
		$li.append($columnUl);
		$(columns).each(function(index, column) {
			var $li = $('<li class="pdl-10"/>');
			$columnUl.append($li);



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

				$li = $('<li class="pdl-30"/>');
				$subUl.append($li);
			}

			if (coos.isTrue(column.custom)) {
				var $input = $('<input class="input" name="customsql" />');
				$input.val(column.customsql);
				$li.append($input);

			} else {

				var $input = $('<input class="input input-mini" name="tablealias" />');
				$input.val(column.tablealias);
				$li.append($input);
				$li.append('<span class="pdlr-10">.</span>');
				var $input = $('<input class="input" name="name" />');

				app.autocomplete({
					$input : $input,
					datas : that.getModelColumnOptions()
				})
				$input.val(column.name);
				$li.append($input);
			}
			$li.append('<span class="pdlr-10">AS</span>');
			var $input = $('<input class="input" name="alias" />');

			app.autocomplete({
				$input : $input,
				datas : that.getModelColumnOptions()
			})
			$input.val(column.alias);
			$li.append($input);

			$li.append('<span class="pdlr-10">格式化值</span>');
			var $input = $('<input class="input" name="formatvalue" />');
			$input.val(column.formatvalue);
			$li.append($input);

			if (!coos.isTrue(column.custom)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="custom"  >设为自定义</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="custom" >设为非自定义</a>');
			}
			if (coos.isEmpty(column.ifrule)) {
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
				columns.splice(columns.indexOf(column), 1);
				that.changeModel();
			});

			that.bindPropertyEvent($subUl, column);
			that.bindLiEvent($subUl, column, false);
		});

		var $li = $('<li />');
		$ul.append($li);

		var $subSelectUl = $('<ul class="sub2" />')
		$li.append($subSelectUl);

		var $li = $('<li />');
		$subSelectUl.append($li);

		if (subselects.length > 0) {
			$li.append('<span class="pdlr-10 color-orange">子查询模型</span>');
		}
		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加子查询</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var subselect = {};
			subselects.push(subselect);
			subselect.name = 'name_' + subselects.length;
			subselect.select = {};
			that.changeModel();
		});
		var $btn = that.createSelectTableBtn(function(table) {
			that.recordHistory();
			var subselect = {};
			subselects.push(subselect);
			subselect.name = table.name.toLocaleString();
			subselect.select = app.design.service.createSelectByTable(table);
			that.changeModel();

		});
		$li.append($btn);

		$(subselects).each(function(index, subselect) {
			var $li = $('<li class="pdl-10"/>');
			$subSelectUl.append($li);

			if (!coos.isEmpty(subselect.ifrule)) {
				$li.append('<span class="">if (</span>');
				var $input = $('<input class="input input-mini" name="ifrule" />');
				$input.val(subselect.ifrule);
				$li.append($input);
				$li.append('<span class="pdlr-10">) //不填或值true、1则为真</span>');
				that.bindLiEvent($li, subselect);

				$li = $('<li class="pdl-30"/>');
				$subSelectUl.append($li);
			}

			var $input = $('<input class="input" name="name" />');
			$input.val(subselect.name);
			$li.append($input);


			var $btn = $('<a class="mgl-10 coos-pointer color-orange">修改</a>');
			$li.append($btn);
			$btn.click(function() {});
			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				subselects.splice(subselects.indexOf(subselect), 1);
				that.changeModel();
			});
			if (coos.isTrue(subselect.queryone)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="queryone"  >查询单个</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="queryone" >查询列表</a>');
			}
			if (coos.isEmpty(subselect.ifrule)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="ifrule" property-value="1=1"  >设置条件</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="ifrule" property-value="">清空条件</a>');
			}

			var $btn = $('<a class="mgl-10 coos-pointer color-green">展开</a>');
			$li.append($btn);
			that.subselect_opens = that.subselect_opens || [];
			$btn.click(function() {
				var _i = that.subselect_opens.indexOf(subselect.name);
				if (_i >= 0) {
					that.subselect_opens.splice(_i, 1);
				} else {
					that.subselect_opens.push(subselect.name);
				}
				if ($(this).data('isOpen')) {
					$subSelectLi.hide();
					$(this).data('isOpen', false);
					$btn.text('展开');
				} else {
					$subSelectLi.show();
					$(this).data('isOpen', true);
					$btn.text('收起');
				}
			});
			that.bindPropertyEvent($li, subselect);
			that.bindLiEvent($li, subselect);
			var $subSelectLi = that.createSqlSelectView(subselect.select);
			$subSelectLi.hide();
			$subSelectLi.addClass('pdl-30 pdb-10');
			$subSelectUl.append($subSelectLi);
			if (that.subselect_opens.indexOf(subselect.name) >= 0) {
				$subSelectLi.show();
			}

		});

		var $li = $('<li />');
		$ul.append($li);

		if (froms.length > 0) {
			$li.append('<span class="pdr-10 color-orange">FROM </span>');
		}
		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加FROM</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var from = {};
			froms.push(from);
			from.table = "";
			from.alias = 'T' + (froms.length + leftjoins.length);
			that.changeModel();
		});

		$(froms).each(function(index, from) {
			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);

			var $input = $('<input class="input" name="table" />');
			app.autocomplete({
				$input : $input,
				datas : that.getOptions('TABLE')
			})
			$input.val(from.table);
			$li.append($input);
			$li.append('<span class="pdlr-10">AS</span>');
			var $input = $('<input class="input input-mini" name="alias" />');
			$input.val(from.alias);
			$li.append($input);

			var $btn = $('<a class="mgl-10 coos-pointer color-orange">修改</a>');
			$li.append($btn);
			$btn.click(function() {});
			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				froms.splice(froms.indexOf(from), 1);
				that.changeModel();
			});
			that.bindLiEvent($li, from);
		});
		var $li = $('<li />');
		$ul.append($li);

		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加LEFT JOIN</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var leftjoin = {};
			leftjoins.push(leftjoin);
			leftjoin.table = "TABLE";
			leftjoin.alias = 'T' + (froms.length + leftjoins.length);
			leftjoin.on = 'T1.id = ' + leftjoin.alias + '.id';
			that.changeModel();
		});
		$(leftjoins).each(function(index, leftjoin) {
			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);
			$li.append('<span class="pdr-10 color-orange">LEFT JOIN</span>');

			var $input = $('<input class="input" name="table" />');
			app.autocomplete({
				$input : $input,
				datas : that.getOptions('TABLE')
			})
			$input.val(leftjoin.table);
			$li.append($input);
			$li.append('<span class="pdlr-10">AS</span>');
			var $input = $('<input class="input input-mini" name="alias" />');
			$input.val(leftjoin.alias);
			$li.append($input);
			$li.append('<span class="pdlr-10">ON</span>');
			var $input = $('<input class="input" name="on" />');
			$input.val(leftjoin.on);
			$li.append($input);

			var $btn = $('<a class="mgl-10 coos-pointer color-orange">修改</a>');
			$li.append($btn);
			$btn.click(function() {});
			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				leftjoins.splice(leftjoins.indexOf(leftjoin), 1);
				that.changeModel();
			});
			that.bindLiEvent($li, leftjoin);
		});

		var table = null;
		if (froms.length > 0) {
			table = that.getTableByName(froms[0].table);
		}
		that.appendWhereLi($ul, wheres, table);

		that.appendGroupBy($ul, groups, froms);


		if (havings.length > 0) {
			var $li = $('<li />');
			$ul.append($li);

			$li.append('HAVING ');

		}
		that.appendOrderBy($ul, orders, froms);
		if (unions.length > 0) {
			var $li = $('<li />');
			$ul.append($li);

			$li.append('UNION ');

		}



		model.appends = model.appends || [];
		that.appendAppends($ul, model.appends, table);

		return $box;
	};
	DaoEditor.prototype.appendGroupBy = function($ul, groups, froms) {
		var that = this;
		var $li = $('<li />');
		$ul.append($li);
		if (groups.length > 0) {
			$li.append('<span class="pdr-10 color-orange">GROUP BY</span>');
		}
		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加分组</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var group = {};
			groups.push(group);
			group.tablealias = 'T1';
			group.name = '';
			that.changeModel();
		});
		$(groups).each(function(index, group) {
			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);

			if (!coos.isEmpty(group.ifrule)) {
				$li.append('<span class="pdlr-10">if (</span>');
				var $input = $('<input class="input input-mini" name="ifrule" />');
				$input.val(group.ifrule);
				$li.append($input);
				$li.append('<span class="pdlr-10">) //不填或值为true、1则为真</span>');

				that.bindLiEvent($li, group);
				var $li = $('<li class="pdl-30"/>');
				$ul.append($li);
			}


			if (coos.isTrue(group.custom)) {
				var $input = $('<input class="input" name="customsql" />');
				$input.val(group.customsql);
				$li.append($input);
			} else {
				var $input = $('<input class="input input-mini" name="tablealias" />');
				$input.val(group.tablealias);
				$li.append($input);
				$li.append('<span class="pdlr-10">.</span>');
				var $input = $('<input class="input" name="name" />');
				app.autocomplete({
					$input : $input,
					datas : that.getModelColumnOptions()
				})
				$input.val(group.name);
				$li.append($input);

			}


			if (!coos.isTrue(group.custom)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="custom"  >设为自定义</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="custom" >设为非自定义</a>');
			}
			if (coos.isEmpty(group.ifrule)) {
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
				groups.splice(groups.indexOf(group), 1);
				that.changeModel();
			});
			that.bindPropertyEvent($li, group);
			that.bindLiEvent($li, group);
		});
	};

	DaoEditor.prototype.appendOrderBy = function($ul, orders, froms) {
		var that = this;
		var $li = $('<li />');
		$ul.append($li);
		if (orders.length > 0) {
			$li.append('<span class="pdr-10 color-orange">ORDER BY</span>');
		}
		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加排序</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var order = {};
			orders.push(order);
			order.tablealias = 'T1';
			order.name = 'name';
			order.order = 'DESC';
			that.changeModel();
		});
		$(orders).each(function(index, order) {
			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);

			if (!coos.isEmpty(order.ifrule)) {
				$li.append('<span class="pdlr-10">if (</span>');
				var $input = $('<input class="input input-mini" name="ifrule" />');
				$input.val(order.ifrule);
				$li.append($input);
				$li.append('<span class="pdlr-10">) //不填或值为true、1则为真</span>');

				that.bindLiEvent($li, order);
				var $li = $('<li class="pdl-30"/>');
				$ul.append($li);
			}

			if (coos.isTrue(order.custom)) {
				var $input = $('<input class="input" name="customsql" />');
				$input.val(order.customsql);
				$li.append($input);
			} else {
				var $input = $('<input class="input input-mini" name="tablealias" />');
				$input.val(order.tablealias);
				$li.append($input);
				$li.append('<span class="pdlr-10">.</span>');
				var $input = $('<input class="input" name="name" />');
				app.autocomplete({
					$input : $input,
					datas : that.getModelColumnOptions()
				})
				$input.val(order.name);
				$li.append($input);
				$li.append('<span class="pdlr-10"></span>');
				var $input = $('<input class="input" name="order" />');
				$input.val(order.order);
				$li.append($input);
			}

			if (!coos.isTrue(order.custom)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="custom"  >设为自定义</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="custom" >设为非自定义</a>');
			}
			if (coos.isEmpty(order.ifrule)) {
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
				orders.splice(orders.indexOf(order), 1);
				that.changeModel();
			});
			that.bindPropertyEvent($li, order);
			that.bindLiEvent($li, order);
		});
	};

})();