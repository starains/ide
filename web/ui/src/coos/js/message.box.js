(function() {
	var MessageBox = function(message, options) {
		this.initOptions(message, options);
	};

	MessageBox.prototype.initOptions = function(message, options) {
		this.message = message;
		options = options || {};
		this.options = options;
		this.type = options.type;
		this.init();
	};

	MessageBox.prototype.init = function() {
		this.initView();
		this.dialog = co.dialog(this.options);
	};

	MessageBox.prototype.initView = function() {
		let html = document.createElement('div');

		let className = 'pdtb-20 pdlr-20 ';

		let type = this.type.toLowerCase();
		html.setAttribute("class", className);

		if (this.message != null) {
			if (co.isObject(this.message)) {
				html.appendChild(this.message);
			} else {
				html.innerHTML = '' + this.message;
			}
		}
		if (this.html != null) {
			if (co.isObject(this.html)) {
				html.appendChild(this.html);
			} else {
				html.innerHTML = '' + this.html;
			}
		}

		let footer = document.createElement('div');
		footer.setAttribute("class", '');

		let confirm = document.createElement('a');
		confirm.setAttribute("class", 'coos-btn bg-green');
		confirm.innerHTML = '确认';

		let that = this;
		confirm.addEventListener("click", function(e) {
			that.close(true);
		});

		let cancel = document.createElement('a');
		cancel.setAttribute("class", 'coos-btn bg-grey');
		cancel.innerHTML = '取消';
		cancel.addEventListener("click", function(e) {
			that.close(false);
		});

		if (type == 'alert') {
			footer.appendChild(confirm);
			this.options.footer = footer;
		} else if (type == 'confirm') {
			this.options.footer = footer;
			this.options['show-close'] = true;
			footer.appendChild(cancel);

			footer.appendChild(confirm);
		}



		if (co.isEmpty(this.options.width)) {
			this.options.width = '400px';
		}
		this.options.title = this.options.title || '提示'
		this.options.html = html;
		this.options.middle = true;
		this.options['show-mask'] = true;

		this.onClose_ = this.options.onClose ;
		this.options.onClose = function() {
			return that.onClose(false)
		}
		this.options.onClosed = function() {
			that.cancel();
		};

	};

	MessageBox.prototype.onClose = function(arg) {
		let flag = true;
		if (co.isFunction(this.onClose_)) {
			flag = this.onClose_(arg);
		}
		if (co.isUndefined(flag) || co.isTrue(flag)) {
			return true;
		}
		return false;
	};

	MessageBox.prototype.close = function(arg) {
		let flag = this.onClose(arg);
		if (!flag) {
			return;
		}
		this.destroy();
		if (arg) {
			this.confirm();
		} else {
			this.cancel();
		}
	};

	MessageBox.prototype.cancel = function() {
		if (co.isFunction(this.options.onCancel)) {
			this.options.onCancel();
		}
	};
	MessageBox.prototype.confirm = function() {
		if (co.isFunction(this.options.onConfirm)) {
			this.options.onConfirm();
		}

	};

	MessageBox.prototype.show = function() {
		this.dialog.show();
	};

	MessageBox.prototype.hide = function() {
		this.dialog.hide();
	};

	MessageBox.prototype.destroy = function() {
		this.dialog.destroy();
	};

	co.MessageBox = MessageBox;

})();