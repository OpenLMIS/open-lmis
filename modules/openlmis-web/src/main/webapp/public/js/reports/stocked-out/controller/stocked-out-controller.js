function StockedOutController($scope, $window,$filter, ngTableParams,  $http, $routeParams,$location ,
                                StockedOutReport, ReportProductsByProgram , ReportPrograms, ProductCategories, RequisitionGroups , ReportFacilityTypes, FacilitiesByProgramParams, GeographicZones,OperationYears,Months) {

    $scope.startYears = [];
    OperationYears.get(function(data){
        $scope.startYears = $scope.endYears = data.years;
        adjustEndYears();
    });

    // default to the monthly period type
    $scope.periodType = 'monthly';

    $scope.periodTypes = [
        {'name':'Monthly', 'value':'monthly'},
        {'name':'Quarterly', 'value':'quarterly'},
        {'name':'Semi Anual', 'value':'semi-anual'},
        {'name':'Annual', 'value':'annual'}
    ];


    $scope.startQuarters = function(){
        return $scope.quarters;
    };

    // initialize default quarters
    $scope.fromQuarter = $scope.toQuarter = 1;

    $scope.endQuarters  = function(){
        if($scope.startYear == $scope.endYear && $scope.startQuarter !== '' ){
            var arr = [];
            for(var i=$scope.startQuarter - 1; i < $scope.quarters.length;i++){
                arr.push($scope.quarters[i]);
            }
            return arr;
        }
        return $scope.quarters;
    };
    Months.get(function(data){
        var months = data.months;

        if(months !== null){
            $scope.startMonths = [];
            $scope.endMonths = [];
            $.each(months,function(idx,obj){
                $scope.startMonths.push({'name':obj.toString(), 'value': idx+1});
                $scope.endMonths.push({'name':obj.toString(), 'value': idx+1});
            });
        }

    });

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

    $scope.product;

    RequisitionGroups.get(function(data){
        $scope.requisitionGroups = data.requisitionGroupList;
        $scope.requisitionGroups.unshift({'name':'-- All Reporting Groups --'});
    });

    // copy over the start month and end months
    // this is just for initial loading.
    $(function (){
        $scope.endYears = $scope.startYears;
        $scope.startQuarters  = $scope.quarters;
        $scope.endQuarters  = $scope.quarters;
        $scope.startSemiAnnuals = $scope.semiAnnuals;
        $scope.endSemiAnnuals = $scope.semiAnnuals;
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
        if ($scope.filterForm.$invalid) {
            $scope.errorShown = true;
            //return;
        }
        $scope.getPagedDataAsync();
    };

    //filter form data section
    $scope.filterObject =  {

        periodType: $scope.periodType,
        fromYear: $scope.fromYear,
        fromMonth: $scope.fromMonth,
        fromQuarter: $scope.fromQuarter,
        fromSemiAnnual:$scope.startHalf,
        toYear: $scope.toYear,
        toMonth: $scope.toMonth,
        toQuarter: $scope.toQuarter,
        toSemiAnnual:$scope.endHalf,

        productId: $scope.product,
        product: "",

        productCategoryId: $scope.productCategory,
        productCategory: "",

        programId : $scope.program,
        program : "",

        facilityTypeId : $scope.facilityType,
        facilityType : "",

        facilityId : $scope.facility,
        facility : "",

        rgroupId : $scope.rgroup,
        rgroup : "",

        zoneId : $scope.zone,
        zone : "",

        pdformat : 0
    };

    ReportFacilityTypes.get(function(data) {
        $scope.facilityTypes = data.facilityTypes;
        $scope.facilityTypes.unshift({'name': '-- All Facility Types --'});
    });


    ProductCategories.get(function(data){
        $scope.productCategories = data.productCategoryList;
        $scope.productCategories.unshift({'name': '-- All Product Categories --'});
    });

    ReportPrograms.get(function(data){
        $scope.programs = data.programs;
        $scope.programs.unshift({'name':'-- Select Programs --'});

        $scope.products = [];
        $scope.products.push({name:"-- Select a Program --"});

        $scope.facilities = [];
        $scope.facilities.push({name:'-- Select a Program -- '});
    });

    $scope.ProgramChanged = function(){
        // load products
        ReportProductsByProgram.get({programId: $scope.filterObject.programId}, function(data){
            $scope.products = data.productList;
            $scope.products.unshift({name: '-- Indicator / Tracer Product --', id:'0'});
            $scope.products.unshift({name: '-- All Products --', id:'-1'});

        });
        // load facilities
        FacilitiesByProgramParams.get({
                program: $scope.filterObject.programId ,
                schedule: 0,
                type: 0
            }, function(data){
                $scope.facilities = data.facilities;
                $scope.facilities.unshift({name:'-- All Facilities --'});
            }
        );

        $scope.filterGrid();
    };

    $scope.$watch('zone.value', function(selection){
        if(selection !== undefined || selection === ""){
            $scope.filterObject.zoneId =  selection;
            $.each( $scope.zones,function( item,idx){
                if(idx.id == selection){
                    $scope.filterObject.zone = idx.name;
                }
            });
        }else{
            $scope.filterObject.zoneId = 0;
            $scope.filterObject.zone = "";
        }
        $scope.filterGrid();
    });


    $scope.$watch('facilityType.value', function(selection){
        if(selection !== undefined || selection === ""){
            $scope.filterObject.facilityTypeId =  selection;
            $.each( $scope.facilityTypes,function( item,idx){
                if(idx.id == selection){
                    $scope.filterObject.facilityType = idx.name;
                }
            });
         }else{
            $scope.filterObject.facilityTypeId =  0;
            $scope.filterObject.facilityType = "";
        }
        $scope.filterGrid();
    });

    $scope.$watch('startYear', function(selection){
        var date = new Date();
        if(selection !== undefined || selection === ""){
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
        if(selection !== undefined || selection === "" ){
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
        if(selection !== undefined || selection === ""){
            $scope.filterObject.fromQuarter =  selection;
            adjustEndQuarters();
        }else{
            var date = new Date();
            $scope.filterObject.fromQuarter =  1;
        }
        $scope.filterGrid();
    });

    $scope.$watch('endQuarter', function(selection){
        if(selection !== undefined || selection === ""){
            $scope.filterObject.toQuarter =  selection;
        }else{
            var date = new Date();
            $scope.filterObject.toQuarter =  $scope.filterObject.fromQuarter;
        }
        $scope.filterGrid();
    });

    $scope.$watch('startHalf', function(selection){

        if(selection !== undefined || selection === ""){
            $scope.filterObject.fromSemiAnnual =  selection;
            adjustEndSemiAnnuals();
        }else{
            $scope.filterObject.fromSemiAnnual =  1;
        }
        $scope.filterGrid();
    });
    $scope.$watch('endHalf', function(selection){

        if(selection !== undefined || selection === ""){
            $scope.filterObject.toSemiAnnual =  selection;
        }else{
            var date = new Date();
            $scope.filterObject.toSemiAnnual =  1;
        }
        $scope.filterGrid();
    });

    $scope.$watch('startMonth', function(selection){
        if($scope.startMonth !== undefined || $scope.startMonth === ""){
            adjustEndMonths();
        }else{
            var date = new Date();
            $scope.endMonth = $scope.startMonth = (date.getMonth()+1 ).toString();
        }
        $scope.filterObject.fromMonth = $scope.startMonth;
        $scope.filterGrid();
    });

    $scope.$watch('endMonth', function(selection){
        $scope.filterObject.toMonth = $scope.endMonth;
        $scope.filterGrid();
    });

    var adjustEndMonths = function(){
        if($scope.startMonth !== undefined && $scope.startMonths !== undefined && $scope.startYear == $scope.endYear ){
            $scope.endMonths = [];
            $.each($scope.startMonths,function(idx,obj){
                if(obj.value >= $scope.startMonth){
                    $scope.endMonths.push({'name':obj.name, 'value': obj.value});
                }
            });
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
        if($scope.startQuarter !== undefined && $scope.startYear === $scope.endYear){
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
        if(selection !== undefined || selection === ""){
            $scope.filterObject.periodType =  selection;

        }else{
            $scope.filterObject.periodType =  "monthly";
        }
        $scope.filterGrid();

    });


    $scope.$watch('product', function(selection){
        if(selection !== undefined || selection === ""){
            $scope.filterObject.productId =  selection;
            $.each( $scope.products,function( item,idx){
                if(idx.id == selection){
                    $scope.filterObject.product = idx.name;
                }
            });

        }else{
            $scope.filterObject.productId =  0;
            $scope.filterObject.product = "";
        }
        $scope.filterGrid();
    });


    $scope.$watch('rgroup', function(selection){

        if(selection !== undefined || selection === ""){
            $scope.filterObject.rgroupId =  selection;
            $.each( $scope.requisitionGroups,function( item,idx){
                if(idx.id == selection){
                    $scope.filterObject.rgroup = idx.name;
                }
            });
        }else{
            $scope.filterObject.rgroupId =  0;
            $scope.filterObject.rgroup = "";
        }
        $scope.filterGrid();
    });

    $scope.$watch('filterObject.facilityId',function(){
        $scope.filterGrid();
    });

    $scope.exportReport   = function (type){
        $scope.filterObject.pdformat =1;
        var params = jQuery.param($scope.filterObject);
        var url = '/reports/download/stocked_out/' + type + '?' + params;
        $scope.$window.open(url);
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

        if($scope.filterObject.program === undefined){
            return;
        }

        $scope.filterObject.max = 10000;
        $scope.filterObject.page = 1;

        // clear old data if there was any
        $scope.data = $scope.datarows = [];

        StockedOutReport.get($scope.filterObject, function (data) {
            if(data.pages !== undefined && data.pages.rows !== undefined ){
                $scope.data = data.pages.rows;
                $scope.paramsChanged($scope.tableParams);
            }

        });

    };

}