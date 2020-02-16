(function() {
	var ServiceEditor = coos.Editor.Service;

	ServiceEditor.prototype.getIdByProcess = function(process) {
		var id = this.id_prefix + process.name;
		return id.replace(/[^0-9a-zA-Z_]/g, '');
	};

	ServiceEditor.prototype.getNodeByProcess = function(process) {
		var id = this.getIdByProcess(process);
		return $('#' + id);
	};


	ServiceEditor.prototype.getProcessFormOptions = function(data) {
		let daos = [];
		let daoOptions = this.getOptions('DAO');
		let serviceOptions = this.getOptions('SERVICE');

		return {
			width : "800px",
			items : [ {
				label : "名称",
				name : "name",
				info : "名称可包含英文、数字、（_）、（-）",
				pattern : "^[a-zA-Z0-9_\-]{0,20}$",
				required : true
			}, {
				label : "显示文案",
				name : "text",
				required : true
			}, {
				"v-if" : "form.type != 'START' || form.type != 'END'",
				label : "类型",
				name : "type",
				required : true,
				type : "radio",
				options : [ {
					text : "决策节点",
					value : "DECISION"
				}, {
					text : "条件节点",
					value : "CONDITION"
				}, {
					text : "数据节点",
					value : "DAO"
				}, {
					text : "子服务节点",
					value : "SUB_SERVICE"
				}, {
					text : "异常结束节点",
					value : "ERROR_END"
				} ]
			}, {
				"v-if" : "form.type == 'DAO'",
				label : "数据访问名称",
				name : "daoname",
				type : "select",
				required : true,
				options : daoOptions
			}, {
				"v-if" : "form.type == 'SUB_SERVICE'",
				label : "服务名称",
				name : "servicename",
				type : "select",
				required : true,
				options : serviceOptions
			}, {
				"v-if" : "form.type == 'DAO' || form.type == 'SERVICE'",
				label : "配置数据",
				name : "data",
				info : "此处配置传入数据，默认使用$data解析的数据",
				"class-name" : "setJexlScriptBtn"
			}, {
				"v-if" : "form.type == 'CONDITION'",
				label : "条件",
				name : "condition"
			}, {
				"v-if" : "form.type == 'ERROR_END'",
				label : "错误码",
				name : "errcode"
			}, {
				"v-if" : "form.type == 'ERROR_END'",
				label : "错误信息",
				name : "errmsg"
			} ],
			data : data
		};

	};
	ServiceEditor.prototype.toInsertProcess = function(process) {
		process = process || {};
		process.type = process.type || 'DAO';

		let that = this;
		let data = {};
		Object.assign(data, process);

		let options = this.getProcessFormOptions(data);
		options.title = '添加流程';
		app.formDialog(options).then(() => {
			data.name.trim();
			var find = false;
			$(that.model.processs).each(function(index, process) {
				if (process.name == data.name) {
					find = true;
				}
			});
			if (find) {
				coos.error('名称已存在，请重新输入！');
				that.toInsertProcess(data);
				return;
			}
			if (data.name == 'start' || data.name == 'end') {
				coos.error('名称不能定义为start或end!');
				that.toInsertProcess(data);
				return;
			}

			that.recordHistory();

			$.extend(true, process, data);
			that.model.processs.push(process);
			that.changeModel();
		}).catch(() => {
		});

	};

	ServiceEditor.prototype.toUpdateProcess = function(process) {
		let that = this;
		let data = {};
		Object.assign(data, process);

		let options = this.getProcessFormOptions(data);
		options.title = '修改流程';
		app.formDialog(options).then(() => {
			data.name.trim();
			var find = false;
			$(that.model.processs).each(function(index, one) {
				if (one.name == data.name && one != process) {
					find = true;
				}
			});
			if (find) {
				coos.error('名称已存在，请重新输入！');
				that.toInsertProcess(data);
				return;
			}
			if (data.name == 'start' || data.name == 'end') {
				coos.error('名称不能定义为start或end!');
				that.toInsertProcess(data);
				return;
			}
			$(that.model.processs).each(function(index, one) {
				if (one.to == process.name) {
					one.to = data.name;
				}
			});

			that.recordHistory();
			$.extend(true, process, data);
			that.changeModel();
		}).catch(() => {
		});

	};



	ServiceEditor.prototype.toDeleteProcess = function(process, eventData) {
		var that = this;
		if (process.type == 'START') {
			coos.error('开始节点不能删除！');
			return;
		}
		if (process.type == 'END') {
			coos.error('结束节点不能删除！');
			return;
		}
		coos.confirm('确定删除' + process.name + '节点？', function() {
			$(that.model.processs).each(function(index, one) {
				if (one.to == one.name) {
					delete one.to;
				}
			});
			that.recordHistory();
			that.model.processs.splice(that.model.processs.indexOf(process), 1);
			that.changeModel();
		});
	};
})();