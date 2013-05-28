function SummaryReportController($scope, SummaryReport, ReportSchedules, ReportPrograms , Periods , $http, $routeParams,$location) {
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
            if (!$scope.$$phase) {
                $scope.$apply();
            }
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


        $scope.ChangeSchedule = function(){
            Periods.get({ scheduleId : $scope.schedule },function(data) {
                $scope.periods = data.periods;
                $scope.periods.push({'name': 'Select Period'});
            });
        }

        $scope.currentPage = ($routeParams.page) ? parseInt($routeParams.page) || 1 : 1;

        $scope.export   = function (type){
            var url = '/reports/download/summary/' + type +'?period=' + $scope.period + '&program=' + $scope.program;
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
                        params.period   = $scope.period;
                        params.program  = $scope.program;

                        // put out the sort order
                        $.each($scope.sortInfo.fields, function(index, value) {
                            if(value != undefined) {
                                params['sort-' + $scope.sortInfo.fields[index]] = $scope.sortInfo.directions[index];
                            }
                        });

                        SummaryReport.get(params, function(data) {
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

                { field: 'category', displayName: 'Category', width: "*" },
                { field: 'code', displayName: 'Code', width: "*", resizable: false},
                { field: 'product', displayName: 'Product', width: "***" },
                { field: 'unit', displayName: 'Unit', width : "*"},
                { field: 'openingBalance', displayName: 'B. Balance', width : "*"},
                { field: 'quantityReceived', displayName: 'Received', width : "*"},
                { field: 'actualDispensedQuantity', displayName: 'Dispensed', width : "*"},
                { field: 'balanceOnHand', displayName: 'Balance On Hand', width : "*"}
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
