function RapidTestReportController($scope, $controller) {
  $controller("BaseProductReportController", {$scope: $scope});

  $scope.$on('$viewContentLoaded', function () {
    $scope.loadProducts();
    $scope.loadHealthFacilities();
  });

}
