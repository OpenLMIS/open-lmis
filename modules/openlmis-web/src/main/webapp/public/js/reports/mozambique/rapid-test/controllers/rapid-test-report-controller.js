function RapidTestReportController($scope, $controller, CubesGenerateCutParamsService, CubesGenerateUrlService, $filter, $http, messageService, ReportExportExcelService) {
  $controller("BaseProductReportController", {$scope: $scope});

  $scope.location = '';
  $scope.$on('$viewContentLoaded', function () {
    $scope.loadHealthFacilities();
  });

  $scope.loadReport = loadReportAction;

  function loadReportAction() {
    if ($scope.validateProvince() && $scope.validateDistrict() && $scope.validateFacility() && $scope.checkDateValidRange()) {
      var params = $scope.reportParams;
      $scope.locationIdToCode(params);
      getReportDataFromCubes();
    }
  }

  function getReportDataFromCubes() {
    var selectedStartTime = $filter('date')($scope.reportParams.startTime, "yyyy,MM,dd");
    var cutsParams = CubesGenerateCutParamsService.generateCutsParams('startdate',
      selectedStartTime, undefined, $scope.reportParams.selectedFacility, undefined, $scope.reportParams.selectedProvince, $scope.reportParams.selectedDistrict);

    if ($scope.reportParams.selectedProvince) {
      $scope.location = $scope.reportParams.selectedProvince.name;
    }
    if ($scope.reportParams.selectedDistrict) {
      $scope.location = $scope.reportParams.selectedDistrict.name;
    }
    if ($scope.reportParams.selectedFacility) {
      $scope.location = $scope.reportParams.selectedFacility.name;
    }

    $http.get(CubesGenerateUrlService.generateFactsUrl('vw_rapid_test', cutsParams)).then(function (result) {
      if (result.data) {
        var groups = _.groupBy(result.data, function (value) {
          return value.item_name + '#' + value.column_code;
        });
        var dataAggregatedByNameAndCode = _.map(groups, function (group) {
          return {
            item_name: group[0].item_name,
            column_code: group[0].column_code,
            item_total_value: _(group).reduce(function (item, next_item) {
              return item + next_item.item_value;
            }, 0)
          };
        });
        var dataGroupedByName = _(dataAggregatedByNameAndCode).groupBy('item_name');
        $scope.rapidTestReportData = _(_.omit(dataGroupedByName, "APES")).map(function (group, key) {
          var item = {};
          item.item_name = key;
          item.formatted_name = messageService.get('report.rapid.test.' + key);
          _.each(group, function (itemInGroup) {
            var code = itemInGroup.column_code;
            item[code] = itemInGroup.item_total_value;
          });
          return item;
        });
        $scope.calculateTotalValues();
      }
    });
  }

  $scope.calculateTotalValues = function () {
    var total = {
      formatted_name: messageService.get('report.header.total'),
      CONSUME_HIVDETERMINE: 0,
      POSITIVE_HIVDETERMINE: 0,
      UNJUSTIFIED_HIVDETERMINE: 0,
      CONSUME_HIVUNIGOLD: 0,
      POSITIVE_HIVUNIGOLD: 0,
      UNJUSTIFIED_HIVUNIGOLD: 0,
      CONSUME_SYPHILLIS: 0,
      POSITIVE_SYPHILLIS: 0,
      UNJUSTIFIED_SYPHILLIS: 0,
      CONSUME_MALARIA: 0,
      POSITIVE_MALARIA: 0,
      UNJUSTIFIED_MALARIA: 0
    };
    $scope.totalValues = $scope.rapidTestReportData.reduce(function (a, b) {
      total.CONSUME_HIVDETERMINE = parse(a.CONSUME_HIVDETERMINE) + parse(b.CONSUME_HIVDETERMINE);
      total.POSITIVE_HIVDETERMINE = parse(a.POSITIVE_HIVDETERMINE) + parse(b.POSITIVE_HIVDETERMINE);
      total.UNJUSTIFIED_HIVDETERMINE = parse(a.UNJUSTIFIED_HIVDETERMINE) + parse(b.UNJUSTIFIED_HIVDETERMINE);
      total.CONSUME_HIVUNIGOLD = parse(a.CONSUME_HIVUNIGOLD) + parse(b.CONSUME_HIVUNIGOLD);
      total.POSITIVE_HIVUNIGOLD = parse(a.POSITIVE_HIVUNIGOLD) + parse(b.POSITIVE_HIVUNIGOLD);
      total.UNJUSTIFIED_HIVUNIGOLD = parse(a.UNJUSTIFIED_HIVUNIGOLD) + parse(b.UNJUSTIFIED_HIVUNIGOLD);
      total.CONSUME_SYPHILLIS = parse(a.CONSUME_SYPHILLIS) + parse(b.CONSUME_SYPHILLIS);
      total.POSITIVE_SYPHILLIS = parse(a.POSITIVE_SYPHILLIS) + parse(b.POSITIVE_SYPHILLIS);
      total.UNJUSTIFIED_SYPHILLIS = parse(a.UNJUSTIFIED_SYPHILLIS) + parse(b.UNJUSTIFIED_SYPHILLIS);
      total.CONSUME_MALARIA = parse(a.CONSUME_MALARIA) + parse(b.CONSUME_MALARIA);
      total.POSITIVE_MALARIA = parse(a.POSITIVE_MALARIA) + parse(b.POSITIVE_MALARIA);
      total.UNJUSTIFIED_MALARIA = parse(a.UNJUSTIFIED_MALARIA) + parse(b.UNJUSTIFIED_MALARIA);
      return total;
    }, total);
  };

  function parse(string) {
    var number = parseInt(string, 10);
    if (isNaN(number)) {
      return 0;
    }
    return number;
  }

  $scope.partialPropertiesFilter = function (searchValue) {
    return function (entry) {
      var regex = new RegExp(searchValue, "gi");

      return regex.test(entry.formatted_name) ||
        regex.test(entry.CONSUME_HIVDETERMINE) ||
        regex.test(entry.POSITIVE_HIVDETERMINE) ||
        regex.test(entry.UNJUSTIFIED_HIVDETERMINE) ||
        regex.test(entry.CONSUME_HIVUNIGOLD) ||
        regex.test(entry.POSITIVE_HIVUNIGOLD) ||
        regex.test(entry.UNJUSTIFIED_HIVUNIGOLD) ||
        regex.test(entry.CONSUME_SYPHILLIS) ||
        regex.test(entry.POSITIVE_SYPHILLIS) ||
        regex.test(entry.UNJUSTIFIED_SYPHILLIS) ||
        regex.test(entry.CONSUME_MALARIA) ||
        regex.test(entry.POSITIVE_MALARIA) ||
        regex.test(entry.UNJUSTIFIED_MALARIA);
    };
  };

  $scope.exportXLSX = function () {
    var data = {
      reportTitles: [
        [
          messageService.get('report.header.province'),
          $scope.reportParams.selectedProvince ?
            $scope.reportParams.selectedProvince.name : messageService.get('label.all'),
          messageService.get('report.header.district'),
          $scope.reportParams.selectedDistrict ?
            $scope.reportParams.selectedDistrict.name : messageService.get('label.all'),
          messageService.get('report.header.facility'),
          $scope.reportParams.selectedFacility ?
            $scope.reportParams.selectedFacility.name : messageService.get('label.all'),
        ],
        [
          messageService.get('report.header.generated.for'),
          $filter('date')($scope.reportParams.startTime, 'dd/MM/yyyy') + '-' +
          $filter('date')($scope.reportParams.endTime, 'dd/MM/yyyy')
        ]
      ],
      reportHeaders: {
        item_name: '',
        consume_hiv_determine: messageService.get('report.consume') + ': ' + messageService.get('report.hiv.determine'),
        positive_hiv_determine: messageService.get('report.positive') + ': ' + messageService.get('report.hiv.determine'),
        unjustified_hiv_determine: messageService.get('report.unjustified') + ': ' + messageService.get('report.hiv.determine'),
        consume_hiv_unigold: messageService.get('report.consume') + ': ' + messageService.get('report.hiv.unigold'),
        positive_hiv_unigold: messageService.get('report.positive') + ': ' + messageService.get('report.hiv.unigold'),
        unjustified_hiv_unigold: messageService.get('report.unjustified') + ': ' + messageService.get('report.hiv.unigold'),
        consume_syphillis: messageService.get('report.consume') + ': ' + messageService.get('report.syphillis'),
        positive_syphillis: messageService.get('report.positive') + ': ' + messageService.get('report.syphillis'),
        unjustified_syphillis: messageService.get('report.unjustified') + ': ' + messageService.get('report.syphillis'),
        consume_malaria: messageService.get('report.consume') + ': ' + messageService.get('report.malaria'),
        positive_malaria: messageService.get('report.positive') + ': ' + messageService.get('report.malaria'),
        unjustified_malaria: messageService.get('report.unjustified') + ': ' + messageService.get('report.malaria')
      },
      reportContent: []
    };

    function setColumnValues(rapidTestReportContent, reportContent) {
      rapidTestReportContent.item_name = reportContent.formatted_name;
      rapidTestReportContent.consume_hiv_determine = reportContent.CONSUME_HIVDETERMINE;
      rapidTestReportContent.positive_hiv_determine = reportContent.POSITIVE_HIVDETERMINE;
      rapidTestReportContent.unjustified_hiv_determine = reportContent.UNJUSTIFIED_HIVDETERMINE;
      rapidTestReportContent.consume_hiv_unigold = reportContent.CONSUME_HIVUNIGOLD;
      rapidTestReportContent.positive_hiv_unigold = reportContent.POSITIVE_HIVUNIGOLD;
      rapidTestReportContent.unjustified_hiv_unigold = reportContent.UNJUSTIFIED_HIVUNIGOLD;
      rapidTestReportContent.consume_syphillis = reportContent.CONSUME_SYPHILLIS;
      rapidTestReportContent.positive_syphillis = reportContent.POSITIVE_SYPHILLIS;
      rapidTestReportContent.unjustified_syphillis = reportContent.UNJUSTIFIED_SYPHILLIS;
      rapidTestReportContent.consume_malaria = reportContent.CONSUME_MALARIA;
      rapidTestReportContent.positive_malaria = reportContent.POSITIVE_MALARIA;
      rapidTestReportContent.unjustified_malaria = reportContent.UNJUSTIFIED_MALARIA;
    }

    if ($scope.rapidTestReportData) {
      $scope.rapidTestReportData.forEach(function (reportContent) {
        var rapidTestReportContent = {};
        setColumnValues(rapidTestReportContent, reportContent);
        data.reportContent.push(rapidTestReportContent);
      });
      var total = {};
      setColumnValues(total, $scope.totalValues);
      data.reportContent.push(total);
      ReportExportExcelService.exportAsXlsx(data, messageService.get('report.file.rapid.test.report'));
    }
  };

}
