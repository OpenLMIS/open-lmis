function AverageConsumptionReportController($scope, AverageConsumptionReport, Products , FacilityTypes, GeographicZones, $http, $routeParams,$location) {

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

        // default to the monthly period type
        $scope.periodType = 'monthly';

        $scope.periodTypes = [
            {'name':'Monthly', 'value':'monthly'},
            {'name':'Quarterly', 'value':'quarterly'},
            {'name':'Semi Anual', 'value':'semi-anual'},
            {'name':'Annual', 'value':'annual'}
       ];

       // TODO: clear out this hardcoded seciton and make sure that it is dynamic
        $scope.startYears   = [
            {'name':'2010','value':'2010'},
            {'name':'2011','value':'2011'},
            {'name':'2012','value':'2012'},
            {'name':'2013','value':'2013'},
            {'name':'2014','value':'2014'}
        ] ;

        $scope.endYears ;

       // $scope.startMonth = 1;

        // TODO: clear this hardcoded start year.
       // $scope.startYear = 2012

        $scope.startMonths  = [];

        // TODO: clear this hardcoded end year
       // $scope.endYear = 2013;

      //  $scope.endMonth = 4;

        $scope.endMonths;

        $scope.onStartMonthChanged = function(){
            $scope.endMonths.clear();
            if($scope.startYear == $scope.endYear && $scope.startMonth != '' ){

                for(var i=$scope.startMonth - 1; i < $scope.months.length;i++){
                    $scope.endMonths.push($scope.months[i]);
                }

            }
            $scope.endMonths = $scope.months;
        };;

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

        $scope.months       = [
            {'name':'Jan', 'value':'1'},
            {'name':'Feb', 'value':'2'},
            {'name':'Mar', 'value':'3'},
            {'name':'Apr', 'value':'4'},
            {'name':'May', 'value':'5'},
            {'name':'Jun', 'value':'6'},
            {'name':'Jul', 'value':'7'},
            {'name':'Aug', 'value':'8'},
            {'name':'Sep', 'value':'9'},
            {'name':'Oct', 'value':'10'},
            {'name':'Nov', 'value':'11'},
            {'name':'Dec', 'value':'12'}
        ];

        $scope.quarters         = [
            {'name':'One','value':'1'},
            {'name':'Two','value':'2'},
            {'name':'Three','value':'3'},
            {'name':'Four','value':'4'}
        ];

        $scope.product;


        // copy over the start month and end months
        // this is just for initial loading.
        $(function (){
            $scope.startMonths  = $scope.months;
            $scope.endMonths    = $scope.months;

            $scope.endYears     = $scope.startYears;
        });


        $scope.isMonthly = function(){
            return $scope.periodType == 'monthly';
        };

        $scope.isQuarterly = function(){
            return $scope.periodType == 'quarterly';
        };


        $scope.filterGrid = function (){
            $scope.$apply();
            $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
        };

        //filter form data section
        $scope.filterOptions = {
            filterText: "",
            useExternalFilter: false
        };



        //filter form data section
        $scope.filterObject =  {
             facilityType : $scope.facilityType,
             zone : $scope.zone,
             periodType: $scope.periodType,
             fromYear: $scope.fromYear,
             fromMonth: $scope.fromMonth,
             toYear: $scope.toYear,
             toMonth: $scope.toMonth,
             product: $scope.product,
             facility : $scope.facilityId

        };

        FacilityTypes.get(function(data) {
            $scope.facilityTypes = data.facilityTypes;
            $scope.facilityTypes.push({'name': '- Please Selct One -'});
        });

        Products.get(function(data){
            $scope.products = data.products;
        });

        $scope.facilities         = [
            {'name':'One','value':'1'},
            {'name':'Two','value':'2'},
            {'name':'Three','value':'3'},
            {'name':'Four','value':'4'}
        ];

        GeographicZones.get(function(data) {
            $scope.zones = data.zones;
            $scope.zones.push({'name': '- Please Selct One -'});
        });

        $scope.currentPage = ($routeParams.page) ? parseInt($routeParams.page) || 1 : 1;

        $scope.$watch('zone.value', function(selection){
            if(selection != undefined || selection == ""){
               $scope.filterObject.zoneId =  selection;
               //$scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
            }else{
                $scope.filterObject.zoneId = 0;
            }
        });

        $scope.$watch('status.value', function(selection){
            if(selection != undefined || selection == ""){
                $scope.filterObject.statusId =  selection;
                //$scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
            }else{
                $scope.filterObject.statusId ='';
            }
        });
        $scope.$watch('facilityType.value', function(selection){
            if(selection != undefined || selection == ""){
                $scope.filterObject.facilityTypeId =  selection;
                //$scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
            }else{
                $scope.filterObject.facilityTypeId =  0;
            }
        });

        $scope.$watch('startYear', function(selection){
        if(selection != undefined || selection == ""){
            $scope.filterObject.fromYear =  selection;
        }else{
            var date = new Date();
            $scope.filterObject.fromYear =  date.getYear();
        }
        });

        $scope.$watch('endYear', function(selection){
            if(selection != undefined || selection == ""){
                $scope.filterObject.toYear =  selection;
            }else{
                var date = new Date();
                $scope.filterObject.toYear =  date.getYear();
            }
        });

        $scope.$watch('startMonth', function(selection){
            if(selection != undefined || selection == ""){
                $scope.filterObject.fromMonth =  selection;
            }else{
                var date = new Date();
                $scope.filterObject.fromMonth =  date.getMonth();
            }
        });

        $scope.$watch('endMonth', function(selection){
            if(selection != undefined || selection == ""){
                $scope.filterObject.toMonth =  selection;
            }else{
                var date = new Date();
                $scope.filterObject.toMonth =  date.getMonth();;
            }
        });



        $scope.$watch('periodType', function(selection){
            if(selection != undefined || selection == ""){
                $scope.filterObject.periodType =  selection;
            }else{
                $scope.filterObject.periodType =  "monthly";
            }
        });



        $scope.export   = function (type){
            var url = '/reports/download/averageconsumption/' + type +'?zone=' + $scope.zone + '&facilityType=' + $scope.facilityType;
            window.location.href = url;
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

            if (!$scope.$$phase) {
                $scope.$apply();
            }

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
                            if(value != undefined)
                                params[index] = value;
                        });

                        AverageConsumptionReport.get(params, function(data) {
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

    $scope.gridOptions = {
        data: 'myData',
        columnDefs:
            [
                //{ field: 'period', displayName: 'Period', width: "*", resizable: false},

                { field: 'category', displayName: 'Product', width: "*" },
                { field: 'product', displayName: 'Product Description', width: "*" },
               // { field: 'facilityType', displayName: 'Facility Type', width : "*"},
               // { field: 'facility', displayName: 'Facility', width : "*"},
                //{ field: 'supplier', displayName: 'Supplying Facility', width : "*"},
                //{ field: 'reportingGroup', displayName: 'Reporting Group', width : "*"},
                { field: 'average', displayName: 'Average Monthly Consumption', width : "*"}
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
