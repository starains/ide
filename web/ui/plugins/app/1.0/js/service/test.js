(function() {
	var ServiceEditor = Editor.Service;


	ServiceEditor.prototype.hasTest = function() {
		return true;
	};

	ServiceEditor.prototype.toTest = function() {
		var that = this;
		let data = {
			body : "{\n}",
			result : ""
		};
		app.formDialog({
			title : '测试服务',
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
})();