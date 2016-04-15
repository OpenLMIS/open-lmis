services.factory('TracerDrugsChartService', function ($http, $filter, $q, messageService, CubesGenerateUrlService, StockoutSingleProductZoneChartService) {

    function getTracerDrugStockRateOnFriday(zone2, friday, stockOuts, tracerDrug, carryStartDates) {
        return StockoutSingleProductZoneChartService.generateChartDataItemsForZone(zone2, friday, friday, _.filter(stockOuts, function (stockOut) {
            return stockOut["drug.drug_code"] === tracerDrug.drug;
        }), _.filter(carryStartDates, function (carry) {
            return carry["drug.drug_code"] === tracerDrug.drug;
        }));
    }

    function getCubesRequestPromise(tracerDrugs, provinceCode, districtCode, userSelectedStartDate, userSelectedEndDate, cubesName, timeDimensionName) {
        var requestUrl = CubesGenerateUrlService.generateFactsUrl(cubesName, [{
            dimension: "drug",
            values: _.pluck(tracerDrugs, "drug")
        }, {dimension: "location", values: [[provinceCode, districtCode]]}, {
            dimension: timeDimensionName,
            values: [$filter('date')(userSelectedStartDate, "yyyy,MM,dd") + "-" + $filter('date')(userSelectedEndDate, "yyyy,MM,dd")]
        }]);
        return $http.get(requestUrl);
    }

    function getFridaysBetween(start, end) {
        var dates = [];
        for (var day = new Date(start); day <= end; day.setDate(day.getDate() + 1)) {
            if (day.getDay() == 5) {
                dates.push(new Date(day));
            }
        }
        return dates;
    }

    function getZone(provinceCode, districtCode) {
        if (districtCode !== undefined) {
            return {
                zoneCode: districtCode,
                zonePropertyName: "location.district_code"
            };
        } else {
            return {
                zoneCode: provinceCode,
                zonePropertyName: "location.province_code"
            };
        }
    }

    function generateTracerDrugsChartDataItems(tracerDrugs, stockOuts, carryStartDates, userSelectedStartDate, userSelectedEndDate, provinceCode, districtCode) {
        var fridays = getFridaysBetween(userSelectedStartDate, userSelectedEndDate);

        return _.chain(fridays).map(function (friday) {
            var chartDataItem = {date: friday};

            var totalPercentage = 0;
            _.forEach(tracerDrugs, function (tracerDrug) {
                var fridayStockOutRate = getTracerDrugStockRateOnFriday(getZone(provinceCode, districtCode), friday, stockOuts, tracerDrug, carryStartDates)[0];
                var hasStockPercentage = 100 - fridayStockOutRate.percentage;
                chartDataItem[tracerDrug.drug] = hasStockPercentage;
                chartDataItem[tracerDrug.drug + "StockOutFacilities"] = fridayStockOutRate.stockOutFacilities;
                chartDataItem[tracerDrug.drug + "CarryingFacilities"] = fridayStockOutRate.carryingFacilities;
                totalPercentage += hasStockPercentage;
            });
            chartDataItem.average = (totalPercentage / tracerDrugs.length).toFixed(0);
            return chartDataItem;
        }).value();
    }

    function makeTracerDrugsChart() {
        var stockOutPromise = getCubesRequestPromise(tracerDrugs, provinceCode, districtCode, userSelectedStartDate, userSelectedEndDate, "vw_stockouts", "overlap_date");
        var carryStartDatesPromise = getCubesRequestPromise(tracerDrugs, provinceCode, districtCode, "", userSelectedEndDate, "vw_carry_start_dates", "carry_start");

        $q.all([stockOutPromise, carryStartDatesPromise]).then(function (arrayOfResults) {
            stockOuts = arrayOfResults[0].data;
            carryStartDates = arrayOfResults[1].data;
            // generateTracerDrugsChartDataItems()
        });
    }

    // httpBackend.expectGET('/cubesreports/cube/products/facts?cut=is_tracer:true').respond(200, tracerDrugs);

    return {
        makeTracerDrugsChart: makeTracerDrugsChart,
        generateTracerDrugsChartDataItems: generateTracerDrugsChartDataItems
    };
});