/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function SummaryReportController($scope, $filter, ngTableParams, SummaryReport, ReportSchedules, ReportPrograms, ReportPeriods, ProductCategoriesByProgram, ReportProductsByProgram, ReportFacilityTypes, GeographicZones, RequisitionGroupsByProgram, AllFacilites, $http, $routeParams, $location) {

  $scope.exportReport = function (type) {
    $scope.filterObject.pdformat = 1;
    var params = jQuery.param($scope.filterObject);
    var url = '/reports/download/summary/' + type + '?' + params;
    window.open(url);
  };
    
  $scope.OnFilterChanged = function() {
        // clear old data if there was any
        $scope.data = $scope.datarows = [];
        $scope.filter.max = 10000;
        SummaryReport.get($scope.filter, function(data) {
            if (data.pages !== undefined && data.pages.rows !== undefined) {
                $scope.data = data.pages.rows;
                $scope.paramsChanged($scope.tableParams);
            }
        });
    };

}
