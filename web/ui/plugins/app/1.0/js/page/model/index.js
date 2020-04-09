
(function() {
	var PageEditor = Editor.Page;

	PageEditor.prototype.getModelBoxHtml = function() {
		let html = `
<div class="page-editor-model-box" :class="{'page-editor-model-box-show':show}">
	<div class="title">UI 模板  <a @click="closeModelBox()" class="ft-12 float-right coos-pointer">关闭</a></div>
	<div class="ui-list">
	    <el-collapse v-for="ui in uis" v-model="ui_active_name" @change="ui_active_change" accordion>
			<el-collapse-item :title="ui.title" :name="ui.name" >
				<div class="ui-group-list">
					<el-collapse v-for="group in ui.groups" :class="ui.name + '-' + group.name" v-model="ui_group_active_name" @change="ui_group_active_change" accordion>
						<el-collapse-item :title="group.title" :name="ui.name + '-' + group.name">
							<div class="ui-model-list coos-scrollbar" >
								<div v-for="model in group.models" class="ui-model-one" :class="ui.name + '-' + group.name + '-' + model.name">
									<div v-if="model.demos != null" v-for="demo in model.demos" class="ui-model-demo" @dblclick="chooseModel(ui, group, model ,demo)" :class="{'ui-model-demo-block' : model.isBlock || demo.isBlock}" v-html="demo.html">
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
</div>
		`;
		return html;
	};


	PageEditor.prototype.buildPageModel = function($box) {
		if (this.buildPageModeled) {
			return;
		}
		this.buildPageModeled = true;
		let that = this;

		let data = {};
		data.show = false;
		data.uis = this.getUIList();
		data.ui_active_name = data.uis[0].name;
		data.ui_group_active_name = data.ui_active_name + '-' + data.uis[0].groups[0].name;

		this.page_model_data = data;
		this.ui_map = {};
		data.uis.forEach((ui) => {
			ui.groups.forEach((group) => {
				group.models.forEach((model) => {
					let key = 'ui-' + ui.name + '-' + group.name + '-' + model.name;
					model.key = key;
					this.ui_map[key] = model;

					if (model.template) {
						model.demos.forEach((demo) => {
							let vue = new Vue({
								el : $('<div>' + model.template + '</div>')[0],
								data : demo.data
							});
							demo.html = vue.$el.innerHTML;
						});
					}
				});
			});
		});

		let $modelBox = $(this.getModelBoxHtml());
		$box.append($modelBox);
		new Vue({
			el : $modelBox[0],
			data : data,
			mounted () {
				let height = $(this.$el).find('.ui-list').height();
				this.uis.forEach((ui, index) => {
					let groupList = $(this.$el).find('.ui-group-list')[index];
					$(groupList).find('.ui-model-list').css('height', height - 30 * this.uis.length - 30 * ui.groups.length)
				});
			},
			methods : {
				chooseModel (ui, group, model, demo) {
					that.onChoosePageModel(ui, group, model, demo);
				},
				closeModelBox () {
					this.show = false;
				},
				ui_active_change (activeName) {
					this.$nextTick().then(res => {
						this.uis.forEach(ui => {
							if (activeName == (ui.name)) {
								if (ui.lastActiveName) {
									this.ui_group_active_name = ui.lastActiveName;
								} else {
									this.ui_group_active_name = ui.name + '-' + ui.groups[0].name;
								}
							}
						});
					});
				},
				ui_group_active_change (activeName) {}
			}
		});

	};
	PageEditor.prototype.choosePageModel = function(callback) {
		this.lastChoosePageModelCallback = callback;
		this.page_model_data.show = true;
	};
	PageEditor.prototype.onChoosePageModel = function(ui, group, model, demo) {
		let layout = {};
		layout.key = model.key;
		this.lastChoosePageModelCallback && this.lastChoosePageModelCallback(layout);
	};


})();