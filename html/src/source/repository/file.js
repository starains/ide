
source.repository.file_data_map = {};
(function () {

    source.openFolder = function (path) {
        let index = source.repository.open_folders.indexOf(path);
        if (index < 0) {
            source.repository.open_folders.push(path);
        }
    };
    source.closeFolder = function (path) {
        let index = source.repository.open_folders.indexOf(path);
        if (index >= 0) {
            source.repository.open_folders.splice(index, 1);
        }
    };


    source.openFileByPath = function (path) {
        if (coos.isEmpty(path)) {
            return;
        }
        let folder = "";
        if (path.lastIndexOf('/') > 0) {
            folder = path.substring(0, path.lastIndexOf('/'));
        }
        source.openFolder(folder);
        source.do("FILE_OPEN", { path: path });
        source.createTabByPath(path);
    };


    source.closeFileByPath = function (path) {
        if (coos.isEmpty(path)) {
            return;
        }
        source.do("FILE_CLOSE", { path: path });
    };

    source.getFileFromFiles = function (path, files) {
        if (coos.isEmpty(path)) {
            path == null;
        }
        if (files == null || files.length == 0) {
            return null;
        }
        let result = null;
        files.forEach(file => {
            if (result == null) {

                if (file.path == path) {
                    result = file;
                }
                if (result == null && file.files != null) {
                    result = source.getFileFromFiles(path, file.files);
                }
            }
        });
        return result;

    };
    source.formatFiles = function (files, parent) {
        if (files == null) {
            return;
        }
        files.forEach(file => {
            source.formatFile(file, parent);
            if (file.files) {
                source.formatFiles(file.files, file);
            }
        });
    };
    source.fileMove = function (file, folder) {

        // let index = file.parent.files.indexOf(file);
        // file.parent.files.splice(index, 1);
        // source.sortFolderFiles(file.parent);
        // folder.files.push(file);
        source.formatFiles([file], folder);
        source.sortFolderFiles(folder);


    };
    source.formatFile = function (file, parent) {
        if (file == null) {
            return;
        }
        if (!file.formated) {
            file.modified = false;
            file.untracked = false;
            file.conflicting = false;
            file.isProject = false;
            file.isRoot = false;
            file.isMaven = false;
            file.isDirectory = false;
            file.isFile = false;
            file.changed = false;
            file.saveing = false;
            file.loading = false;
            file.toRename = false;
            file.formated = true;
        }
        file.parent = parent;
        file.isProject = file.isProject;
        file.isDirectory = file.directory;
        file.isFile = file.file;
        file.isRoot = file.root;
        file.isMaven = file.maven;

        if (parent && !file.isNew) {
            if (parent.path == '') {
                file.path = file.name;
            } else {
                file.path = parent.path + '/' + file.name;
            }
        }
    };

    source.sortFolderFiles = function (folder) {
        if (folder == null) {
            return;
        }
        if (folder.files == null) {
            return;
        }
        let fs = [];
        folder.files.forEach((one) => {
            fs.push(Object.assign({}, one));
        });
        fs.sort(function (file1, file2) {
            if (file1.isDirectory && file2.isFile) {
                return -1;
            }
            if (file1.isFile && file2.isDirectory) {
                return 1;
            }
            return file1.name.localeCompare(file2.name);
        });

        fs.forEach((one, index) => {
            one.sequence = index;
        });

        source.formatFiles(fs, folder);

        coos.trimList(folder.files);
        fs.forEach((one, index) => {
            folder.files.push(one);
        });
    };
    source.updateFileName = function (file, new_name) {
        let old_path = file.path;
        let parentPath = '';
        if (file.path.lastIndexOf('/') > 0) {
            parentPath = file.path.substring(0, file.path.lastIndexOf('/') + 1);
        }
        let new_path = parentPath + new_name;
        file.name = new_name;
        file.path = new_path;
        source.updateFilesPath(file.files, old_path, new_path);

    };
    source.updateFilesPath = function (files, old_p_path, new_p_path) {
        if (files == null) {
            return;
        }
        files.forEach(file => {
            file.path = file.path.replace(old_p_path, new_p_path);
            source.updateFilesPath(file.files);
        });

    };

    source.parentFile = function (path) {
        if (path == null) {
            return null;
        }
        let rootName = path;
        if (path.indexOf('/') > 0) {
            rootName = path.substring(0, path.indexOf('/'));
        }
        let project = source.getProjectByPath(rootName);

        project.files.forEach(file => {

        });
    };
    source.onLoadFile = function (value) {
        if (value == null || coos.isEmpty(value.path)) {
            return;
        }
        source.repository.file_data_map[value.path] = value;
        source.buildFileEditor(value);
    };
    source.onLoadFiles = function (value) {
        console.log(value);
    };
    source.loadFiles = function (path) {
        source.load('FILES', { path: path });
    };
    source.loadFile = function (path) {
        source.load('FILE', { path: path });
    };
    source.saveFile = function (path, content) {
        source.do('FILE_SAVE', { path: path, content: content }).then(() => {
            let tab = source.getTab(path);
            tab.changed = false;
            source.loadGitStatus();
        });
    };

    source.refreshProjectsFileStatus = function () {
        source.repository.projects.forEach(one => {
            source.refreshProjectFileStatus(one);
        });
    };
    source.refreshProjectFileStatus = function (project) {
        source.refreshFileStatus(project);
    };
    source.refreshFileStatus = function (file) {
        file.modified = false;
        file.untracked = false;
        file.conflicting = false;
        source.repository.change_files.forEach(change_file => {
            if (file.isProject) {
                if (change_file.key.startsWith(file.path)) {
                    file.modified = change_file.status == 'modified';
                    file.untracked = change_file.status == 'untracked';
                    file.conflicting = change_file.status == 'conflicting';
                }
            } else if (file.isDirectory) {
                if (change_file.key.startsWith(file.path)) {
                    file.modified = change_file.status == 'modified';
                    file.untracked = change_file.status == 'untracked';
                    file.conflicting = change_file.status == 'conflicting';
                }
            } else if (file.isFile) {
                if (change_file.key.indexOf(file.path) >= 0) {
                    file.modified = change_file.status == 'modified';
                    file.untracked = change_file.status == 'untracked';
                    file.conflicting = change_file.status == 'conflicting';
                }
            }
        });

        if (file.files && file.files.length > 0) {
            file.files.forEach(one => {
                source.refreshFileStatus(one);
            });
        }
    };

})();

export default source;