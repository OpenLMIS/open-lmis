function ALReportController($scope, $controller) {
  $controller("BaseProductReportController", {$scope: $scope});

  $scope.$on('$viewContentLoaded', function () {
  });
}
