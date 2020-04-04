source.repository.tabs = [];
source.repository.activeTab = null;

let git_nav = {
    title: 'Git', icon: 'coos-icon-git', value: 0, disabled: true, dot: true, type: 'success', onClick() {
        source.gitRemoteForm.show();
    }, permission: 'GIT_REMOTE_ADD'
};
source.repository.navs.push(git_nav);

let git_plus_nav = {
    title: 'Git还原', icon: 'coos-icon-undo', value: 0, disabled: true, dot: false, type: 'success', onClick() {
        source.gitRevertForm.show();
    }, permission: 'GIT_REVERT'
};
source.repository.navs.push(git_plus_nav);

let git_push_nav = {
    title: 'Git推送', icon: 'coos-icon-git-push', value: 0, disabled: true, dot: false, type: 'success', onClick() {
        source.gitPushForm.show();
    }, permission: 'GIT_PUSH'
};
source.repository.navs.push(git_push_nav);

let git_pull_nav = {
    title: 'Git拉取', icon: 'coos-icon-git-pull', value: 0, disabled: true, dot: false, type: 'success', onClick() {
        source.gitPull();
    }, permission: 'GIT_PULL'
};
source.repository.navs.push(git_pull_nav);

let git_branche_nav = {
    title: 'Git分支', icon: 'coos-icon-branches', value: 0, disabled: true, dot: false, type: 'success', permission: 'GIT_BRANCHES'
};
source.repository.navs.push(git_branche_nav);

(function () {


    source.onLoadGit = function (value) {
        if (value == null) {
            return;
        }
        git_nav.disabled = false;
        if (value.findGit) {
            git_nav.type = 'success';
            git_plus_nav.disabled = false;
            git_push_nav.disabled = false;
            git_pull_nav.disabled = false;
            git_branche_nav.disabled = false;
        } else {
            git_nav.type = 'warning';
            git_plus_nav.disabled = true;
            git_push_nav.disabled = true;
            git_pull_nav.disabled = true;
            git_branche_nav.disabled = true;
        }
        source.repository.git = value;

        coos.trimList(source.repository.change_files);

        let change_files = source.repository.change_files;
        let status = value.status;
        source.repository.projects.forEach(project => {
            coos.trimArray(project.deletes);
        });
        if (status) {

            status.missing.forEach(path => {
                let project = source.getProjectByPath(path);
                if (project) {
                    project.deletes.push(path);
                }
            });

            status.modified.forEach(path => {
                change_files.push({
                    key: path,
                    status: "modified",
                    label: path + "(更改)"
                });
            });
            status.untracked.forEach(path => {
                change_files.push({
                    key: path,
                    status: "untracked",
                    label: path + "(添加)"
                });
            });
            status.missing.forEach(path => {
                change_files.push({
                    key: path,
                    status: "missing",
                    label: path + "(移除)"
                });
            });
            status.conflicting.forEach(path => {
                change_files.push({
                    key: path,
                    status: "conflicting",
                    label: path + "(冲突)"
                });
            });



            git_push_nav.value = status.added.length + status.changed.length + status.removed.length;
            git_push_nav.dot = git_push_nav.value > 0;
        }
        git_plus_nav.value = change_files.length;
        git_plus_nav.dot = git_plus_nav.value > 0;


        source.refreshProjectsFileStatus();
    };

    source.onLoadGitWorkStatus = function (value) {
        value = value || {};
        let message = '';
        if (value.status == 'PUSHING') {
            message = 'Git推送中...';
        } else if (value.status == 'PULLING') {
            message = 'Git拉取中...';
        } else if (value.status == 'CHECKOUTING') {
            message = 'Git检出中...';
        } else {
            if (value.status == 'PUSHED') {
                coos.success('Git推送成功！');
            } else if (value.status == 'PULLED') {
                coos.success('Git拉取成功！');
            } else if (value.status == 'CHECKOUTED') {
                coos.success('Git检出成功！');
            } else if (!coos.isEmpty(value.message)) {
                coos.error(value.message);
            }
            source.loadGitStatus();
        }
        if (!coos.isEmpty(message)) {
            source.repository.loading = true;
            source.repository.loading_msg = message;

            window.setTimeout(() => {
                source.loadGitWorkStatus();
            }, 100);
        } else {
            source.repository.loading = false;
            source.repository.loading_msg = '';
        }
    };

    source.formatHeadBranchName = function (branch) {
        var name = branch.name.substring("refs/heads/".length);
        return name;
    };
    source.isHeadBranchName = function (branch) {
        return branch.name.startsWith("refs/heads/");
    };
    source.formatRemoteBranchName = function (remoteName, branch) {
        var name = branch.name.substring(
            ("refs/remotes/" + remoteName + "/").length
        );
        return name;
    };
    source.isRemoteBranchName = function (remoteName, branch) {
        return branch.name.startsWith("refs/remotes/" + remoteName + "/");
    };

    source.loadGitStatus = function () {
        source.load('GIT');
    };

    source.loadGitWorkStatus = function () {
        source.load('GIT_WORK_STATUS');
    };

    source.gitPull = function () {
        let data = {};
        data.gitRemoteName = source.repository.git.option.gitRemoteName;
        data.gitRemoteBranch = source.repository.git.option.gitRemoteBranch;
        source.gitCertificateForm.show().then(certificate => {
            data.certificate = certificate;
            source.do("GIT_PULL", data).then(res => {
                if (res.errcode == 0) {
                    source.loadGitWorkStatus();
                } else {
                    coos.error(res.errmsg);
                }
            });
        });
    };


    source.gitRevert = function (paths, deletes) {
        paths = paths || [];
        deletes = deletes || [];
        if ((paths.length == 0) && (deletes.length == 0)) {
            return;
        }
        if (paths.length > 0 && coos.isEmpty(paths[0])) {
            paths[0] = ".";
        }
        let html = '';
        html += '<div class="coos-row">';
        html += '<div class="coos-row ft-14 pdb-10">确定还原以下路径？</div>';
        html += '<div class="coos-row" style="max-height: 500px;overflow:auto;">';

        paths.forEach(one => {
            html += '<div class="coos-row color-grey ft-13 mgb-5" style="word-wrap: break-word;word-break: normal;">' + one + '（还原）</div>';
        });
        deletes.forEach(one => {
            html += '<div class="coos-row color-grey ft-13 mgb-5" style="word-wrap: break-word;word-break: normal;">' + one + '（删除）</div>';
        });

        html += '</div>';
        html += '</div>';
        coos.confirm(html, { width: '700px' }).then(res => {
            var data = {};
            data.paths = paths;
            data.deletes = deletes;

            source.do("GIT_REVERT", data).then(res => {
                if (res.errcode == 0) {
                    coos.success("还原成功！");
                    let path = null;
                    if (paths.length > 0) {
                        path = paths[0];
                    } else {
                        path = deletes[0];
                    }
                    let project = source.getProjectByPath(path);
                    if (project != null) {
                        source.reloadProject(project);
                    }

                    source.loadGitStatus();
                } else {
                    coos.error(res.errmsg);
                }
            });
        }, () => { });
    }
})();

export default source;