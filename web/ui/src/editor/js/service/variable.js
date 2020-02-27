(function() {
	var ServiceEditor = coos.Editor.Service;

	ServiceEditor.prototype.toViewVariable = function(process) {
		this.viewVariables = this.viewVariables || [];
		var $node = this.getNodeByProcess(process);
		var index = this.viewVariables.indexOf(process.name);
		if (this.viewValidates.indexOf(process.name) >= 0) {
			this.toViewValidate(process);
		}
		if (index >= 0) {
			if ($node.find('.process-variable-box').length > 0) {
				this.viewVariables.splice(index, 1);
				$node.find('.process-variable-box').remove();
				$node.find('.process-node-toolbar').removeClass('show');
				$node.find('.process-node-toolbar').find('.variable-btn').removeClass('coos-active');
				return;
			}
		}
		if (index < 0) {
			this.viewVariables.push(process.name);
		}
		var $box = $('<div class="process-variable-box"/>');
		$node.find('.process-node-toolbar').append($box);
		$node.find('.process-node-toolbar').addClass('show');
		$node.find('.process-node-toolbar').find('.variable-btn').addClass('coos-active');
		this.appendVariables($box, process);

	};
	ServiceEditor.prototype.appendVariables = function($box, process) {
		var that = this;
		$box.append('<div class="title color-orange ft-13">变量（名称:值:默认值）</div>');
		var $list = $('<ul class="coos-list ft-12 pd-5  " />');
		$box.append($list);

		process.variables = process.variables || [];

		$(process.variables).each(function(index, variable) {
			var $li = $('<li class=""></li>');
			$list.append($li)

			var $card = $('<div class="coos-card "></div>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);
			if (!coos.isEmpty(variable.name)) {
				$span.text(variable.name);
			}

			$card.append('<span class="pdlr-2 ">:</span>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);

			if (!coos.isEmpty(variable.value)) {
				$span.text(variable.value);
			}

			$card.append('<span class="pdlr-2 ">:</span>');

			var $span = $('<span class="pdlr-5 "></span>');
			$card.append($span);
			if (!coos.isEmpty(variable.defaultvalue)) {
				$span.text(variable.defaultvalue);
			}

			$li.append($card)

			var $btn = $('<a class="coos-link color-green">修改</a>');
			$btn.click(function() {
				that.toUpdateVariable(variable);
			});
			$card.append($btn)

			var $btn = $('<a class="coos-link color-red">删除</a>');
			$btn.click(function() {
				that.toDeleteVariable(process, variable);
			});
			$card.append($btn)
		});
		var $li = $('<li class=""></li>');
		$list.append($li)

		var $btn = $('<a class="coos-link color-green">添加变量</a>');
		$btn.click(function() {
			that.toInsertVariable(process);
		});
		$li.append($btn)

	};

	ServiceEditor.prototype.getVariableFormOptions = function(data) {

		return {
			width : "800px",
			items : [ {
				label : "名称",
				name : "name"
			}, {
				label : "值（Jexl）",
				name : "value",
				"class-name" : ""
			}, {
				label : "默认值（Jexl）",
				name : "defaultvalue",
				"class-name" : ""
			}, {
				label : "取值器",
				name : "valuer"
			} ],
			data : data
		};

	};
	ServiceEditor.prototype.toInsertVariable = function(process) {
		process = process || {};
		process.variables = process.variables || [];
		var variable = {};

		let that = this;
		let data = {};
		Object.assign(data, variable);

		let options = this.getVariableFormOptions(data);
		options.title = '添加变量';
		app.formDialog(options).then(() => {
			data.name.trim();
			that.recordHistory();
			$.extend(true, variable, data);
			process.variables.push(variable);
			that.changeModel();
			editBox.destroy();
		}).catch(() => {
		});

	};

	ServiceEditor.prototype.toUpdateVariable = function(variable) {
		let that = this;
		let data = {};
		Object.assign(data, variable);

		let options = this.getVariableFormOptions(data);
		options.title = '修改变量';
		app.formDialog(options).then(() => {
			data.name.trim();
			that.recordHistory();
			$.extend(true, variable, data);
			that.changeModel();
		}).catch(() => {
		});

	};

	ServiceEditor.prototype.toDeleteVariable = function(process, variable) {
		var that = this;
		coos.confirm('确定删除该变量？', function() {
			that.recordHistory();
			process.variables.splice(process.variables.indexOf(variable), 1);
			that.changeModel();
		});
	};
})();