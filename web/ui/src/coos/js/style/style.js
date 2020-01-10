(function() {
	let style = co.style;
	let config = style.config;
	let eotBase64 = "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";
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