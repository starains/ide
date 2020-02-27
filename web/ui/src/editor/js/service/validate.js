(function() {
	var ServiceEditor = coos.Editor.Service;

	ServiceEditor.prototype.toViewValidate = function(process) {
		this.viewValidates = this.viewValidates || [];
		var $node = this.getNodeByProcess(process);
		if (this.viewVariables.indexOf(process.name) >= 0) {
			this.toViewVariable(process);
		}
		var index = this.viewValidates.indexOf(process.name);
		if (index >= 0) {
			if ($node.find('.process-validate-box').length > 0) {
				this.viewValidates.splice(index, 1);
				$node.find('.process-validate-box').remove();
				$node.find('.process-node-toolbar').removeClass('show');
				$node.find('.process-node-toolbar').find('.validate-btn').removeClass('coos-active');
				return;
			}
		}
		if (index < 0) {
			this.viewValidates.push(process.name);
		}
		var $box = $('<div class="process-validate-box"/>');
		$node.find('.process-node-toolbar').append($box);
		$node.find('.process-node-toolbar').addClass('show');
		$node.find('.process-node-toolbar').find('.validate-btn').addClass('coos-active');
		this.appendValidates($box, process);

	};
	ServiceEditor.prototype.appendValidates = function($box, process) {
		var that = this;
		$box.append('<div class="title color-orange ft-13">验证（值:必填:类型:表达式:正则）</div>');
		var $list = $('<ul class="coos-list ft-12 pd-5 " />');
		$box.append($list);

		process.validates = process.validates || [];

		$(process.validates).each(function(index, validate) {
			var $li = $('<li ></li>');
			$list.append($li)

			var $card = $('<div class="coos-card "></div>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);

			if (!coos.isEmpty(validate.value)) {
				$span.text(validate.value);
			}

			$card.append('<span class="pdlr-2 ">:</span>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);
			$span.text(coos.isTrue(validate.required) ? '必填' : '非必填');

			$card.append('<span class="pdlr-2 ">:</span>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);

			if (!coos.isEmpty(validate.type)) {
				$span.text(validate.type);
			}

			$card.append('<span class="pdlr-2 ">:</span>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);

			if (!coos.isEmpty(validate.rule)) {
				$span.text(validate.rule);
			}


			$card.append('<span class="pdlr-2 ">:</span>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);

			if (!coos.isEmpty(validate.pattern)) {
				$span.text(validate.pattern);
			}

			$li.append($card)


			var $btn = $('<a class="coos-link color-green">修改</a>');
			$btn.click(function() {
				that.toUpdateValidate(validate);
			});
			$card.append($btn)

			var $btn = $('<a class="coos-link color-red">删除</a>');
			$btn.click(function() {
				that.toDeleteValidate(process, validate);
			});
			$card.append($btn)
		});
		var $li = $('<li ></li>');
		$list.append($li)

		var $btn = $('<a class="coos-link color-green">添加验证</a>');
		$btn.click(function() {
			that.toInsertValidate(process);
		});
		$li.append($btn)

	};

	ServiceEditor.prototype.getValidateFormOptions = function(data) {

		let types = [];
		$(source.ENUM_MAP.VALUE_TYPE).each(function(index, one) {
			types.push({
				text : one.text,
				value : one.value
			})
		});

		return {
			width : "800px",
			items : [ {
				label : "值（Jexl）",
				name : "value",
				"class-name" : ""
			}, {
				label : "必填",
				name : "required",
				type : "switch"
			}, {
				label : "正则",
				name : "pattern"
			}, {
				label : "表达式（Jexl）",
				name : "rule",
				"class-name" : ""
			}, {
				label : "类型",
				name : "type",
				type : "select",
				options : types
			}, {
				label : "验证器",
				name : "validator"
			}, {
				label : "错误码",
				name : "errcode"
			}, {
				label : "错误信息",
				name : "errmsg"
			} ],
			data : data
		};


	};
	ServiceEditor.prototype.toInsertValidate = function(process) {
		process = process || {};
		process.validates = process.validates || [];
		var validate = {};

		let that = this;
		let data = {};
		Object.assign(data, validate);

		let options = this.getValidateFormOptions(data);
		options.title = '添加验证';
		app.formDialog(options).then(() => {
			that.recordHistory();
			$.extend(true, validate, data);
			process.validates.push(validate);
			that.changeModel();
		}).catch(() => {
		});

	};

	ServiceEditor.prototype.toUpdateValidate = function(validate) {
		let that = this;
		let data = {};
		Object.assign(data, validate);

		let options = this.getValidateFormOptions(data);
		options.title = '修改验证';
		app.formDialog(options).then(() => {
			that.recordHistory();
			$.extend(true, validate, data);
			that.changeModel();
			editBox.destroy();
		}).catch(() => {
		});

	};

	ServiceEditor.prototype.toDeleteValidate = function(process, validate) {
		var that = this;
		coos.confirm('确定删除该验证？', function() {
			that.recordHistory();
			process.validates.splice(process.validates.indexOf(validate), 1);
			that.changeModel();
		});
	};
})();