/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function DistrictConsumptionReportController($scope,  AggregateConsumptionReport, ReportUserPrograms) {

    //filter form data section

    $scope.wideOption = {'multiple': true, dropdownCss: { 'min-width': '500px' }};

    $scope.OnFilterChanged = function(){
      $scope.data = $scope.datarows = [];

      $scope.filter.max = 10000;
      AggregateConsumptionReport.get($scope.filter, function(data) {
        if(data.pages !== undefined){
          $scope.data = data.pages.rows;
          $scope.paramsChanged($scope.tableParams);
        }
      });

        ReportUserPrograms.get(function (data) {
            $scope.programs = data.programs;

            $scope.programs.forEach( function(program) {

                if(program.id == $scope.filter.program) {

                    if (program.name == 'ILS')
                        $scope.reportFooterNote = 'Note: Estimated consumption is the sum of dispensed quantity. Adjusted Consumption is adjusted for days out of stock.';
                    else if (program.name == 'ARV')
                        $scope.reportFooterNote = 'Note: Estimated consumption is the sum of dispensed quantity, adjusted consumption includes the estimates for new patients';
                }

            });
        });


    };

   $scope.exportReport   = function (type){

        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.filter);
        var url = '/reports/download/aggregate_consumption/' + type +'?' + params;
        window.open(url);
    };


}
