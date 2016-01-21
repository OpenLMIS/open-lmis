/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


function RnRStatusDetailsController($scope,$routeParams,RnRStatusDetail,ngTableParams,$filter) {

    $scope.$parent.currentTab = 'RNR-STATUS-DETAIL';

    $scope.$on('$viewContentLoaded', function () {

        if(!isUndefined($routeParams.zoneId) &&
            !isUndefined($routeParams.programId) &&
            !isUndefined($routeParams.periodId)
            ){
            RnRStatusDetail.get({
                zoneId: $routeParams.zoneId,
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
        $scope.rnRStatusPieChartOption = null;
    };
}
