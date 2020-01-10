(function() {
	var DaoEditor = coos.createClass(coos.Editor);
	coos.Editor.Dao = DaoEditor;

	DaoEditor.prototype.isYaml = function() {
		return true;
	};

	DaoEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-dao editor-case"></div>');
		$design.append($box);

		var model = this.model;

		var $ul = $('<ul />')
		$box.append($ul);

		var $li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">名称</span>');
		var $input = $('<input class="input" name="name" />');
		$input.val(model.name);
		$li.append($input);

		$li.append('<span class="pdr-10">说明</span>');
		var $input = $('<input class="input" name="comment" />');
		$input.val(model.comment);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">映射地址</span>');
		var $input = $('<input class="input" name="requestmapping" />');
		$input.val(model.requestmapping);
		$li.append($input);

		$li.append('<span class="pdr-10">ContentType</span>');
		var $input = $('<input class="input" name="contenttype" />');
		$input.val(model.contenttype);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">执行前Class</span>');
		var $input = $('<input class="input" name="beforeprocessor" />');
		$input.val(model.beforeprocessor);
		$li.append($input);

		$li.append('<span class="pdr-10">验证Class</span>');
		var $input = $('<input class="input" name="validator" />');
		$input.val(model.validator);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		model.process = model.process || {};

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">类型</span>');
		var $input = $('<select class="input mgr-10" name="type" ></select>');
		$li.append($input);
		$(that.ENUM_MAP.DAO_PROCESS_TYPE).each(function(index, one) {
			$input.append('<option value="' + one.value + '">' + one.text + '</option>');
		});
		$input.val(model.process.type);
		$li.append($input);
		that.bindLiEvent($li, model.process);

		model.process.type = model.process.type || 'SQL';
		if (model.process.type == 'SQL') {
			$li = $('<li />');
			$ul.append($li);
			var $view = this.createSqlProcessView(model.process);
			$li.append($view);
		} else if (model.process.type == 'HTTP') {
			$li = $('<li />');
			$ul.append($li);
			var $view = this.createHttpProcessView(model.process);
			$li.append($view);
		}

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">执行后Class</span>');
		var $input = $('<input class="input" name="afterprocessor" />');
		$input.val(model.afterprocessor);
		$li.append($input);
		that.bindLiEvent($li, model, false);


		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">格式化结果</span>');
		var $input = $('<input class="input setJexlScriptBtn" name="resultresolve" />');
		$input.val(model.resultresolve);
		$li.append($input);
		that.bindLiEvent($li, model, false);
	};



	DaoEditor.prototype.hasTest = function() {
		return true;
	};


	DaoEditor.prototype.toTest = function() {
		var that = this;
		let data = {
			body : "{\n}",
			result : ""
		};
		app.formDialog({
			title : '测试数据',
			width : "800px",
			"label-width" : " ",
			"before-html" : '<h4 class="color-orange">请填写JSON格式数据作为参数</h4>',
			items : [ {
				label : "数据",
				name : "data",
				type : "textarea"
			}, {
				label : "结果",
				name : "result",
				type : "textarea"
			} ],
			data : data,
			onClose (arg) {
				if (arg) {
					data.result = '';
					that.options.onTest(data, function(res) {
						data.result = (JSON.stringify(res));
					});
					return false;
				}
			}
		}).then(() => {

		}).catch(() => {
		});

	};



	DaoEditor.prototype.createSelectTableBtn = function() {};



})();