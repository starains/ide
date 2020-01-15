source.repository.tabs = [];
source.repository.activeTab = null;

source.repository.navs = [
];

(function () {
    source.onLoadProject = function (value) {
        if (value == null) {
            return;
        }
        let project = source.getProjectByPath(value.path);
        if (project == null) {
            return;
        }
        project.app = value.app;
        project.files = project.files || [];
        coos.trimList(project.files);
        value.files = value.files || [];
        source.formatFiles(value.files, project);

        if (project.format_folders && project.format_folders.length > 0) {
            let rootPath = '';
            if (!project.root) {
                rootPath = project.path + '/';
            }
            project.format_folders.forEach(format_folder => {
                let format_file = { name: format_folder, path: rootPath + format_folder, directory: true, files: [] };

                let file = source.getFileFromFiles(format_file.path, value.files);
                if (file != null) {
                    if (file.parent != null) {
                        file.parent.files.splice(file.parent.files.indexOf(file), 1);
                    }
                    if (file.files != null) {
                        file.files.forEach(f => {
                            source.formatFile(f, format_file);
                            format_file.files.push(f);
                        });
                        coos.trimList(file.files);
                    }
                }

                source.formatFile(format_file, project);
                project.files.push(format_file);
            });
            value.files.forEach(file => {
                project.files.push(file);
            });
        } else {
            value.files.forEach(file => {
                project.files.push(file);
            });
        }
        project.loading = false;
        source.refreshProjectFileStatus(project);
    };
    source.getProjectByPath = function (path) {
        let result = null;
        if (source.repository) {
            if (source.repository.projects) {
                source.repository.projects.forEach(project => {
                    if (!project.root) {
                        let projectPath = project.path;
                        if (path == projectPath) {
                            result = project;
                        }
                        else {
                            if (!projectPath.endsWith("/")) {
                                projectPath += "/";
                            }
                            if (path.startsWith(projectPath)) {
                                result = project;
                            }
                        }
                    }
                });

                if (result == null) {
                    source.repository.projects.forEach(project => {
                        if (project.root) {
                            result = project;
                        }
                    });
                }
            }
        }
        return result;
    };

    source.reloadProject = function (project) {

        project.loading = true;
        return source.load('PROJECT', { path: project.path });
    };
})();

export default source;