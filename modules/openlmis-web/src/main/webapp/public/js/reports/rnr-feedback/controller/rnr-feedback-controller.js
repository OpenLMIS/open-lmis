function RnRFeedbackController($scope, RnRFeedbackReport, Products ,ReportFacilityTypes,OperationYears,ReportPeriods,ReportPeriodsByScheduleAndYear,AllReportPeriods,ReportFilteredPeriods, $http,ReportSchedules, ReportPrograms,AllFacilites,GetFacilityByFacilityType,SettingsByKey, $routeParams,$location) {
    //to minimize and maximize the filter section
    var section = 1;

    $scope.section = function (id) {
        section = id;
    };

    $scope.showMessage = true;
    $scope.message = "Indicates a required field."

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
    $scope.orderType = 'Regular'


    $scope.filterGrid = function (){
        $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
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

    ReportFacilityTypes.get(function(data) {
        $scope.facilityTypes = data.facilityTypes;
        //  $scope.facilityTypes.push({'name': 'All Facility Types', 'id' : 'All'});
    });

    AllFacilites.get(function(data){
        $scope.allFacilities = data.allFacilities;
    });


    Products.get(function(data){
        $scope.products = data.productList;
        $scope.products.unshift({'name': '-- All Products --', 'id':'All'});
        var ind_prod = $scope.IndicatorProductsDescription;
        $scope.products.unshift({'name': '-- '.concat(ind_prod).concat(' --'), 'id':'-1'});
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
        }else if(selection != undefined || selection == ""){
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
        if(selection != undefined || selection == ""){
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
        if(selection != undefined || selection == ""){
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
        } else if (selection != undefined || selection == "") {
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
            $scope.filterObject.productId =  -1;
        }else if(selection != undefined || selection == ""){
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
        }else if(selection != undefined || selection == ""){
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

    $scope.$watch('period', function(selection){
        if(selection == "All"){
            $scope.filterObject.periodId =  -1;
        }else if(selection != undefined || selection == ""){
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
        } else if (selection != undefined || selection == "") {
            $scope.filterObject.year = selection;

        } else {
            $scope.filterObject.year = 0;
        }

        if($scope.filterObject.year == -1 || $scope.filterObject.year == 0){

            $scope.ChangeSchedule('bySchedule');
        }else{

            $scope.ChangeSchedule('byYear');
        }
    });

    $scope.currentPage = ($routeParams.page) ? parseInt($routeParams.page) || 1 : 1;

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

        if($scope.currentPage != undefined && $scope.currentPage != 1){
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

    $scope.getPagedDataAsync = function (pageSize, page) {
        var params  = {};
        if(pageSize != undefined && page != undefined ){
            var params =  {
                "max" : pageSize,
                "page" : page
            };
        }

        $.each($scope.filterObject, function(index, value) {
            //if(value != undefined)
            params[index] = value;
        });

        RnRFeedbackReport.get(params, function(data) {
            $scope.setPagingData(data.pages.rows,page,pageSize,data.pages.total);
        });

    };

    $scope.$watch('pagingOptions.currentPage', function () {
        $scope.currentPage = $scope.pagingOptions.currentPage;
        $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
    }, true);

    $scope.$watch('pagingOptions.pageSize', function () {
        $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
    }, true);



    $scope.sortInfo = { fields:["productCode","facilityName"], directions: ["ASC","ASC"]};

    // put out the sort order
    $.each($scope.sortInfo.fields, function(index, value) {
        if(value != undefined) {
            $scope.filterObject['sort-'+$scope.sortInfo.fields[index]] = $scope.sortInfo.directions[index];
        }
    });
    $scope.$watch('sortInfo', function () {

        $.each($scope.sortInfo.fields, function(index, value) {
            if(value != undefined)
                $scope.filterObject['sort-'+$scope.sortInfo.fields[index]] = $scope.sortInfo.directions[index];
        });
        $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
    }, true);
    $scope.formatNumber = function(value){
        return utils.formatNumber(value,'0,000');
    };
    $scope.gridOptions = {
        data: 'myData',
        columnDefs:
    [
        { field: 'productCode', displayName: 'Code', width: "100px;", resizable: false},
        { field: 'product', displayName: 'Product', width: "250px;", resizable: false},
                { field: 'unit', displayName: 'Unit', width: "100" },
        { field: 'beginningBalance', displayName: 'Beginning Balance', width : "180px;", cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
        { field: 'totalQuantityReceived', displayName: 'Quantity Received', width : "180px;", cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
        { field: 'totalQuantityDispensed', displayName: 'Quantity Dispensed', width : "180px;", cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
        { field: 'adjustments', displayName: 'Adjustments', width : "180px;", cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
        { field: 'physicalCount', displayName: 'Physical Count', width : "180px;",cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
        { field: 'adjustedAMC', displayName: 'Adjusted AMC', width : "180px;", cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
        { field: 'newEOP', displayName: 'New EOP', width : "180px;", cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
        { field: 'orderQuantity', displayName: 'Order Quantity', width : "180px;", cellClass : 'pull-right',cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
        { field: 'quantitySupplied', displayName: 'Quantity Supplied', width : "180px;", cellClass : 'ngCellTextRight', cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'}

    ],
        enablePaging: true,
        enableSorting :true,
        showFooter: true,
        selectWithCheckboxOnly :false,
        pagingOptions: $scope.pagingOptions,
        filterOptions: $scope.filterOptions,
        useExternalSorting: true,
        sortInfo: $scope.sortInfo,
        showColumnMenu: true,
        enableRowReordering: true,
        showFilter: true,
        plugins: [new ngGridFlexibleHeightPlugin()]

    };

    /*function parseJsonDate(jsonDate) {
        var offset = new Date().getTimezoneOffset() * 60000;
        var parts = /\/Date\((-?\d+)([+-]\d{2})?(\d{2})?.*//*.exec(jsonDate);
        if (parts[2] == undefined) parts[2] = 0;
        if (parts[3] == undefined) parts[3] = 0;
        return new Date(+parts[1] + offset + parts[2] * 3600000 + parts[3] * 60000);
    };

    var init = function () {

        $scope.periodType = $scope.defaultSettings('P');

        if ($scope.periodType == 'quarterly') {
            $scope.startQuarter = $scope.defaultSettings('Q');
        } else {
            $scope.startMonth = $scope.defaultSettings('M');
        }
        $scope.startYear = $scope.defaultSettings('Y');
    };
    init();
*/

}
