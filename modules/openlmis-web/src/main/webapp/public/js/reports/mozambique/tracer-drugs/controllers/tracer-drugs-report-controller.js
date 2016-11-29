function TracerDrugsReportController($scope, $controller, DateFormatService, TracerDrugsChartService, $timeout, $q, $http) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.$on('$viewContentLoaded', function () {
        renderDefaultTracerDrugsReport();
    });

    $scope.loadReport = function () {
        TracerDrugsChartService.makeTracerDrugsChart('tracer-report', 'legend-div', new Date($scope.reportParams.startTime), new Date($scope.reportParams.endTime), getSelectedProvince(), getSelectedDistrict());
    };

    $scope.exportXLSX = function() {
        TracerDrugsChartService.exportXLSX($scope.reportParams.startTime, $scope.reportParams.endTime,  getSelectedProvince(), getSelectedDistrict());
    };

    function getSelectedProvince() {
        return $scope.getGeographicZoneById($scope.provinces, $scope.reportParams.provinceId);
    }

    function getSelectedDistrict() {
        return $scope.getGeographicZoneById($scope.districts, $scope.reportParams.districtId);
    }

    function renderDefaultTracerDrugsReport() {
        var defaultStartTime = DateFormatService.formatDateWithFirstDayOfMonth(new Date());
        var defaultEndTime = $scope.reportParams.endTime;

        var getHomeFacility = $http.get('/rest-api/lookup/home-facility');
        $q.when(getHomeFacility).then(function (result) {
            var homeFacility = result.data['home-facility'];
            $timeout(function () {
                setDefaultLocation(homeFacility);
                TracerDrugsChartService.makeTracerDrugsChart('tracer-report', 'legend-div', new Date(defaultStartTime), new Date(defaultEndTime), getSelectedProvince(), getSelectedDistrict());
            }, 1000);
        });
    }

    function setDefaultLocation(homeFacility) {
        var facilityType = homeFacility.facilityType.code;
        if(facilityType === 'Central'){
            if($scope.provinces.length === 1) {
                $scope.reportParams.provinceId = $scope.provinces[0].id;
            } else {
                $scope.reportParams.provinceId = ' ';
            }
            $scope.reportParams.districtId = ' ';
        }

        if (facilityType === 'DPM') {
            $scope.reportParams.provinceId = $scope.provinces[0].id;
            $scope.reportParams.districtId = ' ';
        }

        if (facilityType === 'DDM') {
            $scope.reportParams.districtId = $scope.districts[0].id;
        }

    }


}
