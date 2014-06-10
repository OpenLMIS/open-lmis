/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */


function RnRStatusDetailsController($scope,$routeParams,RnRStatusDetailsByRequisitionGroup,ngTableParams,$filter) {

    $scope.$parent.currentTab = 'RNR-STATUS-DETAIL';

    $scope.$on('$viewContentLoaded', function () {

        if(!isUndefined($routeParams.rgroupId) &&
            !isUndefined($routeParams.programId) &&
            !isUndefined($routeParams.periodId)
            ){
            RnRStatusDetailsByRequisitionGroup.get({
                requisitionGroupId: $routeParams.rgroupId,
                programId: $routeParams.programId,
                periodId: $routeParams.periodId,
                status: $routeParams.status
            },function(data){
                $scope.totalRnRStaus = 0;

                if(!isUndefined(data.rnrDetails)){
                    $scope.rnrStatusDetails= data.rnrDetails;

                    setupTableOption();
                }else{
                    $scope.resetRnRStatusData();
                }
            });
        } else{
            $scope.resetRnRStatusData();
        }
    });

    function setupTableOption(){
        // the grid options
        $scope.tableParams = new ngTableParams({
            page: 1,            // show first page
            total: 0,           // length of data
            count: 25           // count per page
        });

        $scope.paramsChanged = function(params) {

            // slice array data on pages
            if($scope.rnrStatusDetails === undefined ){
                $scope.rnrStatusData = [];
                params.total = 0;
            }else{
                var data = $scope.rnrStatusDetails;
                var orderedData = params.filter ? $filter('filter')(data, params.filter) : data;
                orderedData = params.sorting ?  $filter('orderBy')(orderedData, params.orderBy()) : data;

                params.total = orderedData.length;
                $scope.rnrStatusData = orderedData.slice( (params.page - 1) * params.count,  params.page * params.count );

            }
        };

        $scope.$watch('tableParams', $scope.paramsChanged , true);

    }


    $scope.resetRnRStatusData = function(){
        $scope.rnrStatusDetails = null;
    };
}
