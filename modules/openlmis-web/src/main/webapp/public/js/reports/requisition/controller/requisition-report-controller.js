function RequisitionReportController($scope, RequisitionsForViewing) {
    $scope.$on('$viewContentLoaded', function () {
        $scope.loadUserSummary();
    });

    $scope.loadUserSummary = function () {
    };
    //var requisitionQueryParameters = {
    //    facilityId: $scope.selectedFacilityId,
    //    dateRangeStart: $scope.startDate,
    //    dateRangeEnd: $scope.endDate
    //};
    //RequisitionsForViewing.get(requisitionQueryParameters, function (data) {
    //    $scope.requisitions = data.rnr_list;
    //}, function () {
    //});
    //
    $scope.data = $scope.requisitions;

    $scope.requisitions = [
        {name: "Moroni", age: 50, money: 100},
        {name: "Name", age: 30, money: 80}
    ];
}