(function() {
	var ControlEditor = coos.Editor.Control;
	ControlEditor.prototype.createMethodView = function(method, methods) {
		var that = this;
		var $box = $('<li />');

		var $ul = $('<ul class="sub1"/>');
		$box.append($ul);

		var $li = $('<li />');
		$ul.append($li);


		$li.append('<span class="pdr-10">// </span>');
		var $input = $('<input class="input" name="comment" />');
		$input.val(method.comment);
		$li.append($input);
		that.bindLiEvent($li, method, false);


		$li = $('<li />');
		$ul.append($li);


		$li.append('<span class="pdr-10">@RequestMapping(path = "</span>');
		var $input = $('<input class="input" name="requestmapping" />');
		$input.val(method.requestmapping);
		$li.append($input);
		$li.append('<span class="pdr-10">", method = </span>');

		var $input = $('<select class="input mgr-10" name="requestmethod" ></select>');
		$li.append($input);
		$input.append('<option value="">全部</option>');
		$(that.ENUM_MAP.HTTP_METHOD).each(function(index, one) {
			$input.append('<option value="' + one.value + '">' + one.text + '</option>');
		});
		$input.val(method.requestmethod);
		$li.append($input);

		$li.append('<span class="pdr-10">) </span>');

		that.bindLiEvent($li, method, false);

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">@ResponseBody</span>');

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">public Object </span>');
		var $input = $('<input class="input" name="name" />');
		$input.val(method.name);
		$li.append($input);

		if (coos.isTrue(method.userrequestbody)) {
			$li.append('<span class="pdr-10">(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject body) { </span>');
		} else {
			$li.append('<span class="pdr-10">(HttpServletRequest request, HttpServletResponse response) { </span>');
		}


		if (coos.isTrue(method.userrequestbody)) {
			$li.append('<a class="mgl-10 coos-pointer color-green updatePropertyBtn" property-type="userrequestbody" property-value="false"  >不读取body</a>');
		} else {
			$li.append('<a class="mgl-10 coos-pointer color-orange updatePropertyBtn" property-type="userrequestbody" property-value="true">读取body</a>');
		}

		var $btn = $('<a class="mgl-10 coos-pointer color-red">删除</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			methods.splice(methods.indexOf(method), 1);
			that.changeModel();
		});

		that.bindPropertyEvent($li, method);

		that.bindLiEvent($li, method, false);


		$li = $('<li />');
		$ul.append($li);

		let $subUl = $('<ul class="sub2"/>');
		$li.append($subUl);

		$li = $('<li />');
		$subUl.append($li);
		$li.append('<span class="pdr-10 ">Object result = null;</span>');

		$li = $('<li />');
		$subUl.append($li);
		$li.append('<span class="pdr-10 ">Object value = null;</span>');


		$li = $('<li />');
		$subUl.append($li);
		$li.append('<span class="pdr-10 ">ClientSession session = ClientHandler.getSession(request);</span>');

		$li = $('<li />');
		$subUl.append($li);
		if (coos.isTrue(method.userrequestbody)) {
			$li.append('<span class="pdr-10 ">DataParam param = new DataParam(body, session);</span>');
		} else {
			$li.append('<span class="pdr-10 ">DataParam param = new DataParam(request, session);</span>');
		}
		$li = $('<li />');
		$subUl.append($li);
		$li.append('<span class="pdr-10 ">JSONObject data = param.getData();</span>');

		$li = $('<li />');
		$subUl.append($li);
		$li.append('<span class="pdr-10 ">JSONObject variableCache = param.toVariableCache(data);</span>');


		method.variables = method.variables || [];
		method.validates = method.validates || [];
		method.processs = method.processs || [];


		$li = $('<li class="mgt-10"/>');
		$subUl.append($li);
		$li.append('<span class="pdr-10 color-orange">// 定义变量存入variableCache对象，后续只需要根据名称就能获取到值</span>');

		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var variable = {};
			method.variables.push(variable);
			that.changeModel();
		});

		method.variables.forEach(variable => {
			$li = $('<li />');
			$subUl.append($li);
			var $view = this.createMethodVariableView(variable, method.variables);
			$li.append($view);
		});



		$li = $('<li class="mgt-10"/>');
		$subUl.append($li);
		$li.append('<span class="pdr-10 color-orange">// 定义一个验证，可以验证参数抛出相应的异常</span>');

		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var validate = {};
			validate.required = true;
			method.validates.push(validate);
			that.changeModel();
		});

		method.validates.forEach(validate => {
			$li = $('<li />');
			$subUl.append($li);
			var $view = this.createMethodValidateView(validate, method.validates);
			$li.append($view);
		});


		$li = $('<li />');
		$subUl.append($li);
		$li.append('<span class="pdr-10 color-orange">// 执行Service 或者 Dao</span>');

		var $btn = $('<a class="mgr-10 coos-pointer color-green">添加</a>');
		$li.append($btn);
		$btn.click(function() {
			that.recordHistory();
			var process = {};
			method.processs.push(process);
			that.changeModel();
		});

		method.processs.forEach(process => {
			$li = $('<li />');
			$subUl.append($li);
			var $view = this.createMethodProcessView(process, method.processs);
			$li.append($view);
		});


		$li = $('<li />');
		$subUl.append($li);
		$li.append('<span class="pdr-10">reutrn AppFactory.toStatus(result, exception);</span>');

		$li = $('<li />');
		$ul.append($li);
		$li.append('<span class="pdr-10">} </span>');
		return $box;

	};
})();