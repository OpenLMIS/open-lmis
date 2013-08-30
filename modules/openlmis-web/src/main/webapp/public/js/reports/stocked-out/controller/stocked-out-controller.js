function StockedOutController($scope, StockedOutReport, AllReportPeriods, Products, ProductCategories, ProductsByCategory, ReportFacilityTypes, RequisitionGroups, OperationYears, Months, $http, $routeParams, $location) {
    //to minimize and maximize the filter section
    var section = 1;
    $scope.showMessage = true;
    $scope.message = "Indicates a required field."

    $scope.defaultFlag = true;
    $scope.reporting = "quarterly";

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

    $scope.section = function (id) {
        section = id;
    };

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


    // default to the monthly period type
    //$scope.periodType = 'monthly';


    $scope.periodTypes = [
        {'name': 'Monthly', 'value': 'monthly'},
        {'name': 'Quarterly', 'value': 'quarterly'},
        {'name': 'Semi Anual', 'value': 'semi-anual'},
        {'name': 'Annual', 'value': 'annual'}
    ];
    $scope.startYears = [];
    OperationYears.get(function (data) {
        $scope.startYears = data.years;
        adjustEndYears();
    });

    Months.get(function (data) {
        var months = data.months;

        if (months != null) {
            $scope.startMonths = [];
            $scope.endMonths = [];
            $.each(months, function (idx, obj) {
                $scope.startMonths.push({'name': obj.toString(), 'value': idx + 1});
                $scope.endMonths.push({'name': obj.toString(), 'value': idx + 1});
            });
            //$scope.startMonth   = prev('M');
            //alert(JSON.stringify(months, null, 4));
        }

    });

    $scope.startQuarters = function () {
        return $scope.quarters;
    };

    $scope.endQuarters = function () {
        if ($scope.startYear == $scope.endYear && $scope.startQuarter != '') {
            var arr = [];
            for (var i = $scope.startQuarter - 1; i < $scope.quarters.length; i++) {
                arr.push($scope.quarters[i]);
            }
            return arr;
        }
        return $scope.quarters;
    };

    $scope.quarters = [
        {'name': 'One', 'value': '1'},
        {'name': 'Two', 'value': '2'},
        {'name': 'Three', 'value': '3'},
        {'name': 'Four', 'value': '4'}
    ];

    $scope.semiAnnuals = [
        {'name': 'First Half', 'value': '1'},
        {'name': 'Second Half', 'value': '2'}
    ];

    // copy over the start month and end months
    // this is just for initial loading.
    $(function () {
        $scope.startQuarters = $scope.quarters;
        $scope.endQuarters = $scope.quarters;
        $scope.endYears = $scope.startYears;
        $scope.startSemiAnnuals = $scope.semiAnnuals;
        $scope.endSemiAnnuals = $scope.semiAnnuals;
        $scope.toQuarter = 1;
        $scope.fromQuarter = 1;
        $scope.startHalf = 1;
        $scope.endHalf = 1;

        //alert('function');
    });

    $scope.isMonthly = function () {
        return $scope.periodType == 'monthly';
    };

    $scope.isQuarterly = function () {
        return $scope.periodType == 'quarterly';
    };

    $scope.isSemiAnnualy = function () {
        return $scope.periodType == 'semi-anual';
    };


    RequisitionGroups.get(function (data) {
        $scope.requisitionGroups = data.requisitionGroupList;
        //$scope.requisitionGroups.push({'name':'All Reporting Groups','id':'All'});
    });

    ReportFacilityTypes.get(function (data) {
        $scope.facilityTypes = data.facilityTypes;
        //$scope.facilityTypes.push({'name': 'All Facility Types', 'id' : 'All'});
    });

    Products.get(function (data) {
        $scope.products = data.productList;
        //alert(JSON.stringify( $scope.products, null, 4));
        //$scope.products.push({'name': 'All Products','id':'All','tracer': 'All'});

    });

    ProductCategories.get(function (data) {
        $scope.productCategories = data.productCategoryList;
        //$scope.productCategories.push({'name': 'All Product Categories', 'id' : 'All'});
    });


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
        } else {
            $scope.filterObject.productCategoryId = 0;
        }
        $scope.ChangeProductList();
        $scope.filterGrid();
    });

    $scope.ChangeProductList = function () {
        ProductsByCategory.get({category: $scope.filterObject.productCategoryId}, function (data) {
            $scope.products = data.productList;
            //$scope.products.push({'name': 'All Products','id':'All','tracer': 'All'});
        });
    }

    $scope.$watch('product', function (selection) {
        if (selection == "All") {
            $scope.filterObject.productId = -1;
        } else if (selection != undefined || selection == "") {
            $scope.filterObject.productId = selection;
        } else {
            $scope.filterObject.productId = 0;
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

    $scope.$watch('startYear', function (selection) {
        var date = new Date();
        //alert(selection);
        if (selection != undefined || selection == "") {
            $scope.filterObject.fromYear = selection;
            adjustEndYears();
            adjustEndMonths();
            adjustEndQuarters();
            adjustEndSemiAnnuals();
        } else {
            $scope.startYear = date.getFullYear().toString();
            $scope.filterObject.fromYear = date.getFullYear();
        }
        //$scope.startYear=prev('Y');
        //checkrequired();
        //alert('startYear');
        $scope.filterGrid();
    });

    $scope.$watch('endYear', function (selection) {
        var date = new Date();
        if (selection != undefined || selection == "") {
            $scope.filterObject.toYear = selection;
            adjustEndMonths();
            adjustEndQuarters();
            adjustEndSemiAnnuals();
        } else {
            $scope.endYear = date.getFullYear().toString();
            $scope.filterObject.toYear = date.getFullYear();
        }
        //alert('endYear');
        $scope.filterGrid();
    });

    $scope.$watch('startQuarter', function (selection) {
        var date = new Date();
        if (selection != undefined || selection == "") {
            $scope.filterObject.fromQuarter = selection;
            adjustEndQuarters();
        } else {
            var date = new Date();
            $scope.filterObject.fromQuarter = 1;
        }
        //alert('startQurater');
        $scope.filterGrid();
    });

    $scope.$watch('endQuarter', function (selection) {
        var date = new Date();
        if (selection != undefined || selection == "") {
            $scope.filterObject.toQuarter = selection;
        } else {
            var date = new Date();
            $scope.filterObject.toQuarter = $scope.filterObject.fromQuarter;
        }
        //alert('endQurater');
        $scope.filterGrid();
    });

    $scope.$watch('startHalf', function (selection) {

        if (selection != undefined || selection == "") {
            $scope.filterObject.fromSemiAnnual = selection;
            adjustEndSemiAnnuals();
        } else {
            $scope.filterObject.fromSemiAnnual = 1;
        }
        //startHalf');
        $scope.filterGrid();
    });
    $scope.$watch('endHalf', function (selection) {

        if (selection != undefined || selection == "") {
            $scope.filterObject.toSemiAnnual = selection;
        } else {
            var date = new Date();
            $scope.filterObject.toSemiAnnual = 1;
        }
        //alert('endHalf');
        $scope.filterGrid();
    });
    $scope.$watch('startMonth', function (selection) {
        var date = new Date();
        if (selection != undefined || selection == "") {
            $scope.filterObject.fromMonth = selection - 1;
            adjustEndMonths();
        } else {
            $scope.startMonth = (date.getMonth() + 1 ).toString();
            $scope.filterObject.fromMonth = (date.getMonth() + 1);
        }
        //alert('startMonth');
        $scope.filterGrid();
    });

    $scope.$watch('endMonth', function (selection) {
        var date = new Date();
        if (selection != undefined || selection == "") {
            $scope.filterObject.toMonth = selection - 1;
        } else {
            $scope.endMonth = (date.getMonth() + 1 ).toString();
            $scope.filterObject.toMonth = (date.getMonth() + 1);
        }
        //alert('endMonth');
        $scope.filterGrid();
    });

    var adjustEndMonths = function () {
        if ($scope.startMonth != undefined && $scope.startMonths != undefined && $scope.startYear == $scope.endYear) {
            $scope.endMonths = [];
            $.each($scope.startMonths, function (idx, obj) {
                if (obj.value >= $scope.startMonth) {
                    $scope.endMonths.push({'name': obj.name, 'value': obj.value});
                }
            });
            if ($scope.endMonth < $scope.startMonth) {
                $scope.endMonth = $scope.startMonth;
            }
        } else {
            $scope.endMonths = $scope.startMonths;
        }
    }

    var adjustEndQuarters = function () {
        if ($scope.startYear == $scope.endYear) {
            $scope.endQuarters = [];
            $.each($scope.startQuarters, function (idx, obj) {
                if (obj.value >= $scope.startQuarter) {
                    $scope.endQuarters.push({'name': obj.name, 'value': obj.value});
                }
            });
            if ($scope.endQuarter < $scope.startQuarter) {
                $scope.endQuarter = $scope.startQuarter;
            }
        } else {
            $scope.endQuarters = $scope.startQuarters;
        }
    }

    var adjustEndSemiAnnuals = function () {

        if ($scope.startYear == $scope.endYear) {
            $scope.endSemiAnnuals = [];
            $.each($scope.startSemiAnnuals, function (idx, obj) {
                if (obj.value >= $scope.startHalf) {
                    $scope.endSemiAnnuals.push({'name': obj.name, 'value': obj.value});
                }
            });
            if ($scope.endHalf < $scope.startHalf) {
                $scope.endHalf = $scope.startHalf;
            }
        } else {
            $scope.endSemiAnnuals = $scope.startSemiAnnuals;
        }
    }

    var adjustEndYears = function () {
        $scope.endYears = [];
        $.each($scope.startYears, function (idx, obj) {
            if (obj >= $scope.startYear) {
                $scope.endYears.push(obj);
            }
        });
        if ($scope.endYear < $scope.startYear) {
            $scope.endYear = new Date().getFullYear();
        }
    }


    $scope.$watch('periodType', function (selection) {
        if (selection != undefined || selection == "") {
            $scope.filterObject.periodType = selection;

        } else {
            $scope.filterObject.periodType = "monthly";
        }
        //alert('PeriodType');
        $scope.filterGrid();
    });


    $scope.currentPage = ($routeParams.page) ? parseInt($routeParams.page) || 1 : 1;

    $scope.exportReport = function (type) {
        //$scope.message ="";
        //if ($scope.message = "") {
        $scope.filterObject.pdformat = 1;
        var params = jQuery.param($scope.filterObject);
        var url = '/reports/download/stocked_out/' + type + '?' + params;
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
        periodType: $scope.periodType,
        fromYear: $scope.fromYear,
        fromMonth: $scope.fromMonth,
        fromQuarter: $scope.fromQuarter,
        fromSemiAnnual: $scope.startHalf,
        toYear: $scope.toYear,
        toMonth: $scope.toMonth,
        toQuarter: $scope.toQuarter,
        toSemiAnnual: $scope.endHalf,
        productId: $scope.productId,
        productCategoryId: $scope.productCategoryId,
        rgroupId: $scope.rgroup,
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


        //alert(JSON.stringify(params))
        StockedOutReport.get(params, function (data) {
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

    $scope.sortInfo = { fields:["supplyingfacility","facility","product"], directions: ["ASC","ASC","ASC"]};

    // put out the sort order
    $.each($scope.sortInfo.fields, function (index, value) {
        if (value != undefined) {
            $scope.filterObject['sort-'+$scope.sortInfo.fields[index]] = $scope.sortInfo.directions[index];
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
        columnDefs:
            [
                { field: 'supplyingFacility', displayName: 'Supplying Facility', width: "180px;"},
                { field: 'facilitycode', displayName: 'Code', width: "*" },
                { field: 'facility', displayName: 'Facility Name', width: "*", resizable: false},
                { field: 'facilitytypename', displayName: 'Facility Type', width: "*", resizable: false},
                { field: 'location', displayName: 'Location', width: "*" },
                { field: 'product', displayName: 'Product Stocked Out', width: "*" }
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


    var checkrequired = function () {
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

    }

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
