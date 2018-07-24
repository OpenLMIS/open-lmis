services.factory('CubesGenerateCutParamsService', function (ReportLocationConfigService) {

    var generateCutsParams = function (timeDimensionName, startTime, endTime, facility, drugs, province, district) {
        var cutsParams = [];

        var selectedStartTime, selectedEndTime;
        if (timeDimensionName) {
            selectedStartTime = startTime === undefined ? "" : startTime;
            selectedEndTime = endTime === undefined ? "" : endTime;
        }

        if (selectedStartTime !== undefined || selectedEndTime !== undefined) {
            cutsParams.push({
                dimension: timeDimensionName, values: [selectedStartTime + "-" + selectedEndTime], skipEscape: true
            });
        }

        if (facility) {
            cutsParams.push({dimension: "facility", values: [facility.code]});
        }

        if (drugs) {
            var everyDrugHasDrugCode = _.every(drugs, function (drug) {
                return drug.hasOwnProperty("drug.drug_code");
            });
            if (everyDrugHasDrugCode) {
                cutsParams.push({dimension: "drug", values: _.pluck(drugs, "drug.drug_code")});
            } else {
                !_.isEmpty(drugs) && cutsParams.push({dimension: "drug", values: drugs});
            }
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

    var addCutsParams = function (cutsParams, startTime, isResolved) {
        if (isResolved !== undefined){
            cutsParams.push({dimension: "is_resolved", values:[isResolved.toString()]});
        }
        if (startTime !== undefined){
            cutsParams.push({dimension: "stockout_resolved_date", values: [startTime + "-"], skipEscape: true});
        }
        return cutsParams;
    };

    return {
        generateCutsParams: generateCutsParams,
        addCutsParams: addCutsParams
    };
});
