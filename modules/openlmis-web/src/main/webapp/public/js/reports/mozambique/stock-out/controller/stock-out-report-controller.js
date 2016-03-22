function StockOutReportController($scope, $filter, $controller, $http, CubesGenerateUrlService, messageService, $dialog) {
    $controller('BaseProductReportController', {$scope: $scope});

    var todayDateString = $filter('date')(new Date(), "yyyy-MM-dd");
    var currentDate = new Date();
    var timeOptions = {
        "month": new Date(),
        "3month": new Date().setMonth(currentDate.getMonth() - 2),
        "year": new Date().setMonth(currentDate.getMonth() - 11)
    };
    $scope.timeTags = Object.keys(timeOptions);
    $scope.showDateRangeInvalidWarning = false;

    function baseTimePickerOptions() {
        return {
            dateFormat: 'yy-mm-dd',
            changeYear: true,
            changeMonth: true,
            showMonthAfterYear: true,
            beforeShow: function (e, t) {
                $("#ui-datepicker-div").addClass("hide-calendar");
                $("#ui-datepicker-div").addClass('MonthDatePicker');
                $("#ui-datepicker-div").addClass('HideTodayButton');
            }
        }
    }

    $scope.datePickerStartOptions = angular.extend(baseTimePickerOptions(), {
        maxDate: currentDate,
        onClose: function () {
            $scope.timeTagSelected = "";
            var selectedYear = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
            var selectedMonth = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
            $scope.$apply(function () {
                $scope.reportParams.startTime = selectedMonth == null ? formatDateWithFirstDayOfMonth(currentDate) : $filter('date')(new Date(selectedYear, selectedMonth, 01), "yyyy-MM-dd");
            });
    }});

    $scope.datePickerEndOptions = angular.extend(baseTimePickerOptions(),{
        maxDate: currentDate,
        onClose: function(){
            $scope.timeTagSelected = "";
            var selectedYear = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
            var selectedMonth = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
            $scope.$apply(function(){
                $scope.reportParams.endTime = selectedMonth == null ? todayDateString :formatDateWithLastDayOfMonth(new Date(selectedYear, selectedMonth));
            });
        }
    });

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
        $scope.reportParams.startTime = formatDateWithFirstDayOfMonth(currentDate);
        $scope.reportParams.endTime = todayDateString;
        $scope.timeTagSelected = "month";
    });

    $scope.loadReport = function () {
        if ($scope.reportParams.startTime > $scope.reportParams.endTime){
            showDateRangeInvalidWarningDialog();
            return;
        }

        $scope.reportData = [];
        $scope.showIncompleteWarning = !isSelectedEndTimeLastDayOfMonth();

        var stockReportParams = getStockReportRequestParam();
        var generateAggregateUrl = CubesGenerateUrlService.generateAggregateUrl('vw_stockouts', ["drug","overlapped_month"], generateCutParams(stockReportParams));
        $http.get(generateAggregateUrl).success(function (data) {
            var groupedDrug =_.groupBy(data.cells, "drug.drug_code");

            _.forEach(groupedDrug, function(drug){
                var sumAvg=0;
                var sumDuration=0;
                var totalOccurrences=0;
                _.forEach(drug, function(stockOut){
                    sumAvg += stockOut.average_days;
                    sumDuration += stockOut['overlap_duration'];
                    totalOccurrences += stockOut.record_count;
                });
                drug.totalDuration = sumDuration;
                drug.code = drug[0]['drug.drug_code'];
                drug.name = drug[0]['drug.drug_name'];
                var numberOfFacilities = $scope.reportParams.facilityId ? 1 : FacilityFilter()($scope.facilities, $scope.districts, $scope.reportParams.districtId, $scope.reportParams.provinceId).length;
                drug.monthlyAvg = sumAvg/drug.length/ numberOfFacilities;
                drug.monthlyOccurrences = totalOccurrences/drug.length;
                $scope.reportData.push(drug);
            });

            formatReportWhenSelectAllFacility();
            $scope.reportParams.reportTitle = generateReportTitle(stockReportParams);
        });
    };

    function formatReportWhenSelectAllFacility() {
        if (!$scope.reportParams.facilityId) {
            $scope.reportData.map(function (data) {
                data.totalDuration = "-";
            });
            $scope.occurrencesHeader = messageService.get("report.avg.stock.out.occurrences");
        } else {
            $scope.occurrencesHeader = messageService.get("report.stock.out.occurrences");
        }
    }

    $scope.changeTimeOption = function (timeTag) {
        $scope.timeTagSelected = timeTag;
        $scope.reportParams.startTime = formatDateWithFirstDayOfMonth(new Date(timeOptions[timeTag]));
        $scope.reportParams.endTime = todayDateString;
    };

    function isSelectedEndTimeLastDayOfMonth() {
        return $scope.reportParams.endTime == formatDateWithLastDayOfMonth(new Date($scope.reportParams.endTime));
    }

    function getStockReportRequestParam() {
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

    function generateCutParams(stockReportParams) {
        var cutsParams = [{dimension: "overlapped_date", values: [stockReportParams.startTime + "-" + stockReportParams.endTime]}];
        if (stockReportParams.selectedFacility) {
            cutsParams.push({dimension: "facility", values: [stockReportParams.selectedFacility.code]});
        }

        if (stockReportParams.selectedProvince && stockReportParams.selectedDistrict) {
            cutsParams.push({
                dimension: "location",
                values: [[stockReportParams.selectedProvince.code, stockReportParams.selectedDistrict.code]]
            });
        }else if (stockReportParams.selectedProvince && !stockReportParams.selectedDistrict) {
            cutsParams.push({dimension: "location", values: [stockReportParams.selectedProvince.code]});
        }
        return cutsParams;
    }

    function generateReportTitle(stockReportParams) {
        var reportTitle = "";
        if (stockReportParams.selectedProvince) {
            reportTitle = stockReportParams.selectedProvince.name;
        }
        if (stockReportParams.selectedDistrict) {
            reportTitle += ("," + stockReportParams.selectedDistrict.name);
        }
        if (stockReportParams.selectedFacility) {
            reportTitle += reportTitle == "" ? stockReportParams.selectedFacility.name : ("," + stockReportParams.selectedFacility.name);
        }
        return reportTitle || messageService.get("label.all");
    }

    function formatDateWithFirstDayOfMonth(date){
        return $filter('date')(new Date(date.getFullYear(), date.getMonth(), 1), "yyyy-MM-dd");
    }

    function formatDateWithLastDayOfMonth(date){
        return $filter('date')(new Date(date.getFullYear(), date.getMonth() +1, 0), "yyyy-MM-dd");
    }

}