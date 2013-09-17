function ListFacilitiesController($scope, FacilityList, ReportFacilityTypes, GeographicZones, RequisitionGroups, $http, $routeParams, $location) {

        //to minimize and maximize the filter section
        var section = 1;

        $scope.section = function (id) {
            section = id;
        };

        $scope.show = function (id) {
            return section == id;
        };

        $scope.filterGrid = function (){
            //forget the current page and go to the first page while filtering
            $scope.pagingOptions.currentPage = 1;
            $scope.getPagedDataAsync($scope.pagingOptions.pageSize, 1);//
        };

        //filter form data section
        $scope.filterOptions = {
            filterText: "",
            useExternalFilter: false
        };

        $scope.pagingOptions = {
            pageSizes: [5, 10, 20, 40, 50, 100],
            pageSize: 10,
            totalServerItems: 0,
            currentPage: 1
        };

        //filter form data section
        $scope.filterObject =  {
             facilityType : $scope.facilityType,
             zone : $scope.zone,
             rgroupId : $scope.rgroupId,
             rgroup : "",
             status : $scope.status
        };
        RequisitionGroups.get(function (data) {
            $scope.requisitionGroups = data.requisitionGroupList;
            $scope.requisitionGroups.unshift({'name':'-- All Requisition Groups --','id':'0'});
        });

         ReportFacilityTypes.get(function(data) {
            $scope.facilityTypes = data.facilityTypes;
            $scope.facilityTypes.unshift({'name': 'All Facility Types', id:'0'});
        });

        GeographicZones.get(function(data) {
            $scope.zones = data.zones;
            $scope.zones.unshift({'name': 'All Zones', id:'0'});
        });
           // [
           // ,
           // {'name': 'District Health Office', 'value': 3},
           // {'name': 'District', 'value': 2},
           // {'name': 'Province', 'value': 1}
        //];

        $scope.statuses = [
            {'name': 'All Statuses'},
            {'name': 'Active', 'value': "TRUE"},
            {'name': 'Inavtive', 'value': "FALSE"}
        ];

        $scope.currentPage = ($routeParams.page) ? parseInt($routeParams.page) || 1 : 1;

        $scope.exportReport   = function (type){
            var url = '/reports/download/facilities/' + type +'?zoneId=' +  $scope.filterObject.zoneId + '&facilityTypeId=' +  $scope.filterObject.facilityTypeId + '&status=' +  $scope.filterObject.statusId;
            window.open(url);
        }

        $scope.goToPage = function (page, event) {
            angular.element(event.target).parents(".dropdown").click();
            $location.search('page', page);
        };

        $scope.$watch('rgroupId', function (selection) {
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


                        params['zoneId'] = $scope.zone;
                        params['facilityTypeId'] = $scope.facilityType;
                        params['statusId'] = $scope.status;
                        params['rgroupId'] =$scope.filterObject.rgroupId;

                        $scope.data = [];

                        FacilityList.get(params, function(data) {
                            //if(data.pages != undefined){
                                $scope.data = data.pages.rows;
                                $scope.setPagingData(data.pages.rows,page,pageSize,data.pages.total);
                            //}

                        });
        };


        $scope.$watch('pagingOptions.currentPage', function () {
            if($scope.currentPage = $scope.pagingOptions.currentPage) {
                $scope.currentPage = $scope.pagingOptions.currentPage;
                $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
            }

        }, true);

        $scope.$watch('pagingOptions.pageSize', function () {
            if($scope.pageSize != $scope.pagingOptions.pageSize){
                $scope.pageSize = $scope.pagingOptions.pageSize;
                $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
            }

        }, true);
        
        $scope.$watch('sortInfo', function () {
           
            $.each($scope.sortInfo.fields, function(index, value) {
                if(value != undefined) {
                    
                    $scope.filterObject["facilityType"] = undefined;
                    $scope.filterObject["active"] = undefined;
                    $scope.filterObject["facilityName"] = undefined;
                    $scope.filterObject["code"] = undefined;
                    $scope.filterObject[$scope.sortInfo.fields[index]] = $scope.sortInfo.directions[index];
                    
                }
            });

            //$scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
        }, true);

    $scope.gridOptions = {
        data: 'myData',
     
        columnDefs:
            [
            { field: 'code', displayName: 'Facility Code', width: "*", resizable: false},
            { field: 'facilityName', displayName: 'Facility Name', width: "**" },
            { field: 'facilityType', displayName: 'Facility Type', width: "*" },
            { field: 'region', displayName: 'Zone', width : "*"},
            { field: 'contact', displayName: 'Contact', width : "*"},
            { field: 'phoneNumber', displayName: 'Phone', width : "*"},
            { field: 'owner', displayName: 'Operator', width : "*"},
            { field: 'active', displayName: 'Active', width : "*"}

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
        showFilter: true,
        autoFit :true,
        plugins: [new ngGridFlexibleHeightPlugin()]
      
    };
}
