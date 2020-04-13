
(function() {
	var PageEditor = Editor.Page;

	PageEditor.prototype.getPageOptionFormHtml = function() {
		let html = `
<div class="pd-10">
	<div class="color-orange pdtb-5 ft-15">基础属性</div>
	<el-form v-if="attrData != null" :model="attrData" size="mini" :rules="attrRules" ref="form" label-width="60px">
		<template v-for="(attr, index) in attrs">
			<el-form-item class :label="attr.text" :prop="attr.name">
				<div class="coos-row ft-12 color-grey">
					绑定data属性：<el-switch v-model="attr.isBind" active-color="#13ce66" inactive-color="#ff4949" ></el-switch>
				</div>
				<template v-if="attr.isBind">
					<el-input type="text" v-model="attr.bindName" @change="attrFormDataChange($event, attr)" autocomplete="off">
					</el-input>
				</template>
				<template v-else>
					<template v-if="attr.type == 'select'">
						<el-select v-model="attrData['' + attrs[index].name]" @change="attrFormDataChange($event, attr)" clearable placeholder="请选择" >
							<el-option v-for="option in attr.options" :value="option.value" >
							<c-color :color="option.color" :bg="option.bg" >{{option.text}}</c-color>
							</el-option>
						</el-select>
					</template>
					<template v-else-if="attr.type == 'switch'">
						<el-switch type="text" v-model="attrData['' + attrs[index].name]" @change="attrFormDataChange($event, attr)" autocomplete="off">
						</el-switch>
					</template>
					<template v-else>
						<el-input type="text" v-model="attrData['' + attrs[index].name]" @change="attrFormDataChange($event, attr)" autocomplete="off">
						</el-input>
					</template>
				</template>
			</el-form-item>
		</template>
	</el-form>
	<div class="pdtb-10">
		<a class="coos-btn bg-green coos-btn-sm" @click="saveAttrData">保存</a>
	</div>
</div>
		`;
		return html;
	};


})();