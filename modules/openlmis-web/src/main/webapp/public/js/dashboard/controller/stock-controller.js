
/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function StockController($scope, $routeParams,dashboardFiltersHistoryService, programsList,formInputValue,UserSupervisoryNodes,userPreferredFilterValues,RequisitionGroupsBySupervisoryNodeProgramSchedule,ReportProgramsBySupervisoryNode, ReportSchedules, ReportPeriods, RequisitionGroupsByProgram,RequisitionGroupsByProgramSchedule, ReportProductsByProgram, OperationYears, ReportPeriodsByScheduleAndYear, StockEfficiencyDetail, ngTableParams) {

    $scope.filterObject = {};

    $scope.formFilter = {};

    initialize();

    function initialize() {
        $scope.stockStatusMapping = [{name: "Stocked out",key:"S", value:"0"},
            {name: "Understocked",key:"U", value:"1"},
            {name: "Overstocked",key:"O", value:"2"},
            {name: "Adequately Stocked",key:"A", value:"3"}];

        $scope.$parent.currentTab = 'STOCK';
        $scope.showProductsFilter = true;
        $scope.productSelectOption = {maximumSelectionSize : 4};
        $scope.showStockStatusFilter = true;
        $scope.stockStatusMapping.unshift({'name':'-- All --','value':-1});
    }
    UserSupervisoryNodes.get(function (data){
        $scope.supervisoryNodes = data.supervisoryNodes;
        if(!isUndefined( $scope.supervisoryNodes)){
            $scope.supervisoryNodes.unshift({'name': formInputValue.supervisoryNodeOptionAll});
        }

    });

    OperationYears.get(function (data) {
        $scope.startYears = data.years;
        $scope.startYears.unshift(formInputValue.yearOptionAll);
    });

    ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
        $scope.schedules.unshift({'name': formInputValue.scheduleOptionSelect}) ;

    });

    $scope.filterProductsByProgram = function (){

        if(isUndefined($scope.formFilter.programId)){
            $scope.products = null;
            $scope.requisitionGroups  = null;
            $scope.resetStockingData();
            return;
        }
        $scope.filterObject.programId = $scope.formFilter.programId;

        ReportProductsByProgram.get({programId:  $scope.filterObject.programId}, function(data){
            $scope.products = data.productList;
        });

        if(!isUndefined($scope.formFilter.supervisoryNodeId)){
            RequisitionGroupsBySupervisoryNodeProgramSchedule.get(
                {programId : $scope.filterObject.programId,
                    scheduleId : isUndefined($scope.filterObject.scheduleId) ? 0 : $scope.filterObject.scheduleId ,
                    supervisoryNodeId : $scope.filterObject.supervisoryNodeId
                },function(data){
                    $scope.requisitionGroups = data.requisitionGroupList;
                    $scope.requisitionGroups.unshift({'name':formInputValue.requisitionOptionAll});
                });
        }else{
            RequisitionGroupsByProgram.get({program: $scope.filterObject.programId }, function(data){
                $scope.requisitionGroups = data.requisitionGroupList;
                $scope.requisitionGroups.unshift({'name':formInputValue.requisitionOptionAll});
            });
        }

    };

    $scope.processProductsFilter = function (){
        $scope.filterObject.productIdList = $scope.formFilter.productIdList;

        $scope.loadStockingData();

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
            if(!isUndefined($scope.filterObject.programId)){
                if(!isUndefined($scope.filterObject.supervisoryNodeId)){
                    RequisitionGroupsBySupervisoryNodeProgramSchedule.get(
                        {programId: $scope.filterObject.programId,
                            scheduleId: $scope.filterObject.scheduleId,
                            supervisoryNodeId: $scope.filterObject.supervisoryNodeId}, function(data){
                            $scope.requisitionGroups = data.requisitionGroupList;
                            $scope.requisitionGroups.unshift({'name':formInputValue.requisitionOptionAll});

                        });
                }else{
                    RequisitionGroupsByProgramSchedule.get({program: $scope.filterObject.programId, schedule:$scope.filterObject.scheduleId}, function(data){
                        $scope.requisitionGroups = data.requisitionGroupList;
                        $scope.requisitionGroups.unshift({'name':formInputValue.requisitionOptionAll});
                    });
                }

            }
        }

        $scope.loadStockingData();
    };

    $scope.changeScheduleByYear = function (){

        if (!isUndefined($scope.formFilter.year)) {
            $scope.filterObject.year = $scope.formFilter.year;

        }
        $scope.changeSchedule();

    };

    $scope.processPeriodFilter = function (){
        if (!isUndefined($scope.formFilter.periodId)) {
            $scope.filterObject.periodId = $scope.formFilter.periodId;
        }

        $scope.loadStockingData();
    };

    $scope.processStockStatusFilter = function(){
        if(!isUndefined($scope.formFilter.status)) {
            $scope.formFilter.status = $scope.formFilter.status;
        }else{
            $scope.formFilter.status = -1;
        }
        $scope.loadStockingData();
    };
    // the grid options
    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        total: 0,           // length of data
        count: 25           // count per page
    });

    $scope.loadStockingData = function(){
        if(!isUndefined($scope.filterObject.productIdList)){
            StockEfficiencyDetail.get({
                periodId: $scope.filterObject.periodId,
                programId: $scope.filterObject.programId,
                rgroupId: $scope.filterObject.rgroupId,
                productListId: $scope.filterObject.productIdList
            },function (data){
                $scope.stockingList = data.stocking;

                if($scope.formFilter.status && $scope.formFilter.status !== -1){

                    var statusMap = _.findWhere($scope.stockStatusMapping,{value:$scope.formFilter.status});
                    if(statusMap){
                        $scope.stockingList = _.where( $scope.stockingList, {stocking:statusMap.key});
                    }
                }
                $scope.stockByProductAndStock = groupStockingByProductAndStock($scope.stockingList);

            });
        }else{
            $scope.stockByProductAndStock = undefined;
        }

    };

    $scope.resetStockingData = function(){
        $scope.stockingList = $scope.stockByProductAndStock = null;
    };

    var groupStockingByProductAndStock = function (data) {
        if(isUndefined(data) || _.isNull(data)){
            return data;
        }
        var groupedByProductAndStocking = [];
        var groupedByProduct = _.chain(data).groupBy('productId').map(function(value, key) { return {productId: key, product: _.first(value).product, stocks: value };}).value();

        angular.forEach(groupedByProduct, function(productGroup){
            var groupedByStocking = _.chain(productGroup.stocks).
                                      groupBy('stocking').
                                      map(function(value, key) {
                                        var statusMap = _.findWhere($scope.stockStatusMapping,{key:key});
                                        var stockStatusDesc = !isUndefined(statusMap) ? statusMap.name : key;

                                        return {stocking: key, name: stockStatusDesc, facilities: value };

                                      }).value();

            groupedByProductAndStocking.push({productId: productGroup.productId, product: productGroup.product, stocks:groupedByStocking });
        });
        return groupedByProductAndStocking;
    };

    $scope.processSupervisoryNodeChange = function(){

        $scope.filterObject.supervisoryNodeId = $scope.formFilter.supervisoryNodeId;

        if(isUndefined($scope.filterObject.supervisoryNodeId)){
            $scope.programs = _.filter(programsList, function(program){ return program.name !== formInputValue.programOptionSelect;});
            $scope.programs.unshift({'name': formInputValue.programOptionSelect});

        }else if(!isUndefined($scope.filterObject.supervisoryNodeId)){
            ReportProgramsBySupervisoryNode.get({supervisoryNodeId : $scope.filterObject.supervisoryNodeId} ,function(data){
                    $scope.programs = data.programs;
                    $scope.programs.unshift({'name': formInputValue.programOptionSelect});
                });
        }

    };

    $scope.processRequisitionFilter = function(){
        if($scope.formFilter.rgroupId && $scope.formFilter.rgroupId.length > 1) {
            $scope.formFilter.rgroupId = _.reject($scope.formFilter.rgroupId, function(rgroup){return rgroup === ""; });
        }

        $scope.filterObject.rgroupId = $scope.formFilter.rgroupId;
        $scope.loadStockingData();
    };

    $scope.$on('$viewContentLoaded', function () {
        var filterHistory = dashboardFiltersHistoryService.get($scope.$parent.currentTab);

        if(_.isEmpty($routeParams) && isUndefined(filterHistory)){

            if(!_.isEmpty(userPreferredFilterValues)){
                var date = new Date();

                $scope.filterObject.supervisoryNodeId = $scope.formFilter.supervisoryNodeId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_SUPERVISORY_NODE];
                $scope.processSupervisoryNodeChange();

                $scope.filterObject.programId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_PROGRAM];
                $scope.filterObject.periodId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_PERIOD];
                $scope.filterObject.scheduleId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_SCHEDULE];
                $scope.filterObject.year = date.getFullYear() - 1;
                $scope.filterObject.rgroupId = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_REQUISITION_GROUP];
                $scope.filterObject.productIdList = userPreferredFilterValues[localStorageKeys.PREFERENCE.DEFAULT_PRODUCTS].split(',');

                $scope.registerWatches();

                $scope.formFilter = $scope.filterObject;
            }
            return;

        }else if(!_.isEmpty($routeParams)){

            $scope.formFilter.supervisoryNodeId = $routeParams.supervisoryNodeId;
            $scope.processSupervisoryNodeChange();
            $scope.registerWatches();
            $scope.formFilter = $scope.filterObject = $routeParams;
            $scope.formFilter.productIdList = $scope.filterObject.productIdList = [$routeParams.productId];

            return;
        }else{
            $scope.formFilter.supervisoryNodeId = filterHistory.supervisoryNodeId;
            $scope.processSupervisoryNodeChange();
            $scope.registerWatches();

            $scope.formFilter = $scope.filterObject = filterHistory;

        }


    });

    $scope.registerWatches = function(){

        $scope.$watch('formFilter.programId',function(){
            $scope.filterProductsByProgram();

        });
        $scope.$watch('formFilter.scheduleId', function(){
            $scope.changeSchedule();

        });

    };

    $scope.$on('$routeChangeStart', function(){
        var data = {};
        angular.extend(data,$scope.filterObject);
        dashboardFiltersHistoryService.add($scope.$parent.currentTab, data);
    });


    $scope.paramsChanged = function(params) {


    };

    // watch for changes of parameters
    $scope.$watch('tableParams', $scope.paramsChanged , true);

}
