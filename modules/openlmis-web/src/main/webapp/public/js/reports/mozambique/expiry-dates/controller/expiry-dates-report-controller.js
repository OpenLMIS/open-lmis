function ExpiryDatesReportController($scope, $filter, $controller, $http, CubesGenerateUrlService, messageService, DateFormatService, CubesGenerateCutParamsService) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadProducts();
        $scope.loadHealthFacilities();
    });

    $scope.loadReport = function () {
        generateReportTitle();
        queryExpiryDatesReportDataFromCubes();
    };

    $scope.highlightDate = function (date) {
        var today = new Date();
        var sixMonthsFromNow = new Date(today.getFullYear(), today.getMonth() - 1 + 6, today.getDate());
        var compareDate = DateFormatService.formatDateWithLastDayOfMonth(sixMonthsFromNow);
        return date <= compareDate;
    };

    $scope.formatDate = function (date) {
        var options = {year: 'numeric', month: 'long'};
        return new Date(date).toLocaleString(locale, options);
    };

    function queryExpiryDatesReportDataFromCubes() {
        var params = getExpiryDateReportsParams();
        var cutsParams = CubesGenerateCutParamsService.generateCutsParams('occurred', undefined, params.endTime, params.selectedFacility, undefined, params.selectedProvince, params.selectedDistrict);

        $http.get(CubesGenerateUrlService.generateAggregateUrl('vw_expiry_dates', ['facility.facility_code', 'drug.drug_code', 'expiry_dates'], cutsParams))
            .success(function (data) {
                generateReportData(data.cells);
            });
    }

    function generateReportData(data) {
        $scope.reportData = [];

        var expiryDatesHash = {};

        _.forEach(_.groupBy(data, 'facility.facility_code'), function (item) {
            var expiryDatesForTheFacility = getExpiryDatesBeforeOccurredForFacility(item);

            _.forEach(expiryDatesForTheFacility, function (expiryDateItem, drugCode) {
                if (expiryDatesHash[drugCode]) {
                    expiryDatesHash[drugCode].expiry_dates = expiryDatesHash[drugCode].expiry_dates + "," + expiryDateItem.expiry_dates;
                } else {
                    expiryDatesHash[drugCode] = expiryDateItem;
                }
            });
        });

        _.forEach(_.values(expiryDatesHash), function (drug) {
            if (drug.expiry_dates) {
                drug.expiry_dates = convertDateListToArray(drug.expiry_dates);
            }
            $scope.reportData.push(drug);
        });
    }

    function convertDateListToArray(expiryDates) {
        var validDates = expiryDates.split(",").filter(function (value) {
            return value;
        });
        var datesForDisplay = [];
        _.forEach(validDates, function (value) {
            var date = new Date(value.split("/")[2], value.split("/")[1] - 1, value.split("/")[0]);
            date = DateFormatService.formatDateWithLastDayOfMonth(date);
            datesForDisplay.push(date);
        });
        return _.sortBy(_.uniq(datesForDisplay));
    }

    function getExpiryDatesBeforeOccurredForFacility(dataForOneFacility) {
        var drugOccurredHash = {};
        _.forEach(dataForOneFacility, function (item) {
            var createddate = item.last_createddate;
            var occurredDate = item.last_occurred;
            var drugCode = item['drug.drug_code'];
            if (drugOccurredHash[drugCode]) {
                if (occurredDate === drugOccurredHash[drugCode].occurred_date) {
                    if (createddate > drugOccurredHash[drugCode].createddate) {
                        drugOccurredHash[drugCode].createddate = createddate;
                        drugOccurredHash[drugCode].expiry_dates = item.expiry_dates;
                    }
                } else if(occurredDate > drugOccurredHash[drugCode].occurred_date) {
                    drugOccurredHash[drugCode].occurred_date = occurredDate;
                    drugOccurredHash[drugCode].createddate = createddate;
                    drugOccurredHash[drugCode].expiry_dates = item.expiry_dates;
                }
            } else {
                drugOccurredHash[drugCode] = {
                    code: drugCode,
                    name: item['drug.drug_name'],
                    expiry_dates: item.expiry_dates,
                    createddate: createddate,
                    occurred_date: occurredDate,
                    facility_code: item['facility.facility_code']
                };
            }
        });
        return drugOccurredHash;
    }

    function getExpiryDateReportsParams() {
        var params = {};
        params.endTime = new Date($scope.reportParams.endTime).setHours(23,59,59,999);
        $scope.locationIdToCode(params);
        return params;
    }

    function generateReportTitle() {
        var expiryDatesReportParams = getExpiryDateReportsParams();
        var reportTitle = "";
        if (expiryDatesReportParams.selectedProvince) {
            reportTitle = expiryDatesReportParams.selectedProvince.name;
        }
        if (expiryDatesReportParams.selectedDistrict) {
            reportTitle += ("," + expiryDatesReportParams.selectedDistrict.name);
        }
        if (expiryDatesReportParams.selectedFacility) {
            reportTitle += reportTitle === "" ? expiryDatesReportParams.selectedFacility.name : ("," + expiryDatesReportParams.selectedFacility.name);
        }
        $scope.reportParams.reportTitle = reportTitle || messageService.get("label.all");
    }
}