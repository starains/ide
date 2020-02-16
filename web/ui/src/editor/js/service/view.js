(function() {
	var ServiceEditor = coos.Editor.Service;

	ServiceEditor.prototype.buildProcesssView = function($box, processs) {
		var that = this;
		this.id_prefix = this.id_prefix || ('' + coos.getNumber() + '-');
		$box.find('.process-box').remove();
		var $processBox = $('<div class="process-box"/>');
		$box.append($processBox);
		jsPlumb.ready(function() {
			var design_ = jsPlumb.getInstance();
			$(processs).each(function(index, process) {
				var $node = $('<div class="process-node"/>');
				var $toolbar = $('<div class="process-node-toolbar"/>');
				var $ul = $('<div  />');
				var $btn = $('<a class="coos-btn validate-btn coos-btn-xs color-green" title="验证">验证</a>');
				$btn.click(function(e) {
					that.toViewValidate(process);
				});
				$ul.append($btn)
				var $btn = $('<a class="coos-btn variable-btn coos-btn-xs color-green" title="变量">变量</a>');
				$btn.click(function(e) {
					that.toViewVariable(process);
				});
				$ul.append($btn)
				var $btn = $('<a class="coos-btn result-btn coos-btn-xs color-orange" title="结果">结果</a>');
				$btn.click(function(e) {
					that.toViewResult(process);
				});
				$ul.append($btn);
				if (process.type == 'START' || process.type == 'END') {

				} else {
					var $btn = $('<a class="coos-btn coos-btn-xs color-green" title="设置">设置</a>');
					$btn.click(function(e) {
						that.toUpdateProcess(process);
					});
					$ul.append($btn)
					var $btn = $('<a class="coos-btn coos-btn-xs color-red" title="删除">删除</a>');
					$btn.click(function(e) {
						that.toDeleteProcess(process);
					});
					$ul.append($btn)
				}
				$node.on('mousedown', function(e) {
					if ($(e.target).closest('.process-node-toolbar').length > 0 ||

						$(e.target).closest('.process-variable-box').length > 0) {
						e.stopPropagation();
						return false;
					}
				});
				$toolbar.append($ul);
				$node.append($toolbar);
				$node.attr('id', that.getIdByProcess(process));
				$node.data('process', process);
				$node.addClass('process-node-' + process.type);
				$node.append('<div class="ring" />');
				var $content = $('<div class="content" />');
				$node.append($content);

				switch (process.type) {
				case "START":
					break;
				case "END":
					break;
				case "DECISION":
					//$content.append('<i class="icon coos-icon coos-icon-branches"></i>');
					break;
				case "CONDITION":
					//$content.append('<i class="icon coos-icon coos-icon-issuesclose"></i>');
					break;
				case "DAO":
					//$content.append('<i class="icon coos-icon coos-icon-database"></i>');
					break;
				case "SUB_SERVICE":
					//$content.append('<i class="icon coos-icon coos-icon-sever"></i>');
					break;
				}
				if (process.image) {
					var $img = $('<img class="image"/>');
					$img.attr('src', coos.url.format(process.image));
					$content.append($img);
				}

				if (process.text) {
					var $text = $('<div class="text" />');
					$text.text(process.text);
					$content.append($text);
				} else if (process.name) {
					var $text = $('<div class="text" />');
					$text.text(process.name);
					$content.append($text);
				}

				$node.css('left', (process.left || 0) + 'px');
				$node.css('top', (process.top || 0) + 'px');
				$processBox.append($node);

				process.tos = process.tos || [];

			});
			design_.importDefaults(defaultStyle);
			$(processs).each(function(index, process) {
				if (process.tos) {
					var tos = [];
					$(process.tos).each(function(index, to) {
						var toProcess = null;
						$(processs).each(function(index, p) {
							if (p.name == to) {
								toProcess = p;
							}
						});
						if (toProcess) {
							tos.push(to);
							design_.connect({
								source : that.getIdByProcess(process),
								target : that.getIdByProcess(toProcess)
							});
						}
					});
					process.tos = tos;
				}
			});
			$processBox.find(".process-node .ring").each(function(i, e) {
				var p = $(e).parent();
				design_.makeSource($(e));
			});

			design_.makeTarget($processBox.find(".process-node .ring"), {
				endpoint : defaultStyle.Endpoints[0],
				connectionOverlays : defaultStyle.ConnectionOverlays,
				anchor : defaultStyle.Anchor,
				connector : defaultStyle.Connector,
				paintStyle : defaultStyle.PaintStyle,
				dragOptions : defaultStyle.DragOptions,
				beforeDrop : function(params) {
					if (params.sourceId == params.targetId) {
						coos.warn('不能连线自己！');
						return false; /* 不能链接自己 */
					}
					var sourceProcess = $processBox.find('#' + params.sourceId).closest('.process-node').data('process');
					var targetProcess = $processBox.find('#' + params.targetId).closest('.process-node').data('process');
					if (sourceProcess.type == 'END') {
						coos.warn('无法从结束节点开始连线！');
						return false;
					}
					if (sourceProcess.type == 'ERROR_END') {
						coos.warn('异常结束节点不能连接其他节点！');
						return false;
					}
					if (targetProcess.type == 'START') {
						coos.warn('无法连线到开始节点！');
						return false;
					}


					sourceProcess.tos = sourceProcess.tos || [];
					targetProcess.tos = targetProcess.tos || [];
					if (sourceProcess.tos.indexOf(targetProcess.name) >= 0) {
						coos.warn('节点已有连线，无法重复连线！');
						return false;
					} else if (targetProcess.tos.indexOf(sourceProcess.name) >= 0) {
						coos.warn('节点已有连线，无法重复连线！');
						return false;
					}
					if (sourceProcess.type != 'DECISION') {
						if (sourceProcess.tos.length > 0) {
							coos.warn('该节点无法建立多条连线！');
							return false;
						}
					} else {
						if (targetProcess.type != 'CONDITION') {
							coos.warn('决策节点只能与条件节点建立连线！');
							return false;
						}
					}
					that.recordHistory();
					sourceProcess.tos.push(targetProcess.name);
					that.changeModel();
					return false;
				}
			});
			design_.draggable($processBox.find(".process-node "), {
				rightButtonCanDrag : false,
				stop : function(arg1, arg2) {
					that.recordHistory();
					var process = $(arg1.el).data('process');
					process.top = $(arg1.el).position().top;
					process.left = $(arg1.el).position().left;
					that.changeModel();
				}
			});
			// 监听所有的连接事件
			design_.bind("connection", function(info, event) {
				// console.log(info);
			});
			// 监听所有的删除连接事件
			design_.bind("connectionDetached", function(params, event) {});

			function connectionDetached(params) {
				// console.log(info);
				var sourceProcess = $processBox.find('#' + params.sourceId).closest('.process-node').data('process');
				var targetProcess = $processBox.find('#' + params.targetId).closest('.process-node').data('process');
				if (!sourceProcess || !targetProcess) {
					return false;
				}
				that.recordHistory();

				$(sourceProcess.tos).each(function(index, to) {
					if (to == targetProcess.name) {
						sourceProcess.tos.splice(index, 1);
					}
				});

				that.changeModel();

				return false;
			}
			// 点击连接时的触发事件
			design_.bind("click", function(component) {
				coos.confirm('是否删除连线？', function() {
					connectionDetached(component);
				});

				return false;
			});

			that.viewValidates = that.viewValidates || [];
			$(that.viewValidates).each(function(index, name) {
				var process = null;
				$(processs).each(function(index, p) {
					if (p.name == name) {
						process = p;
					}
				});
				if (process == null) {
					var index = that.viewValidates.indexOf(name);
					that.viewValidates.splice(index, 1);
					return;
				}
				that.toViewValidate(process);
			});

			that.viewVariables = that.viewVariables || [];
			$(that.viewVariables).each(function(index, name) {
				var process = null;
				$(processs).each(function(index, p) {
					if (p.name == name) {
						process = p;
					}
				});
				if (process == null) {
					var index = that.viewVariables.indexOf(name);
					that.viewVariables.splice(index, 1);
					return;
				}
				that.toViewVariable(process);
			});

			that.viewResults = that.viewResults || [];
			$(that.viewResults).each(function(index, name) {
				var process = null;
				$(processs).each(function(index, p) {
					if (p.name == name) {
						process = p;
					}
				});
				if (process == null) {
					var index = that.viewResults.indexOf(name);
					that.viewResults.splice(index, 1);
					return;
				}
				that.toViewResult(process);
			});
		});

	};
	var defaultStyle = {
		DragOptions : {
			cursor : 'pointer',
		},
		// 端点样式
		Endpoint : [ "Dot", {
			radius : 1
		} ],
		// 端点样式
		Endpoints : [ [ "Dot", {
			radius : 1,
			cssClass : "display-none"
		} ], [ "Dot", {
			radius : 1,
			cssClass : "display-none"
		} ] ],
		// 箭头
		ConnectionOverlays : [ [ "Arrow", {
			width : 10,
			length : 10,
			location : 1,
			foldback : 0.8,
			visible : true
		} ], [ "Label", {
			location : 0.1,
			id : "label",
			cssClass : "aLabel"
		} ] ],
		Anchor : 'Continuous',
		// 连接器的类型
		Connector : [ 'Flowchart', {} ],
		// 连线样式
		PaintStyle : {
			strokeWidth : 2,
			stroke : "#456"
		}
	};
})();