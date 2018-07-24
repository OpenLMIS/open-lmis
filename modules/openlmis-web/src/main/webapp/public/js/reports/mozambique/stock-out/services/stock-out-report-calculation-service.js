services.factory('StockOutReportCalculationService', function () {
    var overlapMonthKey = "overlapped_month";
    var stockoutStartDateKey = "stockout.date";
    var stockoutEndDateKey = "stockout.resolved_date";
    var isResolvedKey = "is_resolved";

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

    function newCalculateStockoutResult(stockOuts, startTime, endTime, occurrences) {
        var numberOfMonths = _.uniq(_.pluck(stockOuts, overlapMonthKey)).length;

        if (numberOfMonths === 0) {
            return {avgDuration: 0, totalOccurrences: 0, totalDuration: 0};
        }

        var sumsDuration = _.chain(stockOuts)
            .groupBy(stockoutStartDateKey, stockoutEndDateKey)
            .map(function(takeFirst){return takeFirst[0];})
            .map(function(stock){return calcDuration(stock, startTime, endTime);})
            .reduce(function(total, x){return total + x;})
            .value();

        return {
            avgDuration: (sumsDuration / occurrences).toFixed(1),
            totalOccurrences: occurrences,
            totalDuration: sumsDuration
        };
    }

    function calcDuration(stock, startTime, endTime) {
        var isResolved = stock[isResolvedKey];
        var start = stock[stockoutStartDateKey];
        var end = stock[stockoutEndDateKey];
        startTime = startTime.replace(/,/,"-").replace(/,/,"-");
        endTime = endTime.replace(/,/,"-").replace(/,/,"-");

        if(isResolved === false || moment(end,"YYYY-MM-DD").diff(moment(endTime,"YYYY-MM-DD"), "days") > 0){
            end = endTime;
        }
        if(moment(start,"YYYY-MM-DD").diff(moment(startTime,"YYYY-MM-DD"), "days") < 0){
            start = startTime;
        }
        var startMomentDate = moment(start,"YYYY-MM-DD");
        var endMomentDate = moment(end,"YYYY-MM-DD");
        var duration =  endMomentDate.diff(startMomentDate, "days") + 1;
        return duration > 0 ? duration:0;
    }

    return {
        calculateStockoutResult: calculateStockoutResult,
        generateIncidents: generateIncidents,
        newCalculateStockoutResult: newCalculateStockoutResult
    };
});
