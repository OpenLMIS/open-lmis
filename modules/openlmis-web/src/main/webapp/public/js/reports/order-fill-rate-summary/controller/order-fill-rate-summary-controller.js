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
function OrderFillRateReportSummaryController($scope, OrderFillRateSummaryReport, messageService, GetOrderFillRateSummary) {
    $scope.$watch('filter.program', function (value){
        $scope.OnFilterChanged();
    });
    $scope.$watch('filter.schedule', function (value){
        $scope.OnFilterChanged();
    });
    $scope.OnFilterChanged = function () {

        $scope.data = $scope.datarows = [];
        $scope.filter.max = 10000;
        OrderFillRateSummaryReport.get($scope.filter,
            function (data) {
                $scope.total = 0;
                $scope.OrderFillRateSummaryPieChartData = [];

                if (data.pages !== undefined && data.pages.rows !== undefined) {

                    $scope.dataRows = data.pages.rows;

                    var totalData = _.pluck($scope.dataRows, 'totalOrderFillRate');
                    if (totalData[0] === 0 && totalData[1] === 0 && totalData[2] === 0) {
                        $scope.resetOrderFillRateSummaryData();
                        return;
                    }
                    var color = {A: '#008000', M: '#FFA500', L: '#FF0000'};
                    for (var i = 0; i < $scope.dataRows.length; i++) {
                        var labelKey = 'label.orderFillRateSummary.status.' + $scope.dataRows[i].orderFillRateStatus;
                        var label = messageService.get(labelKey);
                        $scope.OrderFillRateSummaryPieChartData[i] = {
                            label: label,
                            data: $scope.dataRows[i].totalOrderFillRate,
                            color: color[$scope.dataRows[i].orderFillRateStatus]
                        };

                    }
                    $scope.OrderFillRateSummaryPieChartOptionFunction();
                    $scope.orderFillRateRenderedData = {
                        orderFillRateStatus: _.pairs(_.object(_.range(data.pages.rows.length), _.pluck(data.pages.rows, 'orderFillRateStatus')))

                    };

                    bindChartEvent("#order-fill-rate-summary", "plotclick", $scope.orderFillRateChartClickHandler);
                    bindChartEvent("#order-fill-rate-summary", flotChartHoverCursorHandler);


                } else {

                    $scope.resetOrderFillRateSummaryData();
                }
            });

    };
    $scope.resetOrderFillRateSummaryData = function () {
        $scope.OrderFillRateSummaryPieChartData = null;
        $scope.OrderFillRateSummaryPieChartOption = null;
        $scope.dataRows = null;
        $scope.orderFillRateRenderedData = null;

    };


    $scope.OrderFillRateSummaryPieChartOptionFunction = function () {

        $scope.OrderFillRateSummaryPieChartOption = {
            series: {
                pie: {
                    show: true,
                    align:"left",
                    radius: 1,
                    label: {
                        show: true,
                        radius: 2 / 4,
                        formatter: function (label, series) {
                            return '<div style="font-size:8pt;text-align:center;padding:1px;color:#FFFFFF;">' + Math.round(series.percent) + '%</div>';
                        },
                        threshold: 0.1
                    }
                }
            },
            legend: {
                container: $("#orderFillRateReportLegend"),
                noColumns: 0,
                labelBoxBorderColor: "none",
                // width: 20
                align:"left"
            },
            grid: {
                hoverable: true,
                clickable: true,
                borderWidth: 1,
                borderColor: "#d6d6d6",
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
    };


    function flotChartHoverCursorHandler(event, pos, item) {

        if (item && !isUndefined(item.dataIndex)) {
            $(event.target).css('cursor', 'pointer');
        } else {
            $(event.target).css('cursor', 'auto');
        }
    }

    function bindChartEvent(elementSelector, eventType, callback) {
        $(elementSelector).bind(eventType, callback);
    }

    $scope.orderFillRateChartClickHandler = function (event, pos, item) {

        if (item) {
            var status;
            var label;
            var labelKey;
            if (!isUndefined($scope.orderFillRateRenderedData.orderFillRateStatus)) {
                status = $scope.orderFillRateRenderedData.orderFillRateStatus[item.seriesIndex][1];
                GetOrderFillRateSummary.get({programId: $scope.filter.program,
                    periodId: $scope.filter.period,
                    scheduleId: $scope.filter.schedule,
                    facilityTypeId: $scope.filter.facilityType,
                    zoneId: $scope.filter.zone,
                    status: status
                }, function (data) {
                    if (data.orderFillRateSummary !== undefined) {
                        labelKey = 'label.orderFillRateSummary.title.' + status;
                        label = messageService.get(labelKey);
                        $scope.titleMessage = label;
                        $scope.data = data.orderFillRateSummary;
                        $scope.paramsChanged($scope.tableParams);
                    }

                });


            }
        }
       // $scope.$apply();
    };
}
