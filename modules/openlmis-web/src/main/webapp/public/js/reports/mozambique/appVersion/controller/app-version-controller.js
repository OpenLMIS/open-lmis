function VersionReportController($scope, VersionReportService, $cacheFactory) {
    $scope.$on('$viewContentLoaded', function () {
        $scope.loadUserSummary();
    });

    $scope.loadUserSummary = function () {
        VersionReportService.get(function (data) {
            $scope.appVersions = data.app_versions;
            $scope.sortType = 'userName';
        });
    };

    if ($cacheFactory.get('keepHistoryInStockOnHandPage') !== undefined) {
        $cacheFactory.get('keepHistoryInStockOnHandPage').put('saveDataOfStockOnHand', "no");
    }
    if ($cacheFactory.get('keepHistoryInStockOutReportPage') !== undefined) {
        $cacheFactory.get('keepHistoryInStockOutReportPage').put('saveDataOfStockOutReport', "no");
        $cacheFactory.get('keepHistoryInStockOutReportPage').put('saveDataOfStockOutReportForSingleProduct', "no");
    }

    $scope.partialPropertiesFilter = function(searchValue) {
        return function(entry) {
            var regex = new RegExp(searchValue, "gi");

            return regex.test(entry.userName) ||
                regex.test(entry.facilityName) ||
                regex.test(entry.appVersion);
        };
    };
}