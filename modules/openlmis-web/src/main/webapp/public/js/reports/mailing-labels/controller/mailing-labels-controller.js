function ListMailinglabelsController($scope,$filter, ngTableParams, MailingLabels, ReportFacilityTypes,RequisitionGroups, $http, $routeParams,$location) {

    $scope.filterGrid = function (){
       $scope.getPagedDataAsync(0, 0);
    };

    RequisitionGroups.get(function (data) {
        $scope.requisitionGroups = data.requisitionGroupList;
        $scope.requisitionGroups.unshift({'name':'-- All Requisition Groups --','id':'0'});
    });

    ReportFacilityTypes.get(function(data) {
        $scope.facilityTypes = data.facilityTypes;
        $scope.facilityTypes.unshift({'name':'-- All Facility Types --','id':'0'});
    });


    $scope.exportReport   = function (type){
        var url = '/reports/download/mailinglabels/' + type +'?facilityCodeFilter=' +  $scope.filterObject.facilityCodeFilter + '&facilityNameFilter=' +  $scope.filterObject.facilityNameFilter + '&facilityTypeId=' +  $scope.filterObject.facilityTypeId ;
        if(type == "mailing-list"){
            url = '/reports/download/mailinglabels/list/' + "pdf" +'?facilityCodeFilter=' +  $scope.filterObject.facilityCodeFilter + '&facilityNameFilter=' +  $scope.filterObject.facilityNameFilter + '&facilityTypeId=' +  $scope.filterObject.facilityTypeId ;
        }
        window.open(url);
    }



    // the grid options
    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        total: 0,           // length of data
        count: 25           // count per page
    });

    $scope.paramsChanged = function(params) {

        // slice array data on pages
        if($scope.data == undefined ){
            $scope.datarows = [];
            params.total = 0;
        }else{
            var data = $scope.data;
            var orderedData = params.filter ? $filter('filter')(data, params.filter) : data;
            orderedData = params.sorting ?  $filter('orderBy')(orderedData, params.orderBy()) : data;

            params.total = orderedData.length;
            $scope.datarows = orderedData.slice( (params.page - 1) * params.count,  params.page * params.count );
            var i = 0;
            var baseIndex = params.count * (params.page - 1) + 1;
            while(i < $scope.datarows.length){
                $scope.datarows[i].no = baseIndex + i;
                i++;
            }
        }
    };

    // watch for changes of parameters
    $scope.$watch('tableParams', $scope.paramsChanged , true);

    $scope.getPagedDataAsync = function (pageSize, page) {
        pageSize = 10000;
        page = 1;
        var params  = {};
        if(pageSize != undefined && page != undefined ){
                var params =  {
                                "max" : pageSize,
                                "page" : page
                               };
        }
        //filter form data section
        $scope.filterObject = {
            facilityTypeId : $scope.facilityTypeId,
            rgroupId :  $scope.rgroupId
        }

        // copy the filters over
        $.each($scope.filterObject, function(index, value) {
           params[index] = value;
        });
        // go
        MailingLabels.get(params, function(data) {
            $scope.data = data.pages.rows ;
            $scope.paramsChanged($scope.tableParams);
        });
    };

    $scope.filterGrid();
}
