


(function () {

    source.onApp = function (value) {
        if (value == null) {
            return;
        }
        let project = source.getProjectByPath(value.path);
        if (project == null) {
            return;
        }
        project.app = value.app;
    };


    source.appGenerateSourceCode = function (project) {
        source.do("APP_GENERATE_SOURCE_CODE", {
            path: project.path
        }).then(res => {
            if (res.errcode == 0) {
                coos.success('生成成功！');
                source.reloadProject(project).then(res => {
                    source.loadGitStatus();
                });
            } else {
                coos.error(res.errmsg);
            }
        });
    };

})();

export default source;