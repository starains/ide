
(function() {
	var Editor = coos.PageEditor;

	Editor.prototype.getModelBoxHtml = function() {
		let html = `
		<div class="title">UI 模板  <a @click="closeModelBox()" class="ft-12 float-right coos-pointer">关闭</a></div>

		<div class="ui-list">
		<el-collapse v-for="ui in uis" v-model="ui_active_name" @change="ui_active_change" accordion>
		<el-collapse-item :title="ui.title" :name="ui.name" >

		<div class="ui-group-list">

		<el-collapse v-for="group in ui.groups" :class="ui.name + '-' + group.name" v-model="ui_group_active_name" @change="ui_group_active_change" accordion>
		<el-collapse-item :title="group.title" :name="ui.name + '-' + group.name">

		<div class="ui-model-list coos-scrollbar" >

		<div v-for="model in group.models" class="ui-model-one" :class="ui.name + '-' + group.name + '-' + model.name">
		<div v-if="model.demos != null" v-for="demo in model.demos" class="ui-model-demo" @dblclick="chooseModel(ui, group, model ,demo)" :class="{'ui-model-demo-block' : demo.isBlock}" v-html="demo.html">
		</div>
		<div class="ui-model-title"><el-link type="success">{{model.title}}</el-link></div>
		</div>

		</div>

		</el-collapse-item>
		</el-collapse>

		</div>
		</el-collapse-item>
		</el-collapse>
		</div>
		`;
		return html;
	};

	Editor.prototype.buildModel = function() {};

	Editor.prototype.showAppendModel = function($parent) {
		this.lastAppendParent = $parent;
		this.$modelBox.addClass('show');
	};

	Editor.prototype.closeAppendModel = function() {
		this.$modelBox.removeClass('show');
	};

	Editor.prototype.chooseModel = function(ui, group, model, demo) {
		let $parent = $(this.lastAppendParent);
		if (coos.isEmpty(demo.template)) {
			$parent.append(demo.html);
		} else {
			$parent.append(demo.template);
		}

		this.changePage();
		//		this.closeAppendModel();


	};
})();