(function() {
	var DaoEditor = coos.Editor.Dao;

	DaoEditor.prototype.appendWhereLi = function($ul, wheres, table, isPiece) {
		var that = this;
		var $li = $('<li />');
		$ul.append($li);

		if (wheres.length > 0) {
			if (!isPiece) {
				$li.append('<span class="pdr-10 color-orange">WHERE 1=1</span>');
			} else {
				$li.append('<span class="pdr-10 color-orange">1=1</span>');
			}
		}
		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加条件</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var where = {};
			wheres.push(where);
			where.tablealias = 'T1';
			where.name = '';
			where.comparisonoperator = '=';
			where.relationaloperation = 'AND';
			where.nullable = true;
			that.changeModel();
		});
		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加条件块</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var where = {};
			wheres.push(where);
			where.piece = true;
			where.relationaloperation = 'AND';
			that.changeModel();
		});

		$(wheres).each(function(index, where) {

			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);

			var $subUl = $('<ul class=""/>');
			$li.append($subUl);

			var $li = $('<li class=""/>');
			$subUl.append($li);


			if (!coos.isEmpty(where.ifrule)) {
				$li.append('<span class="pdlr-10">if (</span>');
				var $input = $('<input class="input input-mini" name="ifrule" />');
				$input.val(where.ifrule);
				$li.append($input);
				$li.append('<span class="pdlr-10">) //不填或值为true、1则为真</span>');

				var $li = $('<li class="pdl-30"/>');
				$subUl.append($li);

			}

			var $input = $('<select class="input mgr-10" name="relationaloperation" ></select>');
			$li.append($input);
			$input.append('<option value="AND">AND</option>');
			$input.append('<option value="OR">OR</option>');
			where.relationaloperation = where.relationaloperation || 'AND';
			$input.val(where.relationaloperation);

			if (coos.isTrue(where.piece)) {
				//				var $ulS = $('<ul/>');
				//				$li.append($ulS);
				//				where.wheres = where.wheres || [];
				//				that.appendWhereLi($ulS, where.wheres, table, true);
			} else {

				if (coos.isTrue(where.custom)) {
					var $input = $('<input class="input" name="customsql" />');
					$input.val(where.customsql);
					$li.append($input);

				} else {
					var $input = $('<input class="input input-mini" name="tablealias" />');
					$input.val(where.tablealias);
					$li.append($input);
					$li.append('<span class="pdlr-10">.</span>');
					var $input = $('<input class="input" name="name" />');
					app.autocomplete({
						$input : $input,
						datas : that.getModelColumnOptions()
					})
					$input.val(where.name);
					$li.append($input);
					var $input = $('<select class="input input-mini mglr-10" name="comparisonoperator" ></select>');
					$li.append($input);
					$(that.ENUM_MAP.COMPARISON_OPERATOR).each(function(index, one) {
						$input.append('<option value="' + one.value + '">' + one.text + '</option>');
					});
					where.comparisonoperator = where.comparisonoperator || '=';
					$input.val(where.comparisonoperator);

					var $input = $('<input class="input input-mini " name="value" />');
					$input.val(where.value);
					$li.append($input);
					$li.append('<span class="pdr-10">或</span>');
					$li.append('<span class="">自动取名称相同的值  或</span>');
					var $input = $('<input class="input input-mini " name="defaultvalue" />');
					$input.val(where.defaultvalue);
					$li.append($input);
					$li.append('<span class="pdr-10"></span>');
				}

				if (!coos.isTrue(where.custom)) {
					$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="custom"  >设为自定义</a>');
				} else {
					$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="custom" >设为非自定义</a>');
				}

			}

			if (coos.isEmpty(where.ifrule)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="ifrule" property-value="1=1"  >设置条件</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="ifrule" property-value="">清空条件</a>');
			}

			if (!coos.isTrue(where.required)) {
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
				wheres.splice(wheres.indexOf(where), 1);
				that.changeModel();
			});

			that.bindPropertyEvent($li, where);
			that.bindLiEvent($subUl, where, false);
			if (coos.isTrue(where.piece)) {
				where.wheres = where.wheres || [];

				var $li = $('<li class="pdl-30"/>');
				$subUl.append($li);
				$li.append('(');

				var $li = $('<li class="pdl-30"/>');
				$subUl.append($li);

				var $ul_ = $('<ul class="pdl-30"/>');
				$li.append($ul_);

				that.appendWhereLi($ul_, where.wheres, table, true);

				var $li = $('<li class="pdl-30"/>');
				$subUl.append($li);
				$li.append(')');
			}
		});
	};

})();