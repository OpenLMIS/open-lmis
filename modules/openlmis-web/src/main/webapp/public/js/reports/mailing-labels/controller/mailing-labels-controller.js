/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

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
    };



    // the grid options
    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        total: 0,           // length of data
        count: 25           // count per page
    });

    $scope.paramsChanged = function(params) {

        // slice array data on pages
        if($scope.data === undefined ){
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
        var params =  {
            "max" : 10000,
            "page" : 1
        };

        //filter form data section
        $scope.filterObject = {
            facilityTypeId : $scope.facilityTypeId,
            rgroupId :  $scope.rgroupId
        };

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
