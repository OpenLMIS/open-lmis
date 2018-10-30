services.factory('ReportDataServices', function ($resource, $filter, ReportExportExcelService) {
  function getProductList() {
    return $resource('/reports/data', {}, {post: {method: 'POST'}});
  }
  
  function getDataForExport(data, reportName) {
    if (!data) {
      return;
    }
    
    ReportExportExcelService.exportAsXlsxBackend(
      utils.pickEmptyObject(data), reportName);
  }
  
  return {
    getProductList: getProductList,
    getDataForExport: getDataForExport
  };
});