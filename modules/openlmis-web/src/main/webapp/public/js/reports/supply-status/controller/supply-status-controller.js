function SupplyStatusController($scope, $filter, ngTableParams
                                , SupplyStatusReport, ReportSchedules, ReportPrograms , ReportPeriods , ReportProductsByProgram ,ReportFacilityTypes, FacilitiesByProgramParams,GetFacilityByFacilityType, GeographicZones, RequisitionGroups,SettingsByKey, $http, $routeParams,$location) {
    //to minimize and maximize the filter section
    var section = 1;
    $scope.showMessage = true;
    $scope.message = "Indicates a required field."


    $scope.IndicatorProductsKey = "INDICATOR_PRODUCTS";

    SettingsByKey.get({key: $scope.IndicatorProductsKey},function (data){
        $scope.IndicatorProductsDescription = data.settings.value;
    });


    $scope.section = function (id) {
        section = id;
    };

    $scope.show = function (id) {
        return section == id;
    };
    // lookups and references


    $scope.filterGrid = function (){
        $scope.getPagedDataAsync(0, 0);
    };


    //filter form data section
    $scope.filterObject =  {
        facilityTypeId : $scope.facilityType,
        facilityType : "",
        programId : $scope.program,
        program : "",
        periodId : $scope.period,
        period : "",
        zoneId : $scope.zone,
        zone : "",
        productId : $scope.productId,
        product : "",
        scheduleId : $scope.schedule,
        schedule : "",
        rgroupId : $scope.rgroup,
        rgroup : "",
        facilityId : $scope.facility,
        facility : ""
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
        $scope.filterObject.type = '0';
    });


    ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
        $scope.schedules.unshift({'name':'-- Select a Schedule --', 'id':''});

        $scope.allFacilities = [];
        $scope.allFacilities.push({code:'-- Select a Facility --',id:''});
    });

    $scope.ProgramChanged = function(){
        if($scope.filterObject.schedule != ''){
            $scope.ChangeSchedule();
        }

        ReportProductsByProgram.get({programId: $scope.filterObject.program}, function(data){
            $scope.products = data.productList;
            $scope.products.unshift({id: '',name: '-- Select Product --'});
        });
    };

    $scope.ChangeSchedule = function(){
        ReportPeriods.get({ scheduleId : $scope.filterObject.schedule },function(data) {
            $scope.periods = data.periods;
            $scope.periods.unshift({'name': '-- Select Period --', 'id':''});
        });
        // load products

        $scope.loadFacilities();
    } ;

    $scope.loadFacilities = function(){
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

    GeographicZones.get(function(data) {
        $scope.zones = data.zones;
        $scope.zones.unshift({'name': '-- All Zones --', 'id' : ''});
    });


    $scope.exportReport   = function (type){
        $scope.filterObject.pdformat =1;
        var params = jQuery.param($scope.filterObject);
        var url = '/reports/download/supply_status/' + type +'?' + params;
        window.open(url);

    }

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
        $scope.datarows = $scope.data = [];

        pageSize = 10000;
        page = 1;
        var params  = {};
        if(pageSize != undefined && page != undefined ){
            var params =  {
                "max" : pageSize,
                "page" : page
            };
        }

        $.each($scope.filterObject, function(index, value) {
            params[index] = value;
        });
        SupplyStatusReport.get(params, function(data) {
            $scope.data = data.pages.rows;
            $scope.paramsChanged($scope.tableParams);
        });

    };


    $scope.formatNumber = function(value){
        return utils.formatNumber(value,'0,0.00');
    };





}
