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

function StockedOutDetailController($scope,$routeParams,StockedOutFacilitiesByDistrict) {

    $scope.$parent.currentTab = 'DISTRICT-STOCK-OUT-DETAIL';

    $scope.$on('$viewContentLoaded', function () {
              if(!isUndefined($routeParams.programId) &&
                !isUndefined($routeParams.periodId) &&
                !isUndefined($routeParams.productId) &&
                !isUndefined($routeParams.zoneId)){
                  StockedOutFacilitiesByDistrict.get({
                    periodId: $routeParams.periodId,
                    programId: $routeParams.programId,
                    productId: $routeParams.productId,
                    zoneId: $routeParams.zoneId
                },function(stockData){
                    $scope.totalStockOuts = 0;
                    if(!isUndefined(stockData.stockOut)){
                        $scope.stockedOutDetails = stockData.stockOut;
                        $scope.product = _.pluck(stockData.stockOut,'product')[0];
                        $scope.location = _.pluck(stockData.stockOut,'location')[0];
                    }else{
                        $scope.resetStockedOutData();
                    }
                });
            } else{
                $scope.resetStockedOutData();
            }
    });
    $scope.resetStockedOutData = function(){
        $scope.stockedOutDetails = null;
    };
}
