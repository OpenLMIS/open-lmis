function BaseProductReportController($scope, $filter, ProductReportService, $cacheFactory, $timeout, FacilityService, GeographicZoneService, $dialog, DateFormatService, $location) {
    $scope.provinces = [];
    $scope.districts = [];
    $scope.facilities = [];
    $scope.fullGeoZoneList = [];
    $scope.reportParams = {};
    $scope.products = [];
    if ($cacheFactory.get('keepHistoryInStockOnHandPage') !== undefined && $location.path().indexOf("stock-on-hand-all-products") < 0) {
        $cacheFactory.get('keepHistoryInStockOnHandPage').put('saveDataOfStockOnHand', "no");
    }
    if ($cacheFactory.get('BaseProductReportController') !== undefined && $location.path().indexOf("stock-out-all-products") < 0) {
        $cacheFactory.get('BaseProductReportController').put('saveDataOfStockOutReport', "no");
    }
    $scope.todayDateString = $filter('date')(new Date(), "yyyy-MM-dd");

    $scope.$on('$viewContentLoaded', function () {
        loadGeographicZones();
        $scope.reportParams.endTime = $scope.todayDateString;
    });

    $scope.getTimeRange = function (dateRange) {
        $scope.reportParams.startTime = dateRange.startTime;
        $scope.reportParams.endTime = dateRange.endTime;
    };

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
            addAllOption($scope.facilities, "facility");
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
        addAllOption($scope.provinces, "province");
        addAllOption($scope.districts, "district");
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
        var soh = entry.soh;
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

    $scope.formatMonth = function (dateString) {
        if (dateString) {
            return DateFormatService.formatDateWithLocaleNoDay(dateString);
        }
    };

    $scope.formatDateWithDay = function (dateString) {
        return DateFormatService.formatDateWithLocale(dateString);
    };

    $scope.locationIdToCode = function (params) {
        params.selectedProvince = $scope.getGeographicZoneById($scope.provinces, $scope.reportParams.provinceId);
        params.selectedDistrict = $scope.getGeographicZoneById($scope.districts, $scope.reportParams.districtId);
        params.selectedFacility = ($scope.facilities.find(function (facility) {
            return facility.id == $scope.reportParams.facilityId;
        }));
    };

    function addAllOption(locations, location) {
        $timeout(function () {
            if (locations.length > 1) {
                $("#" + location + "DropDown").append($('<option>', {
                    value: "",
                    text: 'ALL'
                }));
            }
            $scope.reportParams[location + "Id"] = locations[0].id;
        });
    }

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

    $scope.splitPeriods = function (start, end) {
        var previousMonth = -1, thisMonth = 0, nextMonth = 1, periodStartDay = 21, periodEndDay = 20;

        function shiftMonthAtDay(date, shiftMonth, atDay) {
            var resultDate = new Date(date);
            resultDate.setMonth(date.getMonth() + shiftMonth);
            resultDate.setDate(atDay);
            return resultDate;
        }

        function periodOf(date) {
            var coveredDate = new Date(date);
            var periodStart, periodEnd;
            if (coveredDate.getDate() < periodStartDay) {
                periodStart = shiftMonthAtDay(coveredDate, previousMonth, periodStartDay);
                periodEnd = shiftMonthAtDay(coveredDate, thisMonth, periodEndDay);
            } else {
                periodStart = shiftMonthAtDay(coveredDate, thisMonth, periodStartDay);
                periodEnd = shiftMonthAtDay(coveredDate, nextMonth, periodEndDay);
            }
            return {periodStart: periodStart, periodEnd: periodEnd};
        }

        function nextPeriod(period) {
            return {
                periodStart: shiftMonthAtDay(period.periodStart, nextMonth, periodStartDay),
                periodEnd: shiftMonthAtDay(period.periodEnd, nextMonth, periodEndDay)
            };
        }

        function periodsInBetween(first, last) {
            var periods = [first];

            var next = nextPeriod(first);
            while (next.periodStart.getTime() <= last.periodStart.getTime()) {
                periods.push(next);
                next = nextPeriod(next);
            }

            return periods;
        }

        return periodsInBetween(periodOf(start), periodOf(end));
    };

}
