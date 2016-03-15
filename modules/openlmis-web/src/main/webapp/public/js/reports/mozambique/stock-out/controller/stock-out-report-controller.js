function StockOutReportController($scope, $filter, $controller, $http, CubesGenerateUrlService, messageService) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.multiProducts = [];
    $scope.multiSelectionSettings = {
        displayProp: "primaryName",
        idProp: "code",
        externalIdProp: "code",
        enableSearch: true,
        scrollable: true,
        scrollableHeight: "300px",
        showCheckAll: false
    };
    $scope.multiSelectionModifyTexts = {
        dynamicButtonTextSuffix: "drugs selected",
        buttonDefaultText: "Select Drugs"
    };

    var currentDate = new Date();
    var timeOptions = {
        "month": new Date().setMonth(currentDate.getMonth() - 1),
        "3month": new Date().setMonth(currentDate.getMonth() - 3),
        "year": new Date().setFullYear(currentDate.getFullYear() - 1)
    };
    $scope.timeTags = Object.keys(timeOptions);

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadProducts();
        $scope.loadHealthFacilities();
        $scope.reportParams.startTime = $filter('date')(new Date().setMonth(currentDate.getMonth() - 1), "yyyy-MM-dd");
    });

    $scope.loadReport = function () {
        var stockReportParams = getStockReportRequestParam();
        var generateAggregateUrl = CubesGenerateUrlService.generateAggregateUrl('vw_stockouts', 'drug', generateCutParams(stockReportParams));
        $http.get(generateAggregateUrl).success(function (data) {
            $scope.reportData = data.cells;
            $scope.reportParams.reportTitle = generateReportTitle(stockReportParams);
        });
    };

    $scope.changeTimeOption = function (timeTag) {
        $scope.timeTagSelected = timeTag;
        $scope.reportParams.startTime = $filter('date')(timeOptions[timeTag], "yyyy-MM-dd");
        $scope.reportParams.endTime = $scope.todayDateString;
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
        params.selectedProductCodes = $scope.multiProducts.map(function (product) {
            return product.code;
        });
        return params;
    }

    $scope.getGeographicZoneById = function (zones, zoneId) {
        return zones.find(function (zone) {
            return zone.id == zoneId;
        });
    }

    function generateCutParams(stockReportParams) {
        var cutsParams = [{dimension: "date", values: [stockReportParams.startTime + "-" + stockReportParams.endTime]},
            {dimension: "drug", values: stockReportParams.selectedProductCodes}];
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
            reportTitle += stockReportParams.selectedFacility.name;
        }
        return reportTitle || messageService.get("label.all");
    }
}