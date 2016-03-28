function StockOutSingleProductReportController($scope, $filter, $controller, $http, CubesGenerateUrlService, messageService, $dialog) {
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
            $scope.selectedProduct = JSON.parse($scope.reportParams.product);
            getStockOutDataFromCubes();
        }
    };

    function isInvalidDateRange() {
        return $scope.reportParams.startTime > $scope.reportParams.endTime;
    }

    function getStockReportRequestParam() {
        var params = {};
        params.startTime = $filter('date')($scope.reportParams.startTime, "yyyy,MM,dd");
        params.endTime = $filter('date')($scope.reportParams.endTime, "yyyy,MM,dd");
        params.drugCode = $scope.selectedProduct.code;
        return params;
    }

    function generateCutParams(stockReportParams) {
        var cutsParams = [{
            dimension: "overlapped_date",
            values: [stockReportParams.startTime + "-" + stockReportParams.endTime]
        }];

        cutsParams.push({dimension: "drug", values: [stockReportParams.drugCode]});
        return cutsParams;
    }

    function getStockOutDataFromCubes() {
        if (!validateProduct()) {
            return;
        }

        $http.get(CubesGenerateUrlService.generateFactsUrl('vw_stockouts', generateCutParams(getStockReportRequestParam())))
            .success(function (data) {
                $scope.tree_data = generateStockOutData(data);
            });
    }

    function validateProduct() {
        $scope.invalid = !$scope.selectedProduct;
        return !$scope.invalid;
    }

    $scope.expanding_property = {
        field: "name",
        displayName: " "
    };
    $scope.col_defs = [
        {
            field: "monthlyAvg",
            displayName: messageService.get('report.stock.out.avg.duration')
        },
        {
            field: "monthlyOccurrences",
            displayName: messageService.get('report.stock.out.occurrences')
        },
        {
            field: "totalDuration",
            displayName: messageService.get('report.stock.out.total')
        }, {
            field: "incidents",
            displayName: messageService.get('report.stock.out.incidents')
        }
    ];

    function generateStockOutData(testRawTreeData) {
        var groupByProvince = _.groupBy(testRawTreeData, 'location.province_code');

        var provinceData = [];
        _.forEach(groupByProvince, function (province) {
            var provinceNameKey = 'location.province_name';
            var numOfSelectedFacilities = getNumOfSelectedFacilities(province[0]['location.province_code']);
            var provinceResult = calculateStockOut(province, provinceNameKey, numOfSelectedFacilities);

            var districts = _.groupBy(province, 'location.district_code');

            provinceResult.children = generateProvinceChildren(districts);
            provinceData.push(provinceResult);
        });
        return provinceData;
    }

    function generateProvinceChildren(districts) {
        var provinceChildrenArray = [];
        _.forEach(districts, function (district) {
            var districtNameKey = 'location.district_name';
            var numOfSelectedFacilities = getNumOfSelectedFacilities(district[0]['location.province_code'], district[0]['location.district_code']);
            var districtResult = calculateStockOut(district, districtNameKey, numOfSelectedFacilities);

            var facilities = _.groupBy(district, 'facility.facility_code');

            districtResult.children = generateDistrictChildren(facilities);
            provinceChildrenArray.push(districtResult);
        });
        return provinceChildrenArray;
    }

    function generateDistrictChildren(facilities) {
        var districtChildren = [];
        _.forEach(facilities, function (facility) {
            var facilityResult = calculateStockOut(facility, 'facility.facility_name', 1);
            districtChildren.push(facilityResult);
        });
        return districtChildren;
    }

    function getNumOfSelectedFacilities(provinceCode, districtCode) {
        var province = getGeographicZoneByCode($scope.provinces, provinceCode);
        var district = getGeographicZoneByCode($scope.districts, districtCode);
        var districtId = district ? district.id : "";
        return FacilityFilter()($scope.facilities, $scope.districts, districtId, province.id).length;
    }

    function getGeographicZoneByCode(zones, zoneCode) {
        return _.find(zones, function(zone){
            return zone.code == zoneCode;
        });
    }

    function generateReportItem(name, totalDuration, monthlyAvg, monthlyOccurrences, incidents) {
        return {
            'name': name,
            'monthlyAvg': monthlyAvg,
            'monthlyOccurrences': monthlyOccurrences,
            'totalDuration': totalDuration,
            'incidents': incidents
        };
    }

    function calculateStockOut(groupValue, name, numOfSelectedFacilities) {
        var sumAvg = 0;
        var totalOccurrences = 0;
        var totalDuration = 0;
        var incidents = "";
        var groupByOverlapMonth = _.groupBy(groupValue, "overlapped_month");

        _.forEach(groupByOverlapMonth, function (drug) {
            var sum = 0;
            _.forEach(drug, function (stockOut) {
                sum += stockOut.overlap_duration;
                if (numOfSelectedFacilities === 1) {
                    var incident = stockOut['stockout.date'] + "to" + stockOut['stockout.resolved_date'];
                    if (incidents.indexOf(incident) === -1) {
                        incidents += incidents === "" ? incident : ", " + incident;
                    }
                }
            });
            sumAvg += sum / drug.length;
            totalOccurrences += drug.length;
            totalDuration += sum;
        });

        var monthlyAvg = sumAvg / Object.keys(groupByOverlapMonth).length / numOfSelectedFacilities;
        var monthlyOccurrences = totalOccurrences / Object.keys(groupByOverlapMonth).length / numOfSelectedFacilities;

        return generateReportItem(groupValue[0][name], totalDuration, monthlyAvg, monthlyOccurrences, incidents);
    }
}