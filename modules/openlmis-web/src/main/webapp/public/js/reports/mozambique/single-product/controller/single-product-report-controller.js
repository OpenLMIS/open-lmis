function SingleProductReportController($scope, $filter, SingleProductReportService) {

    $scope.provinces = [];
    $scope.districts = [];
    $scope.filteredDistrict = [];
    $scope.filteredProvince = [];
    $scope.fullGeoZoneList = [];

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadProducts();
        $scope.loadGeographicZone();
    });

    $scope.loadProducts = function () {
        SingleProductReportService.loadAllProducts().get({}, function (data) {
            $scope.products = data.products;
        }, function () {
        });
    };

    $scope.loadGeographicZone = function () {
        loadGeographicLevel();
        fillGeoZoneList();
    };

    function loadGeographicLevel() {
        SingleProductReportService.loadGeographicLevel().get({}, function (data) {

            $scope.geographicZoneLevel = data["geographic-levels"];

        }, function () {
        });
    }

    function fillGeoZoneList() {
        SingleProductReportService.loadGeographicZone().get({}, function (data) {

            if (!$scope.geographicZoneLevel) {
                return;
            }

            var provinceLevel = $scope.geographicZoneLevel.find(function (level) {
                return level.code === "province";
            });

            var districtLevel = $scope.geographicZoneLevel.find(function (level) {
                return level.code === "district";
            });


            getProvincesAndDistricts(data, provinceLevel, districtLevel);

            Array.prototype.push.apply($scope.fullGeoZoneList, $scope.provinces);
            Array.prototype.push.apply($scope.fullGeoZoneList, $scope.districts);
        }, function () {
        });
    }

    function getProvincesAndDistricts(data, provinceLevel, districtLevel) {
        for (var i = 0; i < data["geographic-zones"].length; i++) {
            var geoZone = data["geographic-zones"][i];
            if (geoZone.levelId == provinceLevel.id) {
                $scope.provinces.push(geoZone);
            } else if (geoZone.levelId == districtLevel.id) {
                $scope.districts.push(geoZone);
            }
        }
    }

    $scope.filterDistrict = function () {
        if (!$scope.reportParam || !$scope.reportParam.provinceId) {
            $scope.filteredDistrict = $scope.districts;
            return;
        }
        var currentProvince = $scope.getGeoZone($scope.reportParam.provinceId);

        $scope.filteredDistrict = [];
        if (!currentProvince) {
            return;
        }
        for (var i = 0; i < $scope.districts.length; i++) {
            var district = $scope.districts[i];

            if (district.parentId === currentProvince.id) {
                $scope.filteredDistrict.push(district);
            }
        }
    };

    $scope.fillProvince = function () {
        if (!$scope.reportParam || !$scope.reportParam.districtId) {
            $scope.filteredProvince = $scope.provinces;
            return;
        }

        var parent = $scope.getParent($scope.reportParam.districtId);
        $scope.filteredProvince = [];
        $scope.filteredProvince.push(parent);
    };

    $scope.getParent = function (geoZoneId) {
        if (!geoZoneId) {
            return;
        }
        var parent = $scope.fullGeoZoneList.find(function (zone, index, array) {
            return $scope.getGeoZone(geoZoneId).parentId == zone.id;
        });
        return parent;
    };

    $scope.getGeoZone = function (id) {
        if (!id) {
            return;
        }
        var geoZone = $scope.fullGeoZoneList.find(function (zone, index, array) {
            return id == zone.id;
        });
        return geoZone;
    };

    $scope.loadReport = function () {
        var params = {};
        params.geographicZoneId = $scope.reportParam.districtId ? $scope.reportParam.districtId : $scope.reportParam.provinceId;
        params.productId = $scope.reportParam.productId;
        params.endTime = $filter('date')($scope.reportParam.endTime, "yyyy-MM-dd HH:mm:ss");
        SingleProductReportService.loadReport().get(params, function (data) {
            $scope.reportData = data.products;
        });
    };
}
