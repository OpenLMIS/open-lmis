function CreateRequisitionController($scope, Requisition, $location, $routeParams) {

  $scope.showNonFullSupply = $routeParams.supplyType == 'non-full-supply';

  $scope.baseUrl = "/create-rnr/" + $routeParams.facility + '/' + $routeParams.program +'/' + $routeParams.period;
  $scope.fullSupplyLink = $scope.baseUrl + "/full-supply?page=1";
  $scope.nonFullSupplyLink = $scope.baseUrl + "/non-full-supply?page=1";
  $scope.pageSize = 2;

  if (!$scope.$parent.rnr) {
    Requisition.get({facilityId:$routeParams.facility, programId:$routeParams.program, periodId:$routeParams.period},
      function (data) {
        if (data.rnr) {
          $scope.$parent.rnr = data.rnr;
        } else {
          $scope.$parent.error = "Requisition does not exist. Please initiate.";
          $location.path('/init-rnr');
        }
      }, function () {
      });
  }

  $scope.periodDisplayName = function () {
    if (!$scope.rnr) return;

    var startDate = new Date($scope.rnr.period.startDate);

    var endDate = new Date($scope.rnr.period.endDate);
    return utils.getFormattedDate(startDate) + ' - ' + utils.getFormattedDate(endDate);
  };

}