function RequisitionHeaderController($scope, RequisitionHeader, $location, $routeParams) {
    RequisitionHeader.get({facilityId:$routeParams.facility}, function (data) {
        $scope.header = data.requisitionHeader;
    }, function () {
        $location.path($scope.$parent.sourceUrl);
    });


}