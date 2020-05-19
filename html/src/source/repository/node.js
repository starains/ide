

(function () {
    source.nodeInstall = function (project) {
        coos.info('Node Install Start');
        source.do('NODE_INSTALL', { path: project.path }, project).then(res => {
            if (res.errcode == 0) {
                coos.success('Node Install End');
            } else {
                coos.error(res.errmsg);
            }
        });
    };
})();

export default source;