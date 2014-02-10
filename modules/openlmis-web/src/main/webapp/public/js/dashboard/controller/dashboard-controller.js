
/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function AdminDashboardController($scope) {
    /* Bar Chart */
    var barChartTicks = [[1, "Tab1"], [2, "Tab2"], [3, "Tab3"]];

    function GenerateSeries(added){
        var data = [];
        var start = 0 + added;
        var end = 100 + added;

        for(i=1;i<=3;i++){
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
            bars: {show: true}
        },
        bars: {
            align: "center",
            barWidth: 0.5
        },
        xaxis: {
            axisLabel: "Sample tabs",
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            axisLabelFontFamily: 'Verdana, Arial',
            axisLabelPadding: 10,
            ticks: barChartTicks
        },
        yaxis: {
            axisLabel: "Data Size",
            axisLabelUseCanvas: true,
            axisLabelFontSizePixels: 12,
            axisLabelFontFamily: 'Verdana, Arial',
            axisLabelPadding: 3,
            tickFormatter: function (v, axis) {
                return v + "kb";
            }
        },
        legend: {
            noColumns: 0,
            labelBoxBorderColor: "#000000",
            position: "nw"
        },
        grid:{
            clickable:true,
            hoverable: true,
            borderWidth: 2,
            backgroundColor: { colors: ["#ffffff", "#EDF5FF"] }
        }
    };


    $("#afloat1").bind("plotclick", function (event, pos, item) {

        var showTab = 2- item.dataIndex;
        $('#dashboard-tabs li:eq('+showTab+') a').tab('show');
    });

    /* End Bar Chart */

    /* Pie Chart */

    var pieChartSeries = 3;
    var pieChartColors = ["#05BC57","#CC0505", "#FFFF05"];
    var pieChartLabels = ["Reported on time","Did not report","Reported late"];

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
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:black;">' + Math.round(series.percent) + '%</div>';
                    },
                    threshold: 0.1
                }
            }
        },
        legend: {
            show: true
        }
    };

    /* End Pie Chart */

    /* Calendar */
        var date = new Date();
        var d = date.getDate();
        var m = date.getMonth();
        var y = date.getFullYear();

    $scope.events = [
        {title: 'All Day Event',start: new Date(y, m, 1)},
        {title: 'Long Event',start: new Date(y, m, d - 5),end: new Date(y, m, d - 2)},
        {id: 999,title: 'Repeating Event',start: new Date(y, m, d - 3, 16, 0),allDay: false},
        {id: 999,title: 'Repeating Event',start: new Date(y, m, d + 4, 16, 0),allDay: false},
        {title: 'Birthday Party',start: new Date(y, m, d + 1, 19, 0),end: new Date(y, m, d + 1, 22, 30),allDay: false},
        {title: 'Click for Google',start: new Date(y, m, 28),end: new Date(y, m, 29),url: 'http://google.com/'}
    ];

    /* event sources array*/
    $scope.eventSources = [$scope.events];
        /* config object */
        $scope.uiConfig = {
            calendar:{
                height: 450,
                editable: true,
                header: {
                    left: 'prev,next today',
                    center: 'title',
                    right: 'month,basicWeek,basicDay'
                },
                eventClick: $scope.alertOnEventClick,
                eventDrop: $scope.alertOnDrop,
                eventResize: $scope.alertOnResize
            }
        };
    /* End of Calendar */


    /* Easy pie chart */
    $scope.option1  = { animate:false, barColor:'#FEBA50', scaleColor:false, lineWidth:5, lineCap:'butt' };
    $scope.percent1 = 55;
    $scope.option2  = { animate:false, barColor:'#E23E3E', scaleColor:false, lineWidth:5 };
    $scope.percent2 = -46;
    $scope.option3  = { animate:false, barColor:'#38AB49', scaleColor:false, lineWidth:5};
    $scope.percent3 = 85;
    $scope.option4  = { animate:false, barColor:'#FFB848', scaleColor:false, lineWidth:5, lineCap:'butt' };
    $scope.percent4 = 55;

    $scope.anotherPercent = -45;
    $scope.anotherOptions = {
        animate:{
            duration:0,
            enabled:false
        },
        barColor:'#2C3E50',
        scaleColor:false,
        lineWidth:5,
       lineCap:'butt'
    };


    /* End of Easy pie chart */



}