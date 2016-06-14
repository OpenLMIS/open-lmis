function AdjustmentOccurrencesReportController($scope, $controller) {
  $controller('BaseProductReportController', {$scope: $scope});

  $scope.adjustmentTypes = [
    {value: 'negative', name: 'Negative Adjustment'},
    {value: 'positive', name:'Positive Adjustment'}
  ];

  $scope.selectedAdjustmentType= '';

  $scope.$on('$viewContentLoaded', function () {
    $scope.loadProducts();
    $scope.loadHealthFacilities();
  });

  $scope.generateAdjustmentReport = function () {

  };


}