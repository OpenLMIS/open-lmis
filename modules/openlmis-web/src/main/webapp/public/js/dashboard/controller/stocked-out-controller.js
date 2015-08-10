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
function StockedOutController($scope, $location,userPreferredFilters, $timeout, dashboardMenuService,programsList,FlatGeographicZoneList,UserGeographicZoneTree, formInputValue,dashboardFiltersHistoryService,ReportSchedules, ReportPeriods, ReportProductsByProgram, OperationYears, ReportPeriodsByScheduleAndYear,StockedOutFacilities, ngTableParams) {

    initialize();

    function initialize() {

        $scope.showProductsFilter = true;
        $scope.$parent.currentTab = 'STOCK-OUT';
        $scope.productSelectOption = {maximumSelectionSize : 1};
    }
    var filterHistory = dashboardFiltersHistoryService.get($scope.$parent.currentTab);

    if(isUndefined(filterHistory)){
        $scope.formFilter = $scope.filterObject  = userPreferredFilters || {};

    }else{
        $scope.formFilter = $scope.filterObject  = filterHistory || {};
    }

    $scope.formPanel = {openPanel:true};

    $scope.alertsPanel = {openAlertPanel:true, openStockPanel:true};

    FlatGeographicZoneList.get(function (data) {
        $scope.geographicZones = data.zones;
    });

    $scope.programs = programsList;
    $scope.programs.unshift({'name': formInputValue.programOptionSelect});

    $scope.loadGeoZones = function(){
        UserGeographicZoneTree.get({programId:$scope.formFilter.programId}, function(data){
            $scope.zones = data.zone;
            if(!isUndefined($scope.zones)){
                $scope.rootZone = $scope.zones.id;
            }
        });
    };

    OperationYears.get(function (data) {
        $scope.startYears = data.years;
        $scope.startYears.unshift(formInputValue.yearOptionAll);
    });


    ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
        $scope.schedules.unshift({'name': formInputValue.scheduleOptionSelect}) ;

    });

    $scope.filterProductsByProgram = function (){
        $scope.loadGeoZones();
        $scope.filterObject.programId = $scope.formFilter.programId;
        if(!isUndefined($scope.formFilter.programId)){
            ReportProductsByProgram.get({programId:  $scope.filterObject.programId}, function(data){
                $scope.products = data.productList;
            });
        }else{
            $scope.products = undefined;
            $scope.formFilter.productIdList = undefined;
            $scope.processProductsFilter();
        }
    };

    $scope.processZoneFilter = function(){
        $scope.filterObject.zoneId = $scope.formFilter.zoneId;
    };

    $scope.processProductsFilter = function (){
        $scope.filterObject.productIdList = $scope.formFilter.productIdList;
    };

    $scope.changeSchedule = function(){

        if (!isUndefined($scope.formFilter.scheduleId)) {
            $scope.filterObject.scheduleId = $scope.formFilter.scheduleId;
        }

        if(!isUndefined($scope.filterObject.scheduleId) ){
            if(!isUndefined($scope.filterObject.year) ){
                ReportPeriodsByScheduleAndYear.get({scheduleId: $scope.filterObject.scheduleId, year: $scope.filterObject.year}, function(data){
                    $scope.periods = data.periods;
                    $scope.periods.unshift({'name':formInputValue.periodOptionSelect});
                });
            }else{
                ReportPeriods.get({ scheduleId : $scope.filterObject.scheduleId },function(data) {
                    $scope.periods = data.periods;
                    $scope.periods.unshift({'name': formInputValue.periodOptionSelect});
                });
            }
        }
    };


    $scope.processPeriodFilter = function (){
        if (!isUndefined($scope.formFilter.periodId)) {
            $scope.filterObject.periodId = $scope.formFilter.periodId;
        }
    };

    $scope.changeScheduleByYear = function (){

        if (!isUndefined($scope.formFilter.year)) {
            $scope.filterObject.year = $scope.formFilter.year;
        }
        $scope.changeSchedule();
    };

    $scope.stockedOutPieChartOption = {
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
            container:$("#stockedOutReportLegend"),
            noColumns: 1,
            labelBoxBorderColor: "none"
        },
        grid:{
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

    $scope.loadStockedOutData = function(){
        if(!isUndefined($scope.filterObject.programId) &&
            !isUndefined($scope.filterObject.periodId) &&
            !isUndefined($scope.filterObject.productIdList) && $scope.filterObject.productIdList.length > 0){
            StockedOutFacilities.get({
                periodId: $scope.filterObject.periodId,
                programId: $scope.filterObject.programId,
                productId: $scope.filterObject.productIdList[0],
                zoneId: $scope.filterObject.zoneId
            },function(stockData){
                $scope.totalStockOuts = 0;
                if(!isUndefined(stockData.stockOut)){
                    $scope.datarows = stockData.stockOut;

                    $scope.districts = _.pairs(_.object(_.range(stockData.stockOut.length), _.pluck(stockData.stockOut,'geographicZoneId')));
                    $scope.stockedOutPieChartData = [];
                    for (var i = 0; i < stockData.stockOut.length; i++) {
                        $scope.totalStockOuts += stockData.stockOut[i].totalStockOut;
                        $scope.stockedOutPieChartData[i] = {
                            label: stockData.stockOut[i].location,
                            data: stockData.stockOut[i].totalStockOut
                        };
                    }

                    bindChartEvent("#stocked-out-reporting","plotclick",$scope.stockedOutChartClickHandler);
                    bindChartEvent("#stocked-out-reporting","plothover",flotChartHoverCursorHandler);

                }else{
                    $scope.resetStockedOutData();
                }

            });
        } else{
            $scope.resetStockedOutData();
        }
    };

    $scope.getFacilityStockOutPercent = function(value){
        return Math.round((value/$scope.totalStockOuts)*100) +'%';
    };

    $scope.resetStockedOutData = function(){
        $scope.stockedOutPieChartData = null;
        $scope.datarows = null;
    };

    $scope.stockedOutChartClickHandler = function (event, pos, item){
        if(item){
            var rgroupId = $scope.districts[item.seriesIndex][1];
            var districtStockOutPath = '/district-stock-out/'+$scope.filterObject.programId+'/'+$scope.filterObject.periodId+'/'+rgroupId+'/'+$scope.filterObject.productIdList[0];
            dashboardMenuService.addTab('menu.header.dashboard.stocked.out.district','/public/pages/dashboard/index.html#'+districtStockOutPath,'DISTRICT-STOCK-OUT',true, 4);

            $location.path(districtStockOutPath);
            $scope.formFilter.isNavigatedBack = true;
            $scope.setFilterData();
            $scope.$apply();
        }

    };

    $scope.setFilterData = function(){

        var data = {};
        $scope.filterObject = $scope.formFilter;
        angular.extend(data,$scope.filterObject);

        dashboardFiltersHistoryService.add($scope.$parent.currentTab,data);
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
    $scope.$on('$viewContentLoaded', function () {
        $timeout(function(){
            $scope.search();
        },1000);

    });

    var getFilterValues = function () {
        $scope.formFilter.programName = getSelectedItemName($scope.formFilter.programId, $scope.programs);
        $scope.formFilter.periodName = getSelectedItemName($scope.formFilter.periodId,$scope.periods);
        $scope.formFilter.zoneName = getSelectedZoneName($scope.formFilter.zoneId, $scope.zones, $scope.geographicZones);
    };
    $scope.search = function(){
        getFilterValues();
        if($scope.rootZone == $scope.formFilter.zoneId){
            return;
        }
        $scope.loadStockedOutData();
        //Alert Controller listens this event to update its own data
        $scope.$broadcast('dashboardFiltering', null);
    };

    $scope.$watch('formFilter.programId',function(){
        $scope.filterProductsByProgram();

    });
    $scope.$watch('formFilter.scheduleId', function(){
        $scope.changeSchedule();

    });
    $scope.$on('$routeChangeStart', function(){
       $scope.setFilterData();
    });


    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        total: 0,           // length of data
        count: 25           // count per page
    });


}
