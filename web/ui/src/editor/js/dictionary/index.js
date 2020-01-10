(function() {
	var DictionaryEditor = coos.createClass(coos.Editor);
	coos.Editor.Dictionary = DictionaryEditor;

	DictionaryEditor.prototype.isYaml = function() {
		return true;
	};
	DictionaryEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class=""></div>');
		$design.append($box);

		var model = this.model;
		model.options = model.options || [];

		model.options.forEach(option => {
			let html = `
				<div class="coos-row " style="width: 50%;display: inline-block;">
				<div class="pdlr-10 mg-5 bd">
					<h3 class="pd-10 color-orange font-sm">选项—【{{form.text || form.value}}】
					<a class="float-right coos-link color-red font-xs" @click="remove()">删除</a>
					</h3>
					<el-form :model="form" class="overflow-auto" status-icon :rules="rules" ref="form" label-width="70px" size="mini">
						
						<el-form-item :class="{'col-12' : form.type == '','col-6' : form.type != ''}" label="类型" prop="username">
						  <el-select v-model="form.type" placeholder="请选择" class="col-12">
				            <el-option label="使用当前配置数据" value=""></el-option>
				            <el-option label="读Table数据" value="TABLE"></el-option>
				            <el-option label="读Dao数据" value="DAO"></el-option>
				            <el-option label="读Service数据" value="SERVICE"></el-option>
				          </el-select>
						</el-form-item>
						
						<el-form-item class="col-6" label="读取表" v-if="form.type == 'TABLE'" prop="tablename">
						  <el-input type="text" v-model="form.tablename" autocomplete="off" @change="change($event,'tablename')"></el-input>
						</el-form-item>
						<el-form-item class="col-6" label="读取Dao" v-if="form.type == 'DAO'" prop="daoname">
						  <el-input type="text" v-model="form.daoname" autocomplete="off" @change="change($event,'daoname')"></el-input>
						</el-form-item>
						<el-form-item class="col-6" label="读取Service" v-if="form.type == 'SERVICE'" prop="servicename">
						  <el-input type="text" v-model="form.servicename" autocomplete="off" @change="change($event,'servicename')"></el-input>
						</el-form-item>
						
						<el-form-item class="col-4" label="文案" prop="text">
						  <el-input type="text" v-model="form.text" autocomplete="off" @change="change($event,'text')"></el-input>
						</el-form-item>
						<el-form-item class="col-4" label="值" prop="value">
						  <el-input type="text" v-model="form.value" autocomplete="off" @change="change($event,'value')"></el-input>
						</el-form-item>
						<el-form-item class="col-4" label="父" prop="parent">
						  <el-input type="text" v-model="form.parent" autocomplete="off" @change="change($event,'parent')"></el-input>
						</el-form-item>
						
						<el-form-item class="col-4" label="字体图标" prop="fonticon">
						  <el-input type="text" v-model="form.fonticon" autocomplete="off" @change="change($event,'fonticon')"></el-input>
						</el-form-item>
						<el-form-item class="col-4" label="颜色" prop="color">
						  <el-input type="text" v-model="form.color" autocomplete="off" @change="change($event,'color')"></el-input>
						</el-form-item>
						<el-form-item class="col-4" label="地址" prop="url">
						  <el-input type="text" v-model="form.url" autocomplete="off" @change="change($event,'url')"></el-input>
						</el-form-item>
						<el-form-item class="col-4" label="HTML" prop="html">
						  <el-input type="text" v-model="form.html" autocomplete="off" @change="change($event,'html')"></el-input>
						</el-form-item>
						<el-form-item class="col-4" label="图片" prop="image">
						  <el-input type="text" v-model="form.image" autocomplete="off" @change="change($event,'image')"></el-input>
						</el-form-item>
						<el-form-item class="col-4" label="说明" prop="info">
						  <el-input type="text" v-model="form.info" autocomplete="off" @change="change($event,'info')"></el-input>
						</el-form-item>
					</el-form>
				</div>
				</div>
				`;
			option.type = option.type || '';
			let $html = $(html);
			$box.append($html);
			let form = {};
			Object.assign(form, option);
			new Vue({
				el : $html[0],
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
							option[name] = value;
							that.changeModel(false);
						}
					},
					remove () {
						that.recordHistory();
						model.options.splice(model.options.indexOf(option), 1);
						that.changeModel();
					}
				}
			});

		});

		var $btn = $('<a class="coos-link color-green">添加选项</a>');
		$btn.click(function() {
			that.recordHistory();
			model.options.push({});
			that.changeModel();
		});

		var $row = $('<div class="coos-row text-center pdtb-10"/>');
		$row.append($btn);

		$box.append($row);
	};
})();