function ExpiryDatesReportController($scope, $filter, $controller, $http, CubesGenerateUrlService, messageService, $dialog) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.getTimeRange = function (dateRange) {
        $scope.reportParams.startTime = dateRange.startTime;
        $scope.reportParams.endTime = dateRange.endTime;
    };

    function showDateRangeInvalidWarningDialog() {
        var options = {
            id: "chooseDateAlertDialog",
            header: "title.alert",
            body: "dialog.date.range.invalid.warning"
        };
        MozambiqueDialog.newDialog(options, function () {
        }, $dialog);
    }

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadProducts();
        $scope.loadHealthFacilities();
    });

    $scope.loadReport = function () {
        if (isInvalidDateRange()) {
            showDateRangeInvalidWarningDialog();
            return;
        }

        generateReportTitle();
        getStockOutDataFromCubes();
    };

    function isInvalidDateRange() {
        return $scope.reportParams.startTime > $scope.reportParams.endTime;
    }

    function getStockOutDataFromCubes() {
        $http.get(CubesGenerateUrlService.generateFactsUrl('vw_expiry_dates', generateCutParams(getExpiryDateReportsParams())))
            .success(function (data) {
                generateReportItems(data);
            });
    }

    function generateReportItems(data) {
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

    $scope.getGeographicZoneById = function (zones, zoneId) {
        return zones.find(function (zone) {
            return zone.id == zoneId;
        });
    };

    function generateCutParams(expiryDatesParams) {
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