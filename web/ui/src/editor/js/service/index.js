(function() {
	var ServiceEditor = coos.createClass(coos.Editor);
	coos.Editor.Service = ServiceEditor;

	ServiceEditor.prototype.isYaml = function() {
		return true;
	};

	ServiceEditor.prototype.createProcessChoose = function() {

		var $box = $('<div class="editor-process-choose"></div>');


	};

	ServiceEditor.prototype.buildServiceView = function($box, model) {
		var that = this;
		$box.find('.service-view').remove();
		var $case = $('<div class="service-view editor-case"></div>');
		var $a = $('<a class="coos-link color-green mgr-5">收起</a>');
		$a.click(function() {
			if (!that.closeServiceView) {
				that.closeServiceView = true;
				$case.addClass('close');
				$a.text('展开');
			} else {
				that.closeServiceView = false;
				$case.removeClass('close');
				$a.text('收起');
			}
		});
		$case.append($a);
		if (that.closeServiceView) {
			$case.addClass('close');
			$a.text('展开');
		}
		$box.append($case);

		var $ul = $('<ul />')
		$case.append($ul);

		var $li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">名称</span>');
		var $input = $('<input class="input" name="name" />');
		$input.val(model.name);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
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
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">请求方法</span>');
		var $input = $('<select class="input mgr-10" name="requestmethod" ></select>');
		$li.append($input);
		$input.append('<option value="">全部</option>');
		$(that.ENUM_MAP.HTTP_METHOD).each(function(index, one) {
			$input.append('<option value="' + one.value + '">' + one.text + '</option>');
		});
		$input.val(model.requestmethod);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">请求ContentType</span>');
		var $input = $('<input class="input" name="requestcontenttype" />');
		$input.val(model.requestcontenttype);
		$li.append($input);
		that.bindLiEvent($li, model, false);


		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">响应ContentType</span>');
		var $input = $('<input class="input" name="responsecontenttype" />');
		$input.val(model.responsecontenttype);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">执行前Class</span>');
		var $input = $('<input class="input" name="beforeprocessor" />');
		$input.val(model.beforeprocessor);
		$li.append($input);
		that.bindLiEvent($li, model, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">验证Class</span>');
		var $input = $('<input class="input" name="validator" />');
		$input.val(model.validator);
		$li.append($input);
		that.bindLiEvent($li, model, false);


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

	ServiceEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-service"></div>');
		$design.append($box);

		var model = this.model;
		model.processs = model.processs || [];
		var hasStart = false;
		var hasEnd = false;
		$(model.processs).each(function(index, process) {
			if (process.type == 'START') {
				hasStart = true;
			} else if (process.type == 'END') {
				hasEnd = true;
			}
		});
		if (!hasStart) {
			model.processs.push({
				type : 'START',
				name : 'start',
				text : '开始',
				left : 30,
				top : 300
			});
		}
		if (!hasEnd) {
			model.processs.push({
				type : 'END',
				name : 'end',
				text : '结束',
				left : 300,
				top : 300
			});
		}
		that.buildServiceView($box, model);
		that.buildProcesssView($box, model.processs);
		$(document).on("click", "html", function() {
			that.destroyContextmenu();
		});
		$box.on("contextmenu", function(e) {
			e = e || window.e;
			var eventData = {
				clientX : e.clientX,
				clientY : e.clientY
			};
			var $processBox = $(e.target).closest('.process-box');
			if ($processBox.length == 0) {
				return;
			}
			if ($(e.target).closest('.process-node-toolbar').length > 0) {
				return;
			}
			var $node = $(e.target).closest('.process-node');
			var menus = [];
			if ($node.length > 0) {
				var process = $node.data('process');
				menus.push({
					text : "验证",
					onClick : function() {
						that.toViewValidate(process);
					}
				});
				menus.push({
					text : "变量",
					onClick : function() {
						that.toViewVariable(process);
					}
				});
				menus.push({
					text : "结果",
					onClick : function() {
						that.toViewResult(process);
					}
				});
				if (process.type == 'START' || process.type == 'END') {

				} else {
					menus.push({
						text : "修改",
						onClick : function() {
							that.toUpdateProcess(process);
						}
					});
					menus.push({
						text : "删除",
						onClick : function() {
							that.toDeleteProcess(process);
						}
					});
				}
			} else {
				menus.push({
					text : "添加",
					onClick : function() {

						var $service = that.$el.find('.editor-service');
						that.toInsertProcess({
							top : eventData.clientY - $service.offset().top,
							left : eventData.clientX - $service.offset().left
						});
					}
				});

			}

			source.repository.contextmenu.menus = menus;
			source.repository.contextmenu.show = true;
			source.repository.contextmenu.left = e.pageX + "px";
			source.repository.contextmenu.top = e.pageY + "px";
			e.preventDefault();
		});
	};

	ServiceEditor.prototype.onView = function() {
		this.render();
	};

	ServiceEditor.prototype.destroyContextmenu = function() {
		var that = this;
		if (this.$contextmenu) {
			this.$contextmenu.fadeOut(100, function() {
				that.$contextmenu
					.css({
						display : ""
					})
					.find(".drop-left")
					.removeClass("drop-left");
			});
		}
	};

	ServiceEditor.prototype.hasSetting = function() {
		return true;
	};

	ServiceEditor.prototype.toSetting = function() {

		var model = this.model;
		var that = this;

		let data = {};
		Object.assign(data, model);
		app.formDialog({
			title : '配置服务',
			width : "800px",
			items : [ {
				label : "映射路径",
				name : "requestmapping"
			}, {
				label : "前置处理器",
				name : "beforeprocessor"
			}, {
				label : "验证器",
				name : "validator"
			}, {
				label : "后置处理器",
				name : "afterprocessor"
			}, {
				label : "结果报文配置",
				name : "setting",
				"class-name" : "setJexlScriptBtn"
			} ],
			data : data
		}).then(() => {
			that.recordHistory();
			$.extend(true, model, data);
			that.changeModel();
		}).catch(() => {
		});

	};



})();