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
				<el-form-item class label="首页地址" prop="indexurl">
				  <el-input type="text" v-model="form.indexurl" autocomplete="off" @change="change($event,'indexurl')"></el-input>
				</el-form-item>
				<el-form-item class label="登录地址" prop="loginurl">
				  <el-input type="text" v-model="form.loginurl" autocomplete="off" @change="change($event,'loginurl')"></el-input>
				</el-form-item>
				<el-form-item class label="登录后跳转地址" prop="afterlogintourl">
				  <el-input type="text" v-model="form.afterlogintourl" autocomplete="off" @change="change($event,'afterlogintourl')"></el-input>
				</el-form-item>
				<el-form-item class label="退出后跳转" prop="afterlogouttourl">
				  <el-input type="text" v-model="form.afterlogouttourl" autocomplete="off" @change="change($event,'afterlogouttourl')"></el-input>
				</el-form-item>
				
				<el-form-item class label="忽略地址" prop="ignoreurl">
				  <el-input type="text" v-model="form.ignoreurl" autocomplete="off" @change="change($event,'ignoreurl')"></el-input>
				  <span>检查到匹配地址将直接放行，多个地址以“;”隔开如：/res/*;/abc/*。</span>
				</el-form-item>
				<el-form-item class label="登录忽略地址" prop="loginignoreurl">
				  <el-input type="text" v-model="form.loginignoreurl" autocomplete="off" @change="change($event,'loginignoreurl')"></el-input>
				  <span>检查到匹配地址将不检测是否登录，多个地址以“;”隔开如：/res/*;/abc/*。</span>
				</el-form-item>
				<el-form-item class label="必须登录" prop="requiredlogin">
				  <el-switch v-model="form.requiredlogin" @change="change($event,'requiredlogin')"></el-switch>
				</el-form-item>
				<el-form-item class label="登录匹配地址" prop="loginmatchurl">
				  <el-input type="text" v-model="form.loginmatchurl" autocomplete="off" @change="change($event,'loginmatchurl')"></el-input>
				  <span>检查到匹配地址将需要登录，多个地址以“;”隔开如：/res/*;/abc/*。</span>
				</el-form-item>
				
				<h3 class="pdtb-10 color-orange">错误页面配置</h3>
				<el-form-item class label="404地址" prop="error_notfound">
				  <el-input type="text" v-model="form.error_notfound" autocomplete="off" @change="change($event,'error_notfound')"></el-input>
				</el-form-item>
				<el-form-item class label="500地址" prop="error_error">
				  <el-input type="text" v-model="form.error_error" autocomplete="off" @change="change($event,'error_error')"></el-input>
				</el-form-item>
				<el-form-item class label="无权限地址" prop="error_nopermission">
				  <el-input type="text" v-model="form.error_nopermission" autocomplete="off" @change="change($event,'error_nopermission')"></el-input>
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