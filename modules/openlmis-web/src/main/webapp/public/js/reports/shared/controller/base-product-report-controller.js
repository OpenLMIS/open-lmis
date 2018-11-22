function BaseProductReportController($scope, $filter, ProductReportService, $cacheFactory, $timeout, FacilityService, GeographicZoneService, $dialog, DateFormatService, $location, messageService, HomeFacilityService) {
  $scope.provinces = [];
  $scope.districts = [];
  $scope.facilities = [];
  $scope.fullGeoZoneList = [];
  $scope.reportParams = {};
  $scope.products = [];
  $scope.homeFacility = {};
  var CMM_STATUS = {
    stockOut: 'stock-out',
    regularStock: 'regular-stock',
    overStock: 'over-stock',
    lowStock: 'low-stock'
  };

  if ($cacheFactory.get('keepHistoryInStockOnHandPage') !== undefined && $location.path().indexOf("stock-on-hand-all-products") < 0) {
    $cacheFactory.get('keepHistoryInStockOnHandPage').put('saveDataOfStockOnHand', "no");
  }
  if ($cacheFactory.get('BaseProductReportController') !== undefined && $location.path().indexOf("stock-out-all-products") < 0) {
    $cacheFactory.get('BaseProductReportController').put('shouldLoadStockOutReportAllProductsFromCache', "no");
  }
  $scope.todayDateString = $filter('date')(new Date(), "yyyy-MM-dd");

  $scope.$on('$viewContentLoaded', function () {
    loadGeographicZones();
    loadHomeFacility();
    $scope.reportParams.endTime = $scope.todayDateString;
  });

  $scope.endTimeOptions = {
    prevText: "<<",
    nextText: ">>",
    monthNames: ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
      "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"],
    monthNamesShort: ["Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
      "Jul", "Ago", "Set", "Out", "Nov", "Dez"],
    dayNames: [
      "Domingo",
      "Segunda-feira",
      "Terça-feira",
      "Quarta-feira",
      "Quinta-feira",
      "Sexta-feira",
      "Sábado"
    ],
    dayNamesShort: ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"],
    dayNamesMin: ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"],
    weekHeader: "Sem",
    dateFormat: "dd/mm/yy",
    beforeShow: function () {
      $("#ui-datepicker-div").removeClass("hide-calendar");
      $("#ui-datepicker-div").removeClass('MonthDatePicker');
      $("#ui-datepicker-div").removeClass('HideTodayButton');
    }
  };

  function loadHomeFacility() {
    HomeFacilityService.get({}, function (data) {
      setHomeFacility(data);
    });
  }

  function setHomeFacility(data) {
    $scope.homeFacility = data['home-facility'];
  }

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
          if (type.id === facility.typeId) {
            if (type.code !== "DDM" && type.code !== "DPM" && type.code !== "Central") {
              healthFacilities.push(facility);
            }
          }
        });
      });
      $scope.facilities = healthFacilities;

      if (!_.includes(['/stock-on-hand-all-products'], $location.$$path)) {
        addAllOption($scope.facilities, "facility");
      }

      $scope.populateOptions ? $scope.populateOptions() : undefined;
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

    if (!_.includes(['/over-stock', '/stock-on-hand-all-products', '/expired-products', '/expiring-products', '/consumption'], $location.$$path)) {
      addAllOption($scope.provinces, "province");
    }

    if (!_.includes(['/stock-on-hand-all-products', '/consumption'], $location.$$path)) {
      addAllOption($scope.districts, "district");
    }
  };

  $scope.selectedProvince = function () {
    $scope.reportParams.districtId = "";
  };

  $scope.changeDistrict = function () {
    var parent = $scope.getParent($scope.reportParams.districtId);
    $scope.reportParams.provinceId = !parent ? $scope.reportParams.provinceId : parent.id;
    $scope.reportParams.facilityId = "";
  };

  $scope.fillGeographicZone = function () {
    if (!$scope.reportParams.facilityId || $scope.reportParams.facilityId === " ") {
      return;
    }

    var selectedFacility = _.find($scope.facilities, function (facility) {
      return facility.id == $scope.reportParams.facilityId;
    });
  
    getNames(selectedFacility);
    
    $scope.reportParams.districtId = !selectedFacility ? undefined : selectedFacility.geographicZoneId;
    $scope.reportParams.provinceId = !selectedFacility ? undefined : $scope.getParent(selectedFacility.geographicZoneId).id;
  };

  $scope.getParent = function (geoZoneId) {
    return geoZoneId && _.find($scope.fullGeoZoneList, function (zone) {
      if ($scope.getGeoZone(geoZoneId)) {
        return $scope.getGeoZone(geoZoneId).parentId == zone.id;
      }
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
    var syncInterval = (new Date() - new Date(time)) / 1000 / 3600;
    return syncInterval <= 24 && {'background-color': 'green'} ||
      syncInterval > 24 * 3 && {'background-color': 'red'} ||
      {'background-color': 'orange'};
  };

  $scope.hasExpirationRisk = function (entry) {
    if (entry.estimated_months) {
      var selectedDate = new Date($scope.reportParams.endTime);
      var estimatedDrugUseUpDate = new Date(selectedDate.getFullYear(), selectedDate.getMonth() + Math.floor(entry.estimated_months) + 1, 0);
      var expiryDate = new Date(DateFormatService.convertPortugueseDateStringToNormalDateString(entry.expiry_date));
      var expiryDateWithLastDate = new Date(expiryDate.getFullYear(), expiryDate.getMonth() + 1, 0);
      if (entry.expiry_date !== null && expiryDateWithLastDate <= estimatedDrugUseUpDate) {
        return true;
      }
    }
    return false;
  };

  $scope.cmmStatus = function (entry) {
    var cmm = entry.cmm;
    var soh = entry.soh;
    if (soh === 0) {
      return CMM_STATUS.stockOut;
    }
    if (cmm == -1) {
      return CMM_STATUS.regularStock;
    }

    if (soh < 0.05 * cmm) {//low stock
      return CMM_STATUS.lowStock;
    }
    else if (soh > 2 * cmm) {//over stock
      return CMM_STATUS.overStock;
    } else {
      return CMM_STATUS.regularStock;
    }
  };

  $scope.getEntryStockStatus = function (entry) {
    if ($scope.cmmStatus(entry) === CMM_STATUS.lowStock) {
      return messageService.get('stock.cmm.low.stock');
    }
    if ($scope.cmmStatus(entry) === CMM_STATUS.overStock) {
      return messageService.get('stock.cmm.over.stock');
    }
    return '';
  };

  $scope.formatMonth = function (dateString) {
    if (dateString) {
      return DateFormatService.formatDateWithLocaleNoDay(dateString);
    }
  };

  $scope.formatDateWithDay = function (dateString) {
    return DateFormatService.formatDateWithLocale(dateString);
  };

  $scope.formatDateWithTimeAndLocale = function (dateString) {
    return DateFormatService.formatDateWithTimeAndLocale(dateString);
  };

  $scope.locationIdToCode = function (params) {
    params.selectedProvince = $scope.getGeographicZoneById($scope.provinces, $scope.reportParams.provinceId);
    params.selectedDistrict = $scope.getGeographicZoneById($scope.districts, $scope.reportParams.districtId);
    params.selectedFacility = _.find($scope.facilities, function (facility) {
      return facility.id == $scope.reportParams.facilityId;
    });
  };

  $scope.getFacilityByCode = function (facilityCode) {
    return _.find($scope.facilities, function (facility) {
      return facility.code == facilityCode;
    });
  };

  $scope.getDrugByCode = function (drugCode) {
    return _.find($scope.products, function (product) {
      return product.code == drugCode;
    });
  };

  function addAllOption(locations, location) {
    $timeout(function () {
      if (locations.length > 1) {
        $("#" + location + "DropDown").append($('<option>', {
          value: " ",
          text: messageService.get("report.option.all")
        }));
      }
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
  
  function getNames(selectedFacility) {
    if (selectedFacility) {
      $scope.reportParams.facilityName = selectedFacility.name;
      $scope.reportParams.districtName = $scope.getGeoZone(selectedFacility.geographicZoneId).name;
      $scope.reportParams.provinceName = $scope.getParent(selectedFacility.geographicZoneId).name;
    }
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

  $scope.validateProvince = function () {
    $scope.invalidProvince = !$scope.reportParams.provinceId;
    return !$scope.invalidProvince;
  };

  $scope.validateDistrict = function () {
    $scope.invalidDistrict = !$scope.reportParams.districtId;
    return !$scope.invalidDistrict;
  };

  $scope.validateFacility = function () {
    $scope.invalidFacility = !$scope.reportParams.facilityId;
    return !$scope.invalidFacility;
  };

  $scope.validateProduct = function () {
    $scope.invalidProductCode = !$scope.reportParams.productCode;
    return !$scope.invalidProductCode;
  };

  $scope.validateSingleFacility = function () {
    var facilityId = $scope.reportParams.facilityId;
    $scope.invalidFacility = !facilityId || facilityId === ' ';
    return !$scope.invalidFacility;
  };

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
