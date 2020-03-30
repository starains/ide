(function() {
	var AppEditor = coos.createClass(coos.Editor);
	coos.Editor.App = AppEditor;

	AppEditor.prototype.isYaml = function() {
		return true;
	};

	AppEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-app pd-20"></div>');
		$design.append($box);

		var model = this.model;
		model.error = model.error || {};

		let html = `
		<div class="coos-row ">
			<h3 class="pdb-10 color-orange">应用信息配置</h3>
			<el-form :model="form" status-icon :rules="rules" ref="form" label-width="150px" size="mini">
				<el-form-item class label="名称" prop="name">
				  <el-input type="text" v-model="form.name" autocomplete="off" @change="change($event,'name')"></el-input>
				</el-form-item>
				<el-form-item class label="AES密钥" prop="aeskey">
				  <el-input type="text" v-model="form.aeskey" autocomplete="off" @change="change($event,'aeskey')"></el-input>
				</el-form-item>
			</el-form>
		</div>
		`;

		$box.append(html);
		let form = {};
		Object.assign(form, model);
		for (let key in model.error) {
			form['error_' + key] = model.error[key];
		}
		new Vue({
			el : $box[0],
			data () {
				return {
					form : form,
					rules : {}
				}
			},
			methods : {
				change (value, name) {
					if (!coos.isEmpty(name)) {
						that.recordHistory();
						if (name.startsWith("error_")) {
							name = name.replace("error_", "");
							model.error[name] = value;
						} else {
							model[name] = value;
						}
						that.changeModel(false);
					}
				}
			}
		});

	};
})();