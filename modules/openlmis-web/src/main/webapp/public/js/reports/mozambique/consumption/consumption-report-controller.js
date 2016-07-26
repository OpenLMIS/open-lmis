function ConsumptionReportController($scope, $controller, $filter, $http, $q, CubesGenerateCutParamsService, CubesGenerateUrlService, DateFormatService, messageService) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadProducts();
        $scope.loadHealthFacilities();
    });

    $scope.generateConsumptionReport = function () {
        if ($scope.checkDateValidRange() && validateProduct()) {
            $scope.locationIdToCode($scope.reportParams);
            var promises = requestConsumptionDataForEachPeriod();
            $q.all(promises).then(function (consumptionsInPeriods) {
                renderConsumptionChart(_.pluck(_.pluck(consumptionsInPeriods, 'data'), 'summary'));
            });
        }
    };

    function renderConsumptionChart(consumptionInPeriods) {
        AmCharts.makeChart("consumption-report", {
            "type": "serial",
            "theme": "light",
            "dataProvider": consumptionInPeriods,
            "graphs": [{
                "bullet": "round",
                "valueField": "soh",
                "balloonText": messageService.get("stock.movement.soh") + ": [[value]]"
            }, {
                "bullet": "round",
                "valueField": "cmm",
                "balloonText": "CMM: [[value]]"
            }, {
                "bullet": "round",
                "valueField": "total_quantity",
                "balloonText": messageService.get("consumption.chart.balloon.text") + ": [[value]]"
            }],
            "chartScrollbar": {
                "oppositeAxis": false,
                "offset": 30
            },
            "chartCursor": {},
            "categoryField": "period"
        });
    }

    function requestConsumptionDataForEachPeriod() {
        var periodsInSelectedRange = $scope.splitPeriods($scope.reportParams.startTime, $scope.reportParams.endTime);
        return _.map(periodsInSelectedRange, function (period) {
            var cutParams = CubesGenerateCutParamsService.generateCutsParams("periodstart",
                $filter('date')(period.periodStart, "yyyy,MM,dd"),
                $filter('date')(period.periodStart, "yyyy,MM,dd"),
                $scope.reportParams.selectedFacility,
                [{"drug.drug_code": $scope.reportParams.productCode}],
                $scope.reportParams.selectedProvince,
                $scope.reportParams.selectedDistrict
            );
            cutParams.push({dimension: 'reason_code', values: [
                'UNPACK_KIT',
                'DEFAULT_ISSUE',
                'PUB_PHARMACY',
                'MATERNITY',
                'GENERAL_WARD',
                'ACC_EMERGENCY',
                'MOBILE_UNIT',
                'LABORATORY',
                'UATS',
                'PNCTL',
                'PAV',
                'DENTAL_WARD',
                'ISSUE',
                'NO_MOVEMENT_IN_PERIOD'
            ]});
            return $http
                .get(CubesGenerateUrlService.generateAggregateUrl("vw_period_movements", [], cutParams))
                .then(function (consumptionData) {
                    consumptionData.data.summary.period =
                        DateFormatService.formatDateWithLocale(period.periodStart) +
                        "-" +
                        DateFormatService.formatDateWithLocale(period.periodEnd);
                    return consumptionData;
                });
        });
    }

    function validateProduct() {
        $scope.noProductSelected = !$scope.reportParams.productCode;
        return !$scope.noProductSelected;
    }

}