(function() {


	document.writeln('<link rel="stylesheet" type="text/css" href="' + _ROOT_URL + 'resources/coos/doc/main.css" />');

	var themes = [];
	var defaultTheme = {
		body : {
			style : {
				width : '80%',
				css : 'margin: 20px auto;box-shadow: 0px 0px 20px #ddd;'
			},
			center : {
				style : {
					css : 'padding: 20px;'
				}
			}
		},
		header : {
			style : {
				bgcolor : 'teal'
			},
			layouts : [ {
				type : "NAV",
				size : 'lg',
				style : {
					width : 300
				},
				contentStyle : {
					bgcolor : 'teal',
					css : 'padding: 0px 10px 0px 50px;',
					float : 'right'
				},
				navs : [ {
					image : "/resources/coos/images/coos.png",
					href : "/"
				}, {
					html : '<h3>COOS</h3>',
					href : "/"
				} ]
			}, {
				type : "NAV",
				size : 'lg',
				style : {},
				contentStyle : {
					bgcolor : 'teal',
					css : 'padding: 0px 10px;'
				},
				navs : [ {
					text : "IconFont",
					target : '_blank',
					href : "/resources/coos/font/demo_index.html"
				}, {
					text : "基础",

					subs : [ {
						text : "基本CSS",
						subs : [ {
							text : "网格",
							href : "/resources/coos/doc/css/col.html"
						}, {
							text : "颜色",
							href : "/resources/coos/doc/css/color.html"
						}, {
							text : "边框",
							href : "/resources/coos/doc/css/border.html"
						}, {
							text : "按钮",
							href : "/resources/coos/doc/css/button.html"
						}, {
							text : "圆形按钮",
							href : "/resources/coos/doc/css/button.round.html"
						}, {
							text : "按钮组",
							href : "/resources/coos/doc/css/button.group.html"
						}, {
							text : "导航",
							href : "/resources/coos/doc/css/nav.html"
						} ]
					}, {
						text : "面板",
						subs : [ {
							text : "基础",
							href : "/resources/coos/doc/panel/base.html"
						}, {
							text : "固定高度",
							href : "/resources/coos/doc/panel/height.html"
						}, {
							text : "带刷新",
							href : "/resources/coos/doc/panel/refresh.html"
						} ]
					}, {
						text : "页面",
						subs : [ {
							text : "基础",
							href : "/resources/coos/doc/page/base.html"
						}, {
							text : "固定高度",
							href : "/resources/coos/doc/page/height.html"
						}, {
							text : "带刷新",
							href : "/resources/coos/doc/page/refresh.html"
						} ]
					} ]
				}, {
					text : "表单组件",

					subs : [ {
						text : "表单",
						subs : [ {
							text : "表单验证",
							href : "/resources/coos/doc/form/validate.html"
						} ]
					}, {
						text : "输入框",
						subs : [ {
							text : "输入框尺寸",
							href : "/resources/coos/doc/input/input.size.html"
						}, {
							text : "输入框组",
							href : "/resources/coos/doc/input/input.skin.html"
						}, {
							text : "文本输入框",
							href : "/resources/coos/doc/input/text.html"
						}, {
							text : "自动完成输入框",
							href : "/resources/coos/doc/input/autocomplete.html"
						}, {
							text : "下拉框",
							href : "/resources/coos/doc/input/select.html"
						}, {
							text : "复选框",
							href : "/resources/coos/doc/input/checkbox.html"
						}, {
							text : "单选框",
							href : "/resources/coos/doc/input/radio.html"
						}, {
							text : "开关",
							href : "/resources/coos/doc/input/switch.html"
						}, {
							text : "日期/时间",
							href : "/resources/coos/doc/input/date.html"
						}, {
							text : "滑块",
							href : "/resources/coos/doc/input/slider.html"
						}, {
							text : "颜色",
							href : "/resources/coos/doc/input/color.html"
						}, {
							text : "文件/图片/视频/音频",
							href : "/resources/coos/doc/input/file.html"
						}, {
							text : "文本域",
							href : "/resources/coos/doc/input/textarea.html"
						}, {
							text : "编辑器",
							href : "/resources/coos/doc/input/editor.html"
						}, {
							text : "代码编辑器",
							href : "/resources/coos/doc/input/code.editor.html"
						} ]
					} ]
				}, {
					text : "布局",

					subs : [ {
						text : "树",
						href : "/resources/coos/doc/tree/index.html"
					}, {
						text : "表格",
						subs : [ {
							text : "表格",
							href : "/resources/coos/doc/table/table.html"
						}, {
							text : "表格分页",
							href : "/resources/coos/doc/table/pagination.html"
						}, {
							text : "数据表格",
							href : "/resources/coos/doc/table/data.html"
						} ]
					}, {
						text : "列表",
						subs : [ {
							text : "列表",
							href : "/resources/coos/doc/list/index.html"
						} ]
					}, {
						text : "网格",
						subs : [ {
							text : "列表式",
							href : "/resources/coos/doc/grid/list.html"
						}, {
							text : "卡片式",
							href : "/resources/coos/doc/grid/card.html"
						}, {
							text : "九宫格式",
							href : "/resources/coos/doc/grid/same.html"
						} ]
					}, {
						text : "选项卡",
						subs : [ {
							text : "选项卡",
							href : "/resources/coos/doc/tab/tab.html"
						} ]
					}, {
						text : "滑块（轮播）",
						subs : [ {
							text : "幻灯片",
							href : "/resources/coos/doc/slider/index.html"
						} ]
					} ]
				} ]
			}, {
				type : "NAV",
				size : 'lg',
				contentStyle : {
					bgcolor : 'teal',
					css : 'padding: 0px 10px 0px 0px;'
				},
				navs : [ {
					text : 'Template',
					href : "/resources/coos/doc/template/index.html"
				} ]
			} ]
		}
	};

	themes.push(defaultTheme);
	var frame = null;

	frame = coos.frame({
		theme : themes
	});

	function rebodyNavs(navs) {
		frame.themes[0].body.left.navs = navs;
		frame.build();
	}

	function reBuild() {
		frame.build();
	}

	$(function() {
		frame.build();

	});

})();