function ListMailinglabelsController($scope, MailingLabels, ReportFacilityTypes,RequisitionGroups, $http, $routeParams,$location) {

        //to minimize and maximize the filter section
        var section = 1;

        $scope.section = function (id) {
            section = id;
        };

        $scope.show = function (id) {
            return section == id;
        };

        $scope.filterGrid = function (){
           $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
        };

        //filter form data section    facilityName
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
             facilityTypeId : $scope.facilityTypeId,
             facilityType : "",
             rgroupId : $scope.rgroupId,
             rgroup : ""
        };
        RequisitionGroups.get(function (data) {
            $scope.requisitionGroups = data.requisitionGroupList;
            $scope.requisitionGroups.unshift({'name':'-- All Requisition Groups --','id':'0'});
        });

        ReportFacilityTypes.get(function(data) {
            $scope.facilityTypes = data.facilityTypes;
            $scope.facilityTypes.unshift({'name':'-- All Facility Types --','id':'0'});
        });

        $scope.currentPage = ($routeParams.page) ? parseInt($routeParams.page) || 1 : 1;

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

    $scope.$watch('facilityTypeId', function(selection){
            if(selection != undefined || selection == ""){
                $scope.filterObject.facilityTypeId =  selection;
                $.each($scope.facilityTypes, function(idx,value){
                   if(selection == idx.id){
                       $scope.filterObject.facilityType = value.name;
                   }
                });
            }else{
                $scope.filterObject.facilityTypeId =  0;
            }
            $scope.filterGrid();
        });

    $scope.exportReport   = function (type){
        var url = '/reports/download/mailinglabels/' + type +'?facilityCodeFilter=' +  $scope.filterObject.facilityCodeFilter + '&facilityNameFilter=' +  $scope.filterObject.facilityNameFilter + '&facilityTypeId=' +  $scope.filterObject.facilityTypeId ;
        if(type == "mailing-list"){
            url = '/reports/download/mailinglabels/list/' + "pdf" +'?facilityCodeFilter=' +  $scope.filterObject.facilityCodeFilter + '&facilityNameFilter=' +  $scope.filterObject.facilityNameFilter + '&facilityTypeId=' +  $scope.filterObject.facilityTypeId ;
        }
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

        $scope.setPagingData = function(data, page, pageSize, total){

            $scope.myData = data; //pagedData;//
            $scope.pagingOptions.totalServerItems = total;//data.length;
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
                           // alert('filter object '+ index+' '+value);
                           params[index] = value;
                        });
                        MailingLabels.get(params, function(data) {
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

    $scope.gridOptions = {
        data: 'myData',
        columnDefs:
            [
                { field: 'code', displayName: 'Facility Code', width: "*", resizable: false},
                { field: 'facilityName', displayName: 'Facility Name', width: "**" },
                { field: 'facilityType', displayName: 'Facility Type', width: "*" },
                { field: 'region', displayName: 'Region', width : "*"},
                { field: 'address1;', displayName: 'Address 1', width : "**"},
                { field: 'contact', displayName: 'Contact', width : "*"},
                { field: 'owner', displayName: 'Operator', width : "*"},
                { field: 'phoneNumber', displayName: 'Phone', width : "*"},
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
        enableRowReordering: true,
        showFilter: true,
        autoFit :true,
        plugins: [new ngGridFlexibleHeightPlugin()]

    };

}
