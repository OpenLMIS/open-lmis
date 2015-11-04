function ProductReportController(type) {
    return function ($scope, $filter, ProductReportService,GeographicZoneService,FacilityService, $dialog) {

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
            loadGeographicLevel();
            $scope.reportParams = {};
            $scope.reportParams.endTime = $filter('date')(new Date(), "yyyy-MM-dd");
        });

        $scope.loadProducts = function () {
            ProductReportService.loadAllProducts().get({}, function (data) {
                $scope.products = data.products;
            });
        };

        $scope.loadFacilities = function () {
            FacilityService.allFacilities().get({}, function (data) {
                $scope.facilities = data.facilities;
            });
        };

        function loadGeographicLevel() {
            GeographicZoneService.loadGeographicLevel().get({}, function (data) {

                $scope.geographicZoneLevel = data["geographic-levels"];
                fillGeoZoneList();
            });
        }

        function fillGeoZoneList() {
            GeographicZoneService.loadGeographicZone().get({}, function (data) {

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

            _.forEach(data["geographic-zones"], function(zone){
                if (zone.levelId == provinceLevel.id) {
                    $scope.provinces.push(zone);
                } else if (zone.levelId == districtLevel.id) {
                    $scope.districts.push(zone);
                }
            });

            $scope.fullGeoZoneList = _.union($scope.fullGeoZoneList, $scope.provinces, $scope.districts);

            _.forEach($scope.districts, function(district){
                var parent = $scope.getParent(district.id);
                if (parent) {
                    if (!parent.children) {
                        parent.children = [];
                    }
                    parent.children.push(district);
                    district.parent = parent;
                }
            });
        }

        $scope.filterDistrict = function () {
            if (!$scope.reportParams.provinceId) {
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

        $scope.filterFacility = function () {
            $scope.filteredFacilities = [];
            if ($scope.reportParams.districtId) {
                $scope.filteredFacilities = $scope.filteredFacilities
                    .concat(getAllFacilityInDistrict($scope.getGeoZone($scope.reportParams.districtId)));
            } else if ($scope.reportParams.provinceId) {
                var districts = $scope.getGeoZone($scope.reportParams.provinceId).children;

                $scope.filteredFacilities = _.reduce(districts, function(fullList ,district){
                    return fullList.concat(getAllFacilityInDistrict(district));
                }, []);
            } else {
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
            if (!$scope.reportParams.districtId) {
                $scope.filteredProvince = $scope.provinces;
                return;
            }

            var parent = $scope.getParent($scope.reportParams.districtId);
            $scope.filteredProvince = [];
            $scope.filteredProvince.push(parent);

            $scope.reportParams.provinceId = parent.id;
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
            if (type == "singleProduct") {
                params.productId = $scope.reportParams.productId;
                params.geographicZoneId = $scope.reportParams.districtId ? $scope.reportParams.districtId : $scope.reportParams.provinceId;

                if (!validateProduct()) {
                    return;
                }

                ProductReportService.loadProductReport().get(params, function (data) {
                    $scope.reportData = data.products;
                });
            } else {
                params.facilityId = $scope.reportParams.facilityId;
                if (!validateFacility()) {
                    return;
                }
                ProductReportService.loadFacilityReport().get(params, function (data) {
                    $scope.reportData = data.products;
                });
            }
        };

        $scope.checkDate = function(){
            if(new Date() < $scope.reportParams.endTime){
                $scope.reportParams.endTime = null;
                var options = {
                    id: "chooseDateAlertDialog",
                    header: "Confirmation",
                    body: "Cannot choose future date!"
                };
                OpenLmisDialog.newDialog(options, function(){}, $dialog);
            }
        };

        $scope.calculateSyncInterval = function(entry){
            return (new Date() - entry.lastSyncDate)/1000/3600;
        };

        function validateFacility() {
            $scope.invalid = !$scope.reportParams.facilityId;
            return !$scope.invalid;
        }

        function validateProduct() {
            $scope.invalid = !$scope.reportParams.productId;
            return !$scope.invalid;
        }
    };
}
