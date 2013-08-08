function StockedOutController($scope, RequisitionGroupsByProgramSchedule , RequisitionGroups, StockedOutReport, ReportSchedules, ReportFacilityTypes , ReportPeriods, ProductCategories , ReportPrograms, Products,OperationYears,Months, $http, $routeParams,$location) {
    //to minimize and maximize the filter section
    var section = 1;
    $scope.summaries = {};

    $scope.section = function (id) {
        section = id;
    };

    $scope.show = function (id) {
        return section == id;
    };
    // lookups and references

    $scope.pagingOptions = {
        pageSizes: [5, 10, 20, 40, 50, 100],
        pageSize: 10,
        totalServerItems: 0,
        currentPage: 1
    };

    // >>> Period type
    // default to the monthly period type
    $scope.periodType = 'monthly';

    $scope.periodTypes = [
        {'name':'Monthly', 'value':'monthly'},
        {'name':'Quarterly', 'value':'quarterly'},
        {'name':'Semi Anual', 'value':'semi-anual'},
        {'name':'Annual', 'value':'annual'}
    ];

    $scope.startYears = [];
    OperationYears.get(function(data){
        $scope.startYears  = data.years;
    });

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

    $scope.isMonthly = function(){
        return $scope.periodType == 'monthly';
    };

    $scope.isQuarterly = function(){
        return $scope.periodType == 'quarterly';
    };

    $scope.isSemiAnnualy  = function(){
        return $scope.periodType == 'semi-anual';
    };

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
    // <<< Period type

     $scope.filterGrid = function (){
        $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
    };

    //filter form data section
    $scope.filterOptions = {
        period: $scope.period,
        ftype: $scope.facilityType,
        rgroup: $scope.rgroup,
        filterText: "",
        useExternalFilter: false
    };



    //filter form data section
    $scope.filterObject =  {
        facilityType : $scope.period

    };

    ReportPrograms.get(function(data){
        $scope.programs = data.programs;
        $scope.programs.push({'name':'Select a Program'});
    })



    ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
        $scope.schedules.push({'name':'Select a Schedule'});
    })

    ReportFacilityTypes.get(function(data) {
        $scope.facilityTypes = data.facilityTypes;
        $scope.facilityTypes.push({'name': 'All facility types'});
    });

    ProductCategories.get(function(data){
        $scope.productCategories = data.productCategoryList;
        $scope.productCategories.push({'name':"All categories"})
    })


    Products.get(function(data){
        $scope.products = data.productList;
        $scope.products.push({'name': 'All Products','id':'All'});
    });

    $scope.ChangeSchedule = function(){
        //  if($scope.schedule != undefined &&  $scope.isPreDefined){
        ReportPeriods.get({ scheduleId : $scope.schedule },function(data) {
            $scope.periods = data.periods;
            $scope.periods.push({'name': 'Select Period'});
        });

        RequisitionGroupsByProgramSchedule.get({program: $scope.program, schedule:$scope.schedule}, function(data){
            $scope.requisitionGroups = data.requisitionGroupList;
            $scope.requisitionGroups.push({'name':'All requsition groups'});
        });
        //  }

    }


    $scope.currentPage = ($routeParams.page) ? parseInt($routeParams.page) || 1 : 1;

    $scope.exportReport   = function (type){

        var param = $scope.getParams(1, 1);
        var paramString = jQuery.param(param);
        var url = '/reports/download/stocked_out/' + type + '?' + paramString;
        window.open(url);
    }

    $scope.goToPage = function (page, event) {
        angular.element(event.target).parents(".dropdown").click();
        $location.search('page', page);
    };

    $scope.$watch("currentPage", function () {  //good watch no problem

        if($scope.currentPage != undefined && $scope.currentPage != 1){
            //when clicked using the links they have done updated the paging info no problem here
            $location.search("page", $scope.currentPage);
        }
    });

    $scope.$on('$routeUpdate', function () {
        if (!utils.isValidPage($routeParams.page, $scope.numberOfPages)) {
            $location.search('page', 1);
            return;
        }
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
        $scope.filterGrid();
    });

    $scope.$watch('endYear', function(selection){

        var date = new Date();
        if(selection != undefined || selection == "" ){
            $scope.filterObject.toYear =  selection;
            adjustEndMonths();
            adjustEndQuarters();
            adjustEndSemiAnnuals();
        }else{

            $scope.endYear  = date.getFullYear().toString();
            $scope.filterObject.toYear =  date.getFullYear();

        }
        $scope.filterGrid();
    });

    $scope.$watch('startQuarter', function(selection){

        if(selection != undefined || selection == ""){
            $scope.filterObject.fromQuarter =  selection;
            adjustEndQuarters();
        }else{
            var date = new Date();
            $scope.filterObject.fromQuarter =  (date.getMonth() / 4)+1;
        }
        $scope.filterGrid();
    });

    $scope.$watch('endQuarter', function(selection){
        if(selection != undefined || selection == ""){
            $scope.filterObject.toQuarter =  selection;
        }else{
            var date = new Date();
            $scope.filterObject.toQuarter =  (date.getMonth() / 4)+1;
        }
        $scope.filterGrid();
    });

    $scope.$watch('startHalf', function(selection){

        if(selection != undefined || selection == ""){
            $scope.filterObject.fromSemiAnnual =  selection;
            adjustEndSemiAnnuals();
        }else{
            $scope.filterObject.fromSemiAnnual =  1;
        }
        $scope.filterGrid();
    });
    $scope.$watch('endHalf', function(selection){

        if(selection != undefined || selection == ""){
            $scope.filterObject.toSemiAnnual =  selection;
        }else{
            var date = new Date();
            $scope.filterObject.toSemiAnnual =  1;
        }
        $scope.filterGrid();
    });

    $scope.$watch('startMonth', function(selection){

        var date = new Date();
        if(selection != undefined || selection == ""){
            $scope.filterObject.fromMonth =  selection-1;
            adjustEndMonths();
        }else{
            $scope.startMonth = (date.getMonth()+1 );
            $scope.filterObject.fromMonth =  (date.getMonth()+1);
        }
        $scope.filterGrid();
    });


    $scope.$watch('endMonth', function(selection){
        var date = new Date();
        if(selection != undefined || selection == ""){
            $scope.filterObject.toMonth =  selection-1;
        }else{
            $scope.endMonth = (date.getMonth() +1 );
            $scope.filterObject.toMonth =  (date.getMonth()+1);
        }
        $scope.filterGrid();
    });

    var adjustEndMonths = function(){
        if($scope.startMonth != undefined && $scope.startMonths != undefined && $scope.startYear == $scope.endYear ){
            $scope.endMonths = [];
            $.each($scope.startMonths,function(idx,obj){
                if( obj.value >= $scope.startMonth){
                    $scope.endMonths.push({'name':obj.name, 'value': obj.value});
                }
            });
            if($scope.endMonth < $scope.startMonth){
                $scope.endMonth = $scope.startMonth;
            }
        }else{
            $scope.endMonths = $scope.startMonths;
        }
    };
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
    };

    var adjustEndQuarters = function(){
        if($scope.startQuarter != undefined && $scope.startYear == $scope.endYear){
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
    };

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
    };

    $scope.$watch('periodType', function(selection){
        if(selection != undefined || selection == ""){
            $scope.filterObject.periodType =  selection;

        }else{
            $scope.filterObject.periodType =  "monthly";
        }
        $scope.filterGrid();
    });

