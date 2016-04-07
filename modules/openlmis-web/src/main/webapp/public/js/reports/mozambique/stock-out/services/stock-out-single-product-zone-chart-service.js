services.factory('StockoutSingleProductZoneChartService', function () {

    function dateRangeToArray(start, end) {
        var dates = [];
        for (var day = new Date(start); day <= end; day.setDate(day.getDate() + 1)) {
            dates.push(new Date(day));
        }
        return dates;
    }

    function getCarryingFacilitiesAtDay(day, zone, carryStartDates) {
        var carryingAtDay = _.filter(carryStartDates, function (carryStartDate) {
            var isCarrying = new Date(carryStartDate["dates.carry_start_date"]) <= day;
            return carryStartDate[zone.zonePropertyName] == zone.zoneCode && isCarrying;
        });
        return _.pluck(carryingAtDay, "facility.facility_name");
    }

    function getStockoutFacilitiesAtDay(day, stockoutsInZone) {
        var stockoutAtDay = _.filter(stockoutsInZone, function (stockoutEvent) {
            return new Date(stockoutEvent["stockout.date"]) <= day && new Date(stockoutEvent["stockout.resolved_date"]) >= day;
        });
        return _.uniq(_.pluck(stockoutAtDay, "facility.facility_name"));
    }

    function renderZoneChart(chartData, divId) {
        function makeBalloon(item, graph) {
            var stockOutFacilities = item.dataContext.stockOutFacilities;
            var carryingFacilities = item.dataContext.carryingFacilities;
            var percentage = item.values.value;

            if (carryingFacilities.length == 0) {
                return "None of the facilities started carrying this drug yet";
            } else if (percentage == 0) {
                return "None of the facilities had stock out"
                    + "<br>"
                    + carryingFacilities.join(", ");
            } else {
                return percentage + "% <br>"
                    + stockOutFacilities.length + " / " + carryingFacilities.length
                    + '<br><span style="color: red;">'
                    + _.intersection(carryingFacilities, stockOutFacilities).join(", ") + "</span><br>"
                    + _.difference(carryingFacilities, stockOutFacilities).join(", ");
            }
        }

        AmCharts.makeChart(divId, {
            "type": "serial",
            "theme": "light",
            "dataProvider": chartData,
            "valueAxes": [{
                title: "Stock out percentage",
                maximum: 100,
                minimum: 0
            }],
            "graphs": [{
                "title": "stockouts",
                "valueField": "percentage",
                "lineAlpha": 0,
                "fillAlphas": 0.5,
                "lineColor": "#FF0000",
                // "type": "smoothedLine",
                "balloonFunction": makeBalloon

            }],
            balloon: {textAlign: "left"},
            "categoryField": "date",
            "categoryAxis": {
                "parseDates": true,
                "dateFormats": [{
                    period: 'DD',
                    format: 'DD.MM.YYYY'
                }, {
                    period: 'WW',
                    format: 'DD.MM.YYYY'
                }, {
                    period: 'MM',
                    format: 'MM.YYYY'
                }, {
                    period: 'YYYY',
                    format: 'YYYY'
                }],
                "axisColor": "#DADADA",
                "minorGridEnabled": true
            },
            chartCursor: {
                enabled: true,
                categoryBalloonDateFormat: "YYYY.MM.DD"
            }
        });

    }

    function generateChartDataItemsForZone(zone, start, end, stockOuts, carryStartDates) {
        var userSelectedDays = dateRangeToArray(start, end);
        var stockoutsInZone = _.chain(stockOuts)
            .filter(function (stockout) {
                return stockout[zone.zonePropertyName] == zone.zoneCode;
            })
            .uniq(function (stockout) {
                return stockout["facility.facility_code"] + stockout["stockout.date"] + stockout["stockout.resolved_date"]
            })
            .sortBy("facility.facility_name")
            .value();

        return _.map(userSelectedDays, function (day) {
            var stockOutFacilities = getStockoutFacilitiesAtDay(day, stockoutsInZone);
            var carryingFacilities = getCarryingFacilitiesAtDay(day, zone, carryStartDates);
            var percentage = 0;
            if (stockOutFacilities.length > 0) {
                percentage = ((stockOutFacilities.length / carryingFacilities.length) * 100).toFixed(0);
            }

            return {
                date: day,
                percentage: percentage,
                stockOutFacilities: stockOutFacilities,
                carryingFacilities: carryingFacilities
            };
        });
    }

    function makeStockoutChartForZone(zone, divId, start, end, stockOuts, carryStartDates) {
        var chartDataItems = generateChartDataItemsForZone(zone, start, end, stockOuts, carryStartDates);
        renderZoneChart(chartDataItems, divId);
    }

    return {
        makeStockoutChartForZone: makeStockoutChartForZone,
        generateChartDataItemsForZone: generateChartDataItemsForZone
    };
});