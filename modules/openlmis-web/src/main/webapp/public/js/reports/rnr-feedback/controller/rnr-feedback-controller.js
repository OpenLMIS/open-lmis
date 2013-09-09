function RnRFeedbackController($scope, RnRFeedbackReport, Products ,ReportFacilityTypes,GeographicZones,AllReportPeriods,ReportFilteredPeriods, $http,OperationYears, Months, ReportPrograms,AllFacilites,GetFacilityByFacilityType,SettingsByKey, $routeParams,$location) {
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

    $scope.reporting = "quarterly";

    $scope.orderTypes = [
        {'name':'Regular', 'value':'Regular'},
        {'name':'Emergency', 'value':'Emergency'}
    ];

    //Order type defaults to Regular
    $scope.orderType = 'Regular'

    // default to the monthly period type

    $scope.periodTypes = [
        {'name':'Monthly', 'value':'monthly'},
        {'name':'Quarterly', 'value':'quarterly'},
        {'name':'Semi Anual', 'value':'semi-anual'},
        {'name':'Annual', 'value':'annual'}
    ];
    $scope.startYears = [];
    OperationYears.get(function(data){
        $scope.startYears  = data.years;
        adjustEndYears();
    });
    $scope.defaultSettings = function (str) {

        var retval = '';
        var months = new Array(12);
        months[0] = "Jan";
        months[1] = "Feb";
        months[2] = "Mar";
        months[3] = "Apr";
        months[4] = "May";
        months[5] = "Jun";
        months[6] = "Jul";
        months[7] = "Aug";
        months[8] = "Sep";
        months[9] = "Oct";
        months[10] = "Nov";
        months[11] = "Dec";

        var current_date = new Date();
        month_value = current_date.getMonth() - 6;
        day_value = current_date.getDate();
        year_value = current_date.getFullYear();

        retval = "";

        if (str == "M") {
            retval = month_value;
        }
        if (str == "Y") {
            retval = year_value;
        }
        if (str == "P") {
            retval = $scope.reporting;
        }

        if (str == "Q") {
            var d = new Date();
            retval = parseInt((d.getMonth() + 3) / 3) - 1;
            retval = retval + "";
        }
        return retval;
    };

    Months.get(function(data){
        var months = data.months;

        if(months != null){
            $scope.startMonths = [];
            $scope.endMonths = [];
            $.each(months,function(idx,obj){
                $scope.startMonths.push({'name':obj.toString(), 'value': idx+1});
                $scope.endMonths.push({'name':obj.toString(), 'value': idx+1});
            });
        }

    });

    $scope.startQuarters = function(){
        return $scope.quarters;
    };

    $scope.endQuarters  = function(){
        if($scope.startYear == $scope.endYear && $scope.startQuarter != '' ){
            var arr = [];
            for(var i=$scope.startQuarter - 1; i < $scope.quarters.length;i++){
                arr.push($scope.quarters[i]);
            }
            return arr;
        }
        return $scope.quarters;
    };

    $scope.quarters         = [
        {'name':'One','value':'1'},
        {'name':'Two','value':'2'},
        {'name':'Three','value':'3'},
        {'name':'Four','value':'4'}
    ];

    $scope.semiAnnuals= [
        {'name':'First Half','value':'1'},
        {'name':'Second Half','value':'2'}
    ];

    // copy over the start month and end months
    // this is just for initial loading.
    $(function (){
        $scope.startQuarters  = $scope.quarters;
        $scope.endQuarters  = $scope.quarters;
        $scope.endYears     = $scope.startYears;
        $scope.startSemiAnnuals = $scope.semiAnnuals;
        $scope.endSemiAnnuals = $scope.semiAnnuals;
        $scope.toQuarter = 1;
        $scope.fromQuarter = 1;
        $scope.startHalf = 1;
        $scope.endHalf = 1;
    });

    $scope.isMonthly = function(){
        return $scope.periodType == 'monthly';
    };

    $scope.isQuarterly = function(){
        return $scope.periodType == 'quarterly';
    };

    $scope.isSemiAnnualy  = function(){
        return $scope.periodType == 'semi-anual';
    };


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
        periodType: $scope.periodType,
        fromYear: $scope.fromYear,
        fromMonth: $scope.fromMonth,
        fromQuarter: $scope.fromQuarter,
        fromSemiAnnual:$scope.startHalf,
        toYear: $scope.toYear,
        toMonth: $scope.toMonth,
        toQuarter: $scope.toQuarter,
        toSemiAnnual:$scope.endHalf,
        programId : $scope.program,
        program : "",
        periodId : $scope.period,
        zoneId : $scope.zone,
        productId : $scope.productId,
        product : "",
        scheduleId : $scope.schedule,
        rgroupId : $scope.rgroup,
        rgroup : "",
        facilityName : $scope.facilityNameFilter,
        facilityId: $scope.facility,
        orderType: ""
    };

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

    GeographicZones.get(function(data) {
        $scope.zones = data.zones;
        //  $scope.zones.push({'name': '- All Zones -', 'id' : 'All'});
    });

    AllReportPeriods.get(function (data) {
        $scope.periods = data.periods;
        var startdt = parseJsonDate('/Date(' + $scope.periods[1].startdate + ')/');
        var enddt = parseJsonDate('/Date(' + $scope.periods[1].enddate + ')/');
        var diff = enddt.getMonth() - startdt.getMonth() + 1;

        if (diff == 3) {
            $scope.reporting = "quarterly";
        } else {
            $scope.reporting = "monthly";
        }

    });

    $scope.ChangeReportingPeriods = function(){
        var params  = {};

        $.each($scope.filterObject, function(index, value) {

            params[index] = value;
        });

        ReportFilteredPeriods.get(params, function(data) {
            $scope.periods = data.periods;
        });
    }

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

    $scope.$watch('facilityNameFilter', function(selection){
        if(selection != undefined || selection == ""){
            $scope.filterObject.facilityName =  selection;

        }else{
            $scope.filterObject.facilityName = "";
        }
        $scope.filterGrid();
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


    $scope.$watch('zone', function(selection){
        if(selection == "All"){
            $scope.filterObject.zoneId =  -1;
        }else if(selection != undefined || selection == ""){
            $scope.filterObject.zoneId =  selection;
        }else{
            $scope.filterObject.zoneId =  0;
        }
        $scope.filterGrid();
    });

    $scope.$watch('program', function(selection){
        if(selection == "All"){
            $scope.filterObject.programId =  -1;
        }else if(selection != undefined || selection == ""){
            $scope.filterObject.programId =  selection;
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

    $scope.$watch('startYear', function(selection){
        var date = new Date();
        if(selection != undefined || selection == ""){
            $scope.filterObject.fromYear =  selection;
            adjustEndYears();
            adjustEndMonths();
            adjustEndQuarters();
            adjustEndSemiAnnuals();
        }else{
            $scope.startYear  = date.getFullYear().toString();
            $scope.filterObject.fromYear =  date.getFullYear();
        }
        $scope.ChangeReportingPeriods();
        $scope.filterGrid();
    });

    $scope.$watch('endYear', function(selection){
        var date = new Date();
        if(selection != undefined || selection == ""){
            $scope.filterObject.toYear =  selection;
            adjustEndMonths();
            adjustEndQuarters();
            adjustEndSemiAnnuals();
        }else{
            $scope.endYear  = date.getFullYear().toString();
            $scope.filterObject.toYear =  date.getFullYear();
        }
        $scope.ChangeReportingPeriods();
        $scope.filterGrid();
    });

    $scope.$watch('startQuarter', function(selection){
        var date = new Date();
        if(selection != undefined || selection == ""){
            $scope.filterObject.fromQuarter =  selection;
            adjustEndQuarters();
        }else{
            var date = new Date();
            $scope.filterObject.fromQuarter =  1;
        }
        $scope.ChangeReportingPeriods();
        $scope.filterGrid();
    });

    $scope.$watch('endQuarter', function(selection){
        var date = new Date();
        if(selection != undefined || selection == ""){
            $scope.filterObject.toQuarter =  selection;
        }else{
            var date = new Date();
            $scope.filterObject.toQuarter =  $scope.filterObject.fromQuarter;
        }
        $scope.ChangeReportingPeriods();
        $scope.filterGrid();
    });

    $scope.$watch('startHalf', function(selection){

        if(selection != undefined || selection == ""){
            $scope.filterObject.fromSemiAnnual =  selection;
            adjustEndSemiAnnuals();
        }else{
            $scope.filterObject.fromSemiAnnual =  1;
        }
        $scope.ChangeReportingPeriods();
        $scope.filterGrid();
    });
    $scope.$watch('endHalf', function(selection){

        if(selection != undefined || selection == ""){
            $scope.filterObject.toSemiAnnual =  selection;
        }else{
            var date = new Date();
            $scope.filterObject.toSemiAnnual =  1;
        }
        $scope.ChangeReportingPeriods();
        $scope.filterGrid();
    });
    $scope.$watch('startMonth', function(selection){
        var date = new Date();
        if(selection != undefined || selection == ""){
            $scope.filterObject.fromMonth =  selection-1;
            adjustEndMonths();
        }else{
            $scope.startMonth = (date.getMonth()+1 ).toString();
            $scope.filterObject.fromMonth =  (date.getMonth()+1);
        }
        $scope.ChangeReportingPeriods();
        $scope.filterGrid();
    });

    $scope.$watch('endMonth', function(selection){
        var date = new Date();
        if(selection != undefined || selection == ""){
            $scope.filterObject.toMonth =  selection-1;
        }else{
            $scope.endMonth = (date.getMonth() +1 ).toString();
            $scope.filterObject.toMonth =  (date.getMonth()+1);
        }
        $scope.ChangeReportingPeriods();
        $scope.filterGrid();
    });

    var adjustEndMonths = function(){
        if($scope.startMonth != undefined && $scope.startMonths != undefined && $scope.startYear == $scope.endYear ){
            $scope.endMonths = [];
            $.each($scope.startMonths,function(idx,obj){
                if(obj.value >= $scope.startMonth){
                    $scope.endMonths.push({'name':obj.name, 'value': obj.value});
                }
            });
            if($scope.endMonth < $scope.startMonth){
                $scope.endMonth = $scope.startMonth;
            }
        }else{
            $scope.endMonths = $scope.startMonths;
        }
    }

    var adjustEndQuarters = function(){
        if($scope.startYear == $scope.endYear){
            $scope.endQuarters = [];
            $.each($scope.startQuarters, function(idx,obj){
                if(obj.value >= $scope.startQuarter){
                    $scope.endQuarters.push({'name':obj.name, 'value': obj.value});
                }
            });
            if($scope.endQuarter < $scope.startQuarter){
                $scope.endQuarter =  $scope.startQuarter;
            }
        }else{
            $scope.endQuarters = $scope.startQuarters;
        }
    }

    var adjustEndSemiAnnuals = function(){

        if($scope.startYear == $scope.endYear){
            $scope.endSemiAnnuals = [];
            $.each($scope.startSemiAnnuals, function(idx,obj){
                if(obj.value >= $scope.startHalf){
                    $scope.endSemiAnnuals.push({'name':obj.name, 'value': obj.value});
                }
            });
            if($scope.endHalf < $scope.startHalf){
                $scope.endHalf =  $scope.startHalf;
            }
        }else{
            $scope.endSemiAnnuals = $scope.startSemiAnnuals;
        }
    }

    var adjustEndYears = function(){
        $scope.endYears = [];
        $.each( $scope.startYears,function( idx,obj){
            if(obj >= $scope.startYear){
                $scope.endYears.push(obj);
            }
        });
        if($scope.endYear < $scope.startYear){
            $scope.endYear  = new Date().getFullYear();
        }
    }


    $scope.$watch('periodType', function(selection){
        if(selection != undefined || selection == ""){
            $scope.filterObject.periodType =  selection;

        }else{
            $scope.filterObject.periodType =  "monthly";
        }
        $scope.ChangeReportingPeriods();
        $scope.filterGrid();
    });



    $scope.currentPage = ($routeParams.page) ? parseInt($routeParams.page) || 1 : 1;

    $scope.exportReport   = function (type){
        $scope.filterObject.pdformat =1;
        var params = jQuery.param($scope.filterObject);
        var url = '/reports/download/rnr_feedback/' + type +'?' + params;
        window.open(url);

    }

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
    }
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

    function parseJsonDate(jsonDate) {
        var offset = new Date().getTimezoneOffset() * 60000;
        var parts = /\/Date\((-?\d+)([+-]\d{2})?(\d{2})?.*/.exec(jsonDate);
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


}
