(function() {
	var ControlEditor = Editor.Control;
	ControlEditor.prototype.createMethodProcessView = function(process, processs) {
		var that = this;
		var $box = $('<li />');

		var $ul = $('<ul class="sub2"/>');
		$box.append($ul);

		var $li = $('<li />');
		$ul.append($li);

		if (!coos.isEmpty(process.ifrule)) {

			$li.append('<span class="pdlr-10">if (</span>');
			var $input = $('<input class="input " name="ifrule" />');
			$input.val(process.ifrule);
			$li.append($input);
			$li.append('<span class="pdlr-10">) //不填或值为true、1则为真</span>');

			$li = $('<li class="pdl-30"/>');
			$ul.append($li);
			$ul = $('<ul />');
			$li.append($ul);
			$li = $('<li />');
			$ul.append($li);
		}

		$li.append('<span class="pdr-10">// </span>');
		var $input = $('<input class="input" name="comment" />');
		$input.val(process.comment);
		$li.append($input);
		that.bindLiEvent($li, process, true);

		$li = $('<li />');
		$ul.append($li);

		process.type = process.type || 'SERVICE';
		$li.append('<span class="pdr-10">result = </span>');
		var $input = $('<select class="input mgr-10" name="type" ></select>');
		$li.append($input);
		$input.append('<option value="SERVICE">执行Service</option>');
		$input.append('<option value="DAO">执行Dao</option>');
		$input.val(process.type);
		$li.append($input);

		if (process.type == 'SERVICE') {
			$li.append('<span class="pdr-10"></span>');
			var $input = $('<input class="input" name="servicename" />');
			app.autocomplete({
				$input : $input,
				datas : that.getOptions('SERVICE')
			})
			$input.val(process.servicename);
			$li.append($input);
		} else if (process.type == 'DAO') {
			$li.append('<span class="pdr-10"></span>');
			var $input = $('<input class="input" name="daoname" />');
			app.autocomplete({
				$input : $input,
				datas : that.getOptions('DAO')
			})
			$input.val(process.daoname);
			$li.append($input);

		}

		$li.append('<span class="pdr-10">.invoke(variableCache);</span>');

		if (coos.isEmpty(process.ifrule)) {
			$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="ifrule" property-value="1=1"  >设置条件</a>');
		} else {
			$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="ifrule" property-value="">清空条件</a>');
		}

		var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			processs.splice(processs.indexOf(process), 1);
			that.changeModel();
		});


		that.bindPropertyEvent($li, process);


		that.bindLiEvent($li, process, true);

		return $box;

	};
})();