


(function () {


    source.loadApp = function (project) {
        source.plugin.app.event("LOAD_APP", { path: project.path }, project).then(res => {
            let value = res.value || {};
            source.setProjectApp(project, value.app);
        });
    };


    source.appGenerateSourceCode = function (project) {

        source.plugin.app.event("GENERATE_SOURCE_CODE", { path: project.path }, project).then(res => {
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