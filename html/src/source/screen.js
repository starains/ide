
(function () {
    let $screen = $('<div class="app-screen-message-box"></div>');
    $screen.append('<div class="app-screen-message" />');
    $screen.find('.app-screen-message').append('<div class="app-screen-message-img" ></div>');
    $screen.find('.app-screen-message').append('<div class="app-screen-message-text" ></div>');
    source.screen = {
        show() {
            if ($('.app-screen-message-box').length == 0) {
                $screen.find('.app-screen-message-img').css({ backgroundImage: 'url(' + _SERVER_URL + 'resources/images/loading.gif)' });
                $('body').append($screen);
                window.setTimeout(() => {
                    $screen.addClass('app-screen-message-show');
                }, 10);
            }
        },
        error(message) {
            source.screen.show();

            $screen.find('.app-screen-message-text').empty();
            let $text = $('<div />');
            $text.append(message);
            $screen.find('.app-screen-message-text').append($text);
        },
        success(message) {

            source.screen.show();

            $screen.find('.app-screen-message-text').empty();
            let $text = $('<div />');
            $text.append(message);
            $screen.find('.app-screen-message-text').append($text);
        },
        remove() {
            window.setTimeout(() => {
                $screen.removeClass('app-screen-message-show');
            }, 10);
        }
    };

})();
export default source;