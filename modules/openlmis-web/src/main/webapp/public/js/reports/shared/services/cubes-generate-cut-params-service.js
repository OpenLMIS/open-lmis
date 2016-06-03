services.factory('CubesGenerateCutParamsService', function (ReportLocationConfigService) {

    var generateCutsParams = function (timeDimensionName, startTime, endTime, facility, drugs, province, district) {
        var cutsParams = [];

        var selectedStartTime = startTime === undefined ? "" : startTime;
        var selectedEndTime = endTime === undefined ? "" : endTime;

        if (selectedStartTime !== undefined || selectedEndTime !== undefined) {
            cutsParams.push({
                dimension: timeDimensionName, values: [selectedStartTime + "-" + selectedEndTime], skipEscape: true
            });
        }

        if (facility) {
            cutsParams.push({dimension: "facility", values: [facility.code]});
        }

        if (drugs) {
            cutsParams.push({dimension: "drug", values: _.pluck(drugs, "drug.drug_code")});
        }

        var locationConfig = ReportLocationConfigService.getUserSelectedLocationConfig(province, district);
        if (locationConfig.isOneDistrict) {
            cutsParams.push({
                dimension: "location",
                values: [[province.code, district.code]]
            });
        } else if (locationConfig.isOneProvince) {
            cutsParams.push({dimension: "location", values: [province.code]});
        }
        return cutsParams;
    };

    return {generateCutsParams: generateCutsParams};
});
