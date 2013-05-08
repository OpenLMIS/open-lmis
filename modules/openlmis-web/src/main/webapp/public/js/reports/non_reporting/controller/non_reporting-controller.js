function NonReportingController($scope, RequisitionGroupsByProgramSchedule , RequisitionGroups, NonReportingFacilities, Schedules, FacilityTypes , Periods, Programs, $http, $routeParams,$location) {
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
            pageSizes: [5, 10, 20, 40, 50, 100],
            pageSize: 10,
            totalServerItems: 0,
            currentPage: 1
        };



        $scope.filterGrid = function (){
            $scope.$apply();
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
             facilityType : $scope.period,

        };

        Programs.get(function(data){
            $scope.programs = data.programs;
            $scope.programs.push({'name':'Select a Program'});
        })

        Schedules.get(function(data){
            $scope.schedules = data.schedules;
            $scope.schedules.push({'name':'Select a Schedule'});
        })

        FacilityTypes.get(function(data) {
            $scope.facilityTypes = data.facilityTypes;
            $scope.facilityTypes.push({'name': 'All Facility Types -'});
        });

        $scope.ChangeSchedule = function(){
            Periods.get({ scheduleId : $scope.schedule },function(data) {
                $scope.periods = data.periods;
                $scope.periods.push({'name': 'Select Period'});
            });

            RequisitionGroupsByProgramSchedule.get({program: $scope.program, schedule:$scope.schedule}, function(data){
                $scope.requisitionGroups = data.requisitionGroupList;
                $scope.requisitionGroups.push({'name':'All requsition groups'});
            });
        }



        $scope.currentPage = ($routeParams.page) ? parseInt($routeParams.page) || 1 : 1;

        $scope.export   = function (type){

            var param = $scope.getParams(1, 1);
            var paramString = jQuery.param(param);
            var url = '/reports/download/non_reporting/' + type + '?' + paramString;
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


        $scope.sortInfo = { fields:["code","facilityType"], directions: ["ASC"]};

        $scope.setPagingData = function(data, page, pageSize, total){
            $scope.myData = data;
            $scope.pagingOptions.totalServerItems = total;
            $scope.numberOfPages = ( Math.ceil( total / pageSize))  ? Math.ceil( total / pageSize) : 1 ;

            if (!$scope.$$phase) {
                $scope.$apply();
            }

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
            return params;
        }

        $scope.getPagedDataAsync = function (pageSize, page) {
                        var params = $scope.getParams(pageSize, page);
                        NonReportingFacilities.get(params, function(data) {
                            $scope.setPagingData(data.pages.rows[0].details,page,pageSize,data.pages.total);
                            $scope.summaries    =  data.pages.rows[0].summary;
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
