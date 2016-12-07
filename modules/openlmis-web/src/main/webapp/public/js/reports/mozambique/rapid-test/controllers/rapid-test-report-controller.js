function RapidTestReportController($scope, $controller, CubesGenerateCutParamsService, CubesGenerateUrlService, $filter, $http, messageService) {
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
          }
        });
        var dataGroupedByName = _(dataAggregatedByNameAndCode).groupBy('item_name');
        var formattedData = _(dataGroupedByName).map(function (group, key) {
          var item = {};
          item.item_name = key;
          item.formatted_name = messageService.get('report.rapid.test.' + key);
          _.each(group, function (itemInGroup) {
            var code = itemInGroup.column_code;
            item[code] = itemInGroup.item_total_value
          });
          return item;
        });

        $scope.rapidTestReportData = formattedData;
      }
    });
  }
}
