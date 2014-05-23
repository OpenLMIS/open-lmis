/**
 * Created by issa on 4/22/14.
 */

function ReportingPerformanceDetailController($scope,$routeParams,$filter, ReportingPerformanceDetail,ngTableParams) {

    $scope.$parent.currentTab = 'REPORTING-PERFORMANCE-DETAIL';
    $scope.$on('$viewContentLoaded', function () {
        if(!isUndefined($routeParams.programId) &&
            !isUndefined($routeParams.periodId)){
            ReportingPerformanceDetail.get({
                periodId: $routeParams.periodId,
                rgroupId: $routeParams.rgroupId,
                programId: $routeParams.programId,
                status: $routeParams.status
            },function(data){
                if(!isUndefined(data.reporting)){
                    $scope.reportingDetails = data.reporting;
                    setupTableOption();
                }else{
                    $scope.resetReportingData();
                }
            });
        } else{
            $scope.resetReportingData();
        }

        //
    });
    $scope.resetReportingData = function(){
        $scope.reportingDetails = null;
    };

    function setupTableOption(){
        // the grid options
        $scope.tableParams = new ngTableParams({
            page: 1,            // show first page
            total: 0,           // length of data
            count: 25           // count per page
        });

        $scope.paramsChanged = function(params) {

            // slice array data on pages
            if($scope.reportingDetails === undefined ){
                $scope.reportingData = [];
                params.total = 0;
            }else{
                var data = $scope.reportingDetails;
                var orderedData = params.filter ? $filter('filter')(data, params.filter) : data;
                orderedData = params.sorting ?  $filter('orderBy')(orderedData, params.orderBy()) : data;

                params.total = orderedData.length;
                $scope.reportingData = orderedData.slice( (params.page - 1) * params.count,  params.page * params.count );

            }
        };

        $scope.$watch('tableParams', $scope.paramsChanged , true);

    }

}