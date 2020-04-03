(function() {

	let AppPlugin = function(options) {
		options = options || {};
		this.options = options;
	};

	window.AppPlugin = AppPlugin;

	AppPlugin.prototype.onContextmenu = function(data, menus) {

		let project = source.getProjectByPath(data.path);
		let appModel = source.getProjectApp(project);

		if (appModel) {
			menus.push({
				header : "应用"
			});
			if (data.isProject || appModel.path == data.path) {
				if (appModel.context.JAVA == null) {
					menus.push({
						text : "请在模型目录创建java文件，配置源码相关设置",
						onClick () {}
					});
				} else {
					menus.push({
						text : "生成源码",
						onClick () {
							source.appGenerateSourceCode(project);
						}
					});
				}
				menus.push({
					text : "生成库表",
					onClick () {
						let param = {};
						param.path = appModel.localpath;
						param.type = "DATABASE";
						source.plugin.app.event("doTest", param, project).then(result => {
							if (result.errcode == 0) {
								coos.success("库表创建成功！");
							} else {
								coos.error(result.errmsg);
							}
						});
					}
				});
			}

			if (
				(appModel.path == data.path ||
				data.path.startsWith(appModel.path + "/"))
			) {

				let model = source.getModelTypeByPath(data.path);


				if (model) {
					if (model.value == "TABLE") {
						let databases = [];
						databases.push("");

						databases.forEach(database => {
							menus.push({
								text : "导入" + database + "已有表",
								onClick () {
									source.tableImportForm
										.show(
											appModel,
											{
												databasename : database,
												parent : data
											},
											project
									)
										.then(res => {
										});
								}
							});
						});
					}
				}
			}
		}


	};


})();