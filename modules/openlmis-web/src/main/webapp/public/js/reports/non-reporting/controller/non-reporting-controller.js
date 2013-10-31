/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function NonReportingController($scope,ngTableParams, $filter, ReportPeriodsByScheduleAndYear,RequisitionGroupsByProgramSchedule ,OperationYears, RequisitionGroups, NonReportingFacilities, ReportSchedules, ReportFacilityTypes , ReportPeriods, ReportPrograms, $http, $routeParams,$location) {

    $scope.filterGrid = function (){
       $scope.getPagedDataAsync();
    };

    ReportPrograms.get(function(data){
        $scope.programs = data.programs;
        $scope.programs.unshift({'name':'-- Select a Program --'});
    });

    $scope.years = [];
    OperationYears.get(function (data) {
        $scope.years = data.years;
        $scope.years.unshift('-- All --');
    });

    ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
        $scope.schedules.unshift({'name':'-- Select a Schedule --'});
    });

    $scope.ChangeSchedule = function(){

        if($scope.schedule === undefined || $scope.schedule === ''){
            $scope.periods = [];
            $scope.requisitionGroups = [];
            $scope.periods.push({name:'<--'});
            $scope.requisitionGroups.push({name:'<--'});
            $scope.period = '';

            return;
        }

        $scope.LoadPeriods();

        RequisitionGroupsByProgramSchedule.get({program: $scope.program, schedule:$scope.schedule}, function(data){
            $scope.requisitionGroups = data.requisitionGroupList;
            $scope.requisitionGroups.unshift({'name':'-- All requsition groups --'});
        });
    };

    $scope.LoadPeriods = function (){
        if($scope.schedule === undefined || $scope.schedule === '') {
            return;
        }
        $scope.period = '';
        $scope.datarows = [];
        if($scope.year !== 0 ){
            ReportPeriodsByScheduleAndYear.get({scheduleId: $scope.schedule, year: $scope.year}, function(data){
                $scope.periods = data.periods;
                if(!isUndefined($scope.periods)){
                    $scope.periods.unshift({'name':'-- Select a Period --','id':''});
                }
            });
        }else{
            ReportPeriods.get({ scheduleId : $scope.schedule },function(data) {
                $scope.periods = data.periods;
                $scope.periods.unshift({'name': '-- Select Period --', id : ''});
            });
        }
    };

    ReportFacilityTypes.get(function(data) {
        $scope.facilityTypes = data.facilityTypes;
        $scope.facilityTypes.unshift({'name': '-- All Facility Types --'});
    });

    $scope.ChangeSchedule();

    $scope.exportReport   = function (type){

        var param = $scope.getParams(1, 1);
        var paramString = jQuery.param(param);
        var url = '/reports/download/non_reporting/' + type + '?' + paramString;
        window.open(url);
    };


    $scope.getParams = function(){
        var params =  {
            "max" : 50000,
            "page" : 1
        };

        params.period   = $scope.period;
        params.rgroup   = $scope.rgroup;
        params.ftype    = $scope.facilityType;
        params.program  = $scope.program;
        params.schedule = $scope.schedule;
        return params;
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

    $scope.getPagedDataAsync = function () {
        // clear the data that is showing up now.
        $scope.data = $scope.datarows = [];
        // if period or schedule group is not selected, there is no reason to ask the server
        if($scope.period === '' || $scope.program === '' || $scope.schedule === ''){
            return;
        }
        var params = $scope.getParams();

        NonReportingFacilities.get(params, function(data) {
            if(data.pages !== undefined){
                $scope.summaries    =  data.pages.rows[0].summary;
                $scope.data = data.pages.rows[0].details;
                $scope.paramsChanged( $scope.tableParams );
            }
        });
    };

}
