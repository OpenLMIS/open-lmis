function ListMailinglabelsController($scope, MailingLabels, FacilityTypes, $http, $routeParams,$location) {

        //to minimize and maximize the filter section
        var section = 1;

        $scope.section = function (id) {
            section = id;
        };

        $scope.show = function (id) {
            return section == id;
        };

        $scope.filterGrid = function (){
            $scope.$apply();
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
             facilityType : $scope.facilityType,
             facilityNameFilter : $scope.facilityNameFilter,
             facilityCodeFilter : $scope.facilityCodeFilter
        };

        FacilityTypes.get(function(data) {
            $scope.facilityTypes = data.facilityTypes;
            $scope.facilityTypes.push({'name': '- Please Select One -'});
        });

        $scope.currentPage = ($routeParams.page) ? parseInt($routeParams.page) || 1 : 1;

        $scope.$watch('facilityNameFilter', function(selection){
            if(selection != undefined || selection == ""){
               $scope.filterObject.facilityNameFilter =  selection;

            }else{
                $scope.filterObject.facilityNameFilter = "";
            }
        });
        $scope.$watch('facilityCodeFilter', function(selection){
            if(selection != undefined || selection == ""){
                $scope.filterObject.facilityCodeFilter =  selection;

            }else{
                $scope.filterObject.facilityCodeFilter = "";
            }
        });

        $scope.$watch('facilityType.value', function(selection){
            if(selection != undefined || selection == ""){
                $scope.filterObject.facilityTypeId =  selection;

            }else{
                $scope.filterObject.facilityTypeId =  0;
            }
        });

    $scope.export   = function (type){
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

        $scope.setPagingData = function(data, page, pageSize, total){

            $scope.myData = data; //pagedData;//
            $scope.pagingOptions.totalServerItems = total;//data.length;
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

        $scope.$watch('sortInfo', function () {

            //add sorting infro to the filter object
            $.each($scope.sortInfo.fields, function(index, value) {
                if(value != undefined) {
                    //only sort by one of the fields
                    $scope.filterObject["facilityType"] = undefined;
                    $scope.filterObject["active"] = undefined;
                    $scope.filterObject["facilityName"] = undefined;
                    $scope.filterObject["code"] = undefined;
                    $scope.filterObject[$scope.sortInfo.fields[index]] = $scope.sortInfo.directions[index];

                }
            });
            $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
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
