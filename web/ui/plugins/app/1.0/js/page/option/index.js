
(function() {
	var PageEditor = Editor.Page;

	PageEditor.prototype.getPageOptionHtml = function() {
		let html = `
<div class="page-design-option-box">
	<div class="title">设置</div>
	<template v-if="layout != null && template != null">
	<div class="pd-5 ft-12">
		<div class="remark color-grey">UI：{{remark}}</div>
		<div class="place color-grey">层级：HTML
			<template v-for="place in places"><span>&gt;</span><a class="coos-link color-green" @click="clickPlace(place)">{{place.name}}</a></template>
		</div>
		<div class="">
			<a class="coos-btn coos-btn-xs color-green mgr-5 mgb-5" :class="{'coos-disabled' : !has_add_child}" @click="add_child()">添加</a>
			<a class="coos-btn coos-btn-xs color-green mgr-5 mgb-5" :class="{'coos-disabled' : parent == null}" @click="add_before()">前边添加</a>
			<a class="coos-btn coos-btn-xs color-green mgr-5 mgb-5" :class="{'coos-disabled' : parent == null}" @click="add_after()">后边添加</a>
		</div>
		<div class="">
			<a class="coos-btn coos-btn-xs color-orange mgr-5 mgb-5" :class="{'coos-disabled' : parent == null}" @click="move_up()">上移</a>
			<a class="coos-btn coos-btn-xs color-orange mgr-5 mgb-5" :class="{'coos-disabled' : parent == null}" @click="move_dw()">下移</a>
			<a class="coos-btn coos-btn-xs color-red mgr-5 mgb-5" :class="{'coos-disabled' : parent == null}" @click="remove()">删除</a>
		</div>
	</div>
	<div class="page-design-option-tab " >
		<ul class="page-design-option-tab-nav bg-blue-grey" >
			<li><a class="coos-btn bg-blue-grey coos-btn-sm" @click="active_option_tab='form'" 
			:class="{'coos-active' : active_option_tab == 'form'}">基础</a></li>
			<li><a class="coos-btn bg-blue-grey coos-btn-sm" @click="active_option_tab='extend'" 
			:class="{'coos-active' : active_option_tab == 'extend'}">扩展</a></li>
			<li><a class="coos-btn bg-blue-grey coos-btn-sm" @click="active_option_tab='event'" 
			:class="{'coos-active' : active_option_tab == 'event'}">事件</a></li>
		</ul>
		<div class="page-design-option-panels" >
			<div class="page-design-option-form-box coos-scrollbar" v-show="active_option_tab == 'form'">
			` + this.getPageOptionFormHtml() + `
			</div>
			<div class="page-design-option-extend-box coos-scrollbar" v-show="active_option_tab == 'extend'">
			</div>
			<div class="page-design-option-event-box coos-scrollbar" v-show="active_option_tab == 'event'">
			</div>
		</div>
	</div>
	</template>
	<template v-else>
		<div class="text-center pdtb-50 ft-20 color-orange">请选择元素</div>
	</template>
</div>
		`;
		return html;
	};


	PageEditor.prototype.buildPageOption = function($box) {
		if ($box.find('.page-design-option-box').length > 0) {
			return;
		}
		let that = this;

		let data = {
			remark : "",
			rules : {},
			items : [],
			places : [],
			has_add_child : false,
			open_base : false,
			active_option_tab : 'form',
			layout : null,
			template : null,
			parent : null,
			attrs : [],
			expandAttrs : [],
			attrData : null,
			form : {
				slot : null
			},
			attrRules : []
		};

		this.page_option_data = data;

		let $pageOption = $(this.getPageOptionHtml());
		$box.append($pageOption);
		new Vue({
			el : $pageOption[0],
			data : data,
			watch : {
			},
			mounted () {},
			methods : {
				add_child () {
					that.addPageLayoutChild(this.layout);
				},
				add_before () {
					that.addPageLayoutBefore(this.layout, this.parent);
				},
				add_after () {
					that.addPageLayoutAfter(this.layout, this.parent);
				},
				move_up () {
					that.moveUpPageLayout(this.layout, this.parent);
				},
				move_dw () {
					that.moveDwPageLayout(this.layout, this.parent);
				},
				remove () {
					that.removePageLayout(this.layout, this.parent);
				},
				clickPlace (place) {},
				attrFormDataChange ($event, attr) {},
				saveAttrData () {
					if (this.template == null || this.layout == null) {
						return;
					}

					this.layout.option = this.layout.option || {}
					let optionAttrs = [];
					this.attrs.forEach(attr => {
						let optionAttr = {
							name : attr.name,
							value : this.attrData[attr.name]
						};
						if (coos.isTrue(optionAttr.isBind)) {
							optionAttr.isBind = true;
							optionAttr.bindName = attr.bindName;
						}
						if (optionAttr.isBind) {
							if (coos.isNotEmpty(optionAttr.bindName)) {
								optionAttrs.push(optionAttr);
							}
						} else {
							if (coos.isNotEmpty(optionAttr.value)) {
								optionAttrs.push(optionAttr);
							}
						}
					});

					that.recordHistory();
					this.layout.option.attrs = optionAttrs;
					this.layout.option.name = this.form.name;
					this.layout.option.slot = this.form.slot;
					that.changeModel();
				},
				layoutNameChange () {},
				layoutSlotChange () {}
			}
		});

	};
	PageEditor.prototype.onSelectPageLayout = function(layout, template) {
		let parent = this.getLayoutParent(layout);
		this.page_option_data.layout = layout;
		this.page_option_data.template = template;
		this.page_option_data.parent = parent;
		this.page_option_data.has_add_child = true;

		this.initPageOptionPlaces(layout);

		this.page_option_data.attrData = null;
		coos.trimList(this.page_option_data.attrs);

		if (template && layout) {
			let option = layout.option || {};
			this.page_option_data.form.slot = option.slot;
			let templateAttrs = [];
			templateAttrs.push({
				name : 'name',
				text : '名称'
			});
			templateAttrs.push({
				name : 'class',
				text : '类'
			});
			templateAttrs.push({
				name : 'style',
				text : '样式'
			});
			if (template.attrs) {
				template.attrs.forEach(attr => {
					templateAttrs.push(Object.assign({}, attr));
				});
			}
			templateAttrs.push({
				name : 'if',
				text : 'if'
			});
			templateAttrs.push({
				name : 'else-if',
				text : 'else-if'
			});
			templateAttrs.push({
				name : 'else',
				text : 'else'
			});
			let attrData = {};
			templateAttrs.forEach(attr => {
				attr.isBind = false;
				attr.bindName = null;
				this.page_option_data.attrs.push(attr);
				attrData[attr.name] = undefined;
				if (option.attrs) {
					option.attrs.forEach(optionAttr => {
						if (optionAttr.name == attr.name) {
							attr.isBind = coos.isTrue(optionAttr.isBind);
							attrData[attr.name] = optionAttr.value;
							attr.bindName = optionAttr.bindName;
						}
					});
				}
			});
			this.page_option_data.attrData = attrData;
		}

	};
	PageEditor.prototype.initPageOptionPlaces = function(layout) {
		let that = this;
		let root = this.getLayoutRoot();
		let places = this.page_option_data.places;
		places.splice(0, places.length);
		function addPlace(layout) {
			if (layout == null) {
				return;
			}
			if (layout == root) {
				places.push({
					name : 'PAGE',
					layout : layout
				});
			} else {
				addPlace(that.getLayoutParent(layout));
				places.push({
					name : layout.key,
					layout : layout
				});
			}

		}
		addPlace(layout);
	};

	PageEditor.prototype.addPageLayoutChild = function(layout) {
		if (layout == null) {
			return;
		}
		let that = this;
		this.choosePageTemplate(function(res) {
			that.recordHistory();
			layout.layouts = layout.layouts || [];
			layout.layouts.push(res);
			that.changeModel();
		});
	};
	PageEditor.prototype.addPageLayoutBefore = function(layout, parent) {
		if (layout == null || parent == null) {
			return;
		}
		let that = this;
		this.choosePageTemplate(function(res) {
			that.recordHistory();
			parent.layouts.splice(0, 0, res);
			that.changeModel();
		});
	};
	PageEditor.prototype.addPageLayoutAfter = function(layout, parent) {
		if (layout == null || parent == null) {
			return;
		}
		let that = this;
		this.choosePageTemplate(function(res) {
			let list = parent.layouts;
			let index = list.indexOf(layout);
			if (index >= 0) {
				that.recordHistory();
				list.splice(index + 1, 0, res);
				that.changeModel();
			}
		});
	};
	PageEditor.prototype.moveUpPageLayout = function(layout, parent) {
		if (layout == null || parent == null) {
			return;
		}

		let list = parent.layouts;
		let index = list.indexOf(layout);
		if (index > 0) {
			this.recordHistory();
			list[index] = list.splice(index - 1, 1, list[index])[0];
			this.changeModel();
		}

	};
	PageEditor.prototype.moveDwPageLayout = function(layout, parent) {
		if (layout == null || parent == null) {
			return;
		}
		let list = parent.layouts;
		let index = list.indexOf(layout);
		if (index < list.length - 1) {
			this.recordHistory();

			list[index] = list.splice(index + 1, 1, list[index])[0];

			this.changeModel();
		}
	};
	PageEditor.prototype.removePageLayout = function(layout, parent) {
		if (layout == null || parent == null) {
			return;
		}
		this.recordHistory();

		let index = parent.layouts.indexOf(layout);
		parent.layouts.splice(index, 1);

		this.changeModel();
	};

})();