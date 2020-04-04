

(function () {


    source.mavenClean = function (project) {
        coos.info('Maven Clean Start');
        source.do('MAVEN_CLEAN', { path: project.path }, project).then(res => {
            if (res.errcode == 0) {
                coos.success('Maven Clean End');
            } else {
                coos.error(res.errmsg);
            }
        });
    };
    source.mavenCompile = function (project) {
        coos.info('Maven Compile Start');
        source.do('MAVEN_COMPILE', { path: project.path }, project).then(res => {
            if (res.errcode == 0) {
                coos.success('Maven Compile End');
            } else {
                coos.error(res.errmsg);
            }
        });
    };
    source.mavenPackage = function (project) {
        coos.info('Maven Package Start');
        source.do('MAVEN_PACKAGE', { path: project.path }, project).then(res => {
            if (res.errcode == 0) {
                coos.success('Maven Package End');
            } else {
                coos.error(res.errmsg);
            }
        });
    };
    source.mavenInstall = function (project) {
        coos.info('Maven Install Start');
        source.do('MAVEN_INSTALL', { path: project.path }, project).then(res => {
            if (res.errcode == 0) {
                coos.success('Maven Install End');
            } else {
                coos.error(res.errmsg);
            }
        });
    };
    source.mavenDeploy = function (project) {
        coos.info('Maven Deploy Start');
        source.do('MAVEN_DEPLOY', { path: project.path }, project).then(res => {
            if (res.errcode == 0) {
                coos.success('Maven Deploy End');
            } else {
                coos.error(res.errmsg);
            }
        });
    };
})();

export default source;