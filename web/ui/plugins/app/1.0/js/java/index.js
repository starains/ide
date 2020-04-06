(function() {
	var JavaEditor = coos.createClass(Editor);
	Editor.Java = JavaEditor;

	JavaEditor.prototype.isYaml = function() {
		return true;
	};

	JavaEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-java pd-20"></div>');
		$design.append($box);

		var model = this.model;

		let html = `
		<div class="coos-row ">
		
			<h3 class="col-12 pdb-10 color-orange">Java配置</h3>
			
			<strong class="col-12 pdb-20 color-red">
			注意：生成的Java源码需要依赖teamide.base包，源码在
				<a
      href="https://gitee.com/teamide/base"
      class="coos-link color-green"
      target="_blank"
    >https://gitee.com/teamide/base</a>，请自行下载引入。
    teamide.base不定期更新，更新最新代码使用！
			</strong>
			<el-form class="col-12" :model="form" status-icon :rules="rules" ref="form" label-width="150px" size="mini">
				<div class="col-6">
					<el-form-item class label="Java代码目录" prop="javadirectory">
					  <el-input type="text" v-model="form.javadirectory" autocomplete="off" @change="change($event,'javadirectory')" placeholder="默认：src/main/java"></el-input>
					  <span class="color-grey-4">配置Java源码目录，默认：src/main/java</span>
					</el-form-item>
				</div>
				<div class="col-6">
					<el-form-item class label="资源文件目录" prop="resourcesdirectory">
					  <el-input type="text" v-model="form.resourcesdirectory" autocomplete="off" @change="change($event,'resourcesdirectory')" placeholder="默认：src/main/resources"></el-input>
					  <span class="color-grey-4">配置资源文件目录，默认：src/main/resources</span>
					</el-form-item>
				</div>
				<div class="col-12">
					<el-form-item class label="基础包名" prop="basepackage">
					  <el-input type="text" v-model="form.basepackage" autocomplete="off" @change="change($event,'basepackage')" placeholder="默认：com.teamide.app"></el-input>
					  <span class="color-grey-4">配置基础包，生成的源码在基础包下相应位置，默认：com.teamide.app</span>
					</el-form-item>
				</div>
				<div class="col-12">
					<el-form-item class label="合并目录" prop="mergedirectory">
					  <el-switch v-model="form.mergedirectory" autocomplete="off" @change="change($event,'mergedirectory')" ></el-switch>
					  <span class="color-grey-4">如果选中合并，则合并代码到一个文件中，例如：user/insert、user/update，生成的Dao为user/userDao，里边有insert和update方法</span>
					</el-form-item>
				</div>
				<div class="col-12">
					<el-form-item class label="目录包名级别" prop="directorypackagelevel">
					  <el-input type="text" v-model="form.directorypackagelevel" autocomplete="off" @change="change($event,'directorypackagelevel')" placeholder="默认：根据目录名称生成报名"></el-input>
					  <span class="color-grey-4">模型目录名称生成包名级别，如写1则只生成一级报名，子目录下文件都放在该包名下。默认根据模型的目录名称生成包名</span>
					</el-form-item>
				</div>
				<div class="col-12">
					<el-form-item class label="使用Mybatis" prop="usemybatis">
					  <el-switch v-model="form.usemybatis" autocomplete="off" @change="change($event,'usemybatis')" ></el-switch>
					  <span class="color-grey-4">如果选中，则生成mapper目录，创建Mybatis的增删改查xml</span>
					</el-form-item>
				</div>
				<div class="col-6">
					<el-form-item class label="TeamIDE基础包名" prop="teamidepackage">
					  <el-input type="text" v-model="form.teamidepackage" autocomplete="off" @change="change($event,'teamidepackage')" placeholder="默认：基础包名.teamide"></el-input>
					  <span class="color-grey-4">TeamIDE基础包，会生成一些基础文件，默认：基础包名.teamide</span>
					</el-form-item>
				</div>
				<div class="col-6">
					<el-form-item class label="Component包名" prop="componentpackage">
					  <el-input type="text" v-model="form.componentpackage" autocomplete="off" @change="change($event,'componentpackage')" placeholder="默认：基础包名.component"></el-input>
					  <span class="color-grey-4">组件包，生成SpringBoot相应组件，默认：基础包名.component</span>
					</el-form-item>
				</div>
				<div class="col-6">
					<el-form-item class label="Factory包名" prop="factorypackage">
					  <el-input type="text" v-model="form.factorypackage" autocomplete="off" @change="change($event,'factorypackage')" placeholder="默认：基础包名.factory"></el-input>
					</el-form-item>
				</div>
				<div class="col-6">
					<el-form-item class label="Bean包名" prop="beanpackage">
					  <el-input type="text" v-model="form.beanpackage" autocomplete="off" @change="change($event,'beanpackage')" placeholder="默认：基础包名.bean"></el-input>
					</el-form-item>
				</div>
				<div class="col-6">
					<el-form-item class label="Controller包名" prop="controllerpackage">
					  <el-input type="text" v-model="form.controllerpackage" autocomplete="off" @change="change($event,'controllerpackage')" placeholder="默认：基础包名.controller"></el-input>
					</el-form-item>
				</div>
				<div class="col-6">
					<el-form-item class label="Service包名" prop="servicepackage">
					  <el-input type="text" v-model="form.servicepackage" autocomplete="off" @change="change($event,'servicepackage')" placeholder="默认：基础包名.service"></el-input>
					</el-form-item>
				</div>
				<div class="col-6">
					<el-form-item class label="Dao包名" prop="daopackage">
					  <el-input type="text" v-model="form.daopackage" autocomplete="off" @change="change($event,'daopackage')" placeholder="默认：基础包名.dao"></el-input>
					</el-form-item>
				</div>
				<div class="col-6">
					<el-form-item class label="Dao包名" prop="daopackage">
					  <el-input type="text" v-model="form.daopackage" autocomplete="off" @change="change($event,'daopackage')" placeholder="默认：基础包名.dao"></el-input>
					</el-form-item>
				</div>
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