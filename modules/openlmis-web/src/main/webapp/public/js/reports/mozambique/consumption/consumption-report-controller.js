function ConsumptionReportController($scope, $controller) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadProducts();
        $scope.loadHealthFacilities();
    });

    $scope.generateConsumptionReport = function () {
        if ($scope.checkDateValidRange()) {
            $scope.locationIdToCode($scope.reportParams);
        }
    };

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
            if (coveredDate.getDay() < periodStartDay) {
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