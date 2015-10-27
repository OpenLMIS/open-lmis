function RequisitionReportController($scope, $filter, RequisitionReportService) {
    $scope.$on('$viewContentLoaded', function () {
        $scope.loadUserSummary();
    });

    $scope.loadUserSummary = function () {
        var requisitionQueryParameters = {
            startTime:  '2015-09-26 00:00:00',
            endTime: $filter('date')(new Date(), 'yyyy-MM-dd HH:mm:ss')
        };

        RequisitionReportService.get(requisitionQueryParameters, function (data) {
            $scope.requisitions = data.rnr_list;
        }, function () {
        });
    };
}