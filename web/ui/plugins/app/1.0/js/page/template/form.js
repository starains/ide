
(function() {
	var PageEditor = Editor.Page;

	PageEditor.prototype.getFormBoxHtml = function() {
		let html = `
		<div class="">
		<div class="title">设置</div>
		<div class="pd-5">
			<div class="remark color-grey">UI：{{remark}}
			</div>
			<div class="place color-grey">位置：HTML
				<template v-for="place in places"><span>&gt;</span><a class="coos-link color-green" @click="clickPlace(place)">{{place.name}}</a></template>
			</div>
			<div class="">
				<a class="coos-btn coos-btn-xs color-green mgr-5 mgb-5" :class="{'coos-disabled' : !has_add_child}" @click="add_child()">添加子模块</a>
				<a class="coos-btn coos-btn-xs color-green mgr-5 mgb-5" :class="{'coos-disabled' : !has_add_before}" @click="add_before()">之前添加</a>
				<a class="coos-btn coos-btn-xs color-green mgr-5 mgb-5" :class="{'coos-disabled' : !has_add_after}" @click="add_after()">之后添加</a>
			</div>
			<div class="">
				<a class="coos-btn coos-btn-xs color-orange mgr-5 mgb-5" :class="{'coos-disabled' : !has_move_up}" @click="move_up()">上移</a>
				<a class="coos-btn coos-btn-xs color-orange mgr-5 mgb-5" :class="{'coos-disabled' : !has_move_dw}" @click="move_dw()">下移</a>
				<a class="coos-btn coos-btn-xs color-red mgr-5 mgb-5" :class="{'coos-disabled' : !has_remove}" @click="remove()">删除</a>
			</div>
			<div class="">
				<el-form :model="data" size="mini" :rules="rules" ref="form" label-width="60px">
					<h4 class="color-grey ft-12">基础属性</h4>
					<el-form-item class label="name" prop="name">
						<el-input type="text" v-model="data.name" @change="formDataChange($event,'name')" autocomplete="off">
						</el-input>
					</el-form-item>
					<el-form-item class label="class" prop="class">
						<el-input type="textarea" autosize v-model="data.class" @change="formDataChange($event,'class')" autocomplete="off">
						</el-input>
					</el-form-item>
					<el-form-item class label="style" prop="style">
						<el-input type="textarea" autosize v-model="data.style" @change="formDataChange($event,'style')" autocomplete="off">
						</el-input>
					</el-form-item>
					<el-form-item class label="v-if" prop="v-if">
						<el-input type="textarea" autosize v-model="data['v-if']" @change="formDataChange($event,'v-if')" autocomplete="off">
						</el-input>
					</el-form-item>
					<el-form-item class label="v-for" prop="v-for">
						<el-input type="textarea" autosize v-model="data['v-for']" @change="formDataChange($event,'v-for')" autocomplete="off">
						</el-input>
					</el-form-item>
					<el-form-item class label="v-show" prop="v-show">
						<el-input type="textarea" autosize v-model="data['v-show']" @change="formDataChange($event,'v-show')" autocomplete="off">
						</el-input>
					</el-form-item>
					<h4 class="color-grey ft-12">基础属性扩展<a class="coos-link color-orange float-right" v-on:click="open_base=(open_base?false:true)">{{open_base?'收起':'展开'}}</a></h4>
					<div class="" v-show="open_base">
						<el-form-item class label=":class" prop=":class">
							<el-input type="textarea" autosize v-model="data[':class']" @change="formDataChange($event,':class')" autocomplete="off">
							</el-input>
						</el-form-item>
						<el-form-item class label=":style" prop=":style">
							<el-input type="textarea" autosize v-model="data[':style']" @change="formDataChange($event,':style')" autocomplete="off">
							</el-input>
						</el-form-item>
						<el-form-item class label="v-else-if" prop="v-else-if">
							<el-input type="textarea" autosize v-model="data['v-else-if']" @change="formDataChange($event,'v-else-if')" autocomplete="off">
							</el-input>
						</el-form-item>
						<el-form-item class label="v-else" prop="v-else">
							<el-input type="textarea" autosize v-model="data['v-else']" @change="formDataChange($event,'v-else')" autocomplete="off">
							</el-input>
						</el-form-item>
					</div>
					<h4 class="color-grey ft-12">组件属性</h4>
					<template v-for="item in items">
						<template v-if="item.type == 'textarea'">
							<el-form-item class :label="item.label" :prop="item.prop">
								<el-input type="textarea" autosize v-model="data[item.name]" @change="formDataChange($event, item.name)" autocomplete="off"></el-input>
							</el-form-item>
						</template>
						<template v-else-if="item.type == 'color'">
							<el-form-item class :label="item.label" :prop="item.prop">
								<el-color-picker v-model="data[item.name]" @change="formDataChange($event, item.name)"></el-color-picker>
							</el-form-item>
						</template>
						<template v-else-if="item.type == 'switch'">
							<el-form-item class :label="item.label" :prop="item.prop">
								<el-switch v-model="data[item.name]" @change="formDataChange($event, item.name)"></el-switch>
							</el-form-item>
						</template>
						<template v-else-if="item.type == 'slider'">
							<el-form-item class :label="item.label" :prop="item.prop">
								<el-slider v-model="data[item.name]" @change="formDataChange($event, item.name)"></el-slider>
							</el-form-item>
						</template>
						<template v-else-if="item.type == 'select'">
							<el-form-item class :label="item.label" :prop="item.prop">
								<el-select v-model="data[item.name]" @change="formDataChange($event, item.name)" placeholder="请选择">
									<el-option v-for="option in item.options" :key="option.value" :value="option.value" :label="option.text" />
								</el-select>
							</el-form-item>
						</template>
						<template v-else-if="item.type == 'radio'">
							<el-form-item class :label="item.label" :prop="item.prop">
								<el-radio-group v-model="data[item.name]" @change="formDataChange($event, item.name)">
									<el-radio v-for="option in item.options" :key="option.value" :label="option.value" >{{option.text}}</el-radio>
								</el-radio-group>
							</el-form-item>
						</template>
						<template v-else-if="item.type == 'checkbox'">
							<el-form-item class :label="item.label" :prop="item.prop">
								<el-checkbox-group v-model="data[item.name]" @change="formDataChange($event, item.name)">
									<el-checkbox v-for="option in item.options" :key="option.value" :label="option.value" >{{option.text}}</el-checkbox>
								</el-checkbox-group>
							</el-form-item>
						</template>
						<template v-else>
							<el-form-item class :label="item.label" :prop="item.prop">
								<el-input v-model="data[item.name]" @change="formDataChange($event, item.name)" autocomplete="off"></el-input>
							</el-form-item>
						</template>
					</template>
				</el-form>
			</div>
		</div>
		</div>
		`;
		return html;
	};

	PageEditor.prototype.buildForm = function() {};

})();