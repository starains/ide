(function() {
	var Editor = coos.Editor;


	Editor.prototype.recordHistory = function(isStep) {
		var index = this.historys.indexOf(this.model);
		if (index >= 0) {
			this.historys.splice(index, this.historys.length);
		} else {
		}
		this.historys.push($.extend(true, {}, this.model));
		this.stepindex = null;
		this.previous_step_nav.disabled = false;
		this.next_step_nav.disabled = true;
	};

})();