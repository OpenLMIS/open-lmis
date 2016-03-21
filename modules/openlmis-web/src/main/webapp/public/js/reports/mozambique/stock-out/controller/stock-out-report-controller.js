function StockOutReportController($scope, $filter, $controller, $http, CubesGenerateUrlService, messageService) {
    $controller('BaseProductReportController', {$scope: $scope});

    var todayDateString = $filter('date')(new Date(), "yyyy-MM-dd");
    var currentDate = new Date();
    var timeOptions = {
        "month": new Date(),
        "3month": new Date().setMonth(currentDate.getMonth() - 2),
        "year": new Date().setMonth(currentDate.getMonth() - 11)
    };
    $scope.timeTags = Object.keys(timeOptions);

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
        onClose: function () {
            $scope.timeTagSelected = "";
            var selectedYear = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
            var selectedMonth = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
            $scope.$apply(function () {
                $scope.reportParams.startTime = selectedMonth == null ? formatDateWithFirstDayOfMonth(currentDate) : $filter('date')(new Date(selectedYear, selectedMonth, 01), "yyyy-MM-dd");
            });
    }});

    $scope.datePickerEndOptions = angular.extend(baseTimePickerOptions(),{
        onClose: function(){
            $scope.timeTagSelected = "";
            var selectedYear = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
            var selectedMonth = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
            $scope.$apply(function(){
                $scope.reportParams.endTime = selectedMonth == null ? todayDateString :formatDateWithLastDayOfMonth(new Date(selectedYear, selectedMonth));
            });
        }
    });

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadProducts();
        $scope.loadHealthFacilities();
        $scope.reportParams.startTime = formatDateWithFirstDayOfMonth(currentDate);
        $scope.reportParams.endTime = todayDateString;
    });

    $scope.loadReport = function () {
        var stockReportParams = getStockReportRequestParam();
        var generateAggregateUrl = CubesGenerateUrlService.generateAggregateUrl('vw_stockouts', ["drug","overlapped_month"], generateCutParams(stockReportParams));
        $http.get(generateAggregateUrl).success(function (data) {
            $scope.reportData = data.cells;

            if (!stockReportParams.selectedFacility){
                $scope.reportData.map(function(data){
                    data.duration = "-";
                });
                $scope.occurrencesHeader = messageService.get("report.avg.stock.out.occurrences");
            }else {
                $scope.occurrencesHeader = messageService.get("report.stock.out.occurrences");
            }

            $scope.reportParams.reportTitle = generateReportTitle(stockReportParams);
        });
    };

    $scope.changeTimeOption = function (timeTag) {
        $scope.timeTagSelected = timeTag;
        $scope.reportParams.startTime = formatDateWithFirstDayOfMonth(new Date(timeOptions[timeTag]));
        $scope.reportParams.endTime = todayDateString;
    };

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

        if (stockReportParams.selectedDistrict) {
            cutsParams.push({
                dimension: "location",
                values: [[stockReportParams.selectedProvince.code, stockReportParams.selectedDistrict.code]]
            });
        } else if (stockReportParams.selectedProvince) {
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
            reportTitle += ("," + stockReportParams.selectedFacility.name);
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