function RequisitionHeaderController($scope, RequisitionHeader, $location, $routeParams) {
  RequisitionHeader.get({facilityId:$routeParams.facility}, function (data) {
    $scope.header = data.requisitionHeader;
  }, function () {
    $location.path($scope.$parent.sourceUrl);
  });

  $scope.periodDisplayName = function () {
    if($scope.$parent.period == undefined) return "";
    var startDate = new Date($scope.$parent.period.startDate);
    var endDate = new Date($scope.$parent.period.endDate);

    return utils.getFormattedDate(startDate) + ' - ' + utils.getFormattedDate(endDate);
  };
}