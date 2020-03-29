(function() {
	var DatabaseEditor = coos.createClass(coos.Editor);
	coos.Editor.Database = DatabaseEditor;

	DatabaseEditor.prototype.isYaml = function() {
		return true;
	};
	DatabaseEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-database pd-20"></div>');
		$design.append($box);

		var model = this.model;
		let html = `
			<div class="coos-row ">
				<h3 class="pdb-10 color-orange">库信息配置</h3>
				<el-form :model="form" status-icon :rules="rules" ref="form" label-width="150px" size="mini">
					<el-form-item class label="驱动" prop="driver">
					  <el-input type="text" v-model="form.driver" autocomplete="off" @change="change($event,'driver')"></el-input>
					</el-form-item>
					<el-form-item class label="地址" prop="url">
					  <el-input type="text" v-model="form.url" autocomplete="off" @change="change($event,'url')"></el-input>
					</el-form-item>
					<el-form-item class label="用户名" prop="username">
					  <el-input type="text" v-model="form.username" autocomplete="off" @change="change($event,'username')"></el-input>
					</el-form-item>
					<el-form-item class label="密码" prop="password">
					  <el-input type="text" v-model="form.password" autocomplete="off" @change="change($event,'password')"></el-input>
					</el-form-item>
					<el-form-item class label="显示SQL" prop="showsql">
					  <el-switch v-model="form.showsql" @change="change($event,'showsql')"></el-switch>
					</el-form-item>
					<el-form-item class label="拼接库名" prop="mustbringname">
					  <el-switch v-model="form.mustbringname" autocomplete="off" @change="change($event,'mustbringname')" ></el-switch>
					  <span class="color-grey-4">如果选中生产的SQL语句将拼接库名称</span>
					</el-form-item>
					<el-form-item class label="initialsize" prop="initialsize">
					  <el-input type="text" v-model="form.initialsize" autocomplete="off" @change="change($event,'initialsize')"></el-input>
					</el-form-item>
					<el-form-item class label="maxtotal" prop="maxtotal">
					  <el-input type="text" v-model="form.maxtotal" autocomplete="off" @change="change($event,'maxtotal')"></el-input>
					</el-form-item>
					<el-form-item class label="minidle" prop="minidle">
					  <el-input type="text" v-model="form.minidle" autocomplete="off" @change="change($event,'minidle')"></el-input>
					</el-form-item>
					<el-form-item class label="maxidle" prop="maxidle">
					  <el-input type="text" v-model="form.maxidle" autocomplete="off" @change="change($event,'maxidle')"></el-input>
					</el-form-item>
					<el-form-item class label="maxwaitmillis" prop="maxwaitmillis">
					  <el-input type="text" v-model="form.maxwaitmillis" autocomplete="off" @change="change($event,'maxwaitmillis')"></el-input>
					</el-form-item>
					<el-form-item class label="maxactive" prop="maxactive">
					  <el-input type="text" v-model="form.maxactive" autocomplete="off" @change="change($event,'maxactive')"></el-input>
					</el-form-item>
					<el-form-item class label="maxwait" prop="maxwait">
					  <el-input type="text" v-model="form.maxwait" autocomplete="off" @change="change($event,'maxwait')"></el-input>
					</el-form-item>
					<el-form-item class label="validationquery" prop="validationquery">
					  <el-input type="text" v-model="form.validationquery" autocomplete="off" @change="change($event,'validationquery')"></el-input>
					  <span>输入一段SQL以此判断连接是否正常，如：SELECT 1</span>
					</el-form-item>
					<el-form-item class label="数据库初始化实现" prop="initializeclass">
					  <el-input type="text" v-model="form.initializeclass" autocomplete="off" @change="change($event,'initializeclass')"></el-input>
					  <span>需要实现com.teamide.db.ifaces.IDatabaseInitialize接口，返回Database</span>
					</el-form-item>
				</el-form>
			</div>
			`;

		$box.append(html);
		let form = {};
		Object.assign(form, model);
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
						model[name] = value;
						that.changeModel(false);
					}
				}
			}
		});


	};
})();