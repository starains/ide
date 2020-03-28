

(function () {


    source.mavenClean = function (project) {
        source.do('MAVEN_CLEAN', { path: project.path }, project);
    };
    source.mavenCompile = function (project) {
        source.do('MAVEN_COMPILE', { path: project.path }, project);
    };
    source.mavenPackage = function (project) {
        source.do('MAVEN_PACKAGE', { path: project.path }, project);
    };
    source.mavenInstall = function (project) {
        source.do('MAVEN_INSTALL', { path: project.path }, project);
    };
    source.mavenDeploy = function (project) {
        source.do('MAVEN_DEPLOY', { path: project.path }, project);
    };
})();

export default source;