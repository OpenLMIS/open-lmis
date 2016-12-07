function RapidTestReportController($scope, $controller, CubesGenerateCutParamsService, CubesGenerateUrlService, $filter, $http, messageService, ReportExportExcelService) {
  $controller("BaseProductReportController", {$scope: $scope});

  $scope.location = '';
  $scope.$on('$viewContentLoaded', function () {
    $scope.loadHealthFacilities();
  });

  $scope.loadReport = loadReportAction;

  function loadReportAction() {
    if ($scope.checkDateValidRange()) {
      var params = $scope.reportParams;
      $scope.locationIdToCode(params);
      getReportDataFromCubes();
    }
  }

  function getReportDataFromCubes() {
    var selectedStartTime = $filter('date')($scope.reportParams.startTime, "yyyy,MM,dd");
    var cutsParams = CubesGenerateCutParamsService.generateCutsParams('startdate',
        selectedStartTime, undefined, $scope.reportParams.selectedFacility, undefined, $scope.reportParams.selectedProvince, $scope.reportParams.selectedDistrict);

    $scope.location = $scope.reportParams.selectedProvince.name;

    $http.get(CubesGenerateUrlService.generateFactsUrl('vw_rapid_test', cutsParams)).then(function (result) {
      if(result.data) {
        var groups = _.groupBy(result.data, function(value){
          return value.item_name + '#' + value.column_code;
        });
        var dataAggregatedByNameAndCode = _.map(groups, function(group){
          return {
            item_name: group[0].item_name,
            column_code: group[0].column_code,
            item_total_value:  _(group).reduce(function(item, next_item) { return item + next_item.item_value; }, 0)
          };
        });
        var dataGroupedByName = _(dataAggregatedByNameAndCode).groupBy('item_name');
        var formattedData = _(dataGroupedByName).map(function (group, key) {
          var item = {};
          item.item_name = key;
          item.formatted_name = messageService.get('report.rapid.test.' + key);
          _.each(group, function (itemInGroup) {
            var code = itemInGroup.column_code;
            item[code] = itemInGroup.item_total_value;
          });
          return item;
        });
        $scope.rapidTestReportData = formattedData;
      }
    });
  }

  $scope.partialPropertiesFilter = function(searchValue) {
    return function(entry) {
      var regex = new RegExp(searchValue, "gi");

      return regex.test(entry.formatted_name)||
          regex.test(entry['HIV-DETERMINE-CONSUME']) ||
          regex.test(entry['HIV-DETERMINE-POSITIVE']) ||
          regex.test(entry['HIV-UNIGOLD-CONSUME']) ||
          regex.test(entry['HIV-UNIGOLD-POSITIVE']) ||
          regex.test(entry['SYPHILLIS-CONSUME']) ||
          regex.test(entry['SYPHILLIS-POSITIVE']) ||
          regex.test(entry['MALARIA-CONSUME']) ||
          regex.test(entry['MALARIA-POSITIVE']);
    };
  };

  $scope.exportXLSX = function() {
    var data = {
      reportHeaders: {
        item_name: '',
        consume_hiv_determine: messageService.get('report.consume') +': ' + messageService.get('report.hiv.determine'),
        positive_hiv_determine: messageService.get('report.positive') +': ' + messageService.get('report.hiv.determine'),
        consume_hiv_unigold: messageService.get('report.consume') +': ' + messageService.get('report.hiv.unigold'),
        positive_hiv_unigold: messageService.get('report.positive') +': ' + messageService.get('report.hiv.unigold'),
        consume_syphillis: messageService.get('report.consume') +': ' + messageService.get('report.syphillis'),
        positive_syphillis: messageService.get('report.positive') +': ' + messageService.get('report.syphillis'),
        consume_malaria: messageService.get('report.consume') +': ' + messageService.get('report.malaria'),
        positive_malaria: messageService.get('report.positive') +': ' + messageService.get('report.malaria'),
        province: messageService.get('report.header.province'),
        district: messageService.get('report.header.district'),
        facility: messageService.get('report.header.facility'),
        reportStartDate: messageService.get('report.header.report.start.date'),
        reportEndDate: messageService.get('report.header.report.end.date')
      },
      reportContent: []
    };

    if($scope.rapidTestReportData) {
      $scope.rapidTestReportData.forEach(function (reportContent) {
        var rapidTestReportContent = {};
        rapidTestReportContent.item_name = reportContent.formatted_name;
        rapidTestReportContent.consume_hiv_determine = reportContent['HIV-DETERMINE-CONSUME'];
        rapidTestReportContent.positive_hiv_determine = reportContent['HIV-DETERMINE-POSITIVE'];
        rapidTestReportContent.consume_hiv_unigold = reportContent['HIV-UNIGOLD-CONSUME'];
        rapidTestReportContent.positive_hiv_unigold = reportContent['HIV-UNIGOLD-POSITIVE'];
        rapidTestReportContent.consume_syphillis = reportContent['SYPHILLIS-CONSUME'];
        rapidTestReportContent.positive_syphillis = reportContent['SYPHILLIS-POSITIVE'];
        rapidTestReportContent.consume_malaria = reportContent['MALARIA-CONSUME'];
        rapidTestReportContent.positive_malaria = reportContent['MALARIA-POSITIVE'];
        rapidTestReportContent.province = $scope.reportParams.selectedProvince ? $scope.reportParams.selectedProvince.name : '[All]';
        rapidTestReportContent.district = $scope.reportParams.selectedDistrict ? $scope.reportParams.selectedDistrict.name : '[All]';
        rapidTestReportContent.facility = $scope.reportParams.selectedFacility ? $scope.reportParams.selectedFacility.name : '[All]';
        rapidTestReportContent.reportStartDate = $filter('date')($scope.reportParams.startTime, 'dd/MM/yyyy');
        rapidTestReportContent.reportEndDate =  $filter('date')($scope.reportParams.endTime, 'dd/MM/yyyy');
        data.reportContent.push(rapidTestReportContent);
      });
      ReportExportExcelService.exportAsXlsx(data, messageService.get('report.file.rapid.test.report'));
    }
  };

}
