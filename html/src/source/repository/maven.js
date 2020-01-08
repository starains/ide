

(function () {


    source.mavenClean = function (project) {
        source.do('MAVEN_CLEAN', { path: project.path });
    };
    source.mavenCompile = function (project) {
        source.do('MAVEN_COMPILE', { path: project.path });
    };
    source.mavenPackage = function (project) {
        source.do('MAVEN_PACKAGE', { path: project.path });
    };
    source.mavenInstall = function (project) {
        source.do('MAVEN_INSTALL', { path: project.path });
    };
    source.mavenDeploy = function (project) {
        source.do('MAVEN_DEPLOY', { path: project.path });
    };
})();

export default source;