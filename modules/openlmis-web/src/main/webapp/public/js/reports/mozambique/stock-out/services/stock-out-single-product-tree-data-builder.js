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

    function createFacilityTreeItem(stockOuts, carryingFacility) {
        var facilityCode = carryingFacility[facilityCodeKey];

        var stockOutsInFacility = _.filter(stockOuts, function (stockOut) {
            return stockOut[facilityCodeKey] == facilityCode;
        });
        var incidents = _.uniq(_.map(stockOutsInFacility, function (stockout) {
            return stockout[stockoutStartDateKey] + " to " + stockout[stockoutEndDateKey];
        }));
        var facilityResult = calculateStockoutResult(stockOutsInFacility, incidents.length);

        return {
            name: carryingFacility[facilityNameKey],
            facilityCode: facilityCode,
            avgDuration: facilityResult.avgDuration,
            totalOccurrences: facilityResult.totalOccurrences,
            totalDuration: facilityResult.totalDuration,
            incidents: incidents.join(", ")
        };
    }

    function createDistrictTreeItem(stockOuts, carryingFacilitiesInDistrict, facilityChildren) {
        var districtCode = carryingFacilitiesInDistrict[0][districtCodeKey];
        var stockOutsInDistrict = _.filter(stockOuts, function (stockOut) {
            return stockOut[districtCodeKey] == districtCode;
        });
        var facilityCodes = _.uniq(_.pluck(carryingFacilitiesInDistrict, facilityCodeKey));

        var facilityTreeData = _.filter(facilityChildren, function (facilityChild) {
            return facilityCodes.indexOf(facilityChild.facilityCode) != -1;
        });

        var districtResult = calculateStockoutResult(stockOutsInDistrict, calculateZoneOccurrences(facilityTreeData));

        return {
            name: carryingFacilitiesInDistrict[0][districtNameKey],
            avgDuration: districtResult.avgDuration,
            totalOccurrences: districtResult.totalOccurrences,
            totalDuration: districtResult.totalDuration,
            districtCode: districtCode,
            children: facilityTreeData
        };
    }

    function createProvinceTreeItem(stockOuts, carryingFacilitiesInProvince, districtChildren) {
        var provinceCode = carryingFacilitiesInProvince[0][provinceCodeKey];
        var stockOutsInProvince = _.filter(stockOuts, function (stockOut) {
            return stockOut[provinceCodeKey] == provinceCode;
        });
        var districtCodes = _.uniq(_.pluck(carryingFacilitiesInProvince, districtCodeKey));

        var districtTreeData = _.filter(districtChildren, function (districtChild) {
            return districtCodes.indexOf(districtChild.districtCode) != -1;
        });

        var provinceResult = calculateStockoutResult(stockOutsInProvince, calculateZoneOccurrences(districtTreeData));

        return {
            name: carryingFacilitiesInProvince[0][provinceNameKey],
            avgDuration: provinceResult.avgDuration,
            totalOccurrences: provinceResult.totalOccurrences,
            totalDuration: provinceResult.totalDuration,
            provinceCode: provinceCode,
            children: districtTreeData
        };
    }

    function calculateZoneOccurrences(treeData) {
        return _.reduce(treeData, function (memo, data) {
            memo += data.totalOccurrences;
            return memo;
        }, 0);
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