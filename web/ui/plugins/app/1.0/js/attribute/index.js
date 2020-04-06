(function() {
	var AttributeEditor = coos.createClass(Editor);
	Editor.Attribute = AttributeEditor;

	AttributeEditor.prototype.isYaml = function() {
		return true;
	};
	AttributeEditor.prototype.buildDesign = function() {
		var that = this;

		var $design = this.$design;
		$design.empty();

		var $box = $('<div class="editor-attribute pd-20"></div>');
		$design.append($box);

		var model = this.model;
		let html = `
			<div class="coos-row ">
				<h3 class="pdb-10 color-orange">字段属性定义</h3>
				<el-form :model="form" status-icon :rules="rules" ref="form" label-width="150px" size="mini">
					<el-form-item class="col-6" label="响应错误码名称" prop="responseerrcode">
					  <el-input type="text" v-model="form.responseerrcode" autocomplete="off" @change="change($event,'responseerrcode')"></el-input>
					</el-form-item>
					<el-form-item class="col-6" label="响应错误信息名称" prop="responseerrcode">
					  <el-input type="text" v-model="form.responseerrcode" autocomplete="off" @change="change($event,'responseerrcode')"></el-input>
					</el-form-item>
					<el-form-item class="col-6" label="响应值名称" prop="responsevalue">
					  <el-input type="text" v-model="form.responsevalue" autocomplete="off" @change="change($event,'responsevalue')"></el-input>
					</el-form-item>
					<div class="col-12"></div>
					
					<el-form-item class="col-6" label="请求页码名称" prop="requestpageindex">
					  <el-input type="text" v-model="form.requestpageindex" autocomplete="off" @change="change($event,'requestpageindex')"></el-input>
					</el-form-item>
					<el-form-item class="col-6" label="请求每个数量名称" prop="requestpagesize">
					  <el-input type="text" v-model="form.requestpagesize" autocomplete="off" @change="change($event,'requestpagesize')"></el-input>
					</el-form-item>
					<div class="col-12"></div>
					
					<el-form-item class="col-6" label="响应页码名称" prop="responsepageindex">
					  <el-input type="text" v-model="form.responsepageindex" autocomplete="off" @change="change($event,'responsepageindex')"></el-input>
					</el-form-item>
					<el-form-item class="col-6" label="响应每个数量名称" prop="responsepagesize">
					  <el-input type="text" v-model="form.responsepagesize" autocomplete="off" @change="change($event,'responsepagesize')"></el-input>
					</el-form-item>
					
					<el-form-item class="col-6" label="响应总页数名称" prop="responsetotalpages">
					  <el-input type="text" v-model="form.responsetotalpages" autocomplete="off" @change="change($event,'responsetotalpages')"></el-input>
					</el-form-item>
					<el-form-item class="col-6" label="响应总记录数名称" prop="responsetotalcount">
					  <el-input type="text" v-model="form.responsetotalcount" autocomplete="off" @change="change($event,'responsetotalcount')"></el-input>
					</el-form-item>
					
					<el-form-item class="col-6" label="响应上一页名称" prop="responseuppage">
					  <el-input type="text" v-model="form.responseuppage" autocomplete="off" @change="change($event,'responseuppage')"></el-input>
					</el-form-item>
					<el-form-item class="col-6" label="响应下一页名称" prop="responsenextpage">
					  <el-input type="text" v-model="form.responsenextpage" autocomplete="off" @change="change($event,'responsenextpage')"></el-input>
					</el-form-item>
					
					<el-form-item class="col-12" label="响应分页报文规则" prop="responsepageresultrule">
					  <el-input type="textarea" :autosize="{minRows : 6}" v-model="form.responsepageresultrule" autocomplete="off" @change="change($event,'responsepageresultrule')"></el-input>
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