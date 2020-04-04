(function() {


	source.loadApp = function(project) {
		source.plugin.app.event("LOAD_APP", {
			path : project.path
		}, project).then(res => {
			let value = res.value || {};
			source.setProjectApp(project, value.app);
		});
	};


	source.appGenerateSourceCode = function(type, project) {

		source.plugin.app.event("GENERATE_SOURCE_CODE", {
			type : type,
			path : project.path
		}, project).then(res => {
			if (res.errcode == 0) {
				coos.success('生成成功！');
				source.reloadProject(project).then(res => {
					source.loadGitStatus();
				});
			} else {
				coos.error(res.errmsg);
			}
		});
	};
	let AppPlugin = function(options) {
		options = options || {};
		this.options = options;
	};

	window.AppPlugin = AppPlugin;

	function hasPermission(arg) {
		if (source.space.permission == 'MASTER') {
			return true;
		}
		if (source.space.permission == 'DEVELOPER') {
			return true;
		}
		return false;
	}

	AppPlugin.prototype.onContextmenu = function(data, menus) {

		let project = source.getProjectByPath(data.path);
		let appModel = source.getProjectApp(project);

		if (!appModel) {
			return;
		}
		menus.push({
			header : "应用"
		});
		if (data.isProject || appModel.path == data.path) {
			if (appModel.context.JAVA != null) {
				menus.push({
					text : "生成Java源码",
					disabled : !hasPermission("GENERATE_SOURCE_CODE"),
					onClick () {
						source.appGenerateSourceCode('java', project);
					}
				});
			}
			if (appModel.context.GO != null) {
				menus.push({
					text : "生成Go源码",
					disabled : !hasPermission("GENERATE_SOURCE_CODE"),
					onClick () {
						source.appGenerateSourceCode('go', project);
					}
				});
			}
			if (appModel.context.NODE != null) {
				menus.push({
					text : "生成Node源码",
					disabled : !hasPermission("GENERATE_SOURCE_CODE"),
					onClick () {
						source.appGenerateSourceCode('node', project);
					}
				});
			}
			if (appModel.context.VUE != null) {
				menus.push({
					text : "生成Vue源码",
					disabled : !hasPermission("GENERATE_SOURCE_CODE"),
					onClick () {
						source.appGenerateSourceCode('vue', project);
					}
				});
			}
			menus.push({
				text : "生成库表",
				disabled : !hasPermission("GENERATE_TABLE"),
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

		if (appModel.path == data.path || data.path.startsWith(appModel.path + "/")) {
			let model = source.getModelTypeByPath(data.path);
			if (model && model.value == "TABLE") {
				let databases = [];
				databases.push("");

				databases.forEach(database => {
					menus.push({
						text : "导入" + database + "已有表",
						disabled : !hasPermission("IMPORT_TABLE"),
						onClick () {
							source.tableImportForm.show(appModel,
								{
									databasename : database,
									parent : data
								},
								project
							).then(res => {
							});
						}
					});
				});
			}
		}
	};


})();