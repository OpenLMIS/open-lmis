app.directive('fullScreen', function () {
  return {
    restrict: 'A',
    link: function (scope, element) {
      var fullScreen = false;

      var progressFunc = function () {
        fixToolbarWidth();
        $('.rnr-body').trigger('scroll');
      };
      var completeFunc = function () {
        $('.rnr-body').trigger('scroll');
      };

      element.click(function () {
        fullScreen = !fullScreen;
        element.find('i').toggleClass('icon-resize-full icon-resize-small');
        angular.element(window).scrollTop(0);
        if (!$.browser.msie) {
          fullScreen ? angular.element('.toggleFullScreen').slideUp({'duration': 'slow', 'progress': progressFunc, complete: completeFunc}) :
            angular.element('.toggleFullScreen').slideDown({ 'duration': 'slow', 'progress': progressFunc, complete: completeFunc});
        }
        else {
          fullScreen ? angular.element('.toggleFullScreen').hide() : angular.element('.toggleFullScreen').show();
          $('.rnr-body').trigger('scroll');
        }
        fullScreen ? angular.element('.print-button').css('opacity', '1.0') : angular.element('.print-button').css('opacity', '0');
      });
    }
  };
});