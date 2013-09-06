function SupplyStatusController($scope, SupplyStatusReport, ReportSchedules, ReportPrograms , ReportPeriods , Products ,ReportFacilityTypes, AllFacilites,GetFacilityByFacilityType, GeographicZones, RequisitionGroups,SettingsByKey, $http, $routeParams,$location) {
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

    $scope.pagingOptions = {
        pageSizes: [ 20, 40, 50, 100],
        pageSize: 20,
        totalServerItems: 0,
        currentPage: 1
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
        $scope.facilityTypes.unshift({'name': '-- All Facility Types --', 'id' : 'All'});
    });

    AllFacilites.get(function(data){
        $scope.allFacilities = data.allFacilities;
        $scope.allFacilities.unshift({name:'-- Select a Facility --',id:''});
    });

    ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
        $scope.schedules.unshift({'name':'-- Select a Schedule --', 'id':''});
    });

    Products.get(function(data){
        $scope.products = data.productList;
        $scope.products.unshift({'name': '-- All Products --','id':'All'});
        var ind_desc = $scope.IndicatorProductsDescription;
        $scope.products.unshift({'name': '-- '.concat(ind_desc).concat(' --'),'id':'-1'});

    });

    $scope.ChangeSchedule = function(){
        ReportPeriods.get({ scheduleId : $scope.schedule },function(data) {
            $scope.periods = data.periods;
            $scope.periods.unshift({'name': '-- Select Period --', 'id':''});
        });
    }

    GeographicZones.get(function(data) {
        $scope.zones = data.zones;
        $scope.zones.unshift({'name': '-- All Zones --', 'id' : 'All'});
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

    $scope.ChangeFacility = function(){
        GetFacilityByFacilityType.get({ facilityTypeId : $scope.filterObject.facilityTypeId },function(data) {
            $scope.allFacilities =  data.facilities;
            $scope.allFacilities.unshift({name:'-- Select a Facility --',id:''});
        });
    };

    $scope.$watch('product', function(selection){
        if(selection == "All"){
            $scope.filterObject.productId =  0;
        }else if(selection != undefined || selection == ""){
            $scope.filterObject.productId =  selection;
            $.each($scope.products , function (item, idx) {
                if (idx.id == selection) {
                    $scope.filterObject.product = idx.name;
                }
            });
        }else{
            $scope.filterObject.productId =  -1;
        }
        $scope.filterGrid();
    });

    $scope.$watch('rgroup', function(selection){
        if(selection == "All"){
            $scope.filterObject.rgroupId =  -1;
        }else if(selection != undefined || selection == ""){
            $scope.filterObject.rgroupId =  selection;
            $.each( $scope.requisitionGroups,function( item,idx){
                if(idx.id == selection){
                    $scope.filterObject.rgroup = idx.name;
                }
            });
        }else{
            $scope.filterObject.rgroupId =  0;
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

    $scope.$watch('program', function(selection){
        if(selection == "All"){
            $scope.filterObject.programId =  -1;
        }else if(selection != undefined || selection == ""){
            $scope.filterObject.programId =  selection;
            $.each($scope.programs , function (item, idx) {
                if (idx.id == selection) {
                    $scope.filterObject.program = idx.name;
                }
            });

        }else{
            $scope.filterObject.programId =  0;
        }
        $scope.filterGrid();
    });

    $scope.$watch('schedule', function(selection){
        if(selection != undefined || selection == ""){
            $scope.filterObject.scheduleId =  selection;
            $.each($scope.schedules , function (item, idx) {
                if (idx.id == selection) {
                    $scope.filterObject.schedule = idx.name;
                }
            });
        }else{
            $scope.filterObject.scheduleId =  0;
        }
        $scope.filterGrid();
    });

    $scope.$watch('zone', function(selection){
        if(selection == "All"){
            $scope.filterObject.zoneId =  0;
        }else if(selection != undefined || selection == ""){
            $scope.filterObject.zoneId =  selection;
            $.each($scope.zones, function(item, idx){
                if(idx.id == selection){
                    $scope.filterObject.zone = idx.name;
                }
            })
        }else{
            $scope.filterObject.zoneId =  -1;
        }
        $scope.filterGrid();
    });

    $scope.currentPage = ($routeParams.page) ? parseInt($routeParams.page) || 1 : 1;

    $scope.exportReport   = function (type){
        $scope.filterObject.pdformat =1;
        var params = jQuery.param($scope.filterObject);
        var url = '/reports/download/supply_status/' + type +'?' + params;
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

    $scope.sortInfo = { fields:["code","facility"], directions: ["ASC","ASC"]};
    // put default sort criteria
    $.each($scope.sortInfo.fields, function(index, value) {
        if(value != undefined) {
            $scope.filterObject['sort-' + $scope.sortInfo.fields[index]] = $scope.sortInfo.directions[index];
        }
    });

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
        SupplyStatusReport.get(params, function(data) {

            $scope.setPagingData(data.pages.rows,page,pageSize,data.pages.total);
            $scope.data = data.pages.rows;
        });

    };

    $scope.$watch('pagingOptions.currentPage', function () {
        $scope.currentPage = $scope.pagingOptions.currentPage;
        $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
    }, true);

    $scope.$watch('pagingOptions.pageSize', function () {
        $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
    }, true);

    $scope.formatNumber = function(value){
        return utils.formatNumber(value,'0,0.00');
    };

    $scope.$watch('sortInfo', function () {

        $.each($scope.sortInfo.fields, function(index, value) {
            if(value != undefined){

                $scope.filterObject['sort-'+$scope.sortInfo.fields[index]] = $scope.sortInfo.directions[index];
            }
        });
        $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
    }, true);

    $scope.gridOptions = {
        data: 'myData',
        columnDefs:
            [
                { field: 'supplyingFacility', displayName: 'Supplying Facility', width: "150px;", resizable: false},
                { field: 'facility', displayName: 'Facility', width: "150px;", resizable: false},
                { field: 'code', displayName: 'Code', width: "100px;", resizable: false},
                { field: 'product', displayName: 'Product', width: "250px;" },
                { field: 'openingBalance', displayName: 'Opening Balance', width : "150px;", cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
                { field: 'receipts', displayName: 'Receipts', width : "130px;", cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
                { field: 'issues', displayName: 'Issues', width : "130px;", cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
                { field: 'adjustments', displayName: 'Adjustments', width : "130px;", cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
                { field: 'closingBalance', displayName: 'Closing Balance', width : "150px;",cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
                { field: 'monthsOfStock', displayName: 'MOS', width : "130px;", cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
                { field: 'averageMonthlyConsumption', displayName: 'AMC', width : "130px;", cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
                { field: 'maximumStock', displayName: 'Maximum Stock', width : "150px;", cellClass : 'pull-right',cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
                { field: 'reorderAmount', displayName: 'Re-order Amount', width : "150px;", cellClass : 'ngCellTextRight', cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'}

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
