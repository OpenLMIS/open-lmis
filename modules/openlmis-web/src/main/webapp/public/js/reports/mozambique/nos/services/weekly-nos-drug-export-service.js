services.factory('WeeklyNosDrugExportService', function ($http, $filter, $q, $timeout, messageService, CubesGenerateUrlService, CubesGenerateCutParamsService, ReportLocationConfigService, ReportExportExcelService, DateFormatService) {

  var DATE_FORMAT = 'yyyy,MM,dd';
  
  var data = {};

  function getDataForExport(selectedDrugs, province, district, startTime, endTime, allNosDrugs) {

      //TODO use object
      data.selectedDrugs = selectedDrugs;
      data.province = province;
      data.district = district;
      data.startTime = $filter('date')(startTime, DATE_FORMAT);
      data.endTime = $filter('date')(endTime, DATE_FORMAT);
      data.allNosDrugs = allNosDrugs;
      data.reportType = "nosDrugReportGenerator";

      ReportExportExcelService.exportAsXlsxBackend(data, messageService.get('report.file.nos.drugs.report'));
  }

  return {
    getDataForExport: getDataForExport
  };
});