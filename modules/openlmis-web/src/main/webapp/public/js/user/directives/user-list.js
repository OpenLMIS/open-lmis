userModule.directive('userList', function ($timeout) {
  return {
    restrict: 'C',
    link: function () {
      $timeout(function () {
        var listAnchorElements = $(".user-list a");
        listAnchorElements.live("focus", function () {
          $(".user-actions a").hide();
          $(this).parents("li").find(".user-actions a").css("display", "inline-block");
        });
      });
    }
  };
});
