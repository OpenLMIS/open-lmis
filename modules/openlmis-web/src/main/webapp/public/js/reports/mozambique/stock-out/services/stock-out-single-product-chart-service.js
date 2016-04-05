services.factory('StockoutSingleProductChartService', function () {
    function dateRangeToArray(start, end) {
        var dates = [];
        for (var day = new Date(start); day <= end; day.setDate(day.getDate() + 1)) {
            dates.push(new Date(day));
        }
        return dates;
    }

    function getFacilityStockOutDays(facilityCode, stockoutEvents) {
        return _.chain(stockoutEvents)
            .filter(function (stockOutEvent) {
                return stockOutEvent["facility.facility_code"] == facilityCode;
            })
            .map(function (stockOutEvent) {
                return dateRangeToArray(new Date(stockOutEvent["stockout.date"]), new Date(stockOutEvent["stockout.resolved_date"]))
            })
            .flatten()
            .uniq(function (date) {
                return date.getTime();
            })
            .value();
    }

    function generateChartDataItems(start, end, facility, stockoutEvents) {
        var userSelectedDays = dateRangeToArray(start, end);
        var facilityCoveredDays = getFacilityStockOutDays(facility.code, stockoutEvents);

        return _.chain(userSelectedDays)
            .map(function (day) {
                var stockOutBarHeight = 1;
                var match = _.find(facilityCoveredDays, function (coveredDay) {
                    return coveredDay.getTime() == day.getTime();
                });
                if (match == undefined) {
                    stockOutBarHeight = 0;
                }
                return {
                    date: day,
                    stockOutBarHeight: stockOutBarHeight
                };
            }).value();
    }

    function renderFacilityStockoutChart(facilityName, chartData, divId) {
        function makeBaloon(item, graph) {
            var value = item.values.value;
            if (value == 0) {
                return graph.title + " had no stock out";
            } else {
                return graph.title + " stocked out";
            }
        }

        AmCharts.makeChart(divId, {
            "type": "serial",
            "theme": "light",
            "dataProvider": chartData,
            "valueAxes": [{
                color: "#ffffff",
                gridColor: "#ffffff"
            }],
            "graphs": [{
                "title": facilityName,
                "valueField": "stockOutBarHeight",
                "type": "step",
                "lineAlpha": 0,
                "fillAlphas": 0.5,
                "lineColor": "#FF0000",
                "balloonFunction": makeBaloon
            }],
            "categoryField": "date",
            "categoryAxis": {
                "parseDates": true,
                "dateFormats": [{
                    period: 'DD',
                    format: 'YYYY.MM.DD'
                }, {
                    period: 'WW',
                    format: 'YYYY.MM.DD'
                }, {
                    period: 'MM',
                    format: 'YYYY.MM'
                }, {
                    period: 'YYYY',
                    format: 'YYYY'
                }],
                "axisColor": "#DADADA",
                "minorGridEnabled": true
            },
            "chartScrollbar": {
                "oppositeAxis": false,
                "offset": 30,
                "scrollbarHeight": 40,
                "backgroundAlpha": 0,
                "selectedBackgroundAlpha": 0.1,
                "selectedBackgroundColor": "#888888",
                "graphFillAlpha": 0,
                "graphLineAlpha": 0.5,
                "selectedGraphFillAlpha": 0,
                "selectedGraphLineAlpha": 1,
                "autoGridCount": true,
                "color": "#AAAAAA"
            },
            chartCursor: {
                enabled: true,
                categoryBalloonDateFormat: "YYYY.MM.DD"
            }
        });
    }

    function makeStockoutChartForFacility(facility, divId, start, end, stockoutEvents) {
        var chartDataItems = generateChartDataItems(start, end, facility, stockoutEvents);
        renderFacilityStockoutChart(facility.name, chartDataItems, divId);
    }

    return {
        makeStockoutChartForFacility: makeStockoutChartForFacility,
        generateChartDataItems: generateChartDataItems
    };
});