app.directive('fullScreen', function () {
  return {
    restrict: 'A',
    link: function (scope, element) {
      var fullScreen = false;

      var progressFunc = function () {
        fixToolbarWidth();
        $(window).trigger('scroll');
      };

      element.click(function () {
        fullScreen = !fullScreen;
        element.find('i').toggleClass('icon-resize-full', !fullScreen);
        element.find('i').toggleClass('icon-resize-small', fullScreen);
        angular.element(window).scrollTop(0);
        if (!$.browser.msie) {
          fullScreen ? angular.element('.toggleFullScreen').slideUp({'duration': 'slow', 'progress': progressFunc, complete: function () {
            $(window).trigger('scroll');
          }}) :
            angular.element('.toggleFullScreen').slideDown({ 'duration': 'slow', 'progress': progressFunc, complete: function () {
              $(window).trigger('scroll')
            }});
        }
        else {
          fullScreen ? angular.element('.toggleFullScreen').hide() : angular.element('.toggleFullScreen').show();
        }
        fullScreen ? angular.element('.print-button').css('opacity', '1.0') : angular.element('.print-button').css('opacity', '0');
      })
    }
  }
});