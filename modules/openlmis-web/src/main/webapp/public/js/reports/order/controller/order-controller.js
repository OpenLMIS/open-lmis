function OrderReportController($scope, ngTableParams, $filter, OrderReport, ReportProductsByProgram ,ReportFacilityTypes,AllReportPeriods,ReportPeriods,ReportPeriodsByScheduleAndYear,ReportFilteredPeriods, $http,OperationYears, Months, ReportPrograms,FacilitiesByProgramParams,GetFacilityByFacilityType,SettingsByKey,ReportSchedules, $routeParams,$location) {


    $scope.showMessage = true;
    $scope.message = "Indicates a required field."

    $scope.IndicatorProductsKey = "INDICATOR_PRODUCTS";

    SettingsByKey.get({key: $scope.IndicatorProductsKey},function (data){
        $scope.IndicatorProductsDescription = data.settings.value;
    });

    $scope.orderType = 'Regular';
    $scope.orderTypes = [
        {'name':'Regular', 'value':'Regular'},
        {'name':'Emergency', 'value':'Emergency'}
     ];

    $scope.startYears = [];
    OperationYears.get(function (data) {
        $scope.startYears = data.years;
        $scope.startYears.unshift('-- All Years --');
    });

    $scope.filterGrid = function (){
           $scope.getPagedDataAsync(0, 0);
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
         period : "",
         productId : $scope.productId,
         product : "",
         scheduleId : $scope.schedule,
         schedule : "",
         facilityId: $scope.facility,
         year : "",
         orderType: ""
    };

    ReportPrograms.get(function(data){
        $scope.programs = data.programs;
    });

    ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
        $scope.schedules.unshift({'name':'-- Select a Schedule --', 'id':'0'}) ;
    });

    ReportFacilityTypes.get(function(data) {
        $scope.facilityTypes = data.facilityTypes;
        $scope.facilityTypes.unshift({'name': '-- All Facility Types --', 'id' : 'All'});
    });


    AllReportPeriods.get(function (data) {
        $scope.periods = data.periods;
        $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});
    });

    $scope.ChangeReportingPeriods = function(){
        var params  = {};

        $.each($scope.filterObject, function(index, value) {

            params[index] = value;
        });

        ReportFilteredPeriods.get(params, function(data) {
            $scope.periods = data.periods;
            $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});
        });
    };


    $scope.ChangeProgram = function(){
        ReportProductsByProgram.get({programId: $scope.program}, function(data){
            $scope.products = data.productList;
            $scope.products.unshift({id: '',name: '-- Indicator / Tracer Product --', id:'0'});
            $scope.products.unshift({id: '',name: '-- All Products --', id:'-1'});

        });
    }

    $scope.ChangeSchedule = function(){
        if(  $scope.filterObject.year != -1 &&  $scope.filterObject.year != 0){
            ReportPeriodsByScheduleAndYear.get({ scheduleId: $scope.schedule, year: $scope.filterObject.year}, function(data){
                $scope.periods = data.periods;
                $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});
            });
        }else{
            ReportPeriods.get({ scheduleId : $scope.schedule },function(data) {
                $scope.periods = data.periods;
                $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});

            });
        }

        // load facilities
        FacilitiesByProgramParams.get({
                program: $scope.filterObject.programId ,
                schedule: $scope.schedule,
                type: 0
            }, function(data){
                $scope.facilities = data.facilities;
                $scope.facilities.unshift({name:'-- All Facilities --'});
            }
        );

    };


    $scope.$watch('schedule', function(selection){
        if(selection != undefined || selection == ""){
            $scope.filterObject.scheduleId =  selection;
            $.each( $scope.schedules,function( item,idx){
                if(idx.id == selection){
                    $scope.filterObject.schedule = idx.name;
                }
            });
        }else{
            $scope.filterObject.scheduleId =  0;
        }
    });


    $scope.$watch('year', function (selection) {
        if (selection == "-- All Years --") {
            $scope.filterObject.year = -1;
        } else if (selection != undefined || selection == "") {
            $scope.filterObject.year = selection;
        } else {
            $scope.filterObject.year = 0;
        }

    });

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
            $scope.allFacilities.unshift({'name':'-- Select Facility --','id':'0'});
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

    $scope.$watch('product', function(selection){
        if(selection == "All"){
            $scope.filterObject.productId =  0;
        }else if(selection != undefined || selection == ""){
            $scope.filterObject.productId =  selection;
            $.each($scope.products, function( item,idx){
                if(idx.id == selection){
                    $scope.filterObject.product = idx.name;
                }
            });
        }else{
            $scope.filterObject.productId =  -1;
        }
        $scope.filterGrid();
    });

    $scope.$watch('program', function(selection){
        if(selection == "All"){
            $scope.filterObject.programId =  -1;
        }else if(selection != undefined || selection == ""){
            $scope.filterObject.programId =  selection;
            $.each($scope.programs, function(item,indx){
               if(indx.id == selection){
                   $scope.filterObject.program = indx.name;
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


    $scope.exportReport   = function (type){
        $scope.filterObject.pdformat =1;
        var params = jQuery.param($scope.filterObject);
        var url = '/reports/download/order_summary/' + type +'?' + params;
        window.open(url);
    };


    // the grid options
    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        total: 0,           // length of data
        count: 25           // count per page
    });

    $scope.paramsChanged = function(params) {

        // slice array data on pages
        if($scope.data == undefined ){
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
        var params =  {
            "max" : 100000,
            "page" : 1
        };

        $.each($scope.filterObject, function(index, value) {
                params[index] = value;
        });

        OrderReport.get(params, function(data) {
            $scope.data = data.pages.rows;
            $scope.paramsChanged( $scope.tableParams );
        });

    };


    $scope.formatNumber = function(value){
        return utils.formatNumber(value,'0,000');
    }


}
