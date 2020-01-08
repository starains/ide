var app = window.app || new Object();
window.app = app;
(function () {

    app.hasPermission = function (arg) {
        let permission = source.space.permission;
        if (permission == "MASTER") {
            return true;
        }
        if (!coos.isEmpty(arg)) {
            if (arg == 'MANAGER_TEAM') {
                return false;
            }
        }
        if (permission == "DEVELOPER") {
            return true;
        }
        return false;
    };

    app.toSpace = function (space) {
        var path = app.getSpacePath(space);
        coos.toURL(app.getURL(path));
    };

    app.toBranch = function (branch) {
        var path = app.getSpacePath(source.space);
        path += '/' + branch;
        coos.toURL(app.getURL(path));
    };

    app.getSpaceUrl = function (space) {
        var path = app.getSpacePath(space);
        return app.getURL(path);
    };

    app.getURL = function (path) {
        if (!_ROOT_URL.endsWith('/') && !path.startsWith('/')) {
            return _ROOT_URL + '/' + path;

        }
        return _ROOT_URL + path;
    };

    app.getSpacePath = function (space) {
        return space.servletpath;
    };


    app.clipboardSelect = function (text) {
        $('#coos-for-copy-area').remove();
        var textArea = document.createElement("textarea");
        textArea.id = 'coos-for-copy-area';
        textArea.style.position = 'fixed';
        textArea.style.top = '0';
        textArea.style.left = '0';
        textArea.style.width = '2em';
        textArea.style.height = '2em';
        textArea.style.padding = '0';
        textArea.style.border = 'none';
        textArea.style.outline = 'none';
        textArea.style.boxShadow = 'none';
        textArea.style.background = 'transparent';
        textArea.style.zIndex = '-1';
        textArea.value = text;
        document.body.appendChild(textArea);
        textArea.select(); // 选择对象
    };
    app.clipboardGet = function (event, callback) {
        var isChrome = false;
        if (event.clipboardData || event.originalEvent) {
            // not for ie11 某些chrome版本使用的是event.originalEvent
            var clipboardData = (event.clipboardData || event.originalEvent.clipboardData);
            if (clipboardData.items) {
                // for chrome
                var items = clipboardData.items, len = items.length, blob = null;
                isChrome = true;
                // 阻止默认行为即不让剪贴板内容在div中显示出来
                event.preventDefault();
                $(items).each(function (index, item) {
                    item.getAsString(function (value) {
                        callback && callback(value);
                    });
                });
            }
        }
    };
    app.autocomplete = function (options) {
        options = options || {};
        let input = options.$input || options.input;
        let datas = options.datas || [];
        if (!input) {
            return;
        }
        let $ = window.jQuery;
        input = $(input);
        input.on("focus", function (event) {
            var instance = $(this).autocomplete("instance");
            if (instance) {
                $(this).autocomplete("search");
            }
        });
        input.autocomplete(datas, {
            source: datas,
            minLength: 0,
            focus: function (event, ui) {
                return false;
            },
            select: function (event, ui) {
                if (options.onSelect) {
                    options.onSelect(ui.item);
                } else {
                    input.val(ui.item.value);
                    input.change();
                }
                return false;
            }
        });
        var auto = input.data("ui-autocomplete");
        auto._renderItem_back = auto._renderItem;
        auto._renderItem = function (ul, item) {
            var html = item.text;
            if (item.html) {
                html = item.html;
            }
            var $li = this._renderItem_back(ul, item);
            $li.html(html);
            return $li;
        };
        var widget = input.autocomplete("widget");
        $(widget).css('max-height', 300);
        $(widget).css('overflow-x', 'hidden');
        $(widget).css('overflow-y', 'auto');
        $(widget).addClass('coos-autocomplete');
    };

    app.formDialog = function (options) {
        options = options || {};
        let $form = $('<el-form :model="form" ref="form" :rules="rules"/>');
        $form.attr('label-width', options['label-width'] || '120px');
        $form.attr('size', options['size'] || 'mini');

        options.items = options.items || [];
        let data = options.data || {};

        options.items.forEach((item, index) => {
            let $item = $('<el-form-item />');
            $item.attr('label', item.label);
            $item.attr('prop', item.name);
            $item.attr('v-if', item['v-if']);

            let $input = $('<el-input type="text" autocomplete="off" />');
            if (item.type == 'textarea') {
                $input = $('<el-input type="textarea" autocomplete="off" />');
            } else if (item.type == 'switch') {
                $input = $('<el-switch />');
                data[item.name] = coos.isTrue(data[item.name]);
            } else if (item.type == 'slider') {
                $input = $('<el-slider autocomplete="off" />');
                data[item.name] = coos.isTrue(data[item.name]);
            } else if (item.type == 'select') {
                $input = $('<el-select placeholder="请选择"/>');
                let $option = $('<el-option :key="option.value" :value="option.value" :label="option.text" />');
                $input.append($option);
                item.options = item.options || [];
                $option.attr('v-for', 'option in items[' + index + '].options');
            } else if (item.type == 'radio') {
                $input = $('<el-radio-group />');
                let $option = $('<el-radio :key="option.value" :label="option.value" >{{option.text}}</el-radio>');
                $input.append($option);
                item.options = item.options || [];
                $option.attr('v-for', 'option in items[' + index + '].options');
            } else if (item.type == 'checkbox') {
                $input = $('<el-checkbox-group />');
                let $option = $('<el-checkbox :key="option.value" :label="option.value" >{{option.text}}</el-checkbox>');
                $input.append($option);
                item.options = item.options || [];
                $option.attr('v-for', 'option in items[' + index + '].options');
            }
            $input.attr('v-model', 'form.' + item.name);
            $input.attr('v-on:change', "change($event, '" + item.name + "')");
            $input.addClass(item['class-name']);

            if (data[item.name] == null) {
                data[item.name] = '';
            }

            $item.append($input);
            $form.append($item);
        });

        let $html = $('<div class=" " />');
        if (options['before-html']) {
            $html.append(options['before-html']);
        }
        $html.append($form);
        if (options['after-html']) {
            $html.append(options['after-html']);
        }
        if (options.onClose == null) {
            options.onClose = function (arg) {
                if (arg) {
                    if (!vue.valid) {
                        return false;
                    }
                }
            };
        }



        let vue = new Vue({
            el: $html[0],
            data() {
                return {
                    form: data,
                    rules: options.rules || {},
                    valid: true,
                    items: options.items
                }
            },
            methods: {
                change(value, name) {
                    this.validate();
                },
                validate() {
                    this.$refs['form'].validate(valid => {
                        this.valid = valid;
                    });
                }
            }
        });
        $(vue.$el).find('textarea').css('max-height', '150px').css('height', '150px');
        return coos.confirm(vue.$el, options);
    };

})();

export default app;