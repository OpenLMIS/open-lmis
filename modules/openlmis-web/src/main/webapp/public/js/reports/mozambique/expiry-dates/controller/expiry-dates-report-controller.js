function ExpiryDatesReportController($scope, $filter, $controller, $http, CubesGenerateUrlService, messageService) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.getTimeRange = function (dateRange) {
        $scope.reportParams.startTime = dateRange.startTime;
        $scope.reportParams.endTime = dateRange.endTime;
    };

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadProducts();
        $scope.loadHealthFacilities();
    });

    $scope.loadReport = function () {
        if ($scope.checkDateValidRange()) {
            generateReportTitle();
            queryExpiryDatesReportDataFromCubes();
        }
    };

    function queryExpiryDatesReportDataFromCubes() {
        $http.get(CubesGenerateUrlService.generateFactsUrl('vw_expiry_dates', generateCutParams()))
            .success(function (data) {
                generateReportData(data);
            });
    }

    function generateReportData(data) {
        var drugOccurredHash = {};

        _.forEach(data, function (item) {
            var occurredDate = new Date(item['occurred.year'], item['occurred.month'] - 1, item['occurred.day']);
            var drug_code = item['drug.drug_code'];
            if (drugOccurredHash[drug_code]) {
                if (occurredDate <= drugOccurredHash[drug_code]) {
                    drugOccurredHash[drug_code].occurred_date = occurredDate;
                    drugOccurredHash[drug_code].expiry_dates = item.expiry_dates;
                }
            } else {
                drugOccurredHash[drug_code] = {
                    code: drug_code,
                    name: item['drug.drug_name'],
                    expiry_dates: item.expiry_dates,
                    occurred_date: occurredDate
                };
            }
        });
        $scope.reportData = [];
        for (var key in drugOccurredHash) {
            $scope.reportData.push(drugOccurredHash[key]);
        }
    }

    function getExpiryDateReportsParams() {
        var params = {};
        params.startTime = $filter('date')($scope.reportParams.startTime, "yyyy,MM,dd");
        params.endTime = $filter('date')($scope.reportParams.endTime, "yyyy,MM,dd");
        params.selectedProvince = $scope.getGeographicZoneById($scope.provinces, $scope.reportParams.provinceId);
        params.selectedDistrict = $scope.getGeographicZoneById($scope.districts, $scope.reportParams.districtId);
        params.selectedFacility = ($scope.facilities.find(function (facility) {
            return facility.id == $scope.reportParams.facilityId;
        }));
        return params;
    }

    function generateCutParams() {
        var expiryDatesParams = getExpiryDateReportsParams();
        var cutsParams = [
            {dimension: "occurred", values: [expiryDatesParams.startTime + "-" + expiryDatesParams.endTime]}
        ];
        if (expiryDatesParams.selectedFacility) {
            cutsParams.push({dimension: "facility", values: [expiryDatesParams.selectedFacility.code]});
        }

        if (expiryDatesParams.selectedProvince && expiryDatesParams.selectedDistrict) {
            cutsParams.push({
                dimension: "location",
                values: [[expiryDatesParams.selectedProvince.code, expiryDatesParams.selectedDistrict.code]]
            });
        } else if (expiryDatesParams.selectedProvince && !expiryDatesParams.selectedDistrict) {
            cutsParams.push({dimension: "location", values: [expiryDatesParams.selectedProvince.code]});
        }
        return cutsParams;
    }

    function generateReportTitle() {
        var stockReportParams = getExpiryDateReportsParams();
        var reportTitle = "";
        if (stockReportParams.selectedProvince) {
            reportTitle = stockReportParams.selectedProvince.name;
        }
        if (stockReportParams.selectedDistrict) {
            reportTitle += ("," + stockReportParams.selectedDistrict.name);
        }
        if (stockReportParams.selectedFacility) {
            reportTitle += reportTitle === "" ? stockReportParams.selectedFacility.name : ("," + stockReportParams.selectedFacility.name);
        }
        $scope.reportParams.reportTitle = reportTitle || messageService.get("label.all");
    }
}