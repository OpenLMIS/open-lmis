
/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function StockController($scope,userFacilityData,ReportPrograms, ReportSchedules, ReportPeriods, RequisitionGroupsByProgram,RequisitionGroupsByProgramSchedule, ReportProductsByProgram, OperationYears, ReportPeriodsByScheduleAndYear, StockEfficiency, ngTableParams) {

    $scope.filterObject = {};

    $scope.formFilter = {};

    $scope.startYears = [];

    initialize();

    function initialize() {
        if(isUndefined($scope.filterObject.geographicZoneId)){
            $scope.filterObject.geographicZoneId = 0;
            var userFacility = userFacilityData.facilityList[0];
            if (userFacility) {
                $scope.filterObject.geographicZoneId = userFacility.geographicZone.id;
            }
        }
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


    $scope.$watch('formFilter.programId', function(selection){

        if(selection !== undefined || selection === ""){
            if (selection === '') {
                $scope.filterObject.programId = 0;
                return;
            }
            $scope.filterObject.programId = selection;
            $.each($scope.programs, function (item, idx) {
                if (idx.id == selection) {
                    $scope.filterObject.program = idx.name;
                }
            });

            ReportProductsByProgram.get({programId: selection}, function(data){
                $scope.products = data.productList;
            });

            RequisitionGroupsByProgram.get({program: selection }, function(data){
                $scope.requisitionGroups = data.requisitionGroupList;
                $scope.requisitionGroups.unshift({'name':'-- All Requisition Groups --'});
            });
        }
    });



    $scope.ChangeSchedule = function(scheduleBy){
        if(scheduleBy == 'byYear'){

            ReportPeriodsByScheduleAndYear.get({scheduleId: $scope.filterObject.scheduleId, year: $scope.filterObject.year}, function(data){
                $scope.periods = data.periods;
                $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});
            });

        }else{

            ReportPeriods.get({ scheduleId : $scope.filterObject.scheduleId },function(data) {
                $scope.periods = data.periods;
                $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});

            });
        }

        RequisitionGroupsByProgramSchedule.get({program: $scope.filterObject.programId, schedule:$scope.filterObject.scheduleId}, function(data){
            $scope.requisitionGroups = data.requisitionGroupList;
            $scope.requisitionGroups.unshift({'name':'-- All Requisition Groups --','id':'0'});
        });

    };

    $scope.$watch('formFilter.rgroupId', function (selection) {
        if (selection == "All") {
            $scope.filterObject.rgroupId = -1;
        } else if (selection !== undefined || selection === "") {
            $scope.filterObject.rgroupId = selection;
            $.each($scope.requisitionGroups, function (item, idx) {
                if (idx.id == selection) {
                    $scope.filterObject.rgroup = idx.name;
                }
            });
        } else {
            $scope.filterObject.rgroupId = 0;
        }

    });

    $scope.$watch('formFilter.periodId', function (selection) {
        if (selection == "All") {
            $scope.filterObject.periodId = -1;
        } else if (selection !== undefined || selection === "") {
            $scope.filterObject.periodId = selection;
            $.each($scope.periods, function (item, idx) {
                if (idx.id == selection) {
                    $scope.filterObject.period = idx.name;
                }
            });

        } else {
            $scope.filterObject.periodId = 0;
        }
        $scope.loadStockingData();

    });


    $scope.$watch('formFilter.scheduleId', function (selection) {
        if (selection == "All") {
            $scope.filterObject.scheduleId = -1;
        } else if (selection !== undefined || selection === "") {
            $scope.filterObject.scheduleId = selection;
            $.each($scope.schedules , function (item, idx) {
                if (idx.id == selection) {
                    $scope.filterObject.schedule = idx.name;
                }
            });

        } else {
            $scope.filterObject.scheduleId = 0;
        }
        $scope.ChangeSchedule('');

    });

    $scope.$watch('formFilter.year', function (selection) {

        if (selection == "-- All Years --") {
            $scope.filterObject.year = -1;
        } else if (selection !== undefined || selection === "") {
            $scope.filterObject.year = selection;

        } else {
            $scope.filterObject.year = 0;
        }

        if($scope.filterObject.year === -1 || $scope.filterObject.year === 0){

            $scope.ChangeSchedule('bySchedule');
        }else{

            $scope.ChangeSchedule('byYear');
        }
    });

    $scope.$watch('formFilter.productIdList',function(selection){

        $scope.filterObject.productIdList = $scope.formFilter.productIdList;
        $scope.loadStockingData();
    });

    // the grid options
    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        total: 0,           // length of data
        count: 25           // count per page
    });

    $scope.loadStockingData = function(){

        if(!isUndefined($scope.filterObject.productIdList)){
            StockEfficiency.get({
                geographicZoneId: $scope.filterObject.geographicZoneId,
                periodId: $scope.filterObject.periodId,
                programId: $scope.filterObject.programId,
                productListId: $scope.filterObject.productIdList
            },function (data){
                $scope.stockingList = data.stocking;
                $scope.stockByProducts = groupStockingByProduct($scope.stockingList);
            });
        }
    };


    var groupStockingByProduct = function (data) {
        if(isUndefined(data)){
            return data;
        }
        var groupedStockingInfo = [];
        angular.forEach(data,function(stock){
            if(groupedStockingInfo.indexOf(stock.product) == -1){
                groupedStockingInfo.push(stock.product);
            }
        });
        return groupedStockingInfo;
    };





}
