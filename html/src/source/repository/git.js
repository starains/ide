source.repository.tabs = [];
source.repository.activeTab = null;

let git_nav = {
    title: 'Git', icon: 'coos-icon-git', value: 0, disabled: true, dot: true, type: 'success', onClick() {
        source.gitRemoteForm.show();
    }
};
source.repository.navs.push(git_nav);

let git_plus_nav = {
    title: 'Git提交', icon: 'coos-icon-plus-circle', value: 0, disabled: true, dot: false, type: 'success', onClick() {
        source.gitPlusForm.show();
    }
};
source.repository.navs.push(git_plus_nav);

let git_push_nav = {
    title: 'Git推送', icon: 'coos-icon-git-push', value: 0, disabled: true, dot: false, type: 'success', onClick() {
        source.gitPushForm.show();
    }
};
source.repository.navs.push(git_push_nav);

let git_pull_nav = {
    title: 'Git拉取', icon: 'coos-icon-git-pull', value: 0, disabled: true, dot: false, type: 'success', onClick() {
        source.gitPull();
    }
};
source.repository.navs.push(git_pull_nav);

let git_branche_nav = {
    title: 'Git分支', icon: 'coos-icon-branches', value: 0, disabled: true, dot: false, type: 'success'
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
        if (status) {

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


    source.gitRevert = function (paths) {
        if (paths == null || paths.length == 0) {
            return;
        }
        if (coos.isEmpty(paths[0])) {
            paths[0] = ".";
        }
        coos.confirm(
            '确定还原路径<span class="pdlr-10 color-green">[' +
            paths.join("；") +
            "]</span>？"
        ).then(res => {
            var data = {};
            data.paths = paths;

            source.do("GIT_REVERT", data).then(res => {
                if (res.errcode == 0) {
                    coos.success("还原成功！");
                    let project = source.getProjectByPath(value.path);
                    if (project == null) {
                        return;
                    }
                    source.reloadProject(project);

                    source.loadGitStatus();
                } else {
                    coos.error(res.errmsg);
                }
            });
        }, () => { });
    }
})();

export default source;