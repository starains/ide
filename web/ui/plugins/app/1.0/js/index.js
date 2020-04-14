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
		let that = this;
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
			let model = getModelTypeByPath(data.path);
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


	let getModelTypeByPath = function(path) {
		let project = source.getProjectByPath(path);

		let model = null;
		let app = source.getProjectApp(project);
		if (app) {

			if (app.path_model_type) {
				Object.keys(app.path_model_type).forEach(model_path => {
					let m = app.path_model_type[model_path];
					if (m.isDirectory) {
						if (model_path == path || path.startsWith(model_path + '/')) {
							model = m;
						}
					} else {
						if (model_path == path) {
							model = m;
						}
					}
				});
			}

		}
		return model;
	}

	AppPlugin.prototype.onCreateEditory = function(options) {
		let that = this;
		options = options || {};

		let file = options.file;
		if (!file) {
			return;
		}
		let project = source.getProjectByPath(file.path);

		let appBean = source.getProjectApp(project);
		if (!appBean) {
			return;
		}
		let type = null;
		let model = null;
		if (appBean.path_model_type) {
			Object.keys(appBean.path_model_type).forEach(path => {
				let m = appBean.path_model_type[path];
				if (m.isDirectory) {
					if (file.path.startsWith(path + '/')) {
						model = m;
					}
				} else {
					if (file.path == (path)) {
						model = m;
					}
				}
			});
		}
		if (!model) {
			return;
		}
		if (appBean.path_model_bean) {
			model.bean = appBean.path_model_bean[file.path];
		}
		model.bean = model.bean || {};
		file.model = model;
		type = model.value;

		options = Object.assign({}, options);

		options.type = type;
		options.plugin = this;
		options.project = project;
		try {
			options.editor.isYaml = true;
			let modeDesigner = createEditor(options);
			options.editor.addDesigner(modeDesigner);
		} catch (e) {
			coos.error(e.message);
		}
	};

	let createEditor = function(options) {
		options = options || {};
		options.type = options.type || '';
		var editor = null;
		switch (options.type.toUpperCase()) {
		case "APP":
			editor = new Editor.App(options);
			break;
		case "JDBC":
			editor = new Editor.Database(options);
			break;
		case "DATABASE":
			editor = new Editor.Database(options);
			break;
		case "TABLE":
			editor = new Editor.Table(options);
			break;
		case "DAO":
			editor = new Editor.Dao(options);
			break;
		case "SERVICE":
			editor = new Editor.Service(options);
			break;
		case "CACHE":
			editor = new Editor.Cache(options);
			break;
		case "DICTIONARY":
			editor = new Editor.Dictionary(options);
			break;
		case "CODE":
			editor = new Editor.Code(options);
			break;
		case "THEME":
			editor = new Editor.Theme(options);
			break;
		case "PAGE":
			editor = new Editor.Page(options);
			break;
		case "PAGECOMPONENT":
			editor = new Editor.PageComponent(options);
			break;
		case "RESOURCE":
			editor = new Editor.Resource(options);
			break;
		case "PLUGIN":
			editor = new Editor.Plugin(options);
			break;
		case "ATTRIBUTE":
			editor = new Editor.Attribute(options);
			break;
		case "CONTROL":
			editor = new Editor.Control(options);
			break;
		case "JEXL":
			editor = new Editor.Jexl(options);
			break;
		case "BEAN":
			editor = new Editor.Bean(options);
			break;
		case "JAVA":
			editor = new Editor.Java(options);
			break;
		default:
			editor = new Editor(options);
			break;
		}
		return editor;
	};
})();