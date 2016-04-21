function TracerDrugsReportController($scope, $controller, $filter,DateFormatService, TracerDrugsChartService, CubesGenerateUrlService) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.getTimeRange = function (dateRange) {
        $scope.reportParams.startTime = dateRange.startTime;
        $scope.reportParams.endTime = dateRange.endTime;
    };

    var selectedProvinceCode = $scope.reportParams.provinceId ? $scope.getGeographicZoneById($scope.provinces, $scope.reportParams.provinceId).code : undefined;
    var selectedDistrictCode = $scope.reportParams.districtId ? $scope.getGeographicZoneById($scope.districts, $scope.reportParams.districtId).code : undefined;

    $scope.$on('$viewContentLoaded', function () {
        renderDefaultTracerDrugsReport();
    });

    $scope.loadReport = function () {
        TracerDrugsChartService.makeTracerDrugsChart('tracer-report', new Date($scope.reportParams.startTime), new Date($scope.reportParams.endTime), selectedProvinceCode, selectedDistrictCode);
    };

    $scope.downLoadRawData = function () {
        var fields = ["facility.facility_name", "drug.drug_name,date,soh"];
        window.open(CubesGenerateUrlService.generateFactsUrlForCsvDownloading('vw_weekly_tracer_soh', fields, generateCutParams()));
    };

    function generateCutParams() {
        var cutsParams = [{
            dimension: "cutDate",
            values: [$filter('date')($scope.reportParams.startTime, "yyyy,MM,dd") + "-" + $filter('date')($scope.reportParams.endTime, "yyyy,MM,dd")]
        }];
        var zoneConfig = getUserSelectedZoneConfig(selectedProvinceCode, selectedDistrictCode);
        if (zoneConfig.isOneDistrict) {
            cutsParams.push({
                dimension: "location",
                values: [[selectedProvinceCode, selectedDistrictCode]]
            });
        } else if (zoneConfig.isOneProvince) {
            cutsParams.push({dimension: "location", values: [selectedProvinceCode]});
        }
        return cutsParams;
    }

    function getUserSelectedZoneConfig(provinceCode, districtCode) {
        var isOneDistrict = provinceCode !== undefined && districtCode !== undefined;
        var isOneProvince = provinceCode !== undefined && districtCode === undefined;
        return {isOneDistrict: isOneDistrict, isOneProvince: isOneProvince};
    }

    function renderDefaultTracerDrugsReport() {
        var defaultStartTime = DateFormatService.formatDateWithFirstDayOfMonth(new Date());
        var defaultEndTime = $scope.reportParams.endTime;

        TracerDrugsChartService.makeTracerDrugsChart('tracer-report', new Date(defaultStartTime), new Date(defaultEndTime), undefined, undefined);
    }

}
