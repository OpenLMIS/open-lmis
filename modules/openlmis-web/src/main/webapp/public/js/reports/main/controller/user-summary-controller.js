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

function UserSummaryReportController($scope, $window, ReportProgramsBySupervisoryNode, UserRoleAssignmentsSummary, UserSupervisoryNodes, GetAllRoles) {
    $scope.filterObject = {};

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadUserSummary();
    });
    $scope.loadUserSummary = function () {


        $scope.filter.max = 10000;
        UserRoleAssignmentsSummary.get($scope.filterObject, function (data) {
            $scope.total = 0;
            $scope.UserRolePieChartData = [];
            if (!isUndefined(data.userRoleAssignmentSummary)) {

                $scope.datarows = data.userRoleAssignmentSummary;
                for (var i = 0; i < data.userRoleAssignmentSummary.length; i++) {

                    $scope.UserRolePieChartData[i] = {
                        label: $scope.datarows[i].roleName,
                        data: $scope.datarows[i].totalRoles


                    };
                }
                bindChartEvent("#stocked-out-reporting", "plothover", flotChartHoverCursorHandler);

            } else {
                $scope.UserRolePieChartData = [];
            }

        });

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
    };
    $scope.processSupervisoryNodeChange = function () {
        var par = $scope.filterObject.supervisoryNodeId;
        if (!$scope.filterObject.supervisoryNodeId || $scope.filterObject.supervisoryNodeId === 'undefined' || $scope.filterObject.supervisoryNodeId === '') {
            par = 0;
        }
        ReportProgramsBySupervisoryNode.get({supervisoryNodeId: par}, function (data) {
            $scope.programs = data.programs;
            $scope.programs.unshift({'name': '--All Programs--'});
        });

        $scope.loadUserSummary();
    };


    $scope.OnFilterChanged = function () {

    };

    $scope.userSummaryPieChartOption = {
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
            container: $("#userSummaryReportLegend"),
            noColumns: 1,
            labelBoxBorderColor: "none",
            sorted: "descending",
            position: "w",
            backgroundOpacity: 1
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

    $scope.resetUserSummaryReportData = function () {
        $scope.UserRolePieChartData = null;
        $scope.datarows = null;
    };


    UserSupervisoryNodes.get(function (data) {
        $scope.supervisoryNodes = data.supervisoryNodes;
        $scope.supervisoryNodes.unshift({'name': '-- All Supervisory Nodes --'});

        ReportProgramsBySupervisoryNode.get({supervisoryNodeId: $scope.filterObject.supervisoryNodeId}, function (data) {
            $scope.programs = data.programs;
            $scope.programs.unshift({'name': '--All Programs--'});
        });

    });

    GetAllRoles.get(function (data) {
        $scope.roles = data.roles;
        $scope.roles.unshift({'name': '-- All Roles --'});

    });

    $scope.exportReport = function (type) {
        $scope.filterObject.pdformat = 1;
        var params = jQuery.param($scope.filterObject);
        var url = '/reports/download/user_summary/' + type + '?' + params;
        $window.open(url);
    };


}
