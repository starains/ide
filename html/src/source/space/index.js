
source.space = null;
source.NOT_PERMISSION = null;
(function () {

    source.onLoadSpace = function (value) {
        if (value == null) {
            source.space = null;
            return;
        }
        if (coos.isEmpty(value.permission) || value.permission == 'NO') {
            source.NOT_PERMISSION = 'NOT_PERMISSION';
            source.space = null;
            return;
        }
        value.SPACE_PERMISSIONS = [];
        value.levels = null;

        source.space = value;

        source.load('SPACE_PERMISSIONS');
        source.load('PARENTS');
        if (source.space.type == "REPOSITORYS") {
            source.loadRepository();
            source.load('BRANCHS');
        }
    };
    source.onSpacePermissions = function (value) {
        value = value || [];
        coos.trimList(source.space.SPACE_PERMISSIONS);
        value.forEach(one => {
            source.space.SPACE_PERMISSIONS.push(one);
        });
    };
    source.onRepositoryPermissions = function (value) {
        value = value || [];
        coos.trimList(source.repository.REPOSITORY_PERMISSIONS);
        value.forEach(one => {
            source.repository.REPOSITORY_PERMISSIONS.push(one);
        });
    };
    source.onProjectPermissions = function (value) {
        value = value || [];
        coos.trimList(source.repository.PROJECT_PERMISSIONS);
        value.forEach(one => {
            source.repository.PROJECT_PERMISSIONS.push(one);
        });
    };

    source.hasPermission = function (arg) {
        let flag = false;
        if (!flag && source.repository) {
            if (source.repository.REPOSITORY_PERMISSIONS) {
                flag = source.repository.REPOSITORY_PERMISSIONS.indexOf(arg) >= 0;
            }
        }
        if (!flag && source.repository) {
            if (source.repository.PROJECT_PERMISSIONS) {
                flag = source.repository.PROJECT_PERMISSIONS.indexOf(arg) >= 0;
            }
        }
        if (!flag && source.space) {
            if (source.space.SPACE_PERMISSIONS) {
                flag = source.space.SPACE_PERMISSIONS.indexOf(arg) >= 0;
            }
        }
        return flag;
    };
})();

export default source;