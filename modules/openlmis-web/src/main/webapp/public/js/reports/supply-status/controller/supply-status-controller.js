function SupplyStatusController($scope, SupplyStatusReport, ReportSchedules, ReportPrograms , Periods , Products ,ReportFacilityTypes,GeographicZones, RequisitionGroups, $http, $routeParams,$location) {
    //to minimize and maximize the filter section
    var section = 1;

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
        periodId : $scope.period,
        period : "",
        zoneId : $scope.zone,
        productId : $scope.productId,
        scheduleId : $scope.schedule,
        rgroupId : $scope.rgroup,
        rgroup : "",
        facilityName : $scope.facilityNameFilter
    };

    ReportPrograms.get(function(data){
        $scope.programs = data.programs;
        $scope.programs.push({'name':'Select a Program'});
    });

    RequisitionGroups.get(function(data){
        $scope.requisitionGroups = data.requisitionGroupList;
        $scope.requisitionGroups.push({'name':'All Reporting Groups','id':'All'});
    });

    ReportFacilityTypes.get(function(data) {
        $scope.facilityTypes = data.facilityTypes;
        $scope.facilityTypes.push({'name': 'All Facility Types', 'id' : 'All'});
    });

    ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
        $scope.schedules.push({'name':'Select a Schedule', 'id':'All'});
    });

    Products.get(function(data){
        $scope.products = data.productList;
        $scope.products.push({'name': 'All Products','id':'All'});
    });

    $scope.ChangeSchedule = function(){
        Periods.get({ scheduleId : $scope.schedule },function(data) {
            $scope.periods = data.periods;
            $scope.periods.push({'name': 'Select Period', 'id':'All'});
        });
    }

    GeographicZones.get(function(data) {
        $scope.zones = data.zones;
        $scope.zones.push({'name': '- All Zones -', 'id' : 'All'});
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
        }else{
            $scope.filterObject.productId =  0;
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
        }else{
            $scope.filterObject.programId =  0;
        }
        $scope.filterGrid();
    });

    $scope.$watch('schedule', function(selection){
        if(selection != undefined || selection == ""){
            $scope.filterObject.scheduleId =  selection;
        }else{
            $scope.filterObject.scheduleId =  0;
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


    $scope.sortInfo = { fields:["code","facilityType"], directions: ["ASC"]};

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


        // put out the sort order
        $.each($scope.sortInfo.fields, function(index, value) {
            if(value != undefined) {
                params['sort-' + $scope.sortInfo.fields[index]] = $scope.sortInfo.directions[index];
            }
        });

        SupplyStatusReport.get(params, function(data) {
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

    $scope.$watch('sortInfo', function () {
        $.each($scope.sortInfo.fields, function(index, value) {
            if(value != undefined)
                $scope.filterObject[$scope.sortInfo.fields[index]] = $scope.sortInfo.directions[index];
        });
        $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
    }, true);

    $scope.formatNumber = function(arg){
        var numericObject = new Number(arg);
        return numericObject.format('0,0.00');
    }

    $scope.gridOptions = {
        data: 'myData',
        columnDefs:
            [
                { field: 'facility', displayName: 'Facility', width: "*", resizable: false},
                { field: 'code', displayName: 'Code', width: "*", resizable: false},
                { field: 'product', displayName: 'Product', width: "*" },
                { field: 'openingBalance', displayName: 'Opening Balance', width : "*", cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
                { field: 'receipts', displayName: 'Receipts', width : "*", cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
                { field: 'issues', displayName: 'Issues', width : "*", cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
                { field: 'adjustments', displayName: 'Adjustments', width : "*", cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
                { field: 'closingBalance', displayName: 'Closing Balance', width : "*",cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
                { field: 'monthsOfStock', displayName: 'Months of Stock', width : "*", cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
                { field: 'averageMonthlyConsumption', displayName: 'AMC', width : "*", cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
                { field: 'maximumStock', displayName: 'Maximum Stock', width : "*", cellClass : 'pull-right',cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'},
                { field: 'reorderAmount', displayName: 'Re-order Amount', width : "*", cellClass : 'ngCellTextRight', cellTemplate: '<div class="ngCellText" style="text-align:right;" ng-class="col.colIndex()"><span ng-cell-text>{{formatNumber(COL_FIELD)}}</span></div>'}

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
        showFilter: true//,
        //plugins: [new ngGridFlexibleHeightPlugin()]

    };


}
