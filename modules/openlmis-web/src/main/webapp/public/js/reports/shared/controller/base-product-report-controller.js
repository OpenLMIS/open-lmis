function BaseProductReportController($scope, $filter, ProductReportService, FacilityService, GeographicZoneService, $dialog, DateFormatService) {
    $scope.provinces = [];
    $scope.districts = [];
    $scope.facilities = [];
    $scope.fullGeoZoneList = [];
    $scope.reportParams = {};
    $scope.products = [];

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

    $scope.selectedProvince = function () {
        $scope.reportParams.districtId = "";
    };

    $scope.fillProvince = function () {
        var parent = $scope.getParent($scope.reportParams.districtId);
        $scope.reportParams.provinceId = !parent ? undefined : parent.id;
    };

    $scope.fillGeographicZone = function () {
        if (!$scope.reportParams.facilityId) {
            return;
        }

        var selectedFacility = _.find($scope.facilities, function (facility) {
            return facility.id == $scope.reportParams.facilityId;
        });

        $scope.reportParams.districtId = !selectedFacility ? undefined : selectedFacility.geographicZoneId;
        $scope.reportParams.provinceId = !selectedFacility ? undefined : $scope.getParent(selectedFacility.geographicZoneId).id;
    };

    $scope.getParent = function (geoZoneId) {
        return geoZoneId && _.find($scope.fullGeoZoneList, function (zone) {
                return $scope.getGeoZone(geoZoneId).parentId == zone.id;
            });
    };

    $scope.getGeoZone = function (id) {
        return id && _.find($scope.fullGeoZoneList, function (zone) {
                return id == zone.id;
            });
    };

    $scope.getGeographicZoneById = function (zones, zoneId) {
        return _.find(zones, function (zone) {
            return zone.id == zoneId;
        });
    };

    $scope.checkDateBeforeToday = function () {
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

    $scope.checkDateValidRange = function () {
        if ($scope.reportParams.startTime > $scope.reportParams.endTime) {
            showDateRangeInvalidWarningDialog();
            return false;
        }
        return true;
    };

    $scope.checkLastSyncDate = function (time) {
        var syncInterval = (new Date() - time) / 1000 / 3600;
        return syncInterval <= 24 && {'background-color': 'green'} ||
            syncInterval > 24 * 3 && {'background-color': 'red'} ||
            {'background-color': 'orange'};
    };

    $scope.cmmStatus = function (entry) {
        var cmm = entry.cmm;
        var soh = entry.productQuantity;
        if (soh === 0) {
            return "stock-out";
        }
        if (cmm == -1) {
            return "regular-stock";
        }

        if (soh < 0.05 * cmm) {//low stock
            return "low-stock";
        }
        else if (soh > 2 * cmm) {//over stock
            return "over-stock";
        } else {
            return "regular-stock";
        }
    };

    $scope.formatDate = function (dateString) {
        if (dateString) {
            return DateFormatService.formatDateWithLocaleNoDay(dateString);
        }
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

        return _.find(geographicZoneLevels, function (geographicZoneLevel) {
            return geographicZoneLevel.code == code;
        });
    }

    $scope.getGeographicZoneById = function (zones, zoneId) {
        return _.find(zones, function (zone) {
            return zone.id == zoneId;
        });
    };

    function showDateRangeInvalidWarningDialog() {
        var options = {
            id: "chooseDateAlertDialog",
            header: "title.alert",
            body: "dialog.date.range.invalid.warning"
        };
        MozambiqueDialog.newDialog(options, function () {
        }, $dialog);
    }

}
