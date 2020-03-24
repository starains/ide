source.repository.tabs = [];
source.repository.activeTab = null;

source.repository.navs = [
];

(function () {

    source.formatPublicFolder = function (formats, parent, files) {
        if (files == null || files.length == null) {
            return;
        }
        formats = formats || [];
        formats.forEach(format => {
            let format_file = { name: format.name, path: format.path, directory: true, files: [], isFormatFolder: true };

            let file = source.getFileFromFiles(format_file.path, files);
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

            source.formatFile(format_file, parent);
            parent.files.push(format_file);
        });

        files.forEach(file => {
            parent.files.push(file);
        });

    };

    source.appendAllFolders = function (files, folders) {
        folders = folders || [];
        if (files == null || files.length == 0) {
            return folders;
        }
        files.forEach(f => {
            if (f.isDirectory) {
                folders.push(f.path);
                source.appendAllFolders(f.files, folders);
            }
        });

        return folders;
    };

    source.getPublickPathByFiles = function (files) {
        if (files == null || files.length == 0) {
            return null;
        }
        let folders = source.appendAllFolders(files);

        if (folders.length == 0) {
            return null;
        }
        let last = null;
        for (let i = 0; i < folders.length; i++) {
            let folder = folders[i];
            let isAll = true;
            for (let n = 0; n < folders.length; n++) {
                let f = folders[n];
                if (f == folder) {

                } else if (f.startsWith(folder + '/')) {

                } else if (folder.startsWith(f + '/')) {

                } else {
                    isAll = false;
                }
            }
            if (!isAll) {
                break;
            } else {
                last = folder;
            }
        }
        return last;
    };

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
            let formats = [];

            project.format_folders.forEach(format_folder => {
                let format = { name: format_folder, path: rootPath + format_folder };
                formats.push(format);
            });
            source.formatPublicFolder(formats, project, value.files);
            project.files.forEach(file => {
                if (file.name == 'src/main/java') {
                    let path = source.getPublickPathByFiles(file.files);
                    if (path && !coos.isEmpty(path) && path.startsWith('src/main/java/')) {
                        let packPath = path.substring('src/main/java/'.length);
                        if (packPath.length > 0) {
                            let pack = coos.replaceAll(packPath, '/', '.');
                            let fs = [];
                            file.files.forEach(f => {
                                fs.push(f);
                            });
                            coos.trimList(file.files);
                            let formats = [];
                            let format = { name: pack, path: file.path + '/' + packPath };
                            formats.push(format);
                            source.formatPublicFolder(formats, file, fs);
                        }
                    }
                }
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