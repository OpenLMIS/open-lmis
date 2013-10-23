app.directive('rnrBody', function () {
  return{
    restrict: 'C',
    link: function (scope, element) {
      setTimeout(function () {
        element.css('max-height', $(window).height() + 'px');
      });

      $(window).on('resize', function () {
        element.css('max-height', $(window).height() + 'px');
      });
    }
  };
});