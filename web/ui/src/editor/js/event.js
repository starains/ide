(function() {
	var Editor = coos.Editor;

	Editor.prototype.bindPropertyEvent = function($li, model) {
		model = model || {};
		var that = this;
		$li.find('.updatePropertyBtn').click(function() {
			var type = $(this).attr('property-type');
			var value = $(this).attr('property-value');
			if (!coos.isEmpty(type)) {
				that.recordHistory();
				if (typeof (value) == 'undefined') {
					value = model[type];
					if (coos.isTrue(value)) {
						value = false;
					} else {
						value = true;
					}
				}
				model[type] = value;
				that.changeModel();
			}
		});
	};
	Editor.prototype.bindEvent = function($box) {

		var that = this;
		if (!window.editor_event_binded) {
			window.editor_event_binded = true;
			$(window).on('mousedown', function(e) {
				e = e || window.event;
				var editor = $(e.target).closest('.editor-box').data('editor');
				$('.editor-box').each(function(index, $box) {
					$($box).data('editor').mousefocus = false;
				});
				if (editor) {
					editor.mousefocus = true;
				}
			});
			$(window).on('keydown', function(e) {
				e = e || window.event;
				if (e.ctrlKey == true && e.keyCode == 83) { //Ctrl+S
					var editor = null;
					$('.editor-box').each(function(index, $box) {
						if ($($box).data('editor').mousefocus) {
							editor = $($box).data('editor');
						}
					});
					if (editor) {
						e.preventDefault();
						editor.toSave();
					}
				}
			});
		}
	};

	$(function() {
		$(window.document).on('click', '.setJexlScriptBtn', function(e) {
			var $target = $(e.target);
			var $input = null;
			if (e.target.tagName == 'INPUT' || e.target.tagName == 'TEXTAREA') {
				$input = $target;
			} else {
				var $group = $target.closest('.coos-input-group');
				if ($group.length > 0) {
					$input = $group.find('.coos-input');
				}
			}
			if ($input != null && $input.length > 0) {
				setJexlScript($input.val(), function(text) {
					$input.val(text);
					$input.change();
				});
			}
		});
	});

	Editor.help_html = `
	<div class="pdlr-10 mgb-20 ft-13" >
		<h4 class="color-orange">说明</h4>
		<div class="color-grey">1：数据格式以传输的报文为基础</div>
			<div class="color-grey pdl-20">例如：传输{key1:value1,key2:value2}</div>
			<div class="color-grey pdl-20">则映射变量：key1=value1,key2=value2。</div>
		<div class="color-grey">2：内置变量映射：</div>
			<div class="color-grey pdl-20">$data：为传输的数据</div>
			<div class="color-grey pdl-20">$body：为requestBody</div>
			<div class="color-grey pdl-20">$session：为AppSession</div>
			<div class="color-grey pdl-20">$reqeust：为请求参数</div>
			<div class="color-grey pdl-20">$header：为请求Header</div>
			<div class="color-grey pdl-20">$user：为登录用户</div>
			<div class="color-grey pdl-20">$cache：为AppSession缓存数据</div>
			<div class="color-grey pdl-20">$result：为执行节点产生的结果，可以使用$result.节点名称</div>
		<div class="color-grey">3：扩展函数：</div>
			<div class="color-grey pdl-20">coos.now_date()：获取当前时间。示例：coos.now_date("yyyy-MM-dd HH:mm:ss"（非必填）)</div>
			<div class="color-grey pdl-20">coos.generate_id()：生成ID。示例：coos.generate_id(12~32（非必填）)</div>
			<div class="color-grey pdl-20">coos.MD5()：MD5加密。示例：coos.MD5(value（必填）)</div>
			<div class="color-grey pdl-20">coos.to_json()：转为JSON。示例：coos.to_json(value（必填）)</div>
			<div class="color-grey pdl-20">coos.to_tree()：转为Tree结构。示例：coos.to_tree(value（必填）,"id"（ID名称非必填）,"parentid"（父ID名称非必填）,"children"（子名称非必填）)</div>
			<div class="color-grey pdl-20">coos.format_date()：格式化日期字符串。示例：coos.format_date(value（必填，可以是Long类型，Date类型，String类型）,"yyyy-MM-dd HH:mm:ss"（非必填）)</div>
			<div class="color-grey pdl-20">coos.string_join()：将拼接集合字符串。示例：coos.string_join(value,"key"（非必填，用于拼接某个字段）,","（拼接符号默认“,”）)</div>
		<div class="color-grey">4：使用说明：</div>
			<div class="color-grey pdl-20">$user.user：可以取到登录用户ID</div>
	</div>
	`;
	var setJexlScript = function(text, callback) {
		text = text || '';

		let data = {
			text : text
		};
		app.formDialog({
			title : 'JexlScript配置',
			width : "800px",
			"before-html" : Editor.help_html,
			"label-width" : '0px',
			items : [ {
				label : "",
				name : "text",
				type : "textarea"
			} ],
			data : data
		}).then(() => {
			callback && callback(data.text);
		}).catch(() => {
		});

	};
})();