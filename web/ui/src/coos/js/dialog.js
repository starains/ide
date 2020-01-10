(function() {
	var Dialog = function(options) {
		this.initOptions(options);
	};

	Dialog.prototype.initOptions = function(options) {
		options = options || {};
		this.options = options;
		this.title = options.title;
		this.html = options.html;
		this.footer = options.footer;
		this.init();
	};

	Dialog.prototype.init = function() {
		this.initView();

	};

	Dialog.prototype.initView = function() {
		let zIndex = this.options['zIndex'] || this.options['z-index'];
		if (co.isEmpty(zIndex)) {
			zIndex = co.getNextZIndex();
		}
		this.zIndex = zIndex;

		let dialog = document.createElement("div");
		dialog.setAttribute("class", "coos-dialog");
		if (co.isNotEmpty(this.options['width'])) {
			dialog.style['width'] = this.options['width'];
		}

		this.dialog = dialog;

		let header = document.createElement("div");
		header.setAttribute("class", "coos-dialog-header");

		if (this.title != null) {
			dialog.appendChild(header);

			let title = document.createElement("div");
			title.setAttribute("class", "coos-dialog-title");
			if (co.isObject(this.title)) {
				title.appendChild(this.title);
			} else {
				title.innerHTML = '' + this.title;
			}
			header.appendChild(title);
		}

		if (co.isTrue(this.options['show-close'])) {
			dialog.appendChild(header);
			let close = document.createElement("a");
			close.setAttribute("class", "coos-dialog-close");
			let that = this;
			close.addEventListener("click", function(e) {
				that.close();
			});
			header.appendChild(close);
		}

		let body = document.createElement("div");
		body.setAttribute("class", "coos-dialog-body");
		if (this.html != null) {
			dialog.appendChild(body);
			if (co.isObject(this.html)) {
				body.appendChild(this.html);
			} else {
				body.innerHTML = '' + this.html;
			}
		}
		let footer = document.createElement("div");
		footer.setAttribute("class", "coos-dialog-footer");
		if (this.footer != null) {
			dialog.appendChild(footer);
			if (co.isObject(this.footer)) {
				footer.appendChild(this.footer);
			} else {
				footer.innerHTML = '' + this.footer;
			}
		}
		let wrapper = document.createElement('div');
		this.wrapper = wrapper;
		wrapper.setAttribute("class", "coos-dialog-wrapper");
		wrapper.style['z-index'] = zIndex;

		wrapper.appendChild(dialog);
		document.body.appendChild(wrapper);

		if (co.isTrue(this.options['middle'])) {
			let height = dialog.clientHeight;
			let top = (window.outerHeight - height) / 2 - 100;
			if (top < 50) {
				top = 50;
			}
			dialog.style.top = top + 'px';
		}

	};

	Dialog.prototype.show = function() {
		this.showMask();
		co.addClass(this.wrapper, 'coos-show');
	};

	Dialog.prototype.showMask = function() {
		if (co.isTrue(this.options['show-mask'])) {
			this.maskIndex = co.mask.show(this.zIndex - 1);
		}
	};

	Dialog.prototype.hide = function() {
		co.removeClass(this.wrapper, 'coos-show');
		this.hideMask();
	};

	Dialog.prototype.hideMask = function() {
		if (this.maskIndex != null) {
			co.mask.hide(this.maskIndex);
			this.maskIndex = null;
		}
	};

	Dialog.prototype.onClose = function() {
		let flag = true;
		if (co.isFunction(this.options.onClose)) {
			flag = this.options.onClose();
		}
		if (co.isUndefined(flag) || co.isTrue(flag)) {
			return true;
		}
		return false;
	};


	Dialog.prototype.onClosed = function() {
		if (co.isFunction(this.options.onClosed)) {
			this.options.onClosed();
		}
	};

	Dialog.prototype.close = function() {
		let flag = this.onClose();
		if (!flag) {
			return false;
		}
		this.hide();

		this.onClosed();
		return true;
	};

	Dialog.prototype.destroy = function() {
		this.wrapper.parentElement.removeChild(this.wrapper);
		this.hideMask();
	};

	co.Dialog = Dialog;

	co.dialog = function(options) {
		return new Dialog(options);
	};
	co.dialog.zIndex = 1000;
})();