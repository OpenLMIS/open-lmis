function RequisitionController($scope, Requisition, $location, $routeParams) {
  if (!$scope.$parent.rnr) {
    Requisition.get({facilityId:$routeParams.facility, programId:$routeParams.program, periodId:$routeParams.period},
      function (data) {
        if (data.rnr) {
          $scope.rnr = data.rnr;
        } else {
          $scope.$parent.error = "Requisition does not exist. Please initiate.";
          $location.path('/init-rnr');
        }
      }, function () {
      });
  }

  $scope.periodDisplayName = function () {
    if(!$scope.rnr) return;

    var startDate = new Date($scope.rnr.period.startDate);
    var endDate = new Date($scope.rnr.period.endDate);

    return utils.getFormattedDate(startDate) + ' - ' + utils.getFormattedDate(endDate);
  };
}