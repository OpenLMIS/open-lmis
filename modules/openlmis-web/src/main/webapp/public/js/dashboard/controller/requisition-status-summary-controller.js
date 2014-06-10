/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function RequisitionStatusSummaryController($scope, $filter,RnRStatusByRequisitionGroupAndPeriod,dashboardMenuService,$location ,programsList, GetProgramWithBudgetingApplies,dashboardFiltersHistoryService, formInputValue, GetPeriod, RequisitionGroupsBySupervisoryNodeProgramSchedule, userPreferredFilterValues, ReportProgramsBySupervisoryNode, UserSupervisoryNodes, ReportSchedules, ReportPeriods, RequisitionGroupsByProgram, RequisitionGroupsByProgramSchedule, ReportProductsByProgram, OperationYears, ReportPeriodsByScheduleAndYear) {

    $scope.filterObject = {};

    $scope.formFilter = {};

    $scope.formPanel = {openPanel: true};


    initialize();

    function initialize() {
        $scope.showProductsFilter = false;
        $scope.$parent.currentTab = "label.rnr.status.current.tab";

    }

    UserSupervisoryNodes.get(function (data) {
        $scope.supervisoryNodes = data.supervisoryNodes;
        if (!isUndefined($scope.supervisoryNodes)) {
            $scope.supervisoryNodes.unshift({'name': formInputValue.supervisoryNodeOptionAll});
        }

    });

    OperationYears.get(function (data) {
        $scope.startYears = data.years;
        $scope.startYears.unshift(formInputValue.yearOptionAll);
    });

    ReportSchedules.get(function (data) {
        $scope.schedules = data.schedules;
        $scope.schedules.unshift({'name': formInputValue.scheduleOptionSelect});

    });

    $scope.filterProductsByProgram = function () {
        if (isUndefined($scope.formFilter.programId)) {

            return;
        }
        $scope.filterObject.programId = $scope.formFilter.programId;

        if (!isUndefined($scope.formFilter.supervisoryNodeId)) {
            RequisitionGroupsBySupervisoryNodeProgramSchedule.get(
                {programId: $scope.filterObject.programId,
                    scheduleId: isUndefined($scope.filterObject.scheduleId) ? 0 : $scope.filterObject.scheduleId,
                    supervisoryNodeId: $scope.filterObject.supervisoryNodeId
                }, function (data) {
                    $scope.requisitionGroups = data.requisitionGroupList;
                    if (!isUndefined($scope.requisitionGroups)) {
                        $scope.requisitionGroups.unshift({'name': formInputValue.requisitionOptionAll});
                    }
                });
        } else {
            RequisitionGroupsByProgram.get({program: $scope.filterObject.programId }, function (data) {
                $scope.requisitionGroups = data.requisitionGroupList;
                if (!isUndefined($scope.requisitionGroups)) {
                    $scope.requisitionGroups.unshift({'name': formInputValue.requisitionOptionAll});
                }
            });
        }

        $scope.loadRnRStatus();

    };

    $scope.loadRnRStatus= function(){
        if (isUndefined($scope.filterObject.periodId) || isUndefined($scope.filterObject.programId || isUndefined($scope.filterObject.rgroupId))) {
            return;
        }
         $scope.filterObject.requisitionGroupId= $scope.formFilter.rgroupId;

            RnRStatusByRequisitionGroupAndPeriod.get({requisitionGroupId:$scope.filterObject.requisitionGroupId,
                periodId:$scope.filterObject.periodId,
                programId:$scope.filterObject.programId
            },
            function (data) {

                $scope.total = 0;
                $scope.RnRStatusPieChartData = [];
                $scope.dataRows = [];
                $scope.datarows = [];

                if (!isUndefined(data.rnrStatus)) {

                    $scope.dataRows = data.rnrStatus;
                    var statusData = _.pluck($scope.dataRows,'status');
                    var totalData = _.pluck($scope.dataRows,'totalStatus');

                    var color=["#F83103","#37AC02","#02A8FA","#FAA702"];
                    for (var i = 0; i < $scope.dataRows.length; i++) {
                        $scope.total += $scope.dataRows[i].totalStatus;
                        $scope.RnRStatusPieChartData[i] = {

                            label: statusData[i],
                            data: totalData[i],
                            color:color[i]

                        };

                    }
                    $scope.rnrStatusPieChartOptionFunction();
                    $scope.rnrStatusRenderedData = {
                        status : _.pairs(_.object(_.range(data.rnrStatus.length), _.pluck(data.rnrStatus,'status')))

                    };

                    bindChartEvent("#rnr-status-report","plotclick",rnrStatusChartClickHandler);
                    bindChartEvent("#rnr-status-report",flotChartHoverCursorHandler);

                } else {
                    $scope.resetRnRStatusReportData();
                }
                $scope.paramsChanged($scope.tableParams);
            });
       // }

    };

    $scope.rnrStatusPieChartOptionFunction =function(){

        $scope.rnRStatusPieChartOption = {
            series: {
                pie: {
                    show: true,
                    radius: 1,
                    label: {
                        show: true,
                        radius: 3 / 4,
                        formatter: function (label, series) {
                            return '<div style="font-size:8pt;text-align:center;padding:2px;color:#000000;">' + Math.round(series.percent) + '%</div>';
                        },
                        threshold: 0.1
                    }

                }
            },
            legend: {
                show: true,
                container: $("#rnrStatusReportLegend"),
                noColumns: 0,
                labelBoxBorderColor: "none",
                width:20

            },
            grid: {
                hoverable: true,
                clickable: true,
                borderWidth: 1,
                borderColor: "#d6d6d6",
                backgroundColor: {
                    colors: ["#FFF", "#CCC","#FFF","#CCC"]
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


    function flotChartHoverCursorHandler(event,pos,item){

        if (item && !isUndefined(item.dataIndex)) {
           $(event.target).css('cursor','pointer');
        } else {
            $(event.target).css('cursor','auto');
        }
    }

    function bindChartEvent(elementSelector, eventType, callback){
        $(elementSelector).bind(eventType, callback);
    }

    function rnrStatusChartClickHandler(event, pos, item){
        if(item){
            var status;
            if(!isUndefined($scope.rnrStatusRenderedData.status)){
                status = $scope.rnrStatusRenderedData.status[item.seriesIndex][1];
            }
            var rnrDetailPath = '/rnr-status-report/program/'+$scope.filterObject.programId+'/period/'+$scope.filterObject.periodId;//+'/rgroup/'+$scope.filterObject.rgroupId;
            dashboardMenuService.addTab('menu.header.dashboard.rnr.status.detail','/public/pages/dashboard/index.html#'+rnrDetailPath,'RNR-STATUS-DETAIL',true, 8);
            $location.path(rnrDetailPath).search("status="+status+"&rgroupId="+$scope.filterObject.rgroupId);

            $scope.$apply();
        }

    }

    $scope.resetRnRStatusReportData = function () {
        $scope.RnRStatusPieChartData = null;
        $scope.rnRStatusPieChartOption =null;
        $scope.dataRows = null;
    };





    $scope.loadFacilitiesByRequisition = function () {
        if ($scope.formFilter.rgroupId == "All") {
            $scope.filterObject.rgroupId = -1;
        } else if ($scope.formFilter.rgroupId !== undefined || $scope.formFilter.rgroupId === "") {
            $scope.filterObject.rgroupId = $scope.formFilter.rgroupId;
            $.each($scope.requisitionGroups, function (item, idx) {
                if (idx.id == $scope.formFilter.rgroupId) {
                    $scope.filterObject.rgroup = idx.name;
                }
            });
        } else {
            $scope.filterObject.rgroupId = 0;

        }

    };
    $scope.processSupervisoryNodeChange = function () {

        $scope.filterObject.supervisoryNodeId = $scope.formFilter.supervisoryNodeId;

        if (isUndefined($scope.formFilter.supervisoryNodeId)) {
            $scope.programs = _.filter(programsList, function (program) {
                return program.name !== formInputValue.programOptionSelect;
            });

            $scope.programs.unshift({'name': formInputValue.programOptionSelect});
        } else if (!isUndefined($scope.formFilter.supervisoryNodeId)) {
            ReportProgramsBySupervisoryNode.get({supervisoryNodeId: $scope.filterObject.supervisoryNodeId}, function (data) {
                $scope.programs = data.programs;
                $scope.programs.unshift({'name': formInputValue.programOptionSelect});
            });
        }

        $scope.filterProductsByProgram();

    };


    $scope.processPeriodFilter = function () {
        if ($scope.formFilter.periodId == "All") {
            $scope.filterObject.periodId = -1;
        } else if ($scope.formFilter.periodId !== undefined || $scope.formFilter.periodId === "") {
            $scope.filterObject.periodId = $scope.formFilter.periodId;
            $.each($scope.periods, function (item, idx) {
                if (idx.id == $scope.formFilter.periodId) {
                    $scope.filterObject.period = idx.name;
                }
            });

        } else {
            $scope.filterObject.periodId = 0;
        }
        $scope.loadRnRStatus();
    };

    $scope.processRequisitionFilter = function () {

        if ($scope.formFilter.rgroupId && $scope.formFilter.rgroupId.length > 1) {
            $scope.formFilter.rgroupId = _.reject($scope.formFilter.rgroupId, function (rgroup) {
                return rgroup === "";
            });
        }

        $scope.filterObject.rgroupId = $scope.formFilter.rgroupId;

        $scope.loadRnRStatus();
    };

    $scope.changeSchedule = function () {

        if (!isUndefined($scope.formFilter.scheduleId)) {
            $scope.filterObject.scheduleId = $scope.formFilter.scheduleId;
        }

        if (!isUndefined($scope.filterObject.scheduleId)) {
            if (!isUndefined($scope.filterObject.year)) {
                ReportPeriodsByScheduleAndYear.get({scheduleId: $scope.filterObject.scheduleId, year: $scope.filterObject.year}, function (data) {
                    $scope.periods = data.periods;
                    $scope.periods.unshift({'name': formInputValue.periodOptionSelect});
                });
            } else {
                ReportPeriods.get({ scheduleId: $scope.filterObject.scheduleId }, function (data) {
                    $scope.periods = data.periods;
                    $scope.periods.unshift({'name': formInputValue.periodOptionSelect});

                });
            }
            if (!isUndefined($scope.filterObject.programId)) {
                if (!isUndefined($scope.filterObject.supervisoryNodeId)) {
                    RequisitionGroupsBySupervisoryNodeProgramSchedule.get(
                        {programId: $scope.filterObject.programId,
                            scheduleId: $scope.filterObject.scheduleId,
                            supervisoryNodeId: $scope.filterObject.supervisoryNodeId}, function (data) {
                            $scope.requisitionGroups = data.requisitionGroupList;
                            if (!isUndefined($scope.requisitionGroups)) {
                                $scope.requisitionGroups.unshift({'name': formInputValue.requisitionOptionAll});
                            }

                        });
                } else {
                    RequisitionGroupsByProgramSchedule.get({program: $scope.filterObject.programId, schedule: $scope.filterObject.scheduleId}, function (data) {
                        $scope.requisitionGroups = data.requisitionGroupList;
                        if (!isUndefined($scope.requisitionGroups)) {
                            $scope.requisitionGroups.unshift({'name': formInputValue.requisitionOptionAll});
                        }
                    });
                }

            }
        }


        $scope.loadRnRStatus();
    };

    $scope.changeScheduleByYear = function () {

        if (!isUndefined($scope.formFilter.year)) {
            $scope.filterObject.year = $scope.formFilter.year;

        }
        $scope.changeSchedule();

    };

    $scope.$on('$routeChangeStart', function () {
        var data = {};
        angular.extend(data, $scope.filterObject);
        dashboardFiltersHistoryService.add($scope.$parent.currentTab, data);
    });
    
    $scope.$on('$viewContentLoaded', function () {

        var filterHistory = dashboardFiltersHistoryService.get($scope.$parent.currentTab);

        if (isUndefined(filterHistory)) {
            if (!_.isEmpty(userPreferredFilterValues)) {
                var date = new Date();
                $scope.filterObject.supervisoryNodeId = $scope.formFilter.supervisoryNodeId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_SUPERVISORY_NODE];
                $scope.processSupervisoryNodeChange();

                $scope.filterObject.programId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_PROGRAM];
                $scope.filterObject.periodId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_PERIOD];

                if (!isUndefined($scope.filterObject.periodId)) {

                    GetPeriod.get({id: $scope.filterObject.periodId}, function (period) {
                        if (!isUndefined(period.year)) {
                            $scope.filterObject.year = period.year;
                        } else {
                            $scope.filterObject.year = date.getFullYear() - 1;
                        }
                    });
                }
                $scope.filterObject.scheduleId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_SCHEDULE];

                $scope.filterObject.rgroupId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_REQUISITION_GROUP];

                $scope.registerWatches();

                $scope.formFilter = $scope.filterObject;

            }

        } else {
            $scope.formFilter.supervisoryNodeId = filterHistory.supervisoryNodeId;
            $scope.processSupervisoryNodeChange();

            $scope.registerWatches();

            $scope.formFilter = $scope.filterObject = filterHistory;
        }

    });

    $scope.registerWatches = function () {

        $scope.$watch('formFilter.programId', function () {
            $scope.filterProductsByProgram();

        });
        $scope.$watch('formFilter.scheduleId', function () {
            $scope.changeSchedule();

        });

        $scope.$watch('formFilter.rgroupId',function(){
            $scope.loadRnRStatus();

        });

    };

    $scope.paramsChanged = function (params) {

        // slice array data on pages
        if ($scope.data === undefined) {
            $scope.datarows = [];
        } else {
            var data = $scope.data;
            var orderedData = params.filter ? $filter('filter')(data, params.filter) : data;
            orderedData = params.sorting ? $filter('orderBy')(orderedData, params.orderBy()) : data;

            params.total = orderedData.length;
            $scope.datarows = orderedData.slice((params.page - 1) * params.count, params.page * params.count);
            var i = 0;
            var baseIndex = params.count * (params.page - 1) + 1;
            while (i < $scope.datarows.length) {
                $scope.datarows[i].no = baseIndex + i;
                i++;
            }
        }
    };

    // watch for changes of parameters
    $scope.$watch('tableParams', $scope.paramsChanged, true);

}
