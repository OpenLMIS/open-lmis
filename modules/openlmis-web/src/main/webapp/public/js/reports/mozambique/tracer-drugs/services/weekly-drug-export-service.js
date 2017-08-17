services.factory('WeeklyDrugExportService', function ($http, $filter, $q, $timeout, messageService, CubesGenerateUrlService, CubesGenerateCutParamsService, ReportExportExcelService, DateFormatService) {

  var data = {
    reportHeaders: [],
    reportContent: []
  };

  function addReportDateHeaders(dates) {
    var header = {
      drugCode: messageService.get('report.header.drug.code'),
      drugName: messageService.get('report.header.drug.name'),
      province: messageService.get('report.header.province'),
      district: messageService.get('report.header.district'),
      facility: messageService.get('report.header.facility'),
      cmm_value:messageService.get('report.header.cmm')
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

  function prepareDrugsRequestData(selectedDrugs, province, district, startTime, endTime) {
    var params = [{
      name: 'fields',
      value: ['location.province_name', 'location.district_name', 'facility.facility_code', 'facility.facility_name', 'drug.drug_name', 'drug.drug_code', 'date', 'soh']
    }];

    var drugParams = validateDrugs(selectedDrugs);

    return CubesGenerateUrlService.generateFactsUrlWithParams('vw_weekly_tracer_soh', CubesGenerateCutParamsService.generateCutsParams('cutDate',
      $filter('date')(startTime, "yyyy,MM,dd"),
      $filter('date')(endTime, "yyyy,MM,dd"),
      undefined, drugParams, province, district), params);
  }

  function getWeeklyDrugsData(selectedDrugs, province, district, startTime, endTime) {
    var requestUrl = prepareDrugsRequestData(selectedDrugs, province, district, startTime, endTime);
    return $http.get(requestUrl);
  }

  function getCMMForTracerDrugInPeriod(tracerDrugs, endTime) {
    var endDateParam = new Date($filter('date')(endTime, "yyyy,MM,dd"));
    var startDatePeriod = DateFormatService.formatDateWithStartDayOfPeriod(endDateParam);
    var endDatePeriod = DateFormatService.formatDateWithEndDayOfPeriod(endDateParam);

    var facilities = _.uniq(_.map(tracerDrugs, function (tracerDrug) {
      return tracerDrug["facility.facility_code"];
    }));

    var drugs = _.uniq(_.map(tracerDrugs, function (tracerDrug) {
      return tracerDrug["drug.drug_code"];
    }));

    var cutsParams = [
      {dimension: "facilityCode", values: facilities},
      {dimension: "product", values: drugs},
      {dimension: "periodbegin", values: [$filter('date')(startDatePeriod, "yyyy,MM,dd")], skipEscape: true},
      {dimension: "periodend", values: [$filter('date')(endDatePeriod, "yyyy,MM,dd")], skipEscape: true}
    ];
    return $http.get(CubesGenerateUrlService.generateFactsUrl('vw_cmm_entries', cutsParams));
  }

  function getSohOnDate(dates, newTracerDrug, tracerDrugInFacility) {
    _.forEach(dates, function (date) {
      var sohOnDate = _.findWhere(tracerDrugInFacility, {date: date});
      if (sohOnDate) {
        newTracerDrug[date] = sohOnDate.soh;
      } else {
        newTracerDrug[date] = "N/A";
      }
    });
  }

  function getColumnsFromDates(tracerDrugs) {
    var dates = _.map(tracerDrugs, function (tracerDrug) {
      return tracerDrug.date;
    });
    return _.uniq(dates);
  }

  function getDataForExport(selectedDrugs, province, district, startTime, endTime) {
    var tracerDrugHash = {};

    $q.when(getWeeklyDrugsData(selectedDrugs, province, district, startTime, endTime)).then(function (result) {
      var tracerDrugs = result.data;
      var dates = getColumnsFromDates(tracerDrugs);
      data.reportHeaders = addReportDateHeaders(dates);

      var tracerDrugsGroup = _.groupBy(tracerDrugs, function(tracerDrug) {
        return tracerDrug["drug.drug_code"] + tracerDrug["facility.facility_code"];
      });

      _.forEach(tracerDrugsGroup, function (tracerDrugInFacility) {
        var newTracerDrug = {};
        newTracerDrug.drugCode = tracerDrugInFacility[0]['drug.drug_code'];
        newTracerDrug.drugName = tracerDrugInFacility[0]['drug.drug_name'];
        newTracerDrug.province = tracerDrugInFacility[0]['location.province_name'];
        newTracerDrug.district = tracerDrugInFacility[0]['location.district_name'];
        newTracerDrug.facility = tracerDrugInFacility[0]['facility.facility_name'];
        getSohOnDate(dates, newTracerDrug, tracerDrugInFacility);

        tracerDrugHash[tracerDrugInFacility[0]['drug.drug_code'] + "@" + tracerDrugInFacility[0]['facility.facility_code']] = newTracerDrug;
      });

      $q.when(getCMMForTracerDrugInPeriod(tracerDrugs, endTime)).then(function (cmmEntries) {
        _.forEach(cmmEntries.data, function (cmmEntry) {
          tracerDrugHash[cmmEntry.product + "@" + cmmEntry.facilityCode].cmm_value = cmmEntry.cmm ? cmmEntry.cmm : "N/A";
        });
      }).then(function () {
        data.reportContent = _.values(tracerDrugHash);
        ReportExportExcelService.exportAsXlsx(data, messageService.get('report.file.tracer.drugs.report'));
      });
    });
  }

  return {
    getDataForExport: getDataForExport
  };
});