
(function() {
	var PageEditor = Editor.Page;

	PageEditor.prototype.getUIList = function() {
		let list = [];

		list.push(new Editor.Page.UI.CoosXUI());
		list.push(new Editor.Page.UI.ElementUI());

		return list;
	};
	PageEditor.prototype.formatTemplateView = function($view, template, option) {
		if ($view == null || template == null || option == null) {
			return;
		}
		$view = $($view);
		if (option.attrs) {
			option.attrs.forEach(attr => {
				if (attr) {
					if (attr.isBind) {
						$view.attr(':' + attr.name, attr.bindName);
					} else {
						$view.attr(attr.name, attr.value);
					}
				}
			});
		}
		if (option.slot) {
			$view.append(option.slot) ;
		}

	};
	PageEditor.prototype.getPageUIBoxHtml = function() {
		let html = `
<div class="page-design-ui-box" :class="{'page-design-ui-box-show':show}">
	<div class="title">UI 模板  <a @click="closeUIBox()" class="ft-12 float-right coos-pointer">关闭</a></div>
	<div class="ui-list">
	    <el-collapse v-for="ui in uis" v-model="ui_active_name" @change="ui_active_change" accordion>
			<el-collapse-item :title="ui.title" :name="ui.key" >
				<div class="ui-group-list">
					<div v-if="ui.groups.length == 0" class="color-orange text-center pdtb-10">暂无组数据</div>
					<el-collapse v-for="group in ui.groups" :class="group.key" v-model="ui_group_active_name" @change="ui_group_active_change" accordion>
						<el-collapse-item :title="group.title" :name="group.key">
							<div v-if="group.templates.length == 0" class="color-orange text-center pdtb-10">暂无模板数据</div>
							<div class="ui-template-list coos-scrollbar" >
								<div v-for="template in group.templates" class="ui-template-one" :class="template.key">
									<div v-if="template.demos.length == 0" class="color-orange text-center pdtb-10">暂无样式数据</div>
									<div class="ui-demo-list" >
										<template v-for="demo in template.demos" >
											<div v-if="demo.divider" class="ui-template-demo-divider" ></div>
											<div v-if="!demo.divider" class="ui-template-demo" @dblclick="chooseTemplate(ui, group, template ,demo)" :class="{'ui-template-demo-block' : template.isBlock}" v-html="demo.html"></div>
										</template>
									</div>
									<div class="ui-template-title"><el-link type="success">{{template.title}}</el-link></div>
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


	PageEditor.prototype.buildPageUI = function($box) {
		if (this.buildPageUIed) {
			return;
		}
		this.buildPageUIed = true;
		let that = this;

		let data = {};
		data.show = false;
		data.uis = this.getUIList();

		this.ui_map = {};
		this.group_map = {};
		this.template_map = {};

		data.uis.forEach((ui) => {
			ui.key = ui.name;
			this.ui_map[ui.key] = ui;
			ui.groups = ui.groups || [];
			ui.groups.forEach((group) => {
				group.key = ui.name + '-' + group.name;
				group.templates = group.templates || [];
				this.group_map[group.key] = group;
				group.templates.forEach((template) => {
					template.key = ui.name + '-' + group.name + '-' + template.name;
					this.template_map[template.key] = template;

					template.demos = template.demos || [];

					template.demos.forEach((demo) => {
						if (demo.divider) {

						} else {

							let $el = $('<div />');
							let $template = $(template.code);
							this.formatTemplateView($template, template, demo);
							$el.append($template);
							let vue = new Vue({
								el : $el[0],
								computed : demo.computed,
								methods : demo.methods,
								data : demo.data
							});
							demo.html = vue.$el.innerHTML;
						}
					});
				});
			});
		});

		data.ui_active_name = null;
		data.ui_group_active_name = null;
		if (data.uis.length > 0) {
			data.ui_active_name = data.uis[0].key;
			if (data.uis[0].groups.length > 0) {
				data.ui_group_active_name = data.uis[0].groups[0].key;
			}
		}

		this.page_ui_data = data;

		let $pageUIBox = $(this.getPageUIBoxHtml());
		$box.append($pageUIBox);
		new Vue({
			el : $pageUIBox[0],
			data : data,
			watch : {
				show (value) {
					if (value) {
						this.$nextTick().then(res => {
							let width = $(this.$el).find('.ui-list').width();
							let height = $(this.$el).find('.ui-list').height();
							this.uis.forEach((ui, index) => {
								let groupList = $(this.$el).find('.ui-group-list')[index];
								$(groupList).find('.ui-template-list').css('height', height - 30 * this.uis.length - 30 * ui.groups.length)
							});
							$(this.$el).css('left', ($($box).width() - width) / 2);
						});
					}
				}
			},
			mounted () {},
			methods : {
				chooseTemplate (ui, group, template, demo) {
					that.onChoosePageTemplate(ui, group, template, demo);
				},
				closeUIBox () {
					this.show = false;
				},
				ui_active_change (activeName) {
					this.$nextTick().then(res => {
						this.uis.forEach(ui => {
							if (activeName == (ui.name)) {
								if (ui.lastActiveName) {
									this.ui_group_active_name = ui.lastActiveName;
								} else {
									if (ui.groups.length > 0) {
										this.ui_group_active_name = ui.groups[0].key;
									} else {
										this.ui_group_active_name = null;
									}
								}
							}
						});
					});
				},
				ui_group_active_change (activeName) {}
			}
		});

	};
	PageEditor.prototype.choosePageTemplate = function(callback) {
		this.lastChoosePageTemplateCallback = callback;
		this.page_ui_data.show = true;
	};
	PageEditor.prototype.onChoosePageTemplate = function(ui, group, template, demo) {
		let layout = {};
		layout.key = template.key;
		layout.option = {};
		layout.option.attrs = $.extend(true, [], demo.attrs);
		layout.option.slot = demo.slot;
		this.lastChoosePageTemplateCallback && this.lastChoosePageTemplateCallback(layout);
	};


})();