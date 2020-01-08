


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
        });
    };

})();

export default source;