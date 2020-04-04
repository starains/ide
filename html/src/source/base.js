var source = window.source = new Object();
source.isLogin = false;
source.LOGIN_USER = null;
source.isManager = false;
source.roles = [];
source.toRegister = false;
source.SPACE_TYPE = null;
source.branch = null;
source.UPGRADE_STATUS = null;
source.UPGRADE_DOWNLOAD = { count: 0, downloaded_count: 0, downloading_path: '' };
source.cache = {};

source.repository = {
    loading: false,
    branchs: [],
    branch: null,
    projects: [],
    git: null,
    terminals: [],
    runners: [],
    opens: [],
    open_files: [],
    open_folders: [],
    activeTab: null,
    tabs: [],
    change_files: [],
    contextmenu: {
        show: false, menus: []
    },
};

source.toPreference = function () {
    source.preferenceForm.show();
};
source.changePreference = function () {
    source.initPreferenceStyle();
};
source.initPreferenceStyle = function () {
    let $style = $('#preference-style');
    if ($style.length == 0) {
        $style = $('<style type="text/css" id="preference-style"></style>');
        $('head').append($style);
    }
    $style.empty();
    let preference = source.preference;
    if (preference) {
        $style.append('.repository-tab-editor .editor-code{font-size: ' + preference.fontsize + 'px}');
    }
};
window.source = source;


export default source;