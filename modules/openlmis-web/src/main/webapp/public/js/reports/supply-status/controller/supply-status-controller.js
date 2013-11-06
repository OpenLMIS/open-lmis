/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function SupplyStatusController($scope, $window, $filter, ngTableParams , SupplyStatusReport, ReportSchedules, ReportPrograms , ReportPeriods , ReportProductsByProgram ,ReportFacilityTypes, FacilitiesByProgramParams,GetFacilityByFacilityType, GeographicZones, RequisitionGroups,SettingsByKey, $http, $routeParams,$location) {
    //to minimize and maximize the filter section

    $scope.showMessage = true;
    $scope.message = "Indicates a required field." ;


    $scope.IndicatorProductsKey = "INDICATOR_PRODUCTS";

    SettingsByKey.get({key: $scope.IndicatorProductsKey},function (data){
        $scope.IndicatorProductsDescription = data.settings.value;
    });

    $scope.filterGrid = function (){
        $scope.getPagedDataAsync(0, 0);
    };


    ReportPrograms.get(function(data){
        $scope.programs = data.programs;
        $scope.programs.unshift({'name':'-- Select a Program --'});
    });

    RequisitionGroups.get(function(data){
        $scope.requisitionGroups = data.requisitionGroupList;
        $scope.requisitionGroups.unshift({'name':'-- All Reporting Groups --','id':''});
    });

    ReportFacilityTypes.get(function(data) {
        $scope.facilityTypes = data.facilityTypes;
        $scope.facilityTypes.unshift({'name': '-- All Facility Types --', 'id' : '0'});
    });


    ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
        $scope.schedules.unshift({'name':'-- Select a Schedule --'});

        $scope.allFacilities = [];
        $scope.allFacilities.push({code:'-- Select a Facility --',id:''});
    });

    GeographicZones.get(function(data) {
        $scope.zones = data.zones;
        $scope.zones.unshift({'name': '-- All Zones --', 'id' : ''});
    });

    $scope.ProgramChanged = function(){
        ReportProductsByProgram.get({programId: $scope.filterObject.program}, function(data){
            $scope.products = data.productList;
            $scope.products.unshift({id: '',name: '-- Select Product --'});
        });
    };

    $scope.ChangeSchedule = function(){
        ReportPeriods.get({ scheduleId : $scope.filterObject.schedule },function(data) {
            $scope.periods = data.periods;
            $scope.periods.unshift({'name': '-- Select Period --'});
        });
        // load products

        $scope.loadFacilities();
    } ;

    $scope.loadFacilities = function(){
        if(isUndefined($scope.filterObject.program) || isUndefined($scope.filterObject.schedule)){
            return;
        }

        // load facilities
        FacilitiesByProgramParams.get({
                program: $scope.filterObject.program ,
                schedule: $scope.filterObject.schedule,
                type: $scope.filterObject.facilityType
            }, function(data){
                $scope.allFacilities = data.facilities;
                $scope.allFacilities.unshift({code:'-- Select a Facility --',id:''});
            }
        );
    };


    $scope.exportReport   = function (type){
        $scope.filterObject.pdformat = 1;
        var params = jQuery.param($scope.filterObject);
        var url = '/reports/download/supply_status/' + type +'?' + params;
        $window.open(url);
    };

    // the grid options
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


    $scope.getPagedDataAsync = function (pageSize, page) {


        $scope.datarows = $scope.data = [];

        $scope.filterObject.max = 10000;
        $scope.filterObject.page = 1;

        SupplyStatusReport.get($scope.filterObject , function(data) {
            $scope.data = data.pages.rows;
            $scope.paramsChanged($scope.tableParams);
        });

    };


    $scope.formatNumber = function(value){
        return utils.formatNumber(value,'0,0.00');
    };





}
