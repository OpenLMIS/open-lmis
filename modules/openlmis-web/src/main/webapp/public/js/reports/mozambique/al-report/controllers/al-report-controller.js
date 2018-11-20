function ALReportController($scope, $controller) {
  $controller("BaseProductReportController", {$scope: $scope});

  $scope.$on('$viewContentLoaded', function () {
    $scope.loadHealthFacilities();
  });
  
  $scope.loadReport = function () {
    console.log('load al report controller');
  }
}
