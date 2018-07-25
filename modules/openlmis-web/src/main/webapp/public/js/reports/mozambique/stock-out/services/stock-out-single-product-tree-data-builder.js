services.factory('StockoutSingleProductTreeDataBuilder', function (StockOutReportCalculationService) {
    var facilityCodeKey = "facility.facility_code";
    var facilityNameKey = "facility.facility_name";

    var districtCodeKey = "location.district_code";
    var districtNameKey = "location.district_name";

    var provinceCodeKey = "location.province_code";
    var provinceNameKey = "location.province_name";

    function createFacilityTreeItem(stockOuts, carryingFacility, startTime, endTime) {
        var facilityCode = carryingFacility[facilityCodeKey];

        var stockOutsInFacility = _.filter(stockOuts, function (stockOut) {
            return stockOut[facilityCodeKey] == facilityCode;
        });
        var occurrences = StockOutReportCalculationService.generateIncidents(stockOutsInFacility).length;
        var facilityResult = StockOutReportCalculationService.newCalculateStockoutResult(stockOutsInFacility, startTime, endTime, occurrences);

        return {
            name: carryingFacility[facilityNameKey],
            facilityCode: facilityCode,
            avgDuration: facilityResult.avgDuration,
            totalOccurrences: facilityResult.totalOccurrences,
            totalDuration: facilityResult.totalDuration,
            incidents: StockOutReportCalculationService.generateIncidents(stockOutsInFacility).join(", ")
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

        var districtResult = {};
        districtResult.totalOccurrences = calculateZoneOccurrences(facilityTreeData);
        districtResult.totalDuration = calculateZoneDuration(facilityTreeData);
        districtResult.avgDuration = (districtResult.totalDuration / districtResult.totalOccurrences).toFixed(1);
        districtResult.avgDuration = isNaN(districtResult.avgDuration) ? 0 : districtResult.avgDuration;

        return {
            name: carryingFacilitiesInDistrict[0][districtNameKey],
            avgDuration: districtResult.avgDuration,
            totalDuration : districtResult.totalDuration,
            totalOccurrences: districtResult.totalOccurrences,
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

        var provinceResult = {};
        provinceResult.totalOccurrences = calculateZoneOccurrences(districtTreeData);
        provinceResult.totalDuration = calculateZoneDuration(districtTreeData);
        provinceResult.avgDuration = (provinceResult.totalDuration / provinceResult.totalOccurrences).toFixed(1);
        provinceResult.avgDuration = isNaN(provinceResult.avgDuration ) ? 0 : provinceResult.avgDuration;

        return {
            name: carryingFacilitiesInProvince[0][provinceNameKey],
            avgDuration: provinceResult.avgDuration,
            totalOccurrences: provinceResult.totalOccurrences,
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

    function calculateZoneDuration(treeData) {
        return _.reduce(treeData, function (memo, data) {
            memo += data.totalDuration;
            return memo;
        }, 0);
    }

    function buildTreeData(stockOuts, carryStartDates, startTime, endTime) {
        var facilityChildren = _.chain(carryStartDates)
            .map(function (carryingFacility) {
                return createFacilityTreeItem(stockOuts, carryingFacility, startTime, endTime);
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