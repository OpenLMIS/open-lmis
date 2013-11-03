/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function RnRFeedbackController($scope, ngTableParams, $filter, RnRFeedbackReport, Products ,ReportFacilityTypes,OperationYears,ReportPeriods,ReportPeriodsByScheduleAndYear,AllReportPeriods,ReportFilteredPeriods, $http,ReportSchedules, ReportPrograms,RequisitionGroups,AllFacilites,GetFacilityByFacilityType,SettingsByKey, $routeParams,$location) {
    //to minimize and maximize the filter section
    var section = 1;

    $scope.section = function (id) {
        section = id;
    };

    $scope.showMessage = true;
    $scope.message = "Indicates a required field." ;

    $scope.IndicatorProductsKey = "INDICATOR_PRODUCTS";

    SettingsByKey.get({key: $scope.IndicatorProductsKey},function (data){
        $scope.IndicatorProductsDescription = data.settings.value;
    });

    $scope.show = function (id) {
        return section == id;
    };
    // lookups and references

    $scope.pagingOptions = {
        pageSizes: [ 20, 40, 50, 100],
        pageSize: 20,
        totalServerItems: 0,
        currentPage: 1
    };

   /* $scope.reporting = "quarterly";
*/
    $scope.orderTypes = [
        {'name':'Regular', 'value':'Regular'},
        {'name':'Emergency', 'value':'Emergency'}
    ];

    //Order type defaults to Regular
    $scope.orderType = 'Regular' ;


    $scope.filterGrid = function (){
        if (checkMinimumFilled()) {
         $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
        }
    };

    //filter form data section
    $scope.filterOptions = {
        period:$scope.period,
        filterText: "",
        useExternalFilter: false
    };



    //filter form data section
    $scope.filterObject =  {
        facilityTypeId : $scope.facilityType,
        facilityType : "",
        programId : $scope.program,
        program : "",
        periodId : $scope.period,
        productId : $scope.productId,
        product : "",
        scheduleId : $scope.schedule,
        facilityName : $scope.facilityNameFilter,
        facilityId: $scope.facility,
        rgroupId : $scope.rgroup,
        rgroup : "",
        orderType: ""
    };

    $scope.startYears = [];
    OperationYears.get(function (data) {
        $scope.startYears = data.years;
        $scope.startYears.unshift('-- All Years --');
    });

    ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
        $scope.schedules.unshift({'name':'-- Select a Schedule --', 'id':'0'}) ;
    });

    ReportPrograms.get(function(data){
        $scope.programs = data.programs;
    });

    RequisitionGroups.get(function(data){
        $scope.requisitionGroups = data.requisitionGroupList;
        $scope.requisitionGroups.unshift({'name':'All Reporting Groups'});
    });

    ReportFacilityTypes.get(function(data) {
        $scope.facilityTypes = data.facilityTypes;
        //  $scope.facilityTypes.push({'name': 'All Facility Types', 'id' : 'All'});
    });

    AllFacilites.get(function(data){
        $scope.allFacilities = data.allFacilities;
        $scope.products.unshift({'name': '-- All Products --', 'id':'All'});
    });


    Products.get(function(data){
        $scope.products = data.productList;
        var ind_prod = $scope.IndicatorProductsDescription;
        $scope.products.unshift({'name': '-- '.concat(ind_prod).concat(' --'), 'id':'-1'});
        $scope.products.unshift({'name': '-- All Products --', 'id':'All'});
    });

    AllReportPeriods.get(function (data) {
        $scope.periods = data.periods;
    });

    $scope.ChangeReportingPeriods = function(){
        var params  = {};

        $.each($scope.filterObject, function(index, value) {

            params[index] = value;
        });

        ReportFilteredPeriods.get(params, function(data) {
            $scope.periods = data.periods;
        });
    };

    $scope.$watch('facilityType', function(selection){
        if(selection == "All"){
            $scope.filterObject.facilityTypeId =  -1;
        }else if(selection !== undefined || selection === ""){
            $scope.filterObject.facilityTypeId =  selection;
            $.each( $scope.facilityTypes,function( item,idx){
                if(idx.id == selection){
                    $scope.filterObject.facilityType = idx.name;
                }
            });
        }else{
            $scope.filterObject.facilityTypeId =  0;
        }
          $scope.ChangeFacility();

        $scope.filterGrid();
    });

    $scope.ChangeFacility = function(){
        GetFacilityByFacilityType.get({ facilityTypeId : $scope.filterObject.facilityTypeId },function(data) {
            $scope.allFacilities =  data.facilities;
        });
    };

    $scope.$watch('facility', function(selection){
        if(selection !== undefined || selection === ""){
            $scope.filterObject.facilityId =  selection;
            $.each( $scope.allFacilities,function( item,idx){
                if(idx.id == selection){
                    $scope.filterObject.facilityName = idx.name;
                }
            });
        }else{
            $scope.filterObject.facilityId =  0;
        }
        $scope.filterGrid();
    });


    $scope.$watch('orderType', function(selection){
        if(selection !== undefined || selection === ""){
            $scope.filterObject.orderType =  selection;

        }else{
            $scope.filterObject.orderType = "";
        }
        $scope.filterGrid();
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

    };

    $scope.$watch('schedule', function (selection) {
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

    $scope.$watch('product', function(selection){
        if(selection == "All"){
            $scope.filterObject.productId =  0;

        }else if(selection == "-1"){
                $scope.filterObject.productId =  -1;
        }else if(selection !== undefined || selection === ""){
            $scope.filterObject.productId =  selection;
            $.each($scope.products, function(item, idx){
               if(idx.id == selection){
                   $scope.filterObject.product = idx.name;
               }
            });
        }else{
            $scope.filterObject.productId =  0;
        }
        $scope.filterGrid();
    });


    $scope.$watch('program', function(selection){
        if(selection == "All"){
            $scope.filterObject.programId =  -1;
        }else if(selection !== undefined || selection === ""){
            $scope.filterObject.programId =  selection;
            $.each($scope.programs, function(item, idx){
               if(idx.id == selection){
                   $scope.filterObject.program = idx.name;
               }
            });
        }else{
            $scope.filterObject.programId =  0;
        }
        $scope.filterGrid();
    });

    $scope.$watch('rgroup', function(selection){
        if(selection === ""){
            $scope.filterObject.rgroupId =  0;
            $scope.filterObject.rgroup =  "";
        }else if(selection !== undefined || selection === ""){
            $scope.filterObject.rgroupId =  selection;
            $.each( $scope.requisitionGroups,function( item,idx){
                if(idx.id == selection){
                    $scope.filterObject.rgroup = idx.name;
                }
            });
        }else{
            $scope.filterObject.rgroupId =  0;
            $scope.filterObject.rgroup =  "";
        }
        $scope.filterGrid();
    });

    $scope.$watch('period', function(selection){
        if(selection == "All"){
            $scope.filterObject.periodId =  -1;
        }else if(selection !== undefined || selection === ""){
            $scope.filterObject.periodId =  selection;
            $.each( $scope.periods,function( item,idx){
                if(idx.id == selection){
                    $scope.filterObject.period = idx.name;
                }
            });

        }else{
            $scope.filterObject.periodId =  0;
        }
        $scope.filterGrid();
    });

    $scope.$watch('year', function (selection) {

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

    $scope.exportReport   = function (type){
        $scope.filterObject.pdformat =1;
        var params = jQuery.param($scope.filterObject);
        var url = '/reports/download/rnr_feedback/' + type +'?' + params;
        window.open(url);

    };

    $scope.goToPage = function (page, event) {
        angular.element(event.target).parents(".dropdown").click();
        $location.search('page', page);
    };

    $scope.$watch("currentPage", function () {  //good watch no problem

        if($scope.currentPage !== undefined && $scope.currentPage !== 1){
            //when clicked using the links they have done updated the paging info no problem here
            //or using the url page param
            //$scope.pagingOptions.currentPage = $scope.currentPage;
            $location.search("page", $scope.currentPage);
        }
    });

    $scope.$on('$routeUpdate', function () {
        if (!utils.isValidPage($routeParams.page, $scope.numberOfPages)) {
            $location.search('page', 1);
            return;
        }
    });

    $scope.setPagingData = function(data, page, pageSize, total){
        $scope.myData = data;
        $scope.pagingOptions.totalServerItems = total;
        $scope.numberOfPages = ( Math.ceil( total / pageSize))  ? Math.ceil( total / pageSize) : 1 ;

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
            //alert(JSON.stringify($scope.data, null, 4));
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

        var params =  {
            "max" : 10000,
            "page" : 1
        };

        $.each($scope.filterObject, function(index, value) {
            params[index] = value;
        });

        RnRFeedbackReport.get(params, function(data) {
            $scope.data         = data.pages.rows ;
            $scope.paramsChanged( $scope.tableParams );
        });

    };

    $scope.formatNumber = function(value){
        return utils.formatNumber(value,'0,000');
    };

    function checkMinimumFilled()
    {
        // check valid value of each field minimum selection to run the application
        if ($scope.program > 0 && $scope.schedule > 0 && $scope.period > 0 && (typeof($scope.orderType) !== "undefined")){
            return true;        }
        return false;
    }
    $scope.currentFacility = "something";


    $scope.showFacility = function(fac) {
        //alert($scope.currentFacility);
        showFacilityName = (fac!=$scope.currentFacility);

        $scope.currentFacility = fac;
        return showFacilityName;
    }


}
