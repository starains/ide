(function() {

	Editor.prototype.appendCustomProcessors = function($ul, processors, isAfter) {
		processors = processors || [];
		var that = this;
		var $li = $('<li />');
		$ul.append($li);

		if (isAfter) {
			$li.append('<span class="pdr-10 color-orange">后置执行自定义</span>');
		} else {
			$li.append('<span class="pdr-10 color-orange">前置执行自定义</span>');
		}
		$li.append('<span class="pdr-10 color-grey">参数：variableCache（Service变量）,invokeCache（Service结果缓存）,data（Dao变量）,result（Dao结果）,exception（异常）</span>');
		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var processor = {};
			processors.push(processor);
			processor.type = 'CLASS';
			processor.param = 'data';
			that.changeModel();
		});

		$(processors).each(function(index, processor) {

			var $li = $('<li class="pdl-10"/>');
			$ul.append($li);

			var $subUl = $('<ul class=""/>');
			$li.append($subUl);

			var $li = $('<li class=""/>');
			$subUl.append($li);


			if (!coos.isEmpty(processor.ifrule)) {
				$li.append('<span class="pdlr-10">if (</span>');
				var $input = $('<input class="input input-mini" name="ifrule" />');
				$input.val(processor.ifrule);
				$li.append($input);
				$li.append('<span class="pdlr-10">) //不填或值为true、1则为真</span>');

				var $li = $('<li class="pdl-30"/>');
				$subUl.append($li);

			}

			var $input = $('<select class="input mgr-10" name="type" ></select>');
			$li.append($input);
			$input.append('<option value="CLASS">Class</option>');
			$input.append('<option value="CODE">CLASS</option>');
			processor.type = processor.type || 'AND';
			$input.val(processor.type);



			if (processor.type == 'CODE') {
				var $input = $('<textarea class="input" name="processcode" />');
				$input.val(processor.processcode);
				$li.append($input);

			} else {

				$li.append('<span class="pdr-10">Class</span>');
				var $input = $('<input class="input " name="processor" />');
				$input.val(processor.processor);
				$li.append($input);

				$li.append('<span class="pdr-10">Method</span>');
				var $input = $('<input class="input " name="method" />');
				$input.val(processor.method);
				$li.append($input);

				$li.append('<span class="pdr-10">Param</span>');
				var $input = $('<input class="input " name="param" />');
				$input.val(processor.param);
				$li.append($input);

				$li.append('<span class="pdr-10"></span>');
			}



			if (coos.isEmpty(processor.ifrule)) {
				$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="ifrule" property-value="1=1"  >设置条件</a>');
			} else {
				$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="ifrule" property-value="">清空条件</a>');
			}

			if (isAfter) {
				if (coos.isTrue(processor.ignoreexception)) {
					$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="ignoreexception" >始终执行</a>');
				} else {
					$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="ignoreexception" >异常终止</a>');
				}
			}

			var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
			$li.append($btn);
			$btn.click(function() {
				that.recordHistory();
				processors.splice(processors.indexOf(processor), 1);
				that.changeModel();
			});

			that.bindPropertyEvent($li, processor);
			that.bindLiEvent($subUl, processor, false);
		});
	};

})();