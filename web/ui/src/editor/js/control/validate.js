(function() {
	var ControlEditor = coos.Editor.Control;
	ControlEditor.prototype.createMethodValidateView = function(validate, validates) {
		var that = this;
		var $box = $('<li />');

		var $ul = $('<ul class="sub2"/>');
		$box.append($ul);

		var $li = $('<li />');
		$ul.append($li);

		let $baseUl = $ul;

		//		$li.append('<span class="pdr-10">判断表达式（Jexl）</span>');
		//		var $input = $('<input class="input" name="rule" />');
		//		$input.val(validate.rule);
		//		$li.append($input);

		if (!coos.isEmpty(validate.rule)) {
			$li.append('<span class="pdr-10">if (</span>');
			var $input = $('<input class="input " name="rule" />');
			$input.val(validate.rule);
			$li.append($input);
			$li.append('<span class="pdlr-10">) {//值为true、1则为真</span>');

			that.bindLiEvent($li, validate, false);

			$li = $('<li class="pdl-30"/>');
			$ul.append($li);
			$ul = $('<ul />');
			$li.append($ul);
			$li = $('<li />');
			$ul.append($li);
		} else {
			$li.append('<span class="pdr-10">value = AppFactory.getValueByJexlScript("</span>');
			var $input = $('<input class="input" name="value" />');
			$input.val(validate.value);
			$li.append($input);
			that.bindLiEvent($li, validate, false);

			$li.append('<span class="pdr-10">", variableCache);</span>');
			$li = $('<li />');
			$ul.append($li);

			$li.append('<span class="pdr-10">if (</span>');
			if (coos.isTrue(validate.required)) {
				$li.append('<span class="pdlr-10">(value == null || StringUtil.isEmptyIfStr(value))</span>');
			} else {

			}

			if (!coos.isEmpty(validate.pattern)) {
				if (coos.isTrue(validate.required)) {
					$li.append('<span class="pdlr-10"> || </span>');
				} else {
					$li.append('<span class="pdlr-10">value != null && </span>');
				}
				$li.append('<span class="pdr-10">!Pattern.matches("</span>');
				var $input = $('<input class="input" name="pattern" />');
				$input.val(validate.pattern);
				$li.append($input);
				$li.append('<span class="pdlr-10">", String.valueOf(value))</span>');
			}

			$li.append('<span class="pdlr-10">) {(</span>');

			that.bindLiEvent($li, validate, false);

			$li = $('<li class="pdl-30"/>');
			$ul.append($li);
			$ul = $('<ul />');
			$li.append($ul);
			$li = $('<li />');
			$ul.append($li);
		}

		$li.append('<span class="pdr-10">throw new FieldValidateException("</span>');
		var $input = $('<input class="input" name="errcode" />');
		$input.val(validate.errcode);
		$li.append($input);

		$li.append('<span class="pdr-10">" 或  "-1", "</span>');
		var $input = $('<input class="input" name="errmsg" />');
		$input.val(validate.errmsg);
		$li.append($input);

		that.bindLiEvent($li, validate, false);

		$li.append('<span class="pdr-10">");</span>');

		if (coos.isEmpty(validate.rule)) {
			$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="rule" property-value="1=1"  >设值表达式</a>');

			if (coos.isTrue(validate.required)) {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="required" property-value="false"  >设为非必填</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="required" property-value="true">设为必填</a>');
			}
			if (coos.isEmpty(validate.pattern)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="pattern" property-value="^*$"  >设置正则</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="pattern" property-value="">去掉正则</a>');
			}
		} else {
			$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="rule" property-value=""  >去掉表达式</a>');

		}

		that.bindPropertyEvent($li, validate);

		var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			validates.splice(validates.indexOf(validate), 1);
			that.changeModel();
		});

		$li = $('<li />');
		$baseUl.append($li);
		$li.append('<span class="pdr-10">}</span>');
		return $box;

	};
})();