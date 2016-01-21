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
function CustomReportDesignerController($scope, reports, SaveCustomReport, CustomReportFullList){

  $scope.r = reports;
  $scope.reports = _.groupBy( $scope.r, 'category');

  $scope.init = function(){
    if($scope.sqleditor === undefined){
      $scope.sqleditor = ace.edit("sqleditor");
      $scope.sqleditor.setTheme("ace/theme/chrome");
      $scope.sqleditor.getSession().setMode("ace/mode/pgsql");

      $scope.filter = ace.edit("filtereditor");
      $scope.filter.setTheme("ace/theme/chrome");
      $scope.filter.getSession().setMode("ace/mode/json");

      $scope.column = ace.edit("columneditor");
      $scope.column.setTheme("ace/theme/chrome");
      $scope.column.getSession().setMode("ace/mode/json");
    }
    $scope.sqleditor.setValue($scope.current.query);
    $scope.filter.setValue($scope.current.filters);
    $scope.column.setValue($scope.current.columnoptions);
  };

  $scope.select = function(report){
    // clear previous values and message on screen
    $scope.columns = $scope.data = [];
    $scope.message = undefined;
    $scope.current = report;
    $scope.init();
  };

  $scope.New = function(){
    $scope.current = {quer:'', filters:'[]',columnoptions:'[]'};
    $scope.init();
  };

  $scope.Save = function(){
    $scope.current.query = $scope.sqleditor.getValue();
    $scope.current.filters = $scope.filter.getValue();
    $scope.current.columnoptions = $scope.column.getValue();

    var save = SaveCustomReport.save($scope.current);
    save.$promise.then(function(){
      $scope.message =  $scope.current.name + ' saved successfully!';
      $scope.current = undefined;
      $scope.r = CustomReportFullList.get();
      $scope.r.$promise.then(function(){
        $scope.reports = _.groupBy( $scope.r.reports, 'category');
      });
    });
  };
}

CustomReportDesignerController.resolve = {
  reports: function ($q, $timeout, CustomReportFullList) {
    var deferred = $q.defer();
    $timeout(function () {
      CustomReportFullList.get(function (data) {
        deferred.resolve(data.reports);
      });
    }, 100);
    return deferred.promise;
  }
};
