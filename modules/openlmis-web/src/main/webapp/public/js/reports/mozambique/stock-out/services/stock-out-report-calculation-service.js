services.factory('StockOutReportCalculationService', function () {
    var overlapMonthKey = "overlapped_month";
    var stockoutStartDateKey = "stockout.date";
    var stockoutEndDateKey = "stockout.resolved_date";

    function calculateStockoutResult(stockOuts, occurrences) {
        var numberOfMonths = _.uniq(_.pluck(stockOuts, overlapMonthKey)).length;

        if (numberOfMonths === 0) {
            return {avgDuration: 0, totalOccurrences: 0, totalDuration: 0};
        }

        var sumsDuration = _.chain(stockOuts)
            .groupBy(overlapMonthKey)
            .map(function (stockOutsInSameMonth) {
                return _.reduce(stockOutsInSameMonth, function (memo, stockOut) {
                    memo += stockOut.overlap_duration;
                    return memo;
                }, 0);
            })
            .reduce(function (memo, totalDurationOfMonth) {
                memo += totalDurationOfMonth;
                return memo;
            }, 0)
            .value();

        return {
            avgDuration: (sumsDuration / occurrences).toFixed(1),
            totalOccurrences: occurrences,
            totalDuration: sumsDuration
        };
    }

    function generateIncidents(stockOuts) {
        return _.uniq(_.map(stockOuts, function (stockout) {
            return stockout[stockoutStartDateKey] + " to " + stockout[stockoutEndDateKey];
        }));
    }

    return {
        calculateStockoutResult: calculateStockoutResult,
        generateIncidents: generateIncidents
    };
});
