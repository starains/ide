
import tab from "@/source/repository/tab";
import project from "@/source/repository/project";
import file from "@/source/repository/file";
import git from "@/source/repository/git";
import maven from "@/source/repository/maven";
import node from "@/source/repository/node";
import starter from "@/source/repository/starter";

source.repository.branchs = [];
source.repository.projects = [];
source.repository.fullscreen = false;
source.repository.fullscreencenter = false;
source.repository.REPOSITORY_PERMISSIONS = [];
source.repository.PROJECT_PERMISSIONS = [];
source.NOT_FOUND_REPOSITORY_BRANCH = null;
(function () {
    source.fullScreenCenter = function () {
        source.repository.fullscreencenter = true;
    };
    source.fullScreenExitCenter = function () {
        source.repository.fullscreencenter = false;
    };
    source.fullScreen = function () {
        source.repository.fullscreen = true;
    };
    source.fullScreenExit = function () {
        source.repository.fullscreen = false;
    };
    let maven_format_folders = ["src/main/java", "src/main/resources", "src/main/webapp"];
    source.loadRepository = function () {
        source.repository.loading = true;
        source.load('REPOSITORY');
    };
    source.onRepositoryStatus = function (msg) {
        if (coos.isEmpty(msg)) {
            source.repository.loading = false;
            source.repository.loading_msg = "";
        } else {
            source.repository.loading = true;
            source.repository.loading_msg = msg;
        }
    };
    source.onRunnerOptions = function (value) {
        value = value || [];
        coos.trimList(source.repository.runner_options);
        value.forEach(one => {
            source.repository.runner_options.push(one);
        });
    };
    source.onLoadRepository = function (value) {
        source.repository.loading = false;
        if (value == null) {
            source.NOT_FOUND_REPOSITORY_BRANCH = 'NOT_FOUND_REPOSITORY_BRANCH';
            return;
        }
        coos.trimList(source.repository.branchs);
        coos.trimList(source.repository.projects);
        coos.trimList(source.repository.starters);
        coos.trimList(source.repository.runners);
        coos.trimList(source.repository.opens);
        coos.trimList(source.repository.open_files);
        coos.trimList(source.repository.REPOSITORY_PERMISSIONS);
        coos.trimList(source.repository.PROJECT_PERMISSIONS);
        source.load('ENVIRONMENTS');
        source.load('REMOTES');
        source.load('STARTER_OPTIONS');
        source.load('STARTERS');
        source.load('REPOSITORY_PERMISSIONS');
        source.load('PROJECT_PERMISSIONS');
        if (value.branchs) {
            value.branchs.forEach(one => {
                source.repository.branchs.push(one);
            });
        } if (value.projects) {
            value.projects.forEach(one => {
                source.formatFile(one);

                one.isProject = true;
                one.loading = true;
                if (one.root) {
                    one.name = source.space.name;
                }
                one.deletes = [];
                one.files = [];
                if (one.maven && one.packaging != 'pom') {
                    one.format_folders = maven_format_folders;
                }
                source.repository.projects.push(one);
                source.reloadProject(one);
            });
        }
        if (value.starters) {
            value.starters.forEach(one => {
                source.repository.starters.push(one);
            });
        }
        if (value.runners) {
            value.runners.forEach(one => {
                source.repository.runners.push(one);
            });
        }
        if (value.opens) {
            value.opens.forEach((one, index) => {
                source.repository.opens.push(one);
                let openTab = false;
                if (index >= (value.opens.length - 1)) {
                    openTab = true;
                }
                source.openFileByPath(one.path, openTab);
            });
        }
        source.loadGitStatus();
    };
})();

export default source;