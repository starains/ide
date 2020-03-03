(function() {
	var ControlEditor = coos.createClass(coos.Editor);
	coos.Editor.Control = ControlEditor;

	ControlEditor.prototype.isYaml = function() {
		return true;
	};
	ControlEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-control editor-case"></div>');
		$design.append($box);

		var model = this.model;

		var $ul = $('<ul />')
		$box.append($ul);

		var $li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10 color-red">注意：此处值、默认值等为Jexl表达式，如果写字符串的值请用单引号，示例：\'字符串值\'。</span>');

		$li = $('<li />');
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
		$li.append('<span class="pdr-10">@Controller</span>');

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">@RequestMapping("</span>');
		var $input = $('<input class="input" name="requestmapping" />');
		$input.val(model.requestmapping);
		$li.append($input);
		$li.append('<span class="pdr-10">")（不填写则不会加该注解）</span>');

		that.bindLiEvent($li, model, false);


		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">public class Controller {</span>');

		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加方法</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var method = {};
			model.methods.push(method);
			method.name = 'method_' + model.methods.length;
			that.changeModel();
		});

		model.methods = model.methods || [];

		$li = $('<li />');
		$ul.append($li);
		let $methodUl = $('<ul />');
		$li.append($methodUl);
		model.methods.forEach(method => {
			var $view = this.createMethodView(method, model.methods);
			$methodUl.append($view);
		});

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">}</span>');
	};
})();