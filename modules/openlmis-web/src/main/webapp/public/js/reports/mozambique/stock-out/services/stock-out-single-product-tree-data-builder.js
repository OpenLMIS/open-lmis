services.factory('StockoutSingleProductTreeDataBuilder', function () {
    var facilityCodeKey = "facility.facility_code";
    var facilityNameKey = "facility.facility_name";

    var districtCodeKey = "location.district_code";
    var districtNameKey = "location.district_name";

    var provinceCodeKey = "location.province_code";
    var provinceNameKey = "location.province_name";

    var stockoutStartDateKey = "stockout.date";
    var stockoutEndDateKey = "stockout.resolved_date";

    var overlapMonthKey = "overlapped_month";

    function calculateStockoutResult(stockOuts, numberOfFacilities) {
        var numberOfMonths = _.uniq(_.pluck(stockOuts, overlapMonthKey)).length;

        if (numberOfMonths == 0) {
            return {monthlyAvg: 0, monthlyOccurrences: 0, totalDuration: 0};
        }

        var sums = _.chain(stockOuts)
            .groupBy(overlapMonthKey)
            .map(function (stockOutsInSameMonth) {
                var totalDuration = _.reduce(stockOutsInSameMonth, function (memo, stockOut) {
                    memo += stockOut.overlap_duration;
                    return memo;
                }, 0);
                return {totalDurationOfMonth: totalDuration, totalOccurancesOfMonth: stockOutsInSameMonth.length};
            })
            .reduce(function (memo, monthResult) {
                memo.monthsAvgSum += monthResult.totalDurationOfMonth / monthResult.totalOccurancesOfMonth;
                memo.monthsOccurrencesSum += monthResult.totalOccurancesOfMonth;
                memo.monthsDurationSum += monthResult.totalDurationOfMonth;
                return memo;
            }, {monthsOccurrencesSum: 0, monthsDurationSum: 0, monthsAvgSum: 0})
            .value();

        return {
            monthlyAvg: (sums.monthsAvgSum / numberOfMonths / numberOfFacilities).toFixed(1),
            monthlyOccurrences: (sums.monthsOccurrencesSum / numberOfMonths / numberOfFacilities).toFixed(1),
            totalDuration: sums.monthsDurationSum
        };
    }

    function createFacilityTreeItem(stockOuts, carryingFacility) {
        var facilityCode = carryingFacility[facilityCodeKey];

        var stockOutsInFacility = _.filter(stockOuts, function (stockOut) {
            return stockOut[facilityCodeKey] == facilityCode;
        });
        var incidents = _.uniq(_.map(stockOutsInFacility, function (stockout) {
            return stockout[stockoutStartDateKey] + " to " + stockout[stockoutEndDateKey];
        })).join(", ");
        var facilityResult = calculateStockoutResult(stockOutsInFacility, 1);

        return {
            name: carryingFacility[facilityNameKey],
            facilityCode: facilityCode,
            monthlyAvg: facilityResult.monthlyAvg,
            monthlyOccurrences: facilityResult.monthlyOccurrences,
            totalDuration: facilityResult.totalDuration,
            incidents: incidents
        };
    }

    function createDistrictTreeItem(stockOuts, carryingFacilitiesInDistrict, facilityChildren) {
        var districtCode = carryingFacilitiesInDistrict[0][districtCodeKey];
        var stockOutsInDistrict = _.filter(stockOuts, function (stockOut) {
            return stockOut[districtCodeKey] == districtCode;
        });
        var facilityCodes = _.uniq(_.pluck(carryingFacilitiesInDistrict, facilityCodeKey));
        var numberOfFacilities = facilityCodes.length;

        var districtResult = calculateStockoutResult(stockOutsInDistrict, numberOfFacilities);

        return {
            name: carryingFacilitiesInDistrict[0][districtNameKey],
            monthlyAvg: districtResult.monthlyAvg,
            monthlyOccurrences: districtResult.monthlyOccurrences,
            totalDuration: districtResult.totalDuration,
            districtCode: districtCode,
            children: _.filter(facilityChildren, function (facilityChild) {
                return facilityCodes.indexOf(facilityChild.facilityCode) != -1;
            })
        };
    }


    function createProvinceTreeItem(stockOuts, carryingFacilitiesInProvince, districtChildren) {
        var provinceCode = carryingFacilitiesInProvince[0][provinceCodeKey];
        var stockOutsInProvince = _.filter(stockOuts, function (stockOut) {
            return stockOut[provinceCodeKey] == provinceCode;
        });
        var numberOfFacilities = _.uniq(_.pluck(carryingFacilitiesInProvince, facilityCodeKey)).length;
        var districtCodes = _.uniq(_.pluck(carryingFacilitiesInProvince, districtCodeKey));

        var provinceResult = calculateStockoutResult(stockOutsInProvince, numberOfFacilities);

        return {
            name: carryingFacilitiesInProvince[0][provinceNameKey],
            monthlyAvg: provinceResult.monthlyAvg,
            monthlyOccurrences: provinceResult.monthlyOccurrences,
            totalDuration: provinceResult.totalDuration,
            provinceCode: provinceCode,
            children: _.filter(districtChildren, function (districtChild) {
                return districtCodes.indexOf(districtChild.districtCode) != -1;
            })
        };
    }

    function buildTreeData(stockOuts, carryStartDates) {
        var facilityChildren = _.chain(carryStartDates)
            .map(function (carryingFacility) {
                return createFacilityTreeItem(stockOuts, carryingFacility);
            })
            .value();

        var districtChildren = _.chain(carryStartDates)
            .groupBy(function (carryingFacility) {
                return carryingFacility[districtCodeKey];
            })
            .map(function (carryingFacilitiesInDistrict) {
                return createDistrictTreeItem(stockOuts, carryingFacilitiesInDistrict, facilityChildren);
            }).value();

        return _.chain(carryStartDates)
            .groupBy(function (carryingFacility) {
                return carryingFacility[provinceCodeKey];
            })
            .map(function (carryingFacilitiesInProvince) {
                return createProvinceTreeItem(stockOuts, carryingFacilitiesInProvince, districtChildren);
            }).value();
    }

    return {
        buildTreeData: buildTreeData
    };
});