/*
    if(selection == "custom"){
        //populate the  requisition group with the program only
    }

}else{
    $scope.filterObject.periodType =  "predefined";
}

});
*/


$scope.sortInfo = { fields:["code","facilityType"], directions: ["ASC"]};

$scope.setPagingData = function(data, page, pageSize, total){
    $scope.myData = data;
    $scope.pagingOptions.totalServerItems = total;
    $scope.numberOfPages = ( Math.ceil( total / pageSize))  ? Math.ceil( total / pageSize) : 1 ;

};


$scope.getParams = function(pageSize, page){
    var params  = {};
    if(pageSize != undefined && page != undefined ){
        var params =  {
            "max" : pageSize,
            "page" : page
        };
    }

    $.each($scope.sortInfo.fields, function(index, value) {
        if(value != undefined) {
            params['sort-' + $scope.sortInfo.fields[index]] = $scope.sortInfo.directions[index];
        }
    });

    params.period   = $scope.period;
    params.rgroup   = $scope.rgroup;
    params.ftype    = $scope.facilityType;
    params.program  = $scope.program;
    params.schedule = $scope.schedule;
    params.productCategory = $scope.productCategory;
    params.periodType = $scope.periodType;
    params.fromYear = $scope.startYear;
    params.fromMonth = $scope.startMonth;
    params.toYear = $scope.endYear;
    params.toMonth = $scope.endMonth
    return params;
}

$scope.getPagedDataAsync = function (pageSize, page) {
    var params = $scope.getParams(pageSize, page);
    StockedOutReport.get(params, function(data) {
        $scope.setPagingData(data.pages.rows[0].details,page,pageSize,data.pages.total);
        //$scope.summaries    =  data.pages.rows[0].summary;
    });

};

$scope.$watch('pagingOptions.currentPage', function () {
    $scope.currentPage = $scope.pagingOptions.currentPage;
    $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
}, true);

$scope.$watch('pagingOptions.pageSize', function () {
    $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
}, true);

$scope.$watch('sortInfo', function () {

    $.each($scope.sortInfo.fields, function(index, value) {
        if(value != undefined)
            $scope.filterObject[$scope.sortInfo.fields[index]] = $scope.sortInfo.directions[index];
    });
    $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
}, true);

$scope.gridOptions = {
    data: 'myData',
    columnDefs:
        [
            { field: 'code', displayName: 'Code', width: "*" },
            { field: 'name', displayName: 'Facility Name', width: "***", resizable: false},
            { field: 'facilityType', displayName: 'Facility Type', width: "*", resizable: false},
            { field: 'location', displayName: 'Location', width: "*" }
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

}
