if (!window.seajs) {
	throw new Error('please import seajs!');
}
seajs.config({
	alias : {
		'jquery' : './jquery/jquery.js'
	}
});