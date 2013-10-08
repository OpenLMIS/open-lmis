function ListFacilitiesController($scope,$filter,ngTableParams, FacilityList, ReportFacilityTypes, GeographicZones, RequisitionGroups, $http, $routeParams, $location) {


        $scope.filterGrid = function (){
            $scope.getPagedDataAsync(0, 0);//
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
            $scope.facilityTypes.unshift({'name': '-- All Facility Types --', id:'0'});
        });

        GeographicZones.get(function(data) {
            $scope.zones = data.zones;
            $scope.zones.unshift({'name': '-- All Zones --', id:'0'});
        });


        $scope.statuses = [
            {'name': 'All Statuses'},
            {'name': 'Active', 'value': "TRUE"},
            {'name': 'Inactive', 'value': "FALSE"}
        ];


        $scope.exportReport   = function (type){
            var url = '/reports/download/facilities/' + type +'?zoneId=' +  $scope.filterObject.zoneId + '&facilityTypeId=' +  $scope.filterObject.facilityTypeId + '&status=' +  $scope.filterObject.statusId;
            window.open(url);
        }



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


        params['zoneId'] = $scope.zone;
        params['facilityTypeId'] = $scope.facilityType;
        params['statusId'] = $scope.status;
        params['rgroupId'] =$scope.filterObject.rgroupId;

        $scope.data = $scope.datarows = [];

        FacilityList.get(params, function(data) {
                $scope.data = data.pages.rows;
                $scope.paramsChanged( $scope.tableParams );
            });
        };



}
