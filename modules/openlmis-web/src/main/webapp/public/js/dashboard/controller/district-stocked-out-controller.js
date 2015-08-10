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

function DistrictStockedOutController($scope,$location,$routeParams,dashboardMenuService,messageService,StockedOutFacilitiesByDistrict) {
    $scope.filterObject = {};

    $scope.formFilter = {};

    $scope.formPanel = {openPanel:true};


    $scope.startYears = [];
    $scope.showProductsFilter = true;
    $scope.$parent.currentTab = 'DISTRICT-STOCK-OUT';

    $scope.productSelectOption = {maximumSelectionSize : 1};


    $scope.stockedOutPieChartOption = {
        series: {
            pie: {
                show: true,
                radius: 1,
                label: {
                    show: true,
                    radius: 2 / 3,
                    formatter: function (label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:black;">' + Math.round(series.percent) + '%</div>';
                    },
                    threshold: 0.1
                }
            }
        },
        legend: {
            container:$("#stockedOutReportLegend"),
            noColumns: 0,
            labelBoxBorderColor: "none"
        },
        grid:{
            hoverable: true,
            clickable: true,
            borderWidth: 1,
            borderColor: "#d6d6d6",
            backgroundColor: {
                colors: ["#FFF", "#CCC"]
            }
        },
        tooltip: true,
        tooltipOpts: {
            content: "%p.0%, %s",
            shifts: {
                x: 20,
                y: 0
            },
            defaultTheme: false
        }
    };

    $scope.$on('$viewContentLoaded', function () {
        $scope.filterObject = $routeParams;
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
                        $scope.totalStockOuts = stockData.stockOut.length;
                        var suppliedInPast = _.filter(stockData.stockOut, function(stock){ if(stock.suppliedInPast === true){return stock;}});

                        $scope.product = _.pluck(stockData.stockOut,'product')[0];
                        $scope.location = _.pluck(stockData.stockOut,'location')[0];

                        $scope.datarows = [{label:messageService.get('label.facility.supplied.in.past'),
                            total: suppliedInPast.length,
                            color: "#F47900"
                        },
                            {label:messageService.get('label.facility.not.supplied.in.past'),
                                total: $scope.totalStockOuts - suppliedInPast.length,
                                color:"#CC0505"
                            }];


                        $scope.stockedOutPieChartData = [];
                        for (var i = 0; i < $scope.datarows.length; i++) {
                            $scope.stockedOutPieChartData[i] = {
                                label: $scope.datarows[i].label,
                                data: $scope.datarows[i].total,
                                color: $scope.datarows[i].color
                            };
                        }
                        bindChartEvent("#stocked-out-reporting","plotclick",$scope.stockedOutChartClickHandler);
                        bindChartEvent("#stocked-out-reporting","plothover",flotChartHoverCursorHandler);

                    }else{
                        $scope.resetStockedOutData();
                    }

                });
            } else{
                $scope.resetStockedOutData();

            }
       // };


    });

    $scope.stockedOutChartClickHandler = function (event, pos, item){
        if(item){
            var districtStockOutPath = '/stock-out-detail/'+$scope.filterObject.programId+'/'+$scope.filterObject.periodId+'/'+$scope.filterObject.zoneId+'/'+$scope.filterObject.productId;
            dashboardMenuService.addTab('menu.header.dashboard.stocked.out.district.detail','/public/pages/dashboard/index.html#'+districtStockOutPath,'DISTRICT-STOCK-OUT-DETAIL',true, 5);
            $location.path(districtStockOutPath);

            $scope.$apply();
        }

    };

    $scope.resetStockedOutData = function(){
        $scope.stockedOutPieChartData = null;
        $scope.datarows = null;
        $scope.stockedOutDetails = null;
    };


    function flotChartHoverCursorHandler(event,pos,item){

        if (item && !isUndefined(item.dataIndex)) {
            $(event.target).css('cursor','pointer');
        } else {
            $(event.target).css('cursor','auto');
        }
    }

    function bindChartEvent(elementSelector, eventType, callback){
        $(elementSelector).bind(eventType, callback);
    }


    $scope.getFacilityStockOutPercent = function(value){
        return Math.round((value/$scope.totalStockOuts)*100) +'%';
    };


}
