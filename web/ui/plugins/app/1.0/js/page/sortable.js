
(function() {
	var PageEditor = Editor.Page;
	PageEditor.prototype.bindSortable = function() {
		coos.plugin.add({
			sortable : {
				js : [ _SERVER_URL + "resources/plugins/sortable/sortable.js" ],
				css : []
			}
		});

		let that = this;
		coos.plugin.load('sortable', function() {
			that.data.uis.forEach((ui) => {
				//that.bindUISortable(ui);
			});
		});
	};

	PageEditor.prototype.bindUISortable = function(ui) {
		ui.groups.forEach((group) => {
			this.bindUIGroupSortable(ui, group);
		});
	};

	PageEditor.prototype.bindUIGroupSortable = function(ui, group) {
		group.models.forEach((model) => {
			this.bindUIModelSortable(ui, group, model);
		});
	};

	PageEditor.prototype.bindUIModelSortable = function(ui, group, model) {
		let className = ui.name + '-' + group.name + '-' + model.name;
		let $model = this.$modelBox.find('.' + className);
		let name = ui.name + '-' + group.name + '-' + model.name;
		let options = {
			sort : false,
			draggable : '>*',
			group : {
				name : name,
				pull : 'clone'
			},
			onStart : function(arg1) {
				console.log("onStart", arg1);
			},
			onEnd : function(arg1) {
				console.log("onEnd", arg1);
			},
			onSort : function(arg1) {
				console.log("onSort", arg1);
			}
		};
		$model.each(function(index, el) {

			Sortable.create(el, options);
		});


	};
})();