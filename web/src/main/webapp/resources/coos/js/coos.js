(function(window) {
	'use strict';
var co = new Object();

co.style = new Object();

window.coos = co;

window.COOS = co;
co.replaceAll = function(text, arg1, arg2) {
	if (co.isEmpty(text)) {
		return text;
	}
	return text.replace(new RegExp(arg1, "gm"), arg2);
};


co.createClass = function(arg1) {
	var Class = function() {}
	if (arg1) {
		var Class = function(arg) {
			arg1.call(this, arg);
		}
		var Super = function() {};
		Super.prototype = arg1.prototype;
		Class.prototype = new Super();
	}
	return Class;
};
var numberindex = 0;
co.getNumber = function() {
	numberindex++;
	var thisid = null;
	return new Date().getTime() - 1200000000000 + "" + Math.floor(Math.random() * 9 + 1) + "" + Math.floor(Math.random() * 9 + 1) + "" + numberindex;
};
co.trimObject = function(obj, trims) {
	trims = trims || [ undefined, null ];
	if (obj == null) {
		return;
	}
	if (co.isArray(obj)) {

		obj.forEach(o => {
			co.trimObject(o, trims);
		});
	} else if (co.isObject(obj)) {
		for (var key in obj) {
			var value = obj[key];
			if (co.isArray(value)) {
				co.trimObject(value, trims) ;
			} else if (co.isObject(value)) {
				co.trimObject(value, trims);
			} else {
				if (trims.indexOf(value) >= 0) {
					delete obj[key];
				}
			}
		}
	}
};
co.isArray = function(arg) {
	if (!co.isObject(arg)) {
		return false;
	}
	return arg.constructor == Array;
};

co.isString = function(arg) {
	return typeof (arg) === "string";
};
co.isObject = function(arg) {
	return typeof (arg) === "object";
};
co.isFunction = function(arg) {
	return typeof (arg) === "function";
};
co.isNumber = function(arg) {
	if (co.isEmpty(arg)) {
		return false;
	}
	return (/^[-]?[0-9]+\.?[0-9]*$/.test(arg));
};
co.isBoolean = function(arg) {
	if (co.isEmpty(arg)) {
		return false;
	}
	return typeof (arg) === "boolean";
};
co.isInteger = function(arg) {
	if (co.isEmpty(arg)) {
		return false;
	}
	return (/^-?[0-9]*$/.test(arg));
};
co.isUndefined = function(arg) {
	return typeof (arg) == "undefined";
};
co.isEmpty = function(arg) {
	return co.isUndefined(arg) || arg == null || arg.length == 0;
};
co.isNotEmpty = function(arg) {
	return !co.isEmpty(arg);
};
co.isTrue = function(arg) {
	var flag = false;
	if (arg != null) {
		if (co.isBoolean(arg)) {
			flag = arg;
		} else {
			if (arg == 'true' || arg == '1' || arg == 1) {
				flag = true;
			}
		}
	}
	return flag;
};
co.isFalse = function(arg) {

	return !co.isTrue(arg);
};
co.getKeys = function(arg) {
	if (arg == null) {
		return [];
	}
	return Object.keys(arg);
};
co.replaceList = function(arg1, arg2) {
	co.replaceArray(arg1, arg2);
};
co.replaceArray = function(arg1, arg2) {
	if (co.isArray(arg1) && co.isArray(arg2)) {
		co.trimList(arg1);
		arg2.forEach(function(one, i, array) {
			arg1.push(one);
		})
	}
};

co.trimList = function(arg) {
	co.trimArray(arg);
};
co.trimArray = function(arg) {
	if (co.isArray(arg)) {
		arg.splice(0, arg.length);
	}
};
co.trimDate = function(arg) {
	if (arg == null) {
		return arg;
	}
	let date = arg;
	if (arg instanceof Date) {
		date = co.formatDate(arg);
	}
	date = '' + date;
	return arg.replace(/\D/g, '');
};
co.formatDate = function(arg, format) {
	if (arg == null) {
		return arg;
	}
	format = format || "yyyy-MM-dd hh:mm:ss";
	let date = arg;
	if (arg instanceof Date) {

	} else {
		date = co.toDate(arg);
	}
	var o = {
		"M+" : date.getMonth() + 1, // month
		"d+" : date.getDate(), // day
		"h+" : date.getHours(), // hour
		"H+" : date.getHours(), // hour
		"m+" : date.getMinutes(), // minute
		"s+" : date.getSeconds(), // second
		"q+" : Math.floor((date.getMonth() + 3) / 3), // quarter
		"S" : date.getMilliseconds()
	};
	if (/(y+)/.test(format)) {
		format = format.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
	}
	for (var k in o) {
		if (new RegExp("(" + k + ")").test(format)) {
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
		}
	}
	return format;

};
co.toDate = function(arg) {
	if (arg == null) {
		return arg;
	}
	if (arg instanceof Date) {
		return arg;
	}
	arg = co.trimDate(arg);
	let date = '';
	let cs = arg.split('');
	for (let i = 0; i < cs.length; i++) {
		let c = cs[i];
		let a = '';
		if (i == 4 || i == 6) {
			a = '/';
		}
		if (i == 8) {
			a = ' ';
		}
		if (i == 10 || i == 12 || i == 14) {
			a = ':';
		}
		date += a + c;
	}
	return new Date(date);
};
co.getColorSuffix = function(color) {
	if (co.isNotEmpty(color) && co.startsWith('#')) {
		co.style.addColor(color);
		color = color.substring(1);
	}
	return color;
};
co.toURL = function(arg) {
	window.location.href = arg;
};
co.isRequired = function(input) {
	if (input == null) {
		return;
	}
	if (input.length != null) {
		if (input.length > 0) {
			input = input[0];
		} else {
			return;
		}
	}

	var required = input.getAttribute('isrequired');

	if (typeof (required) != 'undefined') {
		return co.isTrue(required);
	}
	required = input.getAttribute('required');
	if (typeof (required) == 'undefined') {
		return false;
	}
	if (required == 'required') {
		return true;
	}
	return co.isTrue(required);
};
co.addClass = function(dom, addClassName) {
	if (dom == null || co.isEmpty(addClassName)) {
		return;
	}
	let classNames = [];
	if (co.isNotEmpty(dom.className)) {
		classNames = dom.className.split(' ');
	}
	let addClassNames = addClassName.split(' ');

	addClassNames.forEach(addClassName => {
		addClassName = addClassName.trim();
		if (co.isNotEmpty(addClassName)) {
			let has = false;
			classNames.forEach(className => {
				className = className.trim();
				if (addClassName == className) {
					has = true;
				}
			});
			if (!has) {
				classNames.push(addClassName);
			}
		}
	});
	dom.className = classNames.join(' ');
};

co.removeClass = function(dom, removeClassName) {
	if (dom == null || co.isEmpty(removeClassName)) {
		return;
	}
	let classNames = [];
	if (co.isNotEmpty(dom.className)) {
		classNames = dom.className.split(' ');
	}
	let removeClassNames = removeClassName.split(' ');

	removeClassNames.forEach(removeClassName => {
		removeClassName = removeClassName.trim();
		if (co.isNotEmpty(removeClassName)) {
			classNames.forEach((className, index) => {
				className = className.trim();
				if (removeClassName == className) {
					classNames[index] = '';
				}
			});
		}
	});
	dom.className = classNames.join(' ');
};
co.info = function(arg1, options) {
	options = options || {};
	options.type = 'info';
	if (options['show-icon'] == null) {
		options['show-icon'] = true;
	}
	var message = new co.Message(arg1, options);
	message.show();
};
co.success = function(arg1, options) {
	options = options || {};
	options.type = 'success';
	if (options['show-icon'] == null) {
		options['show-icon'] = true;
	}
	var message = new co.Message(arg1, options);
	message.show();
};

co.warn = function(arg1, options) {
	options = options || {};
	options.type = 'warn';
	if (options['show-icon'] == null) {
		options['show-icon'] = true;
	}
	var message = new co.Message(arg1, options);
	message.show();
};

co.error = function(arg1, options) {
	options = options || {};
	options.type = 'error';
	if (options['show-icon'] == null) {
		options['show-icon'] = true;
	}
	var message = new co.Message(arg1, options);
	message.show();
};

co.alert = function(arg1) {
	let confirm,
		cancel,
		options = {};

	for (var i = 1; i < arguments.length; i++) {
		var argument = arguments[i];
		if (co.isFunction(argument)) {
			if (confirm == null) {
				confirm = argument;
			} else {
				cancel = argument;
			}
		} else if (co.isObject(argument)) {
			options = argument;
		}
	}
	options.type = 'alert';
	options.onConfirm = function() {
		if (co.isFunction(confirm)) {
			confirm();
		}
	};
	options.onCancel = function() {
		if (co.isFunction(cancel)) {
			cancel();
		}
	};

	var messageBox = new co.MessageBox(arg1, options);
	messageBox.show();
	return new Promise((resolve, reject) => {
		if (!co.isFunction(confirm)) {
			confirm = resolve;
		}
		if (!co.isFunction(cancel)) {
			cancel = reject;
		}
	});
};

co.confirm = function(arg1) {
	let confirm,
		cancel,
		options = {};

	for (var i = 1; i < arguments.length; i++) {
		var argument = arguments[i];
		if (co.isFunction(argument)) {
			if (confirm == null) {
				confirm = argument;
			} else {
				cancel = argument;
			}
		} else if (co.isObject(argument)) {
			options = argument;
		}
	}
	options.type = 'confirm';
	options.onConfirm = function() {
		if (co.isFunction(confirm)) {
			confirm();
		}
	};
	options.onCancel = function() {
		if (co.isFunction(cancel)) {
			cancel();
		}
	};

	var messageBox = new co.MessageBox(arg1, options);
	messageBox.show();
	return new Promise((resolve, reject) => {
		if (!co.isFunction(confirm)) {
			confirm = resolve;
		}
		if (!co.isFunction(cancel)) {
			cancel = reject;
		}
	});
};
co.startZIndex = 1000;
let index_number = 0;
co.getNextZIndex = function() {
	if (index_number > 0) {
		index_number++;
	}
	let index = co.startZIndex + index_number;
	if (index_number == 0) {
		index_number++;
	}
	return index;
};
(function() {
	var Mask = function(options) {
		this.initOptions(options);
	};

	Mask.prototype.initOptions = function(options) {
		options = options || {};
		this.options = options;
		this.init();
	};

	Mask.prototype.init = function() {
		this.initView();

	};

	Mask.prototype.initView = function() {
		let mask = document.createElement("div");
		if (co.isNotEmpty(this.options['zIndex'])) {
			mask.style['z-index'] = this.options['zIndex'];
		} else if (co.isNotEmpty(this.options['z-index'])) {
			mask.style['z-index'] = this.options['z-index'];
		} else {
			co.dialog.zIndex++;
			mask.style['z-index'] = co.dialog.zIndex;
		}
		mask.setAttribute("class", "coos-mask");
		this.mask = mask;

		document.body.appendChild(mask);
	};

	Mask.prototype.show = function() {
		co.addClass(this.mask, 'coos-show');
	};

	Mask.prototype.hide = function() {
		co.removeClass(this.mask, 'coos-show');
	};

	Mask.prototype.close = function() {
		this.hide();
	};

	Mask.prototype.destroy = function() {
		this.mask.parentElement.removeChild(this.mask);
	};

	co.Mask = Mask;

	co.mask = function(options) {
		return new Mask(options);
	};

	let zIndexs = [];

	co.mask.show = function(zIndex) {
		if (zIndex == null) {
			zIndex = co.getNextZIndex();
		}
		if (window.COOS_MASK_DOM == null) {
			window.COOS_MASK_DOM = document.createElement("div");
			window.COOS_MASK_DOM.setAttribute("class", "coos-mask");
			document.body.appendChild(window.COOS_MASK_DOM);
		}
		co.addClass(window.COOS_MASK_DOM, 'coos-show');
		zIndexs.push(zIndex);
		window.COOS_MASK_DOM.style['z-index'] = zIndex;
		return zIndexs.length - 1;
	};

	co.mask.hide = function(index) {
		if (index < 0 || index >= zIndexs.length) {
			return;
		}
		zIndexs.splice(index, 1);
		if (window.COOS_MASK_DOM != null) {
			if (zIndexs.length == 0) {
				co.removeClass(window.COOS_MASK_DOM, 'coos-show');
			} else {
				var max = Math.max.apply(Math, zIndexs);
				window.COOS_MASK_DOM.style['z-index'] = max;
			}
		}
	};
})();
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
(function() {
	var Message = function(message, options) {
		this.initOptions(message, options);
	};

	Message.prototype.initOptions = function(message, options) {
		this.message = message;
		options = options || {};
		this.options = options;
		this.type = options.type;
		this.html = options.html;
		if (co.isEmpty(options.duration)) {
			this.duration = 3000;
		} else {
			this.duration = parseInt(options.duration);
		}
		this.init();
	};

	Message.prototype.init = function() {
		this.initView();
	};

	Message.prototype.initView = function() {
		let message = document.createElement('div');

		let className = 'coos-message ';

		let type = this.type.toLowerCase();
		if (type == 'info') {
			className += 'coos-message-info ';
		} else if (type == 'success') {
			className += 'coos-message-success ';
		} else if (type == 'warn' || type == 'warning') {
			className += 'coos-message-warn ';
		} else if (type == 'error') {
			className += 'coos-message-error ';
		} else {
			className += ' ';
		}
		message.setAttribute("class", className);

		let icon = document.createElement('span');
		icon.setAttribute("class", "coos-message-icon");
		if (co.isTrue(this.options['show-icon'])) {
			message.appendChild(icon);
		}

		let content = document.createElement('span');
		content.setAttribute("class", "coos-message-content");
		message.appendChild(content);

		if (this.message != null) {
			if (co.isObject(this.message)) {
				content.appendChild(this.message);
			} else {
				content.innerHTML = '' + this.message;
			}
		}
		if (this.html != null) {
			if (co.isObject(this.html)) {
				content.appendChild(this.html);
			} else {
				content.innerHTML = '' + this.html;
			}
		}
		let that = this;
		let close = document.createElement('a');
		close.setAttribute("class", "coos-message-close");
		close.addEventListener("click", function(e) {
			that.close();
		});
		if (co.isTrue(this.options['show-close'])) {
			message.appendChild(close);
		}

		this.message = message;
		this.append(this.message);
	};

	Message.prototype.show = function() {
		setTimeout(() => {
			co.addClass(this.message, 'coos-show');
			if (this.duration > 0) {
				setTimeout(() => {
					this.close();
				}, this.duration)
			}
		}, 10);
	};
	Message.prototype.close = function() {
		this.destroy();

	};
	Message.prototype.destroy = function() {
		co.removeClass(this.message, 'coos-show');
		setTimeout(() => {
			if (this.message.parentElement) {
				this.message.parentElement.removeChild(this.message);
			}
		}, 300)

	};

	Message.prototype.append = function(message) {

		if (window.COOS_MESSAGE_BOX == null) {
			window.COOS_MESSAGE_BOX = document.createElement('div');
			window.COOS_MESSAGE_BOX.setAttribute("class", "coos-message-box");
			document.body.appendChild(window.COOS_MESSAGE_BOX);
		}
		window.COOS_MESSAGE_BOX.style['z-index'] = co.getNextZIndex();
		window.COOS_MESSAGE_BOX.appendChild(message);
	};



	co.Message = Message;

})();
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
(function() {

	var common = {
		black : '#00000',
		white : '#FFFFFF'
	};
	var Color = function(options) {
		options = options || {};
		this.options = options;
		this.initOptions();
		this.init();
	};

	Color.prototype.initOptions = function() {
		this.main = this.options.main;
		if (this.main == null || this.main.trim().length == 0) {
			throw new Error('main不能为空，请设置主色');
		}
		this.contrastThreshold = this.options.contrastThreshold || 3;
		this.tonalOffset = this.options.tonalOffset || 0.2;

	};
	Color.prototype.init = function() {
		this.initColors();
	};
	Color.prototype.initColors = function() {
		this.colors = [];
		this.colors.push(this.main);

		this.colors.push(lighten(this.main, .4));
		this.colors.push(lighten(this.main, .3));
		this.colors.push(lighten(this.main, .2));
		this.colors.push(lighten(this.main, .1));
		this.colors.push(this.main);
		this.colors.push(darken(this.main, .1));
		this.colors.push(darken(this.main, .2));
		this.colors.push(darken(this.main, .3));
		this.colors.push(darken(this.main, .4));
	};
	Color.prototype.getColors = function() {
		return this.colors;
	};
	Color.prototype.getContrastText = function(background) {
		return getContrastRatio(background, common.white) >= this.contrastThreshold ? common.white : common.black;
	};
	/**
	 * 返回其值仅限于给定范围的数字。
	 */
	function clamp(value) {
		var min = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0;
		var max = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : 1;

		if (value < min) {
			return min;
		}

		if (value > max) {
			return max;
		}

		return value;
	}
	/**
	 * 将颜色从CSS十六进制格式转换为CSS rgb格式。
	 */
	function convertHexToRGB(color) {
		color = color.substr(1);
		var re = new RegExp(".{1,".concat(color.length / 3, "}"), 'g');
		var colors = color.match(re);

		if (colors && colors[0].length === 1) {
			colors = colors.map(function(n) {
				return n + n;
			});
		}

		return colors ? "rgb(".concat(colors.map(function(n) {
			return parseInt(n, 16);
		}).join(', '), ")") : '';
	}
	/**
	 * 返回具有颜色类型和值的对象. 注意：不支持rgb％值。
	 */
	function decomposeColor(color) {
		if (color.charAt(0) === '#') {
			return decomposeColor(convertHexToRGB(color));
		}

		var marker = color.indexOf('(');
		var type = color.substring(0, marker);
		var values = color.substring(marker + 1, color.length - 1).split(',');
		values = values.map(function(value) {
			return parseFloat(value);
		});
		{
			if ([ 'rgb', 'rgba', 'hsl', 'hsla' ].indexOf(type) === -1) {
				throw new Error('紧持以下格式：#nnn，＃nnnnnn，rgb（），rgba（），hsl（），hsla（）');
			}
		}

		return {
			type : type,
			values : values
		};
	}
	/**
	 * 将具有类型和值的颜色对象转换为字符串。
	 */
	function recomposeColor(color) {
		var type = color.type;
		var values = color.values;
		if (type.indexOf('rgb') !== -1) {
			// Only convert the first 3 values to int (i.e. not alpha)
			values = values.map(function(n, i) {
				return i < 3 ? parseInt(n, 10) : n;
			});
		}
		if (type.indexOf('hsl') !== -1) {
			values[1] = "".concat(values[1], "%");
			values[2] = "".concat(values[2], "%");
		}

		return "".concat(color.type, "(").concat(values.join(', '), ")");
	}
	/**
	 * 计算两种颜色之间的对比度。
	 */
	function getContrastRatio(foreground, background) {
		var lumA = getLuminance(foreground);
		var lumB = getLuminance(background);
		return (Math.max(lumA, lumB) + 0.05) / (Math.min(lumA, lumB) + 0.05);
	}
	/**
	 * 颜色空间中任何点的相对亮度，对于最暗的黑色和黑色标准化为0,1为最轻的白色。
	 */
	function getLuminance(color) {
		var decomposedColor = decomposeColor(color);

		if (decomposedColor.type.indexOf('rgb') !== -1) {
			var rgb = decomposedColor.values.map(function(val) {
				val /= 255; // normalized

				return val <= 0.03928 ? val / 12.92 : Math.pow((val + 0.055) / 1.055, 2.4);
			}); // Truncate at 3 digits

			return Number((0.2126 * rgb[0] + 0.7152 * rgb[1] + 0.0722 * rgb[2]).toFixed(3));
		} // else if (decomposedColor.type.indexOf('hsl') !== -1)

		return decomposedColor.values[2] / 100;
	}
	/**
	 * 根据亮度，使颜色变暗或变浅。 浅色变暗，暗色变浅。
	 */
	function emphasize(color) {
		var coefficient = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0.15;
		return getLuminance(color) > 0.5 ? darken(color, coefficient) : lighten(color, coefficient);
	}
	/**
	 * 设置颜色的绝对透明度。 任何现有的alpha值都会被覆盖。
	 */
	function fade(color, value) {
		if (!color)
			return color;
		color = decomposeColor(color);
		value = clamp(value);

		if (color.type === 'rgb' || color.type === 'hsl') {
			color.type += 'a';
		}

		color.values[3] = value;
		return recomposeColor(color);
	}
	/**
	 * 使颜色变暗。
	 */
	function darken(color, coefficient) {
		if (!color)
			return color;
		color = decomposeColor(color);
		coefficient = clamp(coefficient);

		if (color.type.indexOf('hsl') !== -1) {
			color.values[2] *= 1 - coefficient;
		} else if (color.type.indexOf('rgb') !== -1) {
			for (var i = 0; i < 3; i += 1) {
				color.values[i] *= 1 - coefficient;
			}
		}

		return recomposeColor(color);
	}
	/**
	 * 淡化颜色。
	 */
	function lighten(color, coefficient) {
		if (!color)
			return color;
		color = decomposeColor(color);
		coefficient = clamp(coefficient);

		if (color.type.indexOf('hsl') !== -1) {
			color.values[2] += (100 - color.values[2]) * coefficient;
		} else if (color.type.indexOf('rgb') !== -1) {
			for (var i = 0; i < 3; i += 1) {
				color.values[i] += (255 - color.values[i]) * coefficient;
			}
		}

		return recomposeColor(color);
	}

	function addLightOrDark(intent, direction, shade, tonalOffset) {
		if (!intent[direction]) {
			if (intent.hasOwnProperty(shade)) {
				intent[direction] = intent[shade];
			} else if (direction === 'light') {
				intent.light = lighten(intent.main, tonalOffset);
			} else if (direction === 'dark') {
				intent.dark = darken(intent.main, tonalOffset * 1.5);
			}
		}
	}

	Color.getContrastText = function(background) {
		return getContrastRatio(background, common.white) >= 3 ? common.white : common.black;
	};

	co.Color = Color;
})();
(function() {

	const config = {};
	config.distances = [];
	for (var i = 0; i <= 100; i++) {
		if (i <= 10) {
			config.distances.push(i);
		} else {
			config.distances.push(i);
		}
	}

	config.sizes = [
		{
			name : "",
			text : "默认",
			"font-size" : "15px",
			padding : "3px 13px",
			height : "34px",
			"line-height" : "26px",
			"img-height" : "26px"
		},
		{
			name : "xs",
			text : "最小",
			"font-size" : "12px",
			padding : "1px 5px",
			height : "18px",
			"line-height" : "14px",
			"img-height" : "14px"
		},
		{
			name : "sm",
			text : "小",
			"font-size" : "14px",
			padding : "2px 8px",
			height : "26px",
			"line-height" : "20px",
			"img-height" : "20px"
		},
		{
			name : "md",
			text : "大",
			"font-size" : "18px",
			padding : "6px 18px",
			height : "42px",
			"line-height" : "28px",
			"img-height" : "28px"
		},
		{
			name : "lg",
			text : "最大",
			"font-size" : "22px",
			padding : "8px 25px",
			height : "50px",
			"line-height" : "32px",
			"img-height" : "32px"
		}
	];

	config.cols = [ {
		text : "0格",
		value : "0",
		width : "0%"
	}, {
		text : "1格",
		value : "1",
		width : "8.33333333%"
	}, {
		text : "2格",
		value : "2",
		width : "16.66666667%"
	}, {
		text : "3格",
		value : "3",
		width : "25%"
	}, {
		text : "4格",
		value : "4",
		width : "33.33333333%"
	}, {
		text : "5格",
		value : "5",
		width : "41.66666667%"
	}, {
		text : "6格",
		value : "6",
		width : "50%"
	}, {
		text : "7格",
		value : "7",
		width : "58.33333333%"
	}, {
		text : "8格",
		value : "8",
		width : "66.66666667%"
	}, {
		text : "9格",
		value : "9",
		width : "75%"
	}, {
		text : "10格",
		value : "10",
		width : "83.33333333%"
	}, {
		text : "11格",
		value : "11",
		width : "91.66666667%"
	}, {
		text : "12格",
		value : "12",
		width : "100%"
	}, {
		text : "0.5格",
		value : "0.5",
		width : "4.166666665%"
	}, {
		text : "1.5格",
		value : "1.5",
		width : "12.5%"
	}, {
		text : "2.5格",
		value : "2.5",
		width : "20.833333335%"
	}, {
		text : "3.5格",
		value : "3.5",
		width : "29.166666665%"
	}, {
		text : "4.5格",
		value : "4.5",
		width : "37.5%"
	}, {
		text : "5.5格",
		value : "5.5",
		width : "45.833333335%"
	}, {
		text : "6.5格",
		value : "6.5",
		width : "54.166666665%"
	}, {
		text : "7.5格",
		value : "7.5",
		width : "62.5%"
	}, {
		text : "8.5格",
		value : "8.5",
		width : "70.833333335%"
	}, {
		text : "9.5格",
		value : "9.5",
		width : "79.166666665%"
	}, {
		text : "10.5格",
		value : "10.5",
		width : "87.5%"
	}, {
		text : "11.5格",
		value : "11.5",
		width : "95.833333335%"
	} ];

	config.colors = [ {
		text : "白色",
		value : "white",
		colors : [ "#FFFFFF" ]
	}, {
		text : "黑色",
		value : "black",
		colors : [ "#000000" ]
	}, {
		text : "红色",
		value : "red",
		colors : [ "#F44336", "#FFCDD2", "#EF9A9A", "#E57373", "#EF5350", "#F44336", "#E53935", "#D32F2F", "#C62828", "#B71C1C" ]
	}, {
		text : "粉色",
		value : "pink",
		colors : [ "#E91E63", "#F8BBD0", "#F48FB1", "#F06292", "#EC407A", "#E91E63", "#D81B60", "#C2185B", "#AD1457", "#880E4F" ]
	}, {
		text : "紫色",
		value : "purple",
		colors : [ "#9C27B0", "#E1BEE7", "#CE93D8", "#BA68C8", "#AB47BC", "#9C27B0", "#8E24AA", "#7B1FA2", "#6A1B9A", "#4A148C" ]
	}, {
		text : "深紫色",
		value : "deep-purple",
		colors : [ "#673AB7", "#D1C4E9", "#B39DDB", "#9575CD", "#7E57C2", "#673AB7", "#5E35B1", "#512DA8", "#4527A0", "#311B92" ]
	}, {
		text : "靛青色",
		value : "indigo",
		colors : [ "#3F51B5", "#C5CAE9", "#9FA8DA", "#7986CB", "#5C6BC0", "#3F51B5", "#3949AB", "#303F9F", "#283593", "#1A237E" ]
	}, {
		text : "蓝色",
		value : "blue",
		colors : [ "#2196F3", "#BBDEFB", "#90CAF9", "#64B5F6", "#42A5F5", "#2196F3", "#1E88E5", "#1976D2", "#1565C0", "#0D47A1" ]
	}, {
		text : "浅蓝色",
		value : "light-blue",
		colors : [ "#03A9F4", "#B3E5FC", "#81D4FA", "#4FC3F7", "#29B6F6", "#03A9F4", "#039BE5", "#0288D1", "#0277BD", "#01579B" ]
	}, {
		text : "青色",
		value : "cyan",
		colors : [ "#00BCD4", "#B2EBF2", "#80DEEA", "#4DD0E1", "#26C6DA", "#00BCD4", "#00ACC1", "#0097A7", "#00838F", "#006064" ]
	}, {
		text : "兰绿色",
		value : "teal",
		colors : [ "#009688", "#B2DFDB", "#80CBC4", "#4DB6AC", "#26A69A", "#009688", "#00897B", "#00796B", "#00695C", "#004D40" ]
	}, {
		text : "绿色",
		value : "green",
		colors : [ "#4CAF50", "#C8E6C9", "#A5D6A7", "#81C784", "#66BB6A", "#4CAF50", "#43A047", "#388E3C", "#2E7D32", "#1B5E20" ]
	}, {
		text : "浅绿",
		value : "light-green",
		colors : [ "#8BC34A", "#DCEDC8", "#C5E1A5", "#AED581", "#9CCC65", "#8BC34A", "#7CB342", "#689F38", "#558B2F", "#33691E" ]
	}, {
		text : "青柠色",
		value : "lime",
		colors : [ "#CDDC39", "#F0F4C3", "#E6EE9C", "#DCE775", "#D4E157", "#CDDC39", "#C0CA33", "#AFB42B", "#9E9D24", "#827717" ]
	}, {
		text : "黄色",
		value : "yellow",
		colors : [ "#FFEB3B", "#FFF9C4", "#FFF59D", "#FFF176", "#FFEE58", "#FFEB3B", "#FDD835", "#FBC02D", "#F9A825", "#F57F17" ]
	}, {
		text : "琥珀色",
		value : "amber",
		colors : [ "#FFC107", "#FFECB3", "#FFE082", "#FFD54F", "#FFCA28", "#FFC107", "#FFB300", "#FFA000", "#FF8F00", "#FF6F00" ]
	}, {
		text : "橙色",
		value : "orange",
		colors : [ "#FF9800", "#FFE0B2", "#FFCC80", "#FFB74D", "#FFA726", "#FF9800", "#FB8C00", "#F57C00", "#EF6C00", "#E65100" ]
	}, {
		text : "深橙色",
		value : "deep-orange",
		colors : [ "#FF5722", "#FFCCBC", "#FFAB91", "#FF8A65", "#FF7043", "#FF5722", "#F4511E", "#E64A19", "#D84315", "#BF360C" ]
	}, {
		text : "棕色",
		value : "brown",
		colors : [ "#795548", "#D7CCC8", "#BCAAA4", "#A1887F", "#8D6E63", "#795548", "#6D4C41", "#5D4037", "#4E342E", "#3E2723" ]
	}, {
		text : "灰色",
		value : "grey",
		colors : [ "#9E9E9E", "#F5F5F5", "#EEEEEE", "#E0E0E0", "#BDBDBD", "#9E9E9E", "#757575", "#616161", "#424242", "#212121" ]
	}, {
		text : "灰色",
		value : "gray",
		colors : [ "#9E9E9E", "#F5F5F5", "#EEEEEE", "#E0E0E0", "#BDBDBD", "#9E9E9E", "#757575", "#616161", "#424242", "#212121" ]
	}, {
		text : "蓝灰色",
		value : "blue-grey",
		colors : [ "#607D8B", "#CFD8DC", "#B0BEC5", "#90A4AE", "#78909C", "#607D8B", "#546E7A", "#455A64", "#37474F", "#263238" ]
	}, {
		text : "蓝灰色",
		value : "blue-gray",
		colors : [ "#607D8B", "#CFD8DC", "#B0BEC5", "#90A4AE", "#78909C", "#607D8B", "#546E7A", "#455A64", "#37474F", "#263238" ]
	} ];
	co.style.config = config;
})();
(function() {
	let style = co.style;
	let config = style.config;
	let eotBase64 = "NAoAAIgJAAABAAIAAAAAAAIABQMAAAAAAAABAJABAAAAAExQAAAAAAAAAAAAAAAAAAAAAAEAAAAAAAAADs28zAAAAAAAAAAAAAAAAAAAAAAAABIAYwBvAG8AcwAtAGYAbwBuAHQAAAAOAFIAZQBnAHUAbABhAHIAAAAWAFYAZQByAHMAaQBvAG4AIAAxAC4AMAAAABIAYwBvAG8AcwAtAGYAbwBuAHQAAAAAAAABAAAACwCAAAMAMEdTVUKw/rPtAAABOAAAAEJPUy8yPfNJ8gAAAXwAAABWY21hcLhRx2sAAAHsAAABuGdseWZ8UhIKAAADtAAAAuxoZWFkFoRnWAAAAOAAAAA2aGhlYQfeA4cAAAC8AAAAJGhtdHgYAAAAAAAB1AAAABhsb2NhAq4B3AAAA6QAAAAObWF4cAEVAEAAAAEYAAAAIG5hbWVe8B/IAAAGoAAAAnlwb3N0jBhM0AAACRwAAABsAAEAAAOA/4AAXAQAAAAAAAQAAAEAAAAAAAAAAAAAAAAAAAAGAAEAAAABAADMvM0OXw889QALBAAAAAAA2bKRrgAAAADZspGuAAD/vwQAA0AAAAAIAAIAAAAAAAAAAQAAAAYANAAFAAAAAAACAAAACgAKAAAA/wAAAAAAAAABAAAACgAeACwAAURGTFQACAAEAAAAAAAAAAEAAAABbGlnYQAIAAAAAQAAAAEABAAEAAAAAQAIAAEABgAAAAEAAAAAAAEEAAGQAAUACAKJAswAAACPAokCzAAAAesAMgEIAAACAAUDAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFBmRWQAQOd96AADgP+AAFwDgACAAAAAAQAAAAAAAAQAAAAEAAAABAAAAAQAAAAEAAAABAAAAAAAAAUAAAADAAAALAAAAAQAAAFsAAEAAAAAAGYAAwABAAAALAADAAoAAAFsAAQAOgAAAAgACAACAADnf+eH6AD//wAA533nh+gA//8AAAAAAAAAAQAIAAwADAAAAAEAAgADAAQABQAAAQYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADAAAAAAATAAAAAAAAAAFAADnfQAA530AAAABAADnfgAA534AAAACAADnfwAA538AAAADAADnhwAA54cAAAAEAADoAAAA6AAAAAAFAAAAAABKAJ4A7gE+AXYAAAADAAD/wAPAA0AAEgAeACoAAAEjIg8BJyYnIyIGHwEWMjcTNiYDDgEHHgEXPgE3LgEDLgEnPgE3HgEXDgECuy8QCp1HChAvBQQDfAofC9IDBMC+/QUF/b6+/QUF/b6e0gQE0p6e0gQE0gIfDdpjDAEJBK0NDQEkBQgBIQX9vr79BQX9vr79/NEE0p6e0gQE0p6e0gAAAwAA/78DwAM/ABsAJwAzAAABNCsBBycjBhUUHwEHBhUUFzM3FzM2NTQvATc2Aw4BBx4BFz4BNy4BAy4BJz4BNx4BFw4BAq0IQmNjQggCgoICCEJjY0IIAoGCAa2+/QUF/b6+/QUF/b6e0gQE0p6e0gQE0gIdCHd3AQcDApubAgMHAXd3AQcDApubAgElBf2+vv0FBf2+vv380QTSnp7SBATSnp7SAAAABQAA/8ADwANAAAsAFwAYACEALQAAAQ4BBx4BFz4BNy4BAy4BJz4BNx4BFw4BAyMeATI2NCYiBhcjBgcRFhczNjcRJgIAvv0FBf2+vv0FBf2+ntIEBNKentIEBNKeMAEbKBsbKBtHMAcBAQcwBwEBA0AF/b6+/QUF/b6+/fzRBNKentIEBNKentICIBQbGygbG4QBB/7wBwEBBwEQBwAAAAUAAP/AA8ADQAALABcAGAAhAC0AAAEOAQceARc+ATcuAQMuASc+ATceARcOAScjHgEyNjQmIgY3MzY3ESYnIwYHERYCAL79BQX9vr79BQX9vp7SBATSnp7SBATSnjABGygbGygbFzAHAQEHMAcBAQNABf2+vv0FBf2+vv380QTSnp7SBATSnp7SwBQbGygbG1wBBwEQBwEBB/7wBwAAAAABAAAAAAM+AsYAHwAACQE2JisBIgcLASYrASIGFwkBBhY7ATI3GwEWOwEyNicCNAEGAwQFUAcF2NkFB1AFBAMBBv76AwQFUAcF2dgFB1AFBAMBgAE5BAkG/v4BAgYJBP7H/scECQYBAv7+BgkEAAAAAAASAN4AAQAAAAAAAAAVAAAAAQAAAAAAAQAJABUAAQAAAAAAAgAHAB4AAQAAAAAAAwAJACUAAQAAAAAABAAJAC4AAQAAAAAABQALADcAAQAAAAAABgAJAEIAAQAAAAAACgArAEsAAQAAAAAACwATAHYAAwABBAkAAAAqAIkAAwABBAkAAQASALMAAwABBAkAAgAOAMUAAwABBAkAAwASANMAAwABBAkABAASAOUAAwABBAkABQAWAPcAAwABBAkABgASAQ0AAwABBAkACgBWAR8AAwABBAkACwAmAXUKQ3JlYXRlZCBieSBpY29uZm9udApjb29zLWZvbnRSZWd1bGFyY29vcy1mb250Y29vcy1mb250VmVyc2lvbiAxLjBjb29zLWZvbnRHZW5lcmF0ZWQgYnkgc3ZnMnR0ZiBmcm9tIEZvbnRlbGxvIHByb2plY3QuaHR0cDovL2ZvbnRlbGxvLmNvbQAKAEMAcgBlAGEAdABlAGQAIABiAHkAIABpAGMAbwBuAGYAbwBuAHQACgBjAG8AbwBzAC0AZgBvAG4AdABSAGUAZwB1AGwAYQByAGMAbwBvAHMALQBmAG8AbgB0AGMAbwBvAHMALQBmAG8AbgB0AFYAZQByAHMAaQBvAG4AIAAxAC4AMABjAG8AbwBzAC0AZgBvAG4AdABHAGUAbgBlAHIAYQB0AGUAZAAgAGIAeQAgAHMAdgBnADIAdAB0AGYAIABmAHIAbwBtACAARgBvAG4AdABlAGwAbABvACAAcAByAG8AagBlAGMAdAAuAGgAdAB0AHAAOgAvAC8AZgBvAG4AdABlAGwAbABvAC4AYwBvAG0AAAAAAgAAAAAAAAAKAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGAQIBAwEEAQUBBgEHAAxjaGVjay1jaXJjbGUMY2xvc2UtY2lyY2xlC2luZm8tY2lyY2xlDndhcm5pbmctY2lyY2xlBWNsb3NlAAA=";
	let woffBase64 = "d09GRgABAAAAAAWYAAsAAAAACYgAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAABHU1VCAAABCAAAADMAAABCsP6z7U9TLzIAAAE8AAAARAAAAFY980nyY21hcAAAAYAAAABwAAABuLhRx2tnbHlmAAAB8AAAAYIAAALsfFISCmhlYWQAAAN0AAAALwAAADYWhGdYaGhlYQAAA6QAAAAcAAAAJAfeA4dobXR4AAADwAAAAA4AAAAYGAAAAGxvY2EAAAPQAAAADgAAAA4CrgHcbWF4cAAAA+AAAAAfAAAAIAEVAEBuYW1lAAAEAAAAAU4AAAJ5XvAfyHBvc3QAAAVQAAAASAAAAGyMGEzQeJxjYGRgYOBikGPQYWB0cfMJYeBgYGGAAJAMY05meiJQDMoDyrGAaQ4gZoOIAgCKIwNPAHicY2BkYWCcwMDKwMHUyXSGgYGhH0IzvmYwYuRgYGBiYGVmwAoC0lxTGBye175gYG7438AQw9zA0AAUZgTJAQDnpAxjeJztkdENgzAMRJ8hqRBiFCZgBuboV1cAPjNqsgWc43aLnvWis6Mo0hnIwChWkcA+GK63ptbnI3OfJzb1k2qAetXSuG+54+e6TPeLyt2gt8l/sBd/Lf3cv1325ALPuZ5BZBv4XmoJfDeNgPwACOIbJnicldJBTsJAFAbg98+0MxQEQZGaNFKl2hqNQgDbGBM1bthyAHccgo0LJd6CyA04gQmykrWs1FO4NlKdFmXXqMnkn/cmb/HNZIgTfY75mJ/TCpVpjwhOZRme6zkVYaNUD1Z9l+cgyzBPERyCH8JThWpzYPfVfPqulc5XdY1fpe3MlGvj0UzXZ6N5DqaaNh3Eyezsa2cJhjbMZrGtp7C1mBrNPp5+xlQSRaYHZTojizxqKFNzH9JzxFrRhlRpNgKz4R81qwj8RNwwddHpXKRYr8e+q5sehkm8jVS3C8lZv8+4xKLGTjKT9MXbZcikddqiA2VN8HCnjLrfdCvCdIQslNQFgoLLKAE0qMHatdRq1SQgo+DnSRa2WYxGrVvI8C0aR17+1+ctfEEs82Ll7z7zD77xnHcZwxAjFY8QBT9lj2QTGfDdfVRkBtEmTAOidIJ6YCHafI81Ibimt6X+/KLLtvpxEOH7/OTl+fvkGseaIcIQTBhaOAknqgMLQ9URfQFCZr1ZAAB4nGNgZGBgAOIze7Z/iue3+crAzcIAAjc3TVyHoP/vZ2FgdgByORiYQKIAeRsMhQB4nGNgZGBgbvjfwBDDwgACQJKRARWwAQBHDAJveJxjYWBgYMGCAQFoABkAAAAAAAAASgCeAO4BPgF2AAB4nGNgZGBgYGMwYWBlAAEmIOYCQgaG/2A+AwAOcwFWAHicbZG9TgJBFIXP8mdYogVGS51GCw3LT2NCKQkUdhb0sMwuS5YdMjuQ8Ag+j8/gE9jb+Qy2HpYrBbKbufnuuedMbjIAmviCh/13xbNnD3V2ey7hDDfCZep3whVyIFxFA0/CNerPwj4e8SLcwCU2vMGr1Nk94E3Y4w7vwiVc4EO4TP1TuEL+Fq7iGj/CNTS9c2EfY+9WuIF7b+0PrJ44PVPTrUpCk0Umc35oTN7a0auO1+nEHvoDjLXNE5OpbtA5aCOdaft3V76Je85FKrJmqYac6jQ1amXNQocumDu36rfbkehBaJZcbQALjQkc6wwKU2xZE4QwyBAV1dG36w1ytA7aKxMx1kiZtifm/5UxE5ZKUvQKXT5T54RvRF9WeI/3yvlQMXpUHd2KxzKzJA0lq7lPSlZYFbMFlZB6gHmRWqGPNv/oyB8Ueyx/AXvKcO4AAHicY2BigAAuBuyAjZGJkZmRhZGVkY2RnYEnOSM1OVs3ObMoOSeVJzknvzgVyuHOzEvLh7L5yhOL8jLz0qFcVrA6BgYAtAUVsg==";
	let ttfBase64 = "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";
	let svgBase64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBzdGFuZGFsb25lPSJubyI/Pgo8IURPQ1RZUEUgc3ZnIFBVQkxJQyAiLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4iICJodHRwOi8vd3d3LnczLm9yZy9HcmFwaGljcy9TVkcvMS4xL0RURC9zdmcxMS5kdGQiID4KPCEtLQoyMDEzLTktMzA6IENyZWF0ZWQuCi0tPgo8c3ZnPgo8bWV0YWRhdGE+CkNyZWF0ZWQgYnkgaWNvbmZvbnQKPC9tZXRhZGF0YT4KPGRlZnM+Cgo8Zm9udCBpZD0iY29vcy1mb250IiBob3Jpei1hZHYteD0iMTAyNCIgPgogIDxmb250LWZhY2UKICAgIGZvbnQtZmFtaWx5PSJjb29zLWZvbnQiCiAgICBmb250LXdlaWdodD0iNTAwIgogICAgZm9udC1zdHJldGNoPSJub3JtYWwiCiAgICB1bml0cy1wZXItZW09IjEwMjQiCiAgICBhc2NlbnQ9Ijg5NiIKICAgIGRlc2NlbnQ9Ii0xMjgiCiAgLz4KICAgIDxtaXNzaW5nLWdseXBoIC8+CiAgICAKICAgIDxnbHlwaCBnbHlwaC1uYW1lPSJjaGVjay1jaXJjbGUiIHVuaWNvZGU9IiYjNTkyNjE7IiBkPSJNNjk5IDU0M2gtNDYuOWMtMTAuMiAwLTE5LjktNC45LTI1LjktMTMuM0w0NjkgMzExLjcwMDAwMDAwMDAwMDA1bC03MS4yIDk4LjhjLTYgOC4zLTE1LjYgMTMuMy0yNS45IDEzLjNIMzI1Yy02LjUgMC0xMC4zLTcuNC02LjUtMTIuN2wxMjQuNi0xNzIuOGMxMi43LTE3LjcgMzktMTcuNyA1MS43IDBsMjEwLjYgMjkyYzMuOSA1LjMgMC4xIDEyLjctNi40IDEyLjd6TTUxMiA4MzJDMjY0LjYgODMyIDY0IDYzMS40IDY0IDM4NHMyMDAuNi00NDggNDQ4LTQ0OCA0NDggMjAwLjYgNDQ4IDQ0OFM3NTkuNCA4MzIgNTEyIDgzMnogbTAtODIwYy0yMDUuNCAwLTM3MiAxNjYuNi0zNzIgMzcyczE2Ni42IDM3MiAzNzIgMzcyIDM3Mi0xNjYuNiAzNzItMzcyLTE2Ni42LTM3Mi0zNzItMzcyeiIgIGhvcml6LWFkdi14PSIxMDI0IiAvPgoKICAgIAogICAgPGdseXBoIGdseXBoLW5hbWU9ImNsb3NlLWNpcmNsZSIgdW5pY29kZT0iJiM1OTI2MjsiIGQ9Ik02ODUuNCA1NDEuMmMwIDQuNC0zLjYgOC04IDhsLTY2LTAuM0w1MTIgNDMwLjRsLTk5LjMgMTE4LjQtNjYuMSAwLjNjLTQuNCAwLTgtMy41LTgtOCAwLTEuOSAwLjctMy43IDEuOS01LjJsMTMwLjEtMTU1TDM0MC41IDIyNmMtMS4yLTEuNS0xLjktMy4zLTEuOS01LjIgMC00LjQgMy42LTggOC04bDY2LjEgMC4zTDUxMiAzMzEuNmw5OS4zLTExOC40IDY2LTAuM2M0LjQgMCA4IDMuNSA4IDggMCAxLjktMC43IDMuNy0xLjkgNS4yTDU1My41IDM4MWwxMzAuMSAxNTVjMS4yIDEuNCAxLjggMy4zIDEuOCA1LjJ6TTUxMiA4MzFDMjY0LjYgODMxIDY0IDYzMC40IDY0IDM4M3MyMDAuNi00NDggNDQ4LTQ0OCA0NDggMjAwLjYgNDQ4IDQ0OFM3NTkuNCA4MzEgNTEyIDgzMXogbTAtODIwYy0yMDUuNCAwLTM3MiAxNjYuNi0zNzIgMzcyczE2Ni42IDM3MiAzNzIgMzcyIDM3Mi0xNjYuNiAzNzItMzcyLTE2Ni42LTM3Mi0zNzItMzcyeiIgIGhvcml6LWFkdi14PSIxMDI0IiAvPgoKICAgIAogICAgPGdseXBoIGdseXBoLW5hbWU9ImluZm8tY2lyY2xlIiB1bmljb2RlPSImIzU5MjYzOyIgZD0iTTUxMiA4MzJDMjY0LjYgODMyIDY0IDYzMS40IDY0IDM4NHMyMDAuNi00NDggNDQ4LTQ0OCA0NDggMjAwLjYgNDQ4IDQ0OFM3NTkuNCA4MzIgNTEyIDgzMnogbTAtODIwYy0yMDUuNCAwLTM3MiAxNjYuNi0zNzIgMzcyczE2Ni42IDM3MiAzNzIgMzcyIDM3Mi0xNjYuNiAzNzItMzcyLTE2Ni42LTM3Mi0zNzItMzcyek01MTIgNTYwbS00OCAwYTQ4IDQ4IDAgMSAxIDk2IDAgNDggNDggMCAxIDEtOTYgMFpNNTM2IDQ0OGgtNDhjLTQuNCAwLTgtMy42LTgtOHYtMjcyYzAtNC40IDMuNi04IDgtOGg0OGM0LjQgMCA4IDMuNiA4IDhWNDQwYzAgNC40LTMuNiA4LTggOHoiICBob3Jpei1hZHYteD0iMTAyNCIgLz4KCiAgICAKICAgIDxnbHlwaCBnbHlwaC1uYW1lPSJ3YXJuaW5nLWNpcmNsZSIgdW5pY29kZT0iJiM1OTI3MTsiIGQ9Ik01MTIgODMyQzI2NC42IDgzMiA2NCA2MzEuNCA2NCAzODRzMjAwLjYtNDQ4IDQ0OC00NDggNDQ4IDIwMC42IDQ0OCA0NDhTNzU5LjQgODMyIDUxMiA4MzJ6IG0wLTgyMGMtMjA1LjQgMC0zNzIgMTY2LjYtMzcyIDM3MnMxNjYuNiAzNzIgMzcyIDM3MiAzNzItMTY2LjYgMzcyLTM3Mi0xNjYuNi0zNzItMzcyLTM3MnpNNTEyIDIwOG0tNDggMGE0OCA0OCAwIDEgMSA5NiAwIDQ4IDQ4IDAgMSAxLTk2IDBaTTQ4OCAzMjBoNDhjNC40IDAgOCAzLjYgOCA4VjYwMGMwIDQuNC0zLjYgOC04IDhoLTQ4Yy00LjQgMC04LTMuNi04LTh2LTI3MmMwLTQuNCAzLjYtOCA4LTh6IiAgaG9yaXotYWR2LXg9IjEwMjQiIC8+CgogICAgCiAgICA8Z2x5cGggZ2x5cGgtbmFtZT0iY2xvc2UiIHVuaWNvZGU9IiYjNTkzOTI7IiBkPSJNNTYzLjggMzg0bDI2Mi41IDMxMi45YzQuNCA1LjIgMC43IDEzLjEtNi4xIDEzLjFoLTc5LjhjLTQuNyAwLTkuMi0yLjEtMTIuMy01LjdMNTExLjYgNDQ2LjIgMjk1LjEgNzA0LjNjLTMgMy42LTcuNSA1LjctMTIuMyA1LjdIMjAzYy02LjggMC0xMC41LTcuOS02LjEtMTMuMUw0NTkuNCAzODQgMTk2LjkgNzEuMWMtNC40LTUuMi0wLjctMTMuMSA2LjEtMTMuMWg3OS44YzQuNyAwIDkuMiAyLjEgMTIuMyA1LjdsMjE2LjUgMjU4LjEgMjE2LjUtMjU4LjFjMy0zLjYgNy41LTUuNyAxMi4zLTUuN2g3OS44YzYuOCAwIDEwLjUgNy45IDYuMSAxMy4xTDU2My44IDM4NHoiICBob3Jpei1hZHYteD0iMTAyNCIgLz4KCiAgICAKCgogIDwvZm9udD4KPC9kZWZzPjwvc3ZnPgo=";
	let close_font = '\\e800';
	let check_circle_font = '\\e77d';
	let close_circle_font = '\\e77e';
	let info_circle_font = '\\e77f';
	let warning_circle_font = '\\e787';
	let fontCSS = `
	@font-face {
		font-family: "coos-icon-font";
		src: url(data:application/octet-stream;base64,` + eotBase64 + `); /* IE9 */
		src: url(data:application/octet-stream;base64,` + eotBase64 + `) format('embedded-opentype'), /* IE6-IE8 */
		url(data:application/octet-stream;base64,` + woffBase64 + `) format('woff'),
		url(data:application/octet-stream;base64,` + ttfBase64 + `) format('truetype'), /* chrome, firefox, opera, Safari, Android, iOS 4.2+ */
		url(data:application/octet-stream;base64,` + svgBase64 + `) format('svg'); /* iOS 4.1- */
	}
	`;
	let baseCSS = `
	.coos-disabled{cursor: no-drop;pointer-events: none;opacity: .65;filter: alpha(opacity = 65);}
	.coos-flex-right {flex: 0 0 auto;margin-left: auto;text-align: right;}
	.coos-pointer {cursor: pointer;}
	.coos-move {cursor: move;}
	.coos-relative {position: relative;}
	.coos-row {display: block;width: 100%;}
	.coos-row:after {content: "";display: table;clear: both;}
	.coos-page {display: block;}
	.coos-page:after {content: "";display: table;clear: both;}
	.coos-layout {display: block;padding: 10px;}
	.coos-layout:after {content: "";display: table;clear: both;}
	.coos-panel {display: block;border: 1px solid #ddd;}
	.coos-panel:after {content: "";display: table;clear: both;}
	.coos-panel .coos-panel-header {
	    color: #5a5a5a;
	    font-size: 15px;
	    padding: 5px 10px;
	    border-bottom: 1px solid #ddd;
    }
	.coos-panel .coos-panel-header .coos-panel-title {}
	.coos-panel .coos-panel-body {}
	.coos-panel .coos-panel-footer {
	    color: #5a5a5a;
	    font-size: 15px;
	    padding: 5px 10px;
	    border-top: 1px solid #ddd;
	}
	.coos-page,.coos-panel,.coos-panel-header,.coos-panel-body,.coos-panel-footer,.coos-btn,.coos-link,.coos-row,coos-layout {
		box-sizing:border-box;
		-moz-box-sizing:border-box; /* Firefox */
		-webkit-box-sizing:border-box; /* Safari */
	}
	
	/** font **/
	.font-transparent {color: transparent !important;}
	/** display **/
	.overflow-hidden{overflow: hidden !important;}
	.overflow-auto{overflow: auto !important;}
	/** display **/
	.display-none{display: none !important;}
	.display-block{display: block !important;}
	.display-inline-block{display: inline-block !important;}
	.display-table{display: table !important;}
	.display-table-cell{display: table-cell !important;}
	.display-table-row{display: table-row !important;}
	/** float **/
	.float-none{float: none !important;}
	.float-left{float: left !important;}
	.float-right{float: right !important;}
	/** clear **/
	.clear-none{clear: none !important;}
	.clear-both{clear: both !important;}
	.clear-left{clear: left !important;}
	.clear-right{clear: right !important;}
	/** text **/
	.text-center{text-align: center !important;}
	.text-left{text-align: left !important;}
	.text-right{text-align: right !important;}
	.text-justify{text-align: justify !important;}
	
	.text-pre{white-space: pre !important;}
	.text-pre-line{white-space: pre-line !important;}
	.text-pre-wrap{white-space: pre-wrap !important;}
	.text-nowrap{white-space: nowrap !important;}
	.text-normal{white-space: normal !important;}
	
	.text-lowercase{text-transform: lowercase !important;}
	.text-uppercase{text-transform: uppercase !important;}
	.text-capitalize{text-transform: capitalize !important;}
	
	.text-break-word{word-wrap: break-word !important;}
	
	.text-overline{text-decoration: overline !important;}
	.text-underline{text-decoration: underline !important;}
	.text-line-through{text-decoration: line-through !important;}
	/** border **/
	.bd, .bdt, .bdtb {border-top: 1px solid #ddd;}
	.bd-none, .bdt-none, .bdtb-none {border-top: none;}
	
	.bd, .bdb, .bdtb {border-bottom: 1px solid #ddd;}
	.bd-none, .bdb-none, .bdtb-none {border-bottom: none;}
	
	.bd, .bdl, .bdlr {border-left: 1px solid #ddd;}
	.bd-none, .bdl-none, .bdlr-none {border-left: none;}
	
	.bd, .bdr, .bdlr {border-right: 1px solid #ddd;}
	.bd-none, .bdr-none, .bdlr-none {border-right: none;}
	
	.bd-dashed, .bdt-dashed, .bdtb-dashed {border-top-style: dashed;}
	.bd-dashed, .bdb-dashed, .bdtb-dashed {border-bottom-style: dashed;}
	.bd-dashed, .bdl-dashed, .bdlr-dashed {border-left-style: dashed;}
	.bd-dashed, .bdr-dashed, .bdlr-dashed {border-right-style: dashed;}
	
	.bd-dotted, .bdt-dotted, .bdtb-dotted {border-top-style: dotted;}
	.bd-dotted, .bdb-dotted, .bdtb-dotted {border-bottom-style: dotted;}
	.bd-dotted, .bdl-dotted, .bdlr-dotted {border-left-style: dotted;}
	.bd-dotted, .bdr-dotted, .bdlr-dotted {border-right-style: dotted;}
	
	.bd-double, .bdt-double, .bdtb-double {border-top-style: double;}
	.bd-double, .bdb-double, .bdtb-double {border-bottom-style: double;}
	.bd-double, .bdl-double, .bdlr-double {border-left-style: double;}
	.bd-double, .bdr-double, .bdlr-double {border-right-style: double;}
	
	/** btn **/
	.coos-btn{
	  display: inline-block;
	  cursor: pointer;
	  position: relative;
	  outline: 0px;
	  white-space: nowrap;
	  text-decoration: none;
	  text-overflow: ellipsis;
	  font-weight: normal;
	  text-align: center;
	  -webkit-user-select: none;
	  -moz-user-select: none;
	  -ms-user-select: none;
	  user-select: none;
	  -webkit-touch-action: manipulation;
	  -moz-touch-action: manipulation;
	  -ms-touch-action: manipulation;
	  touch-action: manipulation;
	  -webkit-tap-highlight-color: transparent;
	  vertical-align: middle;
	  color: #9e9e9e;
	  border: 1px solid transparent;
	  border-color: #9e9e9e;
	}
	.coos-btn.coos-btn-block {
	  display: block;
	  padding-left: 0;
	  padding-right: 0;
	}
	.coos-btn.coos-active,.coos-btn.coos-hover{color: #ffffff;border-color: #9e9e9e;background-color: #9e9e9e;}
	@media (hover: hover) {
	    .coos-btn:hover {color: #ffffff;border-color: #9e9e9e;background-color: #9e9e9e;}
	}
	/** link **/
	.coos-link{
	  display: inline-block;
	  cursor: pointer;
	  position: relative;
	  outline: 0px;
	  white-space: nowrap;
	  text-decoration: none;
	  text-overflow: ellipsis;
	  font-weight: normal;
	  text-align: center;
	  -webkit-user-select: none;
	  -moz-user-select: none;
	  -ms-user-select: none;
	  user-select: none;
	  -webkit-touch-action: manipulation;
	  -moz-touch-action: manipulation;
	  -ms-touch-action: manipulation;
	  touch-action: manipulation;
	  -webkit-tap-highlight-color: transparent;
	  color: #9e9e9e;
	  border: 1px solid transparent;
	}
	.coos-link.coos-active,.coos-link.coos-hover {border-bottom-color: #9e9e9e;}
	@media (hover: hover) {
	    .coos-link:hover {border-bottom-color: #9e9e9e;}
	}
	.coos-mask{
		position: fixed;
	    left: 0;
	    top: 0;
	    width: 100%;
	    height: 100%;
	    opacity: .3;
	    background: #000;
		transform: scale(0);
	}
	.coos-mask.coos-show {
		transform: scale(1);
	}
	.coos-dialog-wrapper {
		position: fixed;
		left: 0px;
		right: 0px;
		top: 0px;
		bottom: 0px;
		transform: scale(0);
		text-align: center;
	}
	.coos-dialog-wrapper.coos-show {
		transform: scale(1);
	}
	.coos-dialog{
		display: inline-block;
	    background-color: #fff;
	    border-radius: 4px;
	    border: 1px solid #ebeef5;
	    font-size: 18px;
	    text-align: left;
	    backface-visibility: hidden;
		position: relative;
	    top: 15vh;
	    padding: 10px 0px;
	}
	.coos-dialog-middle .coos-dialog{
	    top: auto;
	}
	.coos-dialog-middle:after {
	    content: "";
	    display: inline-block;
	    height: 100%;
	    width: 0;
	    vertical-align: middle;
	}
	.coos-dialog-header{
	    padding: 10px 20px 10px;
	}
	.coos-dialog-title{
	    line-height: 24px;
	    font-size: 18px;
	    color: #303133;
	}
	.coos-dialog-close{
	    position: absolute;
	    top: 20px;
	    right: 20px;
	    padding: 0;
	    background: transparent;
	    border: none;
	    outline: none;
	    cursor: pointer;
	    font-size: 16px;
	}
	.coos-dialog-close:after{
		content: "` + close_font + `";
		font-family: "coos-icon-font" !important;
		font-size: 16px;
		font-style: normal;
	}
	.coos-dialog-footer{
	    text-align: right;
	    padding:0px 10px;
	}
	.coos-dialog-footer .coos-btn{
		margin-left: 10px;
	}
	.coos-message-box{
		position: fixed;
	    left: 0;
	    top: 0;
	    width: 100%;
	    height: 100%;
		pointer-events: none;
	    text-align: center;
	}
	.coos-message{
		pointer-events: all;
	    width: 400px;
	    position: relative;
	    margin: 0px auto;
	    display: block;
	    padding: 15px 20px;
	    text-align: left;
	    box-sizing: border-box;
	    border-radius: 4px;
	    margin-top: -50px;
	    transition: margin-top .3s;
	    background-color: #CFD8DC;
	    color: #607D8B;
	}
	.coos-message.coos-show{
	    margin-top: 30px;
	}
	.coos-message .coos-message-icon{
	    margin-right: 10px;
	}
	.coos-message .coos-message-close{
	    margin-left: 10px;
	    float: right;
	    cursor: pointer;
	}
	.coos-message .coos-message-close:after{
		content: "` + close_font + `";
		font-family: "coos-icon-font" !important;
		font-size: 16px;
		font-style: normal;
	}
	.coos-message .coos-message-icon:after{
		content: "";
		font-family: "coos-icon-font" !important;
		font-size: 16px;
		font-style: normal;
	}
	.coos-message-info{
	    background-color: #CFD8DC;
	    color: #607D8B;
	    background-color: #607D8B;
	    color: #FFFFFF;
	}
	.coos-message-info .coos-message-icon:after{
		content: "` + info_circle_font + `";
	}
	.coos-message-warn{
	    background-color: #FFE0B2;
	    color: #FF9800;
	    background-color: #FF9800;
	    color: #FFFFFF;
	}
	.coos-message-warn .coos-message-icon:after{
		content: "` + warning_circle_font + `";
	}
	.coos-message-success{
	    background-color: #C8E6C9;
	    color: #4CAF50;
	    background-color: #4CAF50;
	    color: #FFFFFF;
	}
	.coos-message-success .coos-message-icon:after{
		content: "` + check_circle_font + `";
	}
	.coos-message-error{
	    background-color: #FFCDD2;
	    color: #F44336;
	    background-color: #F44336;
	    color: #FFFFFF;
	}
	.coos-message-error .coos-message-icon:after{
		content: "` + close_circle_font + `";
	}
	`;
	let distanceCSS = `
	/** 字体大小 **/
	.ft-key,.hover-ft-key:hover{font-size: "value" !important;}
	/** 宽度 **/
	.wd-key,.hover-wd-key:hover{width: "value" !important;}
	/** 高度 **/
	.hg-key,.hover-hg-key:hover{height: "value" !important;}
	/** 圆角 **/
	.rd-key,.hover-rd-key:hover,.rdt-key,.hover-rdt-key:hover,.rdtl-key,.hover-rdtl-key:hover{border-top-left-radius: "value" !important;}
	.rd-key,.hover-rd-key:hover,.rdt-key,.hover-rdt-key:hover,.rdtr-key,.hover-rdtr-key:hover{border-top-right-radius: "value" !important;}
	.rd-key,.hover-rd-key:hover,.rdb-key,.hover-rdb-key:hover,.rdbl-key,.hover-rdbl-key:hover{border-bottom-left-radius: "value" !important;}
	.rd-key,.hover-rd-key:hover,.rdb-key,.hover-rdb-key:hover,.rdbr-key,.hover-rdbr-key:hover{border-bottom-right-radius: "value" !important;}
	/** 边框 **/
	.bd-key,.bdl-key,.bdlr-key,.hover-bd-key:hover,.hover-bdl-key:hover,.hover-bdlr-key:hover{border-left-style: solid;border-left-width: "value" !important;}
	.bd-key,.bdr-key,.bdlr-key,.hover-bd-key:hover,.hover-bdr-key:hover,.hover-bdlr-key:hover{border-right-style: solid;border-right-width: "value" !important;}
	.bd-key,.bdt-key,.bdtb-key,.hover-bd-key:hover,.hover-bdt-key:hover,.hover-bdtb-key:hover{border-top-style: solid;border-top-width: "value" !important;}
	.bd-key,.bdb-key,.bdtb-key,.hover-bd-key:hover,.hover-bdb-key:hover,.hover-bdtb-key:hover{border-bottom-style: solid;border-bottom-width: "value" !important;}
	/** 内边距 **/
	.pd-key,.pdl-key,.pdlr-key,.hover-pd-key:hover,.hover-pdl-key:hover,.hover-pdlr-key:hover{padding-left: "value" !important;}
	.pd-key,.pdr-key,.pdlr-key,.hover-pd-key:hover,.hover-pdr-key:hover,.hover-pdlr-key:hover{padding-right: "value" !important;}
	.pd-key,.pdt-key,.pdtb-key,.hover-pd-key:hover,.hover-pdt-key:hover,.hover-pdtb-key:hover{padding-top: "value" !important;}
	.pd-key,.pdb-key,.pdtb-key,.hover-pd-key:hover,.hover-pdb-key:hover,.hover-pdtb-key:hover{padding-bottom: "value" !important;}
	/** 外边距 **/
	.mg-key,.mgl-key,.mglr-key,.hover-mg-key:hover,.hover-mgl-key:hover,.hover-mglr-key:hover{margin-left: "value" !important;}
	.mg-key,.mgr-key,.mglr-key,.hover-mg-key:hover,.hover-mgr-key:hover,.hover-mglr-key:hover{margin-right: "value" !important;}
	.mg-key,.mgt-key,.mgtb-key,.hover-mg-key:hover,.hover-mgt-key:hover,.hover-mgtb-key:hover{margin-top: "value" !important;}
	.mg-key,.mgb-key,.mgtb-key,.hover-mg-key:hover,.hover-mgb-key:hover,.hover-mgtb-key:hover{margin-bottom: "value" !important;}

	.mg--key,.mgl--key,.mglr--key,.hover-mg--key:hover,.hover-mgl--key:hover,.hover-mglr--key:hover{margin-left: -"value" !important;}
	.mg--key,.mgr--key,.mglr--key,.hover-mg--key:hover,.hover-mgr--key:hover,.hover-mglr--key:hover{margin-right: -"value" !important;}
	.mg--key,.mgt--key,.mgtb--key,.hover-mg--key:hover,.hover-mgt--key:hover,.hover-mgtb--key:hover{margin-top: -"value" !important;}
	.mg--key,.mgb--key,.mgtb--key,.hover-mg--key:hover,.hover-mgb--key:hover,.hover-mgtb--key:hover{margin-bottom: -"value" !important;}
	`;
	let colCSS = `
	.col-key,.coos-window-xs .col-xs-key,.coos-window-sm .col-sm-key,.coos-window-md .col-md-key,.coos-window-lg .col-lg-key{float: left;width: "value" !important;}
	.offset-key,.coos-window-xs .offset-xs-key,.coos-window-sm .offset-sm-key,.coos-window-md .offset-md-key,.coos-window-lg .offset-lg-key{margin-left: "value" !important;}
    `;
	let sizeCSS = `
	.font-key{font-size: "font-size";}
	.coos-btn.coos-btn-key{line-height: "line-height";padding: "padding";font-size: "font-size";}
	.coos-btn.coos-btn-key>*{height: "line-height";display: inline-block;vertical-align: bottom;}
	.coos-btn.coos-btn-key>img{border-radius: 100px;}
	`;
	let colorCSS = `
	/** color **/
	.color-key,.active-color-key.coos-active,.coos-window-xs .color-xs-key,.coos-window-sm .color-sm-key,.coos-window-md .color-md-key,.coos-window-lg .color-lg-key{color: "color" !important;}
	@media (hover: hover) {
	    .active-color-key:hover{color: "color" !important;}
	    .hover-color-key:hover{color: "color" !important;}
	}
	/** background color **/
	.bg-key,.active-bg-key.coos-active,.coos-window-xs .bg-xs-key,.coos-window-sm .bg-sm-key,.coos-window-md .bg-md-key,.coos-window-lg .bg-lg-key{color: "whiteColor";background-color: "color" !important;}
	@media (hover: hover) {
	    .active-bg-key:hover{ color: "whiteColor";background-color: "color" !important;}
	    .hover-bg-key:hover{ color: "whiteColor";background-color: "color" !important;}
	}
	/** border color **/
	.bd-key,.bdl-key,.bdlr-key,.hover-bd-key:hover,.hover-bdl-key:hover,.hover-bdlr-key:hover{border-left-color: "color";}
	.bd-key,.bdr-key,.bdlr-key,.hover-bd-key:hover,.hover-bdr-key:hover,.hover-bdlr-key:hover{border-right-color: "color";}
	.bd-key,.bdt-key,.bdtb-key,.hover-bd-key:hover,.hover-bdt-key:hover,.hover-bdtb-key:hover{border-top-color: "color";}
	.bd-key,.bdb-key,.bdtb-key,.hover-bd-key:hover,.hover-bdb-key:hover,.hover-bdtb-key:hover{border-bottom-color: "color";}
	`;
	let componentCSS = `
	/** btn color **/
	.coos-btn.color-key{
	    color: "color" !important;
	    border-color: "color" !important;
	    background-color: #FFFFFF !important;
	}
	
	/** btn background color **/
	.coos-btn.bg-key{
	    color: #FFFFFF !important;
	    border-color: "color" !important;
	    background-color: "color" !important;
	}
	`;
	let componentActiveCSS = `
	/** btn color **/
	.coos-btn.color-key.coos-active,
	.coos-btn.color-key.coos-hover{
	    color: #FFFFFF !important;
	    border-color: "color" !important;
	    background-color: "color" !important;
	}
	@media (hover: hover) {
	    .coos-btn.color-key:hover{
	        color: #FFFFFF !important;
	        border-color: "color" !important;
	        background-color: "color" !important;
	    }
	}
	
	/** btn background color **/
	.coos-btn.bg-key.coos-active,
	.coos-btn.bg-key.coos-hover{
	    border-color: "activeColor" !important;
	    background-color: "activeColor" !important;
	}
	@media (hover: hover) {
	    .coos-btn.bg-key:hover{
	        border-color: "activeColor" !important;
	        background-color: "activeColor" !important;
	    }
	}
	
	.coos-btn.active-color-key.coos-active,
	.coos-btn.active-color-key.coos-hover{
	    background-color: #FFFFFF !important;
	    border-color: "color" !important;
	    color: "color" !important;
	}
	
	@media (hover: hover) {
	    .coos-btn.active-color-key:hover{
	        background-color: #FFFFFF !important;
	        border-color: "color" !important;
	        color: "color" !important;
	    }
	}
	
	.coos-btn.active-bg-key.coos-active,
	.coos-btn.active-bg-key.coos-hover{
	    color: #FFFFFF !important;
	    background-color: "color" !important;
	    border-color: "color" !important;
	}
	
	@media (hover: hover) {
	    .coos-btn.active-bg-key:hover{
	        color: #FFFFFF !important;
	        background-color: "color" !important;
	        border-color: "color" !important;
	    }
	}
	
	/** link color **/
	.coos-link.color-key.coos-active,
	.coos-link.color-key.coos-hover{
	    color: "color";
	    background-color: transparent;
	    border-bottom-color: "color";
	}
	
	@media (hover: hover) {
	    .coos-link.color-key:hover{
	        color: "color";
	        background-color: transparent;
	        border-bottom-color: "color";
	    }
	}
	
	.coos-link.active-color-key.coos-active,
	.coos-link.active-color-key.coos-hover{
	    color: "color" !important;
	    background-color: transparent !important;
	    border-bottom-color: "color" !important;
	}
	
	
	@media (hover: hover) {
	    .coos-link.active-color-key:hover{
	        color: "color" !important;
	        background-color: transparent !important;
	        border-bottom-color: "color" !important;
	    }
	}
	`;

	style.getBaseCSS = function() {
		var css = '';
		css += fontCSS;
		css += baseCSS;
		css += '\n';
		return css;
	};
	style.getSizesCSS = function() {
		var css = '\n';
		config.sizes.forEach((one, index) => {
			let key = one.name;
			let coosKey = '';
			let sizeKey = '';
			if (co.isEmpty(key)) {
				key = '';
				coosKey = '';
				sizeKey = '';
			} else {
				coosKey = 'coos-window-' + key + ' ';
				sizeKey = '.coos-size-' + key + '';
				key = '-' + key;
			}
			var text = sizeCSS;
			text = co.replaceAll(text, 'coos-key ', coosKey);

			text = co.replaceAll(text, '.coos-size-key', sizeKey);
			text = co.replaceAll(text, '-key', key);
			for (let n in one) {
				text = co.replaceAll(text, '"' + n + '"', one[n]);
			}
			css += text;
		});
		return css;
	};
	style.getDistancesCSS = function() {
		var css = '\n';
		config.distances.forEach((distance, index) => {
			let key = distance;
			var text = distanceCSS;
			text = co.replaceAll(text, '-key', '-' + key + '');
			text = co.replaceAll(text, '"value"', distance + 'px');
			css += text;
			css += '\n';

		});
		return css;
	};
	style.getColsCSS = function() {
		var css = '\n';
		config.cols.forEach((col, index) => {
			var key = col.value;
			key = key.replace(".", "-");

			var text = colCSS;
			text = co.replaceAll(text, '-key', '-' + key + '');
			text = co.replaceAll(text, '"value"', col.width);
			css += text;
			css += '\n';

		});
		return css;
	};

	style.getColorsCSS = function() {
		var css = '';
		config.colors.forEach((one, index) => {
			css += style.getColorCSS(one);
		});
		return css;
	};
	style.getColorCSS = function(one) {
		var css = '';
		var name = one.value;
		var text = one.text;
		var colors = one.colors;
		colors.forEach((color, index) => {
			var key = name;
			if (index > 0) {
				key = name + '-' + index;
			}
			var whiteColor = "";
			if (name != 'white') {
				whiteColor = "#FFFFFF";
			}
			let colorText = colorCSS;
			colorText = co.replaceAll(colorText, '-key', '-' + key + '');
			colorText = co.replaceAll(colorText, '"whiteColor"', whiteColor);
			colorText = co.replaceAll(colorText, '"color"', color);
			css += colorText;
		});
		return css;
	};

	style.getComponentsCSS = function() {
		var css = '';
		config.colors.forEach((one, index) => {
			css += style.getComponentCSS(one);
		});
		return css;
	};
	style.getComponentCSS = function(one) {
		var css = '';
		var name = one.value;
		var text = one.text;
		var colors = one.colors;
		colors.forEach((color, index) => {
			var key = name;
			if (index > 0) {
				key = name + '-' + index;
				return;
			}
			var whiteColor = "";
			if (name != 'white') {
				whiteColor = "#FFFFFF";
			}
			var activeColor = color;
			if (colors.length >= 10) {
				if (index == 0 || index == 5) {
					activeColor = colors[8];
				} else {
					activeColor = colors[0];
				}
			}

			var text = componentCSS;
			text = co.replaceAll(text, '-key', '-' + key + '');
			text = co.replaceAll(text, '"whiteColor"', whiteColor);
			text = co.replaceAll(text, '"activeColor"', activeColor);
			text = co.replaceAll(text, '"color"', color);
			css += text;
			css += '\n';
		});
		css += style.getComponentActiveCSS(one);
		return css;
	};
	style.getComponentActiveCSS = function(one) {
		var css = '';
		var name = one.value;
		var text = one.text;
		var colors = one.colors;
		colors.forEach((color, index) => {
			var key = name;
			if (index > 0) {
				key = name + '-' + index;
				return;
			}
			var whiteColor = "";
			if (name != 'white') {
				whiteColor = "#FFFFFF";
			}
			var activeColor = color;
			if (colors.length >= 10) {
				if (index == 0 || index == 5) {
					activeColor = colors[8];
				} else {
					activeColor = colors[0];
				}
			}

			var text = componentActiveCSS;
			text = co.replaceAll(text, '-key', '-' + key + '');
			text = co.replaceAll(text, '"whiteColor"', whiteColor);
			text = co.replaceAll(text, '"activeColor"', activeColor);
			text = co.replaceAll(text, '"color"', color);
			css += text;
			css += '\n';
		});
		return css;
	};
})();
(function() {
	let style = co.style;
	style.initStyle = function(arg) {

		if (window._COOS_STYLE_DOCUMENT == null) {
			let heads = document.getElementsByTagName('head');
			if (heads != null && heads.length > 0) {
				let head = heads[0];
				let dom = document.createElement('style');
				dom.setAttribute('id', 'COOS-STYLE');
				dom.setAttribute('type', 'text/css');
				if (head.firstChild) {
					head.insertBefore(dom, head.firstChild);
				} else {
					head.appendChild(dom);
				}
				window._COOS_STYLE_DOCUMENT = dom;
			}

			if (window._COOS_STYLE_DOCUMENT != null) {
				window._COOS_STYLE_BASE_NODE = document.createTextNode("");
				window._COOS_STYLE_DOCUMENT.appendChild(window._COOS_STYLE_BASE_NODE);

				window._COOS_STYLE_COMPONENT_NODE = document.createTextNode("");
				window._COOS_STYLE_DOCUMENT.appendChild(window._COOS_STYLE_COMPONENT_NODE);
			}
		}
	};
	style.appendBase = function(css) {
		style.initStyle();
		if (window._COOS_STYLE_BASE_NODE != null && css) {
			window._COOS_STYLE_BASE_NODE.appendData(css);
		}
	};
	style.appendComponent = function(css) {
		style.initStyle();
		if (window._COOS_STYLE_COMPONENT_NODE != null && css) {
			window._COOS_STYLE_COMPONENT_NODE.appendData(css);
		}
	};

	style.init = function() {
		var css = '';
		css += style.getBaseCSS();
		css += style.getSizesCSS();
		css += style.getDistancesCSS();
		css += style.getColsCSS();
		css += style.getColorsCSS();
		style.appendBase(css);
		css = style.getComponentsCSS();
		style.appendComponent(css);
	};

	style.writeColorCSS = function(one) {
		var css = style.getColorCSS(one);
		style.appendBase(css);
		css = style.getComponentCSS(one);
		style.appendComponent(css);
	};
	var map = {};
	style.addColor = function(main) {
		if (map[main]) {
			return map[main];
		}
		if (co.isEmpty(main) || main.indexOf('#') != 0) {
			return;
		}
		var one = {};
		var color = new co.Color({
			main : main
		});
		one.colors = color.getColors();
		one.text = main;
		one.value = main.substring(1);
		style.writeColorCSS(one);
		map[main] = one;
		return one;
	};

	style.getColorClass = function(color) {
		if (co.isEmpty(color) || color.indexOf('{') >= 0) {
			return "";
		}
		if (color.indexOf('#') == 0) {
			style.addColor(color);
			color = color.substring('1');
		}
		return 'color-' + color;
	};
	style.getBGColorClass = function(color) {
		if (co.isEmpty(color) || color.indexOf('{') >= 0) {
			return "";
		}
		if (color.indexOf('#') == 0) {
			style.addColor(color);
			color = color.substring('1');
		}
		return 'bg-' + color;
	};

	style.getActiveColorClass = function(color) {
		if (co.isEmpty(color) || color.indexOf('{') >= 0) {
			return "";
		}
		if (color.indexOf('#') == 0) {
			style.addColor(color);
			color = color.substring('1');
		}
		return 'active-color-' + color;
	};
	style.getActiveBGColorClass = function(color) {
		if (co.isEmpty(color) || color.indexOf('{') >= 0) {
			return "";
		}
		if (color.indexOf('#') == 0) {
			style.addColor(color);
			color = color.substring('1');
		}
		return 'active-bg-' + color;
	};
	style.init();
})();

})(window);