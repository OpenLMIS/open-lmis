function SingleFacilityReportController($scope, $filter, $controller, $http, CubesGenerateCutParamsService, CubesGenerateUrlService, FeatureToggleService, $cacheFactory, $timeout) {
    $controller('BaseProductReportController', {$scope: $scope});

    if ($cacheFactory.get('keepHistoryInStockOnHandPage') === undefined) {
        $scope.cache = $cacheFactory('keepHistoryInStockOnHandPage', {capacity: 10});
    }
    else {
        $scope.cache = $cacheFactory.get('keepHistoryInStockOnHandPage');
        if ($scope.cache.get('saveDataOfStockOnHand') === "yes") {
            $timeout(function waitHistorySelectShow() {
                if ($('.select2-container .select2-choice .select2-chosen').html() !== undefined) {
                    $scope.reportParams.facilityId = $scope.cache.get('dataOfStockOnHandReport').facilityId;
                    $scope.reportParams.provinceId = $scope.cache.get('dataOfStockOnHandReport').provinceId;
                    $scope.reportParams.districtId = $scope.cache.get('dataOfStockOnHandReport').districtId;
                    $scope.reportParams.endTime = $filter('date')($scope.cache.get('dataOfStockOnHandReport').endTime, "yyyy-MM-dd");
                    loadReportAction();
                } else {
                    $timeout(waitHistorySelectShow, 1000);
                }
            }, 1000);
        }
    }

    $scope.$on('$viewContentLoaded', function () {

        FeatureToggleService.get({key: 'view.stock.movement'}, function (result) {
            $scope.viewStockMovementToggle = result.key;
        });

        $scope.loadHealthFacilities();
    });

    $scope.loadReport = function () {
        if (validateFacility()) {
            var params = $scope.reportParams;
            $scope.locationIdToCode(params);
            var cutsParams = CubesGenerateCutParamsService.generateCutsParams("occurred", undefined, $filter('date')(params.endTime, "yyyy,MM,dd"),
                params.selectedFacility, undefined, params.selectedProvince, params.selectedDistrict);

            $http.get(CubesGenerateUrlService.generateFactsUrl('vw_daily_full_soh', cutsParams)).success(function (sohEntries) {
                $scope.reportData = _.chain(sohEntries)
                    .groupBy(function (sohEntry) {
                        return sohEntry['drug.drug_code'];
                    })
                    .map(function (sameCodeEntries) {
                        var maxOccurredDateEntry = _.max(sameCodeEntries, function (entry) {
                            return new Date(entry.occurred_date);
                        });
                        maxOccurredDateEntry.soh = Number(maxOccurredDateEntry.soh);
                        return maxOccurredDateEntry;
                    })
                    .value();
            });
        }
    };

    $scope.saveHistory = function () {
        $scope.cache.put('dataOfStockOnHandReport', $scope.reportParams);
        console.log($scope.reportParams);
    };

    function validateFacility() {
        $scope.invalid = !$scope.reportParams.facilityId;
        return !$scope.invalid;
    }
}