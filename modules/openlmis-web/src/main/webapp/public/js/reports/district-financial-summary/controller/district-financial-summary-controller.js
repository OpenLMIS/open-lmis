
function DistrictFinancialSummaryControllers( $scope, $window, DistrictFinancialSummaryReport ) {

    $scope.OnFilterChanged = function(){
        // clear old data if there was any
        $scope.data = $scope.datarows = [];
        DistrictFinancialSummaryReport.get($scope.filter, function (data) {
            if (data.pages !== undefined && data.pages.rows !== undefined) {
                $scope.data = data.pages.rows;
                $scope.paramsChanged($scope.tableParams);
            }
        });
    };

    $scope.exportReport = function (type) {
        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.filter);
        var url = '/reports/download/district_financial_summary/' + type + '?' + params;
        $window.open(url);
    };
}
