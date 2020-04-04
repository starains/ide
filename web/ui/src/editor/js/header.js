(function() {
	var Editor = coos.Editor;
	Editor.prototype.initHeaderNav = function() {
		var that = this;
		var navs = [];
		this.data.navs = navs;

		this.save_nav = {
			fonticon : 'save',
			disabled : this.options.onSave == null || this.readyonly,
			text : "保存",
			onClick : function() {
				that.toSave();
			}
		};
		navs.push(this.save_nav);


		this.saveas_nav = {
			fonticon : 'file-copy',
			disabled : this.options.onSaveas == null,
			text : "另存为",
			onClick : function() {
				that.toSaveas();
			}
		};
		navs.push(this.saveas_nav);

		navs.push({
			line : true
		});

		this.reset_nav = {
			fonticon : 'undo',
			disabled : false,
			text : "还原",
			onClick : function() {
				that.toReset();
			}
		};
		navs.push(this.reset_nav);

		this.reload_nav = {
			fonticon : 'reload',
			disabled : false,
			text : "刷新",
			onClick : function() {
				that.toReload();
			}
		};
		navs.push(this.reload_nav);


		navs.push({
			line : true
		});

		this.previous_step_nav = {
			fonticon : 'left',
			disabled : true,
			text : "上一步",
			onClick : function() {
				that.toPreviousStep();
			}
		};
		navs.push(this.previous_step_nav);

		this.next_step_nav = {
			fonticon : 'right',
			disabled : true,
			text : "下一步",
			onClick : function() {
				that.toNextStep();
			}
		};
		navs.push(this.next_step_nav);

		navs.push({
			line : true
		});

		this.setting_nav = {
			fonticon : 'setting',
			disabled : !this.hasSetting(),
			text : "设置",
			onClick : function() {
				that.toSetting();
			}
		};
		navs.push(this.setting_nav);

		navs.push({
			line : true
		});

		this.test_nav = {
			fonticon : 'sliders',
			disabled : !this.hasTest(),
			text : "测试",
			onClick : function() {
				that.toTest();
			}
		};
		navs.push(this.test_nav);



		this.help_nav = {
			fonticon : 'info-circle',
			disabled : !this.hasHelp(),
			text : "帮助",
			onClick : function() {
				that.toHelp();
			}
		};
		navs.push(this.help_nav);
	};
	Editor.prototype.getHeaderHtml = function() {

		this.initHeaderNav();

		return html;

	};

	var html = '';
	html += '<div class="coos-nav coos-nav-full bg-blue-grey coos-nav-full coos-nav-vertical coos-nav-xs">';
	html += '<template v-for="nav in navs">';
	html += '<li v-if="nav.line">';
	html += '</li>';
	html += '<li v-else v-bind:title="nav.text" v-on:click="nav.onClick()">';
	html += '<a class="active-color-white active-bg-blue-grey-8" :class="{\'coos-disabled\' : nav.disabled}">';
	html += '<i v-show="!coos.isEmpty(nav.fonticon)" v-bind:class="\'coos-icon coos-icon-\'+ nav.fonticon"></i>';
	html += '{{nav.text}}';
	html += '</a>';
	html += '</li>';
	html += '</template>';
	html += '</div>';
})();