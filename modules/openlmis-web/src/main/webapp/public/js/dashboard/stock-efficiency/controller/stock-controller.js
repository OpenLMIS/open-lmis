
/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function StockController($scope, $location,$routeParams, userGeographicZoneList,ReportPrograms, ReportSchedules, ReportPeriods, RequisitionGroupsByProgram,RequisitionGroupsByProgramSchedule, ReportProductsByProgram, OperationYears, ReportPeriodsByScheduleAndYear, StockEfficiencyDetail, ngTableParams) {

    $scope.filterObject = {};

    $scope.formFilter = {};

    $scope.startYears = [];

    initialize();

    function initialize() {
        if(isUndefined($scope.filterObject.geographicZoneId)){
            $scope.filterObject.geographicZoneId = 0;
            if(!isUndefined(userGeographicZoneList) ){
                $scope.filterObject.geographicZoneId = userGeographicZoneList[0] !== null ? userGeographicZoneList[0].id : undefined;
            }
        }

        $scope.stockStatusMapping = $scope.$parent.stockStatusMapping;
        $scope.stockStatusMapping.unshift({'name':'-- All --','value':-1});
    }

    $scope.productSelectOption = {maximumSelectionSize : 4};

    OperationYears.get(function (data) {
        $scope.startYears = data.years;
        $scope.startYears.unshift('-- All Years --');
    });

    ReportPrograms.get(function (data) {
        $scope.programs = data.programs;
        $scope.programs.unshift({'name': '-- Select Programs --'});
    });

    ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
        $scope.schedules.unshift({'name':'-- Select a Schedule --', 'id':'0'}) ;

    });

    $scope.filterProductsByProgram = function (){
        if(isUndefined($scope.formFilter.programId)){
            return;
        }
        $scope.filterObject.programId = $scope.formFilter.programId;

        ReportProductsByProgram.get({programId:  $scope.filterObject.programId}, function(data){
            $scope.products = data.productList;
        });

        RequisitionGroupsByProgram.get({program: $scope.filterObject.programId }, function(data){
            $scope.requisitionGroups = data.requisitionGroupList;
            $scope.requisitionGroups.unshift({'name':'-- All Requisition Groups --'});
        });
    };

    $scope.processProductsFilter = function (){
        $scope.filterObject.productIdList = $scope.formFilter.productIdList;

        $scope.loadStockingData();

    };

    $scope.changeSchedule = function(){

        if ($scope.formFilter.scheduleId == "All") {
            $scope.filterObject.scheduleId = -1;
        } else if ($scope.formFilter.scheduleId !== undefined || $scope.formFilter.scheduleId === "") {
            $scope.filterObject.scheduleId = $scope.formFilter.scheduleId;

        } else {
            $scope.filterObject.scheduleId = 0;
        }

        if(!isUndefined($scope.filterObject.scheduleId)){
            ReportPeriods.get({ scheduleId : $scope.filterObject.scheduleId },function(data) {
                $scope.periods = data.periods;
                $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});

            });

            if(!isUndefined($scope.filterObject.programId)){
                RequisitionGroupsByProgramSchedule.get({program: $scope.filterObject.programId, schedule:$scope.filterObject.scheduleId}, function(data){
                    $scope.requisitionGroups = data.requisitionGroupList;
                    $scope.requisitionGroups.unshift({'name':'-- All Requisition Groups --','id':'0'});
                });
            }

            $scope.loadStockingData();
        }


    };

    $scope.processPeriodFilter = function (){
        if ( $scope.formFilter.periodId == "All") {
            $scope.filterObject.periodId = -1;
        } else if ($scope.formFilter.periodId !== undefined || $scope.formFilter.periodId === "") {
            $scope.filterObject.periodId = $scope.formFilter.periodId;

        } else {
            $scope.filterObject.periodId = 0;
        }

        $scope.loadStockingData();
    };

    $scope.changeScheduleByYear = function (){

        if ($scope.formFilter.year == "-- All Years --") {
            $scope.filterObject.year = -1;
        } else if ($scope.formFilter.year !== undefined || $scope.formFilter.year === "") {
            $scope.filterObject.year = $scope.formFilter.year;

        } else {
            $scope.filterObject.year = 0;
        }

        if($scope.filterObject.year === -1 || $scope.filterObject.year === 0){
            $scope.changeSchedule();

        }else{
            if(!isUndefined($scope.filterObject.scheduleId) && !isUndefined($scope.filterObject.year)){
                ReportPeriodsByScheduleAndYear.get({scheduleId: $scope.filterObject.scheduleId, year: $scope.filterObject.year}, function(data){
                    $scope.periods = data.periods;
                    $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});
                });
            }
            if(!isUndefined($scope.filterObject.scheduleId) && !isUndefined($scope.filterObject.programId)){
                RequisitionGroupsByProgramSchedule.get({program: $scope.filterObject.programId, schedule:$scope.filterObject.scheduleId}, function(data){
                    $scope.requisitionGroups = data.requisitionGroupList;
                    $scope.requisitionGroups.unshift({'name':'-- All Requisition Groups --','id':'0'});
                });
            }
            $scope.loadStockingData();
        }

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
                geographicZoneId: $scope.filterObject.geographicZoneId,
                periodId: $scope.filterObject.periodId,
                programId: $scope.filterObject.programId,
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

                //alert('groupedByProduct and stock '+JSON.stringify($scope.stockByProductAndStock));

            });
        }else{
            $scope.stockByProductAndStock = undefined;
        }
    };

    var groupStockingByProductAndStock = function (data) {
        if(isUndefined(data)){
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


    /* process if page is routed from another controller*/
    $(function(){
        if(!isUndefined($routeParams.programId)){
            $scope.filterObject.programId = $scope.formFilter.programId = $routeParams.programId;
            $scope.filterProductsByProgram();
        }
        if(!isUndefined($routeParams.scheduleId)){
            $scope.filterObject.scheduleId = $scope.formFilter.scheduleId = $routeParams.scheduleId;
            $scope.changeSchedule();
        }
        if(!isUndefined($routeParams.year)){
            $scope.filterObject.year =  $scope.formFilter.year = $routeParams.year;
            $scope.changeScheduleByYear();
        }
        if(!isUndefined($routeParams.periodId)){
            $scope.filterObject.periodId =  $scope.formFilter.periodId = $routeParams.periodId;
            $scope.changeScheduleByYear();
        }
        if(!isUndefined($routeParams.productId)){
            $scope.filterObject.productIdList =  $scope.formFilter.productIdList = [$routeParams.productId];
            $scope.processProductsFilter();
        }
        if(!isUndefined($routeParams.status)){
            $scope.filterObject.status =  $scope.formFilter.status = $routeParams.status;
            $scope.processStockStatusFilter();
        }

    });

}
