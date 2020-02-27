


(function () {

    source.onApp = function (value) {
        if (value == null) {
            return;
        }
        let project = source.getProjectByPath(value.path);
        if (project == null) {
            return;
        }
        if (project.app == null) {
            project.app = value.app;
        } else {
            Object.keys(project.app).forEach(key => {
                if (coos.isObject(value.app[key])) {
                    if (project.app[key] == null) {
                        project.app[key] = {};
                    }
                    Object.keys(value.app[key]).forEach(key1 => {
                        project.app[key][key1] = value.app[key][key1];
                    });
                }
            });
        }
    };

    source.loadApp = function (project) {
        source.load("APP", {
            path: project.path
        })
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