(function() {
	var Editor = coos.Editor;
	Editor.prototype.getFooterHtml = function() {

		return html;
	};

	var html = '';
	html += '<div class="bg-blue-grey ft-12 " style="line-height: 25px;">';
	html += '<a class="pdlr-5 coos-btn bg-blue-grey" style="display: inline-block;" :class="{\'coos-active\' : view==\'code\'}" @click="viewCode">';
	html += '代码';
	html += '</a>';
	html += '<a class="pdlr-5 coos-btn bg-blue-grey" style="display: inline-block;" :class="{\'coos-active\' : view==\'design\'}" @click="viewDesign">';
	html += '设计';
	html += '</a>';
	html += '</div>';
})();