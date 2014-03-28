/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 3/16/14
 * Time: 9:08 PM
 * To change this template use File | Settings | File Templates.
 */

function RequisitionGroupStockedOutController($scope,$location,$routeParams,messageService,StockedOutFacilitiesByRequisitionGroup) {
    $scope.filterObject = {};

    $scope.formFilter = {};

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
                !isUndefined($routeParams.rgroupId)){
                StockedOutFacilitiesByRequisitionGroup.get({
                    periodId: $routeParams.periodId,
                    programId: $routeParams.programId,
                    productId: $routeParams.productId,
                    rgroupId: $routeParams.rgroupId
                },function(stockData){
                    $scope.totalStockOuts = 0;
                    if(!isUndefined(stockData.stockOut)){
                        $scope.stockedOutDetails = stockData.stockOut;
                        $scope.totalStockOuts = stockData.stockOut.length;
                        var suppliedInPast = _.filter(stockData.stockOut, function(stock){ if(stock.suppliedInPast === true){return stock;}});

                        $scope.product = _.pluck(stockData.stockOut,'product')[0];
                        $scope.location = _.pluck(stockData.stockOut,'location')[0];

                        $scope.datarows = [{label:messageService.get('label.facility.supplied.in.past'),
                            total: suppliedInPast.length
                        },
                            {label:messageService.get('label.facility.not.supplied.in.past'),
                                total: $scope.totalStockOuts - suppliedInPast.length
                            }];


                        $scope.stockedOutPieChartData = [];
                        for (var i = 0; i < $scope.datarows.length; i++) {
                            $scope.stockedOutPieChartData[i] = {
                                label: $scope.datarows[i].label,
                                data: $scope.datarows[i].total
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
            var districtStockOutPath = '/stock-out-detail/'+$scope.filterObject.programId+'/'+$scope.filterObject.periodId+'/'+$scope.filterObject.rgroupId+'/'+$scope.filterObject.productId;
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