services.factory('WeeklyDrugExportService', function ($http, $filter, $q, $timeout, messageService, CubesGenerateUrlService, CubesGenerateCutParamsService, ReportLocationConfigService, ReportExportExcelService, DateFormatService) {

  var DATE_FORMAT = 'yyyy,MM,dd';
  var CMM_ENTRIES_CUBE = 'vw_cmm_entries';
  var WEEKLY_TRACER_SOH_CUBE = 'vw_weekly_tracer_soh';
  
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

    return CubesGenerateUrlService.generateFactsUrlWithParams(WEEKLY_TRACER_SOH_CUBE, cuts, params);
  }

  function prepareCMMRequestUrl(selectedDrugs, province, district, endTime, allTracerDrugs) {
    var endDateParam = new Date($filter('date')(endTime, DATE_FORMAT));
    var startDatePeriod = DateFormatService.formatDateWithStartDayOfPeriod(endDateParam);
    var endDatePeriod = DateFormatService.formatDateWithEndDayOfPeriod(endDateParam);

    var drugParams = validateDrugs(selectedDrugs) ? selectedDrugs : allTracerDrugs;
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

  function getCMMForTracerDrugInPeriod(selectedDrugs, province, district, endTime, allTracerDrugs) {
    var requestUrl = prepareCMMRequestUrl(selectedDrugs, province, district, endTime, allTracerDrugs);
    return $http.get(requestUrl);
  }

  function getSohOnDate(dates, newTracerDrug, tracerDrugInFacility) {
    _.forEach(dates, function (date) {
      var sohOnDate = _.findWhere(tracerDrugInFacility, {date: date});
      if (sohOnDate) {
        newTracerDrug[date] = sohOnDate.soh;
      } else {
        newTracerDrug[date] = 'N/A';
      }
    });
  }

  function getColumnsFromDates(tracerDrugs) {
    var dates = _.map(tracerDrugs, function (tracerDrug) {
      return tracerDrug.date;
    });
    return _.uniq(dates);
  }

  function populateWeeklyTracerDrugData(tracerDrugs, tracerDrugHash, startTime, endTime) {
    var dates = getColumnsFromDates(tracerDrugs);
    data.reportHeaders = addReportDateHeaders(dates);

    var tracerDrugsGroup = _.groupBy(tracerDrugs, function(tracerDrug) {
      return tracerDrug['drug.drug_code'] + tracerDrug['facility.facility_code'];
    });

    _.forEach(tracerDrugsGroup, function (tracerDrugInFacility) {
      var newTracerDrug = {};
      newTracerDrug.drugCode = tracerDrugInFacility[0]['drug.drug_code'];
      newTracerDrug.area = tracerDrugInFacility[0]['area.area_name'];
      newTracerDrug.subArea = tracerDrugInFacility[0]['area.sub_area_name'];
      newTracerDrug.drugName = tracerDrugInFacility[0]['drug.drug_name'];
      newTracerDrug.province = tracerDrugInFacility[0]['location.province_name'];
      newTracerDrug.district = tracerDrugInFacility[0]['location.district_name'];
      newTracerDrug.facility = tracerDrugInFacility[0]['facility.facility_name'];
      newTracerDrug.reportGeneratedFor = DateFormatService.formatDateWithDateMonthYearForString(startTime) + ' - ' + DateFormatService.formatDateWithDateMonthYearForString(endTime);
      getSohOnDate(dates, newTracerDrug, tracerDrugInFacility);

      tracerDrugHash[tracerDrugInFacility[0]['drug.drug_code'] + '@' + tracerDrugInFacility[0]['facility.facility_code']] = newTracerDrug;
    });
  }

  function populateLastPeriodCMMData(cmmEntries, tracerDrugHash) {
    _.forEach(cmmEntries, function (cmmEntry) {
      if (cmmEntry.cmm !== undefined && cmmEntry.cmm !== null) {
        tracerDrugHash[cmmEntry.product + '@' + cmmEntry.facilityCode].cmmValue = cmmEntry.cmm;
      }
    });
  }

  function getDataForExport(selectedDrugs, province, district, startTime, endTime, allTracerDrugs) {

    var weeklyDrugsDataPromise = getWeeklyDrugsData(selectedDrugs, province, district, startTime, endTime);
    var lastPeriodCMMDataPromise = getCMMForTracerDrugInPeriod(selectedDrugs, province, district, endTime, allTracerDrugs);
    $q.all([weeklyDrugsDataPromise, lastPeriodCMMDataPromise]).then(function (result) {
      var tracerDrugs = result[0].data;
      var cmmEntries = result[1].data;

      var tracerDrugHash = {};
      populateWeeklyTracerDrugData(tracerDrugs, tracerDrugHash, startTime, endTime);
      populateLastPeriodCMMData(cmmEntries, tracerDrugHash);

      data.reportContent = _.values(tracerDrugHash);
      ReportExportExcelService.exportAsXlsx(data, messageService.get('report.file.tracer.drugs.report'));
    });
  }

  return {
    getDataForExport: getDataForExport
  };
});