services.factory('ReportExportExcelService', function ($http, DateFormatService) {
    
    var exportAsXlsx = function (reportData, fileName) {
        if (!reportData.hasOwnProperty('reportHeaders') || !reportData.hasOwnProperty('reportContent')) {
            return new Error('The format of report data is not correct, it should contain reportHeaders and reportContent');

        }
        return doExportAsXlsx('/reports/download/excel', reportData, fileName);
    };
    
    var exportAsXlsxBackend = function (reportData, fileName) {
        return doExportAsXlsx('/reports/download/excel/backend', reportData, fileName);
    };

    function doExportAsXlsx(queryUrl, reportData, fileName) {
        fileName = formatFileNameWithDate(fileName);

        return $http({
            url: queryUrl,
            method: 'POST',
            data: JSON.stringify(reportData),
            headers: {
                'Content-type': 'application/json'
            },
            responseType: 'blob'
        }).success(function (data, status, headers, config) {
            if (data.size > 0) {
                var blob = new Blob([data], {type: "application/vnd.ms-excel"});
                saveAs(blob, fileName + '.xlsx');
            }
        }).error(function (error, status) {
            console.log(error);
        });
    };
    function formatFileNameWithDate(fileName) {
        return fileName + '_' + DateFormatService.formatDateWithUnderscore(new Date());
    }

    return {
        exportAsXlsx: exportAsXlsx,
        formatFileNameWithDate: formatFileNameWithDate,
        exportAsXlsxBackend: exportAsXlsxBackend
    };
});