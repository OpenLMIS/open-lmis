function VersionReportController($scope, VersionReportService,$cacheFactory) {
    $scope.$on('$viewContentLoaded', function () {
        $scope.loadUserSummary();
    });

    $scope.loadUserSummary = function () {
        VersionReportService.get(function (data) {
            $scope.appVersions = data.app_versions;
            $scope.sortType = 'userName';
        });
    };

    if($cacheFactory.get('keepHistoryInStockOnHandPage') != undefined){
        $cacheFactory.get('keepHistoryInStockOnHandPage').put('saveDataOfStockOnHand',"no");
    }
    if($cacheFactory.get('BaseProductReportController') != undefined){
        $cacheFactory.get('BaseProductReportController').put('saveDataOfStockOutReport',"no");
    }
}