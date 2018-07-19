services.factory('WeeklyNosDrugExportService', function ($http, $filter, $q, $timeout, messageService, CubesGenerateUrlService, CubesGenerateCutParamsService, ReportLocationConfigService, ReportExportExcelService, DateFormatService) {

  var DATE_FORMAT = 'yyyy,MM,dd';
  var CMM_ENTRIES_CUBE = 'vw_cmm_entries';
  var WEEKLY_NOS_SOH_CUBE = 'vw_weekly_nos_soh';
  
  var data = {
    reportHeaders: [],
    reportContent: []
  };

  function addReportDateHeaders(dates) {
    var header = {
      drugCode: messageService.get('report.header.drug.code'),
      area: messageService.get('report.header.area'),
      subArea: messageService.get('report.header.subarea'),
      drugName: messageService.get('report.header.drug.name'),
      province: messageService.get('report.header.province'),
      district: messageService.get('report.header.district'),
      facility: messageService.get('report.header.facility'),
      cmmValue:messageService.get('report.header.cmm'),
      reportGeneratedFor: messageService.get('report.header.generated.for')
    };
    _.each(dates, function (date) {
      header[date] = date;
    });
    return header;
  }

  function validateDrugs(selectedDrugs) {
    var everyDrugIsSolid = _.every(selectedDrugs, function (drug) {
      return drug;
    });
    return (!_.isEmpty(selectedDrugs) && everyDrugIsSolid) ? selectedDrugs : undefined;
  }

  function prepareDrugsRequestUrl(selectedDrugs, province, district, startTime, endTime) {
    var drugParams = validateDrugs(selectedDrugs);

    var cuts = CubesGenerateCutParamsService.generateCutsParams('cutDate', $filter('date')(startTime, DATE_FORMAT),
      $filter('date')(endTime, DATE_FORMAT), undefined, drugParams, province, district);

    var params = [{
      name: 'fields',
      value: ['location.province_name', 'location.district_name', 'facility.facility_code', 'facility.facility_name', 'drug.drug_name', 'drug.drug_code', 'date', 'soh', 'area.area_name', 'area.sub_area_name']
    }];

    return CubesGenerateUrlService.generateFactsUrlWithParams(WEEKLY_NOS_SOH_CUBE, cuts, params);
  }

  function prepareCMMRequestUrl(selectedDrugs, province, district, endTime, allNosDrugs) {
    var endDateParam = new Date(moment(endTime, 'YYYY-MM-DD').valueOf());
    var startDatePeriod = DateFormatService.formatDateWithStartDayOfPeriod(endDateParam);
    var endDatePeriod = DateFormatService.formatDateWithEndDayOfPeriod(endDateParam);

    var drugParams = validateDrugs(selectedDrugs) ? selectedDrugs : allNosDrugs;
    var cutsParams = [
      {dimension: 'product', values: drugParams},
      {dimension: 'periodbegin', values: [$filter('date')(startDatePeriod, DATE_FORMAT)], skipEscape: true},
      {dimension: 'periodend', values: [$filter('date')(endDatePeriod, DATE_FORMAT)], skipEscape: true}
    ];

    var locationParam = ReportLocationConfigService.getLocationHierarchy(province, district);
    if (locationParam) {
      cutsParams.push({dimension: 'location', values: [ locationParam ], skipEscape: true});
    }

    return CubesGenerateUrlService.generateFactsUrl(CMM_ENTRIES_CUBE, cutsParams);
  }

  function getWeeklyDrugsData(selectedDrugs, province, district, startTime, endTime) {
    var requestUrl = prepareDrugsRequestUrl(selectedDrugs, province, district, startTime, endTime);
    return $http.get(requestUrl);
  }

  function getCMMForNosDrugInPeriod(selectedDrugs, province, district, endTime, allNosDrugs) {
    var requestUrl = prepareCMMRequestUrl(selectedDrugs, province, district, endTime, allNosDrugs);
    return $http.get(requestUrl);
  }

  function getSohOnDate(dates, newNosDrug, nosDrugInFacility) {
    _.forEach(dates, function (date) {
      var sohOnDate = _.findWhere(nosDrugInFacility, {date: date});
      if (sohOnDate) {
        newNosDrug[date] = sohOnDate.soh;
      } else {
        newNosDrug[date] = 'N/A';
      }
    });
  }

  function getColumnsFromDates(nosDrugs) {
    var dates = _.map(nosDrugs, function (nosDrug) {
      return nosDrug.date;
    });
    return _.uniq(dates).sort();
  }

  function populateWeeklyNosDrugData(nosDrugs, nosDrugHash, startTime, endTime) {
    var dates = getColumnsFromDates(nosDrugs);
    data.reportHeaders = addReportDateHeaders(dates);

    var nosDrugsGroup = _.groupBy(nosDrugs, function(nosDrug) {
      return nosDrug['drug.drug_code'] + nosDrug['facility.facility_code'];
    });

    _.forEach(nosDrugsGroup, function (nosDrugInFacility) {
      var newNosDrug = {};
      newNosDrug.drugCode = nosDrugInFacility[0]['drug.drug_code'];
      newNosDrug.area = nosDrugInFacility[0]['area.area_name'];
      newNosDrug.subArea = nosDrugInFacility[0]['area.sub_area_name'];
      newNosDrug.drugName = nosDrugInFacility[0]['drug.drug_name'];
      newNosDrug.province = nosDrugInFacility[0]['location.province_name'];
      newNosDrug.district = nosDrugInFacility[0]['location.district_name'];
      newNosDrug.facility = nosDrugInFacility[0]['facility.facility_name'];
      newNosDrug.reportGeneratedFor = DateFormatService.formatDateWithDateMonthYearForString(startTime) + ' - ' + DateFormatService.formatDateWithDateMonthYearForString(endTime);
      getSohOnDate(dates, newNosDrug, nosDrugInFacility);

      nosDrugHash[nosDrugInFacility[0]['drug.drug_code'] + '@' + nosDrugInFacility[0]['facility.facility_code']] = newNosDrug;
    });
  }

  function populateLastPeriodCMMData(cmmEntries, nosDrugHash) {
    _.forEach(cmmEntries, function (cmmEntry) {
      if (validateNosDrugEntryInHash(cmmEntry, nosDrugHash)) {
        nosDrugHash[cmmEntry.product + '@' + cmmEntry.facilityCode].cmmValue = cmmEntry.cmm;
      }
    });
  }

  function validateNosDrugEntryInHash(cmmEntry, nosDrugHash) {
    return cmmEntry.cmm !== undefined && cmmEntry.cmm !== null && angular.isDefined(nosDrugHash[cmmEntry.product + '@' + cmmEntry.facilityCode]);
  }

  function formatDateWithDayMonthYearBySlash(dateString) {
    var date = Date.parse(dateString);
    return $filter('date')(date, 'dd/MM/yyyy');
  }

  function getDataForExport(selectedDrugs, province, district, startTime, endTime, allNosDrugs) {

    var weeklyDrugsDataPromise = getWeeklyDrugsData(selectedDrugs, province, district, startTime, endTime);
    var lastPeriodCMMDataPromise = getCMMForNosDrugInPeriod(selectedDrugs, province, district, endTime, allNosDrugs);
    $q.all([weeklyDrugsDataPromise, lastPeriodCMMDataPromise]).then(function (result) {
      var nosDrugs = result[0].data;
      var cmmEntries = result[1].data;

      var nosDrugHash = {};
      populateWeeklyNosDrugData(nosDrugs, nosDrugHash, startTime, endTime);
      populateLastPeriodCMMData(cmmEntries, nosDrugHash);

      data.reportContent = _.values(nosDrugHash);
      data.reportHeaders = data.reportHeaders.map(formatDateWithDayMonthYearBySlash)
      ReportExportExcelService.exportAsXlsx(data, messageService.get('report.file.nos.drugs.report'));
    });
  }

  return {
    getDataForExport: getDataForExport
  };
});