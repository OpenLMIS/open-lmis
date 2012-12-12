function NavigationController($scope) {
  $scope.showSubmenu = function() {
    $(".navigation > ul > li").on("click", function() {
      $(this).find("ul").show();
    });
  }();
}