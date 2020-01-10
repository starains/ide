
(function() {
	var Editor = function(options) {
		options = options || {};
		this.initOptions(options);
		this.init();
	};

	Editor.prototype.initOptions = function(options) {
		options = options || {};
		this.options = options;
		this.readyonly = options.readyonly;
		this.context = options.context || {};
		this.source = options.source || {};
		this.ENUM_MAP = this.source.ENUM_MAP || {};

		this.data = {
			context : this.context,
			source : this.source,
			readyonly : this.readyonly,
			ENUM_MAP : this.ENUM_MAP,
			script : {
				datas : [],
				methods : [],
				services : []
			}
		};
	};

	Editor.prototype.init = function() {};


	Editor.prototype.initBox = function($box) {
		if (this.$editorBox != null) {
			return;
		}

		let html = `
		<div class="page-editor-box" >
			<div class="page-editor-header" >
			
			</div>
			<div class="page-editor-body" >
				<div class="page-editor-model-box" >
				` + this.getModelBoxHtml() + `
				</div>
				<div class="page-editor-tab page-editor-tab-page bg-blue-grey" >
					<ul class="page-editor-tab-nav " >
						<li><a class="coos-btn bg-blue-grey coos-btn-sm" @click="active_page_tab='page'" 
						:class="{'coos-active' : active_page_tab == 'page'}">页面</a></li>
						<li><a class="coos-btn bg-blue-grey coos-btn-sm" @click="active_page_tab='data'" 
						:class="{'coos-active' : active_page_tab == 'data'}">数据</a></li>
						<li><a class="coos-btn bg-blue-grey coos-btn-sm" @click="active_page_tab='method'" 
						:class="{'coos-active' : active_page_tab == 'method'}">方法</a></li>
						<li><a class="coos-btn bg-blue-grey coos-btn-sm" @click="active_page_tab='service'" 
						:class="{'coos-active' : active_page_tab == 'service'}">服务</a></li>
					</ul>
					<div class="page-editor-tab-panels" >
						<div class="page-editor-page-box coos-scrollbar" v-show="active_page_tab == 'page'">
						</div>
						<div class="page-editor-data-box coos-scrollbar" v-show="active_page_tab == 'data'">
							` + this.getVueDataHtml() + `
						</div>
						<div class="page-editor-method-box coos-scrollbar" v-show="active_page_tab == 'method'">
							` + this.getVueMethodHtml() + `
						</div>
						<div class="page-editor-service-box coos-scrollbar" v-show="active_page_tab == 'service'">
							` + this.getVueServiceHtml() + `
						</div>
					</div>
				</div>
				<div class="page-editor-tab page-editor-tab-setting bg-blue-grey" >
					<ul class="page-editor-tab-nav " >
						<li><a class="coos-btn bg-blue-grey coos-btn-sm" @click="active_setting_tab='form'" 
						:class="{'coos-active' : active_setting_tab == 'form'}">基础</a></li>
						<li><a class="coos-btn bg-blue-grey coos-btn-sm" @click="active_setting_tab='extend'" 
						:class="{'coos-active' : active_setting_tab == 'extend'}">扩展</a></li>
						<li><a class="coos-btn bg-blue-grey coos-btn-sm" @click="active_setting_tab='event'" 
						:class="{'coos-active' : active_setting_tab == 'event'}">事件</a></li>
					</ul>
					<div class="page-editor-tab-panels" >
						<div class="page-editor-form-box coos-scrollbar" v-show="active_setting_tab == 'form'">
							<div class="text-center pdtb-50 ft-20 color-orange">请选择元素</div>
						</div>
						<div class="page-editor-extend-box coos-scrollbar" v-show="active_setting_tab == 'extend'">
						</div>
						<div class="page-editor-event-box coos-scrollbar" v-show="active_setting_tab == 'event'">
						</div>
					</div>
				</div>
			</div>
		</div>
		`;
		var $editorBox = $(html);
		this.$editorBox = $editorBox;
		$box.empty();
		$box.append(this.$editorBox);
		this.initVue();
	};

	Editor.prototype.initVue = function() {
		var that = this;
		this.data.uis = this.getUIList();
		this.data.ui_active_name = this.data.uis[0].name;
		this.data.ui_group_active_name = this.data.ui_active_name + '-' + this.data.uis[0].groups[0].name;
		this.data.active_page_tab = 'page';
		this.data.active_setting_tab = 'form';
		this.vue = new Vue({
			data : that.data,
			el : that.$editorBox[0],
			mounted () {
				let height = $(this.$el).find('.ui-list').height();
				this.uis.forEach((ui, index) => {
					let groupList = $(this.$el).find('.ui-group-list')[index];
					$(groupList).find('.ui-model-list').css('height', height - 30 * this.uis.length - 30 * ui.groups.length)
				});
			},
			methods : {
				chooseModel (ui, group, model, demo) {
					that.chooseModel(ui, group, model, demo);
				},
				closeModelBox () {
					that.closeAppendModel();
				},
				ui_active_change (activeName) {
					this.uis.forEach(ui => {
						if (activeName == (ui.name)) {
							window.setTimeout(() => {
								if (ui.lastActiveName) {
									this.ui_group_active_name = ui.lastActiveName;
								} else {
									this.ui_group_active_name = ui.name + '-' + ui.groups[0].name;
								}
							}, 300);
						}
					});
				},
				ui_group_active_change (activeName) {}
			}
		});
		let $el = $(this.vue.$el);

		this.$header = $el.find('.page-editor-header');
		this.$body = $el.find('.page-editor-body');

		this.$modelBox = $el.find('.page-editor-model-box');

		this.$pageBox = $el.find('.page-editor-page-box');
		this.$dataBox = $el.find('.page-editor-data-box');
		this.$methodBox = $el.find('.page-editor-method-box');
		this.$serviceBox = $el.find('.page-editor-service-box');

		this.$formBox = $el.find('.page-editor-form-box');
		this.$eventBox = $el.find('.page-editor-event-box');
	};

	Editor.prototype.build = function($box, page) {
		this.initBox($box);

		this.buildModel();
		this.buildPage(page);
	};
	coos.PageEditor = Editor;
})();