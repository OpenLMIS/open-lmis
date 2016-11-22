services.factory('ReportExportExcelService', function($http) {

  var exportAsXlsx = function(reportData, fileName) {
    if(!reportData.hasOwnProperty('reportHeaders') || !reportData.hasOwnProperty('reportContent')) {
      return new Error('The format of report data is not correct, it should contain reportHeaders and reportContent');
    }

    return $http({
      url: '/reports/download/excel',
      method: 'POST',
      data: JSON.stringify(reportData),
      headers: {
        'Content-type': 'application/json'
      },
      responseType: 'blob'
    }).success(function (data, status, headers, config) {
      if(data.size>0) {
        var blob = new Blob([data], {type: "application/vnd.ms-excel"});
        saveAs(blob, fileName + '.xlsx');
      }
    }).error(function (error, status) {
      console.log(error);
    });
  };

  return {
    exportAsXlsx: exportAsXlsx
  };
});