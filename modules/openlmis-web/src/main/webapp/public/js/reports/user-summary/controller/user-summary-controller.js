/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function UserSummaryReportController($scope,$window, UserSummaryReport,UserSupervisoryNodes,GetAllRoles,$timeout) {

    $scope.OnFilterChanged = function() {
        // clear old data if there was any
        $scope.data = $scope.datarows = [];
        $scope.filter.max = 10000;
        UserSummaryReport.get($scope.filter, function(data) {
            if (data.pages !== undefined && data.pages.rows !== undefined) {
                $scope.data = data.pages.rows;
                $scope.paramsChanged($scope.tableParams);
            }
        });
    };

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

    $("#afloat1").bind("plotclick", function (event, pos, item) {

        if(item) {
            var barIndex =  item.datapoint[0];
            if (item.datapoint[0]) {
                $("#afloat1").css('cursor','crosshair');
            } else {
                $("#afloat1").css('cursor','auto');
            }
            if(++barIndex <=4){
                var tabId ='dashboard-tab-'+ barIndex;

                createTab(tabId);
            }
        }
    });

    function flotChartHoverCursorHandler(event,pos,item){

        if (item && !isUndefined(item.dataIndex)) {
            $(event.target).css('cursor','pointer');
        } else {
            $(event.target).css('cursor','auto');
        }
    }

    $("#afloat1").bind("plothover", function (event, pos, item) {
        flotChartHoverCursorHandler(event, pos, item);
    });
    /* End Bar Chart */
    /* Pie Chart */
    $scope.districtReporting = {};
    $scope.districtReporting.openPanel = true;

    var pieChartSeries = 3;
    var pieChartColors = ["#05BC57","#CC0505", "#FFFF05"];
    var pieChartLabels = ["Reported on time","Did not report","Reported late"];
    $timeout(function(){

        $scope.pieChartData = [];
        var series = 3;
        var colors = ["#05BC57","#CC0505", "#FFFF05"];
        var labels = ["Reported on time","Did not report","Reported late"];

        for (var i = 0; i < series; i++) {
            $scope.pieChartData[i] = {
                label: labels[i],
                data: Math.floor(Math.random() * 100) + 1,
                color: colors[i]
            };
        }
        $scope.pieChartOption = {
            series: {
                pie: {
                    show: true,
                    radius: 1,

                    label: {
                        show: true,
                        radius: 2 / 3,
                        formatter: function (label, series) {
                            return '<div class="pieLabel">' + Math.round(series.percent) + '%</div>';
                        },
                        threshold: 0.1
                    }
                }
            },
            legend: {
                container:$("#districtReportLegend"),
                noColumns: 0,
                labelBoxBorderColor: "none"
            },
            grid:{
                hoverable: true,
                borderWidth: 1,
                borderColor: "#d6d6d6",
                //minBorderMargin: 20,
                //labelMargin: 10,
                backgroundColor: {
                    colors: ["#FFF", "#CCC"]
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

    },100);


    /* End Pie Chart */





    UserSupervisoryNodes.get(function (data) {
        $scope.supervisoryNodes = data.supervisoryNodes;
        $scope.supervisoryNodes.unshift({'name': '-- Select SupervisoryNodes --'});
    });

    GetAllRoles.get(function (data) {
        $scope.roles = data.roles;
        $scope.roles.unshift({'name': '-- Select role --'});
    });

    $scope.exportReport = function(type) {
        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.filter);
        var url = '/reports/download/user_summary/' + type + '?' + params;
        $window.open(url);
    };


}
