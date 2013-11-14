userModule.directive('userList', function ($timeout) {
  return {
    restrict: 'C',
    link: function (scope, element, attr) {

      scope.$watch(attr.ngModel, function ngModelWatchAction() {
        $('.user-actions a').css('display', 'none');
        var showButtonsOnLiHover = function(){
          var listAnchorElements = $(".user-list a");
          listAnchorElements.live("focus", function () {
            $(".user-actions a").hide();
            $(this).parents("li").find(".user-actions a").css("display", "inline-block");
          });
        };
        $timeout(showButtonsOnLiHover);
      });
    }
  };
});
