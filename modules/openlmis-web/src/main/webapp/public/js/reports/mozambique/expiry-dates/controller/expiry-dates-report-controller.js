function ExpiryDatesReportController($scope, $filter, $controller, $http, CubesGenerateUrlService, messageService, DateFormatService) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadProducts();
        $scope.loadHealthFacilities();
    });

    $scope.loadReport = function () {
        generateReportTitle();
        queryExpiryDatesReportDataFromCubes();
    };

    $scope.highlightDate = function(date) {
        var today = new Date();
        var sixMonthsFromNow = new Date(today.getFullYear(), today.getMonth() - 1 + 6, today.getDate());
        var compareDate = DateFormatService.formatDateWithLastDayOfMonth(sixMonthsFromNow);
        return date <= compareDate;
    };

    $scope.formatDate = function(date) {
        var options = {year: 'numeric', month: 'long'};
        return new Date(date).toLocaleString(locale, options);
    };

    function queryExpiryDatesReportDataFromCubes() {
        $http.get(CubesGenerateUrlService.generateAggregateUrl('vw_expiry_dates', ['facility.facility_code', 'drug.drug_code', 'expiry_dates'], generateCutParams()))
            .success(function (data) {
                generateReportData(data.cells);
            });
    }

    function generateReportData(drugData) {
        $scope.reportData = [];

        var expiryDatesHash = {};

        var dataGroupByDrug = _.groupBy(drugData, 'drug.drug_code');

        _.forEach(Object.keys(dataGroupByDrug), function(drugCode) {
            _.forEach(dataGroupByDrug[drugCode], function(facilityItem) {
                if (!expiryDatesHash[drugCode]) {
                    expiryDatesHash[drugCode] = {};
                    expiryDatesHash[drugCode].code = facilityItem['drug.drug_code'];
                    expiryDatesHash[drugCode].name = facilityItem['drug.drug_name'];
                    expiryDatesHash[drugCode].expiry_dates = facilityItem.expiry_dates;
                } else {
                    expiryDatesHash[drugCode].expiry_dates = expiryDatesHash[drugCode].expiry_dates + "," + facilityItem.expiry_dates;
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

    function getExpiryDateReportsParams() {
        var params = {};
        params.endTime = new Date($scope.reportParams.endTime).getTime();
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