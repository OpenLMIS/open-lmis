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
            cutParams.push({dimension: 'reason_code', values: ['ISSUE', 'NO_MOVEMENT_IN_PERIOD']});
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

    $scope.splitPeriods = function (start, end) {
        var previousMonth = -1, thisMonth = 0, nextMonth = 1, periodStartDay = 21, periodEndDay = 20;

        function shiftMonthAtDay(date, shiftMonth, atDay) {
            var resultDate = new Date(date);
            resultDate.setMonth(date.getMonth() + shiftMonth);
            resultDate.setDate(atDay);
            return resultDate;
        }

        function periodOf(date) {
            var coveredDate = new Date(date);
            var periodStart, periodEnd;
            if (coveredDate.getDate() < periodStartDay) {
                periodStart = shiftMonthAtDay(coveredDate, previousMonth, periodStartDay);
                periodEnd = shiftMonthAtDay(coveredDate, thisMonth, periodEndDay);
            } else {
                periodStart = shiftMonthAtDay(coveredDate, thisMonth, periodStartDay);
                periodEnd = shiftMonthAtDay(coveredDate, nextMonth, periodEndDay);
            }
            return {periodStart: periodStart, periodEnd: periodEnd};
        }

        function nextPeriod(period) {
            return {
                periodStart: shiftMonthAtDay(period.periodStart, nextMonth, periodStartDay),
                periodEnd: shiftMonthAtDay(period.periodEnd, nextMonth, periodEndDay)
            };
        }

        function periodsInBetween(first, last) {
            var periods = [first];

            var next = nextPeriod(first);
            while (next.periodStart.getTime() <= last.periodStart.getTime()) {
                periods.push(next);
                next = nextPeriod(next);
            }

            return periods;
        }

        return periodsInBetween(periodOf(start), periodOf(end));
    };
}