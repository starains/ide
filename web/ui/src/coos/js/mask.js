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