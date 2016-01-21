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

function ShipmentLeadTimeController($scope,$filter,userPreferredFilters,$timeout, dashboardFiltersHistoryService,programsList,FlatGeographicZoneList,UserGeographicZoneTree, formInputValue, ReportSchedules, ReportPeriods, ReportProductsByProgram, OperationYears, ReportPeriodsByScheduleAndYear,ShipmentLeadTime, ngTableParams) {


    initialize();

    function initialize() {

        $scope.showProductsFilter = false;
        $scope.$parent.currentTab = 'ORDER';

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

    $scope.getShipmentLeadTimeData = function () {
        if(isUndefined($scope.filterObject.periodId) || isUndefined($scope.filterObject.programId)){
            return;
        }

        ShipmentLeadTime.get($scope.filterObject, function (data) {
            $scope.data = data.leadTime;
            $scope.paramsChanged($scope.tableParams);
        });

    };

    $scope.resetShipmentLeadTimeData = function(){
        $scope.data = undefined;
    };


    $scope.setFilterData = function(){

        var data = {};
        $scope.filterObject = $scope.formFilter;
        angular.extend(data,$scope.filterObject);

        dashboardFiltersHistoryService.add($scope.$parent.currentTab,data);
    };
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
        $scope.getShipmentLeadTimeData();
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

    $scope.paramsChanged = function(params) {

        // slice array data on pages
        if($scope.data === undefined ){
            $scope.datarows = [];
            params.total = 0;
        }else{
            var data = $scope.data;
            var orderedData = params.filter ? $filter('filter')(data, params.filter) : data;
            orderedData = params.sorting ?  $filter('orderBy')(orderedData, params.orderBy()) : data;

            params.total = orderedData.length;
            $scope.datarows = orderedData.slice( (params.page - 1) * params.count,  params.page * params.count );
            var i = 0;
            var baseIndex = params.count * (params.page - 1) + 1;
            while(i < $scope.datarows.length){
                $scope.datarows[i].no = baseIndex + i;
                i++;
            }
        }
    };

    // watch for changes of parameters
    $scope.$watch('tableParams', $scope.paramsChanged , true);


}
