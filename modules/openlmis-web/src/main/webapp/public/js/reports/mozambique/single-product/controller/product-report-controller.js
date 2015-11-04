function ProductReportController(type) {
    return function ($scope, $filter, ProductReportService,GeographicZoneService,FacilityService, $dialog) {

        $scope.provinces = [];
        $scope.districts = [];
        $scope.facilities = [];
        $scope.fullGeoZoneList = [];

        $scope.$on('$viewContentLoaded', function () {
            if (type == "singleProduct") {
                $scope.loadProducts();
            } else {
                $scope.loadFacilities();
            }
            loadGeographicZones();
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

        function loadGeographicZones() {
            GeographicZoneService.loadGeographicLevel().get({}, function (data) {

                var geographicZoneLevel = data["geographic-levels"];
                GeographicZoneService.loadGeographicZone().get({}, function (data) {
                    $scope.getProvincesAndDistricts(geographicZoneLevel,data);
                });
            });
        }

        function findGeographicZoneLevelByCode(geographicZoneLevels, code){
            if (!geographicZoneLevels) {
                return null;
            }
            return geographicZoneLevels.find(function (level) {
                return level.code == code;
            });
        }

        $scope.getProvincesAndDistricts = function(geographicZoneLevels, data) {
            var provinceLevel = findGeographicZoneLevelByCode(geographicZoneLevels, "province");
            var districtLevel = findGeographicZoneLevelByCode(geographicZoneLevels, "district");
            _.forEach(data["geographic-zones"], function(zone){
                if (zone.levelId == provinceLevel.id) {
                    $scope.provinces.push(zone);
                } else if (zone.levelId == districtLevel.id) {
                    $scope.districts.push(zone);
                }
            });
            $scope.fullGeoZoneList = _.union($scope.fullGeoZoneList, $scope.provinces, $scope.districts);
        };

        $scope.fillProvince = function () {
            var parent = $scope.getParent($scope.reportParams.districtId);
            $scope.reportParams.provinceId = !parent ? undefined : parent.id;
        };

        $scope.getParent = function (geoZoneId) {
            return geoZoneId && $scope.fullGeoZoneList.find(function (zone, index, array) {
                return $scope.getGeoZone(geoZoneId).parentId == zone.id;
            });
        };

        $scope.getGeoZone = function (id) {
            return id && $scope.fullGeoZoneList.find(function (zone, index, array) {
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
