function RegimenSummaryControllers($scope, $filter, ngTableParams,
                                   RegimenSummaryReport , $window,DistrictFinancialSummaryReport,ReportRegimens,ReportRegimenCategories,RequisitionGroupsByProgram,ReportRegimensByCategory,ReportPeriodsByScheduleAndYear,ReportSchedules,ReportRegimenPrograms,ReportPeriods, OperationYears, SettingsByKey,localStorageService, $http, $routeParams, $location) {

    $scope.OnFilterChanged = function(){
        // clear old data if there was any
        $scope.data = $scope.datarows = [];
        RegimenSummaryReport.get($scope.filter, function (data) {
            if (data.pages !== undefined && data.pages.rows !== undefined) {
                $scope.data = data.pages.rows;
               $scope.paramsChanged($scope.tableParams);
            }
        });
    };

    $scope.exportReport = function (type) {
        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.filter);
        var url = '/reports/download/regimen_summary/' + type + '?' + params;
        $window.open(url);
    };



}
