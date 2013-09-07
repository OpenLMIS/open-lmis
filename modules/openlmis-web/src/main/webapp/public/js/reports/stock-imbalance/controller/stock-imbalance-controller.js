function StockImbalanceController($scope, StockImbalanceReport,RequisitionGroupsByProgramSchedule, AllReportPeriods,ReportPeriodsByScheduleAndYear, Products, ProductCategories, ProductsByCategory, ReportFacilityTypes, RequisitionGroups,ReportSchedules,ReportPrograms,ReportPeriods, OperationYears, SettingsByKey, $http, $routeParams, $location) {
    //to minimize and maximize the filter section
    var section = 1;
    $scope.showMessage = true;
    $scope.message = "Indicates a required field."

    $scope.defaultFlag = true;
   // $scope.reporting = "quarterly";
    $scope.IndicatorProductsKey = "INDICATOR_PRODUCTS";

    SettingsByKey.get({key: $scope.IndicatorProductsKey},function (data){
         $scope.IndicatorProductsDescription = data.settings.value;
    });

    AllReportPeriods.get(function (data) {
        $scope.periods = data.periods;
        /*var startdt = parseJsonDate('/Date(' + $scope.periods[1].startdate + ')/');
        var enddt = parseJsonDate('/Date(' + $scope.periods[1].enddate + ')/');
        var diff = enddt.getMonth() - startdt.getMonth() + 1;

        if (diff == 3) {
            $scope.reporting = "quarterly";
        } else {
            $scope.reporting = "monthly";
        }*/

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

    $scope.filterGrid = function () {
        $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
    };

    //filter form data section
    $scope.filterOptions = {
        period: $scope.period,
        filterText: "",
        useExternalFilter: false
    };

   $scope.startYears = [];
    OperationYears.get(function (data) {
        $scope.startYears = data.years;
    });


    ReportPrograms.get(function(data){
        $scope.programs = data.programs;
    })

    ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
    });

    RequisitionGroups.get(function (data) {
        $scope.requisitionGroups = data.requisitionGroupList;
    });

    ReportFacilityTypes.get(function (data) {
        $scope.facilityTypes = data.facilityTypes;
    });

    Products.get(function (data) {
        $scope.products = data.productList;
        $scope.products.unshift({'name': '-- All Products --', 'id':'All'});
        var ind_prod = $scope.IndicatorProductsDescription;
        $scope.products.unshift({'name': '-- '.concat(ind_prod).concat(' --'), 'id':'-1'});

    });

    ProductCategories.get(function (data) {
        $scope.productCategories = data.productCategoryList;
    });

    $scope.ChangeSchedule = function(scheduleBy){
        if(scheduleBy == 'byYear'){
            ReportPeriodsByScheduleAndYear.get({scheduleId: $scope.schedule, year: $scope.year}, function(data){
                $scope.periods = data.periods;
            });

        }else{

            ReportPeriods.get({ scheduleId : $scope.schedule },function(data) {
                $scope.periods = data.periods;
            });

        }

        RequisitionGroupsByProgramSchedule.get({program: $scope.program, schedule:$scope.schedule}, function(data){
            $scope.requisitionGroups = data.requisitionGroupList;
        });
    }


    $scope.$watch('facilityType', function (selection) {
        if (selection == "All") {
            $scope.filterObject.facilityTypeId = -1;
        } else if (selection != undefined || selection == "") {
            $scope.filterObject.facilityTypeId = selection;
            $.each($scope.facilityTypes, function (item, idx) {
                if (idx.id == selection) {
                    $scope.filterObject.facilityType = idx.name;
                }
            });
        } else {
            $scope.filterObject.facilityTypeId = 0;
        }
        $scope.filterGrid();

    });

    $scope.$watch('facilityNameFilter', function (selection) {
        if (selection != undefined || selection == "") {
            $scope.filterObject.facilityName = selection;

        } else {
            $scope.filterObject.facilityName = "";
        }
        $scope.filterGrid();
    });

    $scope.$watch('productCategory', function (selection) {
        if (selection == "All") {
            $scope.filterObject.productCategoryId = -1;
        } else if (selection != undefined || selection == "") {
            $scope.filterObject.productCategoryId = selection;
            $.each($scope.productCategories, function(item, idx){
                if(idx.id == selection){
                    $scope.filterObject.productCategory = idx.name;
                }
            })
        } else {
            $scope.filterObject.productCategoryId = 0;
        }
        $scope.ChangeProductList();
        $scope.filterGrid();
    });

    $scope.ChangeProductList = function () {
        ProductsByCategory.get({category: $scope.filterObject.productCategoryId}, function (data) {
            $scope.products = data.productList;
            $scope.products.unshift({'name': '-- All Products --','id':'All'});
            var ind_prod = $scope.IndicatorProductsDescription;
            $scope.products.unshift({'name': '-- '.concat(ind_prod).concat(' --'), 'id':'-1'});
        });
    }

    $scope.$watch('product', function (selection) {
        if (selection == "All") {
            $scope.filterObject.productId = 0;
        } else if (selection != undefined || selection == "") {
            $scope.filterObject.productId = selection;
            $.each($scope.products, function(item, idx){
                if(idx.id == selection){
                    $scope.filterObject.product = idx.name;
                }
            });

        } else {
            $scope.filterObject.productId = -1;
        }
        $scope.filterGrid();
    });

    $scope.$watch('rgroup', function (selection) {
        if (selection == "All") {
            $scope.filterObject.rgroupId = -1;
        } else if (selection != undefined || selection == "") {
            $scope.filterObject.rgroupId = selection;
            $.each($scope.requisitionGroups, function (item, idx) {
                if (idx.id == selection) {
                    $scope.filterObject.rgroup = idx.name;
                }
            });
        } else {
            $scope.filterObject.rgroupId = 0;
        }
        $scope.filterGrid();
    });

    $scope.$watch('program', function (selection) {
        if (selection == "All") {
            $scope.filterObject.programId = -1;
        } else if (selection != undefined || selection == "") {
            $scope.filterObject.programId = selection;
            $.each($scope.programs, function (item, idx) {
                if (idx.id == selection) {
                    $scope.filterObject.program = idx.name;
                }
            });

        } else {
            $scope.filterObject.programId = 0;
        }
        $scope.filterGrid();
    });

    $scope.$watch('period', function (selection) {
        if (selection == "All") {
            $scope.filterObject.periodId = -1;
        } else if (selection != undefined || selection == "") {
            $scope.filterObject.periodId = selection;
            $.each($scope.periods, function (item, idx) {
                if (idx.id == selection) {
                    $scope.filterObject.period = idx.name;
                }
            });

        } else {
            $scope.filterObject.periodId = 0;
        }

        $scope.filterGrid();
    });

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

    $scope.$watch('year', function (selection) {
        if (selection == "All") {
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

    $scope.exportReport = function (type) {
        //$scope.message ="";
        //if ($scope.message = "") {
        $scope.filterObject.pdformat = 1;
        var params = jQuery.param($scope.filterObject);
        var url = '/reports/download/stock_imbalance/' + type + '?' + params;
        window.open(url);
        //}
    }

    $scope.goToPage = function (page, event) {
        angular.element(event.target).parents(".dropdown").click();
        $location.search('page', page);
    };

    $scope.$watch("currentPage", function () {  //good watch no problem

        if ($scope.currentPage != undefined && $scope.currentPage != 1) {
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

    $scope.setPagingData = function (data, page, pageSize, total) {
        $scope.myData = data;
        $scope.pagingOptions.totalServerItems = total;
        $scope.numberOfPages = ( Math.ceil(total / pageSize)) ? Math.ceil(total / pageSize) : 1;

    };


    //filter form data section
    $scope.filterObject = {
        facilityTypeId: $scope.facilityType,
        facilityType: "",
        periodId : $scope.period,
        period : "",
        programId: $scope.program,
        program: "",
        scheduleId: $scope.schedule,
        schedule: "",
        productId: $scope.productId,
        product : "",
        productCategoryId: $scope.productCategoryId,
        productCategory : "",
        rgroupId: $scope.rgroup,
        year : $scope.year,
        rgroup: "",
        facility: $scope.facilityNameFilter
    };

    $scope.getPagedDataAsync = function (pageSize, page) {
        var params = {};
        //alert('xxx');
        if (pageSize != undefined && page != undefined) {
            var params = {
                "max": pageSize,
                "page": page
            };
        }

        $.each($scope.filterObject, function (index, value) {
            //if(value != undefined)
            params[index] = value;
        });

        StockImbalanceReport.get(params, function (data) {
            $scope.setPagingData(data.pages.rows, page, pageSize, data.pages.total);
        });

    };

    $scope.$watch('pagingOptions.currentPage', function () {
        $scope.currentPage = $scope.pagingOptions.currentPage;
        $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
    }, true);

    $scope.$watch('pagingOptions.pageSize', function () {
        $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
    }, true);

    $scope.sortInfo = { fields: ["supplyingfacility", "facility","product"], directions: ["ASC","ASC","ASC"]};
    // put default sort criteria
    $.each($scope.sortInfo.fields, function(index, value) {
        if(value != undefined) {
            $scope.filterObject['sort-' + $scope.sortInfo.fields[index]] = $scope.sortInfo.directions[index];
        }
    });
    $scope.$watch('sortInfo', function () {
        $.each($scope.sortInfo.fields, function (index, value) {
            if (value != undefined)
                $scope.filterObject['sort-'+$scope.sortInfo.fields[index]] = $scope.sortInfo.directions[index];
        });
        $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
    }, true);

    $scope.formatNumber = function (value, format) {
        return utils.formatNumber(value, format);
    }


    $scope.gridOptions = {
        data: 'myData',
        columnDefs: [
        { field: 'supplyingFacility', displayName: 'Supplying Facility', width: "180px;"},
            { field: 'facility', displayName: 'Facility', width: "150px;", resizable: false},
            { field: 'product', displayName: 'Product', width: "300px;" },
            { field: 'physicalCount', displayName: 'Physical Count', width: "150px;", cellTemplate: '<div class="ngCellText" style="text-align:right; padding-right: 5px;" ng-class="col.colIndex()"><span ng-cell-text style="margin-right: 45px;">{{formatNumber(COL_FIELD,"0,000")}}</span></div>'},
            { field: 'amc', displayName: 'AMC', width: "90px;", cellTemplate: '<div class="ngCellText" style="text-align:right; padding-right: 5px;" ng-class="col.colIndex()"><span ng-cell-text style="margin-right: 45px;">{{formatNumber(COL_FIELD,"0,000")}}</span></div>'},
            { field: 'months', displayName: 'MOS', width: "90px;", cellTemplate: '<div class="ngCellText" style="text-align:right; padding-right: 5px;" ng-class="col.colIndex()"><span ng-cell-text style="margin-right: 45px;">{{formatNumber(COL_FIELD,"0,000")}}</span></div>'},
            { field: 'orderQuantity', displayName: 'Order Quantity', width: "150px;", cellTemplate: '<div class="ngCellText" style="text-align:right; padding-right: 5px;" ng-class="col.colIndex()"><span ng-cell-text style="margin-right: 45px;">{{formatNumber(COL_FIELD,"0,000")}}</span></div>'},
            { field: 'status', displayName: 'Status', width: "150px;"}
        ],
        enablePaging: true,
        enableSorting: true,
        showFooter: true,
        selectWithCheckboxOnly: false,
        pagingOptions: $scope.pagingOptions,
        filterOptions: $scope.filterOptions,
        useExternalSorting: true,
        sortInfo: $scope.sortInfo,
        showColumnMenu: true,
        enableRowReordering: true,
        showFilter: true,
        plugins: [new ngGridFlexibleHeightPlugin()]

    };

  /*  var checkrequired = function () {
        var reqMsg = "Please fill the required fields."
        var check = "x";

        if (check != "") {
            check = document.getElementById('periodType').value;
            $scope.message = check == "" ? reqMsg : "";
        }

        if (check != "") {
            check = document.getElementById('startYear').value;
            //alert('Year' + check);
            $scope.message = check == "" ? reqMsg : "";
        }

        if (check != "") {
            //next required
            if ($scope.reporting == "monthly") {
                check = document.getElementById('startMonth').value;
            } else {
                check = document.getElementById('startQuarter').value;
            }
            //alert('Month' + check);
            $scope.message = check == "" ? reqMsg : "";
        }

        if (check != "") {
            check = document.getElementById('product').value;
            //alert('Product' + check);
            $scope.message = check == "" ? reqMsg : "";
        }

    }*/

    /*function parseJsonDate(jsonDate) {
        var offset = new Date().getTimezoneOffset() * 60000;
        var parts = /\/Date\((-?\d+)([+-]\d{2})?(\d{2})?.*//*.exec(jsonDate);
        if (parts[2] == undefined) parts[2] = 0;
        if (parts[3] == undefined) parts[3] = 0;
        return new Date(+parts[1] + offset + parts[2] * 3600000 + parts[3] * 60000);
    };*/


    /*var init = function () {

        $scope.periodType = $scope.defaultSettings('P');

        if ($scope.periodType == 'quarterly') {
            $scope.startQuarter = $scope.defaultSettings('Q');
        } else {
            $scope.startMonth = $scope.defaultSettings('M');
        }
        $scope.startYear = $scope.defaultSettings('Y');
    };
    init();*/

}
