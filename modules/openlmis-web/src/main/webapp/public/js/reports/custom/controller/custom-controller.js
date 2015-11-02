/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
function CustomReportController($scope, $window, reports, CustomReportValue) {

  $scope.reports = reports;
  $scope.displayReports = _.groupBy(reports, 'category');
  $scope.categories = _.uniq(_.pluck(reports, 'category'));

  $scope.isReady = true;
  if (!angular.isUndefined($scope.filter) && angular.isUndefined($scope.filter.report_key)) {
    $scope.OnFilterChanged();
  }

  $scope.OnReportTypeChanged = function(){
    $scope.OnFilterChanged();
  };

  function updateFilterSection($scope) {

    // avoid having the blinking effect if the report has not been changed.
    if ($scope.previous_report_key !== $scope.filter.report_key) {
      $scope.previous_report_key = $scope.filter.report_key;

      $scope.report = _.findWhere($scope.reports, {reportkey: $scope.filter.report_key});

      $scope.report.columns = angular.fromJson($scope.report.columnoptions);
      if ($scope.report.filters !== null && $scope.report.filters !== '') {
        $scope.report.currentFilters = angular.fromJson($scope.report.filters);
        var required = _.pluck($scope.report.currentFilters,'name');
        $scope.requiredFilters = [];
        angular.forEach(required, function(r){
          $scope.requiredFilters[r] = r;
        });
      } else {
        $scope.report.currentFilters = [];
          $scope.requiredFilters = [];
      }
    }
  }

  $scope.OnFilterChanged = function () {
    if (angular.isUndefined($scope.filter) || angular.isUndefined($scope.filter.report_key) || !$scope.isReady) {
      return;
    }
    $scope.applyUrl();
    updateFilterSection($scope);

    //clear existing data
    $scope.data = [];
    $scope.meta = undefined;
    CustomReportValue.get($scope.getSanitizedParameter(), function (data) {
      $scope.meta = data;
      $scope.data = data.values;
    });
  };

  $scope.exportCSV = function() {
    var params = jQuery.param($scope.getSanitizedParameter());
    var url = '/report-api/report.csv?' + params;
    $window.open(url, '_blank');
  };

}

CustomReportController.resolve = {
  reports: function ($q, $timeout, CustomReportList) {
    var deferred = $q.defer();
    $timeout(function () {
      CustomReportList.get(function (data) {
        deferred.resolve(data.reports);
      });
    }, 100);
    return deferred.promise;
  }
};
