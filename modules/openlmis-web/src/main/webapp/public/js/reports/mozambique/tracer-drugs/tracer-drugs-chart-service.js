services.factory('TracerDrugsChartService', function ($http, $filter, $q, messageService, CubesGenerateUrlService, StockoutSingleProductZoneChartService) {

    function getTracerDrugStockRateOnFriday(zone, friday, stockOuts, tracerDrugCode, carryStartDates) {
        var stockOutsOfTracerDrug = _.filter(stockOuts, function (stockOut) {
            return stockOut["drug.drug_code"] === tracerDrugCode;
        });
        var carryStartDatesOfTracerDrug = _.filter(carryStartDates, function (carry) {
            return carry["drug.drug_code"] === tracerDrugCode;
        });

        return StockoutSingleProductZoneChartService.generateChartDataItemsForZone(zone, friday, friday, stockOutsOfTracerDrug, carryStartDatesOfTracerDrug)[0];
    }

    function getCubesRequestPromise(tracerDrugs, provinceCode, districtCode, userSelectedStartDate, userSelectedEndDate, cubesName, timeDimensionName) {
        function addLocaltionCut(cuts) {
            var locationConfig = getUserSelectedZoneConfig(provinceCode, districtCode);
            if (locationConfig.isOneDistrict) {
                cuts.push({dimension: "location", values: [[provinceCode, districtCode]]});
            } else if (locationConfig.isOneProvince) {
                cuts.push({dimension: "location", values: [[provinceCode]]});
            }
        }

        var cuts = [{
            dimension: "drug",
            values: _.pluck(tracerDrugs, "drug.drug_code")
        }, {
            dimension: timeDimensionName,
            values: [$filter('date')(userSelectedStartDate, "yyyy,MM,dd") + "-" + $filter('date')(userSelectedEndDate, "yyyy,MM,dd")]
        }];

        addLocaltionCut(cuts);

        var requestUrl = CubesGenerateUrlService.generateFactsUrl(cubesName, cuts);

        return $http.get(requestUrl);
    }

    function getFridaysBetween(start, end) {
        var dates = [];
        for (var day = new Date(start); day <= end; day.setDate(day.getDate() + 1)) {
            var isFriday = day.getDay() == 5;
            if (isFriday) {
                dates.push(new Date(day));
            }
        }
        return dates;
    }

    function getUserSelectedZoneConfig(provinceCode, districtCode) {
        var isOneDistrict = provinceCode === undefined && districtCode !== undefined;
        var isOneProvince = provinceCode !== undefined && districtCode === undefined;
        var isAllProvinces = provinceCode === undefined && districtCode === undefined;
        return {isOneDistrict: isOneDistrict, isOneProvince: isOneProvince, isAllProvinces: isAllProvinces};
    }

    function getZone(provinceCode, districtCode) {
        var locationConfig = getUserSelectedZoneConfig(provinceCode, districtCode);

        if (locationConfig.isOneDistrict) {
            return {
                zoneCode: districtCode,
                zonePropertyName: "location.district_code"
            };
        }
        else if (locationConfig.isOneProvince) {
            return {
                zoneCode: provinceCode,
                zonePropertyName: "location.province_code"
            };
        }
        else if (locationConfig.isAllProvinces) {
            return undefined;
        }
    }

    function generateTracerDurgDataItemForOneFriday(friday, tracerDrugs, provinceCode, districtCode, stockOuts, carryStartDates) {
        var chartDataItem = {date: friday};

        var totalPercentage = 0;
        _.forEach(tracerDrugs, function (tracerDrug) {
            var tracerDrugCode = tracerDrug["drug.drug_code"];

            var fridayStockOutRate = getTracerDrugStockRateOnFriday(getZone(provinceCode, districtCode), friday, stockOuts, tracerDrugCode, carryStartDates);
            var hasStockPercentage = 100 - fridayStockOutRate.percentage;
            chartDataItem[tracerDrugCode] = hasStockPercentage;
            chartDataItem[tracerDrugCode + "StockOutFacilities"] = fridayStockOutRate.stockOutFacilities;
            chartDataItem[tracerDrugCode + "CarryingFacilities"] = fridayStockOutRate.carryingFacilities;
            totalPercentage += hasStockPercentage;
        });
        chartDataItem.average = (totalPercentage / tracerDrugs.length).toFixed(0);

        return chartDataItem;
    }

    function generateTracerDrugsChartDataItems(tracerDrugs, stockOuts, carryStartDates, userSelectedStartDate, userSelectedEndDate, provinceCode, districtCode) {
        var fridays = getFridaysBetween(userSelectedStartDate, userSelectedEndDate);
        return _.chain(fridays)
            .map(function (friday) {
                return generateTracerDurgDataItemForOneFriday(friday, tracerDrugs, provinceCode, districtCode, stockOuts, carryStartDates);
            }).value();
    }

    function makeTracerDrugsChart(divId, userSelectedStartDate, userSelectedEndDate, provinceCode, districtCode) {
        $http.get('/cubesreports/cube/products/facts?cut=is_tracer:true').success(function (tracerDrugs) {
            var stockOutPromise = getCubesRequestPromise(tracerDrugs, provinceCode, districtCode, userSelectedStartDate, userSelectedEndDate, "vw_stockouts", "overlapped_date");
            var carryStartDatesPromise = getCubesRequestPromise(tracerDrugs, provinceCode, districtCode, "", userSelectedEndDate, "vw_carry_start_dates", "carry_start");

            $q.all([stockOutPromise, carryStartDatesPromise]).then(function (arrayOfResults) {
                var stockOuts = arrayOfResults[0].data;
                var carryStartDates = arrayOfResults[1].data;
                var chartDataItems = generateTracerDrugsChartDataItems(tracerDrugs, stockOuts, carryStartDates, userSelectedStartDate, userSelectedEndDate, provinceCode, districtCode);

                renderTracerDrugsChart(divId, chartDataItems, tracerDrugs);
            });
        });
    }

    function generateGraphs(tracerDrugs) {
        function stringToRGB(str) {
            var hash = 0;
            for (var i = 0; i < str.length; i++) {
                hash = str.charCodeAt(i) + ((hash << 5) - hash);
            }

            var c = (hash & 0x00FFFFFF)
                .toString(16)
                .toUpperCase();

            return "#" + "00000".substring(0, 6 - c.length) + c;
        }

        var tracerDrugGraphs = _.chain(tracerDrugs)
            .map(function (tracerDrug) {
                var tracerDrugcode = tracerDrug["drug.drug_code"];
                var tracerDrugName = tracerDrug["drug.drug_name"];

                return {
                    lineColor: stringToRGB(tracerDrugcode + tracerDrugName),
                    bullet: "round",
                    title: tracerDrugName,
                    valueField: tracerDrugcode
                };
            }).value();

        tracerDrugGraphs.push({
            lineColor: stringToRGB("AVERAGE"),
            bullet: "round",
            title: "Average",
            valueField: "average",
            dashLength: 5
        });

        return tracerDrugGraphs;
    }

    function renderTracerDrugsChart(divId, chartDataItems, tracerDrugs) {
        var graphs = generateGraphs(tracerDrugs);

        AmCharts.makeChart(divId, {
            "type": "serial",
            "theme": "light",
            "legend": {
                "useGraphSettings": true
            },
            "dataProvider": chartDataItems,
            "valueAxes": [{
                "axisThickness": 2,
                "gridAlpha": 0,
                "axisAlpha": 1,
                "position": "left"
            }],
            "graphs": graphs,
            "chartScrollbar": {},
            "chartCursor": {
                "cursorPosition": "mouse"
            },
            "categoryField": "date",
            "categoryAxis": {
                "parseDates": true,
                "axisColor": "#DADADA",
                "minorGridEnabled": true
            }
        });
    }

    return {
        generateGraphs: generateGraphs,
        generateTracerDrugsChartDataItems: generateTracerDrugsChartDataItems,
        makeTracerDrugsChart: makeTracerDrugsChart
    };
});