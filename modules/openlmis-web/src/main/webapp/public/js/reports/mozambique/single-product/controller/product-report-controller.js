function ProductReportController(type) {
    return function ($scope, $filter, ProductReportService) {

        $scope.provinces = [];
        $scope.districts = [];
        $scope.facilities = [];
        $scope.filteredDistrict = [];
        $scope.filteredProvince = [];
        $scope.filteredFacilities = [];
        $scope.fullGeoZoneList = [];

        $scope.$on('$viewContentLoaded', function () {
            if (type == "singleProduct") {
                $scope.loadProducts();
            } else {
                $scope.loadFacilities();
            }
            $scope.loadGeographicZone();
        });

        $scope.loadProducts = function () {
            ProductReportService.loadAllProducts().get({}, function (data) {
                $scope.products = data.products;
            });
        };

        $scope.loadGeographicZone = function () {
            loadGeographicLevel();
        };

        $scope.loadFacilities = function () {
            ProductReportService.loadFacilities().get({}, function (data) {
                $scope.facilities = data.facilities;
            });
        };

        function loadGeographicLevel() {
            ProductReportService.loadGeographicLevel().get({}, function (data) {

                $scope.geographicZoneLevel = data["geographic-levels"];
                fillGeoZoneList();
            });
        }

        function fillGeoZoneList() {
            ProductReportService.loadGeographicZone().get({}, function (data) {

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
            Array.prototype.push.apply($scope.fullGeoZoneList, $scope.provinces);
            Array.prototype.push.apply($scope.fullGeoZoneList, $scope.districts);

            for (i = 0; i < $scope.districts.length; i++) {
                var district = $scope.districts[i];
                var parent = $scope.getParent(district.id);
                if (parent) {
                    if (!parent.children) {
                        parent.children = [];
                    }
                    parent.children.push(district);
                    district.parent = parent;
                }
            }
        }

        $scope.filterDistrict = function () {
            if (!$scope.reportParams || !$scope.reportParams.provinceId) {
                $scope.filteredDistrict = $scope.districts;
                return;
            }
            var currentProvince = $scope.getGeoZone($scope.reportParams.provinceId);

            $scope.filteredDistrict = [];
            if (!currentProvince) {
                return;
            }
            $scope.filteredDistrict = currentProvince.children;
        };

        function addToFilteredFacilities(facilities) {
            if (typeof facilities == Array) {
                Array.prototype.push.apply($scope.filteredFacilities, facilities);
            } else {
                $scope.filteredFacilities.push(facilities);
            }
        }

        $scope.filterFacility = function () {
            $scope.filteredFacilities = [];
            if ($scope.reportParams.districtId) {
                addToFilteredFacilities(getAllFacilityInDistrict($scope.getGeoZone($scope.reportParams.districtId)));
            }else if ($scope.reportParams.provinceId) {
                var districts = $scope.getGeoZone($scope.reportParams.provinceId).children;
                for (var i=0;i<districts.length;i++) {
                    addToFilteredFacilities(getAllFacilityInDistrict(districts[i]));
                }
            }else{
                $scope.filteredFacilities = $scope.facilities;
                $scope.filteredProvince = $scope.provinces;
                $scope.filteredDistrict = $scope.districts;
            }
        };

        function getAllFacilityInDistrict(district) {
            return $scope.facilities.find(function (facility) {
                return facility.geographicZoneId == district.id;
            });
        }

        $scope.fillProvince = function () {
            if (!$scope.reportParams || !$scope.reportParams.districtId) {
                $scope.filteredProvince = $scope.provinces;
                return;
            }

            var parent = $scope.getParent($scope.reportParams.districtId);
            $scope.filteredProvince = [];
            $scope.filteredProvince.push(parent);
        };

        $scope.getParent = function (geoZoneId) {
            if (!geoZoneId) {
                return;
            }
            return $scope.fullGeoZoneList.find(function (zone, index, array) {
                return $scope.getGeoZone(geoZoneId).parentId == zone.id;
            });
        };

        $scope.getGeoZone = function (id) {
            if (!id) {
                return;
            }
            return $scope.fullGeoZoneList.find(function (zone, index, array) {
                return id == zone.id;
            });
        };

        $scope.loadReport = function () {
            var params = {};

            params.endTime = $filter('date')($scope.reportParams.endTime, "yyyy-MM-dd HH:mm:ss");
            if(type == "singleProduct"){
                params.productId = $scope.reportParams.productId;
                params.geographicZoneId = $scope.reportParams.districtId ? $scope.reportParams.districtId : $scope.reportParams.provinceId;

                if(!validateProduct()){
                    return;
                }

                ProductReportService.loadProductReport().get(params, function (data) {
                    $scope.reportData = data.products;
                });
            }else{
                params.facilityId = $scope.reportParams.facilityId;
                if(!validateFacility()){
                    return;
                }
                ProductReportService.loadFacilityReport().get(params, function (data) {
                    $scope.reportData = data.products;
                });
            }
        };

        function validateFacility(){
            $scope.invalid = !$scope.reportParams.facilityId;
            return !$scope.invalid;
        }

        function validateProduct(){
            $scope.invalid = !$scope.reportParams.productId;
            return !$scope.invalid;
        }
    }
}
