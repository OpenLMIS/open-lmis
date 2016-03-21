function BaseProductReportController($scope, $filter, ProductReportService, FacilityService, GeographicZoneService, $dialog) {
    $scope.provinces = [];
    $scope.districts = [];
    $scope.facilities = [];
    $scope.fullGeoZoneList = [];
    $scope.reportParams = {};

    $scope.todayDateString = $filter('date')(new Date(), "yyyy-MM-dd");

    $scope.$on('$viewContentLoaded', function () {
        loadGeographicZones();
        $scope.reportParams.endTime = $scope.todayDateString;
    });

    $scope.loadProducts = function () {
        ProductReportService.loadAllProducts().get({}, function (data) {
            $scope.products = data.products;
        });
    };

    $scope.loadHealthFacilities = function () {
        FacilityService.facilityTypes().get({}, function (data) {
            var facilityTypes = data["facility-types"];

            $scope.loadFacilities(facilityTypes);
        });
    };

    $scope.loadFacilities = function (facilityTypes) {
        FacilityService.allFacilities().get({}, function (data) {
            var facilities = data.facilities;
            var healthFacilities = [];

            _.forEach(facilities, function (facility) {
                _.forEach(facilityTypes, function (type) {
                    if (type.id == facility.typeId) {
                        if (type.code != "DDM" && type.code != "DPM") {
                            healthFacilities.push(facility);
                        }
                    }
                });
            });
            $scope.facilities = healthFacilities;
        });
    };

    $scope.getProvincesAndDistricts = function (geographicZoneLevels, data) {
        var provinceLevel = findGeographicZoneLevelByCode(geographicZoneLevels, "province");
        var districtLevel = findGeographicZoneLevelByCode(geographicZoneLevels, "district");
        _.forEach(data["geographic-zones"], function (zone) {
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

    $scope.fillGeographicZone = function () {
        var selectedFacility = $scope.facilities.find(function (facility) {
            return facility.id == $scope.reportParams.facilityId;
        });
        $scope.reportParams.districtId = !selectedFacility ? undefined : selectedFacility.geographicZoneId;
        $scope.reportParams.provinceId = !selectedFacility ? undefined : $scope.getParent(selectedFacility.geographicZoneId).id;
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

    $scope.checkDate = function () {
        if (new Date() < new Date($scope.reportParams.endTime)) {
            $scope.reportParams.endTime = $filter('date')(new Date(), "yyyy-MM-dd");
            var options = {
                id: "chooseDateAlertDialog",
                header: "title.alert",
                body: "dialog.body.date"
            };
            MozambiqueDialog.newDialog(options, function () {
            }, $dialog);
        }
    };

    $scope.checkLastSyncDate = function (time) {
        var syncInterval = (new Date() - time) / 1000 / 3600;
        return syncInterval <= 24 && {'background-color': 'green'} ||
            syncInterval > 24 * 3 && {'background-color': 'red'} ||
            {'background-color': 'orange'};
    };

    function loadGeographicZones() {
        GeographicZoneService.loadGeographicLevel().get({}, function (data) {

            var geographicZoneLevel = data["geographic-levels"];
            GeographicZoneService.loadGeographicZone().get({}, function (data) {
                $scope.getProvincesAndDistricts(geographicZoneLevel, data);
            });
        });
    }

    function findGeographicZoneLevelByCode(geographicZoneLevels, code) {
        if (!geographicZoneLevels) {
            return null;
        }
        return geographicZoneLevels.find(function (level) {
            return level.code == code;
        });
    }
}
