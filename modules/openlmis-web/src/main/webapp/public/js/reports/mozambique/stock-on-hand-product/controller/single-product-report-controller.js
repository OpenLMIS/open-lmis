function SingleProductReportController($scope, $filter, $controller, $http, CubesGenerateCutParamsService, CubesGenerateUrlService) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadProducts();
    });

    $scope.loadReport = function () {
        if (validateProduct()) {
            var params = $scope.reportParams;
            $scope.locationIdToCode(params);
            var selectedProduct = [{'drug.drug_code': $scope.reportParams.productCode}];
            var cutsParams = CubesGenerateCutParamsService.generateCutsParams("occurred", undefined, $filter('date')(params.endTime, "yyyy,MM,dd"),
                params.selectedFacility, selectedProduct, params.selectedProvince, params.selectedDistrict);

            $http.get(CubesGenerateUrlService.generateFactsUrl('vw_daily_full_soh', cutsParams)).success(function (sohEntries) {
                $scope.reportData = _.chain(sohEntries)
                    .groupBy(function (sohEntry) {
                        return sohEntry['drug.drug_code'] + sohEntry['facility.facility_code'];
                    })
                    .map(function (sameFacilitySameDrugEntries) {
                        var maxOccurredDateEntry = _.max(sameFacilitySameDrugEntries, function (entry) {
                            return new Date(entry.occurred_date);
                        });
                        maxOccurredDateEntry.soh = Number(maxOccurredDateEntry.soh);
                        return maxOccurredDateEntry;
                    })
                    .value();
            });
        }
    };

    function validateProduct() {
        $scope.invalid = !$scope.reportParams.productCode;
        return !$scope.invalid;
    }

}