function ExpiryDatesReportController($scope, $filter, $controller, $http, CubesGenerateUrlService, messageService, DateFormatService) {
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
        generateReportTitle();
        queryExpiryDatesReportDataFromCubes();
    };

    function queryExpiryDatesReportDataFromCubes() {
        $http.get(CubesGenerateUrlService.generateFactsUrl('vw_expiry_dates', generateCutParams()))
            .success(function (data) {
                generateReportData(data);
            });
    }

    function generateReportData(data) {
        $scope.reportData = [];

        var expiryDatesHash = {};

        var dataGroupByFacility = _.groupBy(data, 'facility.facility_code');
        var dataGroupByDrug = _.groupBy(data, 'drug.drug_code');

        _.forEach(dataGroupByFacility, function(item) {
            var expiryDatesForTheFacility = getExpiryDatesBeforeOccurredForFacility(item);

            _.forEach(Object.keys(dataGroupByDrug), function(drugCode) {
                if (!expiryDatesHash[drugCode]) {
                    expiryDatesHash[drugCode] = expiryDatesForTheFacility[drugCode];
                } else if (expiryDatesForTheFacility[drugCode] && expiryDatesForTheFacility[drugCode]["expiry_dates"]) {
                    expiryDatesHash[drugCode]["expiry_dates"] = expiryDatesHash[drugCode]["expiry_dates"] + "," + expiryDatesForTheFacility[drugCode]["expiry_dates"];
                }
            });
        });

        _.forEach(_.values(expiryDatesHash), function(drug) {
            if (drug.expiry_dates) {
                drug.expiry_dates = convertDateListToArray(drug.expiry_dates);
            }
            $scope.reportData.push(drug);
        });
    }

    function convertDateListToArray(expiryDates) {
        var validDates = expiryDates.split(",").filter(function(value) { return value; } );
        var datesForDisplay = [];
        _.forEach(validDates, function(value) {
            var date = new Date(value.split("/")[2], value.split("/")[1] - 1, value.split("/")[0]);
            date = DateFormatService.formatDateWithLastDayOfMonth(date);
            datesForDisplay.push(date);
        });
        return _.sortBy(_.uniq(datesForDisplay));
    }

    function getExpiryDatesBeforeOccurredForFacility(dataForOneFacility) {
        var drugOccurredHash = {};
        _.forEach(dataForOneFacility, function (item) {
            var occurredDate = new Date(item['occurred.year'], item['occurred.month'] - 1, item['occurred.day']);
            var drugCode = item['drug.drug_code'];
            if (drugOccurredHash[drugCode] ) {
                if (occurredDate < drugOccurredHash[drugCode]) {
                    drugOccurredHash[drugCode].occurred_date = occurredDate;
                    drugOccurredHash[drugCode].expiry_dates = item.expiry_dates;
                }
            } else {
                drugOccurredHash[drugCode] = {
                    code: drugCode,
                    name: item['drug.drug_name'],
                    expiry_dates: item.expiry_dates,
                    occurred_date: occurredDate
                };
            }
        });
        return drugOccurredHash;
    }

    function getExpiryDateReportsParams() {
        var params = {};
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
            {dimension: "occurred", values: ["-" + expiryDatesParams.endTime]}
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