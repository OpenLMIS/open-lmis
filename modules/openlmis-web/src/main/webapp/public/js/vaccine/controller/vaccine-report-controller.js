/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function VaccineReportPOCReportController($scope){
    $scope.vaccineData = [
        {name:'Vaccine A'},{name:'Vaccine B'},{name: 'Vaccine C'}
    ];
    $scope.districtReporting = {openPanel: false};
    $scope.actualVsTarget = [];
    $scope.dropOutRate = [];
    $scope.immunizationOfChildren =[];
    $scope.immunizationOfPregnant = [];

    /* Bar Chart */
    var barChartTicks = [[1, "Tab1"], [2, "Tab2"], [3, "Tab3"],[4, "Tab4"],[5, "Tab5"]];

    function GenerateSeries(added){
        var data = [];
        var start = 0 + added;
        var end = 100 + added;

        for(i=1;i<=5;i++){
            var d = Math.floor(Math.random() * (end - start + 1) + start);
            data.push([i, d]);
            start++;
            end++;
        }

        return data;
    }

    $scope.barChartData =  [{ label: "Random Tabs Data Size", data:  GenerateSeries(0), color: "#5482FF" }];
    $scope.barChartOption = {
        series: {
            bars: {show: true,
                align: "center",
                lineWidth:0,
                fill: 0.6,
                barWidth: 0.3
            }
        },
        xaxis: {
            axisLabel: "Sample tabs",
            axisLabelUseCanvas: false,
            ticks: barChartTicks
        },
        yaxis: {
            axisLabel: "Data Size",
            axisLabelUseCanvas: false,
            tickFormatter: function (v, axis) {
                return v + "kb";
            }
        },
        legend: {
            container:$("#barChartLegend"),
            noColumns: 0
        },
        grid:{
            clickable:true,
            hoverable: true,
            //autoHighlight: true,
            borderWidth: 1,
            borderColor: "#d6d6d6",
            minBorderMargin: 20,
            labelMargin: 10,
            backgroundColor: {
                colors: ["#FFF", "#CCC"]
            }
        },tooltip: true,
        tooltipOpts: {
            content: "%s of %x.0 is %y",
            shifts: {
                x: 20,
                y: 0
            },
            defaultTheme: false
        }
    };

    $scope.renderGraph = function(){
        $scope.renderIt = true;
    };
    $scope.renderIt = false;




}

function expandCollapseToggle(element) {
    $(element).parents('.accordion-section').siblings('.accordion-section').each(function () {
        $(this).find('.accordion-body').slideUp();
        $(this).find('.accordion-heading b').text('+');
    });
    $(element).siblings('.accordion-body').stop().slideToggle(function () {
        if ($(element).siblings('.accordion-body').is(':visible')) {
            $(element).find('b').text('-');
        } else {
            $(element).find('b').text('+');
        }
    });
    var offset = $(element).offset();
    var offsetTop = offset ? offset.top : undefined;
    $('body, html').animate({
        scrollTop: utils.parseIntWithBaseTen(offsetTop) + 'px'
    });
}
