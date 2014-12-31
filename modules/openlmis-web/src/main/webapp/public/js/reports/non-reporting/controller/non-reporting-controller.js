/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function NonReportingController($scope, NonReportingFacilities) {

  $scope.OnFilterChanged = function() {
    // clear old data if there was any
    $scope.data = $scope.datarows = [];
    $scope.filter.max = 10000;

    NonReportingFacilities.get($scope.filter, function(data) {
      if (data.pages !== undefined && data.pages.rows !== undefined) {
        $scope.summaries    =  data.pages.rows[0].summary;

        $scope.data = data.pages.rows[0].details;
        $scope.paramsChanged($scope.tableParams);

         $scope.nonReportingFacilitiesPieChartData = [];

          $scope.nonReportingFacilitiesPieChartData[0] = {
              label: $scope.summaries[5].name,
              data:  $scope.summaries[5].count,
              color: '#A3CC29'
          };

          $scope.nonReportingFacilitiesPieChartData[1] = {
              label:  $scope.summaries[4].name,
              data:   $scope.summaries[4].count,
              color: '#FFB445'
          };
      }
    });
  };


  $scope.exportReport   = function (type){
    var paramString = jQuery.param($scope.filter);
    var url = '/reports/download/non_reporting/' + type + '?' + paramString;
    window.open(url);
  };

  // Summary pie chart options
    $scope.nonReportingReportSummaryPieChartOption = {
        series: {
            pie: {
                show: true,
                radius: 1,
                label: {
                    show: true,
                    radius: 2 / 3,
                    formatter: function (label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:black;">' + Math.round(series.percent) + '%</div>';
                    },
                    threshold: 0.1
                }
            }
        },
        legend: {
            container:$("#nonReportingReportSummary"),
            noColumns: 0,
            labelBoxBorderColor: "none",
            sorted:"descending",
            backgroundOpacity:1,
            labelFormatter: function(label, series) {
                var percent= Math.round(series.percent);
                var number= series.data[0][1];
                return('<b>'+label+'</b>');
            }
        },
        grid:{
            hoverable: true,
            clickable: true,
            borderWidth: 1,
            borderColor: "#000",
            backgroundColor: {
                colors: ["red", "green", "yellow"]
            }
        },
        tooltip: true,
        tooltipOpts: {
            content: "%p.0%, %s",
            shifts: {
                x: 20,
                y: 0
            },
            defaultTheme: false
        }
    };
}
