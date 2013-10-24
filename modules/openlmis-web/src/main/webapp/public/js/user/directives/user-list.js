userModule.directive('userList', function ($timeout) {
  return {
    restrict: 'C',
    link: function () {
      $timeout(function () {
        var listAnchorElements = $(".user-list a");
        listAnchorElements.live("focus", function () {
          console.log("inside focus");
          $(".user-actions a").hide();
          $(this).parents("li").find(".user-actions a").css("display", "inline-block");
        });
        listAnchorElements.live("blur", function () {
          console.log("inside blur");
          $timeout(function () {
            if (!$('.user-list a:focus').length)
              $(".user-actions a").hide();
          });
        });
      });
    }
  };
});
