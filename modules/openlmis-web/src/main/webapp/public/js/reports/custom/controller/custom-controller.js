/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
function CustomReportController($scope, CustomReportList, CustomReportValue) {

  CustomReportList.get(function (data) {
    $scope.reports = data.reports;

    $scope.displayReports = _.groupBy(data.reports, 'category');
    $scope.categories = _.uniq( _.pluck(data.reports, 'category') );

    $scope.isReady = true;
    if(!angular.isUndefined($scope.filter) && angular.isUndefined($scope.filter.report_key)){
      $scope.OnFilterChanged();
    }
  });

  function updateFilterSection() {

    // avoid having the blinking effect if the report has not been changed.
    if ($scope.previous_report_key != $scope.filter.report_key) {
      $scope.previous_report_key = $scope.filter.report_key;

      $scope.report = _.findWhere($scope.reports, {reportkey: $scope.filter.report_key});
      $scope.report.columns = angular.fromJson($scope.report.columnoptions);
      if ($scope.report.filters !== null && $scope.report.filters !== '') {
        $scope.report.currentFilters = angular.fromJson($scope.report.filters);
      } else {
        $scope.report.currentFilters = [];
      }
    }
  }

  $scope.OnFilterChanged = function () {

    if ( angular.isUndefined($scope.filter) || angular.isUndefined($scope.filter.report_key) || !$scope.isReady) {
      return;
    }
    updateFilterSection();

    $scope.applyUrl();

    //clear existing data
    $scope.data = [];
    $scope.meta = undefined;
    CustomReportValue.get($scope.filter, function (data) {
      $scope.meta = data;
      $scope.data = data.values;
    });

  };

}